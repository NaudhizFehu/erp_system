package com.erp.common.security;

import com.erp.common.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Spring Security UserDetails 구현체
 * 사용자 인증 정보와 권한 정보를 제공합니다
 */
@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    /**
     * 사용자 엔티티
     */
    private final User user;

    /**
     * 사용자 엔티티로부터 UserPrincipal 생성
     * 
     * @param user 사용자 엔티티
     * @return UserPrincipal 객체
     */
    public static UserPrincipal create(User user) {
        return new UserPrincipal(user);
    }

    /**
     * 사용자 권한 목록 반환
     * 사용자의 역할을 GrantedAuthority로 변환하여 반환합니다
     * 
     * @return 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = "ROLE_" + user.getRole().name();
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    /**
     * 사용자 비밀번호 반환
     * 
     * @return 암호화된 비밀번호
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자명 반환
     * 
     * @return 사용자명
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 계정 만료 여부 확인
     * 
     * @return 계정이 만료되지 않았으면 true
     */
    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    /**
     * 계정 잠금 여부 확인
     * 
     * @return 계정이 잠겨있지 않으면 true
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    /**
     * 비밀번호 만료 여부 확인
     * 
     * @return 비밀번호가 만료되지 않았으면 true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    /**
     * 계정 활성화 여부 확인
     * 
     * @return 계정이 활성화되어 있으면 true
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    /**
     * 사용자 ID 반환
     * 
     * @return 사용자 ID
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * 이메일 반환
     * 
     * @return 이메일
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * 실명 반환
     * 
     * @return 실명
     */
    public String getFullName() {
        return user.getFullName();
    }

    /**
     * 사용자 역할 반환
     * 
     * @return 사용자 역할
     */
    public User.UserRole getRole() {
        return user.getRole();
    }

    /**
     * 회사 ID 반환
     * 
     * @return 회사 ID
     */
    public Long getCompanyId() {
        return user.getCompany() != null ? user.getCompany().getId() : null;
    }

    /**
     * 부서 ID 반환
     * 
     * @return 부서 ID
     */
    public Long getDepartmentId() {
        return user.getDepartment() != null ? user.getDepartment().getId() : null;
    }

    /**
     * 직원 ID 반환
     * User 테이블에는 직원 ID가 없지만, 향후 User와 Employee 간 관계 설정 시 사용
     * 현재는 null 반환 (추후 User 엔티티에 employeeId 필드 추가 필요)
     * 
     * @return 직원 ID
     */
    public Long getEmployeeId() {
        // TODO: User 엔티티에 employeeId 필드 추가 후 구현
        return null;
    }

    /**
     * 특정 권한을 가지고 있는지 확인
     * 
     * @param authority 확인할 권한
     * @return 권한 보유 여부
     */
    public boolean hasAuthority(String authority) {
        return getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }

    /**
     * 특정 역할을 가지고 있는지 확인
     * 
     * @param role 확인할 역할
     * @return 역할 보유 여부
     */
    public boolean hasRole(String role) {
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return hasAuthority(roleWithPrefix);
    }

    /**
     * 관리자 권한 보유 여부 확인
     * 
     * @return 관리자 권한 보유 여부
     */
    public boolean isAdmin() {
        return user.getRole() == User.UserRole.SUPER_ADMIN || 
               user.getRole() == User.UserRole.ADMIN;
    }

    /**
     * 매니저 권한 보유 여부 확인 (관리자 포함)
     * 
     * @return 매니저 권한 보유 여부
     */
    public boolean isManager() {
        return isAdmin() || user.getRole() == User.UserRole.MANAGER;
    }

    /**
     * 같은 회사 소속인지 확인
     * 
     * @param companyId 확인할 회사 ID
     * @return 같은 회사 소속 여부
     */
    public boolean belongsToCompany(Long companyId) {
        if (companyId == null || getCompanyId() == null) {
            return false;
        }
        return Objects.equals(getCompanyId(), companyId);
    }

    /**
     * 같은 부서 소속인지 확인
     * 
     * @param departmentId 확인할 부서 ID
     * @return 같은 부서 소속 여부
     */
    public boolean belongsToDepartment(Long departmentId) {
        if (departmentId == null || getDepartmentId() == null) {
            return false;
        }
        return Objects.equals(getDepartmentId(), departmentId);
    }

    /**
     * 특정 사용자에 대한 접근 권한 확인
     * 관리자는 모든 사용자 접근 가능, 매니저는 같은 회사 내 사용자 접근 가능
     * 
     * @param targetUserId 접근하려는 사용자 ID
     * @param targetCompanyId 접근하려는 사용자의 회사 ID
     * @return 접근 권한 여부
     */
    public boolean canAccessUser(Long targetUserId, Long targetCompanyId) {
        // 본인은 항상 접근 가능
        if (Objects.equals(getId(), targetUserId)) {
            return true;
        }
        
        // 관리자는 모든 사용자 접근 가능
        if (isAdmin()) {
            return true;
        }
        
        // 매니저는 같은 회사 내 사용자만 접근 가능
        if (isManager()) {
            return belongsToCompany(targetCompanyId);
        }
        
        // 일반 사용자는 본인만 접근 가능
        return false;
    }

    /**
     * 특정 회사 데이터에 대한 접근 권한 확인
     * 
     * @param targetCompanyId 접근하려는 회사 ID
     * @return 접근 권한 여부
     */
    public boolean canAccessCompany(Long targetCompanyId) {
        // 관리자는 모든 회사 접근 가능
        if (isAdmin()) {
            return true;
        }
        
        // 매니저와 일반 사용자는 자신의 회사만 접근 가능
        return belongsToCompany(targetCompanyId);
    }

    /**
     * 특정 부서 데이터에 대한 접근 권한 확인
     * 
     * @param targetDepartmentId 접근하려는 부서 ID
     * @param targetCompanyId 해당 부서의 회사 ID
     * @return 접근 권한 여부
     */
    public boolean canAccessDepartment(Long targetDepartmentId, Long targetCompanyId) {
        // 관리자는 모든 부서 접근 가능
        if (isAdmin()) {
            return true;
        }
        
        // 매니저는 같은 회사 내 모든 부서 접근 가능
        if (isManager()) {
            return belongsToCompany(targetCompanyId);
        }
        
        // 일반 사용자는 자신의 부서만 접근 가능
        return belongsToDepartment(targetDepartmentId);
    }

    /**
     * 사용자 계정 관리 권한 확인
     * SUPER_ADMIN, ADMIN, 또는 HR팀 MANAGER만 가능
     * 
     * @return 사용자 계정 관리 권한 보유 여부
     */
    public boolean hasUserManagementPermission() {
        // SUPER_ADMIN 또는 ADMIN은 항상 가능
        if (user.getRole() == User.UserRole.SUPER_ADMIN || user.getRole() == User.UserRole.ADMIN) {
            return true;
        }
        
        // MANAGER + HR팀인 경우 가능
        if (user.getRole() == User.UserRole.MANAGER && user.getDepartment() != null) {
            String deptName = user.getDepartment().getName();
            return deptName.contains("인사") || deptName.contains("HR") || deptName.contains("인력");
        }
        
        return false;
    }

    /**
     * 직원 관리 권한 확인
     * SUPER_ADMIN, ADMIN, 또는 HR팀 MANAGER만 가능
     * 
     * @return 직원 관리 권한 보유 여부
     */
    public boolean hasEmployeeManagementPermission() {
        return hasUserManagementPermission(); // 사용자 관리 권한과 동일
    }

    /**
     * 영업 승인 권한 확인
     * SUPER_ADMIN, ADMIN, 또는 영업팀 MANAGER만 가능
     * 
     * @return 영업 승인 권한 보유 여부
     */
    public boolean hasSalesApprovalPermission() {
        // SUPER_ADMIN 또는 ADMIN은 항상 가능
        if (user.getRole() == User.UserRole.SUPER_ADMIN || user.getRole() == User.UserRole.ADMIN) {
            return true;
        }
        
        // MANAGER + 영업팀인 경우 가능
        if (user.getRole() == User.UserRole.MANAGER && user.getDepartment() != null) {
            String deptName = user.getDepartment().getName();
            return deptName.contains("영업") || deptName.contains("Sales") || deptName.contains("판매");
        }
        
        return false;
    }

    /**
     * 재고 승인 권한 확인
     * SUPER_ADMIN, ADMIN, 또는 재고팀/창고팀 MANAGER만 가능
     * 
     * @return 재고 승인 권한 보유 여부
     */
    public boolean hasInventoryApprovalPermission() {
        // SUPER_ADMIN 또는 ADMIN은 항상 가능
        if (user.getRole() == User.UserRole.SUPER_ADMIN || user.getRole() == User.UserRole.ADMIN) {
            return true;
        }
        
        // MANAGER + 재고팀/창고팀인 경우 가능
        if (user.getRole() == User.UserRole.MANAGER && user.getDepartment() != null) {
            String deptName = user.getDepartment().getName();
            return deptName.contains("재고") || deptName.contains("창고") || 
                   deptName.contains("Inventory") || deptName.contains("Warehouse");
        }
        
        return false;
    }

    /**
     * 재무 수정 권한 확인
     * SUPER_ADMIN, ADMIN, 또는 회계팀 MANAGER만 가능
     * 
     * @return 재무 수정 권한 보유 여부
     */
    public boolean hasFinanceEditPermission() {
        // SUPER_ADMIN 또는 ADMIN은 항상 가능
        if (user.getRole() == User.UserRole.SUPER_ADMIN || user.getRole() == User.UserRole.ADMIN) {
            return true;
        }
        
        // MANAGER + 회계팀인 경우 가능
        if (user.getRole() == User.UserRole.MANAGER && user.getDepartment() != null) {
            String deptName = user.getDepartment().getName();
            return deptName.contains("회계") || deptName.contains("재무") || 
                   deptName.contains("Finance") || deptName.contains("Accounting");
        }
        
        return false;
    }

    /**
     * SUPER_ADMIN 권한 확인
     * 
     * @return SUPER_ADMIN 권한 보유 여부
     */
    public boolean isSuperAdmin() {
        return user.getRole() == User.UserRole.SUPER_ADMIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(user.getId(), that.user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId());
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role=" + getRole() +
                ", companyId=" + getCompanyId() +
                ", departmentId=" + getDepartmentId() +
                '}';
    }
}




