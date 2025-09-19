package com.erp.hr.dto;

import com.erp.hr.entity.Employee;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
// import java.math.BigDecimal; // 사용하지 않음

/**
 * 직원 수정 DTO
 * 기존 직원 정보 수정 시 사용됩니다
 */
public record EmployeeUpdateDto(
        @Size(max = 50, message = "성명은 50자 이하여야 합니다")
        String name,
        
        @Size(max = 100, message = "영문 성명은 100자 이하여야 합니다")
        String nameEn,
        
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
        String email,
        
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다")
        String phone,
        
        @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "올바른 휴대폰번호 형식이어야 합니다")
        String mobile,
        
        LocalDate birthDate,
        Employee.Gender gender,
        
        @Size(max = 500, message = "주소는 500자 이하여야 합니다")
        String address,
        
        @Size(max = 200, message = "상세 주소는 200자 이하여야 합니다")
        String addressDetail,
        
        @Pattern(regexp = "^\\d{5}$", message = "올바른 우편번호 형식이어야 합니다")
        String postalCode,
        
        Long departmentId,
        Long positionId,
        
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
    public EmployeeUpdateDto {
        // 수정 DTO는 모든 필드가 선택사항이므로 별도 검증 없음
        // null 값은 해당 필드를 수정하지 않음을 의미
    }
}




