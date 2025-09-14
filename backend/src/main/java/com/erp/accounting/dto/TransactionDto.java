package com.erp.accounting.dto;

import com.erp.common.dto.CompanyDto;
import com.erp.hr.dto.EmployeeDto;
import com.erp.accounting.entity.Transaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 거래 정보 응답 DTO
 * 거래 정보 조회 시 사용됩니다
 */
public record TransactionDto(
        Long id,
        String transactionNumber,
        CompanyDto company,
        LocalDate transactionDate,
        Transaction.TransactionType transactionType,
        Transaction.TransactionStatus transactionStatus,
        AccountDto account,
        BigDecimal debitAmount,
        BigDecimal creditAmount,
        String description,
        String memo,
        Integer fiscalYear,
        Integer fiscalMonth,
        Integer fiscalQuarter,
        String businessPartner,
        String departmentInfo,
        String projectCode,
        Transaction.TaxType taxType,
        BigDecimal taxAmount,
        String taxInvoiceNumber,
        Transaction.DocumentType documentType,
        String documentNumber,
        String attachmentPath,
        EmployeeDto inputBy,
        EmployeeDto approvedBy,
        LocalDateTime approvedAt,
        String cancelReason,
        LocalDateTime cancelledAt,
        TransactionDto originalTransaction,
        BigDecimal amount,
        Boolean isDebitTransaction,
        Boolean isCreditTransaction,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public TransactionDto {
        if (transactionNumber == null || transactionNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("거래번호는 필수입니다");
        }
        if (company == null) {
            throw new IllegalArgumentException("소속 회사는 필수입니다");
        }
        if (transactionDate == null) {
            throw new IllegalArgumentException("거래일자는 필수입니다");
        }
        if (transactionType == null) {
            throw new IllegalArgumentException("거래 유형은 필수입니다");
        }
        if (transactionStatus == null) {
            throw new IllegalArgumentException("거래 상태는 필수입니다");
        }
        if (account == null) {
            throw new IllegalArgumentException("계정과목은 필수입니다");
        }
        if (debitAmount == null || debitAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("차변 금액은 0 이상이어야 합니다");
        }
        if (creditAmount == null || creditAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("대변 금액은 0 이상이어야 합니다");
        }
        if (fiscalYear == null) {
            throw new IllegalArgumentException("회계연도는 필수입니다");
        }
        if (fiscalMonth == null) {
            throw new IllegalArgumentException("회계월은 필수입니다");
        }
    }
    
    /**
     * Transaction 엔티티로부터 TransactionDto 생성
     */
    public static TransactionDto from(Transaction transaction) {
        return new TransactionDto(
            transaction.getId(),
            transaction.getTransactionNumber(),
            transaction.getCompany() != null ? CompanyDto.from(transaction.getCompany()) : null,
            transaction.getTransactionDate(),
            transaction.getTransactionType(),
            transaction.getTransactionStatus(),
            transaction.getAccount() != null ? AccountDto.from(transaction.getAccount()) : null,
            transaction.getDebitAmount(),
            transaction.getCreditAmount(),
            transaction.getDescription(),
            transaction.getMemo(),
            transaction.getFiscalYear(),
            transaction.getFiscalMonth(),
            transaction.getFiscalQuarter(),
            transaction.getBusinessPartner(),
            transaction.getDepartmentInfo(),
            transaction.getProjectCode(),
            transaction.getTaxType(),
            transaction.getTaxAmount(),
            transaction.getTaxInvoiceNumber(),
            transaction.getDocumentType(),
            transaction.getDocumentNumber(),
            transaction.getAttachmentPath(),
            transaction.getInputBy() != null ? EmployeeDto.from(transaction.getInputBy()) : null,
            transaction.getApprovedBy() != null ? EmployeeDto.from(transaction.getApprovedBy()) : null,
            transaction.getApprovedAt(),
            transaction.getCancelReason(),
            transaction.getCancelledAt(),
            transaction.getOriginalTransaction() != null ? TransactionDto.from(transaction.getOriginalTransaction()) : null,
            transaction.getAmount(),
            transaction.isDebitTransaction(),
            transaction.isCreditTransaction(),
            transaction.getCreatedAt(),
            transaction.getUpdatedAt()
        );
    }
}




