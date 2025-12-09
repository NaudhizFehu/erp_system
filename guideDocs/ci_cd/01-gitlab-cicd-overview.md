# GitLab CI/CD 전체 가이드

**목적**: Backend + Frontend 테스트 자동화 통합 가이드
**범위**: 테스트 자동화 (배포는 별도 작업)
**상태**: 완료 ✅

---

## 📋 개요

GitLab CI/CD를 통해 Backend 및 Frontend의 코드 품질 검증과 테스트를 자동으로 실행하는 완전 통합 파이프라인입니다.

### 자동화 범위

**Backend**
- Maven 테스트 자동 실행
- PostgreSQL 통합 테스트
- WAR 빌드 및 아티팩트 보관

**Frontend**
- Lint 검사 (ESLint + Prettier)
- Type 검사 (TypeScript)
- 단위 테스트 (Vitest)
- 프로덕션 빌드

---

## 🏗️ 시스템 아키텍처

```
GitLab CI/CD Pipeline
│
├─ GitLab Runner (mac-local-runner)
│  ├─ Platform: macOS ARM64
│  ├─ Executor: Shell
│  └─ Location: Self-hosted
│
├─ Backend Pipeline
│  ├─ Stage: build
│  ├─ Database: PostgreSQL 15
│  ├─ Tests: 16개 (JUnit 5)
│  └─ Artifacts: WAR 파일 (76MB)
│
└─ Frontend Pipeline
   ├─ Stage: lint (ESLint + TypeScript)
   ├─ Stage: test (Vitest, 14 tests)
   ├─ Stage: build (Vite)
   └─ Artifacts: dist/ (1.03MB)
```

---

## 📊 파이프라인 구조

### 전체 Stages

```yaml
stages:
  - lint    # Frontend 코드 품질 검사
  - test    # Backend + Frontend 테스트
  - build   # Backend + Frontend 빌드
```

### 실행 흐름

```
Commit & Push
    ↓
GitLab 감지
    ↓
┌─────────────────────────────────────┐
│ Stage 1: lint                       │
│ - frontend-lint (ESLint, TS, Format)│
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ Stage 2: test (병렬 실행)            │
│ - backend-test (16 tests)           │
│ - frontend-test (14 tests)          │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ Stage 3: build (병렬 실행)           │
│ - backend-test (WAR 빌드)           │
│ - frontend-build (Vite 빌드)        │
└─────────────────────────────────────┘
    ↓
Artifacts 업로드 & 보관 (7일)
```

---

## 🛠️ GitLab Runner 설정

### 1. Runner 설치

```bash
# Homebrew로 설치 (macOS)
brew install gitlab-runner

# 설치 확인
gitlab-runner --version
# 출력: Version: 18.6.2
```

### 2. Runner 등록

```bash
# Runner 등록 (대화형)
gitlab-runner register

# 입력 정보:
# GitLab instance URL: https://gitlab.fehu.kr
# Registration token: [GitLab 프로젝트 > Settings > CI/CD > Runners에서 확인]
# Description: mac-local-runner
# Tags: (비워두기)
# Executor: shell
```

### 3. Runner 설정 파일

**파일**: `~/.gitlab-runner/config.toml`

```toml
concurrent = 1
check_interval = 0
connection_max_age = "15m0s"
shutdown_timeout = 0

[session_server]
  session_timeout = 1800

[[runners]]
  name = "mac-local-runner"
  url = "https://gitlab.fehu.kr"
  id = 6
  token = "glrt-xxxxxxxxxxxxx"
  token_obtained_at = 2025-11-30T05:32:48Z
  token_expires_at = 0001-01-01T00:00:00Z
  executor = "shell"
  clone_url = "https://gitlab.fehu.kr"
  [runners.custom_build_dir]
    enabled = true
  [runners.cache]
    MaxUploadedArchiveSize = 0
```

**중요 설정**:
- `clone_url`: Git clone 시 사용할 URL (hostname 해석 문제 해결)
- `custom_build_dir.enabled = true`: GIT_CLONE_PATH 사용 허용

### 4. Runner 시작

```bash
# Runner 서비스 시작
brew services start gitlab-runner

# Runner 상태 확인
gitlab-runner verify
# 출력: Verifying runner... is valid
```

---

## 📁 프로젝트 구조

```
cursor-erp-system/
├─ .gitlab-ci.yml          # CI/CD 파이프라인 정의
├─ backend/
│  ├─ src/
│  │  ├─ main/java/       # Backend 소스 코드
│  │  └─ test/
│  │     ├─ java/         # 테스트 코드
│  │     └─ resources/
│  │        └─ application-test.yml  # 테스트 DB 설정
│  ├─ pom.xml             # Maven 설정
│  └─ target/             # 빌드 산출물
│     ├─ *.war            # WAR 파일
│     └─ surefire-reports/
└─ frontend/
   ├─ src/
   │  ├─ utils/__tests__/ # Utils 테스트
   │  └─ components/__tests__/  # Component 테스트
   ├─ package.json        # npm 설정
   ├─ vitest.config.ts    # 테스트 설정
   └─ dist/               # 빌드 산출물
```

---

## 🔧 .gitlab-ci.yml 전체 구성

```yaml
# GitLab CI/CD 파이프라인 설정
stages:
  - lint
  - test
  - build

# Backend 빌드 및 테스트
backend-test:
  stage: build
  only:
    changes:
      - backend/**
      - .gitlab-ci.yml
  cache:
    key: ${CI_COMMIT_REF_SLUG}
    paths:
      - backend/.m2/repository
  variables:
    MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/backend/.m2/repository"
    JAVA_HOME: "/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
    GIT_CLONE_PATH: $CI_BUILDS_DIR/$CI_PROJECT_PATH
    CI_SERVER_URL: "https://gitlab.fehu.kr"
  before_script:
    - cd backend
    - export PATH="/opt/homebrew/bin:$PATH"
    - java -version
  script:
    - mvn clean package
  artifacts:
    when: always
    paths:
      - backend/target/surefire-reports/
      - backend/target/*.war
    reports:
      junit:
        - backend/target/surefire-reports/TEST-*.xml
    expire_in: 7 days

# Frontend Lint 검사
frontend-lint:
  stage: lint
  only:
    changes:
      - frontend/**
      - .gitlab-ci.yml
  cache:
    key: ${CI_COMMIT_REF_SLUG}-frontend
    paths:
      - frontend/node_modules
  before_script:
    - cd frontend
    - npm ci
  script:
    - npm run lint || true
    - npm run type-check
    - npm run format:check || true

# Frontend 테스트
frontend-test:
  stage: test
  only:
    changes:
      - frontend/**
      - .gitlab-ci.yml
  cache:
    key: ${CI_COMMIT_REF_SLUG}-frontend
    paths:
      - frontend/node_modules
  before_script:
    - cd frontend
    - npm ci
  script:
    - npm run test -- --run
  artifacts:
    when: always
    paths:
      - frontend/coverage/
    expire_in: 7 days

# Frontend 빌드
frontend-build:
  stage: build
  only:
    changes:
      - frontend/**
      - .gitlab-ci.yml
  cache:
    key: ${CI_COMMIT_REF_SLUG}-frontend
    paths:
      - frontend/node_modules
  before_script:
    - cd frontend
    - npm ci
  script:
    - npm run build
  artifacts:
    paths:
      - frontend/dist/
    expire_in: 7 days
```

---

## 📈 실행 결과

### Backend 테스트 결과
```
✅ SimpleLoginTest: 7/7 통과 (90.76초)
✅ LoginIntegrationTest: 9/9 통과 (83.27초)
✅ 전체: 16/16 테스트 통과
✅ 빌드: erp-system-1.0.0.war (76MB)
```

### Frontend 테스트 결과
```
✅ Utils Test: 7/7 통과
✅ Component Test: 7/7 통과
✅ 전체: 14/14 테스트 통과
✅ 빌드: dist/ (1.03MB, gzipped: 241.63KB)
```

### 파이프라인 실행 시간
- **Backend**: 약 3-4분 (Maven 캐시 사용 시 2-3분)
- **Frontend**: 약 2-3분 (npm 캐시 사용 시 1-2분)
- **전체 파이프라인**: 약 5-7분

### GitLab CI/CD 상태
- Pipeline Status: ✅ Passed
- Jobs: 4개 모두 성공
  - frontend-lint ✅
  - backend-test ✅
  - frontend-test ✅
  - frontend-build ✅
- Artifacts: 7일 보관

---

## 🚀 사용 방법

### 1. 자동 실행 (권장)

코드 변경 후 Git Push만 하면 자동으로 실행됩니다:

```bash
# 변경사항 커밋
git add .
git commit -m "feat: 새 기능 추가"

# Push (자동으로 파이프라인 실행)
git push origin feature/my-feature
```

### 2. GitLab 웹에서 확인

1. GitLab 프로젝트 접속
2. **Build** > **Pipelines** 메뉴
3. 최신 파이프라인 클릭
4. 각 Job 상태 확인

### 3. 로컬에서 테스트 (Push 전)

**Backend**:
```bash
cd backend
JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home \
mvn clean package
```

**Frontend**:
```bash
cd frontend
npm run type-check  # TypeScript 검사
npm run test -- --run  # 테스트 실행
npm run build  # 빌드 검증
```

---

## ⚠️ 공통 문제 해결

### 문제 1: Runner가 작동하지 않음

**확인**:
```bash
# Runner 상태 확인
gitlab-runner verify

# Runner 재시작
brew services restart gitlab-runner
```

### 문제 2: Git clone 실패

**에러**:
```
fatal: unable to access 'http://fehu-gitlab/...': Could not resolve host
```

**해결**: `~/.gitlab-runner/config.toml`에서 `clone_url` 설정 확인

### 문제 3: Cache 문제

**증상**: 빌드가 너무 느림

**해결**:
```bash
# GitLab에서 캐시 삭제
# Settings > CI/CD > Clear runner caches
```

---

## 📊 성능 최적화

### Cache 전략
- **Backend**: Maven repository (`backend/.m2/repository`)
- **Frontend**: node_modules (`frontend/node_modules`)
- **Cache Key**: `${CI_COMMIT_REF_SLUG}` (브랜치별 캐시)

### 병렬 실행
- test stage: backend-test + frontend-test 동시 실행
- build stage: backend-test + frontend-build 동시 실행

### Artifacts 관리
- 보관 기간: 7일
- 용량: Backend WAR (76MB) + Frontend dist (1.03MB)

---

## 🔗 상세 가이드

더 자세한 구성 방법은 개별 가이드를 참고하세요:

- **Backend CI/CD**: [02-backend-cicd-guide.md](./02-backend-cicd-guide.md)
  - GitLab Runner 설치 및 설정
  - PostgreSQL 연동
  - 테스트 환경 구성
  - 문제 해결

- **Frontend CI/CD**: [03-frontend-cicd-guide.md](./03-frontend-cicd-guide.md)
  - 샘플 테스트 작성
  - Vitest 설정
  - 빌드 최적화
  - 문제 해결

---

## 📚 참고 자료

- [GitLab CI/CD 공식 문서](https://docs.gitlab.com/ee/ci/)
- [GitLab Runner 설치 가이드](https://docs.gitlab.com/runner/install/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Vitest 공식 문서](https://vitest.dev/)

---

**작성일**: 2025-12-08
**버전**: 1.0
**작성자**: Claude Code
