#!/bin/bash

###############################################################################
# Blue-Green 무중단 배포 스크립트
# 용도: Synology NAS에서 실행하는 무중단 배포 자동화
# 실행 위치: /var/services/homes/naudhizfehu/erp-system
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
NGINX_CONF="/usr/local/etc/nginx/conf.d/erp.conf"
HEALTH_CHECK_TIMEOUT=120
HEALTH_CHECK_INTERVAL=5

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
    echo "║       Blue-Green 무중단 배포 스크립트                      ║"
    echo "║       서비스 중단 0초, 즉시 롤백 가능                      ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# 컨테이너 상태로 현재 활성 환경 감지
detect_active_environment() {
    log_step "현재 활성 환경 감지 중..."

    local blue_running=$(/usr/local/bin/docker ps --filter "name=erp-backend-blue" --filter "status=running" --format "{{.Names}}" 2>/dev/null)
    local green_running=$(/usr/local/bin/docker ps --filter "name=erp-backend-green" --filter "status=running" --format "{{.Names}}" 2>/dev/null)

    if [ -n "$blue_running" ] && [ -z "$green_running" ]; then
        echo "blue"
    elif [ -n "$green_running" ] && [ -z "$blue_running" ]; then
        echo "green"
    elif [ -n "$blue_running" ] && [ -n "$green_running" ]; then
        # 둘 다 실행 중이면 blue가 활성으로 간주
        echo "blue"
    else
        log_warning "실행 중인 환경이 없습니다. 기본값 'blue'로 설정합니다."
        echo "blue"
    fi
}

# 배포 대상 환경 결정
determine_target_environment() {
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

# Docker 이미지 존재 확인
check_docker_images() {
    log_step "Docker 이미지 존재 확인 중..."

    local backend_image="cursor-erp-backend:${VERSION:-latest}"
    local frontend_image="cursor-erp-frontend:${VERSION:-latest}"

    if ! sudo /usr/local/bin/docker images | grep -q "cursor-erp-backend"; then
        log_error "백엔드 이미지를 찾을 수 없습니다: $backend_image"
        log_info "이미지를 먼저 빌드하거나 전송하세요."
        exit 1
    fi

    if ! sudo /usr/local/bin/docker images | grep -q "cursor-erp-frontend"; then
        log_error "프론트엔드 이미지를 찾을 수 없습니다: $frontend_image"
        log_info "이미지를 먼저 빌드하거나 전송하세요."
        exit 1
    fi

    log_success "Docker 이미지 확인 완료"
}

# 타겟 환경에 컨테이너 배포
deploy_to_target_environment() {
    local target_env=$1

    log_step "[$target_env] 환경에 새 버전 배포 중..."

    cd "$PROJECT_DIR"

    if [ "$target_env" == "green" ]; then
        # Green 환경 활성화
        sudo /usr/local/bin/docker-compose -f "$DOCKER_COMPOSE_FILE" --profile green up -d backend-green frontend-green
    else
        # Blue 환경 재시작
        sudo /usr/local/bin/docker-compose -f "$DOCKER_COMPOSE_FILE" up -d backend-blue frontend-blue
    fi

    log_success "[$target_env] 환경 배포 완료"
}

# Health Check
health_check() {
    local target_env=$1
    local ports=($(get_environment_ports "$target_env"))
    local backend_port=${ports[0]}

    log_step "[$target_env] 환경 Health Check 중..."
    log_info "대기 시간: 최대 ${HEALTH_CHECK_TIMEOUT}초"

    local elapsed=0
    local health_url="http://localhost:${backend_port}/actuator/health"

    while [ $elapsed -lt $HEALTH_CHECK_TIMEOUT ]; do
        if curl -sf "$health_url" > /dev/null 2>&1; then
            log_success "[$target_env] 환경 정상 (${elapsed}초 경과)"
            return 0
        fi

        echo -n "."
        sleep $HEALTH_CHECK_INTERVAL
        elapsed=$((elapsed + HEALTH_CHECK_INTERVAL))
    done

    echo ""
    log_error "[$target_env] 환경 Health Check 실패 (${HEALTH_CHECK_TIMEOUT}초 타임아웃)"
    return 1
}

# nginx upstream 전환
switch_nginx_upstream() {
    local target_env=$1
    local ports=($(get_environment_ports "$target_env"))
    local backend_port=${ports[0]}
    local frontend_port=${ports[1]}

    log_step "nginx upstream을 [$target_env] 환경으로 전환 중..."

    if [ ! -f "$NGINX_CONF" ]; then
        log_error "nginx 설정 파일을 찾을 수 없습니다: $NGINX_CONF"
        return 1
    fi

    # 백업 생성
    sudo cp "$NGINX_CONF" "${NGINX_CONF}.backup.$(date +%Y%m%d_%H%M%S)"

    # active-backend 포트 변경
    sudo sed -i "/upstream active-backend/,/}/s/server localhost:[0-9]\+/server localhost:${backend_port}/" "$NGINX_CONF"

    # active-frontend 포트 변경
    sudo sed -i "/upstream active-frontend/,/}/s/server localhost:[0-9]\+/server localhost:${frontend_port}/" "$NGINX_CONF"

    # nginx 설정 테스트
    if ! sudo nginx -t > /dev/null 2>&1; then
        log_error "nginx 설정 검증 실패"
        # 백업 복원
        sudo cp "${NGINX_CONF}.backup.$(date +%Y%m%d_%H%M%S | tail -1)" "$NGINX_CONF"
        return 1
    fi

    # nginx reload
    sudo nginx -s reload

    log_success "nginx upstream 전환 완료 → [$target_env] 환경"
}

# 전환 후 검증
verify_switch() {
    log_step "트래픽 전환 검증 중..."

    # nginx 재시작 후 안정화 대기
    log_info "nginx 재시작 안정화 대기 (10초)..."
    sleep 10

    local health_url="http://localhost/"

    if curl -sf "$health_url" > /dev/null 2>&1; then
        log_success "트래픽 전환 검증 성공"
        return 0
    else
        log_error "트래픽 전환 검증 실패"
        return 1
    fi
}

# 이전 환경 상태 확인
check_previous_environment_status() {
    local previous_env=$1
    local ports=($(get_environment_ports "$previous_env"))
    local backend_port=${ports[0]}

    log_info "[$previous_env] 환경 상태 확인 중..."

    if [ "$previous_env" == "blue" ]; then
        if /usr/local/bin/docker ps | grep -q "erp-backend-blue"; then
            log_success "[$previous_env] 환경이 실행 중입니다 (롤백 가능)"
            return 0
        else
            log_warning "[$previous_env] 환경이 중지되어 있습니다"
            return 1
        fi
    else
        if /usr/local/bin/docker ps | grep -q "erp-backend-green"; then
            log_success "[$previous_env] 환경이 실행 중입니다 (롤백 가능)"
            return 0
        else
            log_warning "[$previous_env] 환경이 중지되어 있습니다"
            return 1
        fi
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

    # 2. 배포 대상 환경 결정
    TARGET_ENV=$(determine_target_environment "$ACTIVE_ENV")
    log_info "배포 대상 환경: [$TARGET_ENV]"
    echo ""

    # 3. nginx 설정 확인 및 초기화
    if [ -f "/tmp/nginx-bluegreen.conf" ]; then
        log_step "nginx Blue-Green 설정 적용 중..."
        sudo cp /tmp/nginx-bluegreen.conf "$NGINX_CONF"
        if sudo nginx -t > /dev/null 2>&1; then
            sudo nginx -s reload
            log_success "nginx 설정 적용 완료"
        else
            log_warning "nginx 설정 검증 실패 - 기존 설정 유지"
        fi
        echo ""
    fi

    # 4. Docker 이미지 확인
    check_docker_images
    echo ""

    # 5. 배포 확인
    log_warning "⚠️  [$TARGET_ENV] 환경에 새 버전을 배포하시겠습니까?"
    log_info "현재 [$ACTIVE_ENV] 환경은 계속 서비스 중입니다."
    echo ""
    read -p "계속 진행하시겠습니까? (yes 입력): " -r
    if [[ "$REPLY" != "yes" ]]; then
        log_info "배포가 취소되었습니다."
        exit 0
    fi
    echo ""

    # 5. 타겟 환경에 배포
    deploy_to_target_environment "$TARGET_ENV"
    echo ""

    # 6. Health Check
    if ! health_check "$TARGET_ENV"; then
        log_error "배포 실패: Health Check 실패"
        echo ""
        log_info "=== 컨테이너 로그 확인 중 ==="
        echo ""

        # 백엔드 로그 출력 (최근 50줄)
        log_info "백엔드 컨테이너 로그 (최근 50줄):"
        sudo /usr/local/bin/docker-compose -f $DOCKER_COMPOSE_FILE logs --tail=50 backend-$TARGET_ENV || true

        echo ""
        log_info "프론트엔드 컨테이너 로그 (최근 50줄):"
        sudo /usr/local/bin/docker-compose -f $DOCKER_COMPOSE_FILE logs --tail=50 frontend-$TARGET_ENV || true

        echo ""
        log_error "위 로그를 확인하여 문제를 해결한 후 다시 배포하세요."
        exit 1
    fi
    echo ""

    # 7. 트래픽 전환 확인
    log_warning "⚠️  트래픽을 [$TARGET_ENV] 환경으로 전환하시겠습니까?"
    log_info "이 작업은 즉시 사용자 트래픽을 새 버전으로 전환합니다."
    echo ""
    read -p "트래픽을 전환하시겠습니까? (yes 입력): " -r
    if [[ "$REPLY" != "yes" ]]; then
        log_warning "트래픽 전환이 취소되었습니다."
        log_info "[$TARGET_ENV] 환경은 계속 실행 중입니다."
        log_info "수동으로 전환하거나 롤백할 수 있습니다."
        exit 0
    fi
    echo ""

    # 8. nginx upstream 전환
    if ! switch_nginx_upstream "$TARGET_ENV"; then
        log_error "nginx 전환 실패"
        exit 1
    fi

    # 짧은 대기
    sleep 2

    # 9. 전환 검증
    if ! verify_switch; then
        log_error "전환 검증 실패"
        log_warning "즉시 롤백을 실행하세요: ./scripts/blue-green-rollback.sh"
        exit 1
    fi
    echo ""

    # 10. 이전 환경 상태 확인 (스킵)
    # check_previous_environment_status "$ACTIVE_ENV"
    log_info "이전 환경 [$ACTIVE_ENV]은 롤백 대비 유지됩니다"
    echo ""

    # 완료
    log_success "=========================================="
    log_success "무중단 배포 완료!"
    log_success "=========================================="
    echo ""
    log_info "현재 활성 환경: [$TARGET_ENV]"
    log_info "이전 환경: [$ACTIVE_ENV] (롤백 대비 유지)"
    echo ""
    log_info "다음 단계:"
    log_info "  1. 서비스 모니터링 (로그, 성능, 에러)"
    log_info "  2. 문제 발생 시 즉시 롤백: ./scripts/blue-green-rollback.sh"
    log_info "  3. 안정화 확인 후 이전 환경 중지 (선택):"
    log_info "     sudo /usr/local/bin/docker-compose -f $DOCKER_COMPOSE_FILE stop backend-$ACTIVE_ENV frontend-$ACTIVE_ENV"
    echo ""
}

# 스크립트 실행
main "$@"
