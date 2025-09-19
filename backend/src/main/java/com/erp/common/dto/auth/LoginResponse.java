package com.erp.common.dto.auth;

import com.erp.common.entity.User;
import java.time.LocalDateTime;

/**
 * 로그인 응답 DTO
 * 로그인 성공 시 반환되는 인증 정보입니다
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        UserInfo user
) {
    public LoginResponse {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("액세스 토큰은 필수입니다");
        }
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new IllegalArgumentException("리프레시 토큰은 필수입니다");
        }
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 필수입니다");
        }
        
        // 기본값 설정
        if (tokenType == null) {
            tokenType = "Bearer";
        }
    }
    
    /**
     * 사용자 정보 내부 클래스
     */
    public static record UserInfo(
            Long id,
            String username,
            String email,
            String fullName,
            String role,
            CompanyInfo company,
            DepartmentInfo department,
            LocalDateTime lastLoginAt
    ) {
        public UserInfo {
            if (id == null) {
                throw new IllegalArgumentException("사용자 ID는 필수입니다");
            }
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("사용자명은 필수입니다");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("이메일은 필수입니다");
            }
            if (fullName == null || fullName.trim().isEmpty()) {
                throw new IllegalArgumentException("실명은 필수입니다");
            }
            if (role == null || role.trim().isEmpty()) {
                throw new IllegalArgumentException("역할은 필수입니다");
            }
        }
        
        /**
         * User 엔티티로부터 UserInfo 생성
         */
        public static UserInfo from(User user) {
            return new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getCompany() != null ? CompanyInfo.from(user.getCompany()) : null,
                user.getDepartment() != null ? DepartmentInfo.from(user.getDepartment()) : null,
                user.getLastLoginAt()
            );
        }
    }
    
    /**
     * 회사 정보 내부 클래스
     */
    public static record CompanyInfo(
            Long id,
            String name,
            String companyCode
    ) {
        public CompanyInfo {
            if (id == null) {
                throw new IllegalArgumentException("회사 ID는 필수입니다");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("회사명은 필수입니다");
            }
        }
        
        /**
         * Company 엔티티로부터 CompanyInfo 생성
         */
        public static CompanyInfo from(com.erp.common.entity.Company company) {
            return new CompanyInfo(
                company.getId(),
                company.getName(),
                company.getCompanyCode()
            );
        }
    }
    
    /**
     * 부서 정보 내부 클래스
     */
    public static record DepartmentInfo(
            Long id,
            String name,
            String departmentCode
    ) {
        public DepartmentInfo {
            if (id == null) {
                throw new IllegalArgumentException("부서 ID는 필수입니다");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("부서명은 필수입니다");
            }
        }
        
        /**
         * Department 엔티티로부터 DepartmentInfo 생성
         */
        public static DepartmentInfo from(com.erp.hr.entity.Department department) {
            return new DepartmentInfo(
                department.getId(),
                department.getName(),
                department.getDepartmentCode()
            );
        }
    }
    
    /**
     * 로그인 응답 생성 팩토리 메소드
     */
    public static LoginResponse of(String accessToken, String refreshToken, Long expiresIn, User user) {
        return new LoginResponse(
            accessToken,
            refreshToken,
            "Bearer",
            expiresIn,
            UserInfo.from(user)
        );
    }
}




