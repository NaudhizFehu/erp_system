package com.erp.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 비밀번호 변경 요청 DTO
 */
public record PasswordChangeDto(
        @NotBlank(message = "현재 비밀번호는 필수입니다")
        String currentPassword,
        @NotBlank(message = "새 비밀번호는 필수입니다")
        @Size(min = 6, message = "새 비밀번호는 6자 이상이어야 합니다")
        String newPassword
) {}
