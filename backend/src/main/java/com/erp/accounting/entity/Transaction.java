package com.erp.accounting.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import com.erp.hr.entity.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 회계 거래 엔티티
 * 복식부기의 기본 단위인 회계 거래를 관리합니다
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transactions_number", columnList = "transaction_number"),
    @Index(name = "idx_transactions_company", columnList = "company_id"),
    @Index(name = "idx_transactions_date", columnList = "transaction_date"),
    @Index(name = "idx_transactions_type", columnList = "transaction_type"),
    @Index(name = "idx_transactions_status", columnList = "transaction_status"),
    @Index(name = "idx_transactions_account", columnList = "account_id"),
    @Index(name = "idx_transactions_period", columnList = "fiscal_year, fiscal_month")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends BaseEntity {

    /**
     * 거래번호 (전표번호)
     */
    @NotBlank(message = "거래번호는 필수입니다")
    @Size(max = 30, message = "거래번호는 30자 이하여야 합니다")
    @Column(name = "transaction_number", nullable = false, length = 30)
    private String transactionNumber;

    /**
     * 소속 회사
     */
    @NotNull(message = "소속 회사는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /**
     * 거래일자
     */
    @NotNull(message = "거래일자는 필수입니다")
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    /**
     * 거래 유형
     */
    @NotNull(message = "거래 유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    /**
     * 거래 상태
     */
    @NotNull(message = "거래 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false, length = 20)
    private TransactionStatus transactionStatus = TransactionStatus.DRAFT;

    /**
     * 계정과목
     */
    @NotNull(message = "계정과목은 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /**
     * 차변 금액
     */
    @NotNull(message = "차변 금액은 필수입니다")
    @Min(value = 0, message = "차변 금액은 0 이상이어야 합니다")
    @Column(name = "debit_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal debitAmount = BigDecimal.ZERO;

    /**
     * 대변 금액
     */
    @NotNull(message = "대변 금액은 필수입니다")
    @Min(value = 0, message = "대변 금액은 0 이상이어야 합니다")
    @Column(name = "credit_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal creditAmount = BigDecimal.ZERO;

    /**
     * 거래 설명
     */
    @Size(max = 500, message = "거래 설명은 500자 이하여야 합니다")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 적요 (간단한 거래 내용)
     */
    @Size(max = 200, message = "적요는 200자 이하여야 합니다")
    @Column(name = "memo", length = 200)
    private String memo;

    /**
     * 회계연도
     */
    @NotNull(message = "회계연도는 필수입니다")
    @Min(value = 2000, message = "회계연도는 2000년 이상이어야 합니다")
    @Max(value = 2100, message = "회계연도는 2100년 이하여야 합니다")
    @Column(name = "fiscal_year", nullable = false)
    private Integer fiscalYear;

    /**
     * 회계월
     */
    @NotNull(message = "회계월은 필수입니다")
    @Min(value = 1, message = "회계월은 1 이상이어야 합니다")
    @Max(value = 12, message = "회계월은 12 이하여야 합니다")
    @Column(name = "fiscal_month", nullable = false)
    private Integer fiscalMonth;

    /**
     * 회계기수 (분기)
     */
    @Min(value = 1, message = "회계기수는 1 이상이어야 합니다")
    @Max(value = 4, message = "회계기수는 4 이하여야 합니다")
    @Column(name = "fiscal_quarter")
    private Integer fiscalQuarter;

    /**
     * 거래처 정보
     */
    @Size(max = 100, message = "거래처 정보는 100자 이하여야 합니다")
    @Column(name = "business_partner", length = 100)
    private String businessPartner;

    /**
     * 부서 정보
     */
    @Size(max = 100, message = "부서 정보는 100자 이하여야 합니다")
    @Column(name = "department_info", length = 100)
    private String departmentInfo;

    /**
     * 프로젝트 코드
     */
    @Size(max = 50, message = "프로젝트 코드는 50자 이하여야 합니다")
    @Column(name = "project_code", length = 50)
    private String projectCode;

    /**
     * 세금 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type", length = 20)
    private TaxType taxType;

    /**
     * 세금 금액
     */
    @Min(value = 0, message = "세금 금액은 0 이상이어야 합니다")
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    /**
     * 세금 계산서 번호
     */
    @Size(max = 50, message = "세금계산서 번호는 50자 이하여야 합니다")
    @Column(name = "tax_invoice_number", length = 50)
    private String taxInvoiceNumber;

    /**
     * 증빙서류 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", length = 20)
    private DocumentType documentType;

    /**
     * 증빙서류 번호
     */
    @Size(max = 50, message = "증빙서류 번호는 50자 이하여야 합니다")
    @Column(name = "document_number", length = 50)
    private String documentNumber;

    /**
     * 첨부파일 경로
     */
    @Size(max = 500, message = "첨부파일 경로는 500자 이하여야 합니다")
    @Column(name = "attachment_path", length = 500)
    private String attachmentPath;

    /**
     * 입력자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "input_by")
    private Employee inputBy;

    /**
     * 승인자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    /**
     * 승인 시간
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * 취소 사유
     */
    @Size(max = 200, message = "취소 사유는 200자 이하여야 합니다")
    @Column(name = "cancel_reason", length = 200)
    private String cancelReason;

    /**
     * 취소 시간
     */
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    /**
     * 원거래 (수정/취소 시 참조)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_transaction_id")
    private Transaction originalTransaction;

    /**
     * 관련 거래들 (수정/취소된 거래들)
     */
    @OneToMany(mappedBy = "originalTransaction", fetch = FetchType.LAZY)
    private List<Transaction> relatedTransactions;

    /**
     * 거래 유형 열거형
     */
    public enum TransactionType {
        JOURNAL("일반분개"),
        SALES("매출"),
        PURCHASE("매입"),
        CASH_RECEIPT("현금수입"),
        CASH_PAYMENT("현금지출"),
        BANK_RECEIPT("예금수입"),
        BANK_PAYMENT("예금지출"),
        ADJUSTMENT("수정분개"),
        CLOSING("결산분개");

        private final String description;

        TransactionType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 거래 상태 열거형
     */
    public enum TransactionStatus {
        DRAFT("임시저장"),
        PENDING("승인대기"),
        APPROVED("승인완료"),
        POSTED("전기완료"),
        CANCELLED("취소");

        private final String description;

        TransactionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 세금 유형 열거형
     */
    public enum TaxType {
        VAT_10("부가세 10%"),
        VAT_0("부가세 0%"),
        TAX_FREE("면세"),
        WITHHOLDING("원천세");

        private final String description;

        TaxType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 증빙서류 유형 열거형
     */
    public enum DocumentType {
        TAX_INVOICE("세금계산서"),
        CASH_RECEIPT("현금영수증"),
        CREDIT_CARD("신용카드"),
        BANK_TRANSFER("계좌이체"),
        PROMISSORY_NOTE("약속어음"),
        RECEIPT("영수증"),
        CONTRACT("계약서"),
        OTHER("기타");

        private final String description;

        DocumentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 거래 금액 반환 (차변 또는 대변 중 0이 아닌 금액)
     */
    public BigDecimal getAmount() {
        return debitAmount.compareTo(BigDecimal.ZERO) > 0 ? debitAmount : creditAmount;
    }

    /**
     * 차변 거래인지 확인
     */
    public boolean isDebitTransaction() {
        return debitAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 대변 거래인지 확인
     */
    public boolean isCreditTransaction() {
        return creditAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 거래 승인
     */
    public void approve(Employee approver) {
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
        this.transactionStatus = TransactionStatus.APPROVED;
    }

    /**
     * 거래 전기
     */
    public void post() {
        if (transactionStatus != TransactionStatus.APPROVED) {
            throw new IllegalStateException("승인된 거래만 전기할 수 있습니다");
        }
        this.transactionStatus = TransactionStatus.POSTED;
        
        // 계정과목 잔액 업데이트
        if (account.getTrackBalance()) {
            account.updateBalance(getAmount(), isDebitTransaction());
        }
    }

    /**
     * 거래 취소
     */
    public void cancel(String reason, Employee cancelBy) {
        if (transactionStatus == TransactionStatus.POSTED) {
            throw new IllegalStateException("전기된 거래는 취소할 수 없습니다. 수정분개를 사용하세요");
        }
        
        this.transactionStatus = TransactionStatus.CANCELLED;
        this.cancelReason = reason;
        this.cancelledAt = LocalDateTime.now();
    }

    /**
     * 거래 복사 (수정분개용)
     */
    public Transaction createReversingEntry() {
        Transaction reversingEntry = new Transaction();
        reversingEntry.setCompany(this.company);
        reversingEntry.setTransactionDate(this.transactionDate);
        reversingEntry.setTransactionType(TransactionType.ADJUSTMENT);
        reversingEntry.setAccount(this.account);
        reversingEntry.setDebitAmount(this.creditAmount); // 차대 반대
        reversingEntry.setCreditAmount(this.debitAmount); // 차대 반대
        reversingEntry.setDescription("수정분개 - " + this.description);
        reversingEntry.setFiscalYear(this.fiscalYear);
        reversingEntry.setFiscalMonth(this.fiscalMonth);
        reversingEntry.setOriginalTransaction(this);
        
        return reversingEntry;
    }

    /**
     * 회계기간 계산
     */
    @PrePersist
    @PreUpdate
    private void beforeSaveOrUpdate() {
        // 회계기간 계산
        if (transactionDate != null) {
            this.fiscalYear = transactionDate.getYear();
            this.fiscalMonth = transactionDate.getMonthValue();
            this.fiscalQuarter = (fiscalMonth - 1) / 3 + 1;
        }
        
        // 거래 검증
        boolean hasDebit = debitAmount.compareTo(BigDecimal.ZERO) > 0;
        boolean hasCredit = creditAmount.compareTo(BigDecimal.ZERO) > 0;
        
        if (hasDebit && hasCredit) {
            throw new IllegalArgumentException("차변과 대변 중 하나만 금액을 입력할 수 있습니다");
        }
        
        if (!hasDebit && !hasCredit) {
            throw new IllegalArgumentException("차변 또는 대변 금액을 입력해야 합니다");
        }

        // 말단 계정과목만 거래 입력 가능
        if (account != null && !account.isLeafAccount()) {
            throw new IllegalArgumentException("말단 계정과목만 거래를 입력할 수 있습니다");
        }
    }
}
