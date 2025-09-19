package com.erp.inventory.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import jakarta.persistence.*;
// import jakarta.validation.constraints.DecimalMin; // 사용하지 않음
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
// import java.time.LocalDateTime; // 제거된 필드들로 인해 사용하지 않음
import java.util.ArrayList;
import java.util.List;

/**
 * 재고 엔티티
 * 창고별 상품 재고 정보를 관리합니다
 */
@Entity
@Table(
    name = "inventories",
    indexes = {
        @Index(name = "idx_inventory_company", columnList = "company_id"),
        @Index(name = "idx_inventory_product", columnList = "product_id"),
        @Index(name = "idx_inventory_warehouse", columnList = "warehouse_id")
        // 존재하지 않는 컬럼들에 대한 인덱스 제거:
        // stock_status, is_low_stock, last_stock_update 컬럼이 DB에 존재하지 않음
    },
    uniqueConstraints = {
        // @UniqueConstraint(name = "uk_inventory_product_warehouse_location", 
        //                  columnNames = {"product_id", "warehouse_id", "location_code"}) // location_code 컬럼이 존재하지 않음
    }
)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"product", "warehouse", "stockMovements"})
@ToString(exclude = {"product", "warehouse", "stockMovements"})
public class Inventory extends BaseEntity {

    /**
     * 재고 상태 열거형
     */
    public enum StockStatus {
        NORMAL("정상"),
        LOW_STOCK("안전재고 미달"),
        OUT_OF_STOCK("재고없음"),
        OVER_STOCK("과재고"),
        RESERVED("예약됨"),
        QUARANTINE("격리"),
        DAMAGED("불량"),
        EXPIRED("유효기간 만료");

        private final String description;

        StockStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 재고 등급 열거형
     */
    public enum StockGrade {
        A("우수"),
        B("양호"),
        C("보통"),
        D("불량"),
        DEFECTIVE("결함");

        private final String description;

        StockGrade(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 회사 정보
     */
    @NotNull(message = "회사 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_company"))
    private Company company;

    /**
     * 상품 정보
     */
    @NotNull(message = "상품 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_product"))
    private Product product;

    /**
     * 창고 정보
     */
    @NotNull(message = "창고 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_warehouse"))
    private Warehouse warehouse;

    // locationCode 필드 제거 - 데이터베이스에 해당 컬럼이 존재하지 않음

    // locationDescription 필드 제거 - 데이터베이스에 해당 컬럼이 존재하지 않음

    /**
     * 현재 재고 수량 (DB: INTEGER)
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    /**
     * 사용 가능한 재고 수량 (DB: INTEGER)
     */
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity = 0;

    /**
     * 예약된 재고 수량 (DB: INTEGER)
     */
    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    // DdlForcer 스키마에 없는 필드들 - 주석 처리
    // ordered_stock, defective_stock, quarantine_stock, safety_stock, min_stock

    /**
     * 최대 재고 수량 (DB: INTEGER)
     */
    @Column(name = "max_stock")
    private Integer maxStock;

    /**
     * 재주문점 (DB: INTEGER)
     */
    @Column(name = "reorder_point")
    private Integer reorderPoint;

    // DdlForcer 스키마에 없는 필드들 - 주석 처리
    // reorder_quantity, last_purchase_price, total_stock_value, stock_status, stock_grade

    // DdlForcer 스키마에 없는 추가 필드들 - 주석 처리
    // is_low_stock, is_out_of_stock, is_over_stock, needs_reorder
    // last_receipt_date, last_issue_date, last_stocktaking_date, last_stock_update

    // DdlForcer 스키마에 없는 추가 필드들 - 주석 처리
    // movement_count, temperature, humidity, expiry_date, manufacture_date
    // lot_number, serial_number, supplier_info

    // DdlForcer 스키마에 없는 추가 필드들 - 주석 처리
    // remarks, metadata

    /**
     * 이 재고의 재고이동 내역
     */
    @OneToMany(mappedBy = "inventory", fetch = FetchType.LAZY)
    @OrderBy("movementDate DESC")
    private List<StockMovement> stockMovements = new ArrayList<>();

    // 편의 메서드들

    /**
     * 재고 상태 업데이트
     */
    public void updateStockStatus() {
        // 제거된 필드들로 인해 단순화
        // 실제 구현에서는 필요한 로직만 유지
    }

    // updateAvailableStock() 메서드 제거 - availableStock 필드가 없음

    /**
     * 재고 가치 업데이트
     */
    public void updateStockValue() {
        // totalStockValue 필드가 제거되어 단순화
        // this.totalStockValue = BigDecimal.ZERO;
    }

    /**
     * 재고 입고 처리
     */
    public void receiveStock(Integer quantity, BigDecimal unitCost) {
        if (quantity == null || quantity <= 0) {
            return;
        }

        // 재고 수량 업데이트
        this.quantity = (this.quantity != null ? this.quantity : 0) + quantity;

        updateStockValue();
        updateStockStatus();
    }

    /**
     * 재고 출고 처리
     */
    public boolean issueStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }

        if (this.quantity == null || this.quantity < quantity) {
            return false; // 재고 부족
        }

        this.quantity = (this.quantity != null ? this.quantity : 0) - quantity;

        updateStockValue();
        updateStockStatus();

        return true;
    }

    /**
     * 재고 예약
     */
    public boolean reserveStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }

        if (this.quantity == null || this.quantity < quantity) {
            return false; // 재고 부족
        }

        this.reservedQuantity = (reservedQuantity != null ? reservedQuantity : 0) + quantity;
        updateStockStatus();

        return true;
    }

    /**
     * 재고 예약 해제
     */
    public void unreserveStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return;
        }

        this.reservedQuantity = Math.max(0, (reservedQuantity != null ? reservedQuantity : 0) - quantity);
        updateStockStatus();
    }

    /**
     * 재고 실사 처리
     */
    public void adjustStock(Integer actualQuantity, String reason) {
        if (actualQuantity == null || actualQuantity < 0) {
            return;
        }

        this.quantity = actualQuantity;
        // this.lastStocktakingDate = LocalDateTime.now();
        // this.movementCount++;

        updateStockValue();
        updateStockStatus();
    }

    /**
     * 안전재고 임계값 조회 - 단순화된 버전
     */
    public Double getSafetyStockThreshold() {
        // safetyStock 필드가 제거되어 단순화
        return null;
    }

    /**
     * 최소재고 임계값 조회 - 단순화된 버전
     */
    public Double getMinStockThreshold() {
        // minStock 필드가 제거되어 단순화
        return null;
    }

    /**
     * 최대재고 임계값 조회
     */
    public Integer getMaxStockThreshold() {
        if (maxStock != null) {
            return maxStock;
        }
        if (product != null && product.getMaxStock() != null) {
            return product.getMaxStock().intValue();
        }
        return null;
    }

    /**
     * 재주문점 임계값 조회
     */
    public Integer getReorderPointThreshold() {
        if (reorderPoint != null) {
            return reorderPoint;
        }
        if (product != null && product.getReorderPoint() != null) {
            return product.getReorderPoint().intValue();
        }
        return null;
    }

    /**
     * 재고 회전율 계산 (연간 기준)
     */
    public Double calculateStockTurnoverRate(Integer annualUsage) {
        if (annualUsage == null || annualUsage <= 0 || quantity == null || quantity <= 0) {
            return 0.0;
        }
        return annualUsage.doubleValue() / quantity.doubleValue();
    }

    /**
     * 재고 보관 일수 계산 - 단순화된 버전
     */
    public Integer calculateDaysInStock() {
        // lastReceiptDate 필드가 제거되어 단순화
        return null;
    }

    /**
     * 유효기간 만료 임박 여부 - 단순화된 버전
     */
    public boolean isExpiringsoon() {
        // expiryDate 필드가 제거되어 단순화
        return false;
    }

    /**
     * 유효기간 만료 여부 - 단순화된 버전
     */
    public boolean isExpired() {
        // expiryDate 필드가 제거되어 단순화
        return false;
    }

    /**
     * 전체 위치 정보 조회
     */
    public String getFullLocation() {
        StringBuilder location = new StringBuilder();
        
        if (warehouse != null) {
            location.append(warehouse.getWarehouseName());
        }
        
        // locationCode, locationDescription 필드가 제거되어 단순화
        return location.toString();
    }

    @PrePersist
    @PreUpdate
    private void updateCalculatedFields() {
        updateStockValue();
        updateStockStatus();
    }
}
