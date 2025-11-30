package com.erp.accounting.dto;

import java.util.List;

/**
 * 계정과목 트리 응답 DTO
 */
public record AccountTreeNodeDto(
        Long id,
        String accountCode,
        String accountName,
        Integer accountLevel,
        Boolean isActive,
        List<AccountTreeNodeDto> children
) {}




