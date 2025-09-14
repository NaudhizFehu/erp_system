/**
 * 테스트 환경 설정 파일
 * 모든 테스트 실행 전에 로드되는 전역 설정
 */

import '@testing-library/jest-dom'

// React Query 테스트를 위한 설정
import { QueryClient } from '@tanstack/react-query'

// 전역 모의 함수들
global.ResizeObserver = vi.fn().mockImplementation(() => ({
  observe: vi.fn(),
  unobserve: vi.fn(),
  disconnect: vi.fn(),
}))

// IntersectionObserver 모의 함수
global.IntersectionObserver = vi.fn().mockImplementation(() => ({
  observe: vi.fn(),
  unobserve: vi.fn(),
  disconnect: vi.fn(),
}))

// matchMedia 모의 함수 (반응형 디자인 테스트용)
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(), // deprecated
    removeListener: vi.fn(), // deprecated
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
})

// 로컬 스토리지 모의 함수
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
}
Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
})

// 세션 스토리지 모의 함수
const sessionStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
}
Object.defineProperty(window, 'sessionStorage', {
  value: sessionStorageMock,
})

// React Query 테스트 유틸리티
export const createTestQueryClient = () =>
  new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
        gcTime: Infinity,
      },
      mutations: {
        retry: false,
      },
    },
  })

// 테스트 전후 정리 작업
beforeEach(() => {
  // 각 테스트 전에 모의 함수들 초기화
  vi.clearAllMocks()
  localStorageMock.clear()
  sessionStorageMock.clear()
})

afterEach(() => {
  // 각 테스트 후 정리 작업
  vi.resetAllMocks()
})

// 전역 에러 핸들링
process.on('unhandledRejection', (reason) => {
  console.error('테스트 중 처리되지 않은 Promise 거부:', reason)
})

// 콘솔 에러 억제 (테스트 환경에서 의도적인 에러 테스트 시)
const originalError = console.error
beforeAll(() => {
  console.error = (...args: any[]) => {
    if (
      typeof args[0] === 'string' &&
      args[0].includes('Warning: ReactDOM.render is no longer supported')
    ) {
      return
    }
    originalError.call(console, ...args)
  }
})

afterAll(() => {
  console.error = originalError
})





