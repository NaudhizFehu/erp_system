package com.erp.common.dto;

import com.erp.common.entity.User;
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
}




