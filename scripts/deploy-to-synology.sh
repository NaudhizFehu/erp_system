#!/bin/bash

###############################################################################
# 시놀로지 NAS 배포 스크립트 (로컬에서 실행)
# 용도: 이미지를 빌드하고 시놀로지로 전송 후 배포
# 사용법: ./scripts/deploy-to-synology.sh [version]
###############################################################################

set -e

# .env.production 파일 로드
if [ -f .env.production ]; then
  source .env.production
  SYNOLOGY_USER="${PROD_USER}"
  SYNOLOGY_HOST="${PROD_HOST}"
  SYNOLOGY_PORT="${PROD_PORT}"
  SYNOLOGY_PATH="${PROD_DIR}"
  SSH_KEY_PATH="${SSH_KEY}"
else
  echo "Error: .env.production 파일이 없습니다."
  echo "cp .env.production.example .env.production 후 설정을 입력하세요."
  exit 1
fi

VERSION=${1:-$(git rev-parse --short HEAD 2>/dev/null || echo "latest")}
SSH_OPTS="-p ${SYNOLOGY_PORT} -i ${SSH_KEY_PATH}"

# 색상
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}시놀로지 배포 시작${NC}"
echo -e "${GREEN}Version: ${VERSION}${NC}"
echo -e "${GREEN}Target: ${SYNOLOGY_USER}@${SYNOLOGY_HOST}${NC}"
echo -e "${GREEN}======================================${NC}"

# 1. 이미지 빌드 및 저장
echo -e "\n${YELLOW}[1/4] 이미지 빌드 중...${NC}"
./scripts/build-and-save.sh ${VERSION}

# 2. docker-compose.prod.yml 전송
echo -e "\n${YELLOW}[2/4] docker-compose.prod.yml 전송 중...${NC}"
scp ${SSH_OPTS} docker-compose.prod.yml ${SYNOLOGY_USER}@${SYNOLOGY_HOST}:${SYNOLOGY_PATH}/

if [ $? -eq 0 ]; then
  echo -e "${GREEN}✓ docker-compose.prod.yml 전송 완료${NC}"
else
  echo -e "${RED}✗ docker-compose.prod.yml 전송 실패${NC}"
  exit 1
fi

# 3. 이미지 파일 전송
echo -e "\n${YELLOW}[3/4] 이미지 파일 전송 중...${NC}"
scp ${SSH_OPTS} docker-images/backend-${VERSION}.tar.gz \
    docker-images/frontend-${VERSION}.tar.gz \
    ${SYNOLOGY_USER}@${SYNOLOGY_HOST}:${SYNOLOGY_PATH}/

if [ $? -eq 0 ]; then
  echo -e "${GREEN}✓ 이미지 파일 전송 완료${NC}"
else
  echo -e "${RED}✗ 이미지 파일 전송 실패${NC}"
  exit 1
fi

# 4. 시놀로지에서 배포 실행
echo -e "\n${YELLOW}[4/4] 시놀로지에서 배포 실행 중...${NC}"
ssh ${SSH_OPTS} ${SYNOLOGY_USER}@${SYNOLOGY_HOST} << EOF
  set -e
  cd ${SYNOLOGY_PATH}

  # 이미지 로드
  echo "이미지 로드 중..."
  sudo docker load < backend-${VERSION}.tar.gz
  sudo docker load < frontend-${VERSION}.tar.gz

  # 이전 버전 태그 제거 (선택적)
  sudo docker tag cursor-erp-backend:${VERSION} cursor-erp-backend:latest || true
  sudo docker tag cursor-erp-frontend:${VERSION} cursor-erp-frontend:latest || true

  # 컨테이너 재시작
  echo "컨테이너 재시작 중..."
  sudo docker-compose -f docker-compose.prod.yml down
  sudo VERSION=${VERSION} docker-compose -f docker-compose.prod.yml up -d

  # 이미지 파일 정리
  rm -f backend-${VERSION}.tar.gz frontend-${VERSION}.tar.gz

  # 사용하지 않는 이미지 정리
  sudo docker image prune -f

  echo "배포 완료!"
EOF

if [ $? -eq 0 ]; then
  echo -e "\n${GREEN}======================================${NC}"
  echo -e "${GREEN}배포 성공!${NC}"
  echo -e "${GREEN}======================================${NC}"
  echo -e "프론트엔드: http://${SYNOLOGY_HOST}:8990"
  echo -e "백엔드: http://${SYNOLOGY_HOST}:8991"
else
  echo -e "\n${RED}배포 실패!${NC}"
  exit 1
fi
