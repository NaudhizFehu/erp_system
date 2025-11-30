# Phase 2: Frontend 테스트 설정

**예상 시간**: 45분
**난이도**: 중간
**담당**: Frontend

---

## 📋 목표

Vitest를 사용하여 Frontend 테스트 환경을 구축하고, 샘플 테스트를 작성하여 로컬에서 실행 가능하도록 설정

---

## 🔍 현재 상태 분석

### 확인 사항
1. `frontend/src/test/setup.ts` - 이미 존재하는지 확인
2. `frontend/vitest.config.ts` - 없음 (새로 생성 필요)
3. `frontend/package.json` - Vitest 의존성 없음

---

## 🛠️ 구현 단계

### Step 1: Vitest 및 테스트 라이브러리 설치

```bash
cd frontend

# Vitest 및 테스트 관련 패키지 설치
npm install -D vitest @vitest/ui jsdom

# React Testing Library
npm install -D @testing-library/react @testing-library/jest-dom @testing-library/user-event

# Coverage 도구 (선택)
npm install -D @vitest/coverage-v8
```

**설치되는 패키지**:
- `vitest`: 테스트 프레임워크
- `@vitest/ui`: 브라우저 기반 테스트 UI
- `jsdom`: DOM 환경 시뮬레이션
- `@testing-library/react`: React 컴포넌트 테스트
- `@testing-library/jest-dom`: DOM 매처 확장
- `@testing-library/user-event`: 사용자 이벤트 시뮬레이션
- `@vitest/coverage-v8`: 코드 커버리지 측정

---

### Step 2: package.json 스크립트 추가

**파일**: `frontend/package.json`

```json
{
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "lint": "eslint .",
    "preview": "vite preview",
    "type-check": "tsc --noEmit",

    // 추가할 스크립트
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:run": "vitest run",
    "test:coverage": "vitest --coverage"
  }
}
```

**스크립트 설명**:
- `test`: Watch 모드로 테스트 실행 (파일 변경 감지)
- `test:ui`: 브라우저 UI로 테스트 실행
- `test:run`: 한 번만 실행 (CI 용)
- `test:coverage`: 커버리지 리포트 생성

---

### Step 3: Vitest 설정 파일 생성

**파일**: `frontend/vitest.config.ts` (신규)

```typescript
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  test: {
    // 전역 설정
    globals: true,

    // 테스트 환경 (jsdom = 브라우저 환경 시뮬레이션)
    environment: 'jsdom',

    // 테스트 실행 전 setup 파일
    setupFiles: './src/test/setup.ts',

    // CSS 처리 활성화
    css: true,

    // 커버리지 설정
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov'],
      exclude: [
        'node_modules/',
        'src/test/',
        '**/*.d.ts',
        '**/*.config.*',
        '**/mockData',
        '**/mocks',
        'dist/',
        'build/',
      ],
      // 커버리지 임계값 (선택)
      thresholds: {
        lines: 60,
        functions: 60,
        branches: 60,
        statements: 60,
      },
    },

    // 테스트 타임아웃 (밀리초)
    testTimeout: 10000,
    hookTimeout: 10000,
  },

  // 경로 별칭 설정
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
});
```

---

### Step 4: 테스트 Setup 파일 확인/생성

**파일**: `frontend/src/test/setup.ts`

**이미 존재하는 경우**: 내용 확인

**없는 경우**: 새로 생성

```typescript
import '@testing-library/jest-dom';
import { cleanup } from '@testing-library/react';
import { afterEach } from 'vitest';

// 각 테스트 후 자동 정리
afterEach(() => {
  cleanup();
});

// 전역 mock 설정 (필요 시)
global.matchMedia = global.matchMedia || function () {
  return {
    matches: false,
    addListener: function () {},
    removeListener: function () {},
  };
};

// React Router mock (필요 시)
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => vi.fn(),
  };
});
```

---

### Step 5: 샘플 테스트 작성

#### 5.1 Button 컴포넌트 테스트

**파일**: `frontend/src/components/ui/button.test.tsx` (신규)

```typescript
import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { Button } from './button';

describe('Button Component', () => {
  it('renders button with text', () => {
    render(<Button>Click me</Button>);
    expect(screen.getByText('Click me')).toBeInTheDocument();
  });

  it('applies default variant classes', () => {
    render(<Button>Default</Button>);
    const button = screen.getByText('Default');
    expect(button).toHaveClass('bg-primary');
  });

  it('applies destructive variant classes', () => {
    render(<Button variant="destructive">Delete</Button>);
    const button = screen.getByText('Delete');
    expect(button).toHaveClass('bg-destructive');
  });

  it('applies size classes correctly', () => {
    render(<Button size="lg">Large Button</Button>);
    const button = screen.getByText('Large Button');
    expect(button).toHaveClass('h-11');
  });

  it('can be disabled', () => {
    render(<Button disabled>Disabled</Button>);
    const button = screen.getByText('Disabled');
    expect(button).toBeDisabled();
  });
});
```

#### 5.2 유틸리티 함수 테스트

**파일**: `frontend/src/lib/utils.test.ts` (신규)

```typescript
import { describe, it, expect } from 'vitest';
import { cn } from './utils';

describe('cn utility', () => {
  it('merges class names correctly', () => {
    const result = cn('bg-red-500', 'text-white');
    expect(result).toContain('bg-red-500');
    expect(result).toContain('text-white');
  });

  it('handles conditional classes', () => {
    const result = cn('base', true && 'conditional', false && 'hidden');
    expect(result).toContain('base');
    expect(result).toContain('conditional');
    expect(result).not.toContain('hidden');
  });
});
```

#### 5.3 Format 유틸리티 테스트

**파일**: `frontend/src/utils/format.test.ts` (신규)

```typescript
import { describe, it, expect } from 'vitest';
import { formatCurrency, formatDate, formatNumber } from './format';

describe('Format Utilities', () => {
  describe('formatCurrency', () => {
    it('formats Korean won correctly', () => {
      expect(formatCurrency(10000)).toBe('₩10,000');
      expect(formatCurrency(1234567)).toBe('₩1,234,567');
    });

    it('handles zero', () => {
      expect(formatCurrency(0)).toBe('₩0');
    });

    it('handles negative numbers', () => {
      expect(formatCurrency(-5000)).toBe('-₩5,000');
    });
  });

  describe('formatDate', () => {
    it('formats date correctly', () => {
      const date = new Date('2025-11-21');
      expect(formatDate(date)).toBe('2025-11-21');
    });
  });

  describe('formatNumber', () => {
    it('formats numbers with commas', () => {
      expect(formatNumber(1000)).toBe('1,000');
      expect(formatNumber(1234567)).toBe('1,234,567');
    });
  });
});
```

---

### Step 6: React Query 테스트 (선택)

React Query를 사용하는 경우:

**파일**: `frontend/src/test/utils.tsx` (신규)

```typescript
import { ReactElement } from 'react';
import { render, RenderOptions } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

// 테스트용 QueryClient 생성
const createTestQueryClient = () => new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
      cacheTime: 0,
    },
  },
});

// 테스트 wrapper
export function renderWithQueryClient(
  ui: ReactElement,
  options?: RenderOptions,
) {
  const testQueryClient = createTestQueryClient();

  return render(
    <QueryClientProvider client={testQueryClient}>
      {ui}
    </QueryClientProvider>,
    options
  );
}
```

**사용 예시**:
```typescript
import { renderWithQueryClient } from '@/test/utils';

it('fetches and displays data', async () => {
  renderWithQueryClient(<MyComponent />);
  // ... 테스트 로직
});
```

---

## 🧪 로컬 테스트 실행

### 1. Watch 모드로 테스트

```bash
npm run test
```

**동작**:
- 파일 변경 시 자동으로 관련 테스트 재실행
- 대화형 CLI 제공

### 2. 한 번만 실행 (CI 용)

```bash
npm run test:run
```

### 3. UI 모드로 실행

```bash
npm run test:ui
```

**동작**:
- 브라우저에서 http://localhost:51204/__vitest__/ 열림
- 시각적으로 테스트 결과 확인
- 개별 테스트 선택 실행 가능

### 4. 커버리지 확인

```bash
npm run test:coverage
```

**결과**:
```
--------------------------|---------|----------|---------|---------|
File                      | % Stmts | % Branch | % Funcs | % Lines |
--------------------------|---------|----------|---------|---------|
All files                 |   75.5  |   68.2   |   80.1  |   75.5  |
 components/ui            |   85.0  |   75.0   |   90.0  |   85.0  |
  button.tsx              |   90.0  |   80.0   |   95.0  |   90.0  |
 lib                      |   70.0  |   65.0   |   75.0  |   70.0  |
  utils.ts                |   70.0  |   65.0   |   75.0  |   70.0  |
--------------------------|---------|----------|---------|---------|
```

**HTML 리포트**: `frontend/coverage/index.html` 열기

---

## 📊 예상 package.json 변경사항

```json
{
  "name": "cursor-erp-frontend",
  "version": "0.1.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "lint": "eslint .",
    "preview": "vite preview",
    "type-check": "tsc --noEmit",
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:run": "vitest run",
    "test:coverage": "vitest --coverage"
  },
  "dependencies": {
    // ... 기존 의존성
  },
  "devDependencies": {
    // ... 기존 devDependencies
    "vitest": "^1.0.0",
    "@vitest/ui": "^1.0.0",
    "@vitest/coverage-v8": "^1.0.0",
    "jsdom": "^23.0.0",
    "@testing-library/react": "^14.1.0",
    "@testing-library/jest-dom": "^6.1.0",
    "@testing-library/user-event": "^14.5.0"
  }
}
```

---

## ⚠️ 문제 해결

### 문제 1: Path alias 인식 안 됨

**에러**:
```
Cannot find module '@/components/ui/button'
```

**해결**:
`vitest.config.ts`에 alias 추가 확인:
```typescript
resolve: {
  alias: {
    '@': path.resolve(__dirname, './src'),
  },
}
```

### 문제 2: CSS import 에러

**에러**:
```
Unknown file extension ".css"
```

**해결**:
`vitest.config.ts`에 `css: true` 추가

### 문제 3: React Router mock 필요

**에러**:
```
useNavigate must be used within a Router
```

**해결**:
`setup.ts`에 Router mock 추가:
```typescript
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

### 문제 4: matchMedia 에러

**에러**:
```
TypeError: window.matchMedia is not a function
```

**해결**:
`setup.ts`에 matchMedia mock 추가 (이미 포함됨)

---

## ✅ 체크리스트

### 필수 작업
- [ ] `cd frontend` 이동
- [ ] Vitest 및 테스트 라이브러리 설치
- [ ] `vitest.config.ts` 파일 생성
- [ ] `package.json`에 테스트 스크립트 추가
- [ ] `src/test/setup.ts` 확인/생성
- [ ] 최소 1개 샘플 테스트 작성
- [ ] `npm run test:run` 실행 성공

### 권장 작업
- [ ] Button 컴포넌트 테스트 작성
- [ ] Utils 유틸리티 테스트 작성
- [ ] Format 유틸리티 테스트 작성
- [ ] 커버리지 리포트 확인

### 선택 작업
- [ ] React Query 테스트 유틸리티 작성
- [ ] 커버리지 임계값 설정
- [ ] UI 모드로 테스트 실행

---

## 📈 예상 테스트 결과

```bash
$ npm run test:run

✓ src/components/ui/button.test.tsx (5)
  ✓ Button Component (5)
    ✓ renders button with text
    ✓ applies default variant classes
    ✓ applies destructive variant classes
    ✓ applies size classes correctly
    ✓ can be disabled

✓ src/lib/utils.test.ts (2)
  ✓ cn utility (2)
    ✓ merges class names correctly
    ✓ handles conditional classes

✓ src/utils/format.test.ts (7)
  ✓ Format Utilities (7)
    ✓ formatCurrency (3)
    ✓ formatDate (1)
    ✓ formatNumber (1)

Test Files  3 passed (3)
     Tests  14 passed (14)
  Start at  12:00:00
  Duration  1.23s
```

---

## 🔗 다음 단계

Phase 2 완료 후:
- **Phase 3**: Frontend CI 워크플로우 수정 (`04-frontend-ci-workflow.md`)

---

## 📚 참고 자료

- [Vitest 공식 문서](https://vitest.dev/)
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/)
- [Testing Library Queries](https://testing-library.com/docs/queries/about/)
- [Jest DOM Matchers](https://github.com/testing-library/jest-dom)

---

**작성일**: 2025-11-21
**버전**: 1.0
**상태**: 준비 완료
