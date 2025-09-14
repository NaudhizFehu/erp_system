# 📊 ERP 시스템 API 문서

## 🚀 개요

ERP 시스템의 완전한 REST API 문서입니다. 모든 API는 OpenAPI 3.0 사양을 준수하며, Swagger UI를 통해 대화형으로 테스트할 수 있습니다.

## 📖 API 문서 접근 방법

### 1. Swagger UI (권장)
```
http://localhost:8080/swagger-ui/index.html
```

### 2. API 소개 페이지
```
http://localhost:8080/api-docs.html
```

### 3. OpenAPI JSON 스펙
```
http://localhost:8080/v3/api-docs
```

## 🔐 인증 방법

### JWT Bearer 토큰 인증
모든 API는 JWT Bearer 토큰을 사용한 인증이 필요합니다.

#### 1단계: 로그인
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "admin",
  "password": "admin123",
  "rememberMe": true
}
```

#### 2단계: 토큰 사용
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 테스트 계정
| 역할 | 사용자명 | 비밀번호 | 권한 |
|------|----------|----------|------|
| 관리자 | admin | admin123 | 모든 기능 접근 |
| 매니저 | manager | manager123 | 관리 기능 접근 |
| 사용자 | user | user123 | 기본 기능 접근 |

## 📋 API 모듈별 구성

### 🔑 인증 관리 (`/api/auth`)
- **POST** `/login` - 사용자 로그인
- **POST** `/refresh` - 토큰 갱신
- **POST** `/logout` - 로그아웃
- **PUT** `/change-password` - 비밀번호 변경
- **GET** `/me` - 현재 사용자 정보 조회
- **GET** `/validate` - 토큰 유효성 검증

### 👥 인사관리 (`/api/hr`)

#### 직원 관리 (`/employees`)
- **POST** `/` - 직원 등록 (ADMIN)
- **GET** `/` - 직원 목록 조회 (페이징, 검색)
- **GET** `/{id}` - 직원 상세 조회
- **PUT** `/{id}` - 직원 정보 수정 (ADMIN)
- **DELETE** `/{id}` - 직원 삭제 (ADMIN)
- **GET** `/search` - 직원 검색
- **GET** `/statistics` - 직원 통계

#### 급여 관리 (`/salaries`)
- **POST** `/` - 급여 등록
- **GET** `/` - 급여 목록 조회
- **GET** `/{id}` - 급여 상세 조회
- **PUT** `/{id}` - 급여 정보 수정
- **GET** `/employee/{employeeId}` - 직원별 급여 조회

#### 근태 관리 (`/attendance`)
- **POST** `/` - 근태 등록
- **GET** `/` - 근태 목록 조회
- **GET** `/{id}` - 근태 상세 조회
- **PUT** `/{id}` - 근태 정보 수정
- **GET** `/employee/{employeeId}` - 직원별 근태 조회

### 💼 영업관리 (`/api/sales`)

#### 고객 관리 (`/customers`)
- **POST** `/` - 고객 등록
- **GET** `/` - 고객 목록 조회 (페이징, 검색)
- **GET** `/{id}` - 고객 상세 조회
- **PUT** `/{id}` - 고객 정보 수정
- **DELETE** `/{id}` - 고객 삭제
- **PATCH** `/{id}/status` - 고객 상태 변경
- **GET** `/statistics` - 고객 통계

#### 주문 관리 (`/orders`)
- **POST** `/` - 주문 생성
- **GET** `/` - 주문 목록 조회 (페이징, 검색)
- **GET** `/{id}` - 주문 상세 조회
- **PUT** `/{id}` - 주문 정보 수정
- **DELETE** `/{id}` - 주문 삭제
- **PATCH** `/{id}/status` - 주문 상태 변경
- **PATCH** `/{id}/payment-status` - 결제 상태 변경

#### 견적서 관리 (`/quotes`)
- **POST** `/` - 견적서 생성
- **GET** `/` - 견적서 목록 조회
- **GET** `/{id}` - 견적서 상세 조회
- **PUT** `/{id}` - 견적서 수정
- **DELETE** `/{id}` - 견적서 삭제
- **POST** `/{id}/convert-to-order` - 견적서를 주문으로 전환

#### 계약 관리 (`/contracts`)
- **POST** `/` - 계약 생성
- **GET** `/` - 계약 목록 조회
- **GET** `/{id}` - 계약 상세 조회
- **PUT** `/{id}` - 계약 수정
- **DELETE** `/{id}` - 계약 삭제
- **POST** `/{id}/renew` - 계약 갱신

### 📦 재고관리 (`/api/inventory`)

#### 상품 관리 (`/products`)
- **POST** `/` - 상품 등록
- **GET** `/` - 상품 목록 조회 (페이징, 검색)
- **GET** `/{id}` - 상품 상세 조회
- **PUT** `/{id}` - 상품 정보 수정
- **DELETE** `/{id}` - 상품 삭제
- **GET** `/low-stock` - 재고 부족 상품 조회
- **GET** `/barcode/{barcode}` - 바코드로 상품 조회

#### 재고 관리 (`/inventory`)
- **POST** `/stock-in` - 입고 처리
- **POST** `/stock-out` - 출고 처리
- **GET** `/current/{productId}` - 현재 재고 조회
- **POST** `/adjustment` - 재고 조정
- **GET** `/movements` - 재고 이동 내역
- **POST** `/physical-count` - 실사 처리

#### 창고 관리 (`/warehouses`)
- **POST** `/` - 창고 등록
- **GET** `/` - 창고 목록 조회
- **GET** `/{id}` - 창고 상세 조회
- **PUT** `/{id}` - 창고 정보 수정
- **DELETE** `/{id}` - 창고 삭제

### 💰 회계관리 (`/api/accounting`)

#### 계정과목 관리 (`/accounts`)
- **POST** `/` - 계정과목 등록
- **GET** `/` - 계정과목 목록 조회
- **GET** `/{id}` - 계정과목 상세 조회
- **PUT** `/{id}` - 계정과목 수정
- **DELETE** `/{id}` - 계정과목 삭제

#### 거래 관리 (`/transactions`)
- **POST** `/` - 거래 등록 (복식부기)
- **GET** `/` - 거래 목록 조회
- **GET** `/{id}` - 거래 상세 조회
- **PUT** `/{id}` - 거래 수정
- **DELETE** `/{id}` - 거래 삭제
- **GET** `/trial-balance` - 시산표 조회

#### 예산 관리 (`/budgets`)
- **POST** `/` - 예산 등록
- **GET** `/` - 예산 목록 조회
- **GET** `/{id}` - 예산 상세 조회
- **PUT** `/{id}` - 예산 수정
- **GET** `/vs-actual` - 예산 대비 실적 분석

#### 재무보고서 (`/reports`)
- **GET** `/income-statement` - 손익계산서
- **GET** `/balance-sheet` - 재무상태표
- **GET** `/cash-flow` - 현금흐름표
- **GET** `/general-ledger` - 총계정원장

### 📈 대시보드 (`/api/dashboard`)

#### 현황 조회
- **GET** `/overview/{companyId}` - 전체 현황 요약
- **GET** `/data/{companyId}` - 대시보드 전체 데이터

#### 차트 데이터
- **GET** `/charts/revenue/{companyId}` - 매출 차트 데이터
- **GET** `/charts/orders/{companyId}` - 주문 차트 데이터
- **GET** `/charts/inventory/{companyId}` - 재고 차트 데이터
- **GET** `/charts/hr/{companyId}` - 인사 차트 데이터

#### 활동 및 알림
- **GET** `/activities/{companyId}` - 최근 활동 로그
- **GET** `/notifications/{userId}` - 사용자 알림
- **PUT** `/notifications/{id}/read` - 알림 읽음 처리
- **PUT** `/notifications/read-all/{userId}` - 모든 알림 읽음

#### 할일 관리
- **GET** `/todos/{userId}` - 할일 목록
- **POST** `/todos` - 새 할일 생성
- **PUT** `/todos/{id}/status` - 할일 상태 변경

#### 설정 관리
- **GET** `/config/{userId}` - 사용자 대시보드 설정
- **PUT** `/config/{userId}` - 대시보드 설정 저장

## 📊 응답 형식

### 성공 응답
```json
{
  "success": true,
  "message": "요청이 성공적으로 처리되었습니다",
  "data": {
    // 응답 데이터
  },
  "timestamp": "2023-12-01T10:30:00Z"
}
```

### 에러 응답
```json
{
  "success": false,
  "message": "에러가 발생했습니다",
  "error": "상세 에러 정보",
  "timestamp": "2023-12-01T10:30:00Z"
}
```

### 페이징 응답
```json
{
  "success": true,
  "message": "목록 조회가 완료되었습니다",
  "data": {
    "content": [
      // 데이터 목록
    ],
    "totalElements": 100,
    "totalPages": 5,
    "size": 20,
    "number": 0,
    "first": true,
    "last": false,
    "empty": false
  },
  "timestamp": "2023-12-01T10:30:00Z"
}
```

## 🔍 검색 및 필터링

### 페이징 파라미터
- `page`: 페이지 번호 (0부터 시작, 기본값: 0)
- `size`: 페이지 크기 (기본값: 20, 최대: 100)
- `sort`: 정렬 조건 (예: `name,asc` 또는 `createdAt,desc`)

### 검색 파라미터 예시
```http
GET /api/hr/employees?page=0&size=10&sort=name,asc&departmentId=1&status=ACTIVE&keyword=홍길동
```

## ⚠️ 에러 코드

| HTTP 상태 | 에러 코드 | 설명 |
|-----------|-----------|------|
| 400 | BAD_REQUEST | 잘못된 요청 데이터 |
| 401 | UNAUTHORIZED | 인증 실패 |
| 403 | FORBIDDEN | 권한 없음 |
| 404 | NOT_FOUND | 리소스 없음 |
| 409 | CONFLICT | 데이터 충돌 |
| 422 | UNPROCESSABLE_ENTITY | 유효성 검증 실패 |
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |

## 🚀 사용 예시

### 1. 로그인 후 직원 목록 조회
```bash
# 1. 로그인
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "admin123"
  }'

# 2. 직원 목록 조회
curl -X GET http://localhost:8080/api/hr/employees \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. 새 고객 등록
```bash
curl -X POST http://localhost:8080/api/sales/customers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerCode": "CUST001",
    "customerName": "ABC 회사",
    "customerType": "CORPORATE",
    "contactPerson": "김담당",
    "contactEmail": "kim@abc.com",
    "contactPhone": "02-1234-5678",
    "address": "서울시 강남구 테헤란로 123"
  }'
```

### 3. 대시보드 데이터 조회
```bash
curl -X GET http://localhost:8080/api/dashboard/overview/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🔧 개발 환경 설정

### 1. 서버 시작
```bash
cd backend
./gradlew bootRun
# 또는
./mvnw spring-boot:run
```

### 2. API 문서 접근
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- API 소개: http://localhost:8080/api-docs.html

### 3. 데이터베이스 초기화
```sql
-- 샘플 데이터 로드
source database/seed/06_sample_data.sql
```

## 📞 지원 및 문의

- **개발팀 이메일**: dev@erp-system.com
- **기술 지원**: tech-support@erp-system.com
- **GitHub**: https://github.com/company/erp-system

## 📝 변경 로그

### v1.0.0 (2023-12-01)
- ✅ 초기 API 문서 생성
- ✅ 인증 시스템 구현
- ✅ 인사관리 모듈 완성
- ✅ 영업관리 모듈 완성
- ✅ 재고관리 모듈 완성
- ✅ 회계관리 모듈 완성
- ✅ 대시보드 모듈 완성
- ✅ Swagger UI 통합

---

**🎉 ERP 시스템 API 문서 v1.0.0**  
*최종 업데이트: 2023년 12월 1일*




