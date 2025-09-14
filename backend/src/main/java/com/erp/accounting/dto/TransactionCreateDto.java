package com.erp.accounting.dto;

import com.erp.accounting.entity.Transaction;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * 거래 생성 DTO
 * 새로운 거래 등록 시 사용됩니다
 */
public record TransactionCreateDto(
        @NotBlank(message = "거래번호는 필수입니다")
        @Size(max = 30, message = "거래번호는 30자 이하여야 합니다")
        String transactionNumber,
        
        @NotNull(message = "소속 회사는 필수입니다")
        Long companyId,
        
        @NotNull(message = "거래일자는 필수입니다")
        LocalDate transactionDate,
        
        @NotNull(message = "거래 유형은 필수입니다")
        Transaction.TransactionType transactionType,
        
        @NotNull(message = "계정과목은 필수입니다")
        Long accountId,
        
        @NotNull(message = "차변 금액은 필수입니다")
        @Min(value = 0, message = "차변 금액은 0 이상이어야 합니다")
        BigDecimal debitAmount,
        
        @NotNull(message = "대변 금액은 필수입니다")
        @Min(value = 0, message = "대변 금액은 0 이상이어야 합니다")
        BigDecimal creditAmount,
        
        @Size(max = 500, message = "거래 설명은 500자 이하여야 합니다")
        String description,
        
        @Size(max = 200, message = "적요는 200자 이하여야 합니다")
        String memo,
        
        @Size(max = 100, message = "거래처 정보는 100자 이하여야 합니다")
        String businessPartner,
        
        @Size(max = 100, message = "부서 정보는 100자 이하여야 합니다")
        String departmentInfo,
        
        @Size(max = 50, message = "프로젝트 코드는 50자 이하여야 합니다")
        String projectCode,
        
        Transaction.TaxType taxType,
        
        @Min(value = 0, message = "세금 금액은 0 이상이어야 합니다")
        BigDecimal taxAmount,
        
        @Size(max = 50, message = "세금계산서 번호는 50자 이하여야 합니다")
        String taxInvoiceNumber,
        
        Transaction.DocumentType documentType,
        
        @Size(max = 50, message = "증빙서류 번호는 50자 이하여야 합니다")
        String documentNumber,
        
        @Size(max = 500, message = "첨부파일 경로는 500자 이하여야 합니다")
        String attachmentPath,
        
        Long inputById
) {
    public TransactionCreateDto {
        if (transactionNumber == null || transactionNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("거래번호는 필수입니다");
        }
        if (companyId == null) {
            throw new IllegalArgumentException("소속 회사는 필수입니다");
        }
        if (transactionDate == null) {
            throw new IllegalArgumentException("거래일자는 필수입니다");
        }
        if (transactionType == null) {
            throw new IllegalArgumentException("거래 유형은 필수입니다");
        }
        if (accountId == null) {
            throw new IllegalArgumentException("계정과목은 필수입니다");
        }
        if (debitAmount == null || debitAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("차변 금액은 0 이상이어야 합니다");
        }
        if (creditAmount == null || creditAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("대변 금액은 0 이상이어야 합니다");
        }
        
        // 차변과 대변 중 하나만 0이 아니어야 함
        boolean hasDebit = debitAmount.compareTo(BigDecimal.ZERO) > 0;
        boolean hasCredit = creditAmount.compareTo(BigDecimal.ZERO) > 0;
        
        if (hasDebit && hasCredit) {
            throw new IllegalArgumentException("차변과 대변 중 하나만 금액을 입력할 수 있습니다");
        }
        
        if (!hasDebit && !hasCredit) {
            throw new IllegalArgumentException("차변 또는 대변 금액을 입력해야 합니다");
        }
        
        // 기본값 설정
        if (taxAmount == null) {
            taxAmount = BigDecimal.ZERO;
        }
        
        // 거래일자가 미래일 수 없음
        if (transactionDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("거래일자는 오늘 이전이어야 합니다");
        }
    }
}




