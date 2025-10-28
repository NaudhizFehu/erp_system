package com.erp.hr.dto;

import java.util.List;

/**
 * 직원 데이터 가져오기 결과 DTO
 */
public record ImportResult(
    int totalRows,
    int successCount,
    int failCount,
    List<String> errors
) {}
