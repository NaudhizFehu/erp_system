# PWA 플러그인 단계적 복구 계획

**작성일**: 2025-01-15  
**목적**: PWA 플러그인 오류 해결 및 단계적 복구  
**중요도**: 🔴 High (모바일 지원 필수)  
**상태**: 📋 계획 단계

## 🎯 PWA 플러그인 복구의 중요성

### 📱 모바일 지원의 필요성
- **ERP 시스템 특성**: 직원들이 다양한 디바이스에서 접근 필요
- **사용자 경험**: 모바일에서 네이티브 앱과 유사한 경험 제공
- **오프라인 지원**: 네트워크 불안정 환경에서도 기본 기능 사용 가능
- **성능 향상**: 캐싱을 통한 빠른 로딩 및 서버 부하 감소

### 🚨 현재 문제 상황
```bash
TypeError: Cannot read properties of undefined (reading 'properties')
    at workbox-build/node_modules/@apideck/better-ajv-errors/dist/better-ajv-errors.cjs.production.min.js:1:1730
```

**오류 원인 분석**:
- `workbox-build` 라이브러리의 스키마 검증 실패
- PWA 설정의 일부 옵션이 예상 형식과 불일치
- `cacheKeyWillBeUsed` 비동기 함수 처리 문제 가능성

## 📋 단계적 복구 계획

### 🎯 Phase 1: 기본 PWA 설정 복구

#### 1.1 목표
- PWA 플러그인 기본 기능 활성화
- 빌드 오류 해결
- Service Worker 및 Manifest 생성 확인

#### 1.2 작업 내용

**Step 1: 기본 설정으로 PWA 플러그인 활성화**

```typescript
// frontend/vite.config.ts
VitePWA({
  registerType: 'autoUpdate',
  workbox: {
    globPatterns: ['**/*.{js,css,html,ico,png,svg}'],
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
})
```

**Step 2: 빌드 테스트**
```bash
cd frontend
npm run build
```

**Step 3: 예상 결과**
- ✅ 빌드 성공
- ✅ Service Worker 파일 생성 (`dist/sw.js`)
- ✅ Manifest 파일 생성 (`dist/manifest.webmanifest`)
- ✅ PWA 아이콘 파일 확인

#### 1.3 검증 체크리스트
- [ ] TypeScript 컴파일 성공
- [ ] Vite 빌드 성공
- [ ] Service Worker 파일 생성 확인
- [ ] Manifest 파일 생성 확인
- [ ] PWA 아이콘 파일 존재 확인

---

### 🎯 Phase 2: 캐싱 전략 단순화

#### 2.1 목표
- 기본 캐싱 전략 구현
- API 캐싱 없이 정적 리소스만 캐싱
- 안정적인 오프라인 지원

#### 2.2 작업 내용

**Step 1: 기본 캐싱 전략 추가**

```typescript
VitePWA({
  registerType: 'autoUpdate',
  workbox: {
    globPatterns: ['**/*.{js,css,html,ico,png,svg}'],
    runtimeCaching: [
      {
        urlPattern: /^https:\/\/fonts\.googleapis\.com\/.*/i,
        handler: 'CacheFirst',
        options: {
          cacheName: 'google-fonts-cache',
          expiration: {
            maxEntries: 10,
            maxAgeSeconds: 60 * 60 * 24 * 365, // 1년
          },
        },
      },
      {
        urlPattern: /^https:\/\/fonts\.gstatic\.com\/.*/i,
        handler: 'CacheFirst',
        options: {
          cacheName: 'google-fonts-stylesheets',
          expiration: {
            maxEntries: 10,
            maxAgeSeconds: 60 * 60 * 24 * 365, // 1년
          },
        },
      },
    ],
  },
  manifest: {
    // ... 이전과 동일
  },
})
```

**Step 2: 빌드 테스트**
```bash
npm run build
```

**Step 3: 개발 서버 테스트**
```bash
npm run dev
```

#### 2.3 검증 체크리스트
- [ ] 빌드 성공
- [ ] 개발 서버 정상 실행
- [ ] 브라우저에서 Service Worker 등록 확인
- [ ] 오프라인 모드에서 기본 페이지 로딩 확인

---

### 🎯 Phase 3: API 캐싱 전략 구현

#### 3.1 목표
- API 응답 캐싱 구현
- 네트워크 요청 최적화
- 오프라인에서 캐시된 API 데이터 사용

#### 3.2 작업 내용

**Step 1: API 캐싱 전략 추가**

```typescript
VitePWA({
  registerType: 'autoUpdate',
  workbox: {
    globPatterns: ['**/*.{js,css,html,ico,png,svg}'],
    runtimeCaching: [
      // 정적 리소스 캐싱 (Phase 2에서 추가한 내용)
      {
        urlPattern: /^https:\/\/fonts\.googleapis\.com\/.*/i,
        handler: 'CacheFirst',
        options: {
          cacheName: 'google-fonts-cache',
          expiration: {
            maxEntries: 10,
            maxAgeSeconds: 60 * 60 * 24 * 365,
          },
        },
      },
      // API 캐싱 전략 추가
      {
        urlPattern: /^http:\/\/localhost:9961\/api\/.*/i,
        handler: 'NetworkFirst',
        options: {
          cacheName: 'api-cache',
          expiration: {
            maxEntries: 50,
            maxAgeSeconds: 60 * 60 * 24, // 24시간
          },
          networkTimeoutSeconds: 3,
        },
      },
      {
        urlPattern: /^https:\/\/api\.erp-system\.com\/.*/i,
        handler: 'NetworkFirst',
        options: {
          cacheName: 'api-cache',
          expiration: {
            maxEntries: 50,
            maxAgeSeconds: 60 * 60 * 24, // 24시간
          },
          networkTimeoutSeconds: 3,
        },
      },
    ],
  },
  manifest: {
    // ... 이전과 동일
  },
})
```

**Step 2: 빌드 테스트**
```bash
npm run build
```

**Step 3: API 캐싱 테스트**
```bash
npm run dev
# 브라우저에서 Network 탭에서 API 요청 캐싱 확인
```

#### 3.3 검증 체크리스트
- [ ] 빌드 성공
- [ ] API 요청 캐싱 확인
- [ ] 오프라인에서 캐시된 API 데이터 사용 확인
- [ ] 네트워크 복구 시 자동 업데이트 확인

---

### 🎯 Phase 4: 고급 캐싱 전략 구현

#### 4.1 목표
- 세분화된 캐싱 전략 구현
- 사용자 경험 최적화
- 백그라운드 동기화

#### 4.2 작업 내용

**Step 1: 고급 캐싱 전략 추가**

```typescript
VitePWA({
  registerType: 'autoUpdate',
  workbox: {
    globPatterns: ['**/*.{js,css,html,ico,png,svg}'],
    runtimeCaching: [
      // 정적 리소스 캐싱
      {
        urlPattern: /^https:\/\/fonts\.googleapis\.com\/.*/i,
        handler: 'CacheFirst',
        options: {
          cacheName: 'google-fonts-cache',
          expiration: {
            maxEntries: 10,
            maxAgeSeconds: 60 * 60 * 24 * 365,
          },
        },
      },
      // API 캐싱 - 읽기 전용 데이터 (StaleWhileRevalidate)
      {
        urlPattern: /^http:\/\/localhost:9961\/api\/hr\/departments\/.*/i,
        handler: 'StaleWhileRevalidate',
        options: {
          cacheName: 'hr-data-cache',
          expiration: {
            maxEntries: 100,
            maxAgeSeconds: 60 * 60 * 24, // 24시간
          },
        },
      },
      {
        urlPattern: /^http:\/\/localhost:9961\/api\/hr\/positions\/.*/i,
        handler: 'StaleWhileRevalidate',
        options: {
          cacheName: 'hr-data-cache',
          expiration: {
            maxEntries: 100,
            maxAgeSeconds: 60 * 60 * 24, // 24시간
          },
        },
      },
      // API 캐싱 - 실시간 데이터 (NetworkFirst)
      {
        urlPattern: /^http:\/\/localhost:9961\/api\/hr\/employees\/.*/i,
        handler: 'NetworkFirst',
        options: {
          cacheName: 'employee-data-cache',
          expiration: {
            maxEntries: 200,
            maxAgeSeconds: 60 * 60 * 2, // 2시간
          },
          networkTimeoutSeconds: 3,
        },
      },
      // API 캐싱 - 일반 API (NetworkFirst)
      {
        urlPattern: /^http:\/\/localhost:9961\/api\/.*/i,
        handler: 'NetworkFirst',
        options: {
          cacheName: 'api-cache',
          expiration: {
            maxEntries: 50,
            maxAgeSeconds: 60 * 60 * 24, // 24시간
          },
          networkTimeoutSeconds: 3,
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
    orientation: 'portrait-primary',
    scope: '/',
    start_url: '/',
    icons: [
      {
        src: 'pwa-192x192.png',
        sizes: '192x192',
        type: 'image/png',
        purpose: 'any maskable',
      },
      {
        src: 'pwa-512x512.png',
        sizes: '512x512',
        type: 'image/png',
        purpose: 'any maskable',
      },
    ],
  },
})
```

**Step 2: 백그라운드 동기화 추가**

```typescript
// 추가 설정 (선택사항)
VitePWA({
  // ... 기존 설정
  workbox: {
    // ... 기존 설정
    skipWaiting: true,
    clientsClaim: true,
    cleanupOutdatedCaches: true,
  },
})
```

#### 4.3 검증 체크리스트
- [ ] 빌드 성공
- [ ] 다양한 캐싱 전략 동작 확인
- [ ] 오프라인/온라인 전환 테스트
- [ ] 백그라운드 업데이트 확인
- [ ] 캐시 크기 및 만료 시간 확인

---

## 🛠️ 문제 해결 가이드

### 🚨 Phase 1에서 오류 발생 시

#### 오류 1: "Cannot read properties of undefined"
**원인**: PWA 아이콘 파일이 존재하지 않음  
**해결방법**:
```bash
# PWA 아이콘 파일 생성 또는 제거
# Option 1: 아이콘 파일 생성
mkdir -p frontend/public
# PWA 아이콘 파일들을 public 폴더에 추가

# Option 2: 아이콘 설정 제거 (임시)
manifest: {
  name: 'Cursor ERP System',
  short_name: 'ERP System',
  theme_color: '#3b82f6',
  background_color: '#ffffff',
  display: 'standalone',
  // icons 설정 제거
}
```

#### 오류 2: "workbox-build validation error"
**원인**: workbox 설정 스키마 오류  
**해결방법**:
```typescript
// 가장 간단한 설정으로 시작
VitePWA({
  registerType: 'autoUpdate',
  // workbox 설정 제거하고 기본값 사용
  manifest: {
    name: 'Cursor ERP System',
    short_name: 'ERP System',
    theme_color: '#3b82f6',
    background_color: '#ffffff',
    display: 'standalone',
  },
})
```

### 🚨 Phase 2-4에서 오류 발생 시

#### 오류 1: "urlPattern is not a valid RegExp"
**원인**: URL 패턴 정규식 오류  
**해결방법**:
```typescript
// 잘못된 패턴
urlPattern: /^http:\/\/localhost:9961\/api\/.*/i,

// 올바른 패턴
urlPattern: /^http:\/\/localhost:9961\/api\/.*/i,
```

#### 오류 2: "cacheName must be a string"
**원인**: 캐시 이름 설정 오류  
**해결방법**:
```typescript
// 잘못된 설정
options: {
  cacheName: 123, // 숫자
}

// 올바른 설정
options: {
  cacheName: 'api-cache', // 문자열
}
```

## 📊 테스트 시나리오

### 🧪 Phase 1 테스트
1. **빌드 테스트**: `npm run build`
2. **개발 서버 테스트**: `npm run dev`
3. **PWA 설치 테스트**: 브라우저에서 "앱 설치" 버튼 확인
4. **Service Worker 등록 확인**: DevTools > Application > Service Workers

### 🧪 Phase 2 테스트
1. **오프라인 테스트**: DevTools > Network > Offline 체크
2. **캐시 확인**: DevTools > Application > Storage > Cache Storage
3. **정적 리소스 캐싱 확인**: 페이지 새로고침 시 빠른 로딩

### 🧪 Phase 3 테스트
1. **API 캐싱 테스트**: API 호출 후 오프라인에서 동일 데이터 사용
2. **네트워크 복구 테스트**: 오프라인 → 온라인 전환 시 자동 업데이트
3. **캐시 만료 테스트**: 설정된 시간 후 캐시 갱신 확인

### 🧪 Phase 4 테스트
1. **다양한 캐싱 전략 테스트**: 각 API 엔드포인트별 캐싱 동작 확인
2. **백그라운드 동기화 테스트**: 앱이 백그라운드에 있을 때 업데이트 확인
3. **성능 테스트**: 캐싱으로 인한 성능 향상 측정

## 📅 실행 일정

### 🗓️ Week 1: Phase 1-2
- **Day 1-2**: Phase 1 (기본 PWA 설정 복구)
- **Day 3-4**: Phase 2 (기본 캐싱 전략 구현)
- **Day 5**: 테스트 및 검증

### 🗓️ Week 2: Phase 3-4
- **Day 1-2**: Phase 3 (API 캐싱 전략 구현)
- **Day 3-4**: Phase 4 (고급 캐싱 전략 구현)
- **Day 5**: 종합 테스트 및 최적화

## 🎯 성공 기준

### ✅ Phase 1 성공 기준
- [ ] PWA 플러그인이 오류 없이 빌드됨
- [ ] Service Worker가 정상적으로 등록됨
- [ ] Manifest 파일이 생성되어 PWA 설치 가능

### ✅ Phase 2 성공 기준
- [ ] 오프라인에서 기본 페이지 로딩 가능
- [ ] 정적 리소스가 적절히 캐싱됨
- [ ] 네트워크 복구 시 자동 업데이트

### ✅ Phase 3 성공 기준
- [ ] API 응답이 적절히 캐싱됨
- [ ] 오프라인에서 캐시된 API 데이터 사용 가능
- [ ] 네트워크 우선 전략이 정상 동작

### ✅ Phase 4 성공 기준
- [ ] 다양한 캐싱 전략이 각각 정상 동작
- [ ] 백그라운드 동기화가 정상 작동
- [ ] 전체적인 성능이 향상됨

## 📝 결론

PWA 플러그인 복구는 **모바일 사용자 경험 향상**에 필수적입니다. 단계적 접근을 통해 안정적으로 복구하고, 각 단계마다 충분한 테스트를 거쳐 최종적으로 **완전한 PWA 기능**을 구현할 수 있습니다.

**핵심 포인트**:
- 🔄 단계별 접근으로 리스크 최소화
- 🧪 각 단계마다 충분한 테스트
- 📊 성능 및 사용자 경험 지속적 모니터링
- 🛠️ 문제 발생 시 즉시 롤백 가능한 구조

