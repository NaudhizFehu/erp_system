# Cursor ERP System - TODO List

**Last Updated**: 2025-11-21
**Current Focus**: 자동 배포 시스템 개선

---

## 🔴 즉시 수행 (이번 주)

### 1. 테스트 자동화 활성화 ⏳
**우선순위**: 최고
**예상 시간**: 1-2시간
**담당**: Backend + Frontend

#### Backend 테스트
- [ ] `.github/workflows/backend-ci.yml` 파일 열기
- [ ] `-DskipTests` 플래그 제거
- [ ] Maven 테스트 실행 확인
- [ ] SonarQube 품질 게이트 추가 (선택)
- [ ] 테스트 커버리지 리포트 생성

**파일**: `.github/workflows/backend-ci.yml`
```yaml
# 수정 필요:
# 현재: mvn clean package -DskipTests
# 변경: mvn clean package
```

#### Frontend 테스트
- [ ] `frontend/package.json`에 Vitest 추가
- [ ] `vitest.config.ts` 설정 파일 생성
- [ ] 샘플 테스트 작성 (최소 1개)
- [ ] `.github/workflows/frontend-ci.yml`에 테스트 단계 추가
- [ ] 테스트 커버리지 설정

**파일**:
- `frontend/package.json`
- `frontend/vitest.config.ts` (신규)
- `.github/workflows/frontend-ci.yml`

---

### 2. 데이터베이스 백업 자동화 ⏳
**우선순위**: 최고
**예상 시간**: 2-3시간
**담당**: DevOps

#### 백업 스크립트 생성
- [ ] `scripts/backup-database.sh` 스크립트 작성
- [ ] PostgreSQL `pg_dump` 명령 사용
- [ ] 날짜별 백업 파일 생성 (압축)
- [ ] 오래된 백업 자동 삭제 (7일 이상)
- [ ] 백업 성공/실패 로그 기록

**백업 위치**: `/var/services/homes/naudhizfehu/erp-system/backups/`

#### 복원 스크립트 생성
- [ ] `scripts/restore-database.sh` 스크립트 작성
- [ ] 백업 파일 선택 기능
- [ ] 복원 전 현재 DB 백업
- [ ] 복원 후 검증

#### Cron 작업 설정
- [ ] Synology NAS에 SSH 접속
- [ ] cron 작업 추가 (매일 새벽 2시)
- [ ] 백업 테스트 실행
- [ ] 복원 테스트 실행

**Cron 예시**:
```bash
0 2 * * * /path/to/backup-database.sh >> /var/log/db-backup.log 2>&1
```

---

### 3. 롤백 절차 문서화 및 스크립트 작성 ⏳
**우선순위**: 높음
**예상 시간**: 1-2시간
**담당**: DevOps

#### 롤백 스크립트
- [ ] `scripts/rollback.sh` 스크립트 작성
- [ ] 이전 버전 Docker 이미지 로드
- [ ] 데이터베이스 마이그레이션 체크
- [ ] 서비스 재시작
- [ ] 헬스체크 수행

#### 버전 관리 개선
- [ ] Git 태그 전략 문서화
- [ ] 버전 네이밍 컨벤션 정의 (예: v1.0.0)
- [ ] 각 배포마다 태그 자동 생성 확인
- [ ] 이전 Docker 이미지 보관 정책 (최근 5개 유지)

#### 문서 작성
- [ ] `docs/ROLLBACK.md` 문서 생성
- [ ] 롤백 절차 단계별 설명
- [ ] 데이터베이스 롤백 시나리오 추가
- [ ] 긴급 상황 대응 가이드

---

## 📋 단기 (2주 내)

### 4. Blue-Green 배포 구현 ⏳
**우선순위**: 중간
**예상 시간**: 4-6시간
**담당**: DevOps

#### 환경 구성
- [ ] `docker-compose.blue.yml` 생성 (포트 8990, 8991)
- [ ] `docker-compose.green.yml` 생성 (포트 8992, 8993)
- [ ] Nginx upstream 설정 추가
- [ ] 환경 전환 스크립트 작성

#### Nginx 설정
- [ ] `nginx/conf.d/upstream.conf` 생성
- [ ] Blue/Green 환경 upstream 정의
- [ ] 헬스체크 엔드포인트 설정
- [ ] 환경 전환 스크립트 (nginx reload)

#### 배포 스크립트 수정
- [ ] `scripts/deploy-blue-green.sh` 작성
- [ ] 현재 활성 환경 감지
- [ ] 비활성 환경에 배포
- [ ] 헬스체크 후 트래픽 전환
- [ ] 롤백 자동화 (헬스체크 실패 시)

**파일**:
- `docker-compose.blue.yml` (신규)
- `docker-compose.green.yml` (신규)
- `nginx/conf.d/upstream.conf` (신규)
- `scripts/deploy-blue-green.sh` (신규)

---

### 5. 컨테이너 헬스체크 추가 ⏳
**우선순위**: 높음
**예상 시간**: 1시간
**담당**: Backend + DevOps

#### Backend 헬스체크
- [ ] `docker-compose.prod.yml`에 healthcheck 섹션 추가
- [ ] Spring Boot Actuator `/actuator/health` 사용
- [ ] interval, timeout, retries 설정
- [ ] start_period 적절히 설정 (40초)

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8991/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 40s
```

#### Frontend 헬스체크
- [ ] Nginx health check endpoint 추가
- [ ] `docker-compose.prod.yml`에 healthcheck 추가
- [ ] 헬스체크 성공 조건 확인

#### 의존성 관리
- [ ] Frontend가 Backend 헬스체크 통과 후 시작되도록 설정
- [ ] `depends_on` 조건 추가

**파일**: `docker-compose.prod.yml`

---

### 6. Slack 알림 통합 ⏳
**우선순위**: 중간
**예상 시간**: 2시간
**담당**: DevOps

#### Slack 설정
- [ ] Slack Incoming Webhook 생성
- [ ] GitHub Secrets에 SLACK_WEBHOOK_URL 추가
- [ ] 알림 채널 생성 (#erp-deployments)

#### GitHub Actions 통합
- [ ] `.github/workflows/deploy-production.yml` 수정
- [ ] 배포 시작 알림 추가
- [ ] 배포 성공 알림 추가
- [ ] 배포 실패 알림 추가
- [ ] 알림 메시지 포맷 개선 (버전, 커밋, 배포자 정보)

#### 배포 스크립트 통합
- [ ] `scripts/deploy-to-synology.sh`에 Slack 알림 추가
- [ ] 성공/실패 시 알림 전송
- [ ] 함수로 모듈화

**참고**:
```yaml
- name: Slack Notification
  uses: slackapi/slack-github-action@v1
  with:
    webhook-url: ${{ secrets.SLACK_WEBHOOK_URL }}
    payload: |
      {
        "text": "Deployment Status",
        "blocks": [...]
      }
```

---

## 📅 중기 (1개월 내)

### 7. 데이터베이스 마이그레이션 자동화 (Flyway) ⏳
**우선순위**: 높음
**예상 시간**: 4-6시간
**담당**: Backend

#### Flyway 설정
- [ ] `pom.xml`에 Flyway 의존성 추가
- [ ] `application.yml`에 Flyway 설정
- [ ] `src/main/resources/db/migration/` 디렉토리 생성
- [ ] 기존 스키마를 V1__Initial_schema.sql로 변환
- [ ] Baseline 마이그레이션 실행

#### 마이그레이션 전략
- [ ] 버전 네이밍 규칙 정의 (V{version}__{description}.sql)
- [ ] 롤백 스크립트 전략 수립
- [ ] 마이그레이션 테스트 절차 문서화
- [ ] Production 적용 계획 수립

#### CI/CD 통합
- [ ] 배포 전 마이그레이션 자동 실행
- [ ] 마이그레이션 실패 시 배포 중단
- [ ] 마이그레이션 로그 보관

**파일**:
- `backend/pom.xml`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/db/migration/V1__Initial_schema.sql` (신규)

---

### 8. 모니터링 시스템 구축 ⏳
**우선순위**: 중간
**예상 시간**: 8-10시간
**담당**: DevOps

#### 로그 수집
- [ ] ELK Stack 또는 DataDog 선택
- [ ] Backend 로그 수집 설정
- [ ] Frontend 액세스 로그 수집
- [ ] 에러 로그 필터링 및 알림

#### APM (Application Performance Monitoring)
- [ ] Spring Boot Actuator 메트릭 활성화
- [ ] Prometheus + Grafana 설정 (또는 DataDog APM)
- [ ] 대시보드 생성 (응답 시간, 에러율, 처리량)
- [ ] 알림 임계값 설정

#### 업타임 모니터링
- [ ] UptimeRobot 또는 Pingdom 설정
- [ ] 주요 엔드포인트 모니터링
- [ ] 다운타임 알림 (이메일, Slack)
- [ ] SLA 목표 설정 (99.9% 가용성)

**도구 선택**:
- 옵션 1: ELK Stack + Prometheus + Grafana (오픈소스, 자체 호스팅)
- 옵션 2: DataDog (유료, 관리형)
- 옵션 3: New Relic (유료, 관리형)

---

### 9. 보안 강화 ⏳
**우선순위**: 높음
**예상 시간**: 6-8시간
**담당**: DevOps + Security

#### SSL/TLS 설정
- [ ] Let's Encrypt 인증서 발급
- [ ] Nginx SSL 설정 추가
- [ ] HTTP → HTTPS 리다이렉트
- [ ] SSL Labs A+ 등급 달성

#### Secrets 관리
- [ ] AWS Secrets Manager 또는 GitHub Secrets 사용
- [ ] `.env.production` 파일을 Secrets로 이전
- [ ] 배포 스크립트에서 Secrets 주입
- [ ] 시크릿 로테이션 정책 수립

#### 보안 스캔
- [ ] GitHub CodeQL 활성화 (SAST)
- [ ] Snyk 또는 Dependabot 활성화 (의존성 스캔)
- [ ] OWASP ZAP 또는 Burp Suite (DAST)
- [ ] 정기 보안 감사 스케줄

#### 컨테이너 보안
- [ ] Docker 이미지 취약점 스캔 (Trivy)
- [ ] 비-루트 사용자로 컨테이너 실행
- [ ] 읽기 전용 파일 시스템 적용
- [ ] 리소스 제한 설정 (CPU, 메모리)

**파일**:
- `nginx/ssl.conf` (신규)
- `docker-compose.prod.yml` (보안 설정 추가)
- `.github/workflows/security-scan.yml` (신규)

---

## 🎯 장기 (분기별)

### 10. Canary 배포 구현 ⏳
**우선순위**: 낮음
**예상 시간**: 8-10시간
**담당**: DevOps

- [ ] Nginx 기반 트래픽 분산 (10% → 50% → 100%)
- [ ] 에러율 모니터링 자동화
- [ ] 자동 롤백 조건 설정
- [ ] Canary 배포 워크플로우 작성

---

### 11. 로드 테스팅 자동화 ⏳
**우선순위**: 낮음
**예상 시간**: 6-8시간
**담당**: QA + DevOps

- [ ] k6 또는 JMeter 스크립트 작성
- [ ] 주요 API 엔드포인트 테스트 시나리오
- [ ] CI/CD 파이프라인에 로드 테스트 통합
- [ ] 성능 베이스라인 수립
- [ ] 성능 저하 시 배포 차단

---

### 12. 멀티 리전 지원 ⏳
**우선순위**: 낮음
**예상 시간**: 20-30시간
**담당**: Architecture + DevOps

- [ ] 두 번째 데이터베이스 리전 구축
- [ ] 데이터베이스 복제 설정
- [ ] 글로벌 로드 밸런서 (CDN)
- [ ] 장애 조치(Failover) 자동화
- [ ] 리전별 모니터링

---

## 📝 회계 모듈 완료 (참고)

### Phase 1: 계정과목 관리 (65% 완료)
**현재 차단 요소**:

#### AccountList 모달 통합 ⏳
- [ ] `AccountList.tsx` 파일 열기
- [ ] 모달 상태 관리 추가 (isFormOpen, formMode, selectedAccount)
- [ ] AccountForm 컴포넌트 import
- [ ] Dialog/Modal wrapper 추가
- [ ] "계정과목 추가" 버튼에 모달 열기 연결
- [ ] 행 "수정" 버튼에 모달 열기 연결
- [ ] 폼 제출 후 목록 새로고침

**예상 시간**: 30분

---

#### AccountDetail 라우팅 추가 ⏳
- [ ] `App.tsx` 파일 열기
- [ ] AccountDetail 컴포넌트 import
- [ ] 라우트 추가: `/accounting/accounts/:id`
- [ ] 권한 가드 추가 (ADMIN)
- [ ] AccountList에서 상세 페이지로 네비게이션 테스트

**예상 시간**: 15분

---

#### 행 액션 버튼 연결 ⏳
- [ ] "보기" 버튼 → AccountDetail 페이지로 이동
- [ ] "수정" 버튼 → AccountForm 모달 열기
- [ ] "삭제" 버튼 → 삭제 확인 다이얼로그
- [ ] 모든 액션 테스트

**예상 시간**: 15분

---

#### 테스트 및 검증 ⏳
- [ ] `npm run type-check` 실행
- [ ] `npm run lint` 실행
- [ ] 브라우저에서 전체 플로우 테스트
- [ ] Network 탭에서 API 호출 확인
- [ ] 에러 없이 동작하는지 확인

**예상 시간**: 30분

---

## 🔧 Quick Reference

### 배포 관련 명령어
```bash
# 개발 환경 시작
docker-compose -f docker-compose.dev.yml up -d

# 이미지 빌드 및 저장
./scripts/build-and-save.sh v1.0.0

# Synology에 배포
./scripts/deploy-to-synology.sh v1.0.0

# 전통적 배포
./scripts/deploy.sh

# 서버 연결 테스트
./scripts/check-server-connection.sh

# Synology 접속
ssh -p 17215 naudhizfehu@192.168.0.30

# 컨테이너 상태 확인
docker-compose -f docker-compose.prod.yml ps

# 로그 확인
docker-compose -f docker-compose.prod.yml logs -f
```

---

## 📊 진행 상황 추적

**전체 진행률**: 0% (아직 시작 안 함)

- [ ] 즉시 수행 (3개 작업) - 0/3
- [ ] 단기 (3개 작업) - 0/3
- [ ] 중기 (3개 작업) - 0/3
- [ ] 장기 (3개 작업) - 0/3

**다음 액션**: 테스트 자동화 활성화 또는 DB 백업 자동화 중 선택

---

**문서 작성일**: 2025-11-21
**작성자**: Claude Code
**버전**: 1.0
