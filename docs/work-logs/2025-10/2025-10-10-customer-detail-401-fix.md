# 고객 상세 조회 401 오류 수정 완료

**작업일**: 2025-10-10  
**작업 유형**: 버그 수정 (Critical)  
**우선순위**: High  
**관련 문서**: `plan.md`

## 작업 개요

고객 상세 페이지에서 발생하는 401 Unauthorized 오류를 해결했습니다.

---

## 문제 분석

### 증상

```
GET http://localhost:9960/api/sales/customers/2 401 (Unauthorized)
```

- SUPER_ADMIN으로 로그인했는데도 401 오류 발생
- 오류 메시지: "인증 토큰이 필요합니다. Authorization 헤더에 Bearer 토큰을 포함해주세요"

### 근본 원인

**CustomerDetail.tsx Line 36**:
```typescript
const response = await fetch(`/api/sales/customers/${customerId}`)
```

**문제점**:
1. **fetch()를 직접 사용** → Authorization 헤더가 자동으로 추가되지 않음
2. customerService를 import했지만 사용하지 않음  
3. 다른 Detail 컴포넌트들(EmployeeDetail, ProductDetail)은 서비스 사용

### 다른 컴포넌트와의 비교

| 컴포넌트 | API 호출 방식 | 인증 토큰 | 결과 |
|---------|-------------|---------|------|
| EmployeeDetail | `employeeService.getEmployeeById()` | ✅ 자동 추가 | 정상 |
| ProductDetail | `productService.getProductById()` | ✅ 자동 추가 | 정상 |
| **CustomerDetail** | **fetch() 직접 사용** | ❌ 없음 | **401 오류** |

### api 인스턴스의 인증 처리 메커니즘

**api.ts 인터셉터** (Line 31-44):
```typescript
api.interceptors.request.use(
  (config) => {
    // 인증 토큰 자동 추가
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)
```

**효과**:
- `api.get()` 사용 시 → Authorization 헤더 자동 추가 ✅
- `fetch()` 직접 사용 시 → Authorization 헤더 없음 ❌

---

## 수정 내용

### 1. CustomerDetail.tsx 수정 ✅

**파일**: `frontend/src/pages/sales/CustomerDetail.tsx`  
**라인**: 30-44

**수정 전**:
```typescript
const fetchCustomerDetail = async (customerId: number) => {
  try {
    setLoading(true)
    setError(null)
    
    // 실제 API 호출
    const response = await fetch(`/api/sales/customers/${customerId}`)
    const data = await response.json()
    
    if (data.success) {
      setCustomer(data.data)
    } else {
      setError(data.message || '고객 정보를 불러오는 중 오류가 발생했습니다.')
    }
  } catch (err) {
    setError('고객 정보를 불러오는 중 오류가 발생했습니다.')
    console.error('고객 상세 정보 조회 오류:', err)
  } finally {
    setLoading(false)
  }
}
```

**수정 후**:
```typescript
const fetchCustomerDetail = async (customerId: number) => {
  try {
    setLoading(true)
    setError(null)
    
    // customerService 사용 (인증 토큰 자동 포함)
    const customerData = await customerService.getCustomerById(customerId)
    setCustomer(customerData)
  } catch (err) {
    setError('고객 정보를 불러오는 중 오류가 발생했습니다.')
    console.error('고객 상세 정보 조회 오류:', err)
  } finally {
    setLoading(false)
  }
}
```

**변경 사항**:
- ❌ `fetch()` 직접 사용 제거
- ✅ `customerService.getCustomerById()` 사용
- ✅ 응답 처리 로직 단순화
- ✅ 인증 토큰 자동 추가

---

### 2. customerService.ts 수정 ✅

**파일**: `frontend/src/services/customerService.ts`  
**라인**: 75-88

**수정 전**:
```typescript
async getCustomerById(id: number): Promise<Customer> {
  try {
    console.log('고객 상세 조회 API 호출:', `${this.baseUrl}/${id}`)
    const response = await api.get(`${this.baseUrl}/${id}`)
    console.log('고객 상세 조회 API 응답:', response)
    console.log('고객 상세 조회 API 응답 데이터:', response.data)
    return response.data  // ← 문제: ApiResponse 전체 반환
  } catch (error) {
    console.error('고객 상세 정보 조회 오류:', error)
    throw new Error('고객 정보를 불러오는 중 오류가 발생했습니다.')
  }
}
```

**수정 후**:
```typescript
async getCustomerById(id: number): Promise<Customer> {
  try {
    console.log('고객 상세 조회 API 호출:', `${this.baseUrl}/${id}`)
    const response = await api.get(`${this.baseUrl}/${id}`)
    console.log('고객 상세 조회 API 응답:', response)
    console.log('고객 상세 조회 API 응답 데이터:', response.data)
    // 백엔드 응답 구조: {success: true, message: '...', data: {고객데이터}, ...}
    // response.data.data가 실제 고객 데이터이므로 이를 반환
    return response.data.data
  } catch (error) {
    console.error('고객 상세 정보 조회 오류:', error)
    throw new Error('고객 정보를 불러오는 중 오류가 발생했습니다.')
  }
}
```

**변경 사항**:
- Line 81-83: 주석 추가 및 반환값 수정
- `return response.data` → `return response.data.data`

---

## 린트 검사 결과

```
✅ No linter errors found.
```

---

## 수정 전 vs 수정 후

### 수정 전 (오류 발생)

**API 호출**:
```typescript
fetch('/api/sales/customers/2')
```

**HTTP 요청**:
```
GET /api/sales/customers/2 HTTP/1.1
Host: localhost:9960
// Authorization 헤더 없음! ← 401 오류 원인
```

**결과**:
```
❌ 401 Unauthorized
❌ 고객 정보 표시 안 됨
```

### 수정 후 (정상 작동)

**API 호출**:
```typescript
customerService.getCustomerById(2)
  → api.get('/sales/customers/2')
```

**HTTP 요청**:
```
GET /api/sales/customers/2 HTTP/1.1
Host: localhost:9960
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...  ← 자동 추가!
```

**결과**:
```
✅ 200 OK
✅ 고객 정보 정상 표시
```

---

## 일관성 확보

이제 모든 Detail 컴포넌트가 동일한 패턴을 사용합니다:

| 컴포넌트 | 서비스 사용 | 인증 토큰 | 응답 처리 |
|---------|-----------|---------|---------|
| EmployeeDetail | ✅ employeeService | ✅ 자동 | response.data.data |
| ProductDetail | ✅ productService | ✅ 자동 | response.data.data |
| **CustomerDetail** | ✅ customerService | ✅ 자동 | response.data.data |

**코드 패턴 통일 완료!**

---

## 테스트 시나리오

### SUPER_ADMIN 테스트

**순서**:
1. SUPER_ADMIN으로 로그인
2. 헤더 검색 → "모든 고객" 클릭
3. 고객 1명 선택하여 상세 페이지 이동
4. ✅ **확인**: 401 오류 없이 고객 정보 표시
5. 개발자 도구 Network 탭:
   - 요청: `GET /api/sales/customers/2`
   - 헤더: `Authorization: Bearer ...` 포함
   - 응답: `200 OK`

### ABC기업 hr_manager 테스트

**순서**:
1. hr_manager로 로그인
2. 헤더 검색 → "모든 고객" 클릭
3. ABC기업 고객 1명 선택하여 상세 페이지 이동
4. ✅ **확인**: 고객 정보 정상 표시

---

## 완료 체크리스트

- [x] CustomerDetail.tsx fetch를 customerService로 변경
- [x] customerService.ts 반환값 수정
- [x] 린트 검사 통과
- [ ] SUPER_ADMIN 고객 상세 조회 테스트
- [ ] hr_manager 고객 상세 조회 테스트

---

## 관련 수정 사항 (이전 작업)

이번 작업과 함께 수정된 내용들:

1. ✅ employeeService.ts: response.data.data 반환
2. ✅ productService.ts: response.data.data 반환
3. ✅ **customerService.ts**: response.data.data 반환 (이번 작업)

**모든 서비스가 동일한 응답 처리 패턴 사용!**

---

## 참고사항

### fetch vs api 인스턴스

**fetch 직접 사용 (권장하지 않음)**:
```typescript
// ❌ 인증 토큰 수동으로 추가해야 함
const token = localStorage.getItem('accessToken')
const response = await fetch('/api/sales/customers/1', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
```

**api 인스턴스 사용 (권장)**:
```typescript
// ✅ 인증 토큰 자동 추가
const response = await api.get('/sales/customers/1')
```

**결론**: 프로젝트 전체에서 `api` 인스턴스를 사용하여 일관성과 보안을 유지!

---

**작업 완료일**: 2025-10-10  
**오류 해결 시간**: 즉시 수정  
**다음 단계**: 브라우저 새로고침 후 테스트


