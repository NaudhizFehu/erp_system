/// <reference types="vite/client" />
/// <reference types="vitest/globals" />

/**
 * Vite 환경 변수 타입 정의
 * 환경 변수의 타입 안전성을 보장합니다
 */
interface ImportMetaEnv {
  /** 애플리케이션 제목 */
  readonly VITE_APP_TITLE: string
  
  /** API 기본 URL */
  readonly VITE_API_BASE_URL: string
  
  /** 애플리케이션 버전 */
  readonly VITE_APP_VERSION: string
  
  /** 디버그 모드 활성화 여부 */
  readonly VITE_DEBUG: string
  
  /** 로그 레벨 */
  readonly VITE_LOG_LEVEL: 'debug' | 'info' | 'warn' | 'error'
  
  /** 기능 플래그: 분석 도구 */
  readonly VITE_FEATURE_ANALYTICS: string
  
  /** 기능 플래그: 알림 */
  readonly VITE_FEATURE_NOTIFICATIONS: string
  
  /** 최대 파일 크기 (바이트) */
  readonly VITE_MAX_FILE_SIZE: string
  
  /** 허용된 파일 타입 */
  readonly VITE_ALLOWED_FILE_TYPES: string
  
  /** 기본 페이지 크기 */
  readonly VITE_DEFAULT_PAGE_SIZE: string
  
  /** 최대 페이지 크기 */
  readonly VITE_MAX_PAGE_SIZE: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

/**
 * 전역 상수 타입 정의
 */
declare const __APP_VERSION__: string
declare const __BUILD_DATE__: string

/**
 * React 관련 타입 확장
 */
declare module '*.svg' {
  import React = require('react')
  export const ReactComponent: React.FC<React.SVGProps<SVGSVGElement>>
  const src: string
  export default src
}

declare module '*.png' {
  const content: string
  export default content
}

declare module '*.jpg' {
  const content: string
  export default content
}

declare module '*.jpeg' {
  const content: string
  export default content
}

declare module '*.gif' {
  const content: string
  export default content
}

declare module '*.webp' {
  const content: string
  export default content
}

declare module '*.ico' {
  const content: string
  export default content
}

declare module '*.bmp' {
  const content: string
  export default content
}

/**
 * CSS 모듈 타입 정의
 */
declare module '*.module.css' {
  const classes: { readonly [key: string]: string }
  export default classes
}

declare module '*.module.scss' {
  const classes: { readonly [key: string]: string }
  export default classes
}

declare module '*.module.sass' {
  const classes: { readonly [key: string]: string }
  export default classes
}

/**
 * JSON 파일 타입 정의
 */
declare module '*.json' {
  const content: { [key: string]: any }
  export default content
}

/**
 * Web Workers 타입 정의
 */
declare module '*?worker' {
  const workerConstructor: {
    new (): Worker
  }
  export default workerConstructor
}

declare module '*?worker&inline' {
  const workerConstructor: {
    new (): Worker
  }
  export default workerConstructor
}





