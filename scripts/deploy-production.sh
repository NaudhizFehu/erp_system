#!/bin/bash

###############################################################################
# 운영 서버 배포 스크립트
# 용도: SSH를 통한 원격 서버 배포 (Ubuntu/Debian)
###############################################################################

set -e  # 에러 발생 시 스크립트 중단

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 설정 변수 (사용자 환경에 맞게 수정하세요)
PROD_HOST="${PROD_HOST:-your-server.com}"
PROD_USER="${PROD_USER:-ubuntu}"
PROD_PORT="${PROD_PORT:-22}"
PROD_DIR="${PROD_DIR:-/home/ubuntu/erp-system}"
SSH_KEY="${SSH_KEY:-$HOME/.ssh/id_rsa}"

# 프로젝트 루트 디렉토리
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FRONTEND_DIR="$PROJECT_ROOT/frontend"
BACKEND_DIR="$PROJECT_ROOT/backend"

# 로그 함수
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 배너 출력
print_banner() {
    echo -e "${BLUE}"
    echo "╔════════════════════════════════════════════════════════╗"
    echo "║       ERP 시스템 운영 서버 배포 스크립트               ║"
    echo "╚════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# SSH 연결 테스트
test_ssh_connection() {
    log_info "SSH 연결 테스트 중..."

    if ssh -i "$SSH_KEY" -p "$PROD_PORT" -o ConnectTimeout=5 "$PROD_USER@$PROD_HOST" "echo 'Connection successful'" >/dev/null 2>&1; then
        log_success "SSH 연결 성공"
        return 0
    else
        log_error "SSH 연결 실패"
        log_info "설정을 확인하세요:"
        log_info "  HOST: $PROD_HOST"
        log_info "  USER: $PROD_USER"
        log_info "  PORT: $PROD_PORT"
        log_info "  SSH_KEY: $SSH_KEY"
        exit 1
    fi
}

# 배포 전 체크리스트
pre_deployment_checklist() {
    log_info "==================== 배포 전 체크리스트 ===================="
    echo ""

    local all_ok=true

    # Git 상태 확인
    cd "$PROJECT_ROOT"
    if [[ -n $(git status -s) ]]; then
        log_warning "커밋되지 않은 변경사항이 있습니다!"
        all_ok=false
    else
        log_success "✓ Git 상태 깨끗함"
    fi

    # 현재 브랜치 확인
    CURRENT_BRANCH=$(git branch --show-current)
    if [[ "$CURRENT_BRANCH" != "main" ]]; then
        log_warning "현재 브랜치가 main이 아닙니다: $CURRENT_BRANCH"
        all_ok=false
    else
        log_success "✓ main 브랜치"
    fi

    # 린트 검사
    cd "$FRONTEND_DIR"
    if npm run lint >/dev/null 2>&1; then
        log_success "✓ 린트 검사 통과"
    else
        log_error "린트 검사 실패"
        all_ok=false
    fi

    # 타입 체크
    if npm run type-check >/dev/null 2>&1; then
        log_success "✓ 타입 체크 통과"
    else
        log_error "타입 체크 실패"
        all_ok=false
    fi

    echo ""

    if [[ "$all_ok" == "false" ]]; then
        log_error "배포 전 체크리스트에서 문제가 발견되었습니다."
        read -p "계속 진행하시겠습니까? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_error "배포가 취소되었습니다."
            exit 1
        fi
    fi
}

# 백업 생성
create_backup() {
    log_info "==================== 백업 생성 ===================="

    local timestamp=$(date +%Y%m%d_%H%M%S)
    local backup_dir="backup_$timestamp"

    log_info "원격 서버에 백업 생성 중..."

    ssh -i "$SSH_KEY" -p "$PROD_PORT" "$PROD_USER@$PROD_HOST" << EOF
        if [ -d "$PROD_DIR" ]; then
            cd $(dirname "$PROD_DIR")
            cp -r $(basename "$PROD_DIR") "$backup_dir"
            echo "백업 생성: $backup_dir"
        else
            echo "기존 배포가 없습니다. 백업을 건너뜁니다."
        fi
EOF

    log_success "백업 완료"
}

# 프론트엔드 빌드
build_frontend() {
    log_info "==================== 프론트엔드 빌드 ===================="

    cd "$FRONTEND_DIR"

    log_info "의존성 설치 중..."
    npm ci

    log_info "프로덕션 빌드 중..."
    npm run build

    log_success "프론트엔드 빌드 완료"
}

# 백엔드 빌드
build_backend() {
    log_info "==================== 백엔드 빌드 ===================="

    cd "$BACKEND_DIR"

    log_info "Maven 빌드 중..."
    ./mvnw clean package -DskipTests

    log_success "백엔드 빌드 완료"
}

# 파일 업로드
upload_files() {
    log_info "==================== 파일 업로드 ===================="

    # 원격 디렉토리 생성
    ssh -i "$SSH_KEY" -p "$PROD_PORT" "$PROD_USER@$PROD_HOST" "mkdir -p $PROD_DIR/{frontend,backend}"

    # 프론트엔드 업로드
    log_info "프론트엔드 파일 업로드 중..."
    rsync -avz --delete \
        -e "ssh -i $SSH_KEY -p $PROD_PORT" \
        "$FRONTEND_DIR/dist/" \
        "$PROD_USER@$PROD_HOST:$PROD_DIR/frontend/"

    # 백엔드 업로드
    log_info "백엔드 파일 업로드 중..."
    rsync -avz \
        -e "ssh -i $SSH_KEY -p $PROD_PORT" \
        "$BACKEND_DIR/target/*.jar" \
        "$PROD_USER@$PROD_HOST:$PROD_DIR/backend/"

    # 설정 파일 업로드
    log_info "설정 파일 업로드 중..."
    rsync -avz \
        -e "ssh -i $SSH_KEY -p $PROD_PORT" \
        "$BACKEND_DIR/src/main/resources/application-prod.yml" \
        "$PROD_USER@$PROD_HOST:$PROD_DIR/backend/application.yml"

    log_success "파일 업로드 완료"
}

# 서비스 재시작
restart_services() {
    log_info "==================== 서비스 재시작 ===================="

    ssh -i "$SSH_KEY" -p "$PROD_PORT" "$PROD_USER@$PROD_HOST" << 'EOF'
        # 기존 백엔드 프로세스 종료
        echo "기존 백엔드 프로세스 종료 중..."
        pkill -f 'java.*erp.*jar' || true
        sleep 3

        # 백엔드 시작
        echo "백엔드 시작 중..."
        cd $PROD_DIR/backend
        nohup java -jar *.jar --spring.config.location=application.yml > backend.log 2>&1 &

        # Nginx 재시작 (프론트엔드)
        echo "Nginx 재시작 중..."
        sudo systemctl reload nginx || sudo systemctl restart nginx

        echo "서비스 재시작 완료"
EOF

    log_success "서비스 재시작 완료"
}

# Health Check
health_check() {
    log_info "==================== Health Check ===================="

    log_info "서비스 시작 대기 중... (30초)"
    sleep 30

    # 백엔드 Health Check
    log_info "백엔드 Health Check..."
    if ssh -i "$SSH_KEY" -p "$PROD_PORT" "$PROD_USER@$PROD_HOST" "curl -f http://localhost:8080/actuator/health" >/dev/null 2>&1; then
        log_success "✓ 백엔드 정상"
    else
        log_error "✗ 백엔드 응답 없음"
        return 1
    fi

    # 프론트엔드 Health Check
    log_info "프론트엔드 Health Check..."
    if curl -f "http://$PROD_HOST" >/dev/null 2>&1; then
        log_success "✓ 프론트엔드 정상"
    else
        log_warning "프론트엔드 응답 없음 (Nginx 설정 확인 필요)"
    fi

    log_success "Health Check 완료"
}

# 롤백
rollback() {
    log_warning "==================== 롤백 시작 ===================="

    ssh -i "$SSH_KEY" -p "$PROD_PORT" "$PROD_USER@$PROD_HOST" << EOF
        # 최신 백업 찾기
        cd $(dirname "$PROD_DIR")
        latest_backup=\$(ls -dt backup_* 2>/dev/null | head -n1)

        if [ -n "\$latest_backup" ]; then
            echo "롤백 중: \$latest_backup"
            rm -rf $(basename "$PROD_DIR")
            cp -r "\$latest_backup" $(basename "$PROD_DIR")

            # 서비스 재시작
            pkill -f 'java.*erp.*jar' || true
            sleep 3
            cd "$PROD_DIR/backend"
            nohup java -jar *.jar --spring.config.location=application.yml > backend.log 2>&1 &

            echo "롤백 완료"
        else
            echo "백업을 찾을 수 없습니다!"
            exit 1
        fi
EOF

    log_success "롤백 완료"
}

# 배포 후 확인
post_deployment_verification() {
    log_info "==================== 배포 후 확인 ===================="
    echo ""

    echo "다음 항목을 확인하세요:"
    echo "  □ 웹사이트 접속: http://$PROD_HOST"
    echo "  □ API 응답: http://$PROD_HOST:8080/actuator/health"
    echo "  □ 로그인 기능"
    echo "  □ 주요 기능 테스트"
    echo "  □ 에러 로그 확인"
    echo ""

    read -p "모든 확인이 완료되었습니까? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        log_warning "문제가 있다면 롤백을 실행하세요:"
        log_warning "  ./scripts/deploy-production.sh --rollback"
        exit 1
    fi
}

# Git 태그 생성
create_git_tag() {
    log_info "==================== Git 태그 생성 ===================="

    cd "$PROJECT_ROOT"

    local version=$(date +%Y.%m.%d-%H%M)
    local tag="deploy-$version"

    git tag -a "$tag" -m "Production deployment $version"

    log_info "태그 생성: $tag"

    read -p "원격 저장소에 태그를 푸시하시겠습니까? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        git push origin "$tag"
        log_success "태그 푸시 완료"
    fi
}

# 메인 함수
main() {
    print_banner

    # 환경 변수 파일 로드
    if [[ -f "$PROJECT_ROOT/.env.production" ]]; then
        log_info "환경 변수 로드 중..."
        source "$PROJECT_ROOT/.env.production"
    fi

    log_info "배포 대상: $PROD_USER@$PROD_HOST:$PROD_DIR"
    echo ""

    # 최종 확인
    echo "⚠️  운영 서버에 배포하시겠습니까?"
    echo "   이 작업은 실제 사용자에게 영향을 미칩니다!"
    echo ""
    read -p "계속 진행하시겠습니까? (yes 입력): " -r
    if [[ "$REPLY" != "yes" ]]; then
        log_info "배포가 취소되었습니다."
        exit 0
    fi

    echo ""

    # 배포 프로세스
    test_ssh_connection
    pre_deployment_checklist
    create_backup
    build_frontend
    build_backend
    upload_files
    restart_services
    health_check

    if [[ $? -eq 0 ]]; then
        log_success "==================== 배포 성공! ===================="
        post_deployment_verification
        create_git_tag

        echo ""
        log_success "배포가 완료되었습니다!"
        log_info "모니터링을 계속 진행해주세요."
    else
        log_error "==================== 배포 실패 ===================="
        log_error "Health Check 실패!"

        read -p "롤백하시겠습니까? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            rollback
        fi

        exit 1
    fi
}

# 롤백 모드
if [[ "$1" == "--rollback" ]]; then
    print_banner
    log_warning "롤백 모드로 실행합니다."
    echo ""
    read -p "정말 롤백하시겠습니까? (yes 입력): " -r
    if [[ "$REPLY" == "yes" ]]; then
        rollback
    else
        log_info "롤백이 취소되었습니다."
    fi
    exit 0
fi

# 스크립트 실행
main "$@"
