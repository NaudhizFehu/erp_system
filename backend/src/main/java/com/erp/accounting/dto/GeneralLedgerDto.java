package com.erp.accounting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 총계정원장 DTO
 * 총계정원장 조회 결과를 담는 DTO입니다
 */
public record GeneralLedgerDto(
        LocalDate transactionDate,
        String transactionNumber,
        String description,
        BigDecimal debitAmount,
        BigDecimal creditAmount,
        BigDecimal balance
) {
    public GeneralLedgerDto {
        if (transactionDate == null) {
            throw new IllegalArgumentException("거래일자는 필수입니다");
        }
        if (transactionNumber == null || transactionNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("거래번호는 필수입니다");
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




