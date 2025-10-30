# ERP 시스템 프론트엔드 하드코딩 데이터 목록

**작성일**: 2025-01-15  
**목적**: 프론트엔드에 하드코딩된 임시 데이터 파악 및 정리  
**상태**: 현재 하드코딩된 데이터 식별 완료

## 📋 개요

프론트엔드에서 하드코딩된 임시 데이터들을 파악하여 실제 API 연동으로 대체할 필요가 있는 데이터들을 정리했습니다.

## 🔍 하드코딩된 데이터 분류

### 1. Mock 데이터 파일 (전용 Mock 파일)

#### 1.1 HR 모듈 Mock 데이터
**파일**: `frontend/src/mocks/hrMockData.ts`

**내용**:
- **회사 데이터**: `mockCompanies` - ABC 기업 정보
- **부서 데이터**: `mockDepartments` - 개발팀, 마케팅팀, 영업팀 등
- **직급 데이터**: `mockPositions` - 인턴, 주니어, 시니어, 팀장 등
- **직원 데이터**: `mockEmployees` - 김철수, 이영희 등 샘플 직원 정보
- **통계 데이터**: `mockPositionStats`, `mockDepartmentStats`, `mockGenderStats`, `mockAgeGroupStats`

**상태**: ✅ **전용 Mock 파일** - 개발/테스트 목적으로 적절히 분리됨

#### 1.2 HR Mock API 서비스
**파일**: `frontend/src/services/hrMockApi.ts`

**내용**:
- Mock 데이터를 사용한 API 시뮬레이션
- `mockEmployeeApi`, `mockPositionApi` 객체

**상태**: ✅ **전용 Mock 서비스** - 개발/테스트 목적으로 적절히 분리됨

### 2. 컴포넌트 내 하드코딩 데이터 (⚠️ 수정 필요)

#### 2.1 EmployeeForm.tsx - 부서/직급 옵션
**파일**: `frontend/src/pages/hr/EmployeeForm.tsx`

**하드코딩된 데이터**:
```typescript
// 부서 선택 옵션 (라인 183-187)
<option value="">부서 선택</option>
<option value={1}>개발팀</option>
<option value={2}>마케팅팀</option>
<option value={3}>영업팀</option>

// 직급 선택 옵션 (라인 196-200)
<option value="">직급 선택</option>
<option value={1}>인턴</option>
<option value={2}>주니어</option>
<option value={3}>시니어</option>
<option value={4}>팀장</option>
```

**문제점**:
- 실제 DB의 부서/직급 데이터와 연결되지 않음
- 하드코딩된 ID 값 (1, 2, 3, 4)
- 새로운 부서/직급 추가 시 코드 수정 필요

**해결 방안**:
- API를 통해 실제 부서/직급 목록 조회
- 동적으로 옵션 생성

#### 2.2 GlobalSearch.tsx - 검색 타입 정보
**파일**: `frontend/src/components/search/GlobalSearch.tsx`

**하드코딩된 데이터**:
```typescript
// 검색 타입별 정보 (라인 336-350)
const getTypeInfo = (type: SearchResult['type']) => {
  switch (type) {
    case 'employee':
      return { label: '직원', color: 'text-blue-600', bgColor: 'bg-blue-50', icon: 'Users' }
    case 'product':
      return { label: '상품', color: 'text-green-600', bgColor: 'bg-green-50', icon: 'Package' }
    case 'order':
      return { label: '주문', color: 'text-purple-600', bgColor: 'bg-purple-50', icon: 'ShoppingCart' }
    case 'customer':
      return { label: '고객', color: 'text-orange-600', bgColor: 'bg-orange-50', icon: 'Building2' }
    case 'department':
      return { label: '부서', color: 'text-indigo-600', bgColor: 'bg-indigo-50', icon: 'FolderOpen' }
    case 'company':
      return { label: '회사', color: 'text-gray-600', bgColor: 'bg-gray-50', icon: 'Building2' }
  }
}
```

**문제점**:
- 검색 가능한 타입이 하드코딩됨
- 새로운 검색 타입 추가 시 코드 수정 필요

**해결 방안**:
- 설정 파일 또는 API를 통해 검색 타입 정보 관리
- 확장 가능한 구조로 변경

### 3. API 서비스 설정

#### 3.1 hrApi.ts - Mock API 사용 설정
**파일**: `frontend/src/services/hrApi.ts`

**하드코딩된 설정**:
```typescript
// Mock API 사용 여부 (라인 32)
const USE_MOCK_API = false
```

**상태**: ✅ **적절한 설정** - 개발/운영 환경에 따라 조절 가능

### 4. 기타 컴포넌트

#### 4.1 대시보드 컴포넌트들
**파일들**:
- `frontend/src/components/dashboard/charts/RevenueChart.tsx`
- `frontend/src/components/dashboard/DashboardCustomizer.tsx`
- `frontend/src/components/inventory/ProductTable.tsx`

**상태**: ✅ **동적 데이터 사용** - 대부분 API를 통해 데이터를 가져오는 구조

#### 4.2 알림 관련 컴포넌트들
**파일들**:
- `frontend/src/pages/notifications/NotificationListPage.tsx`
- `frontend/src/components/notification/NotificationDropdown.tsx`
- `frontend/src/services/notificationService.ts`

**상태**: ✅ **API 연동** - 실제 API를 통해 데이터 조회

## 🚨 우선순위별 수정 필요 항목

### 🔴 High Priority (즉시 수정 필요)

#### 1. EmployeeForm.tsx - 부서/직급 옵션
- **현재 상태**: 하드코딩된 옵션
- **수정 필요**: API를 통해 실제 부서/직급 목록 조회
- **예상 작업량**: 2-3시간

**수정 방안**:
```typescript
// 현재 (하드코딩)
<option value={1}>개발팀</option>

// 수정 후 (API 연동)
{departments.map(dept => (
  <option key={dept.id} value={dept.id}>{dept.name}</option>
))}
```

### 🟡 Medium Priority (점진적 수정)

#### 1. GlobalSearch.tsx - 검색 타입 정보
- **현재 상태**: 하드코딩된 타입 정보
- **수정 필요**: 설정 파일 또는 API를 통한 관리
- **예상 작업량**: 1-2시간

## 📊 하드코딩 데이터 현황 요약

| 구분 | 파일 수 | 상태 | 수정 필요 |
|------|---------|------|-----------|
| **전용 Mock 파일** | 2개 | ✅ 적절 | ❌ 불필요 |
| **컴포넌트 내 하드코딩** | 2개 | ⚠️ 수정 필요 | ✅ 필요 |
| **API 서비스 설정** | 1개 | ✅ 적절 | ❌ 불필요 |
| **기타 컴포넌트** | 5개 | ✅ 적절 | ❌ 불필요 |

## 🎯 수정 계획

### Phase 1: EmployeeForm.tsx 수정 (우선순위 1)
1. **부서 목록 API 연동**
   - `departmentApi.getDepartments()` 사용
   - 동적 옵션 생성

2. **직급 목록 API 연동**
   - `positionApi.getPositions()` 사용
   - 동적 옵션 생성

3. **로딩 상태 처리**
   - API 호출 중 로딩 표시
   - 에러 상태 처리

### Phase 2: GlobalSearch.tsx 개선 (우선순위 2)
1. **검색 타입 설정 파일 생성**
2. **동적 검색 타입 지원**
3. **확장 가능한 구조 구현**

## 🔧 수정 후 기대 효과

### 1. 데이터 일관성 확보
- 모든 데이터가 실제 DB와 동기화
- 하드코딩으로 인한 데이터 불일치 해결

### 2. 유지보수성 향상
- 새로운 부서/직급 추가 시 코드 수정 불필요
- 설정 변경 시 재배포 없이 적용 가능

### 3. 확장성 개선
- 새로운 검색 타입 추가 용이
- 다양한 데이터 소스 지원 가능

## 📝 결론

현재 프론트엔드에는 **2개의 컴포넌트**에서 하드코딩된 데이터가 발견되었습니다:

1. **EmployeeForm.tsx**: 부서/직급 옵션 (즉시 수정 필요)
2. **GlobalSearch.tsx**: 검색 타입 정보 (점진적 수정)

대부분의 Mock 데이터는 전용 파일로 적절히 분리되어 있으며, 나머지 컴포넌트들은 이미 API 연동이 잘 구현되어 있습니다.

**우선적으로 EmployeeForm.tsx의 하드코딩된 옵션을 실제 API 연동으로 변경하는 것을 권장합니다.**

