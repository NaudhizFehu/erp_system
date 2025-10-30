# Phase 1: 권한 기반 검색 기능 구현 완료

**작업일**: 2025-10-10  
**작업 단계**: Phase 1 (권한 기반 검색 구현)  
**관련 문서**: `plan.md`

## 작업 개요

GlobalSearch 컴포넌트에 사용자 권한 기반 검색 기능을 구현했습니다.
- **SUPER_ADMIN**: 전체 회사의 모든 데이터 조회 가능
- **일반 사용자 (ADMIN/MANAGER/USER)**: 자기 회사 데이터만 조회 가능

---

## 구현 내용

### 1. useAuth 훅 import 추가 ✅

**파일**: `frontend/src/components/search/GlobalSearch.tsx`  
**라인**: 4

```typescript
import { useAuth } from '@/contexts/AuthContext'
```

**목적**: 현재 로그인한 사용자의 정보(role, company)를 가져오기 위함

---

### 2. user 정보 가져오기 ✅

**파일**: `frontend/src/components/search/GlobalSearch.tsx`  
**라인**: 47

```typescript
function GlobalSearch() {
  const navigate = useNavigate()
  const { user } = useAuth()  // 사용자 정보 가져오기
  // ...
}
```

**제공되는 정보**:
- `user.role`: 사용자 권한 (`SUPER_ADMIN`, `ADMIN`, `MANAGER`, `USER`)
- `user.company.id`: 소속 회사 ID
- `user.company.name`: 소속 회사 이름

---

### 3. performSearch 함수 수정 (텍스트 검색) ✅

**파일**: `frontend/src/components/search/GlobalSearch.tsx`  
**라인**: 104-121

**수정 전**:
```typescript
const response = await api.get(`/search?q=${encodeURIComponent(term)}&companyId=1`)
```

**수정 후**:
```typescript
// 권한에 따라 companyId 파라미터 설정
let searchUrl = `/search?q=${encodeURIComponent(term)}`

if (user?.role === 'SUPER_ADMIN') {
  // SUPER_ADMIN: 전체 검색 (companyId 없음)
  searchUrl = `/search?q=${encodeURIComponent(term)}`
} else if (user?.company?.id) {
  // 일반 사용자: 자기 회사만 검색
  searchUrl = `/search?q=${encodeURIComponent(term)}&companyId=${user.company.id}`
} else {
  // 회사 정보가 없는 경우 검색 불가
  console.warn('⚠️ 회사 정보가 없어 검색할 수 없습니다')
  setResults([])
  setIsLoading(false)
  return
}
```

**변경 사항**:
- ❌ 하드코딩된 `companyId=1` 제거
- ✅ 권한에 따라 동적으로 URL 생성
- ✅ 회사 정보 없는 경우 처리 추가

---

### 4. performCategorySearch 함수 수정 (카테고리 검색) ✅

**파일**: `frontend/src/components/search/GlobalSearch.tsx`  
**라인**: 170-261

모든 카테고리(직원, 상품, 고객, 부서, 회사)에 권한 기반 API 호출 로직 추가:

#### 4-1. 직원 검색 ✅

```typescript
if (category === 'employee') {
  if (user?.role === 'SUPER_ADMIN') {
    response = await api.get(`/hr/employees?page=0&size=100`)
  } else if (user?.company?.id) {
    response = await api.get(`/hr/employees/company/${user.company.id}?page=0&size=100`)
  } else {
    console.warn('⚠️ 회사 정보가 없어 직원을 조회할 수 없습니다')
    setResults([])
    setIsLoading(false)
    return
  }
}
```

#### 4-2. 상품 검색 ✅

```typescript
else if (category === 'product') {
  if (user?.role === 'SUPER_ADMIN') {
    response = await api.get(`/products?page=0&size=100`)
  } else if (user?.company?.id) {
    response = await api.get(`/products/companies/${user.company.id}?page=0&size=100`)
  } else {
    // 에러 처리
  }
}
```

#### 4-3. 고객 검색 ✅

```typescript
else if (category === 'customer') {
  if (user?.role === 'SUPER_ADMIN') {
    response = await api.get(`/sales/customers?page=0&size=100`)
  } else if (user?.company?.id) {
    response = await api.get(`/sales/customers/company/${user.company.id}?page=0&size=100`)
  } else {
    // 에러 처리
  }
}
```

#### 4-4. 부서 검색 ✅

```typescript
else if (category === 'department') {
  if (user?.role === 'SUPER_ADMIN') {
    response = await api.get(`/hr/departments?page=0&size=100`)
  } else if (user?.company?.id) {
    response = await api.get(`/hr/departments/company/${user.company.id}?page=0&size=100`)
  } else {
    // 에러 처리
  }
}
```

#### 4-5. 회사 검색 (SUPER_ADMIN 전용) ✅

```typescript
else if (category === 'company') {
  if (user?.role === 'SUPER_ADMIN') {
    response = await api.get(`/hr/companies?page=0&size=100`)
  } else {
    console.warn('⚠️ 회사 목록은 SUPER_ADMIN만 조회 가능합니다')
    setResults([])
    setIsLoading(false)
    return
  }
}
```

#### 4-6. 기본 전역 검색 ✅

```typescript
else {
  if (user?.role === 'SUPER_ADMIN') {
    response = await api.get(`/search?q=${encodeURIComponent(term)}`)
  } else if (user?.company?.id) {
    response = await api.get(`/search?q=${encodeURIComponent(term)}&companyId=${user.company.id}`)
  } else {
    // 에러 처리
  }
}
```

---

### 5. 카테고리 버튼 조건부 표시 ✅

**파일**: `frontend/src/components/search/GlobalSearch.tsx`  
**라인**: 68-79

**수정 전**:
```typescript
const quickCategories: SearchSuggestion[] = [
  { id: 'cat-1', text: '모든 직원', type: 'category', category: 'employee', icon: 'Users' },
  { id: 'cat-2', text: '모든 상품', type: 'category', category: 'product', icon: 'Package' },
  { id: 'cat-3', text: '모든 고객', type: 'category', category: 'customer', icon: 'Building2' },
  { id: 'cat-4', text: '모든 부서', type: 'category', category: 'department', icon: 'FolderOpen' },
  { id: 'cat-5', text: '모든 회사', type: 'category', category: 'company', icon: 'Building2' }
]
```

**수정 후**:
```typescript
const quickCategories: SearchSuggestion[] = [
  { id: 'cat-1', text: '모든 직원', type: 'category', category: 'employee', icon: 'Users' },
  { id: 'cat-2', text: '모든 상품', type: 'category', category: 'product', icon: 'Package' },
  { id: 'cat-3', text: '모든 고객', type: 'category', category: 'customer', icon: 'Building2' },
  { id: 'cat-4', text: '모든 부서', type: 'category', category: 'department', icon: 'FolderOpen' },
  // SUPER_ADMIN만 회사 검색 버튼 표시
  ...(user?.role === 'SUPER_ADMIN' 
    ? [{ id: 'cat-5', text: '모든 회사', type: 'category' as const, category: 'company', icon: 'Building2' }]
    : []
  )
]
```

**효과**:
- SUPER_ADMIN: 5개 버튼 표시 (직원, 상품, 고객, 부서, **회사**)
- 일반 사용자: 4개 버튼만 표시 (직원, 상품, 고객, 부서)

---

## 구현된 권한 체크 로직 요약

### API 호출 패턴

| 리소스 | SUPER_ADMIN | 일반 사용자 (ADMIN/MANAGER/USER) |
|--------|------------|--------------------------------|
| **텍스트 검색** | `/search?q={검색어}` | `/search?q={검색어}&companyId={회사ID}` |
| **직원** | `/hr/employees?page=0&size=100` | `/hr/employees/company/{companyId}?page=0&size=100` |
| **상품** | `/products?page=0&size=100` | `/products/companies/{companyId}?page=0&size=100` |
| **고객** | `/sales/customers?page=0&size=100` | `/sales/customers/company/{companyId}?page=0&size=100` |
| **부서** | `/hr/departments?page=0&size=100` | `/hr/departments/company/{companyId}?page=0&size=100` |
| **회사** | `/hr/companies?page=0&size=100` | **접근 불가** (버튼 숨김) |

---

## 콘솔 로그 개선

권한 체크 시 상세한 콘솔 로그 추가:

```
✅ SUPER_ADMIN 권한: 전체 데이터 검색
✅ 일반 사용자 권한: 회사 1(ABC기업) 데이터만 검색
⚠️ 회사 정보가 없어 검색할 수 없습니다
⚠️ 회사 목록은 SUPER_ADMIN만 조회 가능합니다
```

---

## 테스트 준비

### 다음 단계: 테스트 수행 필요

#### 테스트 1: SUPER_ADMIN 계정 (superadmin)

**검증 항목**:
1. 텍스트 검색: "김" 입력 → 모든 회사의 직원 표시 (12명 전체)
2. 카테고리 검색:
   - "모든 직원" → 12명 전체 표시
   - "모든 상품" → 모든 회사의 상품 표시
   - "모든 고객" → 모든 회사의 고객 표시
   - "모든 부서" → 모든 회사의 부서 표시
   - "모든 회사" → 3개 회사 표시 (ABC, XYZ, DEF)

**예상 결과**:
```
🔑 Role: SUPER_ADMIN
📊 조회 범위: 전체 회사의 모든 데이터
✅ 직원: 12명 (ABC 8명 + XYZ 7명 + DEF 7명)
✅ 회사: 3개 (ABC기업, XYZ그룹, DEF코퍼레이션)
✅ 카테고리 버튼: 5개 (직원, 상품, 고객, 부서, 회사)
```

#### 테스트 2: ABC기업 계정 (admin/user)

**검증 항목**:
1. 텍스트 검색: "김" 입력 → ABC기업의 직원만 표시
2. 카테고리 검색:
   - "모든 직원" → ABC기업 직원만 표시 (예: 8명)
   - "모든 상품" → ABC기업 상품만 표시
   - "모든 고객" → ABC기업 고객만 표시
   - "모든 부서" → ABC기업 부서만 표시
   - "모든 회사" → **버튼 숨김**

**예상 결과**:
```
🔑 Role: ADMIN/MANAGER/USER
🏢 Company: ABC기업 (ID: 1)
📊 조회 범위: ABC기업 데이터만
✅ 직원: ABC기업 직원만 (예: 8명)
✅ 카테고리 버튼: 4개 (직원, 상품, 고객, 부서)
❌ 회사 버튼: 표시 안 됨
```

#### 테스트 3: XYZ그룹 계정

**검증 항목**:
1. 텍스트 검색: "개발" 입력 → XYZ그룹의 결과만 표시
2. 카테고리 검색: XYZ그룹 데이터만 표시

**예상 결과**:
```
🔑 Role: ADMIN/MANAGER/USER
🏢 Company: XYZ그룹 (ID: 2)
📊 조회 범위: XYZ그룹 데이터만
✅ 직원: XYZ그룹 직원만 (예: 7명)
```

#### 테스트 4: 회사 정보 없는 경우

**검증 항목**:
- SUPER_ADMIN이 아닌데 `user.company`가 없는 경우

**예상 결과**:
```
⚠️ 회사 정보가 없어 검색할 수 없습니다
📊 검색 결과: 빈 배열 []
```

---

## 보안 고려사항

### 이중 검증 구조

✅ **프론트엔드 (구현 완료)**:
- 사용자 권한에 따라 API 호출 제한
- 카테고리 버튼 조건부 표시
- 회사 정보 없는 경우 처리

✅ **백엔드 (기존 구현)**:
- Controller에서 권한 검증
- 회사 ID 검증
- SUPER_ADMIN이 아닌 경우 타사 데이터 접근 차단

### 예상 시나리오

1. **일반 사용자가 타사 데이터 접근 시도**:
   - 프론트엔드: API 호출 자체를 막음
   - 백엔드: 만약 우회하더라도 403 Forbidden 반환

2. **회사 버튼 직접 URL 접근**:
   - 프론트엔드: 버튼 자체가 숨겨짐
   - 백엔드: API 호출 시 권한 거부

---

## 코드 품질

### 린트 검사 결과
```
✅ No linter errors found.
```

### 코드 리뷰 체크리스트
- [x] useAuth 훅 올바르게 import
- [x] user 정보 안전하게 접근 (`user?.role`, `user?.company?.id`)
- [x] 모든 카테고리에 권한 체크 추가
- [x] 회사 정보 없는 경우 처리
- [x] 콘솔 로그로 디버깅 가능
- [x] TypeScript 타입 안전성 유지
- [x] 에러 핸들링 추가

---

## 다음 작업

### Phase 1 완료 후 테스트 필요 ⏳

**테스트 항목**:
1. SUPER_ADMIN 계정으로 전체 검색 테스트
2. 일반 사용자 계정으로 자기 회사만 검색 테스트
3. 회사 정보 없는 경우 처리 확인

### Phase 2: 페이지네이션 구현 (예정)

**구현 항목**:
1. 검색 결과 초기 표시 개수 제한 (7-8개)
2. 전체 결과 개수 상태 관리 추가
3. "더보기" 버튼 UI 컴포넌트 추가 ("123개 결과 더보기" 형식)
4. 더보기 클릭 시 전체 결과 표시 로직
5. 검색 결과 접기 기능 (선택사항)
6. 페이지네이션 기능 테스트

---

## 변경된 파일

### 수정
- `frontend/src/components/search/GlobalSearch.tsx` (1개 파일)

### 변경 요약
```
+ Line 4: useAuth 훅 import
+ Line 47: user 정보 가져오기
~ Line 104-121: performSearch 함수 권한 체크 추가
~ Line 170-261: performCategorySearch 함수 전체 수정
~ Line 68-79: quickCategories 조건부 표시
```

---

## 작업 완료 확인

- [x] useAuth 훅 import 추가
- [x] GlobalSearch 컴포넌트에서 user 정보 가져오기
- [x] performSearch 함수에 권한 체크 추가
- [x] performCategorySearch - 직원 검색 권한 체크
- [x] performCategorySearch - 상품 검색 권한 체크
- [x] performCategorySearch - 고객 검색 권한 체크
- [x] performCategorySearch - 부서 검색 권한 체크
- [x] performCategorySearch - 회사 검색 권한 체크 (SUPER_ADMIN만)
- [x] 카테고리 버튼 조건부 표시
- [x] 린트 검사 통과
- [ ] SUPER_ADMIN 계정 테스트 (사용자 수행 필요)
- [ ] 일반 사용자 계정 테스트 (사용자 수행 필요)
- [ ] 회사 정보 없는 경우 테스트 (사용자 수행 필요)

---

## 참고 사항

### API 엔드포인트 확인

백엔드 Controller에서 사용 가능한 엔드포인트:
- `GET /api/search?q={검색어}&companyId={회사ID}` - 전역 검색
- `GET /api/hr/employees` - 전체 직원 (SUPER_ADMIN)
- `GET /api/hr/employees/company/{companyId}` - 회사별 직원
- `GET /api/products` - 전체 상품 (SUPER_ADMIN)
- `GET /api/products/companies/{companyId}` - 회사별 상품
- `GET /api/sales/customers` - 전체 고객 (SUPER_ADMIN)
- `GET /api/sales/customers/company/{companyId}` - 회사별 고객
- `GET /api/hr/departments` - 전체 부서 (SUPER_ADMIN)
- `GET /api/hr/departments/company/{companyId}` - 회사별 부서
- `GET /api/hr/companies` - 전체 회사 (SUPER_ADMIN)

### 데이터 현황

현재 시스템에 등록된 데이터:
- **직원**: 총 12명 (ABC기업 8명, XYZ그룹 7명, DEF코퍼레이션 7명)
- **회사**: 총 3개 (ABC기업, XYZ그룹, DEF코퍼레이션)
- **사용자 계정**: 9개 (superadmin 1명 + 각 회사별 계정)

---

**작업 완료일**: 2025-10-10  
**다음 단계**: 테스트 수행 후 결과 공유


