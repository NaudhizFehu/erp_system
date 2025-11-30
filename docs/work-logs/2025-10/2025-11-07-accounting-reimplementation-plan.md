# 회계 모듈 재구현 계획서

**작성일**: 2025-11-07
**기준 문서**:
- claudedocs/회계관리_모듈_구현계획.md
- docs/work-logs/2025-10/2025-11-07-accounting-implementation-status.md

**목표**: PHASE 1 (계정과목 관리) 완성

---

## 🎯 재구현 목표

### 완성 기준
1. ✅ 계정과목 목록 조회 (검색/필터/페이지네이션)
2. ✅ 계정과목 등록 (폼 유효성 검증)
3. ✅ 계정과목 수정 (폼 유효성 검증)
4. ✅ 계정과목 삭제
5. ✅ 계정과목 상세 조회
6. ✅ 계정과목 트리 구조 관리
7. ✅ 계정코드 중복 확인
8. ✅ 계층 구조 검증

### 예상 총 소요 시간
**5.25시간** (백엔드 테스트 포함)

---

## 📋 작업 단계

## STEP 0: 백엔드 API 확인 및 테스트 ⏱️ 30분

### 목적
프론트엔드 작업 전 백엔드 API가 정상 작동하는지 확인

### 작업 내용

#### 1. 백엔드 서버 실행 확인
```bash
# IntelliJ에서 Application.java 실행 확인
# 브라우저에서 확인
http://localhost:8080
```

#### 2. Swagger UI 접속
```bash
http://localhost:8080/swagger-ui.html
```

#### 3. Accounting API 확인
Swagger UI에서 다음 API들이 존재하는지 확인:

```
필수 API 목록:
1. GET    /api/accounting/accounts
   - 계정과목 목록 조회 (페이지네이션)

2. GET    /api/accounting/accounts/{id}
   - 계정과목 상세 조회

3. POST   /api/accounting/accounts
   - 계정과목 등록

4. PUT    /api/accounting/accounts/{id}
   - 계정과목 수정

5. DELETE /api/accounting/accounts/{id}
   - 계정과목 삭제

6. GET    /api/accounting/accounts/tree
   - 계정과목 트리 구조 조회

7. GET    /api/accounting/accounts/check/code
   - 계정코드 중복 확인
```

#### 4. API 테스트 (Swagger UI 사용)

**테스트 1: 목록 조회**
```
GET /api/accounting/accounts
Parameters:
- page: 0
- size: 20

Expected Response: 200 OK
{
  "content": [],
  "totalElements": 0,
  "totalPages": 0,
  ...
}
```

**테스트 2: 계정과목 등록**
```
POST /api/accounting/accounts
Body:
{
  "accountCode": "1000",
  "accountName": "현금",
  "accountType": "ASSET",
  "accountCategory": "CURRENT_ASSET",
  "debitCreditType": "DEBIT",
  "accountLevel": 1,
  "isActive": true,
  "trackBalance": true,
  "companyId": 1
}

Expected Response: 201 Created
```

**테스트 3: 트리 조회**
```
GET /api/accounting/accounts/tree

Expected Response: 200 OK
[
  {
    "id": 1,
    "accountCode": "1000",
    "accountName": "현금",
    "accountLevel": 1,
    "isActive": true,
    "children": []
  }
]
```

#### 5. 응답 데이터 구조 확인
프론트엔드 타입(accounting.ts)과 백엔드 응답 데이터 구조가 일치하는지 확인

### 완료 기준
- [ ] 백엔드 서버 정상 실행
- [ ] Swagger UI 접속 가능
- [ ] 7개 API 모두 존재
- [ ] 목록 조회 API 테스트 성공 (빈 배열이어도 OK)
- [ ] 등록 API 테스트 성공
- [ ] 트리 조회 API 테스트 성공
- [ ] 응답 데이터 구조가 프론트엔드 타입과 일치

### 트러블슈팅

**문제: 백엔드 서버가 실행되지 않음**
```
해결:
1. IntelliJ에서 Application.java 우클릭 → Run
2. 콘솔에서 에러 확인
3. 포트 충돌 확인 (8080 포트 사용 중인지)
```

**문제: Swagger UI에 API가 보이지 않음**
```
해결:
1. AccountController.java 파일 확인
2. @RestController, @RequestMapping 어노테이션 확인
3. 백엔드 재빌드 및 재시작
```

**문제: API 호출 시 404 에러**
```
해결:
1. URL 경로 확인 (/api/accounting/accounts)
2. AccountController.java의 @RequestMapping 경로 확인
3. 백엔드 로그 확인
```

---

## STEP 1: AccountList 페이지 API 연동 ⏱️ 1시간

### 목적
Mock 데이터를 제거하고 실제 API와 연동하여 완전한 기능 구현

### 현재 상태
- ✅ 페이지 레이아웃 완성
- ✅ 테이블 UI 완성
- ❌ Mock 데이터 사용 중
- ❌ 검색 기능 미구현
- ❌ 필터 기능 미구현
- ❌ 페이지네이션 미구현

### Cursor AI 프롬프트

```
📝 Cursor에게 이렇게 요청하세요:

"frontend/src/pages/accounting/AccountList.tsx 파일을 수정해줘.

현재 Mock 데이터를 사용하고 있는데, 실제 API와 연동하도록 변경해줘.

수정 사항:

1. useAccounts 훅 사용
   - src/hooks/useAccounts.ts의 useAccounts 훅 import
   - Mock 데이터 배열 제거
   - useAccounts({ page, size, searchTerm, ... }) 호출
   - data, isLoading, error 사용

2. 검색 기능 구현
   - 검색어 상태 관리 (useState)
   - 디바운싱 적용 (300ms) - useDebounce 훅 사용
   - 검색어 변경 시 API 재호출

3. 필터링 구현
   - 계정 타입 필터 (Select 또는 Tabs)
     - 전체, 자산, 부채, 자본, 수익, 비용
   - 계정 레벨 필터 (Select)
     - 전체, 대분류(1), 중분류(2), 소분류(3)
   - 사용 여부 필터 (Select)
     - 전체, 사용, 미사용

4. 페이지네이션 구현
   - 현재 페이지 상태 관리
   - 페이지 크기 (20개)
   - 페이지네이션 컴포넌트 추가
   - 페이지 변경 시 API 재호출

5. 통계 카드 API 연동
   - 전체 계정 수: data.totalElements
   - 총 자산: 필터링해서 계산 또는 별도 API
   - 활성 계정: 필터링해서 계산

6. 로딩/에러 상태 처리
   - isLoading이면 Skeleton 또는 Spinner 표시
   - error가 있으면 에러 메시지 표시
   - 데이터가 없으면 "데이터가 없습니다" 메시지

7. 액션 버튼 연결
   - "계정 추가" 버튼 → AccountForm 모달 열기 (다음 단계에서 구현)
   - "필터" 버튼 → 필터 섹션 토글
   - "새로고침" 버튼 추가 → refetch 호출
   - "엑셀 다운로드" 버튼 추가 (기능은 나중에)

8. 각 행 액션
   - MoreHorizontal 버튼 클릭 → Dropdown 메뉴
     - "상세보기" → /accounting/accounts/{id}로 이동
     - "수정" → AccountForm 모달 (수정 모드)
     - "삭제" → 확인 다이얼로그 → useDeleteAccount 호출

기존 EmployeeManagement.tsx 파일을 참고해서
비슷한 구조로 만들어줘.

반드시 TypeScript 타입을 정확하게 사용하고,
에러 핸들링도 완벽하게 해줘.
"
```

### 테스트 방법

**테스트 시나리오 1: 목록 조회**
1. `/accounting/accounts` 접속
2. 로딩 스피너 표시 확인
3. 계정과목 목록 테이블 표시 확인
4. 데이터가 있으면 테이블에 표시
5. 데이터가 없으면 "데이터가 없습니다" 메시지

**테스트 시나리오 2: 검색**
1. 검색창에 "현금" 입력
2. 300ms 대기
3. 검색 결과 표시 확인
4. 검색어 삭제 → 전체 목록 복원

**테스트 시나리오 3: 필터**
1. 계정 타입 필터에서 "자산" 선택
2. 자산 계정만 표시 확인
3. 계정 레벨 필터에서 "대분류" 선택
4. 대분류 자산 계정만 표시 확인
5. 필터 초기화 → 전체 목록

**테스트 시나리오 4: 페이지네이션**
1. 계정이 20개 이상 있는 경우
2. 페이지네이션 버튼 표시 확인
3. "다음" 버튼 클릭 → 2페이지 표시
4. "이전" 버튼 클릭 → 1페이지 복원

**테스트 시나리오 5: 삭제**
1. 계정 행의 More 버튼 클릭
2. "삭제" 선택
3. 확인 다이얼로그 표시
4. "삭제" 클릭
5. Toast 알림 "계정과목이 삭제되었습니다"
6. 목록에서 해당 계정 사라짐

### 완료 기준
- [ ] useAccounts 훅 사용
- [ ] 검색 작동 (디바운싱)
- [ ] 필터 작동 (타입/레벨/사용여부)
- [ ] 페이지네이션 작동
- [ ] 통계 카드 실시간 계산
- [ ] 로딩 상태 표시
- [ ] 에러 상태 표시
- [ ] 빈 상태 표시
- [ ] 삭제 기능 작동
- [ ] 상세보기 링크 작동
- [ ] TypeScript 에러 없음
- [ ] 린트 에러 없음

---

## STEP 2: AccountForm 컴포넌트 구현 ⏱️ 2시간

### 목적
계정과목 등록/수정 폼 컴포넌트 구현

### Cursor AI 프롬프트

```
📝 Cursor에게 이렇게 요청하세요:

"frontend/src/components/accounting/AccountForm.tsx 파일을 새로 만들어줘.

계정과목 등록/수정 폼 컴포넌트야.

Props:
- account?: Account (수정 모드일 때 전달)
- onSubmit: (data: AccountCreateRequest) => void
- onCancel: () => void
- isSubmitting: boolean
- mode: 'create' | 'edit'

폼 필드:

1. 계정코드 (필수)
   - Input
   - 계정 레벨에 따라 자리수 제한
     - 대분류(1): 4자리
     - 중분류(2): 6자리
     - 소분류(3): 8자리
   - 중복 확인 버튼
   - useCheckAccountCode 훅 사용
   - 중복이면 에러 메시지

2. 계정명 (필수)
   - Input
   - 최대 100자

3. 계정명 영문 (선택)
   - Input
   - 최대 100자

4. 회사 (필수, 숨김)
   - useAuth()로 현재 회사 ID 가져오기
   - Hidden input

5. 계정 타입 (필수)
   - Select
   - AccountType Enum 사용
   - 옵션: 자산, 부채, 자본, 수익, 비용
   - KOREAN_LABELS 매핑 사용

6. 계정 분류 (필수)
   - Select
   - AccountCategory Enum 사용
   - 계정 타입에 따라 옵션 필터링
     - 자산: 유동자산, 비유동자산
     - 부채: 유동부채, 비유동부채
     - 자본: 납입자본, 이익잉여금
     - 수익: 영업수익, 영업외수익
     - 비용: 영업비용, 영업외비용

7. 차대구분 (필수)
   - Select
   - DebitCreditType Enum 사용
   - 옵션: 차변, 대변
   - 계정 타입에 따라 기본값 설정
     - 자산/비용: 차변
     - 부채/자본/수익: 대변

8. 계정 레벨 (필수)
   - Select
   - 옵션: 1(대분류), 2(중분류), 3(소분류)

9. 상위 계정 (조건부 필수)
   - Combobox (드롭다운 + 검색)
   - useAccountTree 훅으로 계정 트리 가져오기
   - 계정 레벨에 따라 필터링:
     - 대분류(1): 상위 계정 없음 (비활성화)
     - 중분류(2): 대분류 계정만 선택 가능
     - 소분류(3): 중분류 계정만 선택 가능
   - 자기 자신 제외
   - 계정 타입이 같은 계정만 표시

10. 정렬 순서 (선택)
    - Number Input
    - 기본값: 0

11. 사용 여부 (필수)
    - Switch/Toggle
    - 기본값: true

12. 잔액 추적 (필수)
    - Switch/Toggle
    - 기본값: true

13. 기초 잔액 (선택)
    - Number Input
    - 1,000 단위 콤마

14. 예산 금액 (선택)
    - Number Input
    - 1,000 단위 콤마

15. 세금 코드 (선택)
    - Input

16. 설명 (선택)
    - Textarea
    - 최대 500자

유효성 검증 (Zod):
- 계정코드: 필수, 중복 확인
- 계정명: 필수, 2자 이상
- 계정 타입: 필수
- 계정 분류: 필수
- 차대구분: 필수
- 계정 레벨: 필수
- 상위 계정:
  - 중분류/소분류일 때 필수
  - 상위 계정의 타입이 현재 계정 타입과 일치
  - 자기 자신 선택 불가

버튼:
- 취소 (onCancel 호출)
- 저장 (폼 제출)
- 로딩 중일 때 버튼 비활성화 및 로딩 표시

React Hook Form + Zod를 사용하고,
EmployeeForm.tsx를 참고해서 비슷한 구조로 만들어줘.

에러 메시지는 한국어로 사용자 친화적으로 작성해줘.
"
```

### 테스트 방법

**테스트 시나리오 1: 대분류 계정 등록**
1. AccountList에서 "계정 추가" 클릭
2. AccountForm 모달 열림
3. 계정코드: "1000"
4. 계정명: "자산"
5. 계정 타입: "자산"
6. 계정 분류: "유동자산"
7. 차대구분: "차변" (자동 선택)
8. 계정 레벨: "1"
9. 상위 계정: 비활성화
10. 사용 여부: ON
11. "저장" 클릭
12. Toast: "계정과목이 등록되었습니다"
13. 모달 닫힘
14. 목록 새로고침

**테스트 시나리오 2: 중분류 계정 등록**
1. "계정 추가" 클릭
2. 계정코드: "110000"
3. 계정명: "유동자산"
4. 계정 타입: "자산"
5. 계정 분류: "유동자산"
6. 계정 레벨: "2"
7. 상위 계정: "1000 자산" 선택
8. "저장" 클릭
9. 성공 확인

**테스트 시나리오 3: 유효성 검증**
1. "계정 추가" 클릭
2. 아무것도 입력하지 않고 "저장"
3. 필수 필드 에러 메시지 표시
   - "계정코드를 입력해주세요"
   - "계정명을 입력해주세요"
   - ...
4. 계정코드 "1000" 입력 (기존 코드)
5. 중복 확인 → "이미 사용 중인 계정코드입니다"
6. 다른 코드로 변경 → 중복 확인 통과

**테스트 시나리오 4: 계정 수정**
1. 목록에서 계정 More 버튼 → "수정"
2. AccountForm 모달 (수정 모드)
3. 기존 데이터 채워져 있음
4. 계정명 변경
5. "저장" 클릭
6. Toast: "계정과목이 수정되었습니다"
7. 목록에서 변경사항 확인

**테스트 시나리오 5: 계층 구조 검증**
1. 중분류 계정 등록 시도
2. 계정 레벨: "2"
3. 상위 계정: 소분류 선택 시도
4. 소분류는 드롭다운에 표시 안됨 (필터링)
5. 대분류만 선택 가능 확인

### 완료 기준
- [ ] 폼 렌더링 정상
- [ ] 모든 필드 작동
- [ ] 유효성 검증 작동
- [ ] 계정코드 중복 확인 작동
- [ ] 계층 구조 검증 작동
- [ ] 등록 기능 작동
- [ ] 수정 기능 작동
- [ ] 계정 타입별 필터링 작동
- [ ] 에러 핸들링 완벽
- [ ] TypeScript 에러 없음
- [ ] 린트 에러 없음

---

## STEP 3: AccountDetail 페이지 구현 ⏱️ 1.5시간

### 목적
계정과목 상세 정보 페이지 구현

### Cursor AI 프롬프트

```
📝 Cursor에게 이렇게 요청하세요:

"frontend/src/pages/accounting/AccountDetail.tsx 파일을 새로 만들어줘.

계정과목 상세 정보를 보여주는 페이지야.

URL 파라미터:
- id: 계정과목 ID (useParams()로 가져오기)

훅 사용:
- useAccount(id): 계정과목 상세 조회
- useAccountTree(companyId): 하위 계정 트리 조회
- useUpdateAccount(): 수정 mutation
- useDeleteAccount(): 삭제 mutation

섹션:

1. 헤더
   - 뒤로가기 버튼 (← 계정 관리)
   - 계정코드 + 계정명 (제목)
   - 상태 Badge (활성/비활성)
   - 액션 버튼:
     - 수정 → AccountForm 모달 (수정 모드)
     - 삭제 → 확인 다이얼로그 → 삭제 → 목록으로 이동

2. 기본 정보 카드
   좌측:
   - 계정코드
   - 계정명 (한글)
   - 계정명 (영문)
   - 계정 타입 (Badge)
   - 계정 분류 (Badge)
   - 차대구분 (Badge)

   우측:
   - 계정 레벨
   - 상위 계정 (링크 → 상위 계정 상세 페이지)
   - 정렬 순서
   - 사용 여부 (Switch, 변경 불가)
   - 잔액 추적 (Switch, 변경 불가)
   - 등록일
   - 수정일

3. 잔액 정보 카드
   - 기초 잔액: formatCurrency(account.openingBalance)
   - 현재 잔액: formatCurrency(account.currentBalance)
   - 예산 금액: formatCurrency(account.budgetAmount)

4. 하위 계정 목록 (Tabs)
   - useAccountTree로 전체 트리 가져오기
   - 현재 계정의 children만 필터링
   - 트리 구조로 표시
   - 각 하위 계정 클릭 → 해당 계정 상세 페이지
   - "하위 계정 추가" 버튼 → AccountForm (상위 계정 자동 선택)

5. 최근 거래 내역 (Tabs) - PHASE 2 이후 구현
   - 일단 "전표 기능 구현 후 표시됩니다" 메시지만

6. 설명 카드
   - account.description 표시

로딩/에러 상태:
- isLoading: Skeleton 또는 Spinner
- error: 에러 메시지
- 데이터 없음 (404): "계정과목을 찾을 수 없습니다"

EmployeeDetail.tsx를 참고해서 비슷한 구조로 만들어줘.
"
```

### 테스트 방법

**테스트 시나리오 1: 상세 정보 조회**
1. 목록에서 계정 클릭
2. 상세 페이지로 이동 (/accounting/accounts/{id})
3. 로딩 스피너 표시
4. 모든 정보 올바르게 표시 확인
5. 상위 계정 링크 클릭 → 상위 계정 상세 페이지

**테스트 시나리오 2: 하위 계정 트리**
1. 대분류 계정 (예: 1000 자산) 상세 페이지
2. "하위 계정" 탭 클릭
3. 하위 계정 목록 트리 형태로 표시
4. 중분류 계정 클릭 → 해당 계정 상세 페이지
5. 소분류 계정 확인

**테스트 시나리오 3: 수정**
1. "수정" 버튼 클릭
2. AccountForm 모달 열림 (수정 모드)
3. 기존 데이터 채워져 있음
4. 계정명 변경
5. "저장" 클릭
6. Toast 알림
7. 상세 페이지 새로고침
8. 변경사항 반영 확인

**테스트 시나리오 4: 삭제**
1. "삭제" 버튼 클릭
2. 확인 다이얼로그
   - "정말 삭제하시겠습니까?"
   - "이 작업은 되돌릴 수 없습니다."
3. "삭제" 클릭
4. Toast: "계정과목이 삭제되었습니다"
5. 목록 페이지로 자동 이동

**테스트 시나리오 5: 존재하지 않는 계정**
1. URL 직접 입력: /accounting/accounts/999999
2. 404 에러 메시지 표시
3. "계정 목록으로 돌아가기" 버튼

### 완료 기준
- [ ] 상세 정보 모두 표시
- [ ] 하위 계정 트리 표시
- [ ] 상위 계정 링크 작동
- [ ] 수정 기능 작동
- [ ] 삭제 기능 작동
- [ ] 로딩 상태 표시
- [ ] 에러 상태 표시
- [ ] 404 처리
- [ ] 반응형 레이아웃
- [ ] TypeScript 에러 없음

---

## STEP 4: 라우팅 설정 완료 ⏱️ 15분

### 목적
AccountDetail 페이지 라우트 추가

### Cursor AI 프롬프트

```
📝 Cursor에게 이렇게 요청하세요:

"frontend/src/App.tsx 파일을 수정해줘.

AccountDetail 컴포넌트를 import하고,
라우트를 추가해줘.

추가할 라우트:
- path="/accounting/accounts/:id"
- element={<AccountDetail />}
- 권한: ADMIN 이상

기존 직원 관리 라우트 패턴을 참고해서
동일하게 만들어줘.
"
```

### 테스트 방법
1. `/accounting/accounts` → AccountList 페이지
2. 계정 클릭 → `/accounting/accounts/1` → AccountDetail 페이지
3. 일반 사용자 로그인 → 접근 차단 (권한 없음)
4. 관리자 로그인 → 접근 허용

### 완료 기준
- [ ] AccountDetail import
- [ ] 라우트 추가
- [ ] 권한 체크 (ADMIN 이상)
- [ ] 페이지 네비게이션 작동
- [ ] 404 처리 (존재하지 않는 ID)

---

## STEP 5: AccountList 모달 통합 ⏱️ 15분

### 목적
AccountList에서 AccountForm 모달 열기/닫기 통합

### Cursor AI 프롬프트

```
📝 Cursor에게 이렇게 요청하세요:

"AccountList.tsx를 수정해줘.

AccountForm 컴포넌트를 모달로 사용하도록 통합해줘.

1. AccountForm import
2. Modal/Dialog 컴포넌트 사용 (Radix UI Dialog)
3. 상태 관리:
   - isFormOpen: boolean
   - formMode: 'create' | 'edit'
   - selectedAccount: Account | undefined

4. "계정 추가" 버튼 클릭:
   - isFormOpen = true
   - formMode = 'create'
   - selectedAccount = undefined

5. "수정" 버튼 클릭:
   - isFormOpen = true
   - formMode = 'edit'
   - selectedAccount = account

6. onSubmit:
   - create: useCreateAccount mutation
   - edit: useUpdateAccount mutation
   - 성공 시 모달 닫기

7. onCancel:
   - 모달 닫기

EmployeeManagement.tsx를 참고해서 만들어줘.
"
```

### 완료 기준
- [ ] AccountForm 모달 통합
- [ ] 등록 모달 작동
- [ ] 수정 모달 작동
- [ ] 모달 열기/닫기 작동
- [ ] 등록/수정 성공 시 목록 새로고침

---

## 🎯 최종 테스트 시나리오 (통합 테스트)

### 시나리오 1: 계정 계층 구조 생성
1. **대분류 등록 (자산)**
   - 계정코드: 1000
   - 계정명: 자산
   - 계정 레벨: 1

2. **중분류 등록 (유동자산)**
   - 계정코드: 110000
   - 계정명: 유동자산
   - 계정 레벨: 2
   - 상위 계정: 1000 자산

3. **소분류 등록 (현금)**
   - 계정코드: 11010000
   - 계정명: 현금
   - 계정 레벨: 3
   - 상위 계정: 110000 유동자산

4. **트리 구조 확인**
   - 1000 자산 상세 페이지
   - 하위 계정 탭
   - └ 110000 유동자산
   -   └ 11010000 현금

### 시나리오 2: 검색 및 필터
1. 목록 페이지에서 "현금" 검색
2. 결과: 11010000 현금 표시
3. 계정 타입 "자산" 필터
4. 결과: 자산 계정만 표시
5. 계정 레벨 "3" 필터
6. 결과: 소분류 자산 계정만 표시

### 시나리오 3: 수정 및 삭제
1. 11010000 현금 수정
2. 계정명: "현금 및 현금성자산"으로 변경
3. 저장 → 성공
4. 상세 페이지에서 변경 확인
5. 11010000 계정 삭제
6. 확인 → 삭제
7. 목록에서 사라짐 확인
8. 110000 유동자산 상세 → 하위 계정 없음

---

## ✅ PHASE 1 완료 체크리스트

### 기능 완성도
- [ ] 계정과목 목록 조회 (페이지네이션)
- [ ] 계정과목 검색 (디바운싱)
- [ ] 계정과목 필터링 (타입, 레벨, 사용여부)
- [ ] 계정과목 등록
- [ ] 계정과목 수정
- [ ] 계정과목 삭제
- [ ] 계정과목 상세 조회
- [ ] 계정코드 중복 확인
- [ ] 계정 계층 구조 관리
- [ ] 하위 계정 트리 표시

### 코드 품질
- [ ] TypeScript 에러 없음 (`npm run type-check`)
- [ ] 린트 에러 없음 (`npm run lint`)
- [ ] 콘솔 에러 없음
- [ ] 네트워크 에러 없음
- [ ] 반응형 레이아웃

### 사용자 경험
- [ ] 로딩 상태 표시 (Skeleton/Spinner)
- [ ] 에러 메시지 표시 (Toast)
- [ ] 빈 상태 처리 (Empty State)
- [ ] Toast 알림 작동
- [ ] 권한 기반 UI

### 문서화
- [ ] 주요 컴포넌트에 JSDoc 주석
- [ ] 복잡한 로직에 코드 주석
- [ ] 작업 로그 문서 작성

---

## 🚀 배포 전 최종 체크

### 1. 로컬 테스트
```bash
# 프론트엔드
cd frontend
npm run dev
# → http://localhost:5173

# 백엔드
# IntelliJ에서 Application.java 실행
# → http://localhost:8080
```

### 2. 품질 검사
```bash
# TypeScript 타입 체크
npm run type-check

# 린트 검사
npm run lint

# 빌드 테스트
npm run build
```

### 3. 브라우저 테스트
- [ ] Chrome에서 기능 테스트
- [ ] 개발자 도구 콘솔 에러 없음
- [ ] 네트워크 탭에서 API 호출 확인
- [ ] 반응형 테스트 (모바일/태블릿/데스크톱)

### 4. 배포
```bash
# 로컬 Docker 환경에서 테스트
docker-compose -f docker-compose.dev.yml up -d

# 기능 테스트
# → http://localhost:5173

# 문제 없으면 Git 커밋
git add .
git commit -m "feat: PHASE 1 계정과목 관리 완성

- 계정과목 목록 조회 (검색/필터/페이지네이션)
- 계정과목 등록/수정/삭제
- 계정과목 상세 조회
- 계정 계층 구조 관리
- 계정코드 중복 확인

테스트:
- 로컬 환경 테스트 완료
- Docker 환경 테스트 완료
- 린트/타입 체크 통과"

# develop 브랜치에 푸시
git push origin develop

# 운영 서버 배포 (배포 가이드 참조)
# ./scripts/deploy-production.sh
```

---

## 📝 작업 후 문서 업데이트

### 1. 작업 로그 작성
```
docs/work-logs/2025-10/2025-11-07-accounting-phase1-completed.md
```

내용:
- 구현 완료 항목
- 테스트 결과
- 발견된 문제 및 해결 방법
- 다음 단계 (PHASE 2) 계획

### 2. README 업데이트 (선택)
회계 모듈 섹션 추가

---

## 🎉 다음 단계

### PHASE 2: 전표 처리 (11시간)
PHASE 1이 완전히 완료되고 안정화된 후 시작

**시작 전 확인사항**:
- [ ] PHASE 1 모든 기능 정상 작동
- [ ] 사용자 피드백 반영 완료
- [ ] 버그 수정 완료
- [ ] 운영 서버 배포 및 안정화

---

**재구현 계획 작성자**: Claude Code
**계획 버전**: 1.0
**다음 업데이트**: 각 STEP 완료 후
