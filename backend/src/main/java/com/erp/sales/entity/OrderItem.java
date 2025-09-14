package com.erp.sales.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.inventory.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 주문 항목 엔티티
 * 주문의 개별 상품 정보를 관리합니다
 */
@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_item_order", columnList = "order_id"),
    @Index(name = "idx_order_item_product", columnList = "product_id"),
    @Index(name = "idx_order_item_line_number", columnList = "line_number")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    /**
     * 배송 상태 열거형
     */
    public enum DeliveryStatus {
        PENDING,        // 배송대기
        PREPARING,      // 준비중
        SHIPPED,        // 배송중
        DELIVERED,      // 배송완료
        CANCELLED       // 취소됨
    }

    @NotNull(message = "주문은 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_item_order"))
    private Order order;

    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false, 
                foreignKey = @ForeignKey(name = "fk_order_item_product"))
    private Product product;

    @NotNull(message = "라인번호는 필수입니다")
    @Min(value = 1, message = "라인번호는 1 이상이어야 합니다")
    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @NotBlank(message = "상품코드는 필수입니다")
    @Size(max = 50, message = "상품코드는 50자 이내여야 합니다")
    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    @NotBlank(message = "상품명은 필수입니다")
    @Size(max = 200, message = "상품명은 200자 이내여야 합니다")
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Size(max = 1000, message = "상품설명은 1000자 이내여야 합니다")
    @Column(name = "product_description", length = 1000)
    private String productDescription;

    @NotNull(message = "주문수량은 필수입니다")
    @Min(value = 1, message = "주문수량은 1 이상이어야 합니다")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "shipped_quantity")
    private Integer shippedQuantity = 0;

    @Column(name = "delivered_quantity")
    private Integer deliveredQuantity = 0;

    @Column(name = "cancelled_quantity")
    private Integer cancelledQuantity = 0;

    @NotBlank(message = "단위는 필수입니다")
    @Size(max = 20, message = "단위는 20자 이내여야 합니다")
    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @NotNull(message = "단가는 필수입니다")
    @DecimalMin(value = "0", message = "단가는 0 이상이어야 합니다")
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @DecimalMin(value = "0", message = "할인율은 0 이상이어야 합니다")
    @DecimalMax(value = "100", message = "할인율은 100 이하여야 합니다")
    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "할인금액은 0 이상이어야 합니다")
    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull(message = "총가격은 필수입니다")
    @DecimalMin(value = "0", message = "총가격은 0 이상이어야 합니다")
    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", length = 20)
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    @Size(max = 500, message = "비고는 500자 이내여야 합니다")
    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    // 비즈니스 메서드
    /**
     * 배송 상태 설명 반환
     */
    public String getDeliveryStatusDescription() {
        return switch (deliveryStatus) {
            case PENDING -> "배송대기";
            case PREPARING -> "준비중";
            case SHIPPED -> "배송중";
            case DELIVERED -> "배송완료";
            case CANCELLED -> "취소됨";
        };
    }

    /**
     * 총가격 계산
     */
    public void calculateTotalPrice() {
        if (quantity == null || unitPrice == null) {
            totalPrice = BigDecimal.ZERO;
            return;
        }

        BigDecimal lineTotal = unitPrice.multiply(new BigDecimal(quantity));
        
        // 할인 적용
        if (discountRate != null && discountRate.compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = lineTotal.multiply(discountRate).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
            totalPrice = lineTotal.subtract(discountAmount);
        } else if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            totalPrice = lineTotal.subtract(discountAmount);
            // 할인율 역계산
            if (lineTotal.compareTo(BigDecimal.ZERO) > 0) {
                discountRate = discountAmount.multiply(new BigDecimal("100"))
                        .divide(lineTotal, 2, java.math.RoundingMode.HALF_UP);
            }
        } else {
            totalPrice = lineTotal;
        }
    }

    /**
     * 견적 항목으로부터 주문 항목 생성
     */
    public static OrderItem fromQuoteItem(QuoteItem quoteItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(quoteItem.getProductId());
        orderItem.setProductCode(quoteItem.getProductCode());
        orderItem.setProductName(quoteItem.getProductName());
        orderItem.setProductDescription(quoteItem.getProductDescription());
        orderItem.setQuantity(quoteItem.getQuantity());
        orderItem.setUnit(quoteItem.getUnit());
        orderItem.setUnitPrice(quoteItem.getUnitPrice());
        orderItem.setDiscountRate(quoteItem.getDiscountRate());
        orderItem.setDiscountAmount(quoteItem.getDiscountAmount());
        orderItem.setTotalPrice(quoteItem.getTotalPrice());
        orderItem.setRemarks(quoteItem.getRemarks());
        orderItem.setSortOrder(quoteItem.getSortOrder());
        return orderItem;
    }

    /**
     * 상품 정보로 항목 초기화
     */
    public void initializeFromProduct(Product product) {
        if (product != null) {
            this.productId = product.getId();
            this.productCode = product.getProductCode();
            this.productName = product.getProductName();
            this.productDescription = product.getDescription();
            this.unit = product.getBaseUnit();
            this.unitPrice = product.getSellingPrice();
            calculateTotalPrice();
        }
    }

    /**
     * 미배송 수량 반환
     */
    public Integer getPendingQuantity() {
        return quantity - (shippedQuantity != null ? shippedQuantity : 0) 
                        - (cancelledQuantity != null ? cancelledQuantity : 0);
    }

    /**
     * 미배송 수량 확인
     */
    public boolean hasPendingQuantity() {
        return getPendingQuantity() > 0;
    }

    /**
     * 부분 배송 여부 확인
     */
    public boolean isPartiallyShipped() {
        return shippedQuantity != null && shippedQuantity > 0 && shippedQuantity < quantity;
    }

    /**
     * 완전 배송 여부 확인
     */
    public boolean isFullyShipped() {
        return shippedQuantity != null && shippedQuantity.equals(quantity);
    }

    /**
     * 완전 배송완료 여부 확인
     */
    public boolean isFullyDelivered() {
        return deliveredQuantity != null && deliveredQuantity.equals(quantity);
    }

    /**
     * 배송 처리
     */
    public void ship(Integer shipQuantity) {
        if (shipQuantity == null || shipQuantity <= 0) {
            throw new IllegalArgumentException("배송 수량은 0보다 커야 합니다");
        }
        
        if (getPendingQuantity() < shipQuantity) {
            throw new IllegalArgumentException("배송 수량이 미배송 수량을 초과합니다");
        }

        if (shippedQuantity == null) shippedQuantity = 0;
        shippedQuantity += shipQuantity;

        updateDeliveryStatus();
    }

    /**
     * 배송완료 처리
     */
    public void deliver(Integer deliverQuantity) {
        if (deliverQuantity == null || deliverQuantity <= 0) {
            throw new IllegalArgumentException("배송완료 수량은 0보다 커야 합니다");
        }

        if (shippedQuantity == null || shippedQuantity < deliverQuantity) {
            throw new IllegalArgumentException("배송완료 수량이 배송 수량을 초과합니다");
        }

        if (deliveredQuantity == null) deliveredQuantity = 0;
        deliveredQuantity += deliverQuantity;

        updateDeliveryStatus();
    }

    /**
     * 취소 처리
     */
    public void cancel(Integer cancelQuantity) {
        if (cancelQuantity == null || cancelQuantity <= 0) {
            throw new IllegalArgumentException("취소 수량은 0보다 커야 합니다");
        }

        if (getPendingQuantity() < cancelQuantity) {
            throw new IllegalArgumentException("취소 수량이 미배송 수량을 초과합니다");
        }

        if (cancelledQuantity == null) cancelledQuantity = 0;
        cancelledQuantity += cancelQuantity;

        updateDeliveryStatus();
    }

    /**
     * 배송 상태 업데이트
     */
    private void updateDeliveryStatus() {
        if (isFullyDelivered()) {
            deliveryStatus = DeliveryStatus.DELIVERED;
        } else if (deliveredQuantity != null && deliveredQuantity > 0) {
            deliveryStatus = DeliveryStatus.DELIVERED; // 부분 배송완료도 DELIVERED로 처리
        } else if (isFullyShipped()) {
            deliveryStatus = DeliveryStatus.SHIPPED;
        } else if (shippedQuantity != null && shippedQuantity > 0) {
            deliveryStatus = DeliveryStatus.SHIPPED; // 부분 배송도 SHIPPED로 처리
        } else if (cancelledQuantity != null && cancelledQuantity.equals(quantity)) {
            deliveryStatus = DeliveryStatus.CANCELLED;
        } else {
            deliveryStatus = DeliveryStatus.PENDING;
        }
    }

    /**
     * 할인 적용 여부 확인
     */
    public boolean hasDiscount() {
        return (discountRate != null && discountRate.compareTo(BigDecimal.ZERO) > 0) ||
               (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0);
    }

    /**
     * 할인된 단가 반환
     */
    public BigDecimal getDiscountedUnitPrice() {
        if (quantity == null || quantity == 0) return BigDecimal.ZERO;
        return totalPrice.divide(new BigDecimal(quantity), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * 라인 요약 정보 반환
     */
    public String getLineSummary() {
        return String.format("%s x %d %s = %s원", 
                productName, 
                quantity, 
                unit,
                totalPrice.toString());
    }

    /**
     * 배송 진행률 반환 (0-100%)
     */
    public double getDeliveryProgress() {
        if (quantity == null || quantity == 0) return 0.0;
        
        int processedQuantity = (deliveredQuantity != null ? deliveredQuantity : 0) + 
                               (cancelledQuantity != null ? cancelledQuantity : 0);
        
        return (double) processedQuantity / quantity * 100.0;
    }

    // Setter 오버라이드 (자동 계산 포함)
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
        updateDeliveryStatus();
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
        if (discountRate != null && discountRate.compareTo(BigDecimal.ZERO) > 0) {
            this.discountAmount = null; // 할인율 우선
        }
        calculateTotalPrice();
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.discountRate = null; // 할인금액 우선
        }
        calculateTotalPrice();
    }

    public void setShippedQuantity(Integer shippedQuantity) {
        this.shippedQuantity = shippedQuantity;
        updateDeliveryStatus();
    }

    public void setDeliveredQuantity(Integer deliveredQuantity) {
        this.deliveredQuantity = deliveredQuantity;
        updateDeliveryStatus();
    }

    public void setCancelledQuantity(Integer cancelledQuantity) {
        this.cancelledQuantity = cancelledQuantity;
        updateDeliveryStatus();
    }
}
