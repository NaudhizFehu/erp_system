package com.erp.accounting.dto;

import java.math.BigDecimal;

/**
 * 시산표 DTO
 * 시산표 조회 결과를 담는 DTO입니다
 */
public record TrialBalanceDto(
        String accountCode,
        String accountName,
        String accountType,
        BigDecimal debitAmount,
        BigDecimal creditAmount,
        BigDecimal balance
) {
    public TrialBalanceDto {
        if (accountCode == null || accountCode.trim().isEmpty()) {
            throw new IllegalArgumentException("계정과목 코드는 필수입니다");
        }
        if (accountName == null || accountName.trim().isEmpty()) {
            throw new IllegalArgumentException("계정과목명은 필수입니다");
        }
        if (accountType == null || accountType.trim().isEmpty()) {
            throw new IllegalArgumentException("계정과목 유형은 필수입니다");
        }
        if (debitAmount == null) {
            debitAmount = BigDecimal.ZERO;
        }
        if (creditAmount == null) {
            creditAmount = BigDecimal.ZERO;
        }
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
    }
}




