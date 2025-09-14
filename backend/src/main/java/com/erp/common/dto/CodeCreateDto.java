package com.erp.common.dto;

import com.erp.common.entity.Code;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * 공통코드 생성 DTO
 * 새로운 공통코드 등록 시 사용됩니다
 */
public record CodeCreateDto(
        @NotBlank(message = "그룹 코드는 필수입니다")
        @Size(max = 50, message = "그룹 코드는 50자 이하여야 합니다")
        @Pattern(regexp = "^[A-Z0-9_]+$", message = "그룹 코드는 대문자, 숫자, 언더스코어만 사용 가능합니다")
        String groupCode,
        
        @NotBlank(message = "코드 값은 필수입니다")
        @Size(max = 50, message = "코드 값은 50자 이하여야 합니다")
        @Pattern(regexp = "^[A-Z0-9_]+$", message = "코드 값은 대문자, 숫자, 언더스코어만 사용 가능합니다")
        String codeValue,
        
        @NotBlank(message = "코드명은 필수입니다")
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
        Boolean isSystem,
        Boolean isEditable,
        Long companyId,
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
    public CodeCreateDto {
        if (groupCode == null || groupCode.trim().isEmpty()) {
            throw new IllegalArgumentException("그룹 코드는 필수입니다");
        }
        if (codeValue == null || codeValue.trim().isEmpty()) {
            throw new IllegalArgumentException("코드 값은 필수입니다");
        }
        if (codeName == null || codeName.trim().isEmpty()) {
            throw new IllegalArgumentException("코드명은 필수입니다");
        }
        
        // 기본값 설정
        if (sortOrder == null) {
            sortOrder = 0;
        }
        if (isActive == null) {
            isActive = true;
        }
        if (isSystem == null) {
            isSystem = false;
        }
        if (isEditable == null) {
            isEditable = true;
        }
        if (codeType == null) {
            codeType = Code.CodeType.COMMON;
        }
    }
}




