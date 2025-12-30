package com.erp.common.dto.auth;

import com.erp.hr.entity.Employee;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
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
        EmployeeInfo employee
) {
    public LoginResponse {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("액세스 토큰은 필수입니다");
        }
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new IllegalArgumentException("리프레시 토큰은 필수입니다");
        }
        if (employee == null) {
            throw new IllegalArgumentException("직원 정보는 필수입니다");
        }

        // 기본값 설정
        if (tokenType == null) {
            tokenType = "Bearer";
        }
    }

    /**
     * 직원 정보 내부 클래스
     */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static record EmployeeInfo(
            Long id,
            String username,
            String email,
            String name,
            String role,
            String employeeNumber,
            String phone,
            String mobile,
            CompanyInfo company,
            DepartmentInfo department,
            PositionInfo position,
            LocalDateTime lastLoginAt,
            LocalDate birthDate,
            String gender,
            LocalDate hireDate,
            String employmentStatus
    ) {
        public EmployeeInfo {
            if (id == null) {
                throw new IllegalArgumentException("직원 ID는 필수입니다");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("이메일은 필수입니다");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("이름은 필수입니다");
            }
            if (role == null || role.trim().isEmpty()) {
                throw new IllegalArgumentException("역할은 필수입니다");
            }
        }

        /**
         * Employee 엔티티로부터 EmployeeInfo 생성
         */
        public static EmployeeInfo from(Employee employee) {
            return new EmployeeInfo(
                employee.getId(),
                employee.getUsername(),
                employee.getEmail(),
                employee.getName(),
                employee.getRole() != null ? employee.getRole().name() : "USER",
                employee.getEmployeeNumber(),
                employee.getPhone(),
                employee.getMobile(),
                employee.getCompany() != null ? CompanyInfo.from(employee.getCompany()) : null,
                employee.getDepartment() != null ? DepartmentInfo.from(employee.getDepartment()) : null,
                employee.getPosition() != null ? PositionInfo.from(employee.getPosition()) : null,
                employee.getLastLoginAt(),
                employee.getBirthDate(),
                employee.getGender() != null ? employee.getGender().name() : null,
                employee.getHireDate(),
                employee.getEmploymentStatus() != null ? employee.getEmploymentStatus().name() : null
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
                department.getDepartmentCode() != null ? department.getDepartmentCode() : ""
            );
        }
    }

    /**
     * 직급 정보 내부 클래스
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static record PositionInfo(
            Long id,
            String name,
            String positionCode
    ) {
        public PositionInfo {
            if (id == null) {
                throw new IllegalArgumentException("직급 ID는 필수입니다");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("직급명은 필수입니다");
            }
        }

        /**
         * Position 엔티티로부터 PositionInfo 생성
         */
        public static PositionInfo from(com.erp.hr.entity.Position position) {
            return new PositionInfo(
                position.getId(),
                position.getName(),
                position.getPositionCode()
            );
        }
    }

    /**
     * 로그인 응답 생성 팩토리 메소드
     */
    public static LoginResponse of(String accessToken, String refreshToken, Long expiresIn, Employee employee) {
        return new LoginResponse(
            accessToken,
            refreshToken,
            "Bearer",
            expiresIn,
            EmployeeInfo.from(employee)
        );
    }
}
