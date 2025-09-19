package com.erp.sales.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 견적 엔티티
 * 고객에게 제공하는 견적서 정보를 관리합니다
 */
@Entity
@Table(name = "quotes", indexes = {
    @Index(name = "idx_quote_company", columnList = "company_id"),
    @Index(name = "idx_quote_customer", columnList = "customer_id"),
    @Index(name = "idx_quote_number", columnList = "quote_number"),
    @Index(name = "idx_quote_status", columnList = "quote_status"),
    @Index(name = "idx_quote_date", columnList = "quote_date"),
    @Index(name = "idx_quote_valid_until", columnList = "valid_until"),
    @Index(name = "idx_quote_sales_rep", columnList = "sales_rep_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Quote extends BaseEntity {

    /**
     * 견적 상태 열거형
     */
    public enum QuoteStatus {
        DRAFT,          // 임시저장
        SENT,           // 발송됨
        VIEWED,         // 확인됨
        ACCEPTED,       // 승인됨
        REJECTED,       // 거부됨
        EXPIRED,        // 만료됨
        CONVERTED,      // 주문전환
        CANCELLED       // 취소됨
    }

    /**
     * 견적 우선순위 열거형
     */
    public enum QuotePriority {
        LOW,            // 낮음
        NORMAL,         // 보통
        HIGH,           // 높음
        URGENT          // 긴급
    }

    @NotBlank(message = "견적번호는 필수입니다")
    @Size(max = 50, message = "견적번호는 50자 이내여야 합니다")
    @Column(name = "quote_number", nullable = false, unique = true, length = 50)
    private String quoteNumber;

    @NotNull(message = "회사는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_quote_company"))
    private Company company;

    @NotNull(message = "고객은 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_quote_customer"))
    private Customer customer;

    @NotNull(message = "견적일자는 필수입니다")
    @Column(name = "quote_date", nullable = false)
    private LocalDate quoteDate;

    @NotNull(message = "유효기한은 필수입니다")
    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @NotNull(message = "견적상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "quote_status", nullable = false, length = 20)
    private QuoteStatus quoteStatus = QuoteStatus.DRAFT;

    @NotNull(message = "우선순위는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private QuotePriority priority = QuotePriority.NORMAL;

    @Size(max = 200, message = "제목은 200자 이내여야 합니다")
    @Column(name = "title", length = 200)
    private String title;

    @Size(max = 1000, message = "설명은 1000자 이내여야 합니다")
    @Column(name = "description", length = 1000)
    private String description;

    // 영업 담당자 정보
    @Column(name = "sales_rep_id")
    private Long salesRepId;

    @Size(max = 100, message = "영업담당자명은 100자 이내여야 합니다")
    @Column(name = "sales_rep_name", length = 100)
    private String salesRepName;

    @Size(max = 20, message = "영업담당자 연락처는 20자 이내여야 합니다")
    @Column(name = "sales_rep_phone", length = 20)
    private String salesRepPhone;

    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Size(max = 100, message = "영업담당자 이메일은 100자 이내여야 합니다")
    @Column(name = "sales_rep_email", length = 100)
    private String salesRepEmail;

    // 금액 정보
    @NotNull(message = "소계는 필수입니다")
    @DecimalMin(value = "0", message = "소계는 0 이상이어야 합니다")
    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "할인금액은 0 이상이어야 합니다")
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "할인율은 0 이상이어야 합니다")
    @DecimalMax(value = "100", message = "할인율은 100 이하여야 합니다")
    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "세액은 0 이상이어야 합니다")
    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "세율은 0 이상이어야 합니다")
    @DecimalMax(value = "100", message = "세율은 100 이하여야 합니다")
    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate = new BigDecimal("10.00");

    @NotNull(message = "총액은 필수입니다")
    @DecimalMin(value = "0", message = "총액은 0 이상이어야 합니다")
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // 배송 정보
    @Size(max = 200, message = "배송주소는 200자 이내여야 합니다")
    @Column(name = "delivery_address", length = 200)
    private String deliveryAddress;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Size(max = 100, message = "배송방법은 100자 이내여야 합니다")
    @Column(name = "delivery_method", length = 100)
    private String deliveryMethod;

    @DecimalMin(value = "0", message = "배송비는 0 이상이어야 합니다")
    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    // 결제 정보
    @Size(max = 100, message = "결제조건은 100자 이내여야 합니다")
    @Column(name = "payment_terms", length = 100)
    private String paymentTerms;

    @Size(max = 100, message = "결제방법은 100자 이내여야 합니다")
    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    // 특별 조건
    @Size(max = 1000, message = "특별조건은 1000자 이내여야 합니다")
    @Column(name = "special_terms", length = 1000)
    private String specialTerms;

    @Size(max = 1000, message = "비고는 1000자 이내여야 합니다")
    @Column(name = "remarks", length = 1000)
    private String remarks;

    // 상태 관리
    @Column(name = "sent_date")
    private LocalDate sentDate;

    @Column(name = "viewed_date")
    private LocalDate viewedDate;

    @Column(name = "responded_date")
    private LocalDate respondedDate;

    @Column(name = "converted_date")
    private LocalDate convertedDate;

    @Size(max = 500, message = "거부사유는 500자 이내여야 합니다")
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    // 추가 정보
    @Size(max = 500, message = "태그는 500자 이내여야 합니다")
    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    // 연관관계
    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<QuoteItem> quoteItems = new ArrayList<>();


    // 비즈니스 메서드
    /**
     * 견적 상태 설명 반환
     */
    public String getQuoteStatusDescription() {
        return switch (quoteStatus) {
            case DRAFT -> "임시저장";
            case SENT -> "발송됨";
            case VIEWED -> "확인됨";
            case ACCEPTED -> "승인됨";
            case REJECTED -> "거부됨";
            case EXPIRED -> "만료됨";
            case CONVERTED -> "주문전환";
            case CANCELLED -> "취소됨";
        };
    }

    /**
     * 우선순위 설명 반환
     */
    public String getPriorityDescription() {
        return switch (priority) {
            case LOW -> "낮음";
            case NORMAL -> "보통";
            case HIGH -> "높음";
            case URGENT -> "긴급";
        };
    }

    /**
     * 견적서 만료 여부 확인
     */
    public boolean isExpired() {
        return validUntil != null && validUntil.isBefore(LocalDate.now());
    }

    /**
     * 견적서 수정 가능 여부 확인
     */
    public boolean isEditable() {
        return quoteStatus == QuoteStatus.DRAFT;
    }

    /**
     * 견적서 발송 가능 여부 확인
     */
    public boolean isSendable() {
        return quoteStatus == QuoteStatus.DRAFT && !quoteItems.isEmpty();
    }

    /**
     * 주문 전환 가능 여부 확인
     */
    public boolean isConvertible() {
        return quoteStatus == QuoteStatus.ACCEPTED && !isExpired();
    }

    /**
     * 견적 항목 추가
     */
    public void addQuoteItem(QuoteItem quoteItem) {
        quoteItems.add(quoteItem);
        quoteItem.setQuote(this);
        recalculateAmounts();
    }

    /**
     * 견적 항목 제거
     */
    public void removeQuoteItem(QuoteItem quoteItem) {
        quoteItems.remove(quoteItem);
        quoteItem.setQuote(null);
        recalculateAmounts();
    }

    /**
     * 금액 재계산
     */
    public void recalculateAmounts() {
        // 소계 계산
        subtotal = quoteItems.stream()
                .map(QuoteItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 할인 적용
        BigDecimal discountedAmount = subtotal;
        if (discountRate != null && discountRate.compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = subtotal.multiply(discountRate).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
            discountedAmount = subtotal.subtract(discountAmount);
        } else if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            discountedAmount = subtotal.subtract(discountAmount);
            discountRate = discountAmount.multiply(new BigDecimal("100"))
                    .divide(subtotal, 2, java.math.RoundingMode.HALF_UP);
        }

        // 세액 계산
        if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
            taxAmount = discountedAmount.multiply(taxRate).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        }

        // 총액 계산 (할인된 금액 + 세액 + 배송비)
        totalAmount = discountedAmount.add(taxAmount != null ? taxAmount : BigDecimal.ZERO)
                .add(deliveryFee != null ? deliveryFee : BigDecimal.ZERO);
    }

    /**
     * 견적서 발송 처리
     */
    public void markAsSent() {
        if (isSendable()) {
            this.quoteStatus = QuoteStatus.SENT;
            this.sentDate = LocalDate.now();
        } else {
            throw new IllegalStateException("견적서를 발송할 수 없습니다");
        }
    }

    /**
     * 견적서 확인 처리
     */
    public void markAsViewed() {
        if (quoteStatus == QuoteStatus.SENT) {
            this.quoteStatus = QuoteStatus.VIEWED;
            this.viewedDate = LocalDate.now();
        }
    }

    /**
     * 견적서 승인 처리
     */
    public void markAsAccepted() {
        if (quoteStatus == QuoteStatus.SENT || quoteStatus == QuoteStatus.VIEWED) {
            this.quoteStatus = QuoteStatus.ACCEPTED;
            this.respondedDate = LocalDate.now();
        } else {
            throw new IllegalStateException("견적서를 승인할 수 없습니다");
        }
    }

    /**
     * 견적서 거부 처리
     */
    public void markAsRejected(String reason) {
        if (quoteStatus == QuoteStatus.SENT || quoteStatus == QuoteStatus.VIEWED) {
            this.quoteStatus = QuoteStatus.REJECTED;
            this.respondedDate = LocalDate.now();
            this.rejectionReason = reason;
        } else {
            throw new IllegalStateException("견적서를 거부할 수 없습니다");
        }
    }

    /**
     * 주문 전환 처리
     */
    public void markAsConverted() {
        if (isConvertible()) {
            this.quoteStatus = QuoteStatus.CONVERTED;
            this.convertedDate = LocalDate.now();
        } else {
            throw new IllegalStateException("견적서를 주문으로 전환할 수 없습니다");
        }
    }

    /**
     * 견적서 요약 정보 반환
     */
    public String getQuoteSummary() {
        return String.format("%s - %s (%s)", 
                quoteNumber, 
                customer != null ? customer.getCustomerName() : "Unknown",
                getQuoteStatusDescription());
    }

    /**
     * 총 항목 수 반환
     */
    public int getTotalItemCount() {
        return quoteItems.stream()
                .mapToInt(QuoteItem::getQuantity)
                .sum();
    }

    /**
     * 견적 유효 일수 반환
     */
    public long getDaysUntilExpiry() {
        if (validUntil == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), validUntil);
    }
}
