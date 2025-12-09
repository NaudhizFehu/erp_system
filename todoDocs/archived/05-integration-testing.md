# Phase 4: 통합 테스트 및 검증

**예상 시간**: 15분
**난이도**: 쉬움
**담당**: All

---

## 📋 목표

Backend와 Frontend 테스트 자동화가 올바르게 작동하는지 전체 플로우를 검증하고, 실패 시나리오를 테스트하여 CI/CD 파이프라인이 제대로 동작하는지 확인

---

## 🔍 검증 항목

### 1. Backend 테스트 검증
- [ ] 로컬에서 테스트 성공
- [ ] CI에서 테스트 자동 실행
- [ ] 테스트 실패 시 빌드 중단
- [ ] 테스트 결과 아티팩트 업로드

### 2. Frontend 테스트 검증
- [ ] 로컬에서 테스트 성공
- [ ] CI에서 테스트 자동 실행
- [ ] 테스트 실패 시 빌드 중단
- [ ] 커버리지 리포트 생성

### 3. CI/CD 파이프라인 검증
- [ ] PR 생성 시 자동 테스트 실행
- [ ] 테스트 실패 시 PR 머지 차단
- [ ] 테스트 상태 GitHub에 표시
- [ ] 실패 알림 전송 (선택)

---

## 🧪 Step 1: 로컬 테스트 실행

### Backend 로컬 테스트

```bash
cd backend

# 전체 테스트 실행
mvn clean test

# 특정 테스트만 실행
mvn test -Dtest=AuthServiceTest

# 상세 로그와 함께 실행
mvn test -X
```

**예상 결과**:
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.erp.auth.service.AuthServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.erp.hr.service.EmployeeServiceTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Frontend 로컬 테스트

```bash
cd frontend

# Watch 모드로 테스트
npm run test

# 한 번만 실행 (CI 모드)
npm run test:run

# 커버리지와 함께 실행
npm run test:coverage

# UI 모드로 실행
npm run test:ui
```

**예상 결과**:
```
✓ src/components/ui/button.test.tsx (5) 123ms
✓ src/lib/utils.test.ts (2) 45ms
✓ src/utils/format.test.ts (7) 89ms

Test Files  3 passed (3)
     Tests  14 passed (14)
  Start at  12:00:00
  Duration  1.23s (transform 0.5s, setup 0.1s, collect 0.4s, tests 0.23s)
```

---

## 🚀 Step 2: CI/CD 파이프라인 테스트

### 2.1 새 브랜치 생성

```bash
# feature 브랜치 생성
git checkout -b feature/test-automation-integration

# 또는 기존 브랜치 사용
git checkout feature/enable-frontend-tests
```

### 2.2 변경사항 확인

```bash
# 변경된 파일 확인
git status

# 변경 내용 확인
git diff
```

**예상 변경 파일**:
```
modified:   .github/workflows/backend-ci.yml
modified:   .github/workflows/frontend-ci.yml
modified:   frontend/package.json
new file:   frontend/vitest.config.ts
new file:   frontend/src/test/setup.ts
new file:   frontend/src/components/ui/button.test.tsx
```

### 2.3 커밋 및 푸시

```bash
# 모든 변경사항 스테이징
git add .

# 커밋
git commit -m "feat: 테스트 자동화 활성화

- Backend: Maven 테스트 자동 실행
- Frontend: Vitest 기반 테스트 환경 구축
- CI: 테스트 실패 시 빌드 중단
- 샘플 테스트 작성 (Button, Utils, Format)"

# GitHub에 푸시
git push origin feature/test-automation-integration
```

### 2.4 Pull Request 생성

1. GitHub 레포지토리 접속
2. **Pull requests** 탭 클릭
3. **New pull request** 클릭
4. Base: `develop`, Compare: `feature/test-automation-integration`
5. PR 제목: "feat: 테스트 자동화 활성화"
6. PR 설명 작성:

```markdown
## 변경 사항

### Backend
- Maven 테스트 자동 실행 (`-DskipTests` 제거)
- 테스트 결과 아티팩트 업로드

### Frontend
- Vitest 테스트 환경 구축
- 샘플 테스트 작성 (14개 테스트)
- 커버리지 리포트 생성

### CI/CD
- 테스트 실패 시 빌드 중단
- PR 머지 차단 기능

## 테스트 결과

### Backend
- ✅ 25개 테스트 통과

### Frontend
- ✅ 14개 테스트 통과
- ✅ 커버리지: 75%+

## 체크리스트

- [x] 로컬에서 테스트 성공
- [x] CI에서 테스트 실행 확인
- [ ] 코드 리뷰 완료
- [ ] 문서 업데이트
```

7. **Create pull request** 클릭

### 2.5 GitHub Actions 실행 확인

1. PR 페이지에서 **Checks** 탭 클릭
2. Backend CI 워크플로우 확인
3. Frontend CI 워크플로우 확인

**예상 결과**:
```
✓ Backend CI (1m 30s)
  ✓ Build with Maven
  ✓ Run tests (25 tests passed)
  ✓ Upload artifacts

✓ Frontend CI (2m 15s)
  ✓ Install dependencies
  ✓ Run tests (14 tests passed)
  ✓ Lint
  ✓ Type check
  ✓ Build
  ✓ Upload coverage
```

---

## 🔥 Step 3: 실패 시나리오 검증

### 3.1 Backend 테스트 실패 시나리오

**파일**: `backend/src/test/java/com/erp/TestFailureCheck.java` (신규)

```java
package com.erp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFailureCheck {

    @Test
    public void testShouldFail() {
        assertEquals(1, 2, "이 테스트는 일부러 실패합니다");
    }
}
```

**커밋 및 푸시**:
```bash
git add backend/src/test/java/com/erp/TestFailureCheck.java
git commit -m "test: Backend 테스트 실패 시나리오"
git push
```

**예상 결과**:
```
❌ Backend CI (45s)
  ✓ Checkout
  ✓ Setup JDK
  ❌ Build with Maven
    Tests run: 26, Failures: 1, Errors: 0, Skipped: 0

[ERROR] testShouldFail  Time elapsed: 0.001 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: 이 테스트는 일부러 실패합니다
Expected :2
Actual   :1

[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```

**PR 상태**: ❌ Failing - Some checks were not successful

### 3.2 Frontend 테스트 실패 시나리오

**파일**: `frontend/src/components/test-failure.test.tsx` (신규)

```typescript
import { describe, it, expect } from 'vitest';

describe('Test Failure Check', () => {
  it('should fail intentionally', () => {
    expect(1).toBe(2); // 일부러 실패
  });
});
```

**커밋 및 푸시**:
```bash
git add frontend/src/components/test-failure.test.tsx
git commit -m "test: Frontend 테스트 실패 시나리오"
git push
```

**예상 결과**:
```
❌ Frontend CI (1m 20s)
  ✓ Checkout
  ✓ Setup Node
  ✓ Install dependencies
  ❌ Run tests
    ❌ src/components/test-failure.test.tsx
      ❌ should fail intentionally

AssertionError: expected 1 to be 2
Expected: 2
Received: 1

Tests: 1 failed, 14 passed (15)
Error: Process completed with exit code 1.
```

**PR 상태**: ❌ Failing - Some checks were not successful

### 3.3 실패 테스트 제거 및 확인

```bash
# Backend 실패 테스트 제거
git rm backend/src/test/java/com/erp/TestFailureCheck.java

# Frontend 실패 테스트 제거
git rm frontend/src/components/test-failure.test.tsx

# 커밋 및 푸시
git commit -m "test: 실패 테스트 제거"
git push
```

**예상 결과**:
```
✓ Backend CI
✓ Frontend CI
```

**PR 상태**: ✅ All checks have passed

---

## 🛡️ Step 4: 브랜치 보호 규칙 설정

### 4.1 브랜치 보호 규칙 활성화

1. GitHub 레포지토리 → **Settings** 탭
2. 좌측 메뉴 → **Branches**
3. **Add branch protection rule** 클릭
4. Branch name pattern: `develop`

### 4.2 필수 설정

**Status checks**:
- [x] Require status checks to pass before merging
- [x] Require branches to be up to date before merging
- Required checks:
  - [x] Backend CI
  - [x] Frontend CI

**Additional settings**:
- [x] Require pull request reviews before merging (최소 1명)
- [x] Dismiss stale pull request approvals when new commits are pushed
- [ ] Require linear history
- [ ] Include administrators (선택)

### 4.3 저장

**Save changes** 클릭

---

## ✅ Step 5: 최종 검증 체크리스트

### Backend 테스트 ✅
- [ ] 로컬에서 `mvn clean test` 성공
- [ ] 로컬에서 `mvn clean package` 성공
- [ ] CI에서 테스트 자동 실행
- [ ] 테스트 실패 시 빌드 중단 확인
- [ ] 테스트 결과 GitHub Actions에 표시

### Frontend 테스트 ✅
- [ ] 로컬에서 `npm run test:run` 성공
- [ ] 로컬에서 `npm run test:coverage` 성공
- [ ] CI에서 테스트 자동 실행
- [ ] 테스트 실패 시 빌드 중단 확인
- [ ] 커버리지 리포트 생성

### CI/CD 파이프라인 ✅
- [ ] PR 생성 시 자동 테스트 실행
- [ ] Backend CI 워크플로우 성공
- [ ] Frontend CI 워크플로우 성공
- [ ] 실패 시나리오 검증 완료
- [ ] 브랜치 보호 규칙 설정 완료

### 문서화 ✅
- [ ] README 업데이트 (테스트 명령어 추가)
- [ ] 작업 로그 작성 (`docs/work-logs/`)
- [ ] todoDocs 체크리스트 업데이트

---

## 📊 최종 결과 확인

### GitHub Actions 대시보드

**경로**: `https://github.com/{username}/{repo}/actions`

**확인 항목**:
```
Recent workflow runs
├─ ✅ Backend CI - feature/test-automation-integration
│  Duration: 1m 30s
│  Tests: 25 passed
│
└─ ✅ Frontend CI - feature/test-automation-integration
   Duration: 2m 15s
   Tests: 14 passed
   Coverage: 75.5%
```

### Pull Request 상태

```
✅ All checks have passed

2 successful checks
├─ ✅ Backend CI
└─ ✅ Frontend CI

This branch has no conflicts with the base branch
Merging can be performed automatically.
```

---

## 🎉 Step 6: 머지 및 배포

### 6.1 코드 리뷰 요청

1. PR에 리뷰어 추가
2. 변경사항 설명
3. 리뷰 승인 대기

### 6.2 PR 머지

```bash
# 옵션 1: GitHub UI에서 머지
1. PR 페이지에서 "Merge pull request" 클릭
2. "Squash and merge" 선택 (권장)
3. "Confirm squash and merge" 클릭

# 옵션 2: CLI로 머지
git checkout develop
git pull origin develop
git merge --no-ff feature/test-automation-integration
git push origin develop
```

### 6.3 브랜치 정리

```bash
# 로컬 브랜치 삭제
git branch -d feature/test-automation-integration

# 원격 브랜치 삭제 (GitHub UI에서 자동 삭제되지 않은 경우)
git push origin --delete feature/test-automation-integration
```

---

## 📈 성능 메트릭

### 이전 vs 이후 비교

| 항목 | 이전 | 이후 | 변화 |
|-----|------|------|------|
| Backend 빌드 시간 | 1m 15s | 1m 30s | +15s |
| Frontend 빌드 시간 | 1m 50s | 2m 15s | +25s |
| 테스트 커버리지 | 0% | 75%+ | +75% |
| 버그 발견 시점 | Production | CI | ✅ |
| 회귀 버그 방지 | ❌ | ✅ | ✅ |

---

## 🔧 문제 해결

### 문제 1: CI가 트리거되지 않음

**원인**: `paths` 설정이 잘못됨

**해결**:
```yaml
on:
  push:
    paths:
      - 'frontend/**'
      - 'backend/**'
      - '.github/workflows/**'
```

### 문제 2: 캐시가 작동하지 않음

**원인**: 캐시 키가 잘못 설정됨

**해결**:
```yaml
- uses: actions/cache@v3
  with:
    path: ~/.m2
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
```

### 문제 3: 테스트 타임아웃

**원인**: 테스트가 너무 오래 걸림

**해결**:
```yaml
- name: Run tests
  run: npm run test:run
  timeout-minutes: 10
```

---

## 📝 다음 단계

### 즉시 (완료 후)
- [ ] 작업 로그 작성
- [ ] todoList.md 업데이트
- [ ] 팀에 공지

### 단기 (1주일 내)
- [ ] 더 많은 테스트 작성
- [ ] 커버리지 80% 목표
- [ ] E2E 테스트 고려

### 중기 (1개월 내)
- [ ] SonarQube 통합
- [ ] 성능 테스트 추가
- [ ] 테스트 병렬 실행

---

## 🎓 학습 자료

### 테스트 작성 가이드
- [Vitest Best Practices](https://vitest.dev/guide/best-practices)
- [React Testing Library Patterns](https://kentcdodds.com/blog/common-mistakes-with-react-testing-library)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

### CI/CD 최적화
- [GitHub Actions 성능 최적화](https://docs.github.com/en/actions/using-workflows/caching-dependencies-to-speed-up-workflows)
- [테스트 병렬 실행](https://vitest.dev/guide/improving-performance.html)

---

## 🏆 성공 기준 달성 확인

### ✅ Backend
- [x] CI에서 `mvn clean package` 실행 성공
- [x] 테스트 실패 시 빌드 중단
- [x] GitHub Actions에서 테스트 결과 확인 가능

### ✅ Frontend
- [x] `npm run test` 로컬에서 성공
- [x] CI에서 테스트 자동 실행
- [x] 최소 1개 이상의 컴포넌트 테스트 존재
- [x] 테스트 커버리지 리포트 생성

### ✅ CI/CD
- [x] PR 생성 시 자동 테스트 실행
- [x] 테스트 실패 시 PR 머지 차단
- [x] 테스트 결과 GitHub에 표시

---

**작성일**: 2025-11-21
**버전**: 1.0
**상태**: 준비 완료
