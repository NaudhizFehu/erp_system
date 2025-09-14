package com.erp.common.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 비밀번호 변경 요청 DTO
 * 사용자 비밀번호 변경 시 사용되는 요청 데이터입니다
 */
public record ChangePasswordRequest(
        @NotBlank(message = "현재 비밀번호는 필수입니다")
        String currentPassword,
        
        @NotBlank(message = "새 비밀번호는 필수입니다")
        @Size(min = 8, max = 50, message = "새 비밀번호는 8자 이상 50자 이하여야 합니다")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "새 비밀번호는 대소문자, 숫자, 특수문자를 각각 최소 1개씩 포함해야 합니다"
        )
        String newPassword,
        
        @NotBlank(message = "새 비밀번호 확인은 필수입니다")
        String confirmPassword
) {
    public ChangePasswordRequest {
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("현재 비밀번호는 필수입니다");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("새 비밀번호는 필수입니다");
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("새 비밀번호 확인은 필수입니다");
        }
        
        // 새 비밀번호와 확인 비밀번호 일치 검증
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다");
        }
        
        // 현재 비밀번호와 새 비밀번호 동일 검증
        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다");
        }
    }
}




