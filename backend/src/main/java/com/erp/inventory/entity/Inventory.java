package com.erp.inventory.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        @Index(name = "idx_inventory_warehouse", columnList = "warehouse_id"),
        @Index(name = "idx_inventory_location", columnList = "warehouse_id, location_code"),
        @Index(name = "idx_inventory_stock_status", columnList = "company_id, stock_status"),
        @Index(name = "idx_inventory_low_stock", columnList = "company_id, is_low_stock"),
        @Index(name = "idx_inventory_last_updated", columnList = "last_stock_update")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_inventory_product_warehouse_location", 
                         columnNames = {"product_id", "warehouse_id", "location_code"})
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

    /**
     * 저장 위치 코드 (창고 내 구체적 위치)
     */
    @Column(name = "location_code", length = 50)
    private String locationCode = "DEFAULT";

    /**
     * 저장 위치 설명
     */
    @Column(name = "location_description", length = 200)
    private String locationDescription;

    /**
     * 현재 재고 수량
     */
    @DecimalMin(value = "0.0", message = "현재 재고 수량은 0 이상이어야 합니다")
    @Column(name = "current_stock", nullable = false)
    private Double currentStock = 0.0;

    /**
     * 사용 가능한 재고 수량 (현재 재고 - 예약 재고)
     */
    @DecimalMin(value = "0.0", message = "사용 가능한 재고 수량은 0 이상이어야 합니다")
    @Column(name = "available_stock", nullable = false)
    private Double availableStock = 0.0;

    /**
     * 예약된 재고 수량
     */
    @DecimalMin(value = "0.0", message = "예약된 재고 수량은 0 이상이어야 합니다")
    @Column(name = "reserved_stock", nullable = false)
    private Double reservedStock = 0.0;

    /**
     * 주문된 재고 수량 (입고 예정)
     */
    @DecimalMin(value = "0.0", message = "주문된 재고 수량은 0 이상이어야 합니다")
    @Column(name = "ordered_stock", nullable = false)
    private Double orderedStock = 0.0;

    /**
     * 불량 재고 수량
     */
    @DecimalMin(value = "0.0", message = "불량 재고 수량은 0 이상이어야 합니다")
    @Column(name = "defective_stock", nullable = false)
    private Double defectiveStock = 0.0;

    /**
     * 격리된 재고 수량
     */
    @DecimalMin(value = "0.0", message = "격리된 재고 수량은 0 이상이어야 합니다")
    @Column(name = "quarantine_stock", nullable = false)
    private Double quarantineStock = 0.0;

    /**
     * 안전 재고 수량 (상품별 설정을 오버라이드)
     */
    @DecimalMin(value = "0.0", message = "안전 재고 수량은 0 이상이어야 합니다")
    @Column(name = "safety_stock")
    private Double safetyStock;

    /**
     * 최소 재고 수량
     */
    @DecimalMin(value = "0.0", message = "최소 재고 수량은 0 이상이어야 합니다")
    @Column(name = "min_stock")
    private Double minStock;

    /**
     * 최대 재고 수량
     */
    @DecimalMin(value = "0.0", message = "최대 재고 수량은 0 이상이어야 합니다")
    @Column(name = "max_stock")
    private Double maxStock;

    /**
     * 재주문점
     */
    @DecimalMin(value = "0.0", message = "재주문점은 0 이상이어야 합니다")
    @Column(name = "reorder_point")
    private Double reorderPoint;

    /**
     * 재주문 수량
     */
    @DecimalMin(value = "0.0", message = "재주문 수량은 0 이상이어야 합니다")
    @Column(name = "reorder_quantity")
    private Double reorderQuantity;

    /**
     * 평균 원가
     */
    @DecimalMin(value = "0.0", message = "평균 원가는 0 이상이어야 합니다")
    @Column(name = "average_cost", precision = 15, scale = 2, nullable = false)
    private BigDecimal averageCost = BigDecimal.ZERO;

    /**
     * 최근 매입 단가
     */
    @DecimalMin(value = "0.0", message = "최근 매입 단가는 0 이상이어야 합니다")
    @Column(name = "last_purchase_price", precision = 15, scale = 2)
    private BigDecimal lastPurchasePrice;

    /**
     * 총 재고 가치 (평균원가 기준)
     */
    @DecimalMin(value = "0.0", message = "총 재고 가치는 0 이상이어야 합니다")
    @Column(name = "total_stock_value", precision = 18, scale = 2, nullable = false)
    private BigDecimal totalStockValue = BigDecimal.ZERO;

    /**
     * 재고 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "stock_status", nullable = false, length = 20)
    private StockStatus stockStatus = StockStatus.NORMAL;

    /**
     * 재고 등급
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "stock_grade", length = 20)
    private StockGrade stockGrade = StockGrade.A;

    /**
     * 안전재고 미달 여부
     */
    @Column(name = "is_low_stock", nullable = false)
    private Boolean isLowStock = false;

    /**
     * 재고없음 여부
     */
    @Column(name = "is_out_of_stock", nullable = false)
    private Boolean isOutOfStock = false;

    /**
     * 과재고 여부
     */
    @Column(name = "is_over_stock", nullable = false)
    private Boolean isOverStock = false;

    /**
     * 재주문 필요 여부
     */
    @Column(name = "needs_reorder", nullable = false)
    private Boolean needsReorder = false;

    /**
     * 마지막 입고일
     */
    @Column(name = "last_receipt_date")
    private LocalDateTime lastReceiptDate;

    /**
     * 마지막 출고일
     */
    @Column(name = "last_issue_date")
    private LocalDateTime lastIssueDate;

    /**
     * 마지막 재고 실사일
     */
    @Column(name = "last_stocktaking_date")
    private LocalDateTime lastStocktakingDate;

    /**
     * 마지막 재고 업데이트 일시
     */
    @Column(name = "last_stock_update", nullable = false)
    private LocalDateTime lastStockUpdate = LocalDateTime.now();

    /**
     * 재고 이동 횟수 (통계용)
     */
    @Column(name = "movement_count", nullable = false)
    private Integer movementCount = 0;

    /**
     * 온도 (섭씨)
     */
    @Column(name = "temperature")
    private Double temperature;

    /**
     * 습도 (%)
     */
    @Column(name = "humidity")
    private Double humidity;

    /**
     * 유효기간
     */
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    /**
     * 제조일
     */
    @Column(name = "manufacture_date")
    private LocalDateTime manufactureDate;

    /**
     * 로트 번호
     */
    @Column(name = "lot_number", length = 50)
    private String lotNumber;

    /**
     * 시리얼 번호
     */
    @Column(name = "serial_number", length = 50)
    private String serialNumber;

    /**
     * 공급업체 정보
     */
    @Column(name = "supplier_info", length = 200)
    private String supplierInfo;

    /**
     * 비고
     */
    @Column(name = "remarks", length = 500)
    private String remarks;

    /**
     * 메타데이터 (JSON 형태)
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

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
        // 재고없음 확인
        this.isOutOfStock = (currentStock == null || currentStock <= 0);
        
        if (isOutOfStock) {
            this.stockStatus = StockStatus.OUT_OF_STOCK;
            this.isLowStock = false;
            this.isOverStock = false;
            this.needsReorder = true;
            return;
        }

        // 안전재고 미달 확인
        Double safetyStockThreshold = getSafetyStockThreshold();
        this.isLowStock = (safetyStockThreshold != null && currentStock < safetyStockThreshold);

        // 과재고 확인
        this.isOverStock = (maxStock != null && maxStock > 0 && currentStock > maxStock);

        // 재주문 필요 확인
        this.needsReorder = (reorderPoint != null && reorderPoint > 0 && availableStock != null && availableStock <= reorderPoint);

        // 상태 결정
        if (isOverStock) {
            this.stockStatus = StockStatus.OVER_STOCK;
        } else if (isLowStock) {
            this.stockStatus = StockStatus.LOW_STOCK;
        } else if (reservedStock != null && reservedStock > 0) {
            this.stockStatus = StockStatus.RESERVED;
        } else {
            this.stockStatus = StockStatus.NORMAL;
        }

        this.lastStockUpdate = LocalDateTime.now();
    }

    /**
     * 사용 가능한 재고 수량 업데이트
     */
    public void updateAvailableStock() {
        this.availableStock = (currentStock != null ? currentStock : 0.0) - 
                             (reservedStock != null ? reservedStock : 0.0) -
                             (quarantineStock != null ? quarantineStock : 0.0) -
                             (defectiveStock != null ? defectiveStock : 0.0);
        
        if (this.availableStock < 0) {
            this.availableStock = 0.0;
        }
    }

    /**
     * 재고 가치 업데이트
     */
    public void updateStockValue() {
        if (currentStock != null && averageCost != null) {
            this.totalStockValue = averageCost.multiply(BigDecimal.valueOf(currentStock));
        } else {
            this.totalStockValue = BigDecimal.ZERO;
        }
    }

    /**
     * 재고 입고 처리
     */
    public void receiveStock(Double quantity, BigDecimal unitCost) {
        if (quantity == null || quantity <= 0) {
            return;
        }

        // 평균 원가 계산 (가중평균법)
        if (unitCost != null && unitCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal currentValue = averageCost.multiply(BigDecimal.valueOf(currentStock != null ? currentStock : 0.0));
            BigDecimal newValue = unitCost.multiply(BigDecimal.valueOf(quantity));
            BigDecimal totalValue = currentValue.add(newValue);
            Double totalQuantity = (currentStock != null ? currentStock : 0.0) + quantity;
            
            if (totalQuantity > 0) {
                this.averageCost = totalValue.divide(BigDecimal.valueOf(totalQuantity), 2, BigDecimal.ROUND_HALF_UP);
            }
            
            this.lastPurchasePrice = unitCost;
        }

        // 재고 수량 업데이트
        this.currentStock = (currentStock != null ? currentStock : 0.0) + quantity;
        this.lastReceiptDate = LocalDateTime.now();
        this.movementCount++;

        updateAvailableStock();
        updateStockValue();
        updateStockStatus();
    }

    /**
     * 재고 출고 처리
     */
    public boolean issueStock(Double quantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }

        if (availableStock == null || availableStock < quantity) {
            return false; // 재고 부족
        }

        this.currentStock = (currentStock != null ? currentStock : 0.0) - quantity;
        this.lastIssueDate = LocalDateTime.now();
        this.movementCount++;

        updateAvailableStock();
        updateStockValue();
        updateStockStatus();

        return true;
    }

    /**
     * 재고 예약
     */
    public boolean reserveStock(Double quantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }

        if (availableStock == null || availableStock < quantity) {
            return false; // 재고 부족
        }

        this.reservedStock = (reservedStock != null ? reservedStock : 0.0) + quantity;
        updateAvailableStock();
        updateStockStatus();

        return true;
    }

    /**
     * 재고 예약 해제
     */
    public void unreserveStock(Double quantity) {
        if (quantity == null || quantity <= 0) {
            return;
        }

        this.reservedStock = Math.max(0.0, (reservedStock != null ? reservedStock : 0.0) - quantity);
        updateAvailableStock();
        updateStockStatus();
    }

    /**
     * 재고 실사 처리
     */
    public void adjustStock(Double actualQuantity, String reason) {
        if (actualQuantity == null || actualQuantity < 0) {
            return;
        }

        this.currentStock = actualQuantity;
        this.lastStocktakingDate = LocalDateTime.now();
        this.movementCount++;

        updateAvailableStock();
        updateStockValue();
        updateStockStatus();
    }

    /**
     * 안전재고 임계값 조회 (재고별 설정 우선, 없으면 상품별 설정 사용)
     */
    public Double getSafetyStockThreshold() {
        if (safetyStock != null) {
            return safetyStock;
        }
        if (product != null && product.getSafetyStock() != null) {
            return product.getSafetyStock().doubleValue();
        }
        return null;
    }

    /**
     * 최소재고 임계값 조회
     */
    public Double getMinStockThreshold() {
        if (minStock != null) {
            return minStock;
        }
        if (product != null && product.getMinStock() != null) {
            return product.getMinStock().doubleValue();
        }
        return null;
    }

    /**
     * 최대재고 임계값 조회
     */
    public Double getMaxStockThreshold() {
        if (maxStock != null) {
            return maxStock;
        }
        if (product != null && product.getMaxStock() != null) {
            return product.getMaxStock().doubleValue();
        }
        return null;
    }

    /**
     * 재주문점 임계값 조회
     */
    public Double getReorderPointThreshold() {
        if (reorderPoint != null) {
            return reorderPoint;
        }
        if (product != null && product.getReorderPoint() != null) {
            return product.getReorderPoint().doubleValue();
        }
        return null;
    }

    /**
     * 재고 회전율 계산 (연간 기준)
     */
    public Double calculateStockTurnoverRate(Double annualUsage) {
        if (annualUsage == null || annualUsage <= 0 || currentStock == null || currentStock <= 0) {
            return 0.0;
        }
        return annualUsage / currentStock;
    }

    /**
     * 재고 보관 일수 계산
     */
    public Integer calculateDaysInStock() {
        if (lastReceiptDate == null) {
            return null;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(lastReceiptDate.toLocalDate(), LocalDateTime.now().toLocalDate());
    }

    /**
     * 유효기간 만료 임박 여부 (30일 이내)
     */
    public boolean isExpiringsoon() {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.isBefore(LocalDateTime.now().plusDays(30));
    }

    /**
     * 유효기간 만료 여부
     */
    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.isBefore(LocalDateTime.now());
    }

    /**
     * 전체 위치 정보 조회
     */
    public String getFullLocation() {
        StringBuilder location = new StringBuilder();
        
        if (warehouse != null) {
            location.append(warehouse.getWarehouseName());
        }
        
        if (locationCode != null && !locationCode.equals("DEFAULT")) {
            if (location.length() > 0) {
                location.append(" - ");
            }
            location.append(locationCode);
        }
        
        if (locationDescription != null) {
            if (location.length() > 0) {
                location.append(" (").append(locationDescription).append(")");
            } else {
                location.append(locationDescription);
            }
        }
        
        return location.toString();
    }

    @PrePersist
    @PreUpdate
    private void updateCalculatedFields() {
        updateAvailableStock();
        updateStockValue();
        updateStockStatus();
    }
}
