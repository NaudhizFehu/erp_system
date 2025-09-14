package com.erp.inventory.dto;

import com.erp.inventory.entity.Inventory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 재고 DTO 클래스들
 */
public class InventoryDto {

    /**
     * 재고 생성 요청 DTO
     */
    public record InventoryCreateDto(
            @NotNull(message = "회사 ID는 필수입니다")
            Long companyId,

            @NotNull(message = "상품 ID는 필수입니다")
            Long productId,

            @NotNull(message = "창고 ID는 필수입니다")
            Long warehouseId,

            String locationCode,
            String locationDescription,

            @DecimalMin(value = "0.0", message = "현재 재고 수량은 0 이상이어야 합니다")
            Double currentStock,

            @DecimalMin(value = "0.0", message = "예약된 재고 수량은 0 이상이어야 합니다")
            Double reservedStock,

            @DecimalMin(value = "0.0", message = "안전 재고 수량은 0 이상이어야 합니다")
            Double safetyStock,

            @DecimalMin(value = "0.0", message = "최소 재고 수량은 0 이상이어야 합니다")
            Double minStock,

            @DecimalMin(value = "0.0", message = "최대 재고 수량은 0 이상이어야 합니다")
            Double maxStock,

            @DecimalMin(value = "0.0", message = "재주문점은 0 이상이어야 합니다")
            Double reorderPoint,

            @DecimalMin(value = "0.0", message = "재주문 수량은 0 이상이어야 합니다")
            Double reorderQuantity,

            @DecimalMin(value = "0.0", message = "평균 원가는 0 이상이어야 합니다")
            BigDecimal averageCost,

            Inventory.StockGrade stockGrade,
            Double temperature,
            Double humidity,
            LocalDateTime expiryDate,
            LocalDateTime manufactureDate,
            String lotNumber,
            String serialNumber,
            String supplierInfo,
            String remarks,
            String metadata
    ) {
        public InventoryCreateDto {
            // 기본값 설정
            if (locationCode == null) locationCode = "DEFAULT";
            if (currentStock == null) currentStock = 0.0;
            if (reservedStock == null) reservedStock = 0.0;
            if (averageCost == null) averageCost = BigDecimal.ZERO;
            if (stockGrade == null) stockGrade = Inventory.StockGrade.A;
        }
    }

    /**
     * 재고 수정 요청 DTO
     */
    public record InventoryUpdateDto(
            String locationCode,
            String locationDescription,

            @DecimalMin(value = "0.0", message = "예약된 재고 수량은 0 이상이어야 합니다")
            Double reservedStock,

            @DecimalMin(value = "0.0", message = "안전 재고 수량은 0 이상이어야 합니다")
            Double safetyStock,

            @DecimalMin(value = "0.0", message = "최소 재고 수량은 0 이상이어야 합니다")
            Double minStock,

            @DecimalMin(value = "0.0", message = "최대 재고 수량은 0 이상이어야 합니다")
            Double maxStock,

            @DecimalMin(value = "0.0", message = "재주문점은 0 이상이어야 합니다")
            Double reorderPoint,

            @DecimalMin(value = "0.0", message = "재주문 수량은 0 이상이어야 합니다")
            Double reorderQuantity,

            Inventory.StockGrade stockGrade,
            Double temperature,
            Double humidity,
            LocalDateTime expiryDate,
            LocalDateTime manufactureDate,
            String lotNumber,
            String serialNumber,
            String supplierInfo,
            String remarks,
            String metadata
    ) {}

    /**
     * 재고 응답 DTO
     */
    public record InventoryResponseDto(
            Long id,
            Long companyId,
            String companyName,
            Long productId,
            String productCode,
            String productName,
            String categoryName,
            Long warehouseId,
            String warehouseName,
            String locationCode,
            String locationDescription,
            String fullLocation,
            Double currentStock,
            Double availableStock,
            Double reservedStock,
            Double orderedStock,
            Double defectiveStock,
            Double quarantineStock,
            Double safetyStock,
            Double minStock,
            Double maxStock,
            Double reorderPoint,
            Double reorderQuantity,
            BigDecimal averageCost,
            BigDecimal lastPurchasePrice,
            BigDecimal totalStockValue,
            Inventory.StockStatus stockStatus,
            String stockStatusDescription,
            Inventory.StockGrade stockGrade,
            String stockGradeDescription,
            Boolean isLowStock,
            Boolean isOutOfStock,
            Boolean isOverStock,
            Boolean needsReorder,
            LocalDateTime lastReceiptDate,
            LocalDateTime lastIssueDate,
            LocalDateTime lastStocktakingDate,
            LocalDateTime lastStockUpdate,
            Integer movementCount,
            Double temperature,
            Double humidity,
            LocalDateTime expiryDate,
            LocalDateTime manufactureDate,
            String lotNumber,
            String serialNumber,
            String supplierInfo,
            String remarks,
            String metadata,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            
            // 계산 필드들
            Double usageRate,
            Integer daysInStock,
            Boolean isExpiringSoon,
            Boolean isExpired,
            Double stockTurnoverRate
    ) {
        /**
         * Entity에서 DTO로 변환
         */
        public static InventoryResponseDto from(Inventory inventory) {
            return new InventoryResponseDto(
                    inventory.getId(),
                    inventory.getCompany().getId(),
                    inventory.getCompany().getName(),
                    inventory.getProduct().getId(),
                    inventory.getProduct().getProductCode(),
                    inventory.getProduct().getProductName(),
                    inventory.getProduct().getCategory().getName(),
                    inventory.getWarehouse().getId(),
                    inventory.getWarehouse().getWarehouseName(),
                    inventory.getLocationCode(),
                    inventory.getLocationDescription(),
                    inventory.getFullLocation(),
                    inventory.getCurrentStock(),
                    inventory.getAvailableStock(),
                    inventory.getReservedStock(),
                    inventory.getOrderedStock(),
                    inventory.getDefectiveStock(),
                    inventory.getQuarantineStock(),
                    inventory.getSafetyStock(),
                    inventory.getMinStock(),
                    inventory.getMaxStock(),
                    inventory.getReorderPoint(),
                    inventory.getReorderQuantity(),
                    inventory.getAverageCost(),
                    inventory.getLastPurchasePrice(),
                    inventory.getTotalStockValue(),
                    inventory.getStockStatus(),
                    inventory.getStockStatus().getDescription(),
                    inventory.getStockGrade(),
                    inventory.getStockGrade().getDescription(),
                    inventory.getIsLowStock(),
                    inventory.getIsOutOfStock(),
                    inventory.getIsOverStock(),
                    inventory.getNeedsReorder(),
                    inventory.getLastReceiptDate(),
                    inventory.getLastIssueDate(),
                    inventory.getLastStocktakingDate(),
                    inventory.getLastStockUpdate(),
                    inventory.getMovementCount(),
                    inventory.getTemperature(),
                    inventory.getHumidity(),
                    inventory.getExpiryDate(),
                    inventory.getManufactureDate(),
                    inventory.getLotNumber(),
                    inventory.getSerialNumber(),
                    inventory.getSupplierInfo(),
                    inventory.getRemarks(),
                    inventory.getMetadata(),
                    inventory.getCreatedAt(),
                    inventory.getUpdatedAt(),
                    null, // usageRate - 창고에서 계산
                    inventory.calculateDaysInStock(),
                    inventory.isExpiringsoon(),
                    inventory.isExpired(),
                    null  // stockTurnoverRate - 별도 계산 필요
            );
        }
    }

    /**
     * 재고 요약 DTO (목록용)
     */
    public record InventorySummaryDto(
            Long id,
            String productCode,
            String productName,
            String categoryName,
            String warehouseName,
            String locationCode,
            Double currentStock,
            Double availableStock,
            Double reservedStock,
            String baseUnit,
            Inventory.StockStatus stockStatus,
            String stockStatusDescription,
            Boolean isLowStock,
            Boolean isOutOfStock,
            Boolean needsReorder,
            BigDecimal totalStockValue,
            LocalDateTime lastStockUpdate
    ) {
        /**
         * Entity에서 요약 DTO로 변환
         */
        public static InventorySummaryDto from(Inventory inventory) {
            return new InventorySummaryDto(
                    inventory.getId(),
                    inventory.getProduct().getProductCode(),
                    inventory.getProduct().getProductName(),
                    inventory.getProduct().getCategory().getName(),
                    inventory.getWarehouse().getWarehouseName(),
                    inventory.getLocationCode(),
                    inventory.getCurrentStock(),
                    inventory.getAvailableStock(),
                    inventory.getReservedStock(),
                    inventory.getProduct().getBaseUnit(),
                    inventory.getStockStatus(),
                    inventory.getStockStatus().getDescription(),
                    inventory.getIsLowStock(),
                    inventory.getIsOutOfStock(),
                    inventory.getNeedsReorder(),
                    inventory.getTotalStockValue(),
                    inventory.getLastStockUpdate()
            );
        }
    }

    /**
     * 재고 실사 요청 DTO
     */
    public record StocktakingRequestDto(
            @NotNull(message = "재고 ID는 필수입니다")
            Long inventoryId,

            @NotNull(message = "실사 수량은 필수입니다")
            @DecimalMin(value = "0.0", message = "실사 수량은 0 이상이어야 합니다")
            Double actualQuantity,

            @Size(max = 200, message = "실사 사유는 200자를 초과할 수 없습니다")
            String reason,

            @Size(max = 500, message = "비고는 500자를 초과할 수 없습니다")
            String remarks,

            LocalDateTime stocktakingDate
    ) {
        public StocktakingRequestDto {
            if (stocktakingDate == null) stocktakingDate = LocalDateTime.now();
        }
    }

    /**
     * 재고 실사 결과 DTO
     */
    public record StocktakingResultDto(
            Long inventoryId,
            String productCode,
            String productName,
            String warehouseName,
            String locationCode,
            Double systemStock,
            Double actualStock,
            Double differenceQuantity,
            BigDecimal differenceValue,
            String reason,
            String remarks,
            LocalDateTime stocktakingDate,
            String processorName
    ) {}

    /**
     * 재고 조정 요청 DTO
     */
    public record InventoryAdjustmentDto(
            @NotNull(message = "재고 ID는 필수입니다")
            Long inventoryId,

            @NotNull(message = "조정 수량은 필수입니다")
            Double adjustmentQuantity,

            @NotBlank(message = "조정 사유는 필수입니다")
            @Size(max = 200, message = "조정 사유는 200자를 초과할 수 없습니다")
            String reason,

            @Size(max = 500, message = "비고는 500자를 초과할 수 없습니다")
            String remarks,

            BigDecimal unitCost
    ) {}

    /**
     * 재고 예약 요청 DTO
     */
    public record StockReservationDto(
            @NotNull(message = "재고 ID는 필수입니다")
            Long inventoryId,

            @NotNull(message = "예약 수량은 필수입니다")
            @DecimalMin(value = "0.0", inclusive = false, message = "예약 수량은 0보다 커야 합니다")
            Double reservationQuantity,

            @Size(max = 200, message = "예약 사유는 200자를 초과할 수 없습니다")
            String reason,

            @Size(max = 50, message = "참조 번호는 50자를 초과할 수 없습니다")
            String referenceNumber,

            LocalDateTime reservationDate,
            LocalDateTime expiryDate
    ) {
        public StockReservationDto {
            if (reservationDate == null) reservationDate = LocalDateTime.now();
        }
    }

    /**
     * 재고 이동 요청 DTO
     */
    public record InventoryTransferDto(
            @NotNull(message = "재고 ID는 필수입니다")
            Long fromInventoryId,

            @NotNull(message = "도착지 창고 ID는 필수입니다")
            Long toWarehouseId,

            String toLocationCode,

            @NotNull(message = "이동 수량은 필수입니다")
            @DecimalMin(value = "0.0", inclusive = false, message = "이동 수량은 0보다 커야 합니다")
            Double transferQuantity,

            @Size(max = 200, message = "이동 사유는 200자를 초과할 수 없습니다")
            String reason,

            @Size(max = 500, message = "비고는 500자를 초과할 수 없습니다")
            String remarks,

            LocalDateTime transferDate
    ) {
        public InventoryTransferDto {
            if (toLocationCode == null) toLocationCode = "DEFAULT";
            if (transferDate == null) transferDate = LocalDateTime.now();
        }
    }

    /**
     * 재고 통계 DTO
     */
    public record InventoryStatsDto(
            Long totalInventoryItems,
            Long lowStockItems,
            Long outOfStockItems,
            Long overStockItems,
            Long reorderNeededItems,
            Long expiringSoonItems,
            Long expiredItems,
            BigDecimal totalStockValue,
            BigDecimal lowStockValue,
            BigDecimal averageStockValue,
            Double averageStockTurnover
    ) {}

    /**
     * 창고별 재고 현황 DTO
     */
    public record WarehouseInventoryDto(
            Long warehouseId,
            String warehouseName,
            Long totalItems,
            Double totalQuantity,
            BigDecimal totalValue,
            Long lowStockItems,
            Long outOfStockItems,
            Double utilizationRate,
            List<InventorySummaryDto> topItems
    ) {}

    /**
     * 재고 검색 조건 DTO
     */
    public record InventorySearchDto(
            String searchTerm,
            Long categoryId,
            Long warehouseId,
            Inventory.StockStatus stockStatus,
            Inventory.StockGrade stockGrade,
            Boolean isLowStock,
            Boolean isOutOfStock,
            Boolean isOverStock,
            Boolean needsReorder,
            Boolean isExpiringSoon,
            Boolean isExpired,
            LocalDateTime expiryDateFrom,
            LocalDateTime expiryDateTo,
            String lotNumber,
            String serialNumber,
            String sortBy,
            String sortDirection
    ) {}

    /**
     * ABC 분석 결과 DTO
     */
    public record ABCAnalysisDto(
            Long productId,
            String productCode,
            String productName,
            BigDecimal annualUsageValue,
            Double cumulativePercentage,
            String abcClass,
            String recommendation
    ) {}

    /**
     * 입고 처리 DTO
     */
    public record StockInDto(
            @NotNull(message = "입고 수량은 필수입니다")
            @DecimalMin(value = "0.01", message = "입고 수량은 0보다 커야 합니다")
            Double quantity,
            
            @DecimalMin(value = "0.0", message = "단가는 0 이상이어야 합니다")
            BigDecimal unitCost,
            
            String lotNumber,
            String serialNumber,
            LocalDate expiryDate,
            String reason,
            String remarks
    ) {
        public StockInDto {
            if (reason == null || reason.trim().isEmpty()) {
                reason = "입고";
            }
        }
    }

    /**
     * 출고 처리 DTO
     */
    public record StockOutDto(
            @NotNull(message = "출고 수량은 필수입니다")
            @DecimalMin(value = "0.01", message = "출고 수량은 0보다 커야 합니다")
            Double quantity,
            
            String reason,
            String remarks,
            String referenceNumber
    ) {
        public StockOutDto {
            if (reason == null || reason.trim().isEmpty()) {
                reason = "출고";
            }
        }
    }

    /**
     * 재고 조정 DTO
     */
    public record StockAdjustmentDto(
            @NotNull(message = "조정 수량은 필수입니다")
            Double adjustmentQuantity,
            
            @NotNull(message = "조정 사유는 필수입니다")
            String reason,
            
            String remarks
    ) {}

    /**
     * 재고 이동 DTO
     */
    public record StockTransferDto(
            @NotNull(message = "이동 수량은 필수입니다")
            @DecimalMin(value = "0.01", message = "이동 수량은 0보다 커야 합니다")
            Double quantity,
            
            String reason,
            String remarks
    ) {
        public StockTransferDto {
            if (reason == null || reason.trim().isEmpty()) {
                reason = "재고 이동";
            }
        }
    }

    /**
     * 실사 요청 DTO
     */
    public record PhysicalInventoryDto(
            @NotNull(message = "실사 날짜는 필수입니다")
            LocalDate inventoryDate,
            
            String description,
            String remarks,
            List<Long> warehouseIds,
            List<Long> productIds
    ) {}

    /**
     * 실사 결과 DTO
     */
    public record PhysicalInventoryResultDto(
            Long inventoryId,
            Long productId,
            String productCode,
            String productName,
            Long warehouseId,
            String warehouseName,
            Double systemQuantity,
            Double actualQuantity,
            Double differenceQuantity,
            BigDecimal unitCost,
            BigDecimal totalDifference,
            String remarks
    ) {}

    /**
     * 장기 체류 재고 DTO
     */
    public record SlowMovingInventoryDto(
            Long inventoryId,
            Long productId,
            String productCode,
            String productName,
            Long warehouseId,
            String warehouseName,
            Double currentStock,
            LocalDateTime lastMovementDate,
            Integer daysSinceLastMovement,
            BigDecimal stockValue,
            String recommendation
    ) {}
}
