# Phase 1: Backend 테스트 활성화

**예상 시간**: 30분
**난이도**: 쉬움
**담당**: Backend + DevOps

---

## 📋 목표

GitHub Actions CI 파이프라인에서 Backend Maven 테스트를 자동으로 실행하도록 설정

---

## 🔍 현재 상태 분석

### 수정 대상 파일
**파일**: `.github/workflows/backend-ci.yml`

### 현재 설정
```yaml
- name: Build with Maven
  run: mvn clean package -DskipTests
  working-directory: ./backend
```

**문제점**:
- `-DskipTests` 플래그로 모든 테스트 건너뜀
- 버그가 있어도 빌드가 성공함
- 코드 품질 보장 안 됨

---

## 🛠️ 구현 단계

### Step 1: GitHub Actions 워크플로우 파일 열기

```bash
# 파일 경로
.github/workflows/backend-ci.yml
```

### Step 2: 테스트 플래그 제거

**변경 전**:
```yaml
- name: Build with Maven
  run: mvn clean package -DskipTests
  working-directory: ./backend
```

**변경 후**:
```yaml
- name: Build with Maven
  run: mvn clean package
  working-directory: ./backend
```

### Step 3: 테스트 결과 업로드 추가 (선택)

워크플로우에 다음 단계 추가:

```yaml
- name: Upload test results
  if: always()
  uses: actions/upload-artifact@v3
  with:
    name: backend-test-results
    path: backend/target/surefire-reports/
    retention-days: 7
```

**설명**:
- `if: always()`: 테스트 성공/실패 여부와 관계없이 업로드
- `surefire-reports/`: Maven Surefire 플러그인이 생성하는 테스트 리포트
- `retention-days: 7`: 7일간 결과 보관

### Step 4: 테스트 커버리지 리포트 추가 (선택)

JaCoCo 플러그인이 `pom.xml`에 설정되어 있다면:

```yaml
- name: Generate test coverage report
  if: success()
  run: mvn jacoco:report
  working-directory: ./backend

- name: Upload coverage to Codecov
  if: success()
  uses: codecov/codecov-action@v3
  with:
    files: ./backend/target/site/jacoco/jacoco.xml
    flags: backend
    name: backend-coverage
```

---

## 🧪 로컬 테스트

### 1. 로컬에서 테스트 실행

```bash
cd backend
mvn clean test
```

**예상 결과**:
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.erp.auth.AuthServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### 2. 전체 빌드 테스트

```bash
mvn clean package
```

**확인 사항**:
- 모든 테스트가 실행되는지 확인
- 빌드가 성공하는지 확인
- `target/` 디렉토리에 JAR 파일 생성 확인

---

## 🚀 CI 배포

### 1. 변경사항 커밋

```bash
git checkout -b feature/enable-backend-tests
git add .github/workflows/backend-ci.yml
git commit -m "feat: Backend 테스트 자동화 활성화

- -DskipTests 플래그 제거
- 테스트 실패 시 빌드 중단
- 테스트 결과 아티팩트 업로드 추가"
```

### 2. GitHub에 푸시

```bash
git push origin feature/enable-backend-tests
```

### 3. GitHub Actions 확인

1. GitHub 레포지토리 접속
2. **Actions** 탭 클릭
3. **Backend CI** 워크플로우 확인
4. 실행 로그에서 테스트 결과 확인

**성공 예시**:
```
✓ Checkout code
✓ Set up JDK 17
✓ Cache Maven packages
✓ Build with Maven
  - Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
  - BUILD SUCCESS
✓ Upload test results
```

---

## 📊 완성된 워크플로우 예시

```yaml
name: Backend CI

on:
  push:
    branches: [ develop, main ]
    paths:
      - 'backend/**'
      - '.github/workflows/backend-ci.yml'
  pull_request:
    branches: [ develop, main ]
    paths:
      - 'backend/**'

jobs:
  backend-ci:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2

    - name: Build with Maven
      run: mvn clean package
      working-directory: ./backend

    - name: Upload test results
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: backend-test-results
        path: backend/target/surefire-reports/
        retention-days: 7

    - name: Upload JAR artifact
      if: success()
      uses: actions/upload-artifact@v3
      with:
        name: backend-jar
        path: backend/target/*.jar
        retention-days: 7
```

---

## 🔍 테스트 실패 시나리오 검증

### 1. 일부러 실패하는 테스트 추가

```java
// backend/src/test/java/com/erp/TestFailureCheck.java
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

### 2. 커밋 및 푸시

```bash
git add backend/src/test/java/com/erp/TestFailureCheck.java
git commit -m "test: 테스트 실패 시나리오 검증"
git push
```

### 3. GitHub Actions 확인

**예상 결과**:
- ❌ Build with Maven 단계 실패
- ❌ 전체 워크플로우 실패
- 🚫 PR 머지 차단 (브랜치 보호 규칙 설정 시)

### 4. 실패 테스트 제거

```bash
git rm backend/src/test/java/com/erp/TestFailureCheck.java
git commit -m "test: 실패 테스트 제거"
git push
```

---

## ⚠️ 문제 해결

### 문제 1: 테스트가 하나도 없음

**에러 메시지**:
```
[ERROR] No tests were executed!
```

**해결 방법**:
```bash
# 옵션 1: 테스트 없어도 빌드 통과
mvn clean package -DfailIfNoTests=false

# 옵션 2: 간단한 테스트 작성
# backend/src/test/java/com/erp/SmokeTest.java
@Test
public void contextLoads() {
    // 애플리케이션 컨텍스트가 로드되는지 확인
}
```

### 문제 2: 특정 테스트만 실패

**확인 방법**:
```bash
# 실패한 테스트만 재실행
mvn test -Dtest=FailingTestClass

# 로그 상세 출력
mvn test -X
```

**해결**:
- 테스트 로직 수정
- 또는 `@Disabled` 어노테이션으로 임시 비활성화

### 문제 3: 메모리 부족

**에러 메시지**:
```
java.lang.OutOfMemoryError: Java heap space
```

**해결**:
```yaml
- name: Build with Maven
  run: mvn clean package
  working-directory: ./backend
  env:
    MAVEN_OPTS: "-Xmx1024m"
```

---

## ✅ 체크리스트

### 필수 작업
- [ ] `.github/workflows/backend-ci.yml` 파일 열기
- [ ] `-DskipTests` 플래그 제거
- [ ] 로컬에서 `mvn clean package` 실행 성공
- [ ] GitHub에 푸시
- [ ] GitHub Actions에서 테스트 실행 확인
- [ ] 테스트 성공 확인

### 선택 작업
- [ ] 테스트 결과 아티팩트 업로드 추가
- [ ] 테스트 커버리지 리포트 생성
- [ ] Codecov 연동
- [ ] 실패 시나리오 검증

### 검증
- [ ] CI에서 테스트 자동 실행
- [ ] 테스트 실패 시 빌드 중단
- [ ] PR에서 테스트 상태 표시

---

## 📈 예상 결과

### 변경 전
```
✓ Checkout
✓ Setup JDK
✓ Build (tests skipped)
✓ Upload JAR
```

### 변경 후
```
✓ Checkout
✓ Setup JDK
✓ Build
  ├─ Compile
  ├─ Run tests (25 tests)
  └─ Package JAR
✓ Upload test results
✓ Upload JAR
```

---

## 🔗 다음 단계

Phase 1 완료 후:
- **Phase 2**: Frontend 테스트 설정 (`03-frontend-test-setup.md`)

---

**작성일**: 2025-11-21
**버전**: 1.0
**상태**: 준비 완료
