#!/bin/bash

###############################################################################
# Blue-Green 즉시 롤백 스크립트
# 용도: 배포 후 문제 발생 시 이전 환경으로 즉시 복구
# 실행 위치: /var/services/homes/naudhizfehu/erp-system
# 복구 시간: 5초 이내
###############################################################################

set -e  # 에러 발생 시 스크립트 중단

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 설정 변수
PROJECT_DIR="${PROJECT_DIR:-/var/services/homes/naudhizfehu/erp-system}"
DOCKER_COMPOSE_FILE="docker-compose.prod.yml"
NGINX_CONF="/etc/nginx/sites-enabled/erp.conf"

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
    echo -e "${RED}"
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║       Blue-Green 즉시 롤백 스크립트                        ║"
    echo "║       이전 버전으로 5초 이내 복구                          ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# nginx 설정에서 현재 활성 환경 감지
detect_active_environment() {
    log_step "현재 활성 환경 감지 중..."

    if [ ! -f "$NGINX_CONF" ]; then
        log_error "nginx 설정 파일을 찾을 수 없습니다: $NGINX_CONF"
        exit 1
    fi

    # active-backend upstream의 포트 확인
    local backend_port=$(grep -A 1 "upstream active-backend" "$NGINX_CONF" | grep "server" | grep -oP ':\K[0-9]+' | head -1)

    if [ "$backend_port" == "8991" ]; then
        echo "blue"
    elif [ "$backend_port" == "8993" ]; then
        echo "green"
    else
        log_error "활성 환경을 감지할 수 없습니다."
        log_info "nginx 설정을 확인하세요: $NGINX_CONF"
        exit 1
    fi
}

# 롤백 대상 환경 결정
determine_rollback_target() {
    local active_env=$1

    if [ "$active_env" == "blue" ]; then
        echo "green"
    else
        echo "blue"
    fi
}

# 환경 포트 가져오기
get_environment_ports() {
    local env=$1

    if [ "$env" == "blue" ]; then
        echo "8991 8990"  # backend frontend
    else
        echo "8993 8992"  # backend frontend
    fi
}

# 롤백 대상 환경 실행 중인지 확인
check_rollback_target_status() {
    local target_env=$1

    log_step "[$target_env] 환경 상태 확인 중..."

    if [ "$target_env" == "blue" ]; then
        if ! sudo docker ps | grep -q "erp-backend-blue"; then
            log_error "[$target_env] 환경이 실행 중이지 않습니다!"
            log_info "롤백할 수 없습니다. [$target_env] 환경을 먼저 시작하세요:"
            log_info "  sudo docker-compose -f $DOCKER_COMPOSE_FILE up -d backend-blue frontend-blue"
            exit 1
        fi
    else
        if ! sudo docker ps | grep -q "erp-backend-green"; then
            log_error "[$target_env] 환경이 실행 중이지 않습니다!"
            log_info "롤백할 수 없습니다. [$target_env] 환경을 먼저 시작하세요:"
            log_info "  sudo docker-compose -f $DOCKER_COMPOSE_FILE --profile green up -d backend-green frontend-green"
            exit 1
        fi
    fi

    log_success "[$target_env] 환경이 실행 중입니다"
}

# 롤백 대상 환경 Health Check
health_check_rollback_target() {
    local target_env=$1
    local ports=($(get_environment_ports "$target_env"))
    local backend_port=${ports[0]}
    local health_url="http://localhost:${backend_port}/actuator/health"

    log_step "[$target_env] 환경 Health Check 중..."

    if curl -sf "$health_url" > /dev/null 2>&1; then
        log_success "[$target_env] 환경 정상"
        return 0
    else
        log_error "[$target_env] 환경 Health Check 실패"
        log_warning "롤백 대상 환경이 정상 작동하지 않습니다!"
        return 1
    fi
}

# nginx upstream 롤백
rollback_nginx_upstream() {
    local target_env=$1
    local ports=($(get_environment_ports "$target_env"))
    local backend_port=${ports[0]}
    local frontend_port=${ports[1]}

    log_step "nginx upstream을 [$target_env] 환경으로 롤백 중..."

    if [ ! -f "$NGINX_CONF" ]; then
        log_error "nginx 설정 파일을 찾을 수 없습니다: $NGINX_CONF"
        exit 1
    fi

    # 백업 생성
    sudo cp "$NGINX_CONF" "${NGINX_CONF}.rollback.$(date +%Y%m%d_%H%M%S)"

    # active-backend 포트 변경
    sudo sed -i "/upstream active-backend/,/}/s/server localhost:[0-9]\+/server localhost:${backend_port}/" "$NGINX_CONF"

    # active-frontend 포트 변경
    sudo sed -i "/upstream active-frontend/,/}/s/server localhost:[0-9]\+/server localhost:${frontend_port}/" "$NGINX_CONF"

    # nginx 설정 테스트
    if ! sudo nginx -t > /dev/null 2>&1; then
        log_error "nginx 설정 검증 실패"
        # 백업 복원
        sudo cp "${NGINX_CONF}.rollback.$(date +%Y%m%d_%H%M%S | tail -1)" "$NGINX_CONF"
        exit 1
    fi

    # nginx reload
    sudo nginx -s reload

    log_success "nginx upstream 롤백 완료 → [$target_env] 환경"
}

# 롤백 후 검증
verify_rollback() {
    log_step "롤백 검증 중..."

    local health_url="http://localhost/health"

    sleep 2

    if curl -sf "$health_url" > /dev/null 2>&1; then
        log_success "롤백 검증 성공"
        return 0
    else
        log_error "롤백 검증 실패"
        log_warning "수동으로 nginx 설정을 확인하세요: $NGINX_CONF"
        return 1
    fi
}

# 메인 함수
main() {
    print_banner

    # 프로젝트 디렉토리로 이동
    cd "$PROJECT_DIR" || {
        log_error "프로젝트 디렉토리를 찾을 수 없습니다: $PROJECT_DIR"
        exit 1
    }

    log_info "프로젝트 디렉토리: $PROJECT_DIR"
    echo ""

    # 1. 현재 활성 환경 감지
    ACTIVE_ENV=$(detect_active_environment)
    log_info "현재 활성 환경: [$ACTIVE_ENV]"

    # 2. 롤백 대상 환경 결정
    TARGET_ENV=$(determine_rollback_target "$ACTIVE_ENV")
    log_info "롤백 대상 환경: [$TARGET_ENV]"
    echo ""

    # 3. 롤백 대상 환경 실행 확인
    check_rollback_target_status "$TARGET_ENV"
    echo ""

    # 4. 롤백 대상 환경 Health Check
    if ! health_check_rollback_target "$TARGET_ENV"; then
        log_warning "롤백 대상 환경에 문제가 있습니다."
        read -p "그래도 롤백하시겠습니까? (yes 입력): " -r
        if [[ "$REPLY" != "yes" ]]; then
            log_info "롤백이 취소되었습니다."
            exit 0
        fi
    fi
    echo ""

    # 5. 롤백 최종 확인
    log_warning "⚠️  [$TARGET_ENV] 환경으로 즉시 롤백하시겠습니까?"
    log_info "현재 [$ACTIVE_ENV] 환경에서 [$TARGET_ENV] 환경으로 트래픽이 전환됩니다."
    echo ""
    read -p "정말 롤백하시겠습니까? (yes 입력): " -r
    if [[ "$REPLY" != "yes" ]]; then
        log_info "롤백이 취소되었습니다."
        exit 0
    fi
    echo ""

    # 롤백 시작 시간 기록
    START_TIME=$(date +%s)

    # 6. nginx upstream 롤백
    rollback_nginx_upstream "$TARGET_ENV"

    # 7. 롤백 검증
    if ! verify_rollback; then
        log_error "롤백 검증 실패"
        log_warning "nginx 로그를 확인하세요:"
        log_warning "  sudo tail -f /var/log/nginx/error.log"
        exit 1
    fi

    # 롤백 완료 시간 계산
    END_TIME=$(date +%s)
    ELAPSED=$((END_TIME - START_TIME))

    echo ""
    log_success "=========================================="
    log_success "즉시 롤백 완료!"
    log_success "=========================================="
    echo ""
    log_info "롤백 시간: ${ELAPSED}초"
    log_info "현재 활성 환경: [$TARGET_ENV]"
    log_info "이전 환경: [$ACTIVE_ENV]"
    echo ""
    log_info "다음 단계:"
    log_info "  1. 서비스 정상 작동 확인"
    log_info "  2. [$ACTIVE_ENV] 환경 로그 확인:"
    log_info "     sudo docker-compose -f $DOCKER_COMPOSE_FILE logs backend-$ACTIVE_ENV"
    log_info "  3. 문제 원인 파악 및 수정"
    echo ""
}

# 스크립트 실행
main "$@"
