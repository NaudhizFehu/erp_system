package com.erp.common.dto;

import com.erp.common.entity.Code;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * 공통코드 수정 DTO
 * 기존 공통코드 정보 수정 시 사용됩니다
 */
public record CodeUpdateDto(
        @Size(max = 100, message = "코드명은 100자 이하여야 합니다")
        String codeName,
        
        @Size(max = 100, message = "영문 코드명은 100자 이하여야 합니다")
        String codeNameEn,
        
        @Size(max = 500, message = "코드 설명은 500자 이하여야 합니다")
        String description,
        
        Long parentCodeId,
        
        @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
        Integer sortOrder,
        
        Boolean isActive,
        Code.CodeType codeType,
        
        @Size(max = 100, message = "추가 속성1은 100자 이하여야 합니다")
        String attribute1,
        
        @Size(max = 100, message = "추가 속성2는 100자 이하여야 합니다")
        String attribute2,
        
        @Size(max = 100, message = "추가 속성3은 100자 이하여야 합니다")
        String attribute3,
        
        BigDecimal numericAttribute1,
        BigDecimal numericAttribute2,
        LocalDate dateAttribute1,
        LocalDate dateAttribute2
) {
    public CodeUpdateDto {
        // 수정 DTO는 모든 필드가 선택사항이므로 별도 검증 없음
        // null 값은 해당 필드를 수정하지 않음을 의미
        // 단, 시스템 코드의 경우 일부 필드는 수정 불가능
    }
}




