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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 엔티티
 * 고객의 주문 정보를 관리합니다
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_company", columnList = "company_id"),
    @Index(name = "idx_order_customer", columnList = "customer_id"),
    @Index(name = "idx_order_number", columnList = "order_number"),
    @Index(name = "idx_order_status", columnList = "order_status"),
    @Index(name = "idx_order_date", columnList = "order_date"),
    @Index(name = "idx_order_delivery_date", columnList = "delivery_date"),
    @Index(name = "idx_order_sales_rep", columnList = "sales_rep_id"),
    @Index(name = "idx_order_quote", columnList = "quote_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Order extends BaseEntity {

    /**
     * 주문 상태 열거형
     */
    public enum OrderStatus {
        DRAFT,          // 임시저장
        PENDING,        // 대기중
        CONFIRMED,      // 확정됨
        PROCESSING,     // 처리중
        SHIPPED,        // 배송중
        DELIVERED,      // 배송완료
        COMPLETED,      // 완료
        CANCELLED,      // 취소됨
        REFUNDED,       // 환불됨
        RETURNED        // 반품됨
    }

    /**
     * 주문 유형 열거형
     */
    public enum OrderType {
        NORMAL,         // 일반주문
        RUSH,           // 긴급주문
        BACKORDER,      // 백오더
        PREORDER,       // 선주문
        SUBSCRIPTION,   // 정기주문
        SAMPLE,         // 샘플주문
        RETURN,         // 반품주문
        EXCHANGE        // 교환주문
    }

    /**
     * 결제 상태 열거형
     */
    public enum PaymentStatus {
        PENDING,        // 결제대기
        PARTIAL,        // 부분결제
        PAID,           // 결제완료
        OVERDUE,        // 연체
        CANCELLED,      // 결제취소
        REFUNDED        // 환불완료
    }

    @NotBlank(message = "주문번호는 필수입니다")
    @Size(max = 50, message = "주문번호는 50자 이내여야 합니다")
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @NotNull(message = "회사는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_company"))
    private Company company;

    @NotNull(message = "고객은 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_customer"))
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", foreignKey = @ForeignKey(name = "fk_order_quote"))
    private Quote quote;

    @NotNull(message = "주문일자는 필수입니다")
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "required_date")
    private LocalDate requiredDate;

    @Column(name = "promised_date")
    private LocalDate promisedDate;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @NotNull(message = "주문상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    private OrderStatus orderStatus = OrderStatus.DRAFT;

    @NotNull(message = "주문유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 20)
    private OrderType orderType = OrderType.NORMAL;

    @NotNull(message = "결제상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

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

    @DecimalMin(value = "0", message = "결제금액은 0 이상이어야 합니다")
    @Column(name = "paid_amount", precision = 15, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "미수금액은 0 이상이어야 합니다")
    @Column(name = "outstanding_amount", precision = 15, scale = 2)
    private BigDecimal outstandingAmount = BigDecimal.ZERO;

    // 배송 정보
    @Size(max = 100, message = "배송지명은 100자 이내여야 합니다")
    @Column(name = "delivery_name", length = 100)
    private String deliveryName;

    @Size(max = 20, message = "배송지 연락처는 20자 이내여야 합니다")
    @Column(name = "delivery_phone", length = 20)
    private String deliveryPhone;

    @Size(max = 10, message = "배송지 우편번호는 10자 이내여야 합니다")
    @Column(name = "delivery_postal_code", length = 10)
    private String deliveryPostalCode;

    @Size(max = 200, message = "배송지 주소는 200자 이내여야 합니다")
    @Column(name = "delivery_address", length = 200)
    private String deliveryAddress;

    @Size(max = 200, message = "배송지 상세주소는 200자 이내여야 합니다")
    @Column(name = "delivery_address_detail", length = 200)
    private String deliveryAddressDetail;

    @Size(max = 100, message = "배송방법은 100자 이내여야 합니다")
    @Column(name = "delivery_method", length = 100)
    private String deliveryMethod;

    @DecimalMin(value = "0", message = "배송비는 0 이상이어야 합니다")
    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Size(max = 500, message = "배송메모는 500자 이내여야 합니다")
    @Column(name = "delivery_memo", length = 500)
    private String deliveryMemo;

    @Size(max = 100, message = "택배사는 100자 이내여야 합니다")
    @Column(name = "courier_company", length = 100)
    private String courierCompany;

    @Size(max = 50, message = "송장번호는 50자 이내여야 합니다")
    @Column(name = "tracking_number", length = 50)
    private String trackingNumber;

    // 결제 정보
    @Size(max = 100, message = "결제조건은 100자 이내여야 합니다")
    @Column(name = "payment_terms", length = 100)
    private String paymentTerms;

    @Size(max = 100, message = "결제방법은 100자 이내여야 합니다")
    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @Column(name = "payment_due_date")
    private LocalDate paymentDueDate;

    @Column(name = "payment_completed_date")
    private LocalDateTime paymentCompletedDate;

    // 상태 관리
    @Column(name = "confirmed_date")
    private LocalDateTime confirmedDate;

    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

    @Size(max = 500, message = "취소사유는 500자 이내여야 합니다")
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    // 특별 조건
    @Size(max = 1000, message = "특별조건은 1000자 이내여야 합니다")
    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;

    @Size(max = 1000, message = "비고는 1000자 이내여야 합니다")
    @Column(name = "remarks", length = 1000)
    private String remarks;

    // 추가 정보
    @Size(max = 500, message = "태그는 500자 이내여야 합니다")
    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    // 연관관계
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Contract contract;

    // 비즈니스 메서드
    /**
     * 주문 상태 설명 반환
     */
    public String getOrderStatusDescription() {
        return switch (orderStatus) {
            case DRAFT -> "임시저장";
            case PENDING -> "대기중";
            case CONFIRMED -> "확정됨";
            case PROCESSING -> "처리중";
            case SHIPPED -> "배송중";
            case DELIVERED -> "배송완료";
            case COMPLETED -> "완료";
            case CANCELLED -> "취소됨";
            case REFUNDED -> "환불됨";
            case RETURNED -> "반품됨";
        };
    }

    /**
     * 주문 유형 설명 반환
     */
    public String getOrderTypeDescription() {
        return switch (orderType) {
            case NORMAL -> "일반주문";
            case RUSH -> "긴급주문";
            case BACKORDER -> "백오더";
            case PREORDER -> "선주문";
            case SUBSCRIPTION -> "정기주문";
            case SAMPLE -> "샘플주문";
            case RETURN -> "반품주문";
            case EXCHANGE -> "교환주문";
        };
    }

    /**
     * 결제 상태 설명 반환
     */
    public String getPaymentStatusDescription() {
        return switch (paymentStatus) {
            case PENDING -> "결제대기";
            case PARTIAL -> "부분결제";
            case PAID -> "결제완료";
            case OVERDUE -> "연체";
            case CANCELLED -> "결제취소";
            case REFUNDED -> "환불완료";
        };
    }

    /**
     * 전체 배송 주소 반환
     */
    public String getFullDeliveryAddress() {
        StringBuilder sb = new StringBuilder();
        if (deliveryPostalCode != null && !deliveryPostalCode.trim().isEmpty()) {
            sb.append("(").append(deliveryPostalCode).append(") ");
        }
        if (deliveryAddress != null && !deliveryAddress.trim().isEmpty()) {
            sb.append(deliveryAddress);
        }
        if (deliveryAddressDetail != null && !deliveryAddressDetail.trim().isEmpty()) {
            sb.append(" ").append(deliveryAddressDetail);
        }
        return sb.toString().trim();
    }

    /**
     * 주문 수정 가능 여부 확인
     */
    public boolean isEditable() {
        return orderStatus == OrderStatus.DRAFT || orderStatus == OrderStatus.PENDING;
    }

    /**
     * 주문 확정 가능 여부 확인
     */
    public boolean isConfirmable() {
        return orderStatus == OrderStatus.PENDING && !orderItems.isEmpty();
    }

    /**
     * 주문 취소 가능 여부 확인
     */
    public boolean isCancellable() {
        return orderStatus != OrderStatus.COMPLETED && 
               orderStatus != OrderStatus.CANCELLED && 
               orderStatus != OrderStatus.REFUNDED;
    }

    /**
     * 배송 처리 가능 여부 확인
     */
    public boolean isShippable() {
        return orderStatus == OrderStatus.CONFIRMED || orderStatus == OrderStatus.PROCESSING;
    }

    /**
     * 주문 항목 추가
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        recalculateAmounts();
    }

    /**
     * 주문 항목 제거
     */
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
        recalculateAmounts();
    }

    /**
     * 금액 재계산
     */
    public void recalculateAmounts() {
        // 소계 계산
        subtotal = orderItems.stream()
                .map(OrderItem::getTotalPrice)
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

        // 미수금액 계산
        outstandingAmount = totalAmount.subtract(paidAmount != null ? paidAmount : BigDecimal.ZERO);

        // 결제상태 업데이트
        updatePaymentStatus();
    }

    /**
     * 결제상태 업데이트
     */
    private void updatePaymentStatus() {
        if (paidAmount == null || paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            paymentStatus = PaymentStatus.PENDING;
        } else if (paidAmount.compareTo(totalAmount) >= 0) {
            paymentStatus = PaymentStatus.PAID;
        } else {
            paymentStatus = PaymentStatus.PARTIAL;
        }

        // 연체 확인
        if (paymentStatus != PaymentStatus.PAID && paymentDueDate != null && 
            paymentDueDate.isBefore(LocalDate.now())) {
            paymentStatus = PaymentStatus.OVERDUE;
        }
    }

    /**
     * 주문 확정 처리
     */
    public void confirm() {
        if (isConfirmable()) {
            this.orderStatus = OrderStatus.CONFIRMED;
            this.confirmedDate = LocalDateTime.now();
        } else {
            throw new IllegalStateException("주문을 확정할 수 없습니다");
        }
    }

    /**
     * 배송 처리
     */
    public void ship(String courierCompany, String trackingNumber) {
        if (isShippable()) {
            this.orderStatus = OrderStatus.SHIPPED;
            this.courierCompany = courierCompany;
            this.trackingNumber = trackingNumber;
            this.shippedDate = LocalDateTime.now();
        } else {
            throw new IllegalStateException("주문을 배송 처리할 수 없습니다");
        }
    }

    /**
     * 배송완료 처리
     */
    public void markAsDelivered() {
        if (orderStatus == OrderStatus.SHIPPED) {
            this.orderStatus = OrderStatus.DELIVERED;
            this.deliveredDate = LocalDateTime.now();
            this.deliveryDate = LocalDate.now();
        } else {
            throw new IllegalStateException("배송완료 처리할 수 없습니다");
        }
    }

    /**
     * 주문 완료 처리
     */
    public void complete() {
        if (orderStatus == OrderStatus.DELIVERED) {
            this.orderStatus = OrderStatus.COMPLETED;
            this.completedDate = LocalDateTime.now();
        } else {
            throw new IllegalStateException("주문을 완료 처리할 수 없습니다");
        }
    }

    /**
     * 주문 취소 처리
     */
    public void cancel(String reason) {
        if (isCancellable()) {
            this.orderStatus = OrderStatus.CANCELLED;
            this.cancelledDate = LocalDateTime.now();
            this.cancellationReason = reason;
        } else {
            throw new IllegalStateException("주문을 취소할 수 없습니다");
        }
    }

    /**
     * 결제 처리
     */
    public void processPayment(BigDecimal paymentAmount) {
        if (paidAmount == null) paidAmount = BigDecimal.ZERO;
        paidAmount = paidAmount.add(paymentAmount);
        
        if (paidAmount.compareTo(totalAmount) >= 0) {
            paymentCompletedDate = LocalDateTime.now();
        }
        
        recalculateAmounts();
    }

    /**
     * 견적서로부터 주문 생성
     */
    public static Order fromQuote(Quote quote) {
        Order order = new Order();
        order.setCompany(quote.getCompany());
        order.setCustomer(quote.getCustomer());
        order.setQuote(quote);
        order.setOrderDate(LocalDate.now());
        order.setTitle(quote.getTitle());
        order.setDescription(quote.getDescription());
        order.setSalesRepId(quote.getSalesRepId());
        order.setSalesRepName(quote.getSalesRepName());
        order.setDeliveryAddress(quote.getDeliveryAddress());
        order.setDeliveryDate(quote.getDeliveryDate());
        order.setDeliveryMethod(quote.getDeliveryMethod());
        order.setDeliveryFee(quote.getDeliveryFee());
        order.setPaymentTerms(quote.getPaymentTerms());
        order.setPaymentMethod(quote.getPaymentMethod());
        order.setSpecialInstructions(quote.getSpecialTerms());
        order.setRemarks(quote.getRemarks());
        
        // 견적 항목을 주문 항목으로 변환
        for (QuoteItem quoteItem : quote.getQuoteItems()) {
            OrderItem orderItem = OrderItem.fromQuoteItem(quoteItem);
            order.addOrderItem(orderItem);
        }
        
        return order;
    }

    /**
     * 주문 요약 정보 반환
     */
    public String getOrderSummary() {
        return String.format("%s - %s (%s)", 
                orderNumber, 
                customer != null ? customer.getCustomerName() : "Unknown",
                getOrderStatusDescription());
    }

    /**
     * 총 항목 수 반환
     */
    public int getTotalItemCount() {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    /**
     * 배송 예정일까지 남은 일수 반환
     */
    public long getDaysUntilDelivery() {
        if (requiredDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), requiredDate);
    }
}
