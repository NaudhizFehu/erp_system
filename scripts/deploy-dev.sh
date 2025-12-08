#!/bin/bash

###############################################################################
# 개발 서버 배포 스크립트
# 용도: 각 작업 완료 후 개발 서버에 배포하여 테스트
###############################################################################

set -e  # 에러 발생 시 스크립트 중단

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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
    echo "║         ERP 시스템 개발 서버 배포 스크립트            ║"
    echo "╚════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# Git 상태 확인
check_git_status() {
    log_info "Git 상태 확인 중..."

    cd "$PROJECT_ROOT"

    # 현재 브랜치 확인
    CURRENT_BRANCH=$(git branch --show-current)
    log_info "현재 브랜치: $CURRENT_BRANCH"

    # 변경사항 확인
    if [[ -n $(git status -s) ]]; then
        log_warning "커밋되지 않은 변경사항이 있습니다."
        git status -s
        echo ""
        read -p "계속 진행하시겠습니까? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_error "배포가 취소되었습니다."
            exit 1
        fi
    else
        log_success "Git 상태가 깨끗합니다."
    fi
}

# 프론트엔드 빌드 및 배포
deploy_frontend() {
    log_info "==================== 프론트엔드 배포 ===================="

    cd "$FRONTEND_DIR"

    # 의존성 설치
    log_info "의존성 설치 중..."
    npm install

    # 린트 검사
    log_info "린트 검사 중..."
    npm run lint || {
        log_warning "린트 경고가 있지만 계속 진행합니다."
    }

    # 타입 체크
    log_info "타입 체크 중..."
    npm run type-check || {
        log_error "타입 에러가 있습니다. 수정 후 다시 시도하세요."
        exit 1
    }

    # 빌드
    log_info "프론트엔드 빌드 중..."
    npm run build

    log_success "프론트엔드 빌드 완료!"

    # 개발 서버 시작 (선택적)
    read -p "개발 서버를 시작하시겠습니까? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        log_info "개발 서버 시작 중... (Ctrl+C로 종료)"
        npm run dev
    fi
}

# 백엔드 빌드 및 배포
deploy_backend() {
    log_info "==================== 백엔드 배포 ===================="

    cd "$BACKEND_DIR"

    log_info "백엔드는 IDE에서 Maven을 사용하여 빌드해주세요."
    log_info "IntelliJ IDEA: Maven 탭 → Lifecycle → clean → install"
    log_info "또는 터미널에서: ./mvnw clean install -DskipTests"

    read -p "백엔드 빌드가 완료되었습니까? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        log_warning "백엔드 빌드를 먼저 완료해주세요."
        return
    fi

    log_success "백엔드 준비 완료!"

    # 서버 시작 (선택적)
    read -p "백엔드 서버를 시작하시겠습니까? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        log_info "IDE에서 SpringBoot Application을 실행해주세요."
    fi
}

# 배포 상태 확인
check_deployment() {
    log_info "==================== 배포 상태 확인 ===================="

    # 프론트엔드 확인
    log_info "프론트엔드 확인: http://localhost:5173"

    # 백엔드 확인
    log_info "백엔드 확인: http://localhost:8080"
    log_info "Swagger UI: http://localhost:8080/swagger-ui.html"

    echo ""
    log_info "배포 후 테스트 체크리스트:"
    echo "  □ 프론트엔드 정상 로딩"
    echo "  □ 백엔드 API 응답 확인"
    echo "  □ 새로 추가된 기능 테스트"
    echo "  □ 기존 기능 회귀 테스트"
}

# Git 커밋 도우미 (선택적)
git_commit_helper() {
    log_info "==================== Git 커밋 도우미 ===================="

    cd "$PROJECT_ROOT"

    # 변경사항 확인
    if [[ -z $(git status -s) ]]; then
        log_info "커밋할 변경사항이 없습니다."
        return
    fi

    echo ""
    log_info "현재 변경사항:"
    git status -s
    echo ""

    read -p "변경사항을 커밋하시겠습니까? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        log_info "커밋을 건너뜁니다."
        return
    fi

    # 커밋 메시지 입력
    echo ""
    echo "커밋 메시지를 입력하세요 (예: feat: 계정과목 목록 조회 기능 추가):"
    read -r COMMIT_MSG

    if [[ -z "$COMMIT_MSG" ]]; then
        log_error "커밋 메시지가 비어있습니다."
        return
    fi

    # 파일 추가
    log_info "변경된 파일을 스테이징합니다..."
    git add .

    # 커밋
    log_info "커밋 생성 중..."
    git commit -m "$COMMIT_MSG"

    log_success "커밋 완료!"

    # 푸시 여부 확인
    echo ""
    read -p "원격 저장소에 푸시하시겠습니까? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        log_info "푸시 중..."
        git push
        log_success "푸시 완료!"
    fi
}

# 메인 함수
main() {
    print_banner

    log_info "배포를 시작합니다..."
    echo ""

    # 배포 옵션 선택
    echo "배포할 항목을 선택하세요:"
    echo "  1) 프론트엔드만"
    echo "  2) 백엔드만"
    echo "  3) 프론트엔드 + 백엔드"
    echo "  4) 종료"
    echo ""
    read -p "선택 (1-4): " -n 1 -r DEPLOY_OPTION
    echo ""
    echo ""

    case $DEPLOY_OPTION in
        1)
            check_git_status
            deploy_frontend
            ;;
        2)
            check_git_status
            deploy_backend
            ;;
        3)
            check_git_status
            deploy_frontend
            echo ""
            deploy_backend
            ;;
        4)
            log_info "배포를 종료합니다."
            exit 0
            ;;
        *)
            log_error "잘못된 선택입니다."
            exit 1
            ;;
    esac

    echo ""
    check_deployment

    echo ""
    git_commit_helper

    echo ""
    log_success "배포가 완료되었습니다!"
}

# 스크립트 실행
main "$@"
