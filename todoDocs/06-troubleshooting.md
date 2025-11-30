# 테스트 자동화 문제 해결 가이드

**목적**: 테스트 자동화 구현 중 발생할 수 있는 문제와 해결 방법

---

## 📑 목차

1. [Backend 테스트 문제](#backend-테스트-문제)
2. [Frontend 테스트 문제](#frontend-테스트-문제)
3. [CI/CD 파이프라인 문제](#cicd-파이프라인-문제)
4. [성능 문제](#성능-문제)
5. [의존성 문제](#의존성-문제)

---

## Backend 테스트 문제

### 문제 1: 테스트가 하나도 없음

**에러 메시지**:
```
[ERROR] No tests were executed!
[ERROR] Please ensure your test classes use a supported test framework
```

**원인**:
- `src/test/java/` 디렉토리에 테스트 파일이 없음
- 테스트 클래스가 JUnit 규칙을 따르지 않음

**해결 방법**:

**옵션 1**: 빈 테스트로도 통과하도록 설정
```yaml
# .github/workflows/backend-ci.yml
- name: Build with Maven
  run: mvn clean package -DfailIfNoTests=false
  working-directory: ./backend
```

**옵션 2**: 간단한 Smoke 테스트 작성
```java
// backend/src/test/java/com/erp/SmokeTest.java
package com.erp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SmokeTest {

    @Test
    public void contextLoads() {
        // 애플리케이션 컨텍스트가 정상적으로 로드되는지 확인
    }
}
```

---

### 문제 2: 특정 테스트만 실패

**에러 메시지**:
```
[ERROR] testGetUser  Time elapsed: 0.123 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Expected user not found
```

**원인**:
- 테스트 데이터 문제
- Mock 설정 누락
- 환경 설정 차이

**해결 방법**:

**Step 1**: 실패한 테스트만 재실행
```bash
mvn test -Dtest=UserServiceTest#testGetUser
```

**Step 2**: 상세 로그 확인
```bash
mvn test -X -Dtest=UserServiceTest
```

**Step 3**: 테스트 격리 확인
```java
@BeforeEach
public void setUp() {
    // 각 테스트 전에 상태 초기화
    testData.clear();
}

@AfterEach
public void tearDown() {
    // 각 테스트 후에 정리
}
```

**Step 4**: 임시로 테스트 비활성화
```java
@Disabled("임시로 비활성화 - Issue #123 참조")
@Test
public void testGetUser() {
    // ...
}
```

---

### 문제 3: 메모리 부족 에러

**에러 메시지**:
```
java.lang.OutOfMemoryError: Java heap space
```

**원인**:
- 테스트가 너무 많은 메모리 사용
- 메모리 누수
- JVM 힙 크기 부족

**해결 방법**:

**로컬 환경**:
```bash
# 힙 메모리 증가
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"
mvn clean test
```

**CI 환경**:
```yaml
# .github/workflows/backend-ci.yml
- name: Build with Maven
  run: mvn clean package
  working-directory: ./backend
  env:
    MAVEN_OPTS: "-Xmx2048m"
```

**pom.xml 설정**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>-Xmx1024m -XX:MaxPermSize=256m</argLine>
    </configuration>
</plugin>
```

---

### 문제 4: 데이터베이스 연결 실패

**에러 메시지**:
```
org.springframework.jdbc.CannotGetJdbcConnectionException: Failed to obtain JDBC Connection
```

**원인**:
- 테스트용 DB 설정 누락
- H2 의존성 누락

**해결 방법**:

**Step 1**: H2 인메모리 DB 사용
```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

**Step 2**: 테스트 프로파일 설정
```yaml
# src/test/resources/application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
```

**Step 3**: 테스트에서 프로파일 활성화
```java
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
    // ...
}
```

---

## Frontend 테스트 문제

### 문제 5: Vitest를 찾을 수 없음

**에러 메시지**:
```
sh: vitest: command not found
```

**원인**:
- Vitest가 설치되지 않음
- package.json 스크립트 오타

**해결 방법**:

**Step 1**: 의존성 재설치
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

**Step 2**: Vitest 설치 확인
```bash
npm list vitest
```

**Step 3**: 수동 설치
```bash
npm install -D vitest @vitest/ui jsdom
```

---

### 문제 6: Path alias 인식 안 됨

**에러 메시지**:
```
Error: Cannot find module '@/components/ui/button'
```

**원인**:
- vitest.config.ts에 alias 설정 누락
- tsconfig.json과 불일치

**해결 방법**:

**Step 1**: vitest.config.ts 확인
```typescript
import path from 'path';

export default defineConfig({
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
});
```

**Step 2**: tsconfig.json 확인
```json
{
  "compilerOptions": {
    "paths": {
      "@/*": ["./src/*"]
    }
  }
}
```

**Step 3**: 상대 경로로 임시 해결
```typescript
// 절대 경로 (에러)
import { Button } from '@/components/ui/button';

// 상대 경로 (임시 해결)
import { Button } from '../../../components/ui/button';
```

---

### 문제 7: CSS import 에러

**에러 메시지**:
```
Unknown file extension ".css" for /src/components/ui/button.css
```

**원인**:
- Vitest가 CSS 파일을 처리하지 못함

**해결 방법**:

**옵션 1**: CSS 처리 활성화
```typescript
// vitest.config.ts
export default defineConfig({
  test: {
    css: true, // CSS 파일 처리 활성화
  },
});
```

**옵션 2**: CSS를 Mock으로 처리
```typescript
// vitest.config.ts
export default defineConfig({
  test: {
    css: false,
    mockReset: true,
  },
});
```

---

### 문제 8: React Router 에러

**에러 메시지**:
```
Error: useNavigate() may be used only in the context of a <Router> component
```

**원인**:
- 테스트에서 Router context 없음

**해결 방법**:

**옵션 1**: Router mock 설정
```typescript
// src/test/setup.ts
import { vi } from 'vitest';

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => vi.fn(),
    useLocation: () => ({ pathname: '/' }),
    useParams: () => ({}),
  };
});
```

**옵션 2**: 테스트에서 Router 제공
```typescript
import { BrowserRouter } from 'react-router-dom';
import { render } from '@testing-library/react';

function renderWithRouter(ui: ReactElement) {
  return render(
    <BrowserRouter>
      {ui}
    </BrowserRouter>
  );
}

test('navigation works', () => {
  renderWithRouter(<MyComponent />);
});
```

---

### 문제 9: matchMedia 에러

**에러 메시지**:
```
TypeError: window.matchMedia is not a function
```

**원인**:
- jsdom이 matchMedia를 지원하지 않음

**해결 방법**:

```typescript
// src/test/setup.ts
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
});
```

---

### 문제 10: React Query 에러

**에러 메시지**:
```
Error: No QueryClient set, use QueryClientProvider to set one
```

**원인**:
- React Query context 없음

**해결 방법**:

```typescript
// src/test/utils.tsx
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

const createTestQueryClient = () => new QueryClient({
  defaultOptions: {
    queries: { retry: false },
    mutations: { retry: false },
  },
});

export function renderWithQueryClient(ui: ReactElement) {
  const testQueryClient = createTestQueryClient();
  return render(
    <QueryClientProvider client={testQueryClient}>
      {ui}
    </QueryClientProvider>
  );
}
```

**사용**:
```typescript
import { renderWithQueryClient } from '@/test/utils';

test('fetches data', async () => {
  renderWithQueryClient(<MyComponent />);
  // ...
});
```

---

## CI/CD 파이프라인 문제

### 문제 11: CI가 트리거되지 않음

**원인**:
- 워크플로우 paths 설정 문제
- 브랜치 이름 불일치

**해결 방법**:

**Step 1**: paths 설정 확인
```yaml
on:
  push:
    branches: [ develop, main ]
    paths:
      - 'frontend/**'
      - '.github/workflows/frontend-ci.yml'
```

**Step 2**: 모든 변경에 대해 실행 (테스트용)
```yaml
on:
  push:
    branches: [ develop, main ]
  # paths 제거
```

**Step 3**: 워크플로우 문법 확인
```bash
# GitHub CLI로 워크플로우 유효성 검사
gh workflow view frontend-ci
```

---

### 문제 12: npm ci 실패

**에러 메시지**:
```
npm ERR! Cannot read property 'match' of undefined
npm ERR! package-lock.json or npm-shrinkwrap.json missing
```

**원인**:
- package-lock.json 손상
- Node 버전 불일치

**해결 방법**:

**옵션 1**: npm install로 변경
```yaml
- name: Install dependencies
  run: npm install
  working-directory: ./frontend
```

**옵션 2**: 캐시 무효화
```yaml
- name: Clear npm cache
  run: npm cache clean --force
  working-directory: ./frontend

- name: Install dependencies
  run: npm ci
  working-directory: ./frontend
```

**옵션 3**: lock 파일 재생성
```bash
# 로컬에서
rm package-lock.json
npm install
git add package-lock.json
git commit -m "chore: regenerate package-lock.json"
```

---

### 문제 13: 캐시가 작동하지 않음

**원인**:
- 캐시 키가 변경되지 않음
- 캐시 경로 오류

**해결 방법**:

**Step 1**: 캐시 키 확인
```yaml
- uses: actions/cache@v3
  with:
    path: ~/.m2
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
    restore-keys: |
      ${{ runner.os }}-maven-
```

**Step 2**: 캐시 수동 삭제
```
GitHub → Actions → Caches → Delete old caches
```

**Step 3**: 캐시 디버깅
```yaml
- name: Debug cache
  run: |
    echo "Cache key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}"
    ls -la ~/.m2
```

---

### 문제 14: 테스트 타임아웃

**에러 메시지**:
```
Error: The operation was canceled.
```

**원인**:
- 테스트가 너무 오래 걸림
- 무한 루프

**해결 방법**:

**Step 1**: 타임아웃 증가
```yaml
- name: Run tests
  run: npm run test:run
  timeout-minutes: 10
  working-directory: ./frontend
```

**Step 2**: 개별 테스트 타임아웃
```typescript
// vitest.config.ts
export default defineConfig({
  test: {
    testTimeout: 30000, // 30초
    hookTimeout: 30000,
  },
});
```

**Step 3**: 느린 테스트 식별
```bash
npm run test:run -- --reporter=verbose
```

---

## 성능 문제

### 문제 15: CI 빌드가 너무 느림

**원인**:
- 캐싱 부족
- 불필요한 단계
- 병렬 실행 안 함

**해결 방법**:

**Step 1**: 캐싱 최대화
```yaml
# Maven 캐시
- uses: actions/cache@v3
  with:
    path: ~/.m2
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}

# Node 캐시
- uses: actions/setup-node@v3
  with:
    cache: 'npm'
    cache-dependency-path: frontend/package-lock.json
```

**Step 2**: 병렬 실행
```yaml
jobs:
  test:
    strategy:
      matrix:
        module: [backend, frontend]
    steps:
      - name: Test ${{ matrix.module }}
        run: # ...
```

**Step 3**: 불필요한 단계 제거
```yaml
# 아티팩트 업로드는 main 브랜치만
- name: Upload artifact
  if: github.ref == 'refs/heads/main'
  uses: actions/upload-artifact@v3
```

---

### 문제 16: 테스트 실행이 느림

**원인**:
- 너무 많은 통합 테스트
- 단위 테스트 부족

**해결 방법**:

**Step 1**: 테스트 분류
```typescript
// 빠른 단위 테스트
describe('Unit: Utils', () => {
  it('formats currency', () => {
    expect(formatCurrency(1000)).toBe('₩1,000');
  });
});

// 느린 통합 테스트
describe.skip('Integration: API', () => {
  it('fetches data from server', async () => {
    // ...
  });
});
```

**Step 2**: 테스트 병렬 실행
```typescript
// vitest.config.ts
export default defineConfig({
  test: {
    pool: 'threads',
    poolOptions: {
      threads: {
        maxThreads: 4,
      },
    },
  },
});
```

---

## 의존성 문제

### 문제 17: 의존성 버전 충돌

**에러 메시지**:
```
npm ERR! peer dep missing: react@^18.0.0
```

**해결 방법**:

**Step 1**: peer dependency 확인
```bash
npm install --legacy-peer-deps
```

**Step 2**: package.json 업데이트
```json
{
  "peerDependencies": {
    "react": "^18.0.0"
  }
}
```

---

### 문제 18: 보안 취약점 경고

**에러 메시지**:
```
6 vulnerabilities (2 moderate, 4 high)
```

**해결 방법**:

**Step 1**: 취약점 확인
```bash
npm audit
```

**Step 2**: 자동 수정
```bash
npm audit fix
```

**Step 3**: 강제 수정 (주의)
```bash
npm audit fix --force
```

---

## 🆘 추가 도움이 필요한 경우

### 1. 로그 수집
```bash
# Backend 상세 로그
mvn test -X > test-output.log 2>&1

# Frontend 상세 로그
npm run test:run -- --reporter=verbose > test-output.log 2>&1
```

### 2. 이슈 생성
- GitHub Issues에 문제 보고
- 로그 파일 첨부
- 재현 단계 명시

### 3. 참고 자료
- [Vitest 문제 해결](https://vitest.dev/guide/debugging)
- [Maven Surefire FAQ](https://maven.apache.org/surefire/maven-surefire-plugin/faq.html)
- [GitHub Actions 디버깅](https://docs.github.com/en/actions/monitoring-and-troubleshooting-workflows/enabling-debug-logging)

---

**작성일**: 2025-11-21
**버전**: 1.0
**최종 업데이트**: 2025-11-21
