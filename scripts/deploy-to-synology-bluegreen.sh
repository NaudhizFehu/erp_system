#!/bin/bash

###############################################################################
# Synology NAS Blue-Green 배포 스크립트 (로컬 실행용)
# 용도: 로컬에서 이미지 빌드 → Synology 전송 → 무중단 배포 실행
###############################################################################

set -e  # 에러 발생 시 스크립트 중단

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 프로젝트 루트 디렉토리
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FRONTEND_DIR="$PROJECT_ROOT/frontend"
BACKEND_DIR="$PROJECT_ROOT/backend"

# 환경 변수 로드
if [[ -f "$PROJECT_ROOT/.env.bluegreen" ]]; then
    source "$PROJECT_ROOT/.env.bluegreen"
fi

# 설정 변수 (환경 변수 또는 기본값)
SYNOLOGY_HOST="${SYNOLOGY_HOST:-fehu.kr}"
SYNOLOGY_USER="${SYNOLOGY_USER:-naudhizfehu}"
SYNOLOGY_PORT="${SYNOLOGY_PORT:-22}"
SSH_KEY="${SSH_KEY:-$HOME/.ssh/id_rsa}"
DEPLOY_DIR="${DEPLOY_DIR:-/var/services/homes/naudhizfehu/erp-system}"
VERSION="${VERSION:-$(date +%Y.%m.%d-%H%M)}"

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

log_step() {
    echo -e "${CYAN}[STEP]${NC} $1"
}

# 배너 출력
print_banner() {
    echo -e "${CYAN}"
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║       Synology NAS Blue-Green 배포 (로컬 실행)             ║"
    echo "║       빌드 → 전송 → 무중단 배포                            ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# SSH 연결 테스트
test_ssh_connection() {
    log_step "SSH 연결 테스트 중..."

    if ssh -i "$SSH_KEY" -p "$SYNOLOGY_PORT" -o ConnectTimeout=5 "$SYNOLOGY_USER@$SYNOLOGY_HOST" "echo 'Connection successful'" >/dev/null 2>&1; then
        log_success "SSH 연결 성공"
        return 0
    else
        log_error "SSH 연결 실패"
        log_info "설정을 확인하세요:"
        log_info "  HOST: $SYNOLOGY_HOST"
        log_info "  USER: $SYNOLOGY_USER"
        log_info "  PORT: $SYNOLOGY_PORT"
        log_info "  SSH_KEY: $SSH_KEY"
        exit 1
    fi
}

# 배포 전 체크리스트
pre_deployment_checklist() {
    log_step "배포 전 체크리스트 실행 중..."

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
    log_info "현재 브랜치: $CURRENT_BRANCH"

    # 프론트엔드 린트 및 타입 체크
    cd "$FRONTEND_DIR"
    if npm run lint >/dev/null 2>&1; then
        log_success "✓ 프론트엔드 린트 검사 통과"
    else
        log_warning "프론트엔드 린트 검사 실패"
        all_ok=false
    fi

    if npm run type-check >/dev/null 2>&1; then
        log_success "✓ 프론트엔드 타입 체크 통과"
    else
        log_warning "프론트엔드 타입 체크 실패"
        all_ok=false
    fi

    if [[ "$all_ok" == "false" ]]; then
        log_warning "배포 전 체크리스트에서 경고가 발견되었습니다."
        read -p "계속 진행하시겠습니까? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_error "배포가 취소되었습니다."
            exit 1
        fi
    fi
}

# 프론트엔드 Docker 이미지 빌드
build_frontend_image() {
    log_step "프론트엔드 Docker 이미지 빌드 중..."

    cd "$FRONTEND_DIR"

    docker build \
        -f Dockerfile.prod \
        -t cursor-erp-frontend:${VERSION} \
        -t cursor-erp-frontend:latest \
        .

    log_success "프론트엔드 이미지 빌드 완료"
}

# 백엔드 Docker 이미지 빌드
build_backend_image() {
    log_step "백엔드 Docker 이미지 빌드 중..."

    cd "$BACKEND_DIR"

    docker build \
        -f Dockerfile \
        -t cursor-erp-backend:${VERSION} \
        -t cursor-erp-backend:latest \
        .

    log_success "백엔드 이미지 빌드 완료"
}

# Docker 이미지 저장
save_docker_images() {
    log_step "Docker 이미지 저장 중..."

    cd "$PROJECT_ROOT"

    # 임시 디렉토리 생성
    mkdir -p /tmp/erp-images

    # 이미지 저장
    docker save cursor-erp-backend:${VERSION} | gzip > /tmp/erp-images/backend.tar.gz
    docker save cursor-erp-frontend:${VERSION} | gzip > /tmp/erp-images/frontend.tar.gz

    log_success "Docker 이미지 저장 완료"
}

# Synology로 이미지 전송
transfer_images_to_synology() {
    log_step "Synology로 이미지 전송 중..."

    # 원격 디렉토리 생성
    ssh -i "$SSH_KEY" -p "$SYNOLOGY_PORT" "$SYNOLOGY_USER@$SYNOLOGY_HOST" \
        "mkdir -p $DEPLOY_DIR/images"

    # 이미지 파일 전송
    rsync -avz --progress \
        -e "ssh -i $SSH_KEY -p $SYNOLOGY_PORT" \
        /tmp/erp-images/ \
        "$SYNOLOGY_USER@$SYNOLOGY_HOST:$DEPLOY_DIR/images/"

    log_success "이미지 전송 완료"
}

# Synology에서 이미지 로드
load_images_on_synology() {
    log_step "Synology에서 Docker 이미지 로드 중..."

    ssh -i "$SSH_KEY" -p "$SYNOLOGY_PORT" "$SYNOLOGY_USER@$SYNOLOGY_HOST" << EOF
        cd $DEPLOY_DIR/images

        # 백엔드 이미지 로드
        echo "백엔드 이미지 로드 중..."
        sudo docker load < backend.tar.gz

        # 프론트엔드 이미지 로드
        echo "프론트엔드 이미지 로드 중..."
        sudo docker load < frontend.tar.gz

        echo "이미지 로드 완료"
EOF

    log_success "Docker 이미지 로드 완료"
}

# 배포 스크립트 전송
transfer_deployment_scripts() {
    log_step "배포 스크립트 전송 중..."

    # 원격 scripts 디렉토리 생성
    ssh -i "$SSH_KEY" -p "$SYNOLOGY_PORT" "$SYNOLOGY_USER@$SYNOLOGY_HOST" \
        "mkdir -p $DEPLOY_DIR/scripts"

    # 스크립트 전송
    rsync -avz \
        -e "ssh -i $SSH_KEY -p $SYNOLOGY_PORT" \
        "$PROJECT_ROOT/scripts/blue-green-deploy.sh" \
        "$PROJECT_ROOT/scripts/blue-green-rollback.sh" \
        "$SYNOLOGY_USER@$SYNOLOGY_HOST:$DEPLOY_DIR/scripts/"

    # 실행 권한 부여
    ssh -i "$SSH_KEY" -p "$SYNOLOGY_PORT" "$SYNOLOGY_USER@$SYNOLOGY_HOST" \
        "chmod +x $DEPLOY_DIR/scripts/*.sh"

    log_success "배포 스크립트 전송 완료"
}

# docker-compose 파일 전송
transfer_docker_compose() {
    log_step "docker-compose 파일 전송 중..."

    rsync -avz \
        -e "ssh -i $SSH_KEY -p $SYNOLOGY_PORT" \
        "$PROJECT_ROOT/docker-compose.prod.yml" \
        "$SYNOLOGY_USER@$SYNOLOGY_HOST:$DEPLOY_DIR/"

    log_success "docker-compose 파일 전송 완료"
}

# Synology에서 Blue-Green 배포 실행
execute_blue_green_deployment() {
    log_step "Synology에서 Blue-Green 배포 실행 중..."
    echo ""

    log_info "==================================================="
    log_info "이제 Synology NAS에서 배포 스크립트를 실행합니다."
    log_info "승인 단계에서 'yes'를 입력해야 합니다."
    log_info "==================================================="
    echo ""

    ssh -i "$SSH_KEY" -p "$SYNOLOGY_PORT" -t "$SYNOLOGY_USER@$SYNOLOGY_HOST" << EOF
        cd $DEPLOY_DIR
        export PROJECT_DIR=$DEPLOY_DIR
        export VERSION=$VERSION
        ./scripts/blue-green-deploy.sh
EOF
}

# 정리
cleanup() {
    log_step "임시 파일 정리 중..."

    rm -rf /tmp/erp-images

    log_success "정리 완료"
}

# 메인 함수
main() {
    print_banner

    log_info "배포 대상: $SYNOLOGY_USER@$SYNOLOGY_HOST:$DEPLOY_DIR"
    log_info "버전: $VERSION"
    echo ""

    # 최종 확인
    log_warning "⚠️  Synology NAS에 Blue-Green 무중단 배포를 시작하시겠습니까?"
    echo ""
    read -p "계속 진행하시겠습니까? (yes 입력): " -r
    if [[ "$REPLY" != "yes" ]]; then
        log_info "배포가 취소되었습니다."
        exit 0
    fi
    echo ""

    # 배포 프로세스
    test_ssh_connection
    echo ""

    pre_deployment_checklist
    echo ""

    build_frontend_image
    echo ""

    build_backend_image
    echo ""

    save_docker_images
    echo ""

    transfer_images_to_synology
    echo ""

    load_images_on_synology
    echo ""

    transfer_deployment_scripts
    echo ""

    transfer_docker_compose
    echo ""

    execute_blue_green_deployment

    # 정리
    cleanup
    echo ""

    log_success "=========================================="
    log_success "로컬 배포 프로세스 완료!"
    log_success "=========================================="
    echo ""
    log_info "다음 단계:"
    log_info "  1. 서비스 모니터링"
    log_info "  2. 문제 발생 시 Synology에서 롤백:"
    log_info "     ssh $SYNOLOGY_USER@$SYNOLOGY_HOST"
    log_info "     cd $DEPLOY_DIR"
    log_info "     ./scripts/blue-green-rollback.sh"
    echo ""
}

# 스크립트 실행
main "$@"
