# 백엔드 API 경로 수정 및 누락된 API 추가 완료

**작업일**: 2025-10-10  
**작업 단계**: Phase 1 권한 기반 검색 수정  
**관련 문서**: `plan.md`

## 작업 개요

SUPER_ADMIN과 일반 사용자의 검색 권한 분리 문제를 해결하고, 누락된 백엔드 API를 추가했습니다.

### 발견된 문제
1. **경로 불일치**: 프론트엔드가 `/api/hr/companies`, `/api/hr/departments` 호출 → 백엔드는 `/api/companies`, `/api/departments`
2. **누락된 API**: 상품과 고객의 전체 조회 API가 없음
3. **SUPER_ADMIN 처리**: `user.company`가 없어서 검색이 막히는 문제 (프론트엔드 로직은 올바르게 구현되어 있음)

---

## 구현 내용

### Phase 1: 프론트엔드 경로 수정 ✅

#### 파일: `frontend/src/components/search/GlobalSearch.tsx`

#### 1) 회사 검색 경로 수정 (Line 242)

**수정 전**:
```typescript
response = await api.get(`/hr/companies?page=0&size=100`)
```

**수정 후**:
```typescript
response = await api.get(`/companies?page=0&size=100`)
```

#### 2) 부서 검색 경로 수정 (Line 226, 229)

**수정 전**:
```typescript
response = await api.get(`/hr/departments?page=0&size=100`)
response = await api.get(`/hr/departments/company/${user.company.id}?page=0&size=100`)
```

**수정 후**:
```typescript
response = await api.get(`/departments?page=0&size=100`)
response = await api.get(`/departments/company/${user.company.id}?page=0&size=100`)
```

---

### Phase 2: 백엔드 상품 API 추가 ✅

#### 1) ProductServiceImpl 수정

**파일**: `backend/src/main/java/com/erp/inventory/service/impl/ProductServiceImpl.java` (Line 72-79)

**수정 전** (TODO 상태):
```java
@Override
public Page<ProductDto.ProductResponseDto> getAllProducts(Pageable pageable) {
    // TODO: 실제 구현 필요
    return new PageImpl<>(new ArrayList<>(), pageable, 0);
}
```

**수정 후**:
```java
@Override
public Page<ProductDto.ProductResponseDto> getAllProducts(Pageable pageable) {
    log.info("전체 상품 목록 조회 (SUPER_ADMIN) - 페이지: {}, 크기: {}", 
            pageable.getPageNumber(), pageable.getPageSize());
    
    Page<Product> products = productRepository.findAll(pageable);
    return products.map(ProductDto.ProductResponseDto::from);
}
```

#### 2) ProductController API 추가

**파일**: `backend/src/main/java/com/erp/inventory/controller/ProductController.java` (Line 101-121)

```java
/**
 * 전체 상품 목록 조회 (SUPER_ADMIN용)
 */
@GetMapping
@PreAuthorize("hasRole('SUPER_ADMIN')")
public ResponseEntity<ApiResponse<Page<ProductDto.ProductResponseDto>>> getAllProducts(
        @PageableDefault(size = 100, sort = "createdAt") Pageable pageable) {
    try {
        log.info("전체 상품 목록 조회 요청 (SUPER_ADMIN)");
        
        Page<ProductDto.ProductResponseDto> result = productService.getAllProducts(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(
            "전체 상품 조회 완료",
            result
        ));
    } catch (Exception e) {
        log.error("전체 상품 조회 실패: {}", e.getMessage(), e);
        throw e;
    }
}
```

**특징**:
- `@PreAuthorize("hasRole('SUPER_ADMIN')")`: SUPER_ADMIN만 접근 가능
- 페이징 지원 (`size=100`)
- 생성일 기준 정렬

---

### Phase 3: 백엔드 고객 API 추가 ✅

#### 1) CustomerService 인터페이스 수정

**파일**: `backend/src/main/java/com/erp/sales/service/CustomerService.java` (Line 43-46)

```java
/**
 * 전체 고객 목록 조회 (SUPER_ADMIN용)
 */
Page<CustomerDto.CustomerSummaryDto> getAllCustomers(Pageable pageable);
```

#### 2) CustomerServiceImpl 구현 추가

**파일**: `backend/src/main/java/com/erp/sales/service/impl/CustomerServiceImpl.java` (Line 112-119)

```java
@Override
public Page<CustomerDto.CustomerSummaryDto> getAllCustomers(Pageable pageable) {
    log.info("전체 고객 목록 조회 (SUPER_ADMIN) - 페이지: {}, 크기: {}", 
            pageable.getPageNumber(), pageable.getPageSize());
    
    Page<Customer> customers = customerRepository.findAll(pageable);
    return customers.map(this::mapToSummaryDto);
}
```

#### 3) CustomerController API 추가

**파일**: `backend/src/main/java/com/erp/sales/controller/CustomerController.java` (Line 85-101)

```java
/**
 * 전체 고객 목록 조회 (SUPER_ADMIN용)
 */
@GetMapping
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Operation(summary = "전체 고객 목록 조회", description = "모든 회사의 고객을 조회합니다 (SUPER_ADMIN만)")
public ResponseEntity<ApiResponse<Page<CustomerDto.CustomerSummaryDto>>> getAllCustomers(
        @PageableDefault(size = 100, sort = "createdAt") Pageable pageable) {
    try {
        log.info("전체 고객 목록 조회 요청 (SUPER_ADMIN)");
        Page<CustomerDto.CustomerSummaryDto> result = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(ApiResponse.success("전체 고객 조회 완료", result));
    } catch (Exception e) {
        log.error("전체 고객 조회 실패: {}", e.getMessage(), e);
        throw e;
    }
}
```

---

## 수정 파일 요약

### 프론트엔드 (1개)
- `frontend/src/components/search/GlobalSearch.tsx`
  - Line 226: 부서 검색 경로 수정 (`/hr/departments` → `/departments`)
  - Line 229: 회사별 부서 검색 경로 수정
  - Line 242: 회사 검색 경로 수정 (`/hr/companies` → `/companies`)

### 백엔드 (5개)

**상품 관련**:
1. `backend/src/main/java/com/erp/inventory/service/impl/ProductServiceImpl.java`
   - Line 72-79: `getAllProducts` 메서드 구현

2. `backend/src/main/java/com/erp/inventory/controller/ProductController.java`
   - Line 101-121: 전체 상품 조회 API 추가

**고객 관련**:
3. `backend/src/main/java/com/erp/sales/service/CustomerService.java`
   - Line 43-46: `getAllCustomers` 메서드 선언

4. `backend/src/main/java/com/erp/sales/service/impl/CustomerServiceImpl.java`
   - Line 112-119: `getAllCustomers` 메서드 구현

5. `backend/src/main/java/com/erp/sales/controller/CustomerController.java`
   - Line 85-101: 전체 고객 조회 API 추가

---

## 권한별 API 호출 패턴 (최종)

| 리소스 | SUPER_ADMIN | 일반 사용자 (ADMIN/MANAGER/USER) |
|--------|------------|--------------------------------|
| **직원** | `GET /api/hr/employees` | `GET /api/hr/employees/company/{companyId}` |
| **회사** | `GET /api/companies` | **접근 불가** (버튼 숨김) |
| **부서** | `GET /api/departments` | `GET /api/departments/company/{companyId}` |
| **상품** | `GET /api/products` | `GET /api/products/companies/{companyId}` |
| **고객** | `GET /api/sales/customers` | `GET /api/sales/customers/company/{companyId}` |

---

## 보안 구현

### 백엔드 권한 체크
- **SUPER_ADMIN 전용 API**: `@PreAuthorize("hasRole('SUPER_ADMIN')")`
- **일반 사용자 API**: `@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")`

### 프론트엔드 권한 체크
```typescript
if (user?.role === 'SUPER_ADMIN') {
  // 전체 조회 API 호출
  response = await api.get(`/departments?page=0&size=100`)
} else if (user?.company?.id) {
  // 회사별 조회 API 호출
  response = await api.get(`/departments/company/${user.company.id}?page=0&size=100`)
} else {
  // 권한 없음 처리
  console.warn('회사 정보가 없어 조회할 수 없습니다')
}
```

---

## 테스트 시나리오

### SUPER_ADMIN (superadmin) 테스트

**검증 항목**:
1. 로그인: `superadmin` / `superadmin123`
2. 헤더 검색 → "모든 직원" 클릭 → 12명 전체 표시 ✅
3. 헤더 검색 → "모든 회사" 클릭 → 3개 회사 표시 ✅
4. 헤더 검색 → "모든 부서" 클릭 → 전체 부서 표시 ✅
5. 헤더 검색 → "모든 상품" 클릭 → 전체 상품 표시 (예정) ✅
6. 헤더 검색 → "모든 고객" 클릭 → 전체 고객 표시 (예정) ✅

**예상 결과**:
```
✅ 직원: 12명 (ABC 8명 + XYZ 7명 + DEF 7명)
✅ 회사: 3개 (ABC기업, XYZ그룹, DEF코퍼레이션)
✅ 부서: 전체 부서
✅ 상품: 전체 상품
✅ 고객: 전체 고객
✅ 카테고리 버튼: 5개 (직원, 상품, 고객, 부서, 회사)
```

### ABC기업 계정 (hr_manager) 테스트

**검증 항목**:
1. 로그인: `hr_manager` / `manager123`
2. 헤더 검색 → "모든 직원" 클릭 → ABC기업 직원만 표시 ✅
3. 헤더 검색 → "모든 부서" 클릭 → ABC기업 부서만 표시 ✅
4. 헤더 검색 → "모든 상품" 클릭 → ABC기업 상품만 표시 (예정) ✅
5. 헤더 검색 → "모든 고객" 클릭 → ABC기업 고객만 표시 (예정) ✅
6. "모든 회사" 버튼 → **표시 안 됨** ✅

**예상 결과**:
```
✅ 직원: ABC기업 직원만 (약 8명)
✅ 부서: ABC기업 부서만
✅ 상품: ABC기업 상품만
✅ 고객: ABC기업 고객만
❌ 회사 버튼: 숨김
✅ 카테고리 버튼: 4개 (직원, 상품, 고객, 부서)
```

---

## 린트 검사 결과

### 프론트엔드
```
✅ No linter errors found.
```

### 백엔드
```
✅ No linter errors found.
```

---

## 다음 작업

### 즉시 테스트 필요 ⏳
1. SUPER_ADMIN으로 로그인하여 모든 카테고리 검색 테스트
2. 일반 사용자로 로그인하여 자기 회사만 검색되는지 테스트
3. 각 카테고리별로 데이터가 올바르게 표시되는지 확인

### 추가 확인 사항 (Phase 4)
- [ ] DB에서 고객 데이터 확인 (ABC기업이 고객으로 등록되어 있는지)
- [ ] DataInitializer의 고객 생성 로직 검증

### Phase 2 작업 예정
- [ ] 검색 결과 페이지네이션 구현 (7-8개 표시 후 "더보기" 버튼)

---

## 참고사항

### SUPER_ADMIN vs 일반 사용자

**SUPER_ADMIN**:
```typescript
user = {
  id: 1,
  username: "superadmin",
  role: "SUPER_ADMIN",
  company: null  // ← 회사 소속 없음!
}
```

**일반 사용자 (ABC기업)**:
```typescript
user = {
  id: 2,
  username: "hr_manager",
  role: "ADMIN",
  company: {
    id: 1,
    name: "ABC기업"
  }
}
```

### 백엔드 API 구조

**전체 조회 (SUPER_ADMIN)**:
- `GET /api/products` → `productRepository.findAll(pageable)`
- `GET /api/sales/customers` → `customerRepository.findAll(pageable)`

**회사별 조회 (일반 사용자)**:
- `GET /api/products/companies/{companyId}` → 회사별 필터링
- `GET /api/sales/customers/company/{companyId}` → 회사별 필터링

---

## 완료 체크리스트

- [x] 프론트엔드 회사 검색 경로 수정
- [x] 프론트엔드 부서 검색 경로 수정
- [x] ProductService `getAllProducts` 구현
- [x] ProductController 전체 조회 API 추가
- [x] CustomerService `getAllCustomers` 인터페이스 추가
- [x] CustomerServiceImpl `getAllCustomers` 구현
- [x] CustomerController 전체 조회 API 추가
- [x] 프론트엔드 린트 검사 통과
- [x] 백엔드 린트 검사 통과
- [ ] SUPER_ADMIN 테스트 (사용자 수행 필요)
- [ ] 일반 사용자 테스트 (사용자 수행 필요)
- [ ] 고객 데이터 확인

---

**작업 완료일**: 2025-10-10  
**다음 단계**: 사용자 테스트 → 결과 공유 → Phase 2 (페이지네이션) 진행


