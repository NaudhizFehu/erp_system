#!/bin/bash

# 서버 접속 테스트 스크립트
# .env.production 설정을 기반으로 운영 서버 접속 가능 여부를 체크합니다.

set -e

# 색상 정의
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}================================${NC}"
echo -e "${YELLOW}ERP 시스템 서버 접속 테스트${NC}"
echo -e "${YELLOW}================================${NC}"
echo ""

# .env.production 파일 확인 (프로젝트 루트)
if [ ! -f "../.env.production" ]; then
    echo -e "${RED}❌ .env.production 파일이 없습니다.${NC}"
    echo -e "${YELLOW}   경로: 프로젝트루트/.env.production${NC}"
    echo -e "${YELLOW}   .env.production.example을 복사하여 생성해주세요.${NC}"
    exit 1
fi

# .env.production 로드
source ../.env.production

# 필수 변수 확인
if [ -z "$PROD_HOST" ] || [ -z "$PROD_USER" ] || [ -z "$PROD_PORT" ]; then
    echo -e "${RED}❌ .env.production 파일에 필수 변수가 설정되지 않았습니다.${NC}"
    echo -e "${YELLOW}   필수 변수: PROD_HOST, PROD_USER, PROD_PORT${NC}"
    exit 1
fi

echo -e "${YELLOW}📋 서버 정보${NC}"
echo -e "   호스트: ${PROD_HOST}"
echo -e "   사용자: ${PROD_USER}"
echo -e "   포트: ${PROD_PORT}"
echo ""

# 1. SSH 접속 테스트
echo -e "${YELLOW}🔍 1. SSH 접속 테스트${NC}"
if ssh -p ${PROD_PORT} -o ConnectTimeout=10 -o BatchMode=yes ${PROD_USER}@${PROD_HOST} exit 2>/dev/null; then
    echo -e "${GREEN}✅ SSH 접속 성공${NC}"
else
    echo -e "${RED}❌ SSH 접속 실패${NC}"
    echo -e "${YELLOW}   다음을 확인해주세요:${NC}"
    echo -e "   1. 서버 IP/도메인이 올바른지 확인 (${PROD_HOST})"
    echo -e "   2. SSH 포트가 올바른지 확인 (${PROD_PORT})"
    echo -e "   3. SSH 키가 등록되어 있는지 확인"
    echo -e "   4. 방화벽 설정 확인"
    echo ""
    echo -e "${YELLOW}📝 수동 접속 명령어:${NC}"
    echo -e "   ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST}"
    exit 1
fi
echo ""

# 2. 배포 디렉토리 확인
echo -e "${YELLOW}🔍 2. 배포 디렉토리 확인${NC}"
if ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST} "[ -d ${PROD_DIR} ]" 2>/dev/null; then
    echo -e "${GREEN}✅ 배포 디렉토리 존재: ${PROD_DIR}${NC}"
else
    echo -e "${YELLOW}⚠️  배포 디렉토리가 없습니다: ${PROD_DIR}${NC}"
    echo -e "${YELLOW}   생성하시겠습니까? (y/n)${NC}"
    read -r CREATE_DIR
    if [ "$CREATE_DIR" = "y" ]; then
        ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST} "mkdir -p ${PROD_DIR}"
        echo -e "${GREEN}✅ 배포 디렉토리 생성 완료${NC}"
    else
        echo -e "${YELLOW}   배포 디렉토리를 수동으로 생성해주세요.${NC}"
    fi
fi
echo ""

# 3. Docker 설치 확인
echo -e "${YELLOW}🔍 3. Docker 설치 확인${NC}"
if ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST} "command -v docker" &>/dev/null; then
    DOCKER_VERSION=$(ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST} "docker --version" 2>/dev/null | cut -d ' ' -f 3 | sed 's/,//')
    echo -e "${GREEN}✅ Docker 설치됨 (버전: ${DOCKER_VERSION})${NC}"
else
    echo -e "${RED}❌ Docker가 설치되어 있지 않습니다.${NC}"
    echo -e "${YELLOW}   Docker 설치가 필요합니다.${NC}"
fi
echo ""

# 4. Docker Compose 설치 확인
echo -e "${YELLOW}🔍 4. Docker Compose 설치 확인${NC}"
if ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST} "command -v docker-compose" &>/dev/null; then
    COMPOSE_VERSION=$(ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST} "docker-compose --version" 2>/dev/null | cut -d ' ' -f 4 | sed 's/,//')
    echo -e "${GREEN}✅ Docker Compose 설치됨 (버전: ${COMPOSE_VERSION})${NC}"
elif ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST} "docker compose version" &>/dev/null; then
    COMPOSE_VERSION=$(ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST} "docker compose version" 2>/dev/null | cut -d ' ' -f 4)
    echo -e "${GREEN}✅ Docker Compose V2 설치됨 (버전: ${COMPOSE_VERSION})${NC}"
else
    echo -e "${RED}❌ Docker Compose가 설치되어 있지 않습니다.${NC}"
    echo -e "${YELLOW}   Docker Compose 설치가 필요합니다.${NC}"
fi
echo ""

# 5. 데이터베이스 접속 테스트 (선택사항)
echo -e "${YELLOW}🔍 5. 데이터베이스 접속 테스트 (선택)${NC}"
if [ -n "$PROD_DB_HOST" ] && [ -n "$PROD_DB_PORT" ]; then
    echo -e "   데이터베이스: ${PROD_DB_HOST}:${PROD_DB_PORT}"
    if ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST} "nc -zv ${PROD_DB_HOST} ${PROD_DB_PORT}" &>/dev/null; then
        echo -e "${GREEN}✅ 데이터베이스 포트 접근 가능${NC}"
    else
        echo -e "${YELLOW}⚠️  데이터베이스 포트 접근 불가 (방화벽 확인 필요)${NC}"
    fi
else
    echo -e "${YELLOW}⏭️  데이터베이스 설정이 없어 건너뜁니다.${NC}"
fi
echo ""

# 6. 디스크 공간 확인
echo -e "${YELLOW}🔍 6. 디스크 공간 확인${NC}"
DISK_USAGE=$(ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST} "df -h ${PROD_DIR} | tail -1 | awk '{print \$5}'" 2>/dev/null | sed 's/%//')
if [ -n "$DISK_USAGE" ]; then
    echo -e "   디스크 사용률: ${DISK_USAGE}%"
    if [ "$DISK_USAGE" -gt 90 ]; then
        echo -e "${RED}❌ 디스크 공간이 부족합니다 (${DISK_USAGE}% 사용 중)${NC}"
    elif [ "$DISK_USAGE" -gt 80 ]; then
        echo -e "${YELLOW}⚠️  디스크 공간 주의 (${DISK_USAGE}% 사용 중)${NC}"
    else
        echo -e "${GREEN}✅ 디스크 공간 충분 (${DISK_USAGE}% 사용 중)${NC}"
    fi
fi
echo ""

# 7. 메모리 확인
echo -e "${YELLOW}🔍 7. 메모리 확인${NC}"
MEMORY_INFO=$(ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST} "free -h | grep Mem" 2>/dev/null)
if [ -n "$MEMORY_INFO" ]; then
    echo -e "   ${MEMORY_INFO}"
    echo -e "${GREEN}✅ 메모리 정보 확인 완료${NC}"
fi
echo ""

# 최종 결과
echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}✅ 서버 접속 테스트 완료${NC}"
echo -e "${GREEN}================================${NC}"
echo ""
echo -e "${YELLOW}📝 다음 단계:${NC}"
echo -e "   1. 프론트엔드 빌드 테스트: cd frontend && npm run build"
echo -e "   2. 배포 스크립트 실행: ./scripts/deploy.sh"
echo ""
