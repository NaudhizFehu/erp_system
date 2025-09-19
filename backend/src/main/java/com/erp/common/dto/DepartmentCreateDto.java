package com.erp.common.dto;

import com.erp.hr.entity.Department;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 부서 생성 DTO
 * 새로운 부서 등록 시 사용됩니다
 */
public record DepartmentCreateDto(
        @NotBlank(message = "부서 코드는 필수입니다")
        @Size(max = 20, message = "부서 코드는 20자 이하여야 합니다")
        @Pattern(regexp = "^[A-Z0-9_]+$", message = "부서 코드는 대문자, 숫자, 언더스코어만 사용 가능합니다")
        String departmentCode,
        
        @NotBlank(message = "부서명은 필수입니다")
        @Size(max = 100, message = "부서명은 100자 이하여야 합니다")
        String name,
        
        @Size(max = 100, message = "영문 부서명은 100자 이하여야 합니다")
        String nameEn,
        
        @Size(max = 500, message = "부서 설명은 500자 이하여야 합니다")
        String description,
        
        @NotNull(message = "소속 회사는 필수입니다")
        Long companyId,
        
        Long parentDepartmentId,
        Long managerId,
        
        @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
        Integer sortOrder,
        
        Department.DepartmentType departmentType,
        Department.DepartmentStatus status,
        
        @Size(max = 20, message = "비용센터 코드는 20자 이하여야 합니다")
        String costCenterCode,
        
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다")
        String phone,
        
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 팩스번호 형식이어야 합니다")
        String fax,
        
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
        String email,
        
        @Size(max = 200, message = "부서 위치는 200자 이하여야 합니다")
        String location,
        
        @Min(value = 0, message = "예산 금액은 0 이상이어야 합니다")
        BigDecimal budgetAmount
) {
    public DepartmentCreateDto {
        if (departmentCode == null || departmentCode.trim().isEmpty()) {
            throw new IllegalArgumentException("부서 코드는 필수입니다");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("부서명은 필수입니다");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("소속 회사는 필수입니다");
        }
        
        // 기본값 설정
        if (sortOrder == null) {
            sortOrder = 0;
        }
        if (status == null) {
            status = Department.DepartmentStatus.ACTIVE;
        }
    }
}




