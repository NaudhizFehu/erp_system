#!/bin/bash

###############################################################################
# 시놀로지 서버 배포 스크립트 (시놀로지에서 실행)
# 위치: ~/erp-system/deploy.sh
# 용도: 이미지 로드 및 컨테이너 재시작
# 사용법: ./deploy.sh [version]
###############################################################################

set -e

VERSION=${1:-latest}

# 색상
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}======================================${NC}"
echo -e "${GREEN}ERP 시스템 배포 스크립트${NC}"
echo -e "${GREEN}Version: ${VERSION}${NC}"
echo -e "${GREEN}======================================${NC}"

# 1. 이미지 로드
if [ -f "backend-${VERSION}.tar.gz" ] && [ -f "frontend-${VERSION}.tar.gz" ]; then
  echo -e "\n${YELLOW}[1/4] 이미지 로드 중...${NC}"

  sudo docker load < backend-${VERSION}.tar.gz
  sudo docker load < frontend-${VERSION}.tar.gz

  echo -e "${GREEN}✓ 이미지 로드 완료${NC}"

  # latest 태그 추가
  sudo docker tag cursor-erp-backend:${VERSION} cursor-erp-backend:latest || true
  sudo docker tag cursor-erp-frontend:${VERSION} cursor-erp-frontend:latest || true
else
  echo -e "${YELLOW}이미지 파일이 없습니다. 기존 이미지 사용${NC}"
fi

# 2. 기존 컨테이너 중지 및 제거
echo -e "\n${YELLOW}[2/4] 기존 컨테이너 중지 중...${NC}"
sudo docker-compose -f docker-compose.prod.yml down || true
echo -e "${GREEN}✓ 컨테이너 중지 완료${NC}"

# 3. 새 컨테이너 시작
echo -e "\n${YELLOW}[3/4] 새 컨테이너 시작 중...${NC}"
sudo VERSION=${VERSION} docker-compose -f docker-compose.prod.yml up -d

if [ $? -eq 0 ]; then
  echo -e "${GREEN}✓ 컨테이너 시작 완료${NC}"
else
  echo -e "${RED}✗ 컨테이너 시작 실패${NC}"
  exit 1
fi

# 4. 정리
echo -e "\n${YELLOW}[4/4] 정리 중...${NC}"

# 이미지 파일 삭제
if [ -f "backend-${VERSION}.tar.gz" ]; then
  rm -f backend-${VERSION}.tar.gz
  echo -e "${GREEN}✓ backend-${VERSION}.tar.gz 삭제${NC}"
fi

if [ -f "frontend-${VERSION}.tar.gz" ]; then
  rm -f frontend-${VERSION}.tar.gz
  echo -e "${GREEN}✓ frontend-${VERSION}.tar.gz 삭제${NC}"
fi

# 사용하지 않는 이미지 정리
sudo docker image prune -f > /dev/null 2>&1
echo -e "${GREEN}✓ 미사용 이미지 정리 완료${NC}"

# 5. 컨테이너 상태 확인
echo -e "\n${YELLOW}컨테이너 상태:${NC}"
sudo docker-compose -f docker-compose.prod.yml ps

# 완료
echo -e "\n${GREEN}======================================${NC}"
echo -e "${GREEN}배포 완료!${NC}"
echo -e "${GREEN}======================================${NC}"
echo -e "프론트엔드: ${GREEN}http://nas.fehu.kr:8990${NC}"
echo -e "백엔드: ${GREEN}http://nas.fehu.kr:8991${NC}"
echo -e "\n${YELLOW}로그 확인:${NC}"
echo -e "  sudo docker-compose -f docker-compose.prod.yml logs -f"
