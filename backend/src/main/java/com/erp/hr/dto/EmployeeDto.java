package com.erp.hr.dto;

import com.erp.common.dto.CompanyDto;
import com.erp.common.dto.DepartmentDto;
import com.erp.hr.entity.Employee;
import java.time.LocalDate;
import java.time.LocalDateTime;
// import java.math.BigDecimal; // 사용하지 않음

/**
 * 직원 정보 응답 DTO
 * 직원 정보 조회 시 사용됩니다
 */
public record EmployeeDto(
        Long id,
        String employeeNumber,
        String name,
        String nameEn,
        String email,
        String phone,
        String mobile,
        LocalDate birthDate,
        Employee.Gender gender,
        String address,
        String postalCode,
        CompanyDto company,
        DepartmentDto department,
        PositionDto position,
        LocalDate hireDate,
        LocalDate terminationDate,
        Employee.EmploymentStatus employmentStatus,
        Employee.EmploymentType employmentType,
        Long baseSalary,
        String bankName,
        String accountNumber,
        String accountHolder,
        String emergencyContact,
        String emergencyRelation,
        String education,
        String major,
        String career,
        String skills,
        String certifications,
        String memo,
        String profileImageUrl,
        Integer yearsOfService,
        Integer age,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public EmployeeDto {
        if (employeeNumber == null || employeeNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("사번은 필수입니다");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("성명은 필수입니다");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수입니다");
        }
        // company, department, position은 SUPER_ADMIN의 경우 null 가능
        // 검증은 from() 메서드에서 처리
        if (hireDate == null) {
            throw new IllegalArgumentException("입사일은 필수입니다");
        }
        if (employmentStatus == null) {
            throw new IllegalArgumentException("근무 상태는 필수입니다");
        }
    }
    
    /**
     * Employee 엔티티로부터 EmployeeDto 생성
     */
    public static EmployeeDto from(Employee employee) {
        // SUPER_ADMIN은 department/position이 null일 수 있음
        boolean isSuperAdmin = employee.getRole() == Employee.UserRole.SUPER_ADMIN;

        return new EmployeeDto(
            employee.getId(),
            employee.getEmployeeNumber(),
            employee.getName(),
            employee.getNameEn(),
            employee.getEmail(),
            employee.getPhone(),
            employee.getMobile(),
            employee.getBirthDate(),
            employee.getGender(),
            employee.getAddress(),
            employee.getPostalCode(),
            employee.getCompany() != null ? CompanyDto.from(employee.getCompany()) : null,
            // SUPER_ADMIN이 아니면 department는 필수, SUPER_ADMIN이면 null 허용
            employee.getDepartment() != null ? DepartmentDto.from(employee.getDepartment()) : (isSuperAdmin ? null : null),
            // SUPER_ADMIN이 아니면 position은 필수, SUPER_ADMIN이면 null 허용
            employee.getPosition() != null ? com.erp.hr.dto.PositionDto.from(employee.getPosition()) : (isSuperAdmin ? null : null),
            employee.getHireDate(),
            employee.getTerminationDate(),
            employee.getEmploymentStatus(),
            employee.getEmploymentType(),
            employee.getBaseSalary(),
            employee.getBankName(),
            employee.getAccountNumber(),
            employee.getAccountHolder(),
            employee.getEmergencyContact(),
            employee.getEmergencyRelation(),
            employee.getEducation(),
            employee.getMajor(),
            employee.getCareer(),
            employee.getSkills(),
            employee.getCertifications(),
            employee.getMemo(),
            employee.getProfileImageUrl(),
            employee.getYearsOfService(),
            employee.getAge(),
            employee.getCreatedAt(),
            employee.getUpdatedAt()
        );
    }
}