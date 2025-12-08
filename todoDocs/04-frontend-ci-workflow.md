# Phase 3: Frontend CI 워크플로우 수정

**예상 시간**: 15분
**난이도**: 쉬움
**담당**: DevOps

---

## 📋 목표

GitHub Actions CI 파이프라인에서 Frontend 테스트를 자동으로 실행하도록 설정

---

## 🔍 현재 상태 분석

### 수정 대상 파일
**파일**: `.github/workflows/frontend-ci.yml`

### 현재 설정 확인
- 린트: 실행되지만 `continue-on-error: true` (실패해도 계속)
- 타입체크: 실행됨
- 테스트: 주석 처리되어 있거나 없음
- 빌드: 실행됨

---

## 🛠️ 구현 단계

### Step 1: GitHub Actions 워크플로우 파일 열기

```bash
# 파일 경로
.github/workflows/frontend-ci.yml
```

### Step 2: 테스트 실행 단계 추가

**기존 워크플로우 구조**:
```yaml
jobs:
  frontend-ci:
    steps:
      - Checkout
      - Setup Node
      - Install dependencies
      - Lint (continue-on-error)
      - Type check
      - Build
```

**수정 후 구조**:
```yaml
jobs:
  frontend-ci:
    steps:
      - Checkout
      - Setup Node
      - Install dependencies
      - Run tests         # 추가
      - Lint (continue-on-error)
      - Type check
      - Build
      - Upload coverage   # 추가 (선택)
```

### Step 3: 워크플로우 코드 수정

**완성된 워크플로우**:

```yaml
name: Frontend CI

on:
  push:
    branches: [ develop, main ]
    paths:
      - 'frontend/**'
      - '.github/workflows/frontend-ci.yml'
  pull_request:
    branches: [ develop, main ]
    paths:
      - 'frontend/**'

jobs:
  frontend-ci:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Setup Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '18'
        cache: 'npm'
        cache-dependency-path: frontend/package-lock.json

    - name: Cache node modules
      uses: actions/cache@v3
      with:
        path: frontend/node_modules
        key: ${{ runner.os }}-node-${{ hashFiles('frontend/package-lock.json') }}
        restore-keys: |
          ${{ runner.os }}-node-

    - name: Install dependencies
      run: npm ci
      working-directory: ./frontend

    # ✨ 테스트 실행 (새로 추가)
    - name: Run tests
      run: npm run test:run
      working-directory: ./frontend

    - name: Run linting
      run: npm run lint
      working-directory: ./frontend
      continue-on-error: true

    - name: Run type checking
      run: npm run type-check
      working-directory: ./frontend

    - name: Build production
      run: npm run build
      working-directory: ./frontend

    # ✨ 테스트 커버리지 (선택)
    - name: Generate coverage report
      if: success()
      run: npm run test:coverage -- --run
      working-directory: ./frontend

    - name: Upload coverage to Codecov
      if: success()
      uses: codecov/codecov-action@v3
      with:
        files: ./frontend/coverage/coverage-final.json
        flags: frontend
        name: frontend-coverage

    # 빌드 산출물 업로드
    - name: Upload build artifact
      if: success()
      uses: actions/upload-artifact@v3
      with:
        name: frontend-dist
        path: frontend/dist/
        retention-days: 7
```

---

## 📊 단계별 설명

### 1. 테스트 실행
```yaml
- name: Run tests
  run: npm run test:run
  working-directory: ./frontend
```

**동작**:
- `test:run`: watch 모드 아닌 단일 실행
- 테스트 실패 시 전체 워크플로우 중단
- PR 머지 차단 (브랜치 보호 규칙 설정 시)

### 2. 린트 (경고만)
```yaml
- name: Run linting
  run: npm run lint
  working-directory: ./frontend
  continue-on-error: true
```

**동작**:
- 린트 실패해도 계속 진행
- 경고 표시만 하고 빌드는 계속

**변경하려면**: `continue-on-error: true` 제거

### 3. 타입 체크
```yaml
- name: Run type checking
  run: npm run type-check
  working-directory: ./frontend
```

**동작**:
- TypeScript 타입 오류 시 중단
- 빌드 실패

### 4. 커버리지 업로드 (선택)
```yaml
- name: Generate coverage report
  if: success()
  run: npm run test:coverage -- --run
  working-directory: ./frontend
```

**동작**:
- 이전 단계 모두 성공 시만 실행
- 커버리지 HTML 리포트 생성
- Codecov에 업로드

---

## 🚀 배포

### 1. 변경사항 커밋

```bash
git checkout -b feature/enable-frontend-tests
git add .github/workflows/frontend-ci.yml
git commit -m "feat: Frontend 테스트 자동화 활성화

- 테스트 실행 단계 추가
- 테스트 실패 시 빌드 중단
- 커버리지 리포트 업로드 추가 (선택)"
```

### 2. GitHub에 푸시

```bash
git push origin feature/enable-frontend-tests
```

### 3. GitHub Actions 확인

1. GitHub 레포지토리 접속
2. **Actions** 탭 클릭
3. **Frontend CI** 워크플로우 확인
4. 실행 로그 확인

**성공 예시**:
```
✓ Checkout code
✓ Setup Node.js
✓ Cache node modules
✓ Install dependencies
✓ Run tests
  ✓ src/components/ui/button.test.tsx (5 tests)
  ✓ src/lib/utils.test.ts (2 tests)
  ✓ src/utils/format.test.ts (7 tests)
  Tests: 14 passed (14)
✓ Run linting
✓ Run type checking
✓ Build production
✓ Generate coverage report
✓ Upload coverage
✓ Upload build artifact
```

---

## 🧪 테스트 실행 순서 최적화

### 옵션 1: 빠른 실패 (Fast Fail)

테스트를 가장 먼저 실행:

```yaml
steps:
  - Checkout
  - Setup Node
  - Install dependencies
  - Run tests           # 가장 먼저 (빠른 실패)
  - Lint
  - Type check
  - Build
```

**장점**:
- 테스트 실패 시 빠르게 피드백
- 린트/타입체크/빌드 시간 절약

### 옵션 2: 병렬 실행 (Parallel Jobs)

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - Checkout
      - Setup Node
      - Install dependencies
      - Run tests

  lint:
    runs-on: ubuntu-latest
    steps:
      - Checkout
      - Setup Node
      - Install dependencies
      - Run lint

  build:
    needs: [test, lint]
    runs-on: ubuntu-latest
    steps:
      - Checkout
      - Setup Node
      - Install dependencies
      - Type check
      - Build
```

**장점**:
- 더 빠른 실행 (병렬 처리)
- 독립적인 작업 분리

**단점**:
- 복잡도 증가
- GitHub Actions 분(minutes) 소비 증가

---

## 🔍 테스트 실패 시나리오 검증

### 1. 일부러 실패하는 테스트 추가

**파일**: `frontend/src/components/ui/test-failure.test.tsx` (신규)

```typescript
import { describe, it, expect } from 'vitest';

describe('Test Failure Check', () => {
  it('should fail intentionally', () => {
    expect(1).toBe(2); // 일부러 실패
  });
});
```

### 2. 커밋 및 푸시

```bash
git add frontend/src/components/ui/test-failure.test.tsx
git commit -m "test: 테스트 실패 시나리오 검증"
git push
```

### 3. GitHub Actions 확인

**예상 결과**:
```
✓ Checkout code
✓ Setup Node.js
✓ Install dependencies
❌ Run tests
  ✓ src/components/ui/button.test.tsx (5 tests)
  ❌ src/components/ui/test-failure.test.tsx (1 test)
    ❌ should fail intentionally

Expected: 2
Received: 1

Tests: 1 failed, 13 passed (14)
Error: Process completed with exit code 1.
```

**결과**:
- ❌ 전체 워크플로우 실패
- 🚫 PR 머지 차단
- 📧 실패 알림 (설정 시)

### 4. 실패 테스트 제거

```bash
git rm frontend/src/components/ui/test-failure.test.tsx
git commit -m "test: 실패 테스트 제거"
git push
```

---

## 📈 성능 최적화

### 1. 캐싱 전략

**node_modules 캐싱**:
```yaml
- name: Cache node modules
  uses: actions/cache@v3
  with:
    path: frontend/node_modules
    key: ${{ runner.os }}-node-${{ hashFiles('frontend/package-lock.json') }}
    restore-keys: |
      ${{ runner.os }}-node-
```

**Vitest 캐시**:
```yaml
- name: Cache Vitest
  uses: actions/cache@v3
  with:
    path: frontend/node_modules/.vitest
    key: ${{ runner.os }}-vitest-${{ hashFiles('frontend/vitest.config.ts') }}
```

### 2. 조건부 실행

**Frontend 파일 변경 시만 실행**:
```yaml
on:
  push:
    paths:
      - 'frontend/**'
      - '.github/workflows/frontend-ci.yml'
```

---

## ⚠️ 문제 해결

### 문제 1: npm ci 실패

**에러**:
```
npm ERR! Cannot read property 'match' of undefined
```

**해결**:
```yaml
- name: Install dependencies
  run: |
    rm -rf node_modules package-lock.json
    npm install
  working-directory: ./frontend
```

또는:
```yaml
- name: Install dependencies
  run: npm install --legacy-peer-deps
  working-directory: ./frontend
```

### 문제 2: 테스트 타임아웃

**에러**:
```
Test timed out in 5000ms
```

**해결**:
`vitest.config.ts`에서 타임아웃 증가:
```typescript
test: {
  testTimeout: 10000,
  hookTimeout: 10000,
}
```

### 문제 3: Out of Memory

**에러**:
```
JavaScript heap out of memory
```

**해결**:
```yaml
- name: Run tests
  run: NODE_OPTIONS="--max_old_space_size=4096" npm run test:run
  working-directory: ./frontend
```

---

## ✅ 체크리스트

### 필수 작업
- [ ] `.github/workflows/frontend-ci.yml` 파일 열기
- [ ] 테스트 실행 단계 추가
- [ ] `npm run test:run` 명령 사용 (watch 모드 X)
- [ ] GitHub에 푸시
- [ ] GitHub Actions에서 테스트 실행 확인
- [ ] 테스트 성공 확인

### 선택 작업
- [ ] 커버리지 리포트 생성 단계 추가
- [ ] Codecov 업로드 설정
- [ ] 캐싱 전략 추가
- [ ] 병렬 실행 고려

### 검증
- [ ] CI에서 테스트 자동 실행
- [ ] 테스트 실패 시 빌드 중단
- [ ] PR에서 테스트 상태 표시
- [ ] 실패 시나리오 검증

---

## 📊 워크플로우 실행 시간 비교

### 변경 전
```
Total: ~3분
├─ Setup: 30초
├─ Install: 45초
├─ Lint: 20초
├─ Type check: 30초
└─ Build: 55초
```

### 변경 후
```
Total: ~4분
├─ Setup: 30초
├─ Install: 45초
├─ Test: 35초        # 추가
├─ Lint: 20초
├─ Type check: 30초
├─ Build: 55초
└─ Coverage: 25초    # 추가 (선택)
```

**증가 시간**: +35초 ~ +1분 (커버리지 포함 시)

---

## 🔗 다음 단계

Phase 3 완료 후:
- **Phase 4**: 통합 테스트 및 검증 (`05-integration-testing.md`)

---

## 📚 참고 자료

- [GitHub Actions 문서](https://docs.github.com/en/actions)
- [GitHub Actions 캐싱](https://docs.github.com/en/actions/using-workflows/caching-dependencies-to-speed-up-workflows)
- [Codecov GitHub Action](https://github.com/codecov/codecov-action)

---

**작성일**: 2025-11-21
**버전**: 1.0
**상태**: 준비 완료
