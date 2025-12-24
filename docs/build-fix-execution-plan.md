# ERP 시스템 빌드 오류 수정 실행 계획

**작성일**: 2025-01-15  
**목표**: 프론트엔드 정상 빌드 달성  
**예상 소요시간**: 4시간

## 🎯 실행 순서 및 명령어

### Phase 1: API 응답 처리 수정 (1-2시간)

#### 1.1 salesApi.ts 수정
**파일**: `frontend/src/services/salesApi.ts`

**수정 내용**: 모든 API 메서드에서 `response.data` → `response.data.data`로 변경

**수정할 메서드들**:
```typescript
// customerApi 객체 내부 (라인 66-275)
create: return response.data.data!
update: return response.data.data!
getById: return response.data.data!
delete: return response.data.data!
getList: return response.data.data!
search: return response.data.data!
getStats: return response.data.data!

// orderApi 객체 내부 (라인 440-870)
create: return response.data.data!
update: return response.data.data!
getById: return response.data.data!
// ... 모든 orderApi 메서드들

// 기타 API 객체들도 동일하게 수정
```

**실행 명령**:
```
"salesApi.ts 파일의 모든 return response.data! 를 return response.data.data! 로 수정해줘"
```

#### 1.2 hrApi.ts 수정
**파일**: `frontend/src/services/hrApi.ts`

**수정 내용**: 
1. 모든 API 메서드에서 `response.data` → `response.data.data`
2. `getRecentEmployeesByCompany` 메서드 응답 처리 수정

**실행 명령**:
```
"hrApi.ts 파일의 모든 return response.data! 를 return response.data.data! 로 수정해줘"
```

#### 1.3 dashboardApi.ts 수정
**파일**: `frontend/src/services/dashboardApi.ts`

**수정 내용**: 남은 모든 `dashboardApi` 호출을 `dashboardApiClient`로 변경

**실행 명령**:
```
"dashboardApi.ts에서 남은 모든 dashboardApi 호출을 dashboardApiClient로 변경해줘"
```

### Phase 2: TypeScript 설정 조정 (30분)

#### 2.1 tsconfig.json 수정
**파일**: `frontend/tsconfig.json`

**수정 내용**:
```json
{
  "compilerOptions": {
    "exactOptionalPropertyTypes": false,
    "noUncheckedIndexedAccess": false,
    "noUnusedLocals": false,
    "noUnusedParameters": false
  }
}
```

**실행 명령**:
```
"tsconfig.json에서 TypeScript strict 모드 설정을 임시로 완화해줘"
```

### Phase 3: 누락된 의존성 설치 (10분)

#### 3.1 의존성 설치
**실행 명령**:
```
"frontend 디렉토리에서 누락된 Radix UI 의존성들을 설치해줘"
```

**설치할 패키지들**:
```bash
npm install @radix-ui/react-scroll-area @radix-ui/react-slider
npm install --save-dev @types/node
```

### Phase 4: React Hook Form 타입 수정 (1시간)

#### 4.1 EmployeeForm.tsx 수정
**파일**: `frontend/src/components/hr/EmployeeForm.tsx`

**수정 내용**: useForm 훅의 타입 정의 수정

**실행 명령**:
```
"EmployeeForm.tsx의 React Hook Form 타입 오류를 수정해줘"
```

#### 4.2 UserProfilePage.tsx 수정
**파일**: `frontend/src/pages/profile/UserProfilePage.tsx`

**수정 내용**: Form 스키마 및 상태 타입 수정

**실행 명령**:
```
"UserProfilePage.tsx의 React Hook Form 타입 오류를 수정해줘"
```

### Phase 5: 사용하지 않는 Import 정리 (30분)

#### 5.1 주요 컴포넌트 정리
**대상 파일들**:
- `frontend/src/components/hr/EmployeeNumberHelper.tsx`
- `frontend/src/components/inventory/InventoryDashboard.tsx`
- `frontend/src/components/inventory/ProductForm.tsx`
- `frontend/src/components/inventory/ProductTable.tsx`
- `frontend/src/components/layout/Header.tsx`
- `frontend/src/components/notification/NotificationDropdown.tsx`
- `frontend/src/components/sales/CustomerCard.tsx`
- `frontend/src/components/sales/CustomerTable.tsx`
- `frontend/src/components/search/GlobalSearch.tsx`

**실행 명령**:
```
"사용하지 않는 import들을 정리해줘. 다음 파일들부터 시작: EmployeeNumberHelper.tsx, InventoryDashboard.tsx, ProductForm.tsx"
```

### Phase 6: 중간 빌드 테스트

#### 6.1 빌드 테스트
**실행 명령**:
```
"현재까지 수정된 내용으로 빌드 테스트를 실행해줘"
```

### Phase 7: 추가 오류 수정 (필요시)

#### 7.1 남은 타입 오류 수정
**실행 명령**:
```
"빌드에서 남은 오류들을 분석하고 수정해줘"
```

### Phase 8: 최종 빌드 및 검증

#### 8.1 최종 빌드 테스트
**실행 명령**:
```
"최종 빌드 테스트를 실행하고 성공하면 개발 서버도 테스트해줘"
```

## 📋 체크리스트

### Phase 1 체크리스트
- [ ] salesApi.ts - customerApi 모든 메서드 수정
- [ ] salesApi.ts - orderApi 모든 메서드 수정
- [ ] salesApi.ts - 기타 API 객체들 수정
- [ ] hrApi.ts - 모든 API 메서드 수정
- [ ] dashboardApi.ts - 남은 API 호출 수정

### Phase 2 체크리스트
- [ ] tsconfig.json - exactOptionalPropertyTypes: false
- [ ] tsconfig.json - noUncheckedIndexedAccess: false
- [ ] tsconfig.json - noUnusedLocals: false
- [ ] tsconfig.json - noUnusedParameters: false

### Phase 3 체크리스트
- [ ] @radix-ui/react-scroll-area 설치
- [ ] @radix-ui/react-slider 설치
- [ ] @types/node 설치

### Phase 4 체크리스트
- [ ] EmployeeForm.tsx 타입 오류 수정
- [ ] UserProfilePage.tsx 타입 오류 수정

### Phase 5 체크리스트
- [ ] EmployeeNumberHelper.tsx import 정리
- [ ] InventoryDashboard.tsx import 정리
- [ ] ProductForm.tsx import 정리
- [ ] ProductTable.tsx import 정리
- [ ] Header.tsx import 정리
- [ ] NotificationDropdown.tsx import 정리
- [ ] CustomerCard.tsx import 정리
- [ ] CustomerTable.tsx import 정리
- [ ] GlobalSearch.tsx import 정리

### Phase 6 체크리스트
- [ ] npm run build 실행
- [ ] 오류 개수 확인
- [ ] 주요 오류 분석

### Phase 7 체크리스트
- [ ] 남은 오류 분석
- [ ] 추가 수정 작업

### Phase 8 체크리스트
- [ ] 최종 빌드 성공
- [ ] npm run dev 테스트
- [ ] 기본 기능 동작 확인

## 🚨 주의사항

1. **순차적 실행**: 각 Phase를 완료한 후 다음 Phase로 진행
2. **빌드 테스트**: 각 Phase 후 빌드 테스트로 진행 상황 확인
3. **백업**: 중요한 수정 전 파일 백업 권장
4. **점진적 수정**: 한 번에 모든 파일을 수정하지 말고 단계별로 진행

## 📞 지원 요청

각 Phase에서 문제가 발생하면:
1. 오류 메시지 전체 복사
2. 어떤 파일을 수정 중이었는지 명시
3. 예상과 다른 결과가 나온 경우 상세 설명

---

**준비 완료**: 위 순서대로 명령을 주시면 단계별로 진행하겠습니다.

