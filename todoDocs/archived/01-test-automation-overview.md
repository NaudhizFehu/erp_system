# 테스트 자동화 활성화 - 개요

**작업 번호**: 1
**우선순위**: 최고
**예상 시간**: 1시간 45분
**담당**: Backend + Frontend + DevOps

---

## 📋 목표

Backend와 Frontend의 테스트를 CI/CD 파이프라인에서 자동 실행하여 코드 품질을 보장하고, 버그가 프로덕션까지 도달하는 것을 방지합니다.

---

## 🎯 작업 범위

### 현재 상태
- ❌ Backend: `-DskipTests` 플래그로 테스트 건너뜀
- ❌ Frontend: 테스트 설정이 없거나 주석 처리됨
- ❌ CI/CD: 테스트 없이 빌드만 수행

### 목표 상태
- ✅ Backend: Maven 테스트 자동 실행
- ✅ Frontend: Vitest 기반 테스트 자동 실행
- ✅ CI/CD: 테스트 실패 시 빌드 중단 및 PR 머지 차단

---

## 📂 작업 단계

### Phase 1: Backend 테스트 활성화 (30분)
**문서**: `02-backend-test-activation.md`
- GitHub Actions 워크플로우 수정
- `-DskipTests` 플래그 제거
- 테스트 결과 업로드 설정
- 로컬 및 CI 테스트

### Phase 2: Frontend 테스트 설정 (45분)
**문서**: `03-frontend-test-setup.md`
- Vitest 설치 및 설정
- 테스트 설정 파일 생성
- 샘플 테스트 작성
- 로컬 테스트 실행

### Phase 3: Frontend CI 워크플로우 수정 (15분)
**문서**: `04-frontend-ci-workflow.md`
- GitHub Actions 워크플로우 수정
- 테스트 실행 단계 추가
- 커버리지 리포트 설정

### Phase 4: 통합 테스트 및 검증 (15분)
**문서**: `05-integration-testing.md`
- 전체 플로우 테스트
- 실패 시나리오 검증
- PR 머지 차단 확인

---

## 🚨 주요 위험 요소

### 1. 기존 테스트 부재
**문제**: 테스트가 없어서 CI가 실패할 수 있음
**해결**: 최소 1개 이상의 통과하는 테스트 작성

### 2. 빌드 시간 증가
**문제**: 테스트 실행으로 CI 시간 증가
**해결**: 캐싱 활용, 병렬 실행 고려

### 3. 설정 충돌
**문제**: Vitest 설정이 기존 Vite 설정과 충돌
**해결**: 별도 vitest.config.ts 파일 사용

---

## 📊 성공 기준

### Backend ✅
- [ ] CI에서 `mvn clean package` 실행 성공
- [ ] 테스트 실패 시 빌드 중단
- [ ] GitHub Actions에서 테스트 결과 확인 가능

### Frontend ✅
- [ ] `npm run test` 로컬에서 성공
- [ ] CI에서 테스트 자동 실행
- [ ] 최소 1개 이상의 컴포넌트 테스트 존재
- [ ] 테스트 커버리지 리포트 생성

### CI/CD ✅
- [ ] PR 생성 시 자동 테스트 실행
- [ ] 테스트 실패 시 PR 머지 차단
- [ ] 테스트 결과 GitHub에 표시

---

## ⏱️ 예상 소요 시간

| Phase | 작업 내용 | 시간 |
|-------|----------|------|
| 1 | Backend 테스트 활성화 | 30분 |
| 2 | Frontend 테스트 설정 | 45분 |
| 3 | Frontend CI 워크플로우 | 15분 |
| 4 | 통합 테스트 및 검증 | 15분 |
| **합계** | | **1시간 45분** |

---

## 📝 선택적 개선사항 (별도 시간)

### Option A: SonarQube 통합 (+1시간)
- 코드 품질 분석
- 품질 게이트 설정
- 기술 부채 추적

### Option B: 테스트 병렬 실행 (+30분)
- 여러 Node 버전 테스트
- 빌드 시간 단축

### Option C: E2E 테스트 추가 (+4시간)
- Playwright 기반 E2E 테스트
- 별도 워크플로우 구성

---

## 🔗 관련 문서

- `02-backend-test-activation.md` - Backend 테스트 활성화 상세
- `03-frontend-test-setup.md` - Frontend 테스트 설정 상세
- `04-frontend-ci-workflow.md` - Frontend CI 워크플로우 상세
- `05-integration-testing.md` - 통합 테스트 및 검증
- `06-troubleshooting.md` - 문제 해결 가이드

---

**작성일**: 2025-11-21
**버전**: 1.0
**상태**: 계획 단계
