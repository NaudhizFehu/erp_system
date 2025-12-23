package com.erp.common.dto;

import com.erp.common.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 DTO
 * 사용자 정보 조회 시 사용됩니다
 */
public record UserDto(
        Long id,
        String username,
        String email,
        String fullName,
        String phone,
        User.UserRole role,
        Boolean isActive,
        Boolean isLocked,
        Boolean isPasswordExpired,
        CompanyDto company,
        DepartmentDto department,
        LocalDateTime lastLoginAt,
        LocalDateTime passwordChangedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public UserDto {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명은 필수입니다");
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
    }

    /**
     * 사용자 정보 수정 요청 DTO
     */
    public record UserUpdateDto(
            @Email(message = "올바른 이메일 형식이어야 합니다")
            @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
            String email,

            @Size(max = 50, message = "실명은 50자 이하여야 합니다")
            String fullName,

            @Pattern(regexp = "^(01[016789]-?\\d{3,4}-?\\d{4}|0[2-6]\\d?-?\\d{3,4}-?\\d{4}|\\d{4}-?\\d{4}|\\d{10,11}|\\d{8})$",
                    message = "올바른 유선전화번호 형식이어야 합니다")
            String phone,

            @Pattern(regexp = "^(01[016789]-?\\d{3,4}-?\\d{4}|0[2-6]\\d?-?\\d{3,4}-?\\d{4}|\\d{4}-?\\d{4}|\\d{10,11}|\\d{8})$",
                    message = "올바른 휴대폰번호 형식이어야 합니다")
            String phoneNumber,

            @Size(max = 100, message = "직급은 100자 이하여야 합니다")
            String position
    ) {}
}




