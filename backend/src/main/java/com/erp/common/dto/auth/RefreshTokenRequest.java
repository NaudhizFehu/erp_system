package com.erp.common.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * 토큰 갱신 요청 DTO
 * 액세스 토큰 갱신 시 사용되는 요청 데이터입니다
 */
public record RefreshTokenRequest(
        @NotBlank(message = "리프레시 토큰은 필수입니다")
        String refreshToken
) {
    public RefreshTokenRequest {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new IllegalArgumentException("리프레시 토큰은 필수입니다");
        }
    }
}




