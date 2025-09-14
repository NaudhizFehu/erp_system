import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { VitePWA } from 'vite-plugin-pwa'
import tsconfigPaths from 'vite-tsconfig-paths'
import path from 'path'

/**
 * Vite 빌드 도구 설정
 * React + TypeScript 프로젝트를 위한 개발 및 빌드 환경 구성
 */
export default defineConfig({
  plugins: [
    // React 플러그인
    react(),
    
    // TypeScript 경로 매핑 지원
    tsconfigPaths(),
    
    // PWA 플러그인 (Progressive Web App)
    VitePWA({
      registerType: 'autoUpdate',
      workbox: {
        globPatterns: ['**/*.{js,css,html,ico,png,svg}'],
        runtimeCaching: [
          {
            urlPattern: /^https:\/\/api\.erp-system\.com\/.*/i,
            handler: 'CacheFirst',
            options: {
              cacheName: 'api-cache',
              expiration: {
                maxEntries: 10,
                maxAgeSeconds: 60 * 60 * 24 * 365, // 1년
              },
              cacheKeyWillBeUsed: async ({ request }) => {
                return `${request.url}?version=1.0.0`
              },
            },
          },
        ],
      },
      manifest: {
        name: 'Cursor ERP System',
        short_name: 'ERP System',
        description: '통합 기업 자원 관리 시스템',
        theme_color: '#3b82f6',
        background_color: '#ffffff',
        display: 'standalone',
        icons: [
          {
            src: 'pwa-192x192.png',
            sizes: '192x192',
            type: 'image/png',
          },
          {
            src: 'pwa-512x512.png',
            sizes: '512x512',
            type: 'image/png',
          },
        ],
      },
    }),
  ],
  
  // 경로 별칭 설정
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@components': path.resolve(__dirname, './src/components'),
      '@pages': path.resolve(__dirname, './src/pages'),
      '@services': path.resolve(__dirname, './src/services'),
      '@types': path.resolve(__dirname, './src/types'),
      '@utils': path.resolve(__dirname, './src/lib'),
      '@hooks': path.resolve(__dirname, './src/hooks'),
      '@assets': path.resolve(__dirname, './src/assets'),
    },
  },
  
  // 개발 서버 설정
  server: {
    port: 3000,
    host: true, // 네트워크 접근 허용
    open: true, // 브라우저 자동 열기
    cors: true,
    proxy: {
      // API 프록시 설정
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '/api'),
      },
    },
  },
  
  // 빌드 설정
  build: {
    outDir: 'dist',
    sourcemap: true,
    minify: 'esbuild',
    target: 'es2020',
    
    // 청크 분할 최적화
    rollupOptions: {
      output: {
        manualChunks: {
          // React 관련 라이브러리
          'react-vendor': ['react', 'react-dom', 'react-router-dom'],
          
          // UI 라이브러리
          'ui-vendor': [
            '@radix-ui/react-dialog',
            '@radix-ui/react-dropdown-menu',
            '@radix-ui/react-select',
            '@radix-ui/react-tabs',
          ],
          
          // 데이터 관련 라이브러리
          'data-vendor': ['@tanstack/react-query', 'axios', 'zustand'],
          
          // 폼 관련 라이브러리
          'form-vendor': ['react-hook-form', '@hookform/resolvers', 'zod'],
          
          // 유틸리티 라이브러리
          'utils-vendor': ['date-fns', 'lodash-es', 'clsx'],
        },
      },
    },
    
    // 빌드 성능 최적화
    chunkSizeWarningLimit: 1000,
    
    // 에셋 처리
    assetsDir: 'assets',
    assetsInlineLimit: 4096,
  },
  
  // 미리보기 서버 설정
  preview: {
    port: 4173,
    host: true,
    cors: true,
  },
  
  // CSS 처리 설정
  css: {
    devSourcemap: true,
    preprocessorOptions: {
      scss: {
        additionalData: `@import "@/styles/variables.scss";`,
      },
    },
  },
  
  // 최적화 설정
  optimizeDeps: {
    include: [
      'react',
      'react-dom',
      'react-router-dom',
      '@tanstack/react-query',
      'axios',
      'zustand',
    ],
    exclude: ['@vite/client', '@vite/env'],
  },
  
  // 환경 변수 설정
  define: {
    __APP_VERSION__: JSON.stringify(process.env.npm_package_version),
    __BUILD_DATE__: JSON.stringify(new Date().toISOString()),
  },
})
