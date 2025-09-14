package com.erp.common.dto;

import com.erp.common.entity.User;
import jakarta.validation.constraints.*;

/**
 * 사용자 생성 DTO
 * 새로운 사용자 등록 시 사용됩니다
 */
public record UserCreateDto(
        @NotBlank(message = "사용자명은 필수입니다")
        @Size(min = 3, max = 50, message = "사용자명은 3자 이상 50자 이하여야 합니다")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "사용자명은 영문, 숫자, ., _, -만 사용 가능합니다")
        String username,
        
        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, max = 50, message = "비밀번호는 8자 이상 50자 이하여야 합니다")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
                message = "비밀번호는 대소문자, 숫자, 특수문자를 각각 최소 1개씩 포함해야 합니다")
        String password,
        
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
        String email,
        
        @NotBlank(message = "실명은 필수입니다")
        @Size(max = 50, message = "실명은 50자 이하여야 합니다")
        String fullName,
        
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다")
        String phone,
        
        @NotNull(message = "사용자 역할은 필수입니다")
        User.UserRole role,
        
        Boolean isActive,
        Long companyId,
        Long departmentId
) {
    public UserCreateDto {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명은 필수입니다");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수입니다");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("실명은 필수입니다");
        }
        if (role == null) {
            throw new IllegalArgumentException("사용자 역할은 필수입니다");
        }
        
        // 기본값 설정
        if (isActive == null) {
            isActive = true;
        }
    }
}




