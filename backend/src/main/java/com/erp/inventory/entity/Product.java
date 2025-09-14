package com.erp.inventory.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 상품 엔티티
 * 재고 관리 대상 상품 정보를 관리합니다
 */
@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_product_company", columnList = "company_id"),
        @Index(name = "idx_product_category", columnList = "category_id"),
        @Index(name = "idx_product_code", columnList = "company_id, product_code"),
        @Index(name = "idx_product_barcode", columnList = "barcode"),
        @Index(name = "idx_product_type", columnList = "company_id, product_type"),
        @Index(name = "idx_product_status", columnList = "company_id, product_status"),
        @Index(name = "idx_product_active", columnList = "company_id, is_active"),
        @Index(name = "idx_product_name", columnList = "company_id, product_name")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_code", columnNames = {"company_id", "product_code"}),
        @UniqueConstraint(name = "uk_product_barcode", columnNames = "barcode")
    }
)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"category", "inventories", "stockMovements"})
@ToString(exclude = {"category", "inventories", "stockMovements"})
public class Product extends BaseEntity {

    /**
     * 상품 유형 열거형
     */
    public enum ProductType {
        FINISHED_GOODS("완제품"),
        RAW_MATERIAL("원재료"),
        SEMI_FINISHED("반제품"),
        CONSUMABLE("소모품"),
        SERVICE("서비스"),
        VIRTUAL("가상상품"),
        BUNDLE("번들상품"),
        DIGITAL("디지털상품");

        private final String description;

        ProductType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 상품 상태 열거형
     */
    public enum ProductStatus {
        ACTIVE("판매중"),
        INACTIVE("판매중단"),
        DISCONTINUED("단종"),
        PENDING("승인대기"),
        DRAFT("임시저장");

        private final String description;

        ProductStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 재고 관리 방식 열거형
     */
    public enum StockManagementType {
        FIFO("선입선출"),
        LIFO("후입선출"),
        AVERAGE("평균법"),
        SPECIFIC("개별법");

        private final String description;

        StockManagementType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 상품 코드 (회사별 고유)
     */
    @NotBlank(message = "상품 코드는 필수입니다")
    @Size(max = 30, message = "상품 코드는 30자를 초과할 수 없습니다")
    @Column(name = "product_code", nullable = false, length = 30)
    private String productCode;

    /**
     * 상품명
     */
    @NotBlank(message = "상품명은 필수입니다")
    @Size(max = 200, message = "상품명은 200자를 초과할 수 없습니다")
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    /**
     * 상품명 (영문)
     */
    @Size(max = 200, message = "영문 상품명은 200자를 초과할 수 없습니다")
    @Column(name = "product_name_en", length = 200)
    private String productNameEn;

    /**
     * 상품 설명
     */
    @Size(max = 1000, message = "상품 설명은 1000자를 초과할 수 없습니다")
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * 상세 설명
     */
    @Column(name = "detailed_description", columnDefinition = "TEXT")
    private String detailedDescription;

    /**
     * 회사 정보
     */
    @NotNull(message = "회사 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_company"))
    private Company company;

    /**
     * 상품 분류
     */
    @NotNull(message = "상품 분류는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_category"))
    private ProductCategory category;

    /**
     * 상품 유형
     */
    @NotNull(message = "상품 유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false, length = 20)
    private ProductType productType;

    /**
     * 상품 상태
     */
    @NotNull(message = "상품 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false, length = 20)
    private ProductStatus productStatus = ProductStatus.ACTIVE;

    /**
     * 재고 관리 방식
     */
    @NotNull(message = "재고 관리 방식은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "stock_management_type", nullable = false, length = 20)
    private StockManagementType stockManagementType = StockManagementType.FIFO;

    /**
     * 활성 상태
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 재고 관리 대상 여부
     */
    @Column(name = "track_inventory", nullable = false)
    private Boolean trackInventory = true;

    /**
     * 바코드
     */
    @Size(max = 50, message = "바코드는 50자를 초과할 수 없습니다")
    @Column(name = "barcode", length = 50)
    private String barcode;

    /**
     * QR 코드
     */
    @Size(max = 200, message = "QR 코드는 200자를 초과할 수 없습니다")
    @Column(name = "qr_code", length = 200)
    private String qrCode;

    /**
     * SKU (Stock Keeping Unit)
     */
    @Size(max = 50, message = "SKU는 50자를 초과할 수 없습니다")
    @Column(name = "sku", length = 50)
    private String sku;

    /**
     * 기본 단위
     */
    @NotBlank(message = "기본 단위는 필수입니다")
    @Size(max = 10, message = "기본 단위는 10자를 초과할 수 없습니다")
    @Column(name = "base_unit", nullable = false, length = 10)
    private String baseUnit;

    /**
     * 보조 단위
     */
    @Size(max = 10, message = "보조 단위는 10자를 초과할 수 없습니다")
    @Column(name = "sub_unit", length = 10)
    private String subUnit;

    /**
     * 단위 변환 비율 (보조단위 = 기본단위 * 변환비율)
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "단위 변환 비율은 0보다 커야 합니다")
    @Column(name = "unit_conversion_rate", precision = 10, scale = 4)
    private BigDecimal unitConversionRate = BigDecimal.ONE;

    /**
     * 표준 원가
     */
    @DecimalMin(value = "0.0", message = "표준 원가는 0 이상이어야 합니다")
    @Column(name = "standard_cost", precision = 15, scale = 2)
    private BigDecimal standardCost = BigDecimal.ZERO;

    /**
     * 평균 원가
     */
    @DecimalMin(value = "0.0", message = "평균 원가는 0 이상이어야 합니다")
    @Column(name = "average_cost", precision = 15, scale = 2)
    private BigDecimal averageCost = BigDecimal.ZERO;

    /**
     * 최근 매입 단가
     */
    @DecimalMin(value = "0.0", message = "최근 매입 단가는 0 이상이어야 합니다")
    @Column(name = "last_purchase_price", precision = 15, scale = 2)
    private BigDecimal lastPurchasePrice = BigDecimal.ZERO;

    /**
     * 판매 단가
     */
    @DecimalMin(value = "0.0", message = "판매 단가는 0 이상이어야 합니다")
    @Column(name = "selling_price", precision = 15, scale = 2)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    /**
     * 최소 판매 단가
     */
    @DecimalMin(value = "0.0", message = "최소 판매 단가는 0 이상이어야 합니다")
    @Column(name = "min_selling_price", precision = 15, scale = 2)
    private BigDecimal minSellingPrice = BigDecimal.ZERO;

    /**
     * 안전 재고 수량
     */
    @DecimalMin(value = "0.0", message = "안전 재고 수량은 0 이상이어야 합니다")
    @Column(name = "safety_stock", precision = 12, scale = 3)
    private BigDecimal safetyStock = BigDecimal.ZERO;

    /**
     * 최소 재고 수량
     */
    @DecimalMin(value = "0.0", message = "최소 재고 수량은 0 이상이어야 합니다")
    @Column(name = "min_stock", precision = 12, scale = 3)
    private BigDecimal minStock = BigDecimal.ZERO;

    /**
     * 최대 재고 수량
     */
    @DecimalMin(value = "0.0", message = "최대 재고 수량은 0 이상이어야 합니다")
    @Column(name = "max_stock", precision = 12, scale = 3)
    private BigDecimal maxStock = BigDecimal.ZERO;

    /**
     * 재주문점
     */
    @DecimalMin(value = "0.0", message = "재주문점은 0 이상이어야 합니다")
    @Column(name = "reorder_point", precision = 12, scale = 3)
    private BigDecimal reorderPoint = BigDecimal.ZERO;

    /**
     * 재주문 수량
     */
    @DecimalMin(value = "0.0", message = "재주문 수량은 0 이상이어야 합니다")
    @Column(name = "reorder_quantity", precision = 12, scale = 3)
    private BigDecimal reorderQuantity = BigDecimal.ZERO;

    /**
     * 리드타임 (일)
     */
    @Min(value = 0, message = "리드타임은 0 이상이어야 합니다")
    @Column(name = "lead_time_days")
    private Integer leadTimeDays = 0;

    /**
     * 유효기간 (일)
     */
    @Min(value = 0, message = "유효기간은 0 이상이어야 합니다")
    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    /**
     * 가로 (cm)
     */
    @DecimalMin(value = "0.0", message = "가로는 0 이상이어야 합니다")
    @Column(name = "width", precision = 8, scale = 2)
    private BigDecimal width;

    /**
     * 세로 (cm)
     */
    @DecimalMin(value = "0.0", message = "세로는 0 이상이어야 합니다")
    @Column(name = "height", precision = 8, scale = 2)
    private BigDecimal height;

    /**
     * 깊이 (cm)
     */
    @DecimalMin(value = "0.0", message = "깊이는 0 이상이어야 합니다")
    @Column(name = "depth", precision = 8, scale = 2)
    private BigDecimal depth;

    /**
     * 무게 (kg)
     */
    @DecimalMin(value = "0.0", message = "무게는 0 이상이어야 합니다")
    @Column(name = "weight", precision = 8, scale = 3)
    private BigDecimal weight;

    /**
     * 부피 (cm³)
     */
    @DecimalMin(value = "0.0", message = "부피는 0 이상이어야 합니다")
    @Column(name = "volume", precision = 12, scale = 3)
    private BigDecimal volume;

    /**
     * 색상
     */
    @Size(max = 30, message = "색상은 30자를 초과할 수 없습니다")
    @Column(name = "color", length = 30)
    private String color;

    /**
     * 크기
     */
    @Size(max = 30, message = "크기는 30자를 초과할 수 없습니다")
    @Column(name = "size", length = 30)
    private String size;

    /**
     * 브랜드
     */
    @Size(max = 50, message = "브랜드는 50자를 초과할 수 없습니다")
    @Column(name = "brand", length = 50)
    private String brand;

    /**
     * 제조사
     */
    @Size(max = 100, message = "제조사는 100자를 초과할 수 없습니다")
    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    /**
     * 공급업체
     */
    @Size(max = 100, message = "공급업체는 100자를 초과할 수 없습니다")
    @Column(name = "supplier", length = 100)
    private String supplier;

    /**
     * 원산지
     */
    @Size(max = 50, message = "원산지는 50자를 초과할 수 없습니다")
    @Column(name = "origin_country", length = 50)
    private String originCountry;

    /**
     * HS 코드 (관세청 품목분류코드)
     */
    @Size(max = 20, message = "HS 코드는 20자를 초과할 수 없습니다")
    @Column(name = "hs_code", length = 20)
    private String hsCode;

    /**
     * 세율 (%)
     */
    @DecimalMin(value = "0.0", message = "세율은 0 이상이어야 합니다")
    @DecimalMax(value = "100.0", message = "세율은 100 이하여야 합니다")
    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate = BigDecimal.ZERO;

    /**
     * 이미지 경로들 (JSON 배열 형태)
     */
    @Column(name = "image_paths", columnDefinition = "TEXT")
    private String imagePaths;

    /**
     * 첨부파일 경로들 (JSON 배열 형태)
     */
    @Column(name = "attachment_paths", columnDefinition = "TEXT")
    private String attachmentPaths;

    /**
     * 태그들 (JSON 배열 형태)
     */
    @Size(max = 500, message = "태그는 500자를 초과할 수 없습니다")
    @Column(name = "tags", length = 500)
    private String tags;

    /**
     * 정렬 순서
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /**
     * 메타데이터 (JSON 형태)
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    /**
     * 이 상품의 재고 목록
     */
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Inventory> inventories = new ArrayList<>();

    /**
     * 이 상품의 재고이동 내역
     */
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<StockMovement> stockMovements = new ArrayList<>();

    // 편의 메서드들

    /**
     * 총 재고 수량 조회
     */
    public BigDecimal getTotalStock() {
        return inventories.stream()
                .filter(inv -> inv.getCurrentStock() != null)
                .map(inv -> BigDecimal.valueOf(inv.getCurrentStock()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 사용 가능한 재고 수량 조회
     */
    public BigDecimal getAvailableStock() {
        return inventories.stream()
                .filter(inv -> inv.getAvailableStock() != null)
                .map(inv -> BigDecimal.valueOf(inv.getAvailableStock()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 예약된 재고 수량 조회
     */
    public BigDecimal getReservedStock() {
        return inventories.stream()
                .filter(inv -> inv.getReservedStock() != null)
                .map(inv -> BigDecimal.valueOf(inv.getReservedStock()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 안전재고 미달 여부
     */
    public boolean isLowStock() {
        BigDecimal totalStock = getTotalStock();
        return safetyStock != null && totalStock.compareTo(safetyStock) < 0;
    }

    /**
     * 재고없음 여부
     */
    public boolean isOutOfStock() {
        return getTotalStock().compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * 과재고 여부
     */
    public boolean isOverStock() {
        if (maxStock == null || maxStock.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return getTotalStock().compareTo(maxStock) > 0;
    }

    /**
     * 재주문 필요 여부
     */
    public boolean needsReorder() {
        if (reorderPoint == null || reorderPoint.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return getAvailableStock().compareTo(reorderPoint) <= 0;
    }

    /**
     * 판매 가능 여부
     */
    public boolean isSellable() {
        return isActive && productStatus == ProductStatus.ACTIVE && getAvailableStock().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 부피 계산
     */
    public BigDecimal calculateVolume() {
        if (width != null && height != null && depth != null) {
            return width.multiply(height).multiply(depth);
        }
        return null;
    }

    /**
     * 이익률 계산 (판매단가 기준)
     */
    public BigDecimal calculateProfitRate() {
        if (sellingPrice == null || sellingPrice.compareTo(BigDecimal.ZERO) <= 0 ||
            averageCost == null || averageCost.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal profit = sellingPrice.subtract(averageCost);
        return profit.divide(sellingPrice, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
    }

    /**
     * 재고 회전율 계산을 위한 평균 재고 조회
     */
    public BigDecimal getAverageStock() {
        // 실제 구현시에는 일정 기간의 재고 데이터를 기반으로 계산
        return getTotalStock();
    }

    /**
     * 상품 상태 설명 조회
     */
    public String getStatusDescription() {
        if (!isActive) {
            return "비활성";
        }
        return productStatus.getDescription();
    }

    /**
     * 재고 상태 요약
     */
    public String getStockStatusSummary() {
        if (isOutOfStock()) {
            return "재고없음";
        } else if (isLowStock()) {
            return "안전재고 미달";
        } else if (isOverStock()) {
            return "과재고";
        } else if (needsReorder()) {
            return "재주문 필요";
        } else {
            return "정상";
        }
    }

    /**
     * 단위 변환 (기본단위 → 보조단위)
     */
    public BigDecimal convertToSubUnit(BigDecimal baseQuantity) {
        if (baseQuantity == null || unitConversionRate == null) {
            return baseQuantity;
        }
        return baseQuantity.multiply(unitConversionRate);
    }

    /**
     * 단위 변환 (보조단위 → 기본단위)
     */
    public BigDecimal convertToBaseUnit(BigDecimal subQuantity) {
        if (subQuantity == null || unitConversionRate == null || 
            unitConversionRate.compareTo(BigDecimal.ZERO) == 0) {
            return subQuantity;
        }
        return subQuantity.divide(unitConversionRate, 3, BigDecimal.ROUND_HALF_UP);
    }

    @PrePersist
    @PreUpdate
    private void calculateFields() {
        // 부피 자동 계산
        if (volume == null) {
            volume = calculateVolume();
        }
    }
}