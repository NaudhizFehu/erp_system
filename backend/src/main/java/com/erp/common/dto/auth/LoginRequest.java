package com.erp.common.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 로그인 요청 DTO
 * 사용자 로그인 시 사용되는 요청 데이터입니다
 */
public record LoginRequest(
        @NotBlank(message = "사용자명 또는 이메일은 필수입니다")
        @Size(min = 3, max = 100, message = "사용자명 또는 이메일은 3자 이상 100자 이하여야 합니다")
        String usernameOrEmail,
        
        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 1, max = 100, message = "비밀번호는 100자 이하여야 합니다")
        String password,
        
        Boolean rememberMe
) {
    public LoginRequest {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명 또는 이메일은 필수입니다");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다");
        }
        
        // 기본값 설정
        if (rememberMe == null) {
            rememberMe = false;
        }
    }
}




