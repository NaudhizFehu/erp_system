# 회계 모듈 React Query 훅 추가 (useAccounts)

작업일: 2025-10-30
작업자: AI Assistant
관련 문서: claudedocs/회계관리_모듈_구현계획.md (303-316)

## 구현 내용

### 파일 추가
- `frontend/src/hooks/useAccounts.ts`

### 참고 파일
- `frontend/src/hooks/useEmployees.ts` 패턴 참조
- `frontend/src/services/accountService.ts` 사용
- 타입: `frontend/src/types/accounting.ts`에서 import

### 구현된 훅

#### 1. useAccounts(params) - 계정과목 목록 조회
- **타입**: `useQuery`
- **파라미터**: `AccountListParams` (page, size, sort, searchTerm)
- **기능**: 계정과목 목록을 페이지네이션하여 조회
- **캐시 시간**: 5분 (staleTime)
- **재시도**: 3회

#### 2. useAccount(id) - 계정과목 상세 조회
- **타입**: `useQuery`
- **파라미터**: `id: number`
- **기능**: 특정 계정과목의 상세 정보 조회
- **캐시 시간**: 5분
- **재시도**: 3회
- **조건부 실행**: id가 있을 때만 실행 (enabled)

#### 3. useAccountTree(companyId?) - 계정과목 트리 구조 조회
- **타입**: `useQuery`
- **파라미터**: `companyId?: number` (선택)
- **기능**: 계정과목의 계층 구조를 트리 형태로 조회
- **캐시 시간**: 10분
- **재시도**: 3회

#### 4. useCreateAccount() - 계정과목 등록
- **타입**: `useMutation`
- **파라미터**: `AccountCreateRequest & { companyId: number }`
- **기능**: 새로운 계정과목 등록
- **성공 시**: 
  - 관련 쿼리 무효화 (invalidateQueries)
  - 성공 토스트 메시지 표시
- **실패 시**: 에러 토스트 메시지 표시

#### 5. useUpdateAccount() - 계정과목 수정
- **타입**: `useMutation`
- **파라미터**: `{ id: number; data: AccountUpdateRequest }`
- **기능**: 기존 계정과목 정보 수정
- **성공 시**:
  - 특정 계정과목 쿼리 업데이트 (setQueryData)
  - 목록 및 트리 쿼리 무효화
  - 성공 토스트 메시지 표시
- **실패 시**: 에러 토스트 메시지 표시

#### 6. useDeleteAccount() - 계정과목 삭제
- **타입**: `useMutation`
- **파라미터**: `id: number`
- **기능**: 계정과목 삭제
- **성공 시**:
  - 모든 관련 쿼리 무효화
  - 성공 토스트 메시지 표시
- **실패 시**: 에러 토스트 메시지 표시

#### 7. useCheckAccountCode() - 계정코드 중복 확인 (추가)
- **타입**: `useQuery`
- **파라미터**: `code: string, companyId?: number, excludeId?: number`
- **기능**: 계정코드 중복 여부 확인
- **캐시 시간**: 0 (실시간 검증)
- **재시도**: 1회
- **조건부 실행**: code가 있을 때만 실행

### 쿼리 키 관리
- `ACCOUNT_QUERY_KEYS` 상수 객체로 모든 쿼리 키를 체계적으로 관리
- 계층 구조: `all` → `lists` → `list(params)`, `details` → `detail(id)`, `tree(companyId)`

### 에러 처리
- 모든 훅에서 try-catch로 에러 처리
- React Query의 `onError` 콜백에서 toast 알림 표시
- 백엔드 응답의 `message` 필드를 우선 사용, 없으면 기본 메시지

### 성공 알림
- 모든 mutation 훅에서 성공 시 toast.success로 사용자 알림
- 한국어 메시지 사용

## 검증 완료
- ✅ TypeScript 컴파일 오류 없음 (`npm run type-check` 통과)
- ✅ ESLint 오류 없음
- ✅ React Query 패턴 준수 (useEmployees.ts와 일관성)
- ✅ Toast 알림 구현 완료

## 변경 파일 목록
- `frontend/src/hooks/useAccounts.ts` (신규)

## 다음 작업 제안
- UI 컴포넌트에서 훅 사용 예시 추가
- 계정과목 목록 페이지 구현
- 계정과목 등록/수정 폼 컴포넌트 구현
- 계정과목 트리 뷰 컴포넌트 구현

