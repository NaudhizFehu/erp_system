package com.erp.common.dto;

import com.erp.common.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 사용자 프로필 DTO
 */
public record UserProfileDto(
        Long id,
        String username,
        @NotBlank(message = "이름은 필수입니다")
        @Size(max = 100, message = "이름은 100자 이하여야 합니다")
        String fullName,
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        @Size(max = 255, message = "이메일은 255자 이하여야 합니다")
        String email,
        @Size(max = 20, message = "유선전화번호는 20자 이하여야 합니다")
        String phone,
        @Size(max = 20, message = "휴대폰번호는 20자 이하여야 합니다")
        String phoneNumber,
        @Size(max = 100, message = "부서명은 100자 이하여야 합니다")
        String department,
        @Size(max = 100, message = "직급은 100자 이하여야 합니다")
        String position,
        String role,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserProfileDto from(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getPhoneNumber(),
                user.getDepartment() != null ? user.getDepartment().getName() : null,
                user.getPosition(),
                user.getRole() != null ? user.getRole().toString() : null,
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
