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
 * 견적 항목 엔티티
 * 견적서의 개별 상품 정보를 관리합니다
 */
@Entity
@Table(name = "quote_items", indexes = {
    @Index(name = "idx_quote_item_quote", columnList = "quote_id"),
    @Index(name = "idx_quote_item_product", columnList = "product_id"),
    @Index(name = "idx_quote_item_line_number", columnList = "line_number")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class QuoteItem extends BaseEntity {

    @NotNull(message = "견적은 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false, foreignKey = @ForeignKey(name = "fk_quote_item_quote"))
    private Quote quote;

    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false, 
                foreignKey = @ForeignKey(name = "fk_quote_item_product"))
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

    @NotNull(message = "수량은 필수입니다")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

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

    @Size(max = 500, message = "비고는 500자 이내여야 합니다")
    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    // 비즈니스 메서드
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
     * 할인율 설정 및 총가격 재계산
     */
    public void setDiscountRateAndRecalculate(BigDecimal discountRate) {
        this.discountRate = discountRate;
        this.discountAmount = null; // 할인율 우선 적용
        calculateTotalPrice();
    }

    /**
     * 할인금액 설정 및 총가격 재계산
     */
    public void setDiscountAmountAndRecalculate(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        this.discountRate = null; // 할인금액 우선 적용
        calculateTotalPrice();
    }

    /**
     * 수량 설정 및 총가격 재계산
     */
    public void setQuantityAndRecalculate(Integer quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }

    /**
     * 단가 설정 및 총가격 재계산
     */
    public void setUnitPriceAndRecalculate(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
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

    // Setter 오버라이드 (자동 계산 포함)
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
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
}
