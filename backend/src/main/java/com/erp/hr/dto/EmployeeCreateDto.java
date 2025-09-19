package com.erp.hr.dto;

import com.erp.hr.entity.Employee;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
// import java.math.BigDecimal; // 사용하지 않음

/**
 * 직원 생성 DTO
 * 새로운 직원 등록 시 사용됩니다
 */
public record EmployeeCreateDto(
        @NotBlank(message = "사번은 필수입니다")
        @Size(max = 20, message = "사번은 20자 이하여야 합니다")
        @Pattern(regexp = "^[A-Z0-9]+$", message = "사번은 대문자와 숫자만 사용 가능합니다")
        String employeeNumber,
        
        @NotBlank(message = "성명은 필수입니다")
        @Size(max = 50, message = "성명은 50자 이하여야 합니다")
        String name,
        
        @Size(max = 100, message = "영문 성명은 100자 이하여야 합니다")
        String nameEn,
        
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
        String email,
        
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다")
        String phone,
        
        @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "올바른 휴대폰번호 형식이어야 합니다")
        String mobile,
        
        @Size(max = 200, message = "주민등록번호는 200자 이하여야 합니다")
        String residentNumber,
        
        LocalDate birthDate,
        Employee.Gender gender,
        
        @Size(max = 500, message = "주소는 500자 이하여야 합니다")
        String address,
        
        @Size(max = 200, message = "상세 주소는 200자 이하여야 합니다")
        String addressDetail,
        
        @Pattern(regexp = "^\\d{5}$", message = "올바른 우편번호 형식이어야 합니다")
        String postalCode,
        
        @NotNull(message = "소속 회사는 필수입니다")
        Long companyId,
        
        @NotNull(message = "소속 부서는 필수입니다")
        Long departmentId,
        
        @NotNull(message = "직급은 필수입니다")
        Long positionId,
        
        @NotNull(message = "입사일은 필수입니다")
        LocalDate hireDate,
        
        Employee.EmploymentStatus employmentStatus,
        Employee.EmploymentType employmentType,
        
        @Min(value = 0, message = "기본급은 0 이상이어야 합니다")
        // baseSalary 필드 제거됨
        
        @Size(max = 50, message = "은행명은 50자 이하여야 합니다")
        String bankName,
        
        @Size(max = 50, message = "계좌번호는 50자 이하여야 합니다")
        String accountNumber,
        
        @Size(max = 50, message = "예금주명은 50자 이하여야 합니다")
        String accountHolder,
        
        @Size(max = 20, message = "비상연락처는 20자 이하여야 합니다")
        String emergencyContact,
        
        @Size(max = 20, message = "비상연락처 관계는 20자 이하여야 합니다")
        String emergencyRelation,
        
        @Size(max = 100, message = "학력은 100자 이하여야 합니다")
        String education,
        
        @Size(max = 100, message = "전공은 100자 이하여야 합니다")
        String major,
        
        @Size(max = 1000, message = "경력은 1000자 이하여야 합니다")
        String career,
        
        @Size(max = 500, message = "기술 스택은 500자 이하여야 합니다")
        String skills,
        
        @Size(max = 500, message = "자격증은 500자 이하여야 합니다")
        String certifications,
        
        String memo,
        
        @Size(max = 500, message = "프로필 이미지 URL은 500자 이하여야 합니다")
        String profileImageUrl
) {
    public EmployeeCreateDto {
        if (employeeNumber == null || employeeNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("사번은 필수입니다");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("성명은 필수입니다");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수입니다");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("소속 회사는 필수입니다");
        }
        if (departmentId == null) {
            throw new IllegalArgumentException("소속 부서는 필수입니다");
        }
        if (positionId == null) {
            throw new IllegalArgumentException("직급은 필수입니다");
        }
        if (hireDate == null) {
            throw new IllegalArgumentException("입사일은 필수입니다");
        }
        
        // 기본값 설정
        if (employmentStatus == null) {
            employmentStatus = Employee.EmploymentStatus.ACTIVE;
        }
        if (employmentType == null) {
            employmentType = Employee.EmploymentType.FULL_TIME;
        }
        
        // 입사일 검증
        if (hireDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("입사일은 오늘 이전이어야 합니다");
        }
    }
}