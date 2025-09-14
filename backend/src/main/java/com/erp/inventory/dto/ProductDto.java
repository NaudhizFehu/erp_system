package com.erp.inventory.dto;

import com.erp.inventory.entity.Product;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 상품 DTO 클래스들
 */
public class ProductDto {

    /**
     * 상품 생성 요청 DTO
     */
    public record ProductCreateDto(
            @NotBlank(message = "상품 코드는 필수입니다")
            @Size(max = 30, message = "상품 코드는 30자를 초과할 수 없습니다")
            String productCode,

            @NotBlank(message = "상품명은 필수입니다")
            @Size(max = 200, message = "상품명은 200자를 초과할 수 없습니다")
            String productName,

            @Size(max = 200, message = "영문 상품명은 200자를 초과할 수 없습니다")
            String productNameEn,

            @Size(max = 1000, message = "상품 설명은 1000자를 초과할 수 없습니다")
            String description,

            String detailedDescription,

            @NotNull(message = "회사 ID는 필수입니다")
            Long companyId,

            @NotNull(message = "상품 분류 ID는 필수입니다")
            Long categoryId,

            @NotNull(message = "상품 유형은 필수입니다")
            Product.ProductType productType,

            Product.ProductStatus productStatus,
            Product.StockManagementType stockManagementType,
            Boolean isActive,
            Boolean trackInventory,

            @Size(max = 50, message = "바코드는 50자를 초과할 수 없습니다")
            String barcode,

            @Size(max = 200, message = "QR 코드는 200자를 초과할 수 없습니다")
            String qrCode,

            @Size(max = 50, message = "SKU는 50자를 초과할 수 없습니다")
            String sku,

            @NotBlank(message = "기본 단위는 필수입니다")
            @Size(max = 10, message = "기본 단위는 10자를 초과할 수 없습니다")
            String baseUnit,

            @Size(max = 10, message = "보조 단위는 10자를 초과할 수 없습니다")
            String subUnit,

            @DecimalMin(value = "0.0", inclusive = false, message = "단위 변환 비율은 0보다 커야 합니다")
            BigDecimal unitConversionRate,

            @DecimalMin(value = "0.0", message = "표준 원가는 0 이상이어야 합니다")
            BigDecimal standardCost,

            @DecimalMin(value = "0.0", message = "판매 단가는 0 이상이어야 합니다")
            BigDecimal sellingPrice,

            @DecimalMin(value = "0.0", message = "최소 판매 단가는 0 이상이어야 합니다")
            BigDecimal minSellingPrice,

            @DecimalMin(value = "0.0", message = "안전 재고 수량은 0 이상이어야 합니다")
            BigDecimal safetyStock,

            @DecimalMin(value = "0.0", message = "최소 재고 수량은 0 이상이어야 합니다")
            BigDecimal minStock,

            @DecimalMin(value = "0.0", message = "최대 재고 수량은 0 이상이어야 합니다")
            BigDecimal maxStock,

            @DecimalMin(value = "0.0", message = "재주문점은 0 이상이어야 합니다")
            BigDecimal reorderPoint,

            @DecimalMin(value = "0.0", message = "재주문 수량은 0 이상이어야 합니다")
            BigDecimal reorderQuantity,

            @Min(value = 0, message = "리드타임은 0 이상이어야 합니다")
            Integer leadTimeDays,

            @Min(value = 0, message = "유효기간은 0 이상이어야 합니다")
            Integer shelfLifeDays,

            // 규격 정보
            @DecimalMin(value = "0.0", message = "가로는 0 이상이어야 합니다")
            BigDecimal width,

            @DecimalMin(value = "0.0", message = "세로는 0 이상이어야 합니다")
            BigDecimal height,

            @DecimalMin(value = "0.0", message = "깊이는 0 이상이어야 합니다")
            BigDecimal depth,

            @DecimalMin(value = "0.0", message = "무게는 0 이상이어야 합니다")
            BigDecimal weight,

            @Size(max = 30, message = "색상은 30자를 초과할 수 없습니다")
            String color,

            @Size(max = 30, message = "크기는 30자를 초과할 수 없습니다")
            String size,

            // 업체 정보
            @Size(max = 50, message = "브랜드는 50자를 초과할 수 없습니다")
            String brand,

            @Size(max = 100, message = "제조사는 100자를 초과할 수 없습니다")
            String manufacturer,

            @Size(max = 100, message = "공급업체는 100자를 초과할 수 없습니다")
            String supplier,

            @Size(max = 50, message = "원산지는 50자를 초과할 수 없습니다")
            String originCountry,

            // 세무 정보
            @Size(max = 20, message = "HS 코드는 20자를 초과할 수 없습니다")
            String hsCode,

            @DecimalMin(value = "0.0", message = "세율은 0 이상이어야 합니다")
            @DecimalMax(value = "100.0", message = "세율은 100 이하여야 합니다")
            BigDecimal taxRate,

            // 기타
            String imagePaths,
            String attachmentPaths,
            String tags,
            Integer sortOrder,
            String metadata
    ) {
        public ProductCreateDto {
            // 기본값 설정
            if (productStatus == null) productStatus = Product.ProductStatus.ACTIVE;
            if (stockManagementType == null) stockManagementType = Product.StockManagementType.FIFO;
            if (isActive == null) isActive = true;
            if (trackInventory == null) trackInventory = true;
            if (unitConversionRate == null) unitConversionRate = BigDecimal.ONE;
            if (standardCost == null) standardCost = BigDecimal.ZERO;
            if (sellingPrice == null) sellingPrice = BigDecimal.ZERO;
            if (minSellingPrice == null) minSellingPrice = BigDecimal.ZERO;
            if (safetyStock == null) safetyStock = BigDecimal.ZERO;
            if (minStock == null) minStock = BigDecimal.ZERO;
            if (maxStock == null) maxStock = BigDecimal.ZERO;
            if (reorderPoint == null) reorderPoint = BigDecimal.ZERO;
            if (reorderQuantity == null) reorderQuantity = BigDecimal.ZERO;
            if (leadTimeDays == null) leadTimeDays = 0;
            if (taxRate == null) taxRate = BigDecimal.ZERO;
            if (sortOrder == null) sortOrder = 0;
        }
    }

    /**
     * 상품 수정 요청 DTO
     */
    public record ProductUpdateDto(
            @Size(max = 200, message = "상품명은 200자를 초과할 수 없습니다")
            String productName,

            @Size(max = 200, message = "영문 상품명은 200자를 초과할 수 없습니다")
            String productNameEn,

            @Size(max = 1000, message = "상품 설명은 1000자를 초과할 수 없습니다")
            String description,

            String detailedDescription,
            Long categoryId,
            Product.ProductType productType,
            Product.ProductStatus productStatus,
            Product.StockManagementType stockManagementType,
            Boolean isActive,
            Boolean trackInventory,

            @Size(max = 50, message = "바코드는 50자를 초과할 수 없습니다")
            String barcode,

            @Size(max = 200, message = "QR 코드는 200자를 초과할 수 없습니다")
            String qrCode,

            @Size(max = 50, message = "SKU는 50자를 초과할 수 없습니다")
            String sku,

            @Size(max = 10, message = "기본 단위는 10자를 초과할 수 없습니다")
            String baseUnit,

            @Size(max = 10, message = "보조 단위는 10자를 초과할 수 없습니다")
            String subUnit,

            @DecimalMin(value = "0.0", inclusive = false, message = "단위 변환 비율은 0보다 커야 합니다")
            BigDecimal unitConversionRate,

            @DecimalMin(value = "0.0", message = "표준 원가는 0 이상이어야 합니다")
            BigDecimal standardCost,

            @DecimalMin(value = "0.0", message = "판매 단가는 0 이상이어야 합니다")
            BigDecimal sellingPrice,

            @DecimalMin(value = "0.0", message = "최소 판매 단가는 0 이상이어야 합니다")
            BigDecimal minSellingPrice,

            @DecimalMin(value = "0.0", message = "안전 재고 수량은 0 이상이어야 합니다")
            BigDecimal safetyStock,

            @DecimalMin(value = "0.0", message = "최소 재고 수량은 0 이상이어야 합니다")
            BigDecimal minStock,

            @DecimalMin(value = "0.0", message = "최대 재고 수량은 0 이상이어야 합니다")
            BigDecimal maxStock,

            @DecimalMin(value = "0.0", message = "재주문점은 0 이상이어야 합니다")
            BigDecimal reorderPoint,

            @DecimalMin(value = "0.0", message = "재주문 수량은 0 이상이어야 합니다")
            BigDecimal reorderQuantity,

            @Min(value = 0, message = "리드타임은 0 이상이어야 합니다")
            Integer leadTimeDays,

            @Min(value = 0, message = "유효기간은 0 이상이어야 합니다")
            Integer shelfLifeDays,

            // 규격 정보
            @DecimalMin(value = "0.0", message = "가로는 0 이상이어야 합니다")
            BigDecimal width,

            @DecimalMin(value = "0.0", message = "세로는 0 이상이어야 합니다")
            BigDecimal height,

            @DecimalMin(value = "0.0", message = "깊이는 0 이상이어야 합니다")
            BigDecimal depth,

            @DecimalMin(value = "0.0", message = "무게는 0 이상이어야 합니다")
            BigDecimal weight,

            @Size(max = 30, message = "색상은 30자를 초과할 수 없습니다")
            String color,

            @Size(max = 30, message = "크기는 30자를 초과할 수 없습니다")
            String size,

            // 업체 정보
            @Size(max = 50, message = "브랜드는 50자를 초과할 수 없습니다")
            String brand,

            @Size(max = 100, message = "제조사는 100자를 초과할 수 없습니다")
            String manufacturer,

            @Size(max = 100, message = "공급업체는 100자를 초과할 수 없습니다")
            String supplier,

            @Size(max = 50, message = "원산지는 50자를 초과할 수 없습니다")
            String originCountry,

            // 세무 정보
            @Size(max = 20, message = "HS 코드는 20자를 초과할 수 없습니다")
            String hsCode,

            @DecimalMin(value = "0.0", message = "세율은 0 이상이어야 합니다")
            @DecimalMax(value = "100.0", message = "세율은 100 이하여야 합니다")
            BigDecimal taxRate,

            // 기타
            String imagePaths,
            String attachmentPaths,
            String tags,
            Integer sortOrder,
            String metadata
    ) {}

    /**
     * 상품 응답 DTO
     */
    public record ProductResponseDto(
            Long id,
            String productCode,
            String productName,
            String productNameEn,
            String description,
            String detailedDescription,
            Long companyId,
            String companyName,
            Long categoryId,
            String categoryName,
            String categoryFullPath,
            Product.ProductType productType,
            String productTypeDescription,
            Product.ProductStatus productStatus,
            String productStatusDescription,
            Product.StockManagementType stockManagementType,
            String stockManagementTypeDescription,
            Boolean isActive,
            Boolean trackInventory,
            String barcode,
            String qrCode,
            String sku,
            String baseUnit,
            String subUnit,
            BigDecimal unitConversionRate,
            BigDecimal standardCost,
            BigDecimal averageCost,
            BigDecimal lastPurchasePrice,
            BigDecimal sellingPrice,
            BigDecimal minSellingPrice,
            BigDecimal safetyStock,
            BigDecimal minStock,
            BigDecimal maxStock,
            BigDecimal reorderPoint,
            BigDecimal reorderQuantity,
            Integer leadTimeDays,
            Integer shelfLifeDays,
            BigDecimal width,
            BigDecimal height,
            BigDecimal depth,
            BigDecimal weight,
            BigDecimal volume,
            String color,
            String size,
            String brand,
            String manufacturer,
            String supplier,
            String originCountry,
            String hsCode,
            BigDecimal taxRate,
            String imagePaths,
            String attachmentPaths,
            String tags,
            Integer sortOrder,
            String metadata,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            
            // 재고 관련 정보
            BigDecimal totalStock,
            BigDecimal availableStock,
            BigDecimal reservedStock,
            Boolean isLowStock,
            Boolean isOutOfStock,
            Boolean isOverStock,
            Boolean needsReorder,
            String stockStatusSummary,
            BigDecimal profitRate
    ) {
        /**
         * Entity에서 DTO로 변환
         */
        public static ProductResponseDto from(Product product) {
            return new ProductResponseDto(
                    product.getId(),
                    product.getProductCode(),
                    product.getProductName(),
                    product.getProductNameEn(),
                    product.getDescription(),
                    product.getDetailedDescription(),
                    product.getCompany().getId(),
                    product.getCompany().getName(),
                    product.getCategory().getId(),
                    product.getCategory().getName(),
                    product.getCategory().getFullPath(),
                    product.getProductType(),
                    product.getProductType().getDescription(),
                    product.getProductStatus(),
                    product.getProductStatus().getDescription(),
                    product.getStockManagementType(),
                    product.getStockManagementType().getDescription(),
                    product.getIsActive(),
                    product.getTrackInventory(),
                    product.getBarcode(),
                    product.getQrCode(),
                    product.getSku(),
                    product.getBaseUnit(),
                    product.getSubUnit(),
                    product.getUnitConversionRate(),
                    product.getStandardCost(),
                    product.getAverageCost(),
                    product.getLastPurchasePrice(),
                    product.getSellingPrice(),
                    product.getMinSellingPrice(),
                    product.getSafetyStock(),
                    product.getMinStock(),
                    product.getMaxStock(),
                    product.getReorderPoint(),
                    product.getReorderQuantity(),
                    product.getLeadTimeDays(),
                    product.getShelfLifeDays(),
                    product.getWidth(),
                    product.getHeight(),
                    product.getDepth(),
                    product.getWeight(),
                    product.getVolume(),
                    product.getColor(),
                    product.getSize(),
                    product.getBrand(),
                    product.getManufacturer(),
                    product.getSupplier(),
                    product.getOriginCountry(),
                    product.getHsCode(),
                    product.getTaxRate(),
                    product.getImagePaths(),
                    product.getAttachmentPaths(),
                    product.getTags(),
                    product.getSortOrder(),
                    product.getMetadata(),
                    product.getCreatedAt(),
                    product.getUpdatedAt(),
                    product.getTotalStock(),
                    product.getAvailableStock(),
                    product.getReservedStock(),
                    product.isLowStock(),
                    product.isOutOfStock(),
                    product.isOverStock(),
                    product.needsReorder(),
                    product.getStockStatusSummary(),
                    product.calculateProfitRate()
            );
        }
    }

    /**
     * 상품 요약 DTO (목록용)
     */
    public record ProductSummaryDto(
            Long id,
            String productCode,
            String productName,
            String categoryName,
            Product.ProductType productType,
            String productTypeDescription,
            Product.ProductStatus productStatus,
            String productStatusDescription,
            Boolean isActive,
            String baseUnit,
            BigDecimal sellingPrice,
            BigDecimal totalStock,
            BigDecimal availableStock,
            Boolean isLowStock,
            Boolean isOutOfStock,
            String stockStatusSummary,
            String imagePaths
    ) {
        /**
         * Entity에서 요약 DTO로 변환
         */
        public static ProductSummaryDto from(Product product) {
            return new ProductSummaryDto(
                    product.getId(),
                    product.getProductCode(),
                    product.getProductName(),
                    product.getCategory().getName(),
                    product.getProductType(),
                    product.getProductType().getDescription(),
                    product.getProductStatus(),
                    product.getProductStatus().getDescription(),
                    product.getIsActive(),
                    product.getBaseUnit(),
                    product.getSellingPrice(),
                    product.getTotalStock(),
                    product.getAvailableStock(),
                    product.isLowStock(),
                    product.isOutOfStock(),
                    product.getStockStatusSummary(),
                    product.getImagePaths()
            );
        }
    }

    /**
     * 상품 재고 현황 DTO
     */
    public record ProductStockDto(
            Long productId,
            String productCode,
            String productName,
            String categoryName,
            String baseUnit,
            BigDecimal totalStock,
            BigDecimal availableStock,
            BigDecimal reservedStock,
            BigDecimal safetyStock,
            BigDecimal minStock,
            BigDecimal maxStock,
            BigDecimal reorderPoint,
            Boolean isLowStock,
            Boolean isOutOfStock,
            Boolean isOverStock,
            Boolean needsReorder,
            String stockStatusSummary,
            BigDecimal totalStockValue,
            List<WarehouseStockDto> warehouseStocks
    ) {
        /**
         * 창고별 재고 정보 DTO
         */
        public record WarehouseStockDto(
                Long warehouseId,
                String warehouseName,
                String locationCode,
                Double currentStock,
                Double availableStock,
                Double reservedStock,
                BigDecimal averageCost,
                BigDecimal totalValue
        ) {}
    }

    /**
     * 상품 통계 DTO
     */
    public record ProductStatsDto(
            Long totalProducts,
            Long activeProducts,
            Long inactiveProducts,
            Long lowStockProducts,
            Long outOfStockProducts,
            Long overStockProducts,
            Long reorderNeededProducts,
            BigDecimal totalStockValue,
            BigDecimal averageStockValue
    ) {}

    /**
     * 상품 검색 조건 DTO
     */
    public record ProductSearchDto(
            String searchTerm,
            Long categoryId,
            Product.ProductType productType,
            Product.ProductStatus productStatus,
            Boolean isActive,
            Boolean trackInventory,
            Boolean isLowStock,
            Boolean isOutOfStock,
            Boolean needsReorder,
            String brand,
            String manufacturer,
            String supplier,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sortBy,
            String sortDirection
    ) {}
}
