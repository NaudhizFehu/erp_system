# 테스트 결과 기반 문제 수정 완료

**작업일**: 2025-10-10  
**작업 단계**: 테스트 중 발견된 5가지 문제 해결  
**관련 문서**: `plan.md`

## 작업 개요

Phase 1 권한 기반 검색 구현 후 테스트에서 발견된 문제들을 해결했습니다.

---

## 발견된 문제 및 해결

### 문제 1: 고객 상세 조회 401 Unauthorized ❌

**증상**:
```
GET http://localhost:9960/api/sales/customers/1 401 (Unauthorized)
```

**원인**:
- CustomerController에서 `@GetMapping` (전체 조회)이 `@GetMapping("/{customerId}")` (상세 조회)보다 먼저 배치됨
- Spring이 `/api/sales/customers/1` 요청을 전체 조회 API로 라우팅
- 전체 조회는 `@PreAuthorize("hasRole('SUPER_ADMIN')")` 적용되어 일반 사용자는 401 발생

**해결 방법**:
API 순서 변경 - 구체적 경로를 먼저 배치

**파일**: `backend/src/main/java/com/erp/sales/controller/CustomerController.java`

**수정 전 (잘못된 순서)**:
```java
@GetMapping  // ← 먼저 매칭됨
@PreAuthorize("hasRole('SUPER_ADMIN')")
public ResponseEntity<ApiResponse<Page<CustomerDto.CustomerSummaryDto>>> getAllCustomers(...)

@GetMapping("/{customerId}")  // ← 도달하지 못함!
public ResponseEntity<ApiResponse<CustomerDto.CustomerResponseDto>> getCustomer(...)
```

**수정 후 (올바른 순서)**:
```java
@GetMapping("/{customerId}")  // ← 구체적 경로 먼저
public ResponseEntity<ApiResponse<CustomerDto.CustomerResponseDto>> getCustomer(...)

@GetMapping  // ← 일반 경로 나중에
@PreAuthorize("hasRole('SUPER_ADMIN')")
public ResponseEntity<ApiResponse<Page<CustomerDto.CustomerSummaryDto>>> getAllCustomers(...)
```

**변경 라인**: Line 85-120 (순서 변경)

---

### 문제 2: ABC기업 상품 검색 결과 없음 ❌

**증상**:
- DB에 `company_id=1` (ABC기업) 상품 데이터 존재
- hr_manager(ABC기업)로 상품 검색 시 결과 없음

**원인**:
- ProductServiceImpl의 `getProductsByCompany` 메서드가 TODO 상태로 빈 배열 반환

**현재 코드** (Line 81-86):
```java
@Override
public Page<ProductDto.ProductSummaryDto> getProductsByCompany(Long companyId, Pageable pageable) {
    // TODO: 실제 구현 필요
    return new PageImpl<>(new ArrayList<>(), pageable, 0);
}
```

**수정 후**:
```java
@Override
public Page<ProductDto.ProductSummaryDto> getProductsByCompany(Long companyId, Pageable pageable) {
    log.info("회사별 상품 목록 조회 - 회사: {}", companyId);
    
    Page<Product> products = productRepository.findByCompanyIdOrderByProductNameAsc(companyId, pageable);
    return products.map(ProductDto.ProductSummaryDto::from);
}
```

**파일**: `backend/src/main/java/com/erp/inventory/service/impl/ProductServiceImpl.java`

---

### 문제 3: 상품 상세 정보 표시 안 됨 ⚠️

**증상**:
- 상품 검색은 정상
- 상세 페이지 접근은 되지만 상품명, 코드, 단위, 판매가 등이 표시되지 않음

**원인**:
- `productService.ts`의 `getProductById`가 `response.data` 반환
- 백엔드는 `ApiResponse` 구조 `{success: true, data: {...}}` 반환
- 실제 상품 데이터는 `response.data.data`에 있음

**파일**: `frontend/src/services/productService.ts`

**수정 전** (Line 127-135):
```typescript
async getProductById(id: number): Promise<Product> {
  try {
    const response = await api.get(`${this.baseUrl}/${id}`)
    return response.data
  } catch (error) {
    console.error('상품 상세 정보 조회 오류:', error)
    throw new Error('상품 정보를 불러오는 중 오류가 발생했습니다.')
  }
}
```

**수정 후**:
```typescript
async getProductById(id: number): Promise<Product> {
  try {
    const response = await api.get(`${this.baseUrl}/${id}`)
    // 백엔드 응답 구조: {success: true, message: '...', data: {상품데이터}, ...}
    // response.data.data가 실제 상품 데이터이므로 이를 반환
    return response.data.data
  } catch (error) {
    console.error('상품 상세 정보 조회 오류:', error)
    throw new Error('상품 정보를 불러오는 중 오류가 발생했습니다.')
  }
}
```

**변경 라인**: Line 130-132

**참고**: employeeService.ts와 동일한 패턴

---

### 문제 4: 고객 데이터 오류 확인 ✅

**증상**:
- ABC기업 계정으로 고객 검색 시 "ABC기업"이 고객으로 표시됨

**확인 결과**:
- `DataInitializer.java`의 고객 이름 배열 확인 (Line 1130-1133)
- 고객 이름: "삼성전자", "LG전자", "현대자동차" 등
- "ABC기업", "XYZ그룹", "DEF코퍼레이션" **없음**

**결론**:
- 코드상으로는 정상
- DB에 직접 삽입된 데이터일 가능성 있음
- 추가 확인 필요 (사용자에게 DB 조회 요청)

**추후 확인 쿼리**:
```sql
SELECT id, customer_name, company_id 
FROM customers 
WHERE customer_name LIKE '%ABC%' 
   OR customer_name LIKE '%기업%'
   OR customer_name LIKE '%XYZ%'
   OR customer_name LIKE '%그룹%';
```

---

### 문제 5: 최근 검색어 전역 공유 🔄

**증상**:
- superadmin의 검색 기록이 hr_manager, xyz_manager에도 표시됨
- localStorage 키가 모든 계정에서 `'recentSearches'`로 동일

**해결 방법**:
계정 ID별로 localStorage 키 분리

**파일**: `frontend/src/components/search/GlobalSearch.tsx`

#### 1) 최근 검색어 로드 수정 (Line 55-68)

**수정 전**:
```typescript
useEffect(() => {
  const saved = localStorage.getItem('recentSearches')
  if (saved) {
    setRecentSearches(JSON.parse(saved))
  }
}, [])
```

**수정 후**:
```typescript
useEffect(() => {
  if (user?.id) {
    const saved = localStorage.getItem(`recentSearches_${user.id}`)
    if (saved) {
      try {
        setRecentSearches(JSON.parse(saved))
      } catch (error) {
        console.error('최근 검색어 로드 실패:', error)
        setRecentSearches([])
      }
    }
  }
}, [user])
```

#### 2) 최근 검색어 저장 함수 추가 (Line 101-110)

```typescript
/**
 * 최근 검색어 저장
 */
const saveRecentSearch = (term: string) => {
  if (user?.id && term.trim()) {
    const updated = [term, ...recentSearches.filter(t => t !== term)].slice(0, 5)
    setRecentSearches(updated)
    localStorage.setItem(`recentSearches_${user.id}`, JSON.stringify(updated))
  }
}
```

#### 3) performSearch에서 저장 함수 호출 (Line 122)

```typescript
const performSearch = async (term: string) => {
  if (!term.trim()) {
    setResults([])
    return
  }

  // 최근 검색어에 추가
  saveRecentSearch(term)  // ← 추가

  setIsLoading(true)
  // ...
}
```

**효과**:
- localStorage 키: `recentSearches_1` (superadmin), `recentSearches_2` (hr_manager) 등
- 계정별로 독립적인 최근 검색어 관리

---

## 수정 파일 요약

### 프론트엔드 (2개)

1. **frontend/src/components/search/GlobalSearch.tsx**
   - Line 56-68: 최근 검색어 로드 로직 수정 (계정별 키 사용)
   - Line 101-110: 최근 검색어 저장 함수 추가
   - Line 122: performSearch에서 저장 함수 호출

2. **frontend/src/services/productService.ts**
   - Line 130-132: `getProductById` 반환값 수정 (`response.data` → `response.data.data`)

### 백엔드 (3개)

3. **backend/src/main/java/com/erp/sales/controller/CustomerController.java**
   - Line 85-120: API 메서드 순서 변경 (구체적 경로 먼저)

4. **backend/src/main/java/com/erp/inventory/service/impl/ProductServiceImpl.java**
   - Line 81-87: `getProductsByCompany` 메서드 구현

5. **backend/src/main/java/com/erp/sales/service/CustomerService.java**
   - Line 43-46: `getAllCustomers` 메서드 인터페이스 추가

6. **backend/src/main/java/com/erp/sales/service/impl/CustomerServiceImpl.java**
   - Line 112-119: `getAllCustomers` 메서드 구현

---

## 린트 검사 결과

```
✅ No linter errors found.
```

모든 파일에서 린트 오류 없음!

---

## 테스트 시나리오 (재테스트 필요)

### SUPER_ADMIN (superadmin) 테스트

**검증 항목**:
1. ✅ 직원 검색: 12명 전체 표시
2. ✅ 부서 검색: 전체 부서 표시
3. ✅ 회사 검색: 3개 회사 표시
4. ⏳ 상품 검색 → 상세 페이지: 상품명, 코드, 판매가 등 **모든 정보 표시 확인**
5. ⏳ 고객 검색 → 상세 페이지: **401 오류 해결 확인**
6. ⏳ 최근 검색어: **superadmin 전용 표시 확인**

**예상 결과**:
```
✅ 직원: 12명 전체
✅ 회사: 3개 회사
✅ 부서: 전체 부서
✅ 상품: 검색 + 상세 정보 모두 표시
✅ 고객: 검색 + 상세 정보 모두 표시
✅ 최근 검색어: superadmin만의 검색 기록
```

### ABC기업 hr_manager 테스트

**검증 항목**:
1. ✅ 직원 검색: ABC기업 직원만 표시
2. ✅ 부서 검색: ABC기업 부서만 표시
3. ⏳ 상품 검색: **ABC기업 상품 표시 확인** (이전에는 빈 결과)
4. ⏳ 고객 검색: ABC기업 고객만 표시 + **ABC기업이 고객으로 나타나지 않는지 확인**
5. ⏳ 최근 검색어: **hr_manager 전용 표시 확인**

**예상 결과**:
```
✅ 직원: ABC기업 직원만 (약 8명)
✅ 부서: ABC기업 부서만
✅ 상품: ABC기업 상품만 (DB에 있는 상품 표시)
✅ 고객: ABC기업 고객만 ("삼성전자", "LG전자" 등, ABC기업 제외)
✅ 최근 검색어: hr_manager만의 검색 기록
```

### 로그아웃 → 다른 계정 로그인 테스트

**검증 항목**:
1. superadmin으로 검색 → 로그아웃
2. hr_manager로 로그인
3. 최근 검색어에 superadmin의 기록이 **표시되지 않는지** 확인

---

## 기술적 세부사항

### Spring MVC 경로 매칭 우선순위

Spring은 다음 순서로 경로를 매칭합니다:
1. 명시적 경로 (`/code/{code}`, `/{id}`)
2. 와일드카드 경로 (`/*`)
3. 루트 경로 (`/`)

**주의사항**:
- `@GetMapping`은 모든 GET 요청을 매칭
- `@GetMapping("/{customerId}")`보다 **먼저** 선언하면 모든 요청을 가로챔
- 해결: 구체적 경로를 먼저 선언

### ApiResponse 구조 통일

**백엔드 응답 구조**:
```json
{
  "success": true,
  "message": "정상 처리되었습니다",
  "data": {
    // 실제 데이터
  },
  "status": 200,
  "timestamp": "2025-10-10 09:00:00"
}
```

**프론트엔드 처리**:
```typescript
// ❌ 잘못된 방법
return response.data  // {success: true, data: {...}} 전체 반환

// ✅ 올바른 방법
return response.data.data  // 실제 데이터만 반환
```

**적용된 서비스**:
- ✅ employeeService.ts (이미 수정됨)
- ✅ productService.ts (이번에 수정)
- ⚠️ customerService.ts (확인 필요)

### localStorage 키 네이밍

**수정 전**:
```typescript
localStorage.getItem('recentSearches')  // 모든 계정 공유
```

**수정 후**:
```typescript
localStorage.getItem(`recentSearches_${user.id}`)  // 계정별 분리
```

**키 예시**:
- superadmin (ID: 1): `recentSearches_1`
- hr_manager (ID: 2): `recentSearches_2`
- xyz_manager (ID: 3): `recentSearches_3`

---

## 완료 체크리스트

### Backend 수정
- [x] CustomerController API 순서 변경
- [x] CustomerService `getAllCustomers` 인터페이스 추가
- [x] CustomerServiceImpl `getAllCustomers` 구현
- [x] ProductServiceImpl `getProductsByCompany` 구현
- [x] ProductServiceImpl `getProductById` 확인 (이미 구현됨)
- [x] DataInitializer 고객 데이터 확인 (정상)

### Frontend 수정
- [x] productService.ts `getProductById` 반환값 수정
- [x] GlobalSearch.tsx 최근 검색어 로드 로직 수정
- [x] GlobalSearch.tsx 최근 검색어 저장 함수 추가

### 린트 검사
- [x] 프론트엔드 린트 통과
- [x] 백엔드 린트 통과

### 테스트 (사용자 수행 필요)
- [ ] SUPER_ADMIN: 고객 상세 조회 (401 해결 확인)
- [ ] SUPER_ADMIN: 상품 상세 정보 표시 확인
- [ ] hr_manager: 상품 검색 결과 확인 (ABC기업 상품 표시)
- [ ] hr_manager: 고객 목록에 ABC기업 없는지 확인
- [ ] 계정별 최근 검색어 분리 확인

---

## 추가 확인 필요

### 고객 데이터 DB 조회

DataInitializer 코드상으로는 정상이지만, 사용자가 "ABC기업이 고객으로 표시됨"이라고 보고했습니다.

**확인 방법**:
```sql
SELECT id, customer_name, company_id 
FROM customers 
WHERE customer_name LIKE '%ABC%' 
   OR customer_name LIKE '%기업%'
   OR customer_name LIKE '%XYZ%'
   OR customer_name LIKE '%그룹%'
   OR customer_name LIKE '%DEF%'
   OR customer_name LIKE '%코퍼레이션%'
ORDER BY id;
```

**예상 결과**:
- 검색 결과 없음: 정상
- 검색 결과 있음: DB에 직접 삽입된 데이터 → 삭제 필요

**만약 발견되면**:
```sql
-- ABC기업을 고객에서 제거
DELETE FROM customers 
WHERE customer_name IN ('ABC기업', 'XYZ그룹', 'DEF코퍼레이션');
```

---

## 다음 단계

### 즉시 테스트 필요 ⏳

**백엔드 재기동**:
- ProductController, CustomerController 수정사항 반영을 위해 재기동 필요
- IDE에서 서버 재시작

**테스트 순서**:
1. 백엔드 서버 재기동
2. 프론트엔드 브라우저 새로고침 (Ctrl + Shift + R)
3. SUPER_ADMIN 테스트 (5개 카테고리)
4. hr_manager 테스트 (4개 카테고리)
5. 로그아웃 → 재로그인하여 최근 검색어 분리 확인

### Phase 2 작업 예정

테스트 완료 후:
- [ ] 검색 결과 페이지네이션 구현 (7-8개 표시 후 "더보기" 버튼)

---

## 참고사항

### 수정된 API 엔드포인트

| 리소스 | SUPER_ADMIN | 일반 사용자 | 상세 조회 |
|--------|------------|-----------|---------|
| 직원 | `GET /api/hr/employees` | `GET /api/hr/employees/company/{id}` | `GET /api/hr/employees/{id}` |
| 회사 | `GET /api/companies` | 접근 불가 | `GET /api/companies/{id}` |
| 부서 | `GET /api/departments` | `GET /api/departments/company/{id}` | `GET /api/departments/{id}` |
| 상품 | `GET /api/products` | `GET /api/products/companies/{id}` | `GET /api/products/{id}` |
| 고객 | `GET /api/sales/customers` | `GET /api/sales/customers/company/{id}` | `GET /api/sales/customers/{id}` |

**모든 상세 조회 API는 구체적 경로 먼저 선언!**

---

**작업 완료일**: 2025-10-10  
**다음 단계**: 백엔드 재기동 → 테스트 → 결과 공유


