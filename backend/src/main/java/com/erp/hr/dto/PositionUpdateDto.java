package com.erp.hr.dto;

import com.erp.hr.entity.Position;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 직급 수정 DTO
 * 기존 직급 정보 수정 시 사용됩니다
 */
public record PositionUpdateDto(
        @Size(max = 50, message = "직급명은 50자 이하여야 합니다")
        String name,
        
        @Size(max = 100, message = "영문 직급명은 100자 이하여야 합니다")
        String nameEn,
        
        @Size(max = 500, message = "직급 설명은 500자 이하여야 합니다")
        String description,
        
        @Min(value = 1, message = "직급 레벨은 1 이상이어야 합니다")
        @Max(value = 20, message = "직급 레벨은 20 이하여야 합니다")
        Integer positionLevel,
        
        Position.PositionCategory positionCategory,
        Position.PositionType positionType,
        
        @Min(value = 0, message = "최소 기본급은 0 이상이어야 합니다")
        BigDecimal minSalary,
        
        @Min(value = 0, message = "최대 기본급은 0 이상이어야 합니다")
        BigDecimal maxSalary,
        
        @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
        Integer sortOrder,
        
        Boolean isActive,
        
        @Size(max = 200, message = "승진 가능 직급은 200자 이하여야 합니다")
        String promotionTargets,
        
        @Size(max = 1000, message = "필요 자격 요건은 1000자 이하여야 합니다")
        String requirements,
        
        @Size(max = 1000, message = "직무 권한은 1000자 이하여야 합니다")
        String authorities
) {
    public PositionUpdateDto {
        // 수정 DTO는 모든 필드가 선택사항이므로 별도 검증 없음
        // null 값은 해당 필드를 수정하지 않음을 의미
        
        // 급여 범위 검증 (둘 다 null이 아닌 경우에만)
        if (minSalary != null && maxSalary != null && minSalary.compareTo(maxSalary) > 0) {
            throw new IllegalArgumentException("최소 급여가 최대 급여보다 클 수 없습니다");
        }
    }
}




