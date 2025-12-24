# ERP 시스템 전체 배포 프로세스 가이드

**작성일**: 2025-12-23
**대상**: Cursor ERP System 개발자 및 운영자

---

## 📋 목차

1. [배포 프로세스 개요](#1-배포-프로세스-개요)
2. [사전 준비사항](#2-사전-준비사항)
3. [GitLab CI/CD 파이프라인](#3-gitlab-cicd-파이프라인)
4. [로컬 개발 환경 배포](#4-로컬-개발-환경-배포)
5. [Synology NAS 프로덕션 배포](#5-synology-nas-프로덕션-배포)
6. [Blue-Green 무중단 배포](#6-blue-green-무중단-배포)
7. [배포 후 검증](#7-배포-후-검증)
8. [롤백 절차](#8-롤백-절차)
9. [트러블슈팅](#9-트러블슈팅)
10. [FAQ](#10-faq)

---

## 1. 배포 프로세스 개요

### 1.1 전체 배포 흐름도

```
┌─────────────┐
│  코드 작성   │
└──────┬──────┘
       │
       ▼
┌─────────────────────┐
│  Git Push (GitLab)  │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│  GitLab CI/CD       │ ◄── 자동 테스트 및 빌드
│  - Lint             │
│  - Test             │
│  - Build            │
└──────┬──────────────┘
       │
       ▼ (수동 트리거)
┌─────────────────────┐
│  로컬 배포 스크립트  │
│  deploy-to-         │
│  synology-bluegreen │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│  Docker 이미지 빌드  │
│  - Backend WAR      │
│  - Frontend Dist    │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│  Synology NAS 전송  │
│  (rsync over SSH)   │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│  Blue-Green 배포    │
│  - Health Check     │
│  - 수동 승인        │
│  - nginx 전환       │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│  서비스 중단 0초    │
│  배포 완료 ✅       │
└─────────────────────┘
```

### 1.2 주요 구성 요소

| 구성 요소 | 설명 | 위치 |
|----------|------|------|
| **GitLab CI/CD** | 자동 테스트 및 빌드 | `.gitlab-ci.yml` |
| **Docker Compose** | Blue/Green 환경 정의 | `docker-compose.prod.yml` |
| **배포 스크립트** | 로컬 → Synology 배포 자동화 | `scripts/deploy-to-synology-bluegreen.sh` |
| **무중단 배포 스크립트** | Blue-Green 전환 | `scripts/blue-green-deploy.sh` |
| **롤백 스크립트** | 5초 즉시 롤백 | `scripts/blue-green-rollback.sh` |
| **nginx** | 트래픽 라우팅 | Synology NAS |

---

## 2. 사전 준비사항

### 2.1 GitLab Runner 설정 (이미 완료됨 ✅)

GitLab Runner는 이미 설치되어 있으므로 확인만 하면 됩니다:

```bash
# GitLab Runner 상태 확인
gitlab-runner status

# Runner 목록 확인
gitlab-runner list
```

**예상 출력**:
```
mac-local-runner    Executor=shell Token=glrt-xxx URL=http://gitlab.fehu.kr/
```

### 2.2 로컬 개발 환경 설정

#### 필수 도구 설치 확인

```bash
# Docker 설치 확인
docker --version
# 예상 출력: Docker version 24.x.x

# Maven 설치 확인 (Backend 빌드용)
mvn --version
# 예상 출력: Apache Maven 3.9.11

# Node.js 설치 확인 (Frontend 빌드용)
node --version
# 예상 출력: v18.x.x 이상

# SSH 키 설정 확인 (Synology 접속용)
ls -la ~/.ssh/id_rsa
# Synology에 공개키가 등록되어 있어야 함
```

#### SSH 키 등록 (아직 안했다면)

```bash
# 1. SSH 키 생성 (이미 있으면 건너뛰기)
ssh-keygen -t rsa -b 4096 -C "your_email@example.com"

# 2. Synology에 공개키 복사
ssh-copy-id naudhizfehu@fehu.kr

# 3. 접속 테스트
ssh naudhizfehu@fehu.kr
# 비밀번호 없이 접속되어야 함
```

### 2.3 환경 변수 파일 설정

```bash
# .env.bluegreen 파일이 이미 생성되어 있는지 확인
ls -la .env.bluegreen

# 없다면 예제 파일에서 복사
cp .env.bluegreen.example .env.bluegreen

# 실제 서버 정보로 수정 (이미 설정되어 있음)
cat .env.bluegreen
```

**`.env.bluegreen` 내용 확인**:
```bash
SYNOLOGY_HOST=fehu.kr
SYNOLOGY_USER=naudhizfehu
SYNOLOGY_PORT=22
SSH_KEY=$HOME/.ssh/id_rsa
DEPLOY_DIR=/var/services/homes/naudhizfehu/erp-system
NGINX_CONF_PATH=/etc/nginx/sites-enabled/erp.conf
VERSION=latest
HEALTH_CHECK_TIMEOUT=60
HEALTH_CHECK_INTERVAL=5
```

### 2.4 Synology NAS 초기 설정

#### 2.4.1 디렉토리 구조 생성

Synology NAS에 SSH 접속 후 디렉토리 생성:

```bash
# Synology에 SSH 접속
ssh naudhizfehu@fehu.kr

# 배포 디렉토리 생성
mkdir -p ~/erp-system/{images,scripts}

# 디렉토리 구조 확인
tree -L 2 ~/erp-system
```

**예상 디렉토리 구조**:
```
~/erp-system/
├── docker-compose.prod.yml    (배포 시 자동 생성)
├── .env                        (배포 시 자동 생성)
├── images/                     (Docker 이미지 저장소)
│   ├── backend.tar.gz
│   └── frontend.tar.gz
└── scripts/                    (배포 스크립트)
    ├── blue-green-deploy.sh
    ├── blue-green-rollback.sh
    └── nginx-bluegreen.conf.template
```

#### 2.4.2 nginx 설정

```bash
# Synology에서 nginx 설정 디렉토리 확인
ls -la /etc/nginx/sites-enabled/

# nginx 설정 템플릿 업로드 (로컬에서)
scp scripts/nginx-bluegreen.conf.template naudhizfehu@fehu.kr:/tmp/

# Synology에서 설정 파일 이동 및 적용
ssh naudhizfehu@fehu.kr
sudo mv /tmp/nginx-bluegreen.conf.template /etc/nginx/sites-enabled/erp.conf
sudo nginx -t  # 설정 파일 문법 검사
sudo nginx -s reload  # nginx 재시작
```

**nginx 설정 내용 확인**:
```nginx
upstream active-backend {
    server localhost:8991;  # 초기값: Blue 환경
}

upstream active-frontend {
    server localhost:8990;  # 초기값: Blue 환경
}

server {
    listen 80;
    server_name erp.fehu.kr;

    location / {
        proxy_pass http://active-frontend;
    }

    location /api/ {
        proxy_pass http://active-backend/;
    }

    location /health {
        proxy_pass http://active-backend/actuator/health;
    }
}
```

#### 2.4.3 배포 스크립트 업로드

```bash
# 로컬에서 배포 스크립트들을 Synology로 복사
scp scripts/blue-green-deploy.sh naudhizfehu@fehu.kr:~/erp-system/scripts/
scp scripts/blue-green-rollback.sh naudhizfehu@fehu.kr:~/erp-system/scripts/

# Synology에서 실행 권한 부여
ssh naudhizfehu@fehu.kr
chmod +x ~/erp-system/scripts/*.sh
```

---

## 3. GitLab CI/CD 파이프라인

### 3.1 파이프라인 구조

`.gitlab-ci.yml` 파일에 정의된 자동화 프로세스:

```yaml
stages:
  - lint      # 코드 품질 검사
  - test      # 자동 테스트 실행
  - build     # 빌드 및 아티팩트 생성
```

### 3.2 각 Stage 상세

#### Stage 1: Lint (코드 품질 검사)

```yaml
frontend-lint:
  stage: lint
  script:
    - cd frontend
    - npm ci
    - npm run lint        # ESLint 검사
    - npm run type-check  # TypeScript 타입 검사
    - npm run format:check # Prettier 포맷 검사
```

**검사 항목**:
- ESLint 규칙 위반 여부
- TypeScript 타입 오류
- 코드 포맷팅 일관성

**통과 조건**: 모든 검사 0 에러

#### Stage 2: Test (자동 테스트)

**Backend Test**:
```yaml
backend-test:
  stage: build
  script:
    - cd backend
    - mvn clean package  # 16개 JUnit 테스트 자동 실행
```

**Frontend Test**:
```yaml
frontend-test:
  stage: test
  script:
    - cd frontend
    - npm ci
    - npm run test  # 14개 Vitest 테스트 실행
```

**테스트 현황**:
- Backend: 16/16 테스트 통과 ✅
- Frontend: 14/14 테스트 통과 ✅

#### Stage 3: Build (빌드)

**Backend Build**:
```yaml
backend-test:
  script:
    - mvn clean package
  artifacts:
    paths:
      - backend/target/*.war
    expire_in: 7 days
```

**산출물**: `backend/target/ROOT.war`

**Frontend Build**:
```yaml
frontend-build:
  script:
    - npm run build
  artifacts:
    paths:
      - frontend/dist/
    expire_in: 7 days
```

**산출물**: `frontend/dist/` 디렉토리

### 3.3 파이프라인 실행 확인

```bash
# 코드 푸시 시 자동 실행됨
git push origin develop

# GitLab 웹 UI에서 확인:
# http://gitlab.fehu.kr/developer/cursor-erp-system/-/pipelines
```

**파이프라인 성공 조건**:
- ✅ frontend-lint: 통과
- ✅ frontend-test: 14/14 통과
- ✅ backend-test: 16/16 통과
- ✅ frontend-build: dist 생성 성공

---

## 4. 로컬 개발 환경 배포

### 4.1 로컬 Docker Compose 실행

개발 중 로컬에서 테스트할 때:

```bash
# 개발 환경 실행 (Hot Reload 지원)
docker-compose -f docker-compose.dev.yml up -d

# 로그 확인
docker-compose -f docker-compose.dev.yml logs -f

# 접속 확인
open http://localhost:3000  # Frontend
open http://localhost:8991  # Backend
```

### 4.2 프로덕션 모드 로컬 테스트

Synology 배포 전 로컬에서 프로덕션 빌드 테스트:

```bash
# 1. 프로덕션 이미지 빌드
docker-compose -f docker-compose.prod.yml build

# 2. Blue 환경으로 실행
docker-compose -f docker-compose.prod.yml up -d

# 3. Health Check 확인
curl http://localhost:8991/actuator/health

# 4. 프론트엔드 확인
open http://localhost:8990

# 5. 정리
docker-compose -f docker-compose.prod.yml down
```

---

## 5. Synology NAS 프로덕션 배포

### 5.1 배포 전 체크리스트

배포하기 전 반드시 확인할 사항:

```bash
# ✅ 1. Git 상태 확인 (커밋되지 않은 변경사항 없어야 함)
git status
# 출력: nothing to commit, working tree clean

# ✅ 2. 최신 코드로 업데이트
git pull origin develop

# ✅ 3. GitLab CI/CD 파이프라인 성공 확인
# http://gitlab.fehu.kr/developer/cursor-erp-system/-/pipelines

# ✅ 4. Lint 통과 확인
cd frontend && npm run lint && cd ..

# ✅ 5. 타입 체크 통과 확인
cd frontend && npm run type-check && cd ..

# ✅ 6. 로컬 테스트 통과 확인
cd frontend && npm run test && cd ..
cd backend && mvn test && cd ..

# ✅ 7. Synology 접속 가능 확인
ssh naudhizfehu@fehu.kr "echo 'Connection OK'"

# ✅ 8. 환경 변수 파일 확인
cat .env.bluegreen
```

### 5.2 배포 실행

#### 방법 1: 자동 배포 스크립트 (권장 ⭐)

```bash
# 전체 프로세스 자동 실행
./scripts/deploy-to-synology-bluegreen.sh
```

**스크립트가 수행하는 작업**:
1. ✅ 배포 전 체크리스트 자동 검증
2. ✅ Backend 빌드 (mvn clean package)
3. ✅ Frontend 빌드 (npm run build)
4. ✅ Docker 이미지 빌드
5. ✅ 이미지를 tar.gz로 압축
6. ✅ Synology로 전송 (rsync)
7. ✅ Synology에서 이미지 로드
8. ✅ Blue-Green 배포 스크립트 실행

**실행 화면 예시**:
```
🚀 Cursor ERP System - Blue-Green Deployment to Synology NAS
=============================================================

📋 Pre-deployment Checklist
---------------------------
✅ Git status: Clean
✅ Git branch: develop
✅ Latest commit: 10dff01
✅ .env.bluegreen file exists
✅ SSH connection to Synology: OK

🔨 Building Backend...
[INFO] BUILD SUCCESS

🎨 Building Frontend...
✓ built in 4.38s

🐳 Building Docker Images...
[+] Building 45.2s

📦 Saving Docker Images...
✓ backend.tar.gz (850MB)
✓ frontend.tar.gz (125MB)

📤 Transferring to Synology...
sending incremental file list
backend.tar.gz
          850.12M 100%   45.23MB/s    0:00:18

🚢 Loading Images on Synology...
Loaded image: cursor-erp-backend:latest
Loaded image: cursor-erp-frontend:latest

🎯 Starting Blue-Green Deployment...
[진행중... 자세한 내용은 6장 참고]
```

#### 방법 2: 수동 단계별 배포

자동 스크립트가 실패하거나 디버깅이 필요한 경우:

```bash
# 1. Backend 빌드
cd backend
mvn clean package
cd ..

# 2. Frontend 빌드
cd frontend
npm run build
cd ..

# 3. Docker 이미지 빌드
docker build -f backend/Dockerfile -t cursor-erp-backend:latest ./backend
docker build -f frontend/Dockerfile -t cursor-erp-frontend:latest ./frontend

# 4. 이미지 저장
mkdir -p /tmp/erp-images
docker save cursor-erp-backend:latest | gzip > /tmp/erp-images/backend.tar.gz
docker save cursor-erp-frontend:latest | gzip > /tmp/erp-images/frontend.tar.gz

# 5. Synology로 전송
rsync -avz --progress /tmp/erp-images/ naudhizfehu@fehu.kr:~/erp-system/images/

# 6. Synology에서 이미지 로드
ssh naudhizfehu@fehu.kr << 'EOF'
cd ~/erp-system/images
docker load < backend.tar.gz
docker load < frontend.tar.gz
EOF

# 7. Blue-Green 배포 실행 (다음 섹션 참고)
```

---

## 6. Blue-Green 무중단 배포

### 6.1 Blue-Green 배포란?

**개념**:
- **Blue 환경**: 현재 운영 중인 서비스
- **Green 환경**: 새 버전을 배포할 대기 환경
- **전환**: nginx upstream만 변경하여 즉시 트래픽 전환

**장점**:
- ⚡ 서비스 중단 **0초**
- 🔄 5초 이내 즉시 롤백 가능
- ✅ 새 버전 Health Check 후 전환
- 🛡️ 이전 버전 유지로 안전성 확보

### 6.2 배포 프로세스 상세

#### Step 1: Synology SSH 접속

```bash
ssh naudhizfehu@fehu.kr
cd ~/erp-system
```

#### Step 2: 현재 환경 확인

```bash
# 현재 활성 환경 확인
./scripts/blue-green-deploy.sh --status

# 예상 출력:
# 현재 활성 환경: blue
# Backend: localhost:8991
# Frontend: localhost:8990
```

#### Step 3: Blue-Green 배포 실행

```bash
# Blue-Green 배포 스크립트 실행
./scripts/blue-green-deploy.sh
```

**실행 화면 및 설명**:

```
🚀 Blue-Green Deployment Script
=================================

📊 Step 1: 환경 감지
---------------------------------
✅ 현재 활성 환경: blue
✅ 배포 대상 환경: green
✅ Docker 이미지 확인 완료

📦 Step 2: Green 환경 배포
---------------------------------
[+] Running 2/2
✅ Container erp-backend-green   Started
✅ Container erp-frontend-green  Started

🏥 Step 3: Health Check (최대 60초)
---------------------------------
⏳ Checking http://localhost:8993/actuator/health...
⏳ Waiting... (5초)
⏳ Waiting... (10초)
✅ Green 환경 정상 동작 확인!

Response: {"status":"UP","components":{"db":{"status":"UP"}}}

⚠️  Step 4: 수동 승인 필요
---------------------------------
현재 상태:
  - Blue (활성): http://localhost:8991 ✅
  - Green (대기): http://localhost:8993 ✅

트래픽을 Blue → Green으로 전환하시겠습니까?
(전환 시 모든 사용자가 Green 환경으로 연결됩니다)

승인하려면 'yes'를 입력하세요: _
```

**⚠️ 여기서 선택**:

**Option A: 배포 진행 (yes 입력)**
```
승인하려면 'yes'를 입력하세요: yes

🔄 Step 5: nginx Upstream 전환
---------------------------------
✅ nginx 설정 백업 완료: /etc/nginx/sites-enabled/erp.conf.backup.20251223_143022
✅ active-backend: 8991 → 8993
✅ active-frontend: 8990 → 8992
✅ nginx reload 성공

✅ Step 6: 전환 후 검증
---------------------------------
⏳ Green 환경 최종 확인...
✅ Backend Health: OK
✅ Frontend Health: OK

📊 Step 7: 이전 환경 확인
---------------------------------
Blue 환경 상태: 실행 중 (롤백 대비 유지)

Container Name          Status     Ports
erp-backend-blue        Up         8991->8991
erp-frontend-blue       Up         8990->80

⚠️  참고: Blue 환경은 롤백을 위해 유지됩니다.
        문제 발생 시 ./scripts/blue-green-rollback.sh 실행

✅ 배포 완료! 🎉
---------------------------------
활성 환경: green
Backend: http://localhost:8993
Frontend: http://localhost:8992
nginx: http://erp.fehu.kr (→ green)

배포 시간: 2025-12-23 14:30:45
소요 시간: 약 2분 15초
```

**Option B: 배포 취소 (no 입력)**
```
승인하려면 'yes'를 입력하세요: no

❌ 배포 취소됨
Green 환경을 정리합니다...
✅ Green 환경 중지 완료
```

### 6.3 배포 후 Blue 환경 정리 (선택사항)

배포가 성공적으로 완료되고 일정 시간(예: 1주일) 동안 문제가 없다면:

```bash
# Blue 환경 중지 (리소스 절약)
docker-compose -f docker-compose.prod.yml stop backend-blue frontend-blue

# 또는 완전히 제거
docker-compose -f docker-compose.prod.yml rm -f backend-blue frontend-blue
```

**⚠️ 주의**: 롤백이 불가능해지므로 충분한 검증 후에만 실행하세요.

---

## 7. 배포 후 검증

### 7.1 Health Check 확인

```bash
# Backend Health Check
curl http://erp.fehu.kr/health

# 예상 응답:
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

### 7.2 Frontend 접속 확인

```bash
# 브라우저에서 접속
open http://erp.fehu.kr

# 또는 curl로 확인
curl -I http://erp.fehu.kr
# HTTP/1.1 200 OK
```

### 7.3 API 동작 확인

```bash
# 로그인 API 테스트
curl -X POST http://erp.fehu.kr/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "password": "test_password"
  }'

# 예상 응답:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "username": "test_user",
  "role": "USER"
}
```

### 7.4 로그 확인

```bash
# Backend 로그
docker logs erp-backend-green -f

# Frontend 로그
docker logs erp-frontend-green -f

# nginx 로그
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

### 7.5 브라우저 개발자 도구 확인

1. 브라우저에서 `http://erp.fehu.kr` 접속
2. F12 (개발자 도구) 열기
3. **Console 탭**: JavaScript 오류 없는지 확인
4. **Network 탭**: API 요청 200 OK 확인
5. **Application 탭**: LocalStorage/SessionStorage 확인

### 7.6 기능 테스트 체크리스트

```
□ 로그인 성공
□ 계정과목 목록 조회
□ 회계 전표 입력
□ 재무제표 조회
□ 사용자 관리 (ADMIN 권한)
□ 데이터 내보내기 (CSV/Excel)
□ 로그아웃 성공
```

---

## 8. 롤백 절차

### 8.1 롤백이 필요한 경우

- ❌ Health Check 실패
- ❌ API 응답 오류 (500 에러)
- ❌ Frontend 렌더링 오류
- ❌ 데이터베이스 연결 실패
- ❌ 사용자 기능 장애 신고

### 8.2 즉시 롤백 실행

```bash
# Synology에 SSH 접속
ssh naudhizfehu@fehu.kr
cd ~/erp-system

# 롤백 스크립트 실행
./scripts/blue-green-rollback.sh
```

**실행 화면**:

```
🔄 Blue-Green Rollback Script
===============================

📊 Step 1: 현재 상태 확인
---------------------------------
✅ 현재 활성 환경: green
✅ 롤백 대상 환경: blue

🏥 Step 2: Blue 환경 Health Check
---------------------------------
⏳ Checking http://localhost:8991/actuator/health...
✅ Blue 환경 정상 동작 확인!

⚠️  Step 3: 롤백 확인
---------------------------------
green (현재) → blue (이전)로 롤백하시겠습니까?
(모든 사용자가 이전 버전으로 연결됩니다)

롤백하려면 'yes'를 입력하세요: yes

🔄 Step 4: nginx Upstream 전환
---------------------------------
⏱️  시작 시간: 2025-12-23 14:35:12
✅ nginx 설정 백업 완료
✅ active-backend: 8993 → 8991
✅ active-frontend: 8992 → 8990
✅ nginx reload 성공
⏱️  완료 시간: 2025-12-23 14:35:15

✅ 롤백 완료! ⚡
---------------------------------
롤백 소요 시간: 3초
활성 환경: blue (이전 버전으로 복구됨)
nginx: http://erp.fehu.kr (→ blue)
```

### 8.3 롤백 후 검증

```bash
# Health Check 확인
curl http://erp.fehu.kr/health

# Frontend 확인
open http://erp.fehu.kr

# 로그 확인
docker logs erp-backend-blue -f
```

### 8.4 Green 환경 문제 분석

롤백 후 Green 환경에서 무엇이 문제였는지 분석:

```bash
# Green 환경 로그 확인
docker logs erp-backend-green --tail 100

# Green 환경 컨테이너 상태 확인
docker ps -a | grep green

# Green 환경 직접 테스트 (nginx 우회)
curl http://localhost:8993/actuator/health
```

---

## 9. 트러블슈팅

### 9.1 배포 스크립트 실패

#### 문제: "Permission denied" 오류

```
./scripts/deploy-to-synology-bluegreen.sh: Permission denied
```

**해결**:
```bash
chmod +x scripts/*.sh
```

#### 문제: SSH 연결 실패

```
ssh: connect to host fehu.kr port 22: Connection refused
```

**해결**:
```bash
# 1. Synology SSH 서비스 활성화 확인
# Synology DSM → 제어판 → 터미널 및 SNMP → SSH 서비스 활성화

# 2. 방화벽 확인
# Synology DSM → 제어판 → 보안 → 방화벽 → 22번 포트 허용

# 3. SSH 키 재등록
ssh-copy-id naudhizfehu@fehu.kr
```

#### 문제: rsync 전송 실패

```
rsync: connection unexpectedly closed
```

**해결**:
```bash
# rsync 설치 확인 (Synology)
ssh naudhizfehu@fehu.kr "which rsync"

# Synology에 rsync 설치 (없는 경우)
# DSM → 패키지 센터 → rsync 설치
```

### 9.2 Docker 이미지 문제

#### 문제: Docker 빌드 실패

```
ERROR [backend 5/5] COPY target/ROOT.war /usr/local/tomcat/webapps/
```

**해결**:
```bash
# Backend WAR 파일 빌드 확인
cd backend
mvn clean package
ls -lh target/ROOT.war  # 파일 존재 확인
```

#### 문제: Docker 이미지 로드 실패

```
Error loading image: invalid tar header
```

**해결**:
```bash
# tar.gz 파일 무결성 확인
gzip -t /tmp/erp-images/backend.tar.gz

# 재생성
docker save cursor-erp-backend:latest | gzip > /tmp/erp-images/backend.tar.gz
```

### 9.3 Blue-Green 배포 문제

#### 문제: Health Check 타임아웃

```
❌ Health Check 실패: 60초 타임아웃
```

**해결**:
```bash
# 1. 컨테이너 로그 확인
docker logs erp-backend-green

# 2. 수동 Health Check
curl -v http://localhost:8993/actuator/health

# 3. 데이터베이스 연결 확인
docker exec -it erp-backend-green bash
psql -h fehu.kr -U cursor_erp_system -d cursor_erp_system

# 4. Health Check 타임아웃 늘리기 (.env.bluegreen)
HEALTH_CHECK_TIMEOUT=120
```

#### 문제: nginx 설정 오류

```
nginx: [emerg] invalid host in upstream "localhost:8993"
```

**해결**:
```bash
# nginx 설정 파일 확인
sudo cat /etc/nginx/sites-enabled/erp.conf

# 문법 검사
sudo nginx -t

# 올바른 upstream 설정 복원
sudo cp ~/erp-system/scripts/nginx-bluegreen.conf.template /etc/nginx/sites-enabled/erp.conf
sudo nginx -t && sudo nginx -s reload
```

#### 문제: 포트 충돌

```
Error starting userland proxy: listen tcp4 0.0.0.0:8991: bind: address already in use
```

**해결**:
```bash
# 1. 포트 사용 프로세스 확인
sudo lsof -i :8991

# 2. 기존 컨테이너 중지
docker stop erp-backend-blue

# 3. 또는 강제 제거
docker rm -f erp-backend-blue
```

### 9.4 nginx 트래픽 전환 문제

#### 문제: nginx upstream 변경이 적용 안됨

```
# upstream을 8993으로 변경했는데도 8991로 요청이 감
```

**해결**:
```bash
# 1. nginx 설정 재확인
sudo grep "active-backend" /etc/nginx/sites-enabled/erp.conf

# 2. nginx 프로세스 완전 재시작
sudo nginx -s stop
sudo nginx

# 3. nginx 캐시 삭제
sudo rm -rf /var/cache/nginx/*
sudo nginx -s reload
```

### 9.5 데이터베이스 연결 문제

#### 문제: Database connection failed

```
Caused by: org.postgresql.util.PSQLException: Connection refused
```

**해결**:
```bash
# 1. PostgreSQL 서비스 확인
ssh naudhizfehu@fehu.kr
sudo systemctl status postgresql

# 2. 데이터베이스 접속 테스트
psql -h fehu.kr -U cursor_erp_system -d cursor_erp_system

# 3. 방화벽 확인
sudo ufw status | grep 5432

# 4. PostgreSQL 설정 확인
sudo cat /etc/postgresql/15/main/pg_hba.conf
# host cursor_erp_system cursor_erp_system 0.0.0.0/0 md5 확인
```

---

## 10. FAQ

### Q1: 배포는 얼마나 자주 해야 하나요?

**A**: 일반적인 배포 주기:
- **핫픽스**: 즉시 (심각한 버그 수정)
- **기능 추가**: 주 1-2회
- **정기 배포**: 금요일 오전 (주말 모니터링 가능)

### Q2: 배포 시간대는 언제가 좋나요?

**A**: 권장 배포 시간:
- ✅ **오전 10-11시**: 사용자 적고, 오후 동안 모니터링 가능
- ✅ **금요일 오전**: 주말 동안 안정화
- ❌ **월요일**: 업무 시작 시간 회피
- ❌ **야간**: 즉시 대응 어려움

### Q3: Blue-Green 배포 후 Blue 환경은 언제 정리하나요?

**A**:
- **최소 1주일** 유지 권장
- 사용자 피드백 수집 완료 후
- 로그 분석으로 이상 없음 확인 후
- 디스크 공간이 부족한 경우 우선 정리

### Q4: GitLab CI/CD 파이프라인이 실패하면 어떻게 하나요?

**A**:
```bash
# 1. 파이프라인 로그 확인
# GitLab UI → Pipelines → 실패한 Job 클릭

# 2. 로컬에서 재현
npm run lint  # frontend-lint 실패 시
npm run test  # frontend-test 실패 시
mvn test      # backend-test 실패 시

# 3. 수정 후 재푸시
git add .
git commit -m "fix: CI/CD 오류 수정"
git push origin develop
```

### Q5: 배포 중 문제가 생기면 어떻게 하나요?

**A**:
1. **즉시 롤백**: `./scripts/blue-green-rollback.sh`
2. **문제 분석**: 로그 확인, Health Check 테스트
3. **수정 후 재배포**: 로컬에서 충분히 테스트 후 재배포

### Q6: 여러 개발자가 동시에 배포하면 어떻게 되나요?

**A**:
- Blue-Green 배포는 **한 번에 한 명만** 실행해야 함
- 배포 전 팀에 공지 (Slack 등)
- GitLab Lock 기능 활용 권장

### Q7: 데이터베이스 마이그레이션은 어떻게 하나요?

**A**:
- 현재는 수동 마이그레이션
- 향후 Flyway 도입 예정
- **중요**: 스키마 변경 시 Blue와 Green 모두 호환되도록 설계

### Q8: 배포 실패 시 자동 롤백되나요?

**A**:
- **수동 승인 필요**: Health Check 통과해도 사용자가 'yes' 입력해야 전환
- 배포 실패 시 Green 환경만 중지, Blue는 그대로 유지
- 자동 롤백은 현재 미지원 (수동으로 롤백 스크립트 실행)

### Q9: 로컬 개발과 프로덕션 환경 차이는?

**A**:

| 항목 | 로컬 개발 | 프로덕션 |
|------|----------|---------|
| **Hot Reload** | ✅ 지원 | ❌ 미지원 |
| **포트** | 3000, 8080 | 8990-8993 |
| **Database** | 로컬 PostgreSQL | Synology PostgreSQL |
| **nginx** | 없음 | 있음 (트래픽 라우팅) |
| **환경 변수** | `.env.development` | `.env` (Synology) |

### Q10: Blue-Green 배포 없이 일반 배포는 안되나요?

**A**:
- 가능하지만 **서비스 중단** 발생 (30-60초)
- Blue-Green 없이 배포하려면:

```bash
# Synology에서
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d
# ⚠️ 이 방법은 서비스 중단 발생!
```

---

## 📚 추가 참고 문서

- **Blue-Green 배포 상세 가이드**: `docs/BLUEGREEN_DEPLOYMENT_GUIDE.md`
- **GitLab CI/CD 가이드**: `guideDocs/ci_cd/01-gitlab-cicd-overview.md`
- **트러블슈팅 가이드**: `todoDocs/06-troubleshooting.md`
- **프로젝트 현황**: `todoDocs/00-project-status.md`

---

## 🆘 긴급 문제 발생 시

1. **즉시 롤백**: `./scripts/blue-green-rollback.sh`
2. **서비스 상태 확인**: `docker ps`, `curl http://erp.fehu.kr/health`
3. **로그 확인**: `docker logs <container-name> -f`
4. **팀에 공지**: Slack 또는 이메일
5. **문제 분석**: 로그 수집, 재현 시도
6. **수정 후 재배포**: 충분한 테스트 후

---

**문서 버전**: 1.0
**최종 업데이트**: 2025-12-23
**작성자**: Claude Code
**검토자**: 개발팀
