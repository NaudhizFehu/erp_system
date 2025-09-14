import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'
import path from 'path'

/**
 * Vitest 테스트 환경 설정
 * React + TypeScript 프로젝트를 위한 단위 테스트 설정
 */
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  test: {
    // 테스트 환경 설정
    environment: 'jsdom',
    
    // 전역 설정
    globals: true,
    
    // 테스트 셋업 파일
    setupFiles: ['./src/test/setup.ts'],
    
    // 테스트 파일 패턴
    include: [
      'src/**/*.{test,spec}.{js,ts,jsx,tsx}',
      'src/**/__tests__/**/*.{js,ts,jsx,tsx}',
    ],
    
    // 제외할 파일 패턴
    exclude: [
      'node_modules',
      'dist',
      '.idea',
      '.git',
      '.cache',
      'build',
    ],
    
    // 커버리지 설정
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'src/test/',
        '**/*.d.ts',
        '**/*.config.{js,ts}',
        '**/index.ts',
        'src/main.tsx',
        'src/vite-env.d.ts',
      ],
      thresholds: {
        global: {
          branches: 80,
          functions: 80,
          lines: 80,
          statements: 80,
        },
      },
    },
    
    // 테스트 실행 설정
    testTimeout: 10000,
    hookTimeout: 10000,
    
    // 모의(Mock) 설정
    clearMocks: true,
    mockReset: true,
    restoreMocks: true,
    
    // 병렬 실행 설정
    pool: 'threads',
    poolOptions: {
      threads: {
        singleThread: false,
      },
    },
    
    // 리포터 설정
    reporter: ['verbose', 'html'],
    outputFile: {
      html: './coverage/test-results.html',
    },
  },
})





