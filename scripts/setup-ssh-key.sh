#!/bin/bash

# SSH 키 생성 및 서버 등록 스크립트

set -e

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}================================${NC}"
echo -e "${YELLOW}SSH 키 생성 및 설정${NC}"
echo -e "${YELLOW}================================${NC}"
echo ""

# .env.production 로드 (프로젝트 루트)
if [ ! -f "../.env.production" ]; then
    echo -e "${RED}❌ .env.production 파일이 없습니다.${NC}"
    echo -e "${YELLOW}   경로: 프로젝트루트/.env.production${NC}"
    exit 1
fi

source ../.env.production

# 1. SSH 키 존재 확인
echo -e "${YELLOW}🔍 1. SSH 키 확인${NC}"
SSH_KEY_PATH="$HOME/.ssh/id_rsa"

if [ -f "$SSH_KEY_PATH" ]; then
    echo -e "${GREEN}✅ SSH 키가 이미 존재합니다: $SSH_KEY_PATH${NC}"
    echo -e "${YELLOW}   기존 키를 사용하시겠습니까? (y/n)${NC}"
    read -r USE_EXISTING

    if [ "$USE_EXISTING" != "y" ]; then
        echo -e "${YELLOW}   새로운 키 이름을 입력하세요 (예: id_rsa_erp):${NC}"
        read -r NEW_KEY_NAME
        SSH_KEY_PATH="$HOME/.ssh/$NEW_KEY_NAME"
    fi
fi

# 2. SSH 키 생성
if [ ! -f "$SSH_KEY_PATH" ]; then
    echo -e "${YELLOW}🔑 2. SSH 키 생성 중...${NC}"
    echo -e "${YELLOW}   이메일을 입력하세요:${NC}"
    read -r EMAIL

    ssh-keygen -t rsa -b 4096 -C "$EMAIL" -f "$SSH_KEY_PATH" -N ""

    echo -e "${GREEN}✅ SSH 키 생성 완료${NC}"
    echo -e "   공개키: ${SSH_KEY_PATH}.pub"
    echo -e "   개인키: ${SSH_KEY_PATH}"
else
    echo -e "${GREEN}✅ 기존 SSH 키 사용: $SSH_KEY_PATH${NC}"
fi
echo ""

# 3. 공개키 내용 출력
echo -e "${YELLOW}📋 3. 공개키 내용 (서버에 등록 필요)${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
cat "${SSH_KEY_PATH}.pub"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# 4. 서버에 공개키 등록 안내
echo -e "${YELLOW}📝 4. 서버에 공개키 등록 방법${NC}"
echo -e ""
echo -e "${YELLOW}방법 1: ssh-copy-id 사용 (권장)${NC}"
echo -e "   ${GREEN}ssh-copy-id -p ${PROD_PORT} -i ${SSH_KEY_PATH}.pub ${PROD_USER}@${PROD_HOST}${NC}"
echo -e ""
echo -e "${YELLOW}방법 2: 수동 등록${NC}"
echo -e "   1. 위의 공개키 내용을 복사합니다"
echo -e "   2. 서버에 비밀번호로 접속:"
echo -e "      ${GREEN}ssh -p ${PROD_PORT} ${PROD_USER}@${PROD_HOST}${NC}"
echo -e "   3. 서버에서 다음 명령 실행:"
echo -e "      ${GREEN}mkdir -p ~/.ssh${NC}"
echo -e "      ${GREEN}echo '위에서_복사한_공개키' >> ~/.ssh/authorized_keys${NC}"
echo -e "      ${GREEN}chmod 700 ~/.ssh${NC}"
echo -e "      ${GREEN}chmod 600 ~/.ssh/authorized_keys${NC}"
echo -e ""

# 5. .env.production 업데이트
echo -e "${YELLOW}🔧 5. .env.production 업데이트${NC}"
if [ "$SSH_KEY_PATH" != "$HOME/.ssh/id_rsa" ]; then
    echo -e "${YELLOW}   SSH_KEY 경로를 업데이트하시겠습니까? (y/n)${NC}"
    read -r UPDATE_ENV

    if [ "$UPDATE_ENV" = "y" ]; then
        # .env.production에서 SSH_KEY 라인 업데이트
        sed -i.bak "s|SSH_KEY=.*|SSH_KEY=$SSH_KEY_PATH|" ../.env.production
        echo -e "${GREEN}✅ .env.production 업데이트 완료${NC}"
    fi
fi
echo ""

# 6. 자동 등록 시도
echo -e "${YELLOW}🚀 6. 서버에 자동으로 공개키를 등록하시겠습니까? (y/n)${NC}"
echo -e "${YELLOW}   (서버 비밀번호가 필요합니다)${NC}"
read -r AUTO_REGISTER

if [ "$AUTO_REGISTER" = "y" ]; then
    echo -e "${YELLOW}   ssh-copy-id를 실행합니다...${NC}"
    ssh-copy-id -p ${PROD_PORT} -i ${SSH_KEY_PATH}.pub ${PROD_USER}@${PROD_HOST}

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ 공개키 등록 완료!${NC}"
        echo ""
        echo -e "${YELLOW}🧪 SSH 접속 테스트 중...${NC}"
        ssh -p ${PROD_PORT} -i ${SSH_KEY_PATH} ${PROD_USER}@${PROD_HOST} exit

        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✅ SSH 키 인증 성공!${NC}"
        else
            echo -e "${RED}❌ SSH 키 인증 실패${NC}"
        fi
    else
        echo -e "${RED}❌ 공개키 등록 실패${NC}"
        echo -e "${YELLOW}   수동으로 등록해주세요.${NC}"
    fi
fi
echo ""

echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}✅ SSH 키 설정 완료${NC}"
echo -e "${GREEN}================================${NC}"
echo ""
echo -e "${YELLOW}📝 다음 단계:${NC}"
echo -e "   1. 서버 접속 테스트: ./scripts/check-server-connection.sh"
echo -e "   2. 배포 실행: ./scripts/deploy.sh"
echo ""
