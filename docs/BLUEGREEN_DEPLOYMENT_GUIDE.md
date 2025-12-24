# Blue-Green 무중단 배포 가이드

**버전**: 1.0
**작성일**: 2025-12-23
**대상**: Synology NAS + Docker + nginx 환경

---

## 목차

1. [개요](#개요)
2. [아키텍처](#아키텍처)
3. [초기 설정](#초기-설정)
4. [배포 프로세스](#배포-프로세스)
5. [롤백 프로세스](#롤백-프로세스)
6. [트러블슈팅](#트러블슈팅)
7. [모니터링](#모니터링)

---

## 개요

### Blue-Green 배포란?

두 개의 동일한 프로덕션 환경(Blue, Green)을 유지하면서 **서비스 중단 없이** 새 버전을 배포하는 전략입니다.

### 핵심 장점

- ✅ **서비스 중단 0초**: 배포 중에도 사용자 서비스 계속
- ✅ **즉시 롤백**: 문제 발생 시 5초 이내 이전 버전으로 복구
- ✅ **안전한 배포**: Health Check 통과 후에만 트래픽 전환
- ✅ **사용자 승인**: 자동화되었지만 수동 승인 포함

### 현재 vs Blue-Green 비교

| 항목 | 기존 배포 | Blue-Green 배포 |
|------|----------|----------------|
| 서비스 중단 | 30-60초 | **0초** |
| 롤백 시간 | 1-2분 | **5초** |
| 배포 안전성 | 검증 없음 | Health Check + 승인 |
| 메모리 사용 | 1GB | 2GB (배포 중) |
| 디스크 사용 | 1GB | 3GB |

---

## 아키텍처

### 구조 다이어그램

```
사용자 트래픽
    ↓
nginx (Synology NAS)
    ↓
┌─────────────────┴──────────────────┐
↓                                    ↓
Blue 환경                        Green 환경
- backend-blue:8991              - backend-green:8993
- frontend-blue:8990             - frontend-green:8992
    ↓                                ↓
    └──────────────┬─────────────────┘
                   ↓
          공유 PostgreSQL
          (fehu.kr:5432)
```

### 트래픽 전환 메커니즘

1. **Blue 환경 활성** → 사용자는 Blue 접속
2. **Green 환경 배포** → Green에 새 버전 배포 (사용자 영향 없음)
3. **Health Check** → Green 정상 작동 확인
4. **nginx 전환** → `active-backend` 포트만 변경 (8991 → 8993)
5. **즉시 적용** → `nginx -s reload` (5초 이내)
6. **Blue 유지** → 롤백 대비 Blue 컨테이너 유지

---

## 초기 설정

### 1. 로컬 환경 설정

#### 1-1. 환경 변수 파일 생성

```bash
cd /Users/leetaehoon/Desktop/project/cursor-erp-system
cp .env.bluegreen.example .env.bluegreen
vim .env.bluegreen
```

**필수 설정 값**:
```bash
SYNOLOGY_HOST=fehu.kr
SYNOLOGY_USER=naudhizfehu
SYNOLOGY_PORT=22
SSH_KEY=$HOME/.ssh/id_rsa
DEPLOY_DIR=/var/services/homes/naudhizfehu/erp-system
```

#### 1-2. 배포 스크립트 권한 부여

```bash
chmod +x scripts/blue-green-deploy.sh
chmod +x scripts/blue-green-rollback.sh
chmod +x scripts/deploy-to-synology-bluegreen.sh
```

#### 1-3. SSH 키 설정 확인

```bash
# SSH 키 존재 확인
ls -la ~/.ssh/id_rsa

# SSH 접속 테스트
ssh naudhizfehu@fehu.kr "echo 'Connection successful'"
```

### 2. Synology NAS 설정

#### 2-1. nginx 설정 업데이트

```bash
# Synology에 SSH 접속
ssh naudhizfehu@fehu.kr

# nginx 설정 디렉토리로 이동
cd /etc/nginx/sites-enabled

# 기존 설정 백업
sudo cp erp.conf erp.conf.backup.$(date +%Y%m%d)

# 새 설정 적용 (로컬에서 전송 또는 수동 편집)
sudo vim erp.conf
```

**설정 파일 내용**: `scripts/nginx-bluegreen.conf.template` 참고

#### 2-2. nginx 설정 검증 및 적용

```bash
# 설정 파일 문법 검증
sudo nginx -t

# 정상이면 적용
sudo nginx -s reload
```

#### 2-3. 배포 디렉토리 생성

```bash
# Synology에서 실행
mkdir -p /var/services/homes/naudhizfehu/erp-system/{scripts,images}
```

### 3. 초기 Blue 환경 배포

#### 3-1. 로컬에서 초기 이미지 빌드 및 전송

```bash
# 로컬에서 실행
cd /Users/leetaehoon/Desktop/project/cursor-erp-system

# 초기 배포 (Blue 환경으로)
./scripts/deploy-to-synology-bluegreen.sh
```

#### 3-2. 초기 배포 확인

```bash
# Synology에서 확인
ssh naudhizfehu@fehu.kr
cd /var/services/homes/naudhizfehu/erp-system

# 컨테이너 실행 확인
sudo docker ps

# Health Check
curl http://localhost:8991/actuator/health
curl http://localhost/health
```

---

## 배포 프로세스

### 전체 배포 절차

```bash
# 로컬에서 실행
cd /Users/leetaehoon/Desktop/project/cursor-erp-system
./scripts/deploy-to-synology-bluegreen.sh
```

### 상세 단계별 설명

#### Step 1: 배포 전 체크리스트

**자동 실행 항목**:
- Git 상태 확인 (커밋되지 않은 변경사항 경고)
- 현재 브랜치 확인
- 프론트엔드 린트 검사
- 프론트엔드 타입 체크

**수동 확인 항목**:
```bash
# 린트 수동 실행
cd frontend
npm run lint

# 타입 체크 수동 실행
npm run type-check

# Backend 빌드 테스트
cd ../backend
./mvnw clean package -DskipTests
```

#### Step 2: Docker 이미지 빌드

**프론트엔드**:
```bash
cd frontend
docker build -f Dockerfile.prod -t cursor-erp-frontend:latest .
```

**백엔드**:
```bash
cd backend
docker build -t cursor-erp-backend:latest .
```

**버전 태그 추가** (선택):
```bash
export VERSION=1.0.0
docker tag cursor-erp-backend:latest cursor-erp-backend:$VERSION
docker tag cursor-erp-frontend:latest cursor-erp-frontend:$VERSION
```

#### Step 3: Synology로 이미지 전송

**이미지 저장**:
```bash
docker save cursor-erp-backend:latest | gzip > /tmp/backend.tar.gz
docker save cursor-erp-frontend:latest | gzip > /tmp/frontend.tar.gz
```

**Synology로 전송**:
```bash
rsync -avz --progress /tmp/backend.tar.gz naudhizfehu@fehu.kr:/var/services/homes/naudhizfehu/erp-system/images/
rsync -avz --progress /tmp/frontend.tar.gz naudhizfehu@fehu.kr:/var/services/homes/naudhizfehu/erp-system/images/
```

**Synology에서 로드**:
```bash
ssh naudhizfehu@fehu.kr
cd /var/services/homes/naudhizfehu/erp-system/images
sudo docker load < backend.tar.gz
sudo docker load < frontend.tar.gz
```

#### Step 4: Blue-Green 배포 실행

**Synology에서 실행**:
```bash
cd /var/services/homes/naudhizfehu/erp-system
./scripts/blue-green-deploy.sh
```

**배포 스크립트 자동 실행 순서**:

1. **현재 활성 환경 감지**
   ```
   현재 활성 환경: [blue]
   배포 대상 환경: [green]
   ```

2. **Docker 이미지 확인**
   - backend, frontend 이미지 존재 확인

3. **배포 확인 프롬프트**
   ```
   [green] 환경에 새 버전을 배포하시겠습니까?
   계속 진행하시겠습니까? (yes 입력):
   ```
   → **yes** 입력

4. **Green 환경에 컨테이너 배포**
   ```bash
   sudo docker-compose -f docker-compose.prod.yml --profile green up -d
   ```

5. **Health Check (최대 60초)**
   ```
   [green] 환경 Health Check 중...
   .....
   [green] 환경 정상 (15초 경과)
   ```

6. **트래픽 전환 확인 프롬프트**
   ```
   트래픽을 [green] 환경으로 전환하시겠습니까?
   트래픽을 전환하시겠습니까? (yes 입력):
   ```
   → **yes** 입력

7. **nginx upstream 전환**
   ```bash
   # active-backend: 8991 → 8993
   # active-frontend: 8990 → 8992
   sudo nginx -s reload
   ```

8. **전환 검증**
   ```bash
   curl http://localhost/health
   ```

9. **완료 메시지**
   ```
   무중단 배포 완료!
   현재 활성 환경: [green]
   이전 환경: [blue] (롤백 대비 유지)
   ```

#### Step 5: 배포 후 확인

**서비스 정상 작동 확인**:
```bash
# Health Check
curl http://erp.fehu.kr/health

# API 테스트
curl http://erp.fehu.kr/api/health

# 프론트엔드 접속
open http://erp.fehu.kr
```

**로그 확인**:
```bash
# Synology에서
cd /var/services/homes/naudhizfehu/erp-system

# Green 백엔드 로그
sudo docker-compose -f docker-compose.prod.yml logs -f backend-green

# Green 프론트엔드 로그
sudo docker-compose -f docker-compose.prod.yml logs -f frontend-green

# nginx 로그
sudo tail -f /var/log/nginx/erp_access.log
sudo tail -f /var/log/nginx/erp_error.log
```

**컨테이너 상태 확인**:
```bash
# 실행 중인 컨테이너
sudo docker ps

# 메모리 사용량
sudo docker stats --no-stream

# 컨테이너 Health 상태
sudo docker inspect erp-backend-green | grep -A 10 Health
```

#### Step 6: 이전 환경 정리 (선택)

**안정화 확인 후** (1-2일 후):

```bash
# Blue 환경 중지
cd /var/services/homes/naudhizfehu/erp-system
sudo docker-compose -f docker-compose.prod.yml stop backend-blue frontend-blue

# 또는 완전 제거
sudo docker-compose -f docker-compose.prod.yml down backend-blue frontend-blue
```

---

## 롤백 프로세스

### 즉시 롤백 (5초 이내)

#### 실행 방법

```bash
# Synology에서 실행
ssh naudhizfehu@fehu.kr
cd /var/services/homes/naudhizfehu/erp-system
./scripts/blue-green-rollback.sh
```

#### 롤백 스크립트 자동 실행 순서

1. **현재 활성 환경 감지**
   ```
   현재 활성 환경: [green]
   롤백 대상 환경: [blue]
   ```

2. **롤백 대상 환경 실행 확인**
   ```
   [blue] 환경 상태 확인 중...
   [blue] 환경이 실행 중입니다
   ```

3. **롤백 대상 환경 Health Check**
   ```
   [blue] 환경 Health Check 중...
   [blue] 환경 정상
   ```

4. **롤백 최종 확인**
   ```
   [blue] 환경으로 즉시 롤백하시겠습니까?
   정말 롤백하시겠습니까? (yes 입력):
   ```
   → **yes** 입력

5. **nginx upstream 롤백**
   ```bash
   # active-backend: 8993 → 8991
   # active-frontend: 8992 → 8990
   sudo nginx -s reload
   ```

6. **롤백 검증**
   ```bash
   curl http://localhost/health
   ```

7. **완료 메시지**
   ```
   즉시 롤백 완료!
   롤백 시간: 4초
   현재 활성 환경: [blue]
   ```

#### 롤백 후 확인

```bash
# 서비스 정상 작동 확인
curl http://erp.fehu.kr/health

# Green 환경 로그 확인 (문제 원인 파악)
sudo docker-compose -f docker-compose.prod.yml logs backend-green

# Green 환경 중지 (선택)
sudo docker-compose -f docker-compose.prod.yml stop backend-green frontend-green
```

### 수동 롤백 (nginx 설정 직접 변경)

**긴급 상황 시**:

```bash
# Synology에서 실행
ssh naudhizfehu@fehu.kr

# nginx 설정 파일 직접 편집
sudo vim /etc/nginx/sites-enabled/erp.conf

# active-backend, active-frontend 포트 변경
# Green (8993, 8992) → Blue (8991, 8990)

# 설정 테스트
sudo nginx -t

# 적용
sudo nginx -s reload
```

---

## 트러블슈팅

### 문제 1: Health Check 실패

**증상**:
```
[green] 환경 Health Check 실패 (60초 타임아웃)
```

**원인 파악**:
```bash
# 컨테이너 로그 확인
sudo docker-compose -f docker-compose.prod.yml logs backend-green

# 컨테이너 상태 확인
sudo docker ps -a | grep green

# Health Check URL 직접 테스트
curl http://localhost:8993/actuator/health
```

**해결 방법**:
1. 컨테이너 재시작
   ```bash
   sudo docker-compose -f docker-compose.prod.yml restart backend-green
   ```

2. 이미지 재빌드 및 재배포
   ```bash
   # 로컬에서
   ./scripts/deploy-to-synology-bluegreen.sh
   ```

3. 롤백
   ```bash
   ./scripts/blue-green-rollback.sh
   ```

### 문제 2: nginx 전환 실패

**증상**:
```
nginx 설정 검증 실패
```

**원인 파악**:
```bash
# nginx 설정 테스트
sudo nginx -t

# 에러 로그 확인
sudo tail -100 /var/log/nginx/error.log
```

**해결 방법**:
1. 백업 복원
   ```bash
   sudo cp /etc/nginx/sites-enabled/erp.conf.backup.YYYYMMDD /etc/nginx/sites-enabled/erp.conf
   sudo nginx -t
   sudo nginx -s reload
   ```

2. 수동 설정 수정
   ```bash
   sudo vim /etc/nginx/sites-enabled/erp.conf
   sudo nginx -t
   sudo nginx -s reload
   ```

### 문제 3: 이미지 로드 실패

**증상**:
```
Error loading image from backend.tar.gz
```

**원인**: 디스크 공간 부족 또는 파일 손상

**해결 방법**:
1. 디스크 공간 확인
   ```bash
   df -h
   ```

2. 불필요한 이미지 삭제
   ```bash
   sudo docker image prune -a
   ```

3. 이미지 재전송
   ```bash
   # 로컬에서
   rsync -avz --progress /tmp/backend.tar.gz naudhizfehu@fehu.kr:/var/services/homes/naudhizfehu/erp-system/images/
   ```

### 문제 4: 두 환경 모두 실패

**극단적 상황 대응**:

```bash
# 1. 기존 컨테이너 모두 중지
sudo docker-compose -f docker-compose.prod.yml down

# 2. 최신 안정 이미지 확인
sudo docker images | grep cursor-erp

# 3. Blue 환경 강제 재시작
sudo docker-compose -f docker-compose.prod.yml up -d backend-blue frontend-blue

# 4. nginx를 Blue로 전환
sudo sed -i '/upstream active-backend/,/}/s/server localhost:[0-9]\+/server localhost:8991/' /etc/nginx/sites-enabled/erp.conf
sudo sed -i '/upstream active-frontend/,/}/s/server localhost:[0-9]\+/server localhost:8990/' /etc/nginx/sites-enabled/erp.conf
sudo nginx -s reload

# 5. 서비스 확인
curl http://localhost/health
```

---

## 모니터링

### 실시간 모니터링

#### 컨테이너 상태

```bash
# 실행 중인 컨테이너
watch -n 5 'sudo docker ps'

# 리소스 사용량
watch -n 5 'sudo docker stats --no-stream'

# Health 상태
watch -n 10 'curl -s http://localhost/health | jq'
```

#### 로그 모니터링

```bash
# 실시간 로그
sudo docker-compose -f docker-compose.prod.yml logs -f backend-blue backend-green

# nginx 액세스 로그
sudo tail -f /var/log/nginx/erp_access.log

# nginx 에러 로그
sudo tail -f /var/log/nginx/erp_error.log
```

### 성능 모니터링

#### CPU/메모리

```bash
# Synology 리소스 모니터
# Resource Monitor 앱 사용

# 또는 CLI
top
htop
```

#### 네트워크

```bash
# 활성 연결
sudo netstat -an | grep -E '(8991|8993|8990|8992)'

# 포트 리스닝 확인
sudo ss -tulpn | grep -E '(8991|8993|8990|8992)'
```

### Health Check 스크립트

```bash
#!/bin/bash
# health-monitor.sh

while true; do
    echo "=== $(date) ==="

    # Blue 환경
    echo -n "Blue Backend: "
    curl -sf http://localhost:8991/actuator/health > /dev/null && echo "✓" || echo "✗"

    echo -n "Blue Frontend: "
    curl -sf http://localhost:8990/ > /dev/null && echo "✓" || echo "✗"

    # Green 환경
    echo -n "Green Backend: "
    curl -sf http://localhost:8993/actuator/health > /dev/null && echo "✓" || echo "✗"

    echo -n "Green Frontend: "
    curl -sf http://localhost:8992/ > /dev/null && echo "✓" || echo "✗"

    # 프록시
    echo -n "Proxy: "
    curl -sf http://localhost/health > /dev/null && echo "✓" || echo "✗"

    echo ""
    sleep 30
done
```

---

## 주의사항

### 데이터베이스 스키마 변경

**중요**: Blue-Green 배포는 애플리케이션 코드만 전환합니다. 데이터베이스는 공유됩니다.

**안전한 마이그레이션**:
```sql
-- ✅ 안전: 컬럼 추가 (nullable 또는 default 값)
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
ALTER TABLE users ADD COLUMN status VARCHAR(10) DEFAULT 'active';

-- ❌ 위험: 컬럼 삭제 (롤백 불가)
ALTER TABLE users DROP COLUMN email;

-- ✅ 안전: 컬럼 이름 변경 (2단계)
-- 1단계: 새 컬럼 추가, 데이터 복사
ALTER TABLE users ADD COLUMN new_email VARCHAR(255);
UPDATE users SET new_email = email;

-- 2단계: 배포 완료 후 이전 컬럼 삭제 (별도 작업)
-- ALTER TABLE users DROP COLUMN email;
```

**마이그레이션 순서**:
1. 스키마 변경 (배포 **전**)
2. 애플리케이션 배포 (Blue-Green)
3. 검증 및 안정화
4. 이전 컬럼/테이블 정리 (선택)

### WebSocket 사용 시

현재 ERP 시스템은 JWT 기반으로 문제없지만, 향후 WebSocket 추가 시:

- 트래픽 전환 시 연결 끊김 발생
- 클라이언트 자동 재연결 로직 필수
- 또는 sticky session 설정 필요

### 리소스 관리

**배포 중 메모리 사용량**:
- Blue + Green 동시 실행: ~2GB
- Synology NAS 메모리 충분한지 확인

**디스크 공간**:
- Docker 이미지 여러 버전 유지 시 공간 필요
- 주기적으로 불필요한 이미지 정리:
  ```bash
  sudo docker image prune -a
  ```

---

## 체크리스트

### 배포 전 체크리스트

- [ ] Git 상태 깨끗함 (커밋 완료)
- [ ] 프론트엔드 린트 통과
- [ ] 프론트엔드 타입 체크 통과
- [ ] 백엔드 빌드 성공
- [ ] 데이터베이스 마이그레이션 완료 (필요 시)
- [ ] SSH 접속 가능
- [ ] Synology 디스크 공간 충분
- [ ] 배포 스크립트 권한 확인

### 배포 후 체크리스트

- [ ] Health Check 통과
- [ ] 프론트엔드 접속 정상
- [ ] API 응답 정상
- [ ] 로그인 기능 정상
- [ ] 주요 기능 테스트 완료
- [ ] 에러 로그 없음
- [ ] 이전 환경 실행 중 (롤백 대비)

### 롤백 체크리스트

- [ ] 롤백 대상 환경 실행 중
- [ ] 롤백 대상 환경 Health Check 통과
- [ ] 롤백 사유 문서화
- [ ] 롤백 후 서비스 정상 확인
- [ ] 문제 원인 파악 및 수정 계획

---

## 참고 자료

### 파일 위치

- Docker Compose: `docker-compose.prod.yml`
- 배포 스크립트: `scripts/blue-green-deploy.sh`
- 롤백 스크립트: `scripts/blue-green-rollback.sh`
- 로컬 배포: `scripts/deploy-to-synology-bluegreen.sh`
- nginx 템플릿: `scripts/nginx-bluegreen.conf.template`
- 환경 변수: `.env.bluegreen.example`

### 포트 할당

| 환경 | 백엔드 | 프론트엔드 |
|------|--------|-----------|
| Blue | 8991 | 8990 |
| Green | 8993 | 8992 |
| 공용 nginx | 80 | - |

### 주요 명령어

```bash
# 배포
./scripts/deploy-to-synology-bluegreen.sh

# 롤백
ssh naudhizfehu@fehu.kr
cd /var/services/homes/naudhizfehu/erp-system
./scripts/blue-green-rollback.sh

# 상태 확인
sudo docker ps
sudo docker-compose -f docker-compose.prod.yml logs -f

# nginx 확인
sudo nginx -t
sudo nginx -s reload
```

---

**마지막 업데이트**: 2025-12-23
**작성자**: ERP 시스템 개발팀
**버전**: 1.0
