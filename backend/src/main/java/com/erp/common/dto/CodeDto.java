package com.erp.common.dto;

import com.erp.common.entity.Code;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

/**
 * 공통코드 정보 응답 DTO
 * 공통코드 정보 조회 시 사용됩니다
 */
public record CodeDto(
        Long id,
        String groupCode,
        String codeValue,
        String codeName,
        String codeNameEn,
        String description,
        CodeDto parentCode,
        List<CodeDto> subCodes,
        Integer sortOrder,
        Integer level,
        Boolean isActive,
        Boolean isSystem,
        Boolean isEditable,
        CompanyDto company,
        Code.CodeType codeType,
        String attribute1,
        String attribute2,
        String attribute3,
        BigDecimal numericAttribute1,
        BigDecimal numericAttribute2,
        LocalDate dateAttribute1,
        LocalDate dateAttribute2,
        String fullPath,
        String fullCodeValue,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public CodeDto {
        if (groupCode == null || groupCode.trim().isEmpty()) {
            throw new IllegalArgumentException("그룹 코드는 필수입니다");
        }
        if (codeValue == null || codeValue.trim().isEmpty()) {
            throw new IllegalArgumentException("코드 값은 필수입니다");
        }
        if (codeName == null || codeName.trim().isEmpty()) {
            throw new IllegalArgumentException("코드명은 필수입니다");
        }
        if (sortOrder == null || sortOrder < 0) {
            throw new IllegalArgumentException("정렬 순서는 0 이상이어야 합니다");
        }
        if (level == null || level < 1) {
            throw new IllegalArgumentException("코드 레벨은 1 이상이어야 합니다");
        }
        if (isActive == null) {
            throw new IllegalArgumentException("사용 여부는 필수입니다");
        }
    }
}




