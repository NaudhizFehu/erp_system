package com.erp.common.dto;

import com.erp.common.entity.User;
import jakarta.validation.constraints.*;

/**
 * 사용자 수정 DTO
 * 기존 사용자 정보 수정 시 사용됩니다
 */
public record UserUpdateDto(
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
        String email,
        
        @Size(max = 50, message = "실명은 50자 이하여야 합니다")
        String fullName,
        
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다")
        String phone,
        
        User.UserRole role,
        Boolean isActive,
        Boolean isLocked,
        Long companyId,
        Long departmentId
) {
    public UserUpdateDto {
        // 수정 DTO는 모든 필드가 선택사항이므로 별도 검증 없음
        // null 값은 해당 필드를 수정하지 않음을 의미
    }
}




