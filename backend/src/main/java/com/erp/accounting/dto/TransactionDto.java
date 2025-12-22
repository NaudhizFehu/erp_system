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
        // fiscalYear와 fiscalMonth는 기존 데이터에서 null일 수 있으므로 검증 제거
        // 필요시 transactionDate에서 자동 계산
    }
    
    /**
     * Transaction 엔티티로부터 TransactionDto 생성
     */
    public static TransactionDto from(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction 엔티티는 null일 수 없습니다");
        }
        
        // 필수 필드 검증
        if (transaction.getCompany() == null) {
            throw new IllegalArgumentException("Transaction의 company는 null일 수 없습니다. transactionId: " + transaction.getId());
        }
        if (transaction.getAccount() == null) {
            throw new IllegalArgumentException("Transaction의 account는 null일 수 없습니다. transactionId: " + transaction.getId());
        }
        
        // fiscalYear와 fiscalMonth가 null인 경우 transactionDate에서 계산
        Integer fiscalYear = transaction.getFiscalYear();
        Integer fiscalMonth = transaction.getFiscalMonth();
        if (fiscalYear == null && transaction.getTransactionDate() != null) {
            fiscalYear = transaction.getTransactionDate().getYear();
        }
        if (fiscalMonth == null && transaction.getTransactionDate() != null) {
            fiscalMonth = transaction.getTransactionDate().getMonthValue();
        }
        
        return new TransactionDto(
            transaction.getId(),
            transaction.getTransactionNumber(),
            CompanyDto.from(transaction.getCompany()),
            transaction.getTransactionDate(),
            transaction.getTransactionType(),
            transaction.getTransactionStatus(),
            AccountDto.from(transaction.getAccount()),
            transaction.getDebitAmount(),
            transaction.getCreditAmount(),
            transaction.getDescription(),
            transaction.getMemo(),
            fiscalYear,
            fiscalMonth,
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
            // originalTransaction은 재귀 호출을 피하기 위해 null로 설정 (필요시 별도 조회)
            null,
            // getAmount()는 debitAmount나 creditAmount가 null이면 NPE 발생 가능하므로 안전하게 처리
            transaction.getDebitAmount() != null && transaction.getCreditAmount() != null 
                ? (transaction.getDebitAmount().compareTo(BigDecimal.ZERO) > 0 
                    ? transaction.getDebitAmount() 
                    : transaction.getCreditAmount())
                : (transaction.getDebitAmount() != null ? transaction.getDebitAmount() : 
                   (transaction.getCreditAmount() != null ? transaction.getCreditAmount() : BigDecimal.ZERO)),
            // isDebitTransaction()과 isCreditTransaction()도 null 체크 필요
            transaction.getDebitAmount() != null && transaction.getDebitAmount().compareTo(BigDecimal.ZERO) > 0,
            transaction.getCreditAmount() != null && transaction.getCreditAmount().compareTo(BigDecimal.ZERO) > 0,
            transaction.getCreatedAt(),
            transaction.getUpdatedAt()
        );
    }
}




