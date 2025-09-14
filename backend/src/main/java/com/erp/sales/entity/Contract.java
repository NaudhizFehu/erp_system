package com.erp.sales.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 계약 엔티티
 * 고객과의 계약 정보를 관리합니다
 */
@Entity
@Table(name = "contracts", indexes = {
    @Index(name = "idx_contract_company", columnList = "company_id"),
    @Index(name = "idx_contract_customer", columnList = "customer_id"),
    @Index(name = "idx_contract_number", columnList = "contract_number"),
    @Index(name = "idx_contract_status", columnList = "contract_status"),
    @Index(name = "idx_contract_start_date", columnList = "start_date"),
    @Index(name = "idx_contract_end_date", columnList = "end_date"),
    @Index(name = "idx_contract_order", columnList = "order_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Contract extends BaseEntity {

    /**
     * 계약 상태 열거형
     */
    public enum ContractStatus {
        DRAFT,          // 임시저장
        PENDING,        // 검토중
        APPROVED,       // 승인됨
        ACTIVE,         // 활성
        SUSPENDED,      // 중단
        COMPLETED,      // 완료
        TERMINATED,     // 해지
        EXPIRED,        // 만료
        CANCELLED       // 취소
    }

    /**
     * 계약 유형 열거형
     */
    public enum ContractType {
        SALES,          // 판매계약
        SERVICE,        // 서비스계약
        MAINTENANCE,    // 유지보수계약
        SUPPLY,         // 공급계약
        DISTRIBUTION,   // 유통계약
        LICENSE,        // 라이선스계약
        PARTNERSHIP,    // 파트너십계약
        SUBSCRIPTION,   // 구독계약
        LEASE,          // 임대계약
        FRAMEWORK       // 기본계약
    }

    /**
     * 갱신 유형 열거형
     */
    public enum RenewalType {
        MANUAL,         // 수동갱신
        AUTOMATIC,      // 자동갱신
        NONE           // 갱신없음
    }

    @NotBlank(message = "계약번호는 필수입니다")
    @Size(max = 50, message = "계약번호는 50자 이내여야 합니다")
    @Column(name = "contract_number", nullable = false, unique = true, length = 50)
    private String contractNumber;

    @NotNull(message = "회사는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_contract_company"))
    private Company company;

    @NotNull(message = "고객은 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_contract_customer"))
    private Customer customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "fk_contract_order"))
    private Order order;

    @NotBlank(message = "계약제목은 필수입니다")
    @Size(max = 200, message = "계약제목은 200자 이내여야 합니다")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Size(max = 1000, message = "계약설명은 1000자 이내여야 합니다")
    @Column(name = "description", length = 1000)
    private String description;

    @NotNull(message = "계약상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "contract_status", nullable = false, length = 20)
    private ContractStatus contractStatus = ContractStatus.DRAFT;

    @NotNull(message = "계약유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false, length = 20)
    private ContractType contractType;

    @NotNull(message = "시작일자는 필수입니다")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "종료일자는 필수입니다")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "signed_date")
    private LocalDate signedDate;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    // 담당자 정보
    @Column(name = "our_representative_id")
    private Long ourRepresentativeId;

    @Size(max = 100, message = "당사 담당자명은 100자 이내여야 합니다")
    @Column(name = "our_representative_name", length = 100)
    private String ourRepresentativeName;

    @Size(max = 100, message = "당사 담당자 부서는 100자 이내여야 합니다")
    @Column(name = "our_representative_department", length = 100)
    private String ourRepresentativeDepartment;

    @Size(max = 100, message = "고객 담당자명은 100자 이내여야 합니다")
    @Column(name = "customer_representative_name", length = 100)
    private String customerRepresentativeName;

    @Size(max = 100, message = "고객 담당자 부서는 100자 이내여야 합니다")
    @Column(name = "customer_representative_department", length = 100)
    private String customerRepresentativeDepartment;

    @Size(max = 20, message = "고객 담당자 연락처는 20자 이내여야 합니다")
    @Column(name = "customer_representative_phone", length = 20)
    private String customerRepresentativePhone;

    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Size(max = 100, message = "고객 담당자 이메일은 100자 이내여야 합니다")
    @Column(name = "customer_representative_email", length = 100)
    private String customerRepresentativeEmail;

    // 계약 금액 정보
    @NotNull(message = "계약금액은 필수입니다")
    @DecimalMin(value = "0", message = "계약금액은 0 이상이어야 합니다")
    @Column(name = "contract_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal contractAmount;

    @DecimalMin(value = "0", message = "세액은 0 이상이어야 합니다")
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "세율은 0 이상이어야 합니다")
    @DecimalMax(value = "100", message = "세율은 100 이하여야 합니다")
    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate = new BigDecimal("10.00");

    @NotNull(message = "총계약금액은 필수입니다")
    @DecimalMin(value = "0", message = "총계약금액은 0 이상이어야 합니다")
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    // 결제 조건
    @Size(max = 100, message = "결제조건은 100자 이내여야 합니다")
    @Column(name = "payment_terms", length = 100)
    private String paymentTerms;

    @Size(max = 100, message = "결제방법은 100자 이내여야 합니다")
    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @Column(name = "payment_cycle_days")
    private Integer paymentCycleDays;

    // 갱신 조건
    @Enumerated(EnumType.STRING)
    @Column(name = "renewal_type", length = 20)
    private RenewalType renewalType = RenewalType.MANUAL;

    @Column(name = "renewal_period_months")
    private Integer renewalPeriodMonths;

    @Column(name = "renewal_notice_days")
    private Integer renewalNoticeDays = 30;

    @Column(name = "auto_renewal_enabled")
    private Boolean autoRenewalEnabled = false;

    // 계약 조건
    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    @Column(name = "special_clauses", columnDefinition = "TEXT")
    private String specialClauses;

    @Column(name = "delivery_terms", length = 500)
    private String deliveryTerms;

    @Column(name = "warranty_terms", length = 500)
    private String warrantyTerms;

    @Column(name = "liability_terms", length = 500)
    private String liabilityTerms;

    @Column(name = "termination_terms", length = 500)
    private String terminationTerms;

    // 상태 관리
    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Column(name = "activated_date")
    private LocalDateTime activatedDate;

    @Column(name = "suspended_date")
    private LocalDateTime suspendedDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "terminated_date")
    private LocalDateTime terminatedDate;

    @Size(max = 500, message = "해지사유는 500자 이내여야 합니다")
    @Column(name = "termination_reason", length = 500)
    private String terminationReason;

    // 첨부 파일
    @Size(max = 1000, message = "첨부파일 경로는 1000자 이내여야 합니다")
    @Column(name = "attachment_paths", length = 1000)
    private String attachmentPaths;

    // 추가 정보
    @Size(max = 1000, message = "비고는 1000자 이내여야 합니다")
    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Size(max = 500, message = "태그는 500자 이내여야 합니다")
    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    // 비즈니스 메서드
    /**
     * 계약 상태 설명 반환
     */
    public String getContractStatusDescription() {
        return switch (contractStatus) {
            case DRAFT -> "임시저장";
            case PENDING -> "검토중";
            case APPROVED -> "승인됨";
            case ACTIVE -> "활성";
            case SUSPENDED -> "중단";
            case COMPLETED -> "완료";
            case TERMINATED -> "해지";
            case EXPIRED -> "만료";
            case CANCELLED -> "취소";
        };
    }

    /**
     * 계약 유형 설명 반환
     */
    public String getContractTypeDescription() {
        return switch (contractType) {
            case SALES -> "판매계약";
            case SERVICE -> "서비스계약";
            case MAINTENANCE -> "유지보수계약";
            case SUPPLY -> "공급계약";
            case DISTRIBUTION -> "유통계약";
            case LICENSE -> "라이선스계약";
            case PARTNERSHIP -> "파트너십계약";
            case SUBSCRIPTION -> "구독계약";
            case LEASE -> "임대계약";
            case FRAMEWORK -> "기본계약";
        };
    }

    /**
     * 갱신 유형 설명 반환
     */
    public String getRenewalTypeDescription() {
        return switch (renewalType) {
            case MANUAL -> "수동갱신";
            case AUTOMATIC -> "자동갱신";
            case NONE -> "갱신없음";
        };
    }

    /**
     * 계약 기간 (일수) 반환
     */
    public long getContractDurationDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 계약 만료까지 남은 일수 반환
     */
    public long getDaysUntilExpiry() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    /**
     * 계약 만료 여부 확인
     */
    public boolean isExpired() {
        return endDate.isBefore(LocalDate.now());
    }

    /**
     * 계약 활성 여부 확인
     */
    public boolean isActive() {
        return contractStatus == ContractStatus.ACTIVE && 
               !isExpired() && 
               (effectiveDate == null || !effectiveDate.isAfter(LocalDate.now()));
    }

    /**
     * 계약 만료 임박 여부 확인 (30일 이내)
     */
    public boolean isExpiringSoon() {
        return getDaysUntilExpiry() <= 30 && getDaysUntilExpiry() > 0;
    }

    /**
     * 갱신 알림 필요 여부 확인
     */
    public boolean needsRenewalNotice() {
        if (renewalType == RenewalType.NONE) return false;
        if (renewalNoticeDays == null) return false;
        
        return getDaysUntilExpiry() <= renewalNoticeDays && getDaysUntilExpiry() > 0;
    }

    /**
     * 계약 승인 처리
     */
    public void approve() {
        if (contractStatus == ContractStatus.PENDING) {
            this.contractStatus = ContractStatus.APPROVED;
            this.approvedDate = LocalDateTime.now();
        } else {
            throw new IllegalStateException("승인할 수 없는 계약 상태입니다");
        }
    }

    /**
     * 계약 활성화 처리
     */
    public void activate() {
        if (contractStatus == ContractStatus.APPROVED) {
            this.contractStatus = ContractStatus.ACTIVE;
            this.activatedDate = LocalDateTime.now();
            if (effectiveDate == null) {
                this.effectiveDate = LocalDate.now();
            }
        } else {
            throw new IllegalStateException("활성화할 수 없는 계약 상태입니다");
        }
    }

    /**
     * 계약 중단 처리
     */
    public void suspend(String reason) {
        if (contractStatus == ContractStatus.ACTIVE) {
            this.contractStatus = ContractStatus.SUSPENDED;
            this.suspendedDate = LocalDateTime.now();
            this.remarks = (remarks != null ? remarks + "\n" : "") + "중단사유: " + reason;
        } else {
            throw new IllegalStateException("중단할 수 없는 계약 상태입니다");
        }
    }

    /**
     * 계약 재개 처리
     */
    public void resume() {
        if (contractStatus == ContractStatus.SUSPENDED && !isExpired()) {
            this.contractStatus = ContractStatus.ACTIVE;
            this.suspendedDate = null;
        } else {
            throw new IllegalStateException("재개할 수 없는 계약 상태입니다");
        }
    }

    /**
     * 계약 완료 처리
     */
    public void complete() {
        if (contractStatus == ContractStatus.ACTIVE) {
            this.contractStatus = ContractStatus.COMPLETED;
            this.completedDate = LocalDateTime.now();
        } else {
            throw new IllegalStateException("완료할 수 없는 계약 상태입니다");
        }
    }

    /**
     * 계약 해지 처리
     */
    public void terminate(String reason) {
        if (contractStatus == ContractStatus.ACTIVE || contractStatus == ContractStatus.SUSPENDED) {
            this.contractStatus = ContractStatus.TERMINATED;
            this.terminatedDate = LocalDateTime.now();
            this.terminationReason = reason;
        } else {
            throw new IllegalStateException("해지할 수 없는 계약 상태입니다");
        }
    }

    /**
     * 계약 갱신 처리
     */
    public Contract renew(LocalDate newEndDate) {
        if (!isActive() && !isExpired()) {
            throw new IllegalStateException("갱신할 수 없는 계약 상태입니다");
        }

        Contract renewedContract = new Contract();
        renewedContract.setCompany(this.company);
        renewedContract.setCustomer(this.customer);
        renewedContract.setTitle(this.title + " (갱신)");
        renewedContract.setDescription(this.description);
        renewedContract.setContractType(this.contractType);
        renewedContract.setStartDate(this.endDate.plusDays(1));
        renewedContract.setEndDate(newEndDate);
        renewedContract.setContractAmount(this.contractAmount);
        renewedContract.setTaxRate(this.taxRate);
        renewedContract.setPaymentTerms(this.paymentTerms);
        renewedContract.setPaymentMethod(this.paymentMethod);
        renewedContract.setRenewalType(this.renewalType);
        renewedContract.setRenewalPeriodMonths(this.renewalPeriodMonths);
        renewedContract.setTermsAndConditions(this.termsAndConditions);
        
        // 기존 계약 완료 처리
        this.complete();
        
        return renewedContract;
    }

    /**
     * 총 계약 금액 계산
     */
    public void calculateTotalAmount() {
        if (contractAmount == null) {
            totalAmount = BigDecimal.ZERO;
            return;
        }

        // 세액 계산
        if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
            taxAmount = contractAmount.multiply(taxRate).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        } else {
            taxAmount = BigDecimal.ZERO;
        }

        // 총액 계산
        totalAmount = contractAmount.add(taxAmount);
    }

    /**
     * 계약 요약 정보 반환
     */
    public String getContractSummary() {
        return String.format("%s - %s (%s)", 
                contractNumber, 
                customer != null ? customer.getCustomerName() : "Unknown",
                getContractStatusDescription());
    }

    /**
     * 계약 진행률 반환 (0-100%)
     */
    public double getProgress() {
        LocalDate now = LocalDate.now();
        
        if (now.isBefore(startDate)) {
            return 0.0;
        } else if (now.isAfter(endDate)) {
            return 100.0;
        } else {
            long totalDays = getContractDurationDays();
            long elapsedDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, now);
            return (double) elapsedDays / totalDays * 100.0;
        }
    }

    // Setter 오버라이드 (자동 계산 포함)
    public void setContractAmount(BigDecimal contractAmount) {
        this.contractAmount = contractAmount;
        calculateTotalAmount();
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
        calculateTotalAmount();
    }
}
