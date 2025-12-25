#!/bin/bash

# ============================================
# Synology NAS 컨테이너 로그 확인 스크립트
# ============================================

set -e

# 환경 변수 로드
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

if [ -f "$PROJECT_DIR/.env.bluegreen" ]; then
    source "$PROJECT_DIR/.env.bluegreen"
else
    echo "❌ .env.bluegreen 파일을 찾을 수 없습니다."
    exit 1
fi

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 로그 함수
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

echo ""
log_info "=================================================="
log_info "  Synology NAS 컨테이너 로그 확인"
log_info "=================================================="
echo ""

# SSH로 접속해서 대화형으로 로그 확인
log_info "SSH로 NAS에 접속합니다..."
log_info "접속 후 다음 명령어를 실행하세요:"
echo ""
echo -e "${GREEN}cd $DEPLOY_DIR${NC}"
echo -e "${GREEN}sudo /usr/local/bin/docker-compose -f docker-compose.prod.yml ps${NC}"
echo -e "${GREEN}sudo /usr/local/bin/docker-compose -f docker-compose.prod.yml logs backend-blue${NC}"
echo -e "${GREEN}sudo /usr/local/bin/docker-compose -f docker-compose.prod.yml logs frontend-blue${NC}"
echo ""
log_warning "로그 확인 후 'exit'로 연결을 종료하세요"
echo ""

ssh -i "$SSH_KEY" -p "$SYNOLOGY_PORT" "$SYNOLOGY_USER@$SYNOLOGY_HOST"
