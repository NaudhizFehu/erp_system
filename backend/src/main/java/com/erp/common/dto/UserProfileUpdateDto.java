package com.erp.common.dto;

import jakarta.validation.constraints.*;

/**
 * 사용자 프로필 업데이트 DTO
 * 사용자가 자신의 프로필 정보를 수정할 때 사용됩니다
 */
public record UserProfileUpdateDto(
        @Size(max = 50, message = "실명은 50자 이하여야 합니다")
        String fullName,
        
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
        String email,
        
        @Pattern(regexp = "^(01[016789]-?\\d{3,4}-?\\d{4}|0[2-6]\\d?-?\\d{3,4}-?\\d{4}|\\d{4}-?\\d{4}|\\d{10,11}|\\d{8})$", message = "올바른 유선전화번호 형식이어야 합니다")
        String phone,
        
        @Pattern(regexp = "^(01[016789]-?\\d{3,4}-?\\d{4}|0[2-6]\\d?-?\\d{3,4}-?\\d{4}|\\d{4}-?\\d{4}|\\d{10,11}|\\d{8})$", message = "올바른 휴대폰번호 형식이어야 합니다")
        String phoneNumber,
        
        @NotBlank(message = "부서는 필수입니다")
        String department,
        
        @NotBlank(message = "직책은 필수입니다")
        @Size(max = 50, message = "직책은 50자 이하여야 합니다")
        String position
) {
    public UserProfileUpdateDto {
        // 부서와 직책은 필수값으로 검증
        if (department != null && department.trim().isEmpty()) {
            throw new IllegalArgumentException("부서는 필수입니다");
        }
        if (position != null && position.trim().isEmpty()) {
            throw new IllegalArgumentException("직책은 필수입니다");
        }
        
        // 전화번호 정규화
        if (phone != null && !phone.trim().isEmpty()) {
            phone = phone.trim();
        }
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            phoneNumber = phoneNumber.trim();
        }
        
        // 이메일 정규화
        if (email != null && !email.trim().isEmpty()) {
            email = email.trim().toLowerCase();
        }
        
        // 실명 정규화
        if (fullName != null && !fullName.trim().isEmpty()) {
            fullName = fullName.trim();
        }
    }
}