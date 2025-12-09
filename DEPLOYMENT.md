# ERP 시스템 배포 가이드

시놀로지 NAS에 Docker 이미지 기반으로 배포하는 가이드입니다.

---

## 🚀 빠른 시작

### 로컬 개발 (권장)

```bash
# 1. Docker 환경 시작
cp .env.dev.example .env.dev
docker-compose -f docker-compose.dev.yml up -d

# 2. 프론트엔드
cd frontend
npm install
npm run dev
# → http://localhost:5173

# 3. 백엔드 (IntelliJ에서)
# Application.java 실행
# → http://localhost:8080
```

---

### 시놀로지 NAS 배포 (운영)

```bash
# 1. 자동 배포 (권장)
./scripts/deploy-to-synology.sh

# 2. 특정 버전으로 배포
./scripts/deploy-to-synology.sh v1.0.0

# 3. 수동 배포
./scripts/build-and-save.sh                    # 이미지 빌드
scp docker-images/*.tar.gz naudhizfehu@nas.fehu.kr:~/erp-system/
ssh naudhizfehu@nas.fehu.kr "cd ~/erp-system && ./deploy.sh"
```

**접속 URL**:
- 프론트엔드: http://nas.fehu.kr:8990
- 백엔드: http://nas.fehu.kr:8991

---

## 📚 상세 문서

배포에 대한 모든 것은 다음 문서들을 참고하세요:

### 핵심 문서
- **[배포_완전_가이드_최종.md](./claudedocs/배포_완전_가이드_최종.md)** ⭐
  - 단계별 상세 배포 가이드
  - 로컬 Docker 환경 구성
  - 운영 서버 배포 방법
  - 롤백 가이드

### 추가 문서
- **[배포_전략_분석_및_개선안.md](./claudedocs/배포_전략_분석_및_개선안.md)**
  - 배포 전략 분석
  - 위험 요소 및 대응 방안

- **[배포_워크플로우_다이어그램.md](./claudedocs/배포_워크플로우_다이어그램.md)**
  - 시각적 워크플로우
  - Git 브랜치 전략
  - CI/CD 파이프라인

- **[배포_빠른_참조.md](./claudedocs/배포_빠른_참조.md)**
  - 1분 빠른 시작
  - 주요 명령어
  - 트러블슈팅

---

## ⚡ 핵심 명령어

```bash
# 로컬 개발
docker-compose -f docker-compose.dev.yml up -d      # Docker 시작
docker-compose -f docker-compose.dev.yml down       # Docker 종료
npm run dev                                         # 프론트엔드 개발 서버
npm run lint                                        # 린트 검사
npm run type-check                                  # 타입 체크

# 시놀로지 배포
./scripts/build-and-save.sh                        # 이미지 빌드만
./scripts/deploy-to-synology.sh                    # 전체 배포 (빌드+전송+실행)
./scripts/deploy-to-synology.sh v1.0.0             # 버전 지정 배포

# 시놀로지에서 (SSH 접속 후)
cd ~/erp-system
./deploy.sh                                        # 배포 스크립트 실행
sudo docker-compose -f docker-compose.prod.yml ps  # 컨테이너 상태
sudo docker-compose -f docker-compose.prod.yml logs -f  # 로그 확인

# Git
git checkout develop && git push origin develop    # 개발 브랜치
git checkout main && git push origin main          # 운영 브랜치
```

---

## 🔐 환경 설정 파일

### 로컬 개발 (.env.dev)

```bash
cp .env.dev.example .env.dev
```

**주요 설정**:
- `POSTGRES_DB=erp_dev`
- `POSTGRES_USER=erp_user`
- `POSTGRES_PASSWORD=your_password`

### 운영 서버 (.env.production)

```bash
cp .env.production.example .env.production
vim .env.production  # 실제 운영 서버 정보 입력
```

**필수 설정**:
- `PROD_HOST=your-server.com`
- `PROD_USER=ubuntu`
- `PROD_DIR=/home/ubuntu/erp-system`
- `SSH_KEY=$HOME/.ssh/id_rsa`

---

## 📋 체크리스트

### 배포 전
```
□ 린트 오류 0개 (npm run lint)
□ 타입 에러 0개 (npm run type-check)
□ 로컬 + Docker 테스트 완료
□ Git 커밋 메시지 작성
□ 배포 시간 확인 (업무 외 권장)
```

### 배포 후
```
□ 웹사이트 접속 확인
□ 로그인 기능 확인
□ 새 기능 테스트
□ 기존 기능 회귀 테스트
□ 에러 로그 확인
□ 30분간 모니터링
```

---

## 🆘 문제 해결

### 권한 오류

```bash
# 문제: permission denied while trying to connect to Docker daemon
# 해결: sudo 사용 또는 Docker 그룹 추가
ssh naudhizfehu@nas.fehu.kr
sudo usermod -aG docker $USER
newgrp docker
```

### 컨테이너 시작 실패

```bash
# 1. 로그 확인
ssh naudhizfehu@nas.fehu.kr
sudo docker logs erp-backend
sudo docker logs erp-frontend

# 2. 컨테이너 재시작
cd ~/erp-system
sudo docker-compose -f docker-compose.prod.yml restart
```

### 이미지 로드 실패

```bash
# 이미지 파일 확인
ls -lh ~/erp-system/*.tar.gz

# 수동 로드
sudo docker load < ~/erp-system/backend-latest.tar.gz
sudo docker load < ~/erp-system/frontend-latest.tar.gz

# 이미지 확인
sudo docker images | grep cursor-erp
```

### 일반적인 문제

| 문제 | 해결 |
|------|------|
| SSH 연결 실패 | SSH 키 및 호스트 정보 확인 |
| 빌드 실패 | `npm run lint && npm run type-check` |
| 포트 충돌 | `sudo netstat -tlnp \| grep 8990` |
| Docker 오류 | `sudo docker-compose down && up -d` |
| 이미지 없음 | `./scripts/build-and-save.sh` 재실행 |

---

## 📞 도움말

더 자세한 정보는 다음 문서를 참고하세요:
- [배포_완전_가이드_최종.md](./claudedocs/배포_완전_가이드_최종.md)
- [회계관리_모듈_구현계획.md](./claudedocs/회계관리_모듈_구현계획.md)

---

**마지막 업데이트**: 2025-11-04
