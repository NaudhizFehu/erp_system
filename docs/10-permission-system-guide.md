# ERP 시스템 권한 체계 가이드

**작성일**: 2025-01-15  
**문서 목적**: ERP 시스템의 권한 구조와 각 계정별 접근 가능한 기능 설명

## 📋 목차
1. [권한 체계 개요](#권한-체계-개요)
2. [5단계 권한 구조](#5단계-권한-구조)
3. [현재 테스트 계정 목록](#현재-테스트-계정-목록)
4. [권한별 접근 가능 기능](#권한별-접근-가능-기능)
5. [데이터 접근 범위](#데이터-접근-범위)
6. [권한 체계 구현 상태](#권한-체계-구현-상태)

---

## 🎯 권한 체계 개요

ERP 시스템은 **5단계 권한 구조**를 가지고 있으며, 각 사용자는 **소속 회사**에 따라 데이터 접근 범위가 제한됩니다.

### 기본 원칙
1. **SUPER_ADMIN**: ERP 시스템 전체 관리 (모든 회사 데이터 접근)
2. **ADMIN**: 소속 회사 내 모든 데이터 관리
3. **MANAGER**: 소속 회사 내 데이터 조회 및 일부 관리
4. **USER**: 소속 회사 내 제한된 데이터 조회 및 자신의 데이터만 수정
5. **READONLY**: 소속 회사 내 데이터 조회만 가능

---

## 🔐 5단계 권한 구조

### 1. **SUPER_ADMIN (시스템 관리자)**
```
역할: ERP 시스템 전체 관리자
범위: 모든 회사의 모든 데이터
목적: 시스템 운영 및 전체 관리
```

**주요 권한**:
- ✅ **모든 회사** 데이터 조회/수정/삭제
- ✅ **회사 관리**: 새 회사 추가, 회사 정보 수정, 회사 삭제
- ✅ **사용자 관리**: 모든 회사의 사용자 계정 생성/수정/삭제
- ✅ **시스템 설정**: 전역 설정, 공통코드 관리
- ✅ **감사 로그**: 모든 시스템 활동 로그 조회

**접근 가능 데이터**:
- ABC기업 데이터 ✅
- XYZ그룹 데이터 ✅
- DEF코퍼레이션 데이터 ✅
- 시스템 전역 설정 ✅

---

### 2. **ADMIN (회사 관리자)**
```
역할: 소속 회사의 관리자
범위: 자신이 소속된 회사의 모든 데이터
목적: 회사 내 모든 업무 관리
```

**주요 권한**:
- ✅ **자사 직원** 전체 조회/등록/수정/삭제
- ✅ **자사 부서/직급** 관리
- ✅ **자사 사용자** 계정 생성/수정 (단, ADMIN 이하만 가능)
- ✅ **자사 영업/재고/회계** 데이터 전체 관리
- ✅ **자사 통계** 조회
- ❌ **다른 회사** 데이터 접근 불가

**접근 가능 데이터**:
- 자사 데이터 ✅
- 타사 데이터 ❌
- 시스템 전역 설정 ❌

---

### 3. **MANAGER (매니저)**
```
역할: 소속 회사의 부서 관리자 또는 팀장
범위: 자신이 소속된 회사의 데이터
목적: 회사 내 데이터 조회 및 일부 관리
특이사항: 부서별 추가 권한 가능
```

**주요 권한**:
- ✅ **자사 직원** 전체 조회, 일부 등록/수정 (삭제 불가)
- ✅ **자사 부서/직급** 조회
- ✅ **자사 영업/재고** 데이터 조회 및 수정
- ✅ **자사 통계** 조회
- ⚠️ **사용자 계정** 관리: **HR팀 매니저만 가능** (부서별 추가 권한)
- ❌ **자사 데이터 삭제** 권한 제한
- ❌ **다른 회사** 데이터 접근 불가

**부서별 추가 권한**:
- **HR팀 MANAGER**: 
  - ✅ 자사 사용자 계정 생성/수정 (ADMIN 권한 제외)
  - ✅ 직원 채용/퇴사 처리
  - ✅ 인사 평가 관리
- **영업팀 MANAGER**:
  - ✅ 견적 승인/거부
  - ✅ 주문 최종 승인
- **재고팀 MANAGER**:
  - ✅ 입출고 승인
  - ✅ 재고 조정 권한

**접근 가능 데이터**:
- 자사 데이터 ✅ (조회 위주, 일부 수정)
- 타사 데이터 ❌
- 시스템 전역 설정 ❌

---

### 4. **USER (일반 사용자)**
```
역할: 소속 회사의 일반 직원
범위: 자신이 소속된 회사의 제한된 데이터
목적: 일상 업무 처리
```

**주요 권한**:
- ✅ **자사 직원** 목록 조회 (상세 정보 제한)
- ✅ **자사 부서/직급** 조회
- ✅ **본인 정보** 수정
- ✅ **자신이 담당하는 업무** 데이터 조회/수정
- ❌ **타인 정보** 수정 불가
- ❌ **데이터 삭제** 권한 없음
- ❌ **통계 및 관리 기능** 접근 불가

**접근 가능 데이터**:
- 자사 기본 데이터 ✅ (조회만)
- 본인 데이터 ✅ (조회/수정)
- 타사 데이터 ❌

---

### 5. **READONLY (읽기 전용)**
```
역할: 데이터 조회만 가능한 사용자
범위: 자신이 소속된 회사의 데이터 (읽기만)
목적: 감사, 리포팅, 외부 협력자
```

**주요 권한**:
- ✅ **자사 데이터** 조회만 가능
- ❌ **모든 수정/삭제** 권한 없음
- ❌ **데이터 생성** 권한 없음

---

## 👥 현재 테스트 계정 목록

### **현재 구현 상태: ⚠️ SUPER_ADMIN 미구현**

현재 시스템에는 **SUPER_ADMIN 계정이 없으며**, 각 회사별 ADMIN/MANAGER/USER 계정만 존재합니다.

### ABC기업 계정 (3개)

#### 1. **ABC 관리자**
```yaml
사용자명: admin
비밀번호: admin123
권한: ADMIN
소속 회사: ABC기업
소속 부서: 인사팀
직급: 대표이사
```
**접근 가능**:
- ✅ ABC기업 전체 데이터
- ❌ XYZ그룹, DEF코퍼레이션 데이터

#### 2. **ABC 매니저**
```yaml
사용자명: manager
비밀번호: manager123
권한: MANAGER
소속 회사: ABC기업
소속 부서: 개발팀
직급: 부장
```
**접근 가능**:
- ✅ ABC기업 데이터 (조회 위주)
- ❌ XYZ그룹, DEF코퍼레이션 데이터
- ❌ **사용자 계정 관리 불가** (개발팀이므로)

**참고**: 만약 이 매니저가 **인사팀 매니저**였다면 사용자 계정 관리 권한도 가질 수 있습니다.

#### 3. **ABC 일반 사용자**
```yaml
사용자명: user
비밀번호: user123
권한: USER
소속 회사: ABC기업
소속 부서: 개발팀
직급: 대리
```
**접근 가능**:
- ✅ ABC기업 기본 데이터 (조회)
- ✅ 본인 정보 (조회/수정)
- ❌ XYZ그룹, DEF코퍼레이션 데이터

---

### XYZ그룹 계정 (2개)

#### 4. **XYZ 관리자**
```yaml
사용자명: xyz_admin
비밀번호: xyz123
권한: ADMIN
소속 회사: XYZ그룹
소속 부서: 인사팀
직급: 대표이사
```
**접근 가능**:
- ✅ XYZ그룹 전체 데이터
- ❌ ABC기업, DEF코퍼레이션 데이터

#### 5. **XYZ 매니저**
```yaml
사용자명: xyz_manager
비밀번호: xyz123
권한: MANAGER
소속 회사: XYZ그룹
소속 부서: 인사팀
직급: 부장
```
**접근 가능**:
- ✅ XYZ그룹 데이터 (조회 위주)
- ✅ **사용자 계정 관리 가능** (인사팀 매니저이므로)
- ✅ 직원 채용/퇴사 처리 가능
- ❌ ABC기업, DEF코퍼레이션 데이터

**참고**: 이 매니저는 **인사팀 소속**이므로 사용자 계정 관리 등 인사 관련 추가 권한을 가집니다.

---

### DEF코퍼레이션 계정 (2개)

#### 6. **DEF 관리자**
```yaml
사용자명: def_admin
비밀번호: def123
권한: ADMIN
소속 회사: DEF코퍼레이션
소속 부서: 인사팀
직급: 대표이사
```
**접근 가능**:
- ✅ DEF코퍼레이션 전체 데이터
- ❌ ABC기업, XYZ그룹 데이터

#### 7. **DEF 일반 사용자**
```yaml
사용자명: def_user
비밀번호: def123
권한: USER
소속 회사: DEF코퍼레이션
소속 부서: 인사팀
직급: 사원
```
**접근 가능**:
- ✅ DEF코퍼레이션 기본 데이터 (조회)
- ✅ 본인 정보 (조회/수정)
- ❌ ABC기업, XYZ그룹 데이터

---

## 🎯 권한별 접근 가능 기능

### 🏢 **부서별 MANAGER 권한 차이**

| 기능 | ADMIN | HR팀 MANAGER | 영업팀 MANAGER | 재고팀 MANAGER | 개발팀 MANAGER |
|------|-------|-------------|---------------|---------------|---------------|
| **인사 관리** | | | | | |
| 사용자 계정 생성 | ✅ | ✅ | ❌ | ❌ | ❌ |
| 사용자 계정 수정 | ✅ | ✅ | 본인만 ✅ | 본인만 ✅ | 본인만 ✅ |
| 직원 채용 처리 | ✅ | ✅ | ❌ | ❌ | ❌ |
| 직원 퇴사 처리 | ✅ | ✅ | ❌ | ❌ | ❌ |
| 인사 평가 | ✅ | ✅ | ❌ | ❌ | ❌ |
| **영업 관리** | | | | | |
| 견적 승인 | ✅ | 조회만 ✅ | ✅ | 조회만 ✅ | 조회만 ✅ |
| 주문 승인 | ✅ | 조회만 ✅ | ✅ | 조회만 ✅ | 조회만 ✅ |
| 고객 정보 수정 | ✅ | 조회만 ✅ | ✅ | 조회만 ✅ | 조회만 ✅ |
| **재고 관리** | | | | | |
| 입출고 승인 | ✅ | 조회만 ✅ | 조회만 ✅ | ✅ | 조회만 ✅ |
| 재고 조정 | ✅ | 조회만 ✅ | 조회만 ✅ | ✅ | 조회만 ✅ |
| 창고 관리 | ✅ | 조회만 ✅ | 조회만 ✅ | ✅ | 조회만 ✅ |
| **회계 관리** | | | | | |
| 재무 데이터 수정 | ✅ | ❌ | ❌ | ❌ | ❌ |
| 재무 데이터 조회 | ✅ | ✅ | ✅ | ✅ | ✅ |

### 📊 기능별 권한 매트릭스

| 기능 | SUPER_ADMIN | ADMIN | MANAGER | USER | READONLY |
|------|-------------|-------|---------|------|----------|
| **인사관리** | | | | | |
| 직원 목록 조회 | 전체 ✅ | 자사 ✅ | 자사 ✅ | 자사 ✅ | 자사 ✅ |
| 직원 상세 조회 | 전체 ✅ | 자사 ✅ | 자사 ✅ | 제한적 ✅ | 자사 ✅ |
| 직원 등록 | 전체 ✅ | 자사 ✅ | 자사 ✅ | ❌ | ❌ |
| 직원 수정 | 전체 ✅ | 자사 ✅ | 자사 ✅ | 본인만 ✅ | ❌ |
| 직원 삭제 | 전체 ✅ | 자사 ✅ | ❌ | ❌ | ❌ |
| 직원 통계 | 전체 ✅ | 자사 ✅ | 자사 ✅ | ❌ | 자사 ✅ |
| **공통 관리** | | | | | |
| 회사 목록 조회 | 전체 ✅ | 자사만 ✅ | 자사만 ✅ | 자사만 ✅ | 자사만 ✅ |
| 회사 등록 | ✅ | ❌ | ❌ | ❌ | ❌ |
| 회사 수정 | 전체 ✅ | 자사 ✅ | ❌ | ❌ | ❌ |
| 회사 삭제 | ✅ | ❌ | ❌ | ❌ | ❌ |
| 부서 관리 | 전체 ✅ | 자사 ✅ | 조회만 ✅ | 조회만 ✅ | 조회만 ✅ |
| 직급 관리 | 전체 ✅ | 자사 ✅ | 조회만 ✅ | 조회만 ✅ | 조회만 ✅ |
| **사용자 계정** | | | | | |
| 사용자 목록 | 전체 ✅ | 자사 ✅ | HR팀만 ✅ | ❌ | ❌ |
| 사용자 생성 | 전체 ✅ | 자사 ✅ | HR팀만 ✅ | ❌ | ❌ |
| 사용자 수정 | 전체 ✅ | 자사 ✅ | HR팀 ✅ / 본인 ✅ | 본인만 ✅ | ❌ |
| 사용자 삭제 | 전체 ✅ | 자사 ✅ | ❌ | ❌ | ❌ |
| **영업관리** | | | | | |
| 고객 관리 | 전체 ✅ | 자사 ✅ | 자사 ✅ | 제한적 ✅ | 조회만 ✅ |
| 주문 관리 | 전체 ✅ | 자사 ✅ | 자사 ✅ | 제한적 ✅ | 조회만 ✅ |
| 견적 관리 | 전체 ✅ | 자사 ✅ | 자사 ✅ | 제한적 ✅ | 조회만 ✅ |
| **재고관리** | | | | | |
| 상품 관리 | 전체 ✅ | 자사 ✅ | 자사 ✅ | 조회만 ✅ | 조회만 ✅ |
| 재고 관리 | 전체 ✅ | 자사 ✅ | 자사 ✅ | 제한적 ✅ | 조회만 ✅ |
| 입출고 관리 | 전체 ✅ | 자사 ✅ | 자사 ✅ | 제한적 ✅ | 조회만 ✅ |
| **회계관리** | | | | | |
| 계정과목 관리 | 전체 ✅ | 자사 ✅ | 조회만 ✅ | ❌ | 조회만 ✅ |
| 전표 처리 | 전체 ✅ | 자사 ✅ | 자사 ✅ | ❌ | 조회만 ✅ |
| 재무제표 | 전체 ✅ | 자사 ✅ | 자사 ✅ | ❌ | 자사 ✅ |
| **대시보드** | | | | | |
| 전체 통계 | ✅ | 자사 ✅ | 자사 ✅ | 제한적 ✅ | 자사 ✅ |
| 성과 지표 | ✅ | 자사 ✅ | 자사 ✅ | ❌ | 자사 ✅ |

---

## 🔍 데이터 접근 범위

### 시나리오별 접근 권한

#### **시나리오 1: 직원 목록 조회**

**ABC기업 admin 로그인 시**:
- ✅ ABC기업 직원 20명 전체 조회
- ❌ XYZ그룹 직원 조회 불가
- ❌ DEF코퍼레이션 직원 조회 불가

**XYZ그룹 xyz_admin 로그인 시**:
- ❌ ABC기업 직원 조회 불가
- ✅ XYZ그룹 직원 전체 조회
- ❌ DEF코퍼레이션 직원 조회 불가

**SUPER_ADMIN 로그인 시 (미구현)**:
- ✅ ABC기업 직원 20명 조회
- ✅ XYZ그룹 직원 조회
- ✅ DEF코퍼레이션 직원 조회
- ✅ **모든 회사** 직원 조회

---

#### **시나리오 2: 직원 정보 수정**

**ABC기업 admin (ADMIN):**
- ✅ ABC기업 모든 직원 정보 수정 가능
- ❌ XYZ그룹, DEF코퍼레이션 직원 수정 불가

**ABC기업 manager (MANAGER):**
- ✅ ABC기업 직원 정보 수정 가능 (일부 제한)
- ❌ 직원 삭제 불가
- ❌ 타사 직원 수정 불가

**ABC기업 user (USER):**
- ✅ 본인 정보만 수정 가능
- ❌ 다른 직원 정보 수정 불가

---

#### **시나리오 3: 부서 관리**

**ABC기업 admin (ADMIN):**
- ✅ ABC기업 부서 생성/수정/삭제
- ❌ XYZ그룹, DEF코퍼레이션 부서 관리 불가

**ABC기업 manager (MANAGER):**
- ✅ ABC기업 부서 목록 조회
- ❌ 부서 생성/수정/삭제 불가

**SUPER_ADMIN (미구현):**
- ✅ 모든 회사의 부서 관리 가능

---

## ⚠️ 권한 체계 구현 상태

### ✅ **구현 완료**

1. **권한 정의**: 5단계 권한 (SUPER_ADMIN, ADMIN, MANAGER, USER, READONLY)
2. **회사별 데이터 격리**: `User.company` 필드로 소속 회사 구분
3. **권한 체크 메서드**:
   - `isAdmin()`: 관리자 권한 확인
   - `isManager()`: 매니저 권한 확인
   - `belongsToCompany()`: 같은 회사 소속 확인
   - `canAccessUser()`: 사용자 접근 권한 확인
   - `canAccessCompany()`: 회사 데이터 접근 권한 확인
   - `canAccessDepartment()`: 부서 데이터 접근 권한 확인

4. **JWT 토큰**: 사용자 ID, 역할, 회사 ID 포함

### ⚠️ **부분 구현**

1. **백엔드 권한 체크**: 
   - UserPrincipal에 메서드는 구현됨
   - 각 Controller에서 권한 체크 **일부만 적용**
   - 모든 API 엔드포인트에 일관된 권한 체크 **미완료**

2. **프론트엔드 권한 체크**:
   - 권한별 메뉴 표시/숨김 **부분 구현**
   - 권한별 버튼 활성화/비활성화 **부분 구현**

### ❌ **미구현**

1. **SUPER_ADMIN 계정**: 
   - 권한은 정의되어 있으나 **실제 계정 없음**
   - DataInitializer에 SUPER_ADMIN 계정 생성 로직 없음

2. **READONLY 계정**:
   - 권한은 정의되어 있으나 **실제 계정 없음**

3. **Controller 권한 체크**:
   - 모든 API에서 일관된 권한 체크 **미구현**
   - 회사별 데이터 필터링 **일부만 구현**

4. **프론트엔드 권한 기반 UI**:
   - 권한별 메뉴 완전한 제어 **미구현**
   - 권한별 기능 버튼 제어 **미구현**

---

## 🔧 권한 체계 보완 필요 사항

### 1. **SUPER_ADMIN 계정 생성**
```java
// DataInitializer.java에 추가 필요
users.add(createUser("superadmin", "super123", "super@erp.com", "시스템관리자", "02-0000-0000", 
    User.UserRole.SUPER_ADMIN, null, null, "시스템관리자"));
```

### 2. **부서별 추가 권한 구현**

#### **방안 A: User 엔티티에 추가 권한 필드 추가 (권장)**
```java
// User.java
@ElementCollection(fetch = FetchType.EAGER)
@CollectionTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"))
@Column(name = "permission")
@Enumerated(EnumType.STRING)
private Set<Permission> additionalPermissions = new HashSet<>();

public enum Permission {
    USER_MANAGEMENT("사용자 관리"),
    EMPLOYEE_MANAGEMENT("직원 관리"),
    SALES_APPROVAL("영업 승인"),
    INVENTORY_APPROVAL("재고 승인"),
    FINANCE_VIEW("재무 조회"),
    FINANCE_EDIT("재무 수정");
    
    private final String description;
    
    Permission(String description) {
        this.description = description;
    }
}
```

#### **방안 B: 부서 타입에 따라 자동 권한 부여**
```java
// UserPrincipal.java에 추가
public boolean hasUserManagementPermission() {
    // HR팀 매니저는 사용자 관리 권한 보유
    if (isManager() && getDepartment() != null) {
        String deptName = getDepartment().getName();
        return deptName.contains("인사") || deptName.contains("HR");
    }
    return isAdmin();
}

public boolean hasSalesApprovalPermission() {
    // 영업팀 매니저는 영업 승인 권한 보유
    if (isManager() && getDepartment() != null) {
        String deptName = getDepartment().getName();
        return deptName.contains("영업") || deptName.contains("Sales");
    }
    return isAdmin();
}
```

#### **방안 C: 역할 세분화 (복잡하지만 명확)**
```java
public enum UserRole {
    SUPER_ADMIN("시스템 관리자"),
    ADMIN("관리자"),
    HR_MANAGER("인사 매니저"),      // 추가
    SALES_MANAGER("영업 매니저"),   // 추가
    INVENTORY_MANAGER("재고 매니저"), // 추가
    MANAGER("일반 매니저"),
    USER("일반 사용자"),
    READONLY("읽기 전용");
}
```

### 3. **권장 구현 방안: 방안 B (부서 기반 자동 권한)**

**선택 이유**:
- ✅ 기존 구조 변경 최소화 (DB 스키마 변경 불필요)
- ✅ 구현 간단함 (UserPrincipal에만 메서드 추가)
- ✅ 유연성: 부서명으로 권한 자동 부여
- ✅ 유지보수 용이

**구현 예시**:
```java
// UserPrincipal.java
public boolean hasUserManagementPermission() {
    // ADMIN은 항상 가능
    if (isAdmin()) return true;
    
    // MANAGER + HR팀인 경우 가능
    if (isManager() && user.getDepartment() != null) {
        String deptName = user.getDepartment().getName();
        return deptName.contains("인사") || deptName.contains("HR") || deptName.contains("인력");
    }
    
    return false;
}
```

**Controller 적용 예시**:
```java
@PostMapping("/users")
public ResponseEntity<?> createUser(
    @RequestBody UserCreateDto dto,
    @AuthenticationPrincipal UserPrincipal userPrincipal) {
    
    // 권한 체크: ADMIN 또는 HR팀 MANAGER만 가능
    if (!userPrincipal.hasUserManagementPermission()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("사용자 계정을 생성할 권한이 없습니다"));
    }
    
    // 사용자 생성 로직
    // ...
}
```

### 4. **Controller 권한 체크 강화**
```java
// 모든 API에 권한 체크 추가 필요 예시
@GetMapping("/employees")
public ResponseEntity<?> getEmployees(@AuthenticationPrincipal UserPrincipal userPrincipal) {
    // SUPER_ADMIN이 아니면 자사 데이터만 조회
    if (!userPrincipal.getUser().getRole().equals(User.UserRole.SUPER_ADMIN)) {
        // 자사 데이터만 필터링
        Long companyId = userPrincipal.getCompanyId();
        // ...
    }
    // ...
}
```

### 5. **부서별 추가 권한 전체 구현**

#### **UserPrincipal.java에 추가할 메서드들**
```java
/**
 * 사용자 계정 관리 권한 확인
 * ADMIN 또는 HR팀 MANAGER만 가능
 */
public boolean hasUserManagementPermission() {
    if (isAdmin()) return true;
    if (isManager() && user.getDepartment() != null) {
        String deptName = user.getDepartment().getName();
        return deptName.contains("인사") || deptName.contains("HR") || deptName.contains("인력");
    }
    return false;
}

/**
 * 직원 관리 권한 확인
 * ADMIN 또는 HR팀 MANAGER만 가능
 */
public boolean hasEmployeeManagementPermission() {
    return hasUserManagementPermission(); // 사용자 관리 권한과 동일
}

/**
 * 영업 승인 권한 확인
 * ADMIN 또는 영업팀 MANAGER만 가능
 */
public boolean hasSalesApprovalPermission() {
    if (isAdmin()) return true;
    if (isManager() && user.getDepartment() != null) {
        String deptName = user.getDepartment().getName();
        return deptName.contains("영업") || deptName.contains("Sales");
    }
    return false;
}

/**
 * 재고 승인 권한 확인
 * ADMIN 또는 재고팀/창고팀 MANAGER만 가능
 */
public boolean hasInventoryApprovalPermission() {
    if (isAdmin()) return true;
    if (isManager() && user.getDepartment() != null) {
        String deptName = user.getDepartment().getName();
        return deptName.contains("재고") || deptName.contains("창고") || deptName.contains("Inventory");
    }
    return false;
}

/**
 * 재무 수정 권한 확인
 * ADMIN 또는 회계팀 MANAGER만 가능
 */
public boolean hasFinanceEditPermission() {
    if (isAdmin()) return true;
    if (isManager() && user.getDepartment() != null) {
        String deptName = user.getDepartment().getName();
        return deptName.contains("회계") || deptName.contains("재무") || deptName.contains("Finance");
    }
    return false;
}
```

### 6. **프론트엔드 권한 기반 라우팅**
```typescript
// 권한별 접근 가능 라우트 정의 필요
const roleBasedRoutes = {
  SUPER_ADMIN: ['/admin', '/companies', '/system'],
  ADMIN: ['/employees', '/departments', '/dashboard'],
  MANAGER: ['/employees', '/dashboard'],
  USER: ['/profile', '/dashboard'],
  READONLY: ['/dashboard']
};
```

---

## 📝 권한 테스트 시나리오

### **테스트 1: 회사별 데이터 격리**
```
1. admin (ABC기업) 로그인
2. 직원 목록 조회
3. 확인: ABC기업 직원만 표시되어야 함
4. xyz_admin (XYZ그룹) 로그인
5. 직원 목록 조회
6. 확인: XYZ그룹 직원만 표시되어야 함
```

### **테스트 2: 권한별 수정 권한**
```
1. manager (ABC기업, MANAGER) 로그인
2. 직원 정보 수정 시도
3. 확인: 수정 가능
4. 직원 삭제 시도
5. 확인: 삭제 불가 (권한 오류)
```

### **테스트 3: 일반 사용자 제한**
```
1. user (ABC기업, USER) 로그인
2. 본인 정보 수정 시도
3. 확인: 수정 가능
4. 다른 직원 정보 수정 시도
5. 확인: 수정 불가 (권한 오류)
```

---

## 📌 핵심 요약

### ✅ **현재 권한 체계 상태**

1. **권한 구조**: 5단계 (SUPER_ADMIN, ADMIN, MANAGER, USER, READONLY)
2. **회사별 격리**: 각 사용자는 소속 회사 데이터만 접근
3. **부서별 추가 권한**: 
   - **HR팀 MANAGER**: 사용자 계정 관리 권한
   - **영업팀 MANAGER**: 영업 승인 권한
   - **재고팀 MANAGER**: 재고 승인 권한
   - **회계팀 MANAGER**: 재무 수정 권한

### ⚠️ **미구현 사항**

1. **SUPER_ADMIN 계정**: 정의만 있고 실제 계정 없음
2. **부서별 추가 권한 로직**: 설계는 완료, 실제 코드 적용 필요
3. **Controller 권한 체크**: 일부 API에만 적용됨
4. **프론트엔드 권한 UI**: 부분적으로만 구현됨

### 🎯 **권한 체계 완성을 위한 TODO**

1. **SUPER_ADMIN 계정 생성** (DataInitializer)
2. **부서별 권한 메서드 추가** (UserPrincipal)
3. **모든 Controller에 권한 체크 적용**
4. **프론트엔드 권한 기반 UI 구현**
5. **권한 테스트 시나리오 수행**

---

**문서 관리**: 이 문서는 권한 체계 구현이 완료되면 업데이트됩니다.  
**다음 업데이트 예정**: 권한 체계 완전 구현 후 (2025-02-01 예정)
