package com.erp.hr.dto;

import jakarta.validation.constraints.*;
// import java.math.BigDecimal; // 사용하지 않음

/**
 * 직급 수정 DTO
 * 기존 직급 정보 수정 시 사용됩니다
 */
public record PositionUpdateDto(
        @Size(max = 50, message = "직급명은 50자 이하여야 합니다")
        String name,
        
        @Size(max = 500, message = "직급 설명은 500자 이하여야 합니다")
        String description,
        
        @Min(value = 1, message = "직급 레벨은 1 이상이어야 합니다")
        @Max(value = 20, message = "직급 레벨은 20 이하여야 합니다")
        Integer level,
        
        Boolean isActive,
        
        @Size(max = 1000, message = "직무 권한은 1000자 이하여야 합니다")
        String authorities
) {
    public PositionUpdateDto {
        // 수정 DTO는 모든 필드가 선택사항이므로 별도 검증 없음
        // null 값은 해당 필드를 수정하지 않음을 의미
        
        // 급여 범위 검증 - minSalary, maxSalary 필드 제거로 인해 비활성화
    }
}




