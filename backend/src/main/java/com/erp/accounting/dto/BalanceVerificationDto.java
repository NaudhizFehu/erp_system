package com.erp.accounting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 대차평형 검증 DTO
 * 대차평형 검증 결과를 담는 DTO입니다
 */
public record BalanceVerificationDto(
        LocalDate asOfDate,
        BigDecimal totalAssets,
        BigDecimal totalLiabilities,
        BigDecimal totalEquity,
        BigDecimal totalDebits,
        BigDecimal totalCredits,
        BigDecimal balanceDifference,
        Boolean isBalanced,
        String message
) {
    public BalanceVerificationDto {
        if (asOfDate == null) {
            throw new IllegalArgumentException("기준일은 필수입니다");
        }
        if (totalAssets == null) {
            totalAssets = BigDecimal.ZERO;
        }
        if (totalLiabilities == null) {
            totalLiabilities = BigDecimal.ZERO;
        }
        if (totalEquity == null) {
            totalEquity = BigDecimal.ZERO;
        }
        if (totalDebits == null) {
            totalDebits = BigDecimal.ZERO;
        }
        if (totalCredits == null) {
            totalCredits = BigDecimal.ZERO;
        }
        if (balanceDifference == null) {
            balanceDifference = BigDecimal.ZERO;
        }
        if (isBalanced == null) {
            isBalanced = balanceDifference.compareTo(BigDecimal.ZERO) == 0;
        }
    }
}




