package com.erp.common.security;

import com.erp.hr.entity.Employee;
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
 * 직원 인증 정보와 권한 정보를 제공합니다
 */
@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    /**
     * 직원 엔티티
     */
    private final Employee employee;

    /**
     * 직원 엔티티로부터 UserPrincipal 생성
     *
     * @param employee 직원 엔티티
     * @return UserPrincipal 객체
     */
    public static UserPrincipal create(Employee employee) {
        return new UserPrincipal(employee);
    }

    /**
     * 사용자 권한 목록 반환
     * 직원의 역할을 GrantedAuthority로 변환하여 반환합니다
     *
     * @return 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (employee.getRole() == null) {
            return Collections.emptyList();
        }
        String roleName = "ROLE_" + employee.getRole().name();
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    /**
     * 사용자 비밀번호 반환
     *
     * @return 암호화된 비밀번호
     */
    @Override
    public String getPassword() {
        return employee.getPassword();
    }

    /**
     * 사용자명 반환
     *
     * @return 사용자명
     */
    @Override
    public String getUsername() {
        return employee.getUsername();
    }

    /**
     * 계정 만료 여부 확인
     *
     * @return 계정이 만료되지 않았으면 true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금 여부 확인
     *
     * @return 계정이 잠겨있지 않으면 true
     */
    @Override
    public boolean isAccountNonLocked() {
        return employee.getIsLocked() == null || !employee.getIsLocked();
    }

    /**
     * 비밀번호 만료 여부 확인
     *
     * @return 비밀번호가 만료되지 않았으면 true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return employee.getIsPasswordExpired() == null || !employee.getIsPasswordExpired();
    }

    /**
     * 계정 활성화 여부 확인
     *
     * @return 계정이 활성화되어 있으면 true
     */
    @Override
    public boolean isEnabled() {
        return employee.getIsActive() != null && employee.getIsActive()
                && employee.getEmploymentStatus() != Employee.EmploymentStatus.TERMINATED;
    }

    /**
     * 직원 ID 반환
     *
     * @return 직원 ID
     */
    public Long getId() {
        return employee.getId();
    }

    /**
     * 직원 이름 반환
     *
     * @return 직원 이름
     */
    public String getName() {
        return employee.getName();
    }

    /**
     * 직원 이메일 반환
     *
     * @return 직원 이메일
     */
    public String getEmail() {
        return employee.getEmail();
    }

    /**
     * 직원 역할 반환
     *
     * @return 직원 역할
     */
    public Employee.UserRole getRole() {
        return employee.getRole();
    }

    /**
     * 회사 ID 반환
     *
     * @return 회사 ID
     */
    public Long getCompanyId() {
        return employee.getCompany() != null ? employee.getCompany().getId() : null;
    }

    /**
     * 부서 ID 반환
     *
     * @return 부서 ID
     */
    public Long getDepartmentId() {
        return employee.getDepartment() != null ? employee.getDepartment().getId() : null;
    }

    /**
     * 직급 ID 반환
     *
     * @return 직급 ID
     */
    public Long getPositionId() {
        return employee.getPosition() != null ? employee.getPosition().getId() : null;
    }

    /**
     * SUPER_ADMIN 권한 여부 확인
     *
     * @return SUPER_ADMIN이면 true
     */
    public boolean isSuperAdmin() {
        return employee.getRole() == Employee.UserRole.SUPER_ADMIN;
    }

    /**
     * Employee 엔티티 반환
     *
     * @return Employee 엔티티
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * User 엔티티 반환 (하위 호환성)
     *
     * @return Employee 엔티티
     */
    @Deprecated
    public Employee getUser() {
        return employee;
    }

    /**
     * 직원 ID 반환 (employeeId 별칭)
     *
     * @return 직원 ID
     */
    public Long getEmployeeId() {
        return employee.getId();
    }

    /**
     * 특정 역할 보유 여부 확인
     *
     * @param role 확인할 역할
     * @return 역할 보유시 true
     */
    public boolean hasRole(String role) {
        if (employee.getRole() == null) {
            return false;
        }
        return employee.getRole().name().equals(role) ||
               ("ROLE_" + employee.getRole().name()).equals(role);
    }

    /**
     * 특정 회사 소속 여부 확인
     *
     * @param companyId 확인할 회사 ID
     * @return 해당 회사 소속이면 true
     */
    public boolean belongsToCompany(Long companyId) {
        return employee.getCompany() != null &&
               employee.getCompany().getId().equals(companyId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(employee.getId(), that.employee.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee.getId());
    }
}
