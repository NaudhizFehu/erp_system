/**
 * 환경 변수 설정
 * Vite 환경 변수를 타입 안전하게 관리합니다
 */

interface AppConfig {
  title: string
  version: string
  apiBaseUrl: string
  debug: boolean
  logLevel: 'debug' | 'info' | 'warn' | 'error'
  features: {
    analytics: boolean
    notifications: boolean
  }
  upload: {
    maxFileSize: number
    allowedTypes: string[]
  }
  pagination: {
    defaultPageSize: number
    maxPageSize: number
  }
}

/**
 * 환경 변수에서 불린 값을 파싱하는 함수
 */
function parseBoolean(value: string | undefined, defaultValue: boolean = false): boolean {
  if (!value) return defaultValue
  return value.toLowerCase() === 'true'
}

/**
 * 환경 변수에서 숫자 값을 파싱하는 함수
 */
function parseNumber(value: string | undefined, defaultValue: number): number {
  if (!value) return defaultValue
  const parsed = parseInt(value, 10)
  return isNaN(parsed) ? defaultValue : parsed
}

/**
 * 환경 변수에서 배열 값을 파싱하는 함수
 */
function parseArray(value: string | undefined, defaultValue: string[] = []): string[] {
  if (!value) return defaultValue
  return value.split(',').map(item => item.trim()).filter(Boolean)
}

/**
 * 애플리케이션 설정
 */
export const config: AppConfig = {
  title: import.meta.env.VITE_APP_TITLE || 'ERP 시스템',
  version: import.meta.env.VITE_APP_VERSION || '1.0.0',
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  debug: parseBoolean(import.meta.env.VITE_DEBUG, false),
  logLevel: (import.meta.env.VITE_LOG_LEVEL as AppConfig['logLevel']) || 'info',
  
  features: {
    analytics: parseBoolean(import.meta.env.VITE_FEATURE_ANALYTICS, false),
    notifications: parseBoolean(import.meta.env.VITE_FEATURE_NOTIFICATIONS, true),
  },
  
  upload: {
    maxFileSize: parseNumber(import.meta.env.VITE_MAX_FILE_SIZE, 10485760), // 10MB
    allowedTypes: parseArray(
      import.meta.env.VITE_ALLOWED_FILE_TYPES,
      ['.jpg', '.jpeg', '.png', '.pdf', '.doc', '.docx', '.xls', '.xlsx']
    ),
  },
  
  pagination: {
    defaultPageSize: parseNumber(import.meta.env.VITE_DEFAULT_PAGE_SIZE, 20),
    maxPageSize: parseNumber(import.meta.env.VITE_MAX_PAGE_SIZE, 100),
  },
}

/**
 * 개발 모드 여부
 */
export const isDevelopment = import.meta.env.DEV

/**
 * 프로덕션 모드 여부
 */
export const isProduction = import.meta.env.PROD

/**
 * 빌드 모드
 */
export const mode = import.meta.env.MODE

/**
 * 디버그 로그 출력 함수
 */
export function debugLog(message: string, ...args: any[]) {
  if (config.debug && config.logLevel === 'debug') {
    console.log(`[DEBUG] ${message}`, ...args)
  }
}

/**
 * 설정 검증 함수
 */
export function validateConfig(): void {
  const errors: string[] = []
  
  if (!config.apiBaseUrl) {
    errors.push('API_BASE_URL이 설정되지 않았습니다')
  }
  
  if (config.pagination.defaultPageSize > config.pagination.maxPageSize) {
    errors.push('기본 페이지 크기가 최대 페이지 크기보다 클 수 없습니다')
  }
  
  if (errors.length > 0) {
    throw new Error(`설정 오류: ${errors.join(', ')}`)
  }
}

// 애플리케이션 시작 시 설정 검증
validateConfig()





