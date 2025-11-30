#!/bin/bash

###############################################################################
# ERP 시스템 이미지 빌드 및 저장 스크립트
# 용도: 로컬에서 Docker 이미지를 빌드하고 tar.gz 파일로 저장
# 사용법: ./scripts/build-and-save.sh [version]
###############################################################################

set -e  # 에러 발생 시 즉시 중단

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 버전 설정 (인자가 없으면 Git commit hash 사용)
VERSION=${1:-$(git rev-parse --short HEAD 2>/dev/null || echo "latest")}
OUTPUT_DIR="./docker-images"

echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}ERP 시스템 이미지 빌드 시작${NC}"
echo -e "${GREEN}Version: ${VERSION}${NC}"
echo -e "${GREEN}======================================${NC}"

# 출력 디렉토리 생성
mkdir -p "${OUTPUT_DIR}"

# 1. 백엔드 이미지 빌드
echo -e "\n${YELLOW}[1/4] 백엔드 이미지 빌드 중...${NC}"
docker build \
  --platform linux/amd64 \
  -t cursor-erp-backend:${VERSION} \
  -t cursor-erp-backend:latest \
  -f backend/Dockerfile \
  ./backend

if [ $? -eq 0 ]; then
  echo -e "${GREEN}✓ 백엔드 이미지 빌드 완료${NC}"
else
  echo -e "${RED}✗ 백엔드 이미지 빌드 실패${NC}"
  exit 1
fi

# 2. 프론트엔드 이미지 빌드
echo -e "\n${YELLOW}[2/4] 프론트엔드 이미지 빌드 중...${NC}"
docker build \
  --platform linux/amd64 \
  -t cursor-erp-frontend:${VERSION} \
  -t cursor-erp-frontend:latest \
  -f frontend/Dockerfile.prod \
  ./frontend

if [ $? -eq 0 ]; then
  echo -e "${GREEN}✓ 프론트엔드 이미지 빌드 완료${NC}"
else
  echo -e "${RED}✗ 프론트엔드 이미지 빌드 실패${NC}"
  exit 1
fi

# 3. 백엔드 이미지 저장
echo -e "\n${YELLOW}[3/4] 백엔드 이미지 저장 중...${NC}"
docker save cursor-erp-backend:${VERSION} | gzip > "${OUTPUT_DIR}/backend-${VERSION}.tar.gz"

if [ $? -eq 0 ]; then
  SIZE=$(du -h "${OUTPUT_DIR}/backend-${VERSION}.tar.gz" | cut -f1)
  echo -e "${GREEN}✓ 백엔드 이미지 저장 완료 (${SIZE})${NC}"
else
  echo -e "${RED}✗ 백엔드 이미지 저장 실패${NC}"
  exit 1
fi

# 4. 프론트엔드 이미지 저장
echo -e "\n${YELLOW}[4/4] 프론트엔드 이미지 저장 중...${NC}"
docker save cursor-erp-frontend:${VERSION} | gzip > "${OUTPUT_DIR}/frontend-${VERSION}.tar.gz"

if [ $? -eq 0 ]; then
  SIZE=$(du -h "${OUTPUT_DIR}/frontend-${VERSION}.tar.gz" | cut -f1)
  echo -e "${GREEN}✓ 프론트엔드 이미지 저장 완료 (${SIZE})${NC}"
else
  echo -e "${RED}✗ 프론트엔드 이미지 저장 실패${NC}"
  exit 1
fi

# 완료 메시지
echo -e "\n${GREEN}======================================${NC}"
echo -e "${GREEN}이미지 빌드 및 저장 완료!${NC}"
echo -e "${GREEN}======================================${NC}"
echo -e "출력 위치: ${OUTPUT_DIR}/"
echo -e "  - backend-${VERSION}.tar.gz"
echo -e "  - frontend-${VERSION}.tar.gz"
echo -e "\n${YELLOW}다음 단계:${NC}"
echo -e "1. 시놀로지로 이미지 전송:"
echo -e "   ${GREEN}scp ${OUTPUT_DIR}/*.tar.gz naudhizfehu@nas.fehu.kr:~/${NC}"
echo -e "\n2. 시놀로지에서 이미지 로드 및 배포:"
echo -e "   ${GREEN}ssh naudhizfehu@nas.fehu.kr${NC}"
echo -e "   ${GREEN}cd ~/erp-system && ./deploy.sh${NC}"
