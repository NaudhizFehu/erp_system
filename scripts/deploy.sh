#!/bin/bash

# ERP 시스템 운영 서버 배포 스크립트
# SSH 키 또는 비밀번호 방식 모두 지원

set -e

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}================================${NC}"
echo -e "${BLUE}ERP 시스템 배포 시작${NC}"
echo -e "${BLUE}================================${NC}"
echo ""

# .env.production 파일 확인 (프로젝트 루트)
if [ ! -f "../.env.production" ]; then
    echo -e "${RED}❌ .env.production 파일이 없습니다.${NC}"
    echo -e "${YELLOW}   경로: 프로젝트루트/.env.production${NC}"
    exit 1
fi

source ../.env.production

# 필수 변수 확인
if [ -z "$PROD_HOST" ] || [ -z "$PROD_USER" ] || [ -z "$PROD_PORT" ]; then
    echo -e "${RED}❌ .env.production 파일에 필수 변수가 설정되지 않았습니다.${NC}"
    exit 1
fi

echo -e "${YELLOW}📋 배포 정보${NC}"
echo -e "   호스트: ${PROD_HOST}"
echo -e "   사용자: ${PROD_USER}"
echo -e "   포트: ${PROD_PORT}"
echo -e "   배포 경로: ${PROD_DIR}"
echo ""

# SSH 인증 방식 선택
echo -e "${YELLOW}🔐 SSH 인증 방식 선택${NC}"
echo -e "   1) SSH 키 방식 (자동화, 추천)"
echo -e "   2) 비밀번호 방식 (수동 입력)"
echo -e "${YELLOW}선택 (1 또는 2): ${NC}"
read -r AUTH_METHOD

SSH_OPTIONS=""
if [ "$AUTH_METHOD" = "1" ]; then
    # SSH 키 방식
    if [ -z "$SSH_KEY" ]; then
        SSH_KEY="$HOME/.ssh/id_rsa"
    fi

    if [ ! -f "$SSH_KEY" ]; then
        echo -e "${RED}❌ SSH 키가 없습니다: $SSH_KEY${NC}"
        echo -e "${YELLOW}   ./scripts/setup-ssh-key.sh 를 먼저 실행해주세요.${NC}"
        exit 1
    fi

    SSH_OPTIONS="-i $SSH_KEY"
    echo -e "${GREEN}✅ SSH 키 방식 사용: $SSH_KEY${NC}"
else
    # 비밀번호 방식
    echo -e "${YELLOW}⚠️  비밀번호 방식 사용 (여러 번 입력 필요)${NC}"
    SSH_OPTIONS=""
fi
echo ""

# SSH 접속 함수
ssh_exec() {
    ssh -p ${PROD_PORT} ${SSH_OPTIONS} ${PROD_USER}@${PROD_HOST} "$@"
}

scp_copy() {
    scp -P ${PROD_PORT} ${SSH_OPTIONS} "$@"
}

# 1. 프론트엔드 빌드
echo -e "${YELLOW}🔨 1. 프론트엔드 빌드${NC}"
cd ../frontend
npm run build

if [ ! -d "dist" ]; then
    echo -e "${RED}❌ 빌드 실패: dist 폴더가 없습니다.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ 프론트엔드 빌드 완료${NC}"
cd ..
echo ""

# 2. 백엔드 빌드
echo -e "${YELLOW}🔨 2. 백엔드 빌드${NC}"
cd backend

# Maven 빌드
if [ -f "mvnw" ]; then
    ./mvnw clean package -DskipTests
else
    mvn clean package -DskipTests
fi

if [ ! -f "target/*.jar" ]; then
    echo -e "${RED}❌ 빌드 실패: JAR 파일이 없습니다.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ 백엔드 빌드 완료${NC}"
cd ..
echo ""

# 3. 서버 접속 확인
echo -e "${YELLOW}🔍 3. 서버 접속 확인${NC}"
if ssh_exec exit 2>/dev/null; then
    echo -e "${GREEN}✅ 서버 접속 성공${NC}"
else
    echo -e "${RED}❌ 서버 접속 실패${NC}"
    exit 1
fi
echo ""

# 4. 배포 디렉토리 생성
echo -e "${YELLOW}📁 4. 배포 디렉토리 확인${NC}"
ssh_exec "mkdir -p ${PROD_DIR}/{frontend,backend}"
echo -e "${GREEN}✅ 배포 디렉토리 생성 완료${NC}"
echo ""

# 5. 프론트엔드 파일 전송
echo -e "${YELLOW}📤 5. 프론트엔드 파일 전송${NC}"
scp_copy -r frontend/dist/* ${PROD_USER}@${PROD_HOST}:${PROD_DIR}/frontend/
echo -e "${GREEN}✅ 프론트엔드 파일 전송 완료${NC}"
echo ""

# 6. 백엔드 JAR 파일 전송
echo -e "${YELLOW}📤 6. 백엔드 JAR 파일 전송${NC}"
JAR_FILE=$(ls backend/target/*.jar | head -1)
scp_copy $JAR_FILE ${PROD_USER}@${PROD_HOST}:${PROD_DIR}/backend/app.jar
echo -e "${GREEN}✅ 백엔드 JAR 파일 전송 완료${NC}"
echo ""

# 7. 환경 설정 파일 전송 (선택)
echo -e "${YELLOW}📤 7. 환경 설정 파일 전송${NC}"
echo -e "${YELLOW}   application-prod.yml 파일을 전송하시겠습니까? (y/n)${NC}"
read -r UPLOAD_CONFIG

if [ "$UPLOAD_CONFIG" = "y" ]; then
    if [ -f "backend/src/main/resources/application-prod.yml" ]; then
        scp_copy backend/src/main/resources/application-prod.yml ${PROD_USER}@${PROD_HOST}:${PROD_DIR}/backend/
        echo -e "${GREEN}✅ 환경 설정 파일 전송 완료${NC}"
    else
        echo -e "${YELLOW}⚠️  application-prod.yml 파일이 없습니다.${NC}"
    fi
fi
echo ""

# 8. 애플리케이션 재시작
echo -e "${YELLOW}🔄 8. 애플리케이션 재시작${NC}"
echo -e "${YELLOW}   백엔드 애플리케이션을 재시작하시겠습니까? (y/n)${NC}"
read -r RESTART_APP

if [ "$RESTART_APP" = "y" ]; then
    echo -e "${YELLOW}   기존 프로세스 종료 중...${NC}"
    ssh_exec "pkill -f 'app.jar' || true"

    sleep 2

    echo -e "${YELLOW}   새 프로세스 시작 중...${NC}"
    ssh_exec "cd ${PROD_DIR}/backend && nohup java -jar app.jar --spring.profiles.active=prod > app.log 2>&1 &"

    sleep 3

    echo -e "${GREEN}✅ 백엔드 애플리케이션 재시작 완료${NC}"
fi
echo ""

# 9. Nginx 재시작 (선택)
echo -e "${YELLOW}🔄 9. Nginx 재시작${NC}"
echo -e "${YELLOW}   Nginx를 재시작하시겠습니까? (y/n)${NC}"
read -r RESTART_NGINX

if [ "$RESTART_NGINX" = "y" ]; then
    ssh_exec "sudo systemctl restart nginx || sudo service nginx restart"
    echo -e "${GREEN}✅ Nginx 재시작 완료${NC}"
fi
echo ""

# 10. 배포 확인
echo -e "${YELLOW}🧪 10. 배포 확인${NC}"
echo -e "${YELLOW}   백엔드 상태 확인 중...${NC}"

BACKEND_STATUS=$(ssh_exec "ps aux | grep 'app.jar' | grep -v grep || echo 'NOT_RUNNING'")
if [ "$BACKEND_STATUS" != "NOT_RUNNING" ]; then
    echo -e "${GREEN}✅ 백엔드 실행 중${NC}"
else
    echo -e "${RED}❌ 백엔드가 실행되지 않음${NC}"
fi

echo -e "${YELLOW}   프론트엔드 파일 확인 중...${NC}"
FRONTEND_FILES=$(ssh_exec "ls ${PROD_DIR}/frontend | wc -l")
if [ "$FRONTEND_FILES" -gt 0 ]; then
    echo -e "${GREEN}✅ 프론트엔드 파일 존재 (${FRONTEND_FILES}개)${NC}"
else
    echo -e "${RED}❌ 프론트엔드 파일 없음${NC}"
fi
echo ""

# 완료
echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}✅ 배포 완료!${NC}"
echo -e "${GREEN}================================${NC}"
echo ""
echo -e "${YELLOW}📝 접속 정보:${NC}"
echo -e "   프론트엔드: ${VITE_API_BASE_URL}"
echo -e "   백엔드: ${VITE_API_BASE_URL}/api"
echo ""
echo -e "${YELLOW}📋 확인사항:${NC}"
echo -e "   1. 웹 브라우저에서 프론트엔드 접속 확인"
echo -e "   2. API 엔드포인트 동작 확인"
echo -e "   3. 로그 확인: ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST} 'tail -f ${PROD_DIR}/backend/app.log'"
echo ""
