# 회계 모듈 프론트엔드 서비스 추가 (accountService)

작업일: 2025-10-30
작업자: AI Assistant
관련 문서: claudedocs/회계관리_모듈_구현계획.md (264-278)

## 구현 내용
- 파일 추가: `frontend/src/services/accountService.ts`
- API 인스턴스: 기존 `frontend/src/services/api.ts`의 Axios 인스턴스 사용
- 타입 참조: `frontend/src/types/accounting.ts`

### 제공 함수
1. `getAccounts(params)` — 계정과목 목록 조회 (페이지네이션)
2. `getAccountById(id)` — 계정과목 상세 조회
3. `getAccountTree(companyId?)` — 계정과목 트리 구조 조회 (회사 기준)
4. `createAccount(data)` — 계정과목 등록 (companyId 포함 필요)
5. `updateAccount(id, data)` — 계정과목 수정
6. `deleteAccount(id)` — 계정과목 삭제
7. `checkAccountCode(code, companyId?, excludeId?)` — 계정코드 중복 확인

### 엔드포인트 매핑
- 베이스 경로: `/api/accounting/accounts`
- 컨트롤러 매핑과 일치: `AccountController`의 경로 구조 준수

## 예외 처리 및 응답 구조
- 모든 함수는 `api.ts` 인터셉터의 표준 에러 핸들링을 사용
- 성공 시 백엔드 `ApiResponse<T>`의 `data` 필드를 반환

## 체크 사항
- `createAccount`는 백엔드 요청 스키마에 따라 `companyId` 필수
- 트리 조회 시 `companyId` 미지정이면 백엔드에서 인증 사용자의 회사로 처리 가능

## 변경 파일 목록
- `frontend/src/services/accountService.ts` (신규)

## 비고
- 후속 작업: React Query 훅 래퍼 및 UI 연동 컴포넌트에서 서비스 함수 사용
