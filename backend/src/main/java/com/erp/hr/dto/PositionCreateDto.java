package com.erp.hr.dto;

import jakarta.validation.constraints.*;
// import java.math.BigDecimal; // 사용하지 않음

/**
 * 직급 생성 DTO
 * 새로운 직급 등록 시 사용됩니다
 */
public record PositionCreateDto(
        @NotBlank(message = "직급 코드는 필수입니다")
        @Size(max = 20, message = "직급 코드는 20자 이하여야 합니다")
        @Pattern(regexp = "^[A-Z0-9_]+$", message = "직급 코드는 대문자, 숫자, 언더스코어만 사용 가능합니다")
        String positionCode,
        
        @NotBlank(message = "직급명은 필수입니다")
        @Size(max = 50, message = "직급명은 50자 이하여야 합니다")
        String name,
        
        @Size(max = 500, message = "직급 설명은 500자 이하여야 합니다")
        String description,
        
        @NotNull(message = "소속 회사는 필수입니다")
        Long companyId,
        
        @NotNull(message = "직급 레벨은 필수입니다")
        @Min(value = 1, message = "직급 레벨은 1 이상이어야 합니다")
        @Max(value = 20, message = "직급 레벨은 20 이하여야 합니다")
        Integer level,
        
        Boolean isActive,
        
        @Size(max = 1000, message = "직무 권한은 1000자 이하여야 합니다")
        String authorities
) {
    public PositionCreateDto {
        if (positionCode == null || positionCode.trim().isEmpty()) {
            throw new IllegalArgumentException("직급 코드는 필수입니다");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("직급명은 필수입니다");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("소속 회사는 필수입니다");
        }
        
        // 기본값 설정
        if (isActive == null) {
            isActive = true;
        }
        if (level == null) {
            level = 1;
        }
        
        // 급여 범위 검증 - minSalary, maxSalary 필드 제거로 인해 비활성화
    }
}




