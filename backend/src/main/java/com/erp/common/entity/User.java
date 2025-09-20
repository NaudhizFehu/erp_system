package com.erp.common.entity;

import com.erp.hr.entity.Department;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 사용자 엔티티
 * 시스템 사용자 정보를 관리합니다
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_username", columnList = "username"),
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_company", columnList = "company_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    /**
     * 사용자명 (로그인 ID)
     */
    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 3, max = 50, message = "사용자명은 3자 이상 50자 이하여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "사용자명은 영문, 숫자, ., _, -만 사용 가능합니다")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    /**
     * 비밀번호 (암호화 저장)
     */
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 60, max = 100, message = "암호화된 비밀번호 길이가 올바르지 않습니다")
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    /**
     * 이메일 주소
     */
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    /**
     * 실명
     */
    @NotBlank(message = "실명은 필수입니다")
    @Size(max = 50, message = "실명은 50자 이하여야 합니다")
    @Column(name = "full_name", nullable = false, length = 50)
    private String fullName;

    /**
     * 전화번호
     */
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다")
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 전화번호 (추가 필드)
     */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * 직급/직책
     */
    @Size(max = 100, message = "직급은 100자 이하여야 합니다")
    @Column(name = "position", length = 100)
    private String position;

    /**
     * 사용자 역할
     */
    @NotNull(message = "사용자 역할은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    /**
     * 계정 활성화 여부
     */
    @NotNull(message = "계정 활성화 여부는 필수입니다")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 계정 잠금 여부
     */
    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked = false;

    /**
     * 비밀번호 만료 여부
     */
    @Column(name = "is_password_expired", nullable = false)
    private Boolean isPasswordExpired = false;

    /**
     * 소속 회사
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    /**
     * 소속 부서
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * 마지막 로그인 일시
     */
    @Column(name = "last_login_at")
    private java.time.LocalDateTime lastLoginAt;

    /**
     * 비밀번호 변경 일시
     */
    @Column(name = "password_changed_at")
    private java.time.LocalDateTime passwordChangedAt;

    /**
     * 사용자 역할 열거형
     */
    public enum UserRole {
        SUPER_ADMIN("시스템 관리자"),
        ADMIN("관리자"),
        MANAGER("매니저"),
        USER("일반 사용자"),
        READONLY("읽기 전용");

        private final String description;

        UserRole(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 계정이 잠겨있지 않은지 확인
     */
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    /**
     * 계정이 만료되지 않았는지 확인
     */
    public boolean isAccountNonExpired() {
        return isActive;
    }

    /**
     * 비밀번호가 만료되지 않았는지 확인
     */
    public boolean isCredentialsNonExpired() {
        return !isPasswordExpired;
    }

    /**
     * 계정이 활성화되어 있는지 확인
     */
    public boolean isEnabled() {
        return isActive && !getIsDeleted();
    }
}
