package com.erp.sales.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import com.erp.sales.entity.Customer;
import com.erp.sales.entity.Quote;
import com.erp.sales.entity.OrderItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 엔티티
 * 실제 DB 스키마와 완전히 일치하도록 수정됨
 */
@Entity
@Table(name = "orders")
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {

    /**
     * 주문 상태 열거형
     */
    public enum OrderStatus {
        DRAFT("임시저장"),
        PENDING("주문대기"),
        CONFIRMED("주문확정"),
        PROCESSING("처리중"),
        SHIPPED("배송중"),
        DELIVERED("배송완료"),
        CANCELLED("주문취소"),
        RETURNED("반품");

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 결제 상태 열거형
     */
    public enum PaymentStatus {
        PENDING("결제대기"),
        PARTIAL("부분결제"),
        PAID("결제완료"),
        OVERDUE("연체"),
        CANCELLED("결제취소"),
        REFUNDED("환불완료");

        private final String description;

        PaymentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
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

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @NotNull(message = "주문상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    private OrderStatus orderStatus = OrderStatus.DRAFT;

    @NotNull(message = "결제상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @NotNull(message = "총금액은 필수입니다")
    @DecimalMin(value = "0", message = "총금액은 0 이상이어야 합니다")
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    // 주문 아이템들
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * 주문 생성자
     */
    public Order() {
        super();
    }

    /**
     * 주문 생성자
     */
    public Order(String orderNumber, Company company, Customer customer, LocalDate orderDate, BigDecimal totalAmount) {
        super();
        this.orderNumber = orderNumber;
        this.company = company;
        this.customer = customer;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
    }

    /**
     * 견적으로부터 주문 생성
     */
    public static Order fromQuote(Quote quote) {
        Order order = new Order();
        order.setCompany(quote.getCompany());
        order.setCustomer(quote.getCustomer());
        order.setQuote(quote);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(quote.getTotalAmount());
        return order;
    }

    /**
     * 주문 상태 변경
     */
    public void updateStatus(OrderStatus newStatus) {
        this.orderStatus = newStatus;
    }

    /**
     * 결제 상태 변경
     */
    public void updatePaymentStatus(PaymentStatus newStatus) {
        this.paymentStatus = newStatus;
    }

    /**
     * 주문 아이템 추가
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * 주문 아이템 제거
     */
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    /**
     * 주문이 편집 가능한지 확인
     */
    public boolean isEditable() {
        return orderStatus == OrderStatus.DRAFT || orderStatus == OrderStatus.PENDING;
    }

    /**
     * 주문이 확정 가능한지 확인
     */
    public boolean isConfirmable() {
        return orderStatus == OrderStatus.DRAFT || orderStatus == OrderStatus.PENDING;
    }

    /**
     * 주문이 취소 가능한지 확인
     */
    public boolean isCancellable() {
        return orderStatus != OrderStatus.CANCELLED && orderStatus != OrderStatus.DELIVERED;
    }

    /**
     * 주문이 배송 가능한지 확인
     */
    public boolean isShippable() {
        return orderStatus == OrderStatus.CONFIRMED || orderStatus == OrderStatus.PROCESSING;
    }

    /**
     * 주문 요약 정보
     */
    public String getOrderSummary() {
        return String.format("주문번호: %s, 고객: %s, 금액: %s", 
                orderNumber, 
                customer != null ? customer.getCustomerName() : "미지정", 
                totalAmount);
    }

    /**
     * 총 아이템 수
     */
    public int getTotalItemCount() {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    /**
     * 배송까지 남은 일수
     */
    public long getDaysUntilDelivery() {
        if (deliveryDate == null) {
            return -1;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), deliveryDate);
    }
}