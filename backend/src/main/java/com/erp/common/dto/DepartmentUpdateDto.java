package com.erp.common.dto;

import com.erp.common.entity.Department;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 부서 수정 DTO
 * 기존 부서 정보 수정 시 사용됩니다
 */
public record DepartmentUpdateDto(
        @Size(max = 100, message = "부서명은 100자 이하여야 합니다")
        String name,
        
        @Size(max = 100, message = "영문 부서명은 100자 이하여야 합니다")
        String nameEn,
        
        @Size(max = 500, message = "부서 설명은 500자 이하여야 합니다")
        String description,
        
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
    public DepartmentUpdateDto {
        // 수정 DTO는 모든 필드가 선택사항이므로 별도 검증 없음
        // null 값은 해당 필드를 수정하지 않음을 의미
    }
}




