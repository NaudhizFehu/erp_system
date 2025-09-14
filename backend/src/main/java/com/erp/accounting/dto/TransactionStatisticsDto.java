package com.erp.accounting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 거래 통계 DTO
 * 거래 통계 정보를 담는 DTO입니다
 */
public record TransactionStatisticsDto(
        LocalDate startDate,
        LocalDate endDate,
        Long totalTransactionCount,
        BigDecimal totalTransactionAmount,
        BigDecimal totalDebitAmount,
        BigDecimal totalCreditAmount,
        Map<String, Long> transactionCountByType,
        Map<String, BigDecimal> transactionAmountByType,
        Map<String, Long> transactionCountByStatus,
        Map<String, Long> dailyTransactionCounts,
        Map<String, BigDecimal> dailyTransactionAmounts
) {
    public TransactionStatisticsDto {
        if (startDate == null) {
            throw new IllegalArgumentException("시작일은 필수입니다");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("종료일은 필수입니다");
        }
        if (totalTransactionCount == null) {
            totalTransactionCount = 0L;
        }
        if (totalTransactionAmount == null) {
            totalTransactionAmount = BigDecimal.ZERO;
        }
        if (totalDebitAmount == null) {
            totalDebitAmount = BigDecimal.ZERO;
        }
        if (totalCreditAmount == null) {
            totalCreditAmount = BigDecimal.ZERO;
        }
    }
}




