package com.erp.inventory.dto;

import com.erp.inventory.entity.StockMovement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 재고이동 DTO 클래스들
 */
public class StockMovementDto {

    /**
     * 재고이동 생성 요청 DTO
     */
    public record StockMovementCreateDto(
            @NotBlank(message = "이동 번호는 필수입니다")
            @Size(max = 30, message = "이동 번호는 30자를 초과할 수 없습니다")
            String movementNumber,

            @NotNull(message = "회사 ID는 필수입니다")
            Long companyId,

            @NotNull(message = "상품 ID는 필수입니다")
            Long productId,

            @NotNull(message = "창고 ID는 필수입니다")
            Long warehouseId,

            Long inventoryId,

            @NotNull(message = "이동 유형은 필수입니다")
            StockMovement.MovementType movementType,

            StockMovement.MovementStatus movementStatus,

            @NotNull(message = "이동 일시는 필수입니다")
            LocalDateTime movementDate,

            @NotNull(message = "수량은 필수입니다")
            @DecimalMin(value = "0.0", inclusive = false, message = "수량은 0보다 커야 합니다")
            Double quantity,

            @NotBlank(message = "단위는 필수입니다")
            @Size(max = 10, message = "단위는 10자를 초과할 수 없습니다")
            String unit,

            @DecimalMin(value = "0.0", message = "단가는 0 이상이어야 합니다")
            BigDecimal unitPrice,

            // 이동 관련
            Long fromWarehouseId,
            String fromLocation,
            Long toWarehouseId,
            String toLocation,

            // 참조 정보
            @Size(max = 50, message = "참조 번호는 50자를 초과할 수 없습니다")
            String referenceNumber,

            @Size(max = 20, message = "참조 유형은 20자를 초과할 수 없습니다")
            String referenceType,

            @Size(max = 100, message = "거래처 정보는 100자를 초과할 수 없습니다")
            String businessPartner,

            // 추적 정보
            @Size(max = 50, message = "로트 번호는 50자를 초과할 수 없습니다")
            String lotNumber,

            @Size(max = 50, message = "시리얼 번호는 50자를 초과할 수 없습니다")
            String serialNumber,

            LocalDateTime expiryDate,
            LocalDateTime manufactureDate,

            // 설명
            @Size(max = 200, message = "이동 사유는 200자를 초과할 수 없습니다")
            String reason,

            @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
            String description,

            @Size(max = 500, message = "첨부파일 경로는 500자를 초과할 수 없습니다")
            String attachmentPath,

            String metadata
    ) {
        public StockMovementCreateDto {
            // 기본값 설정
            if (movementStatus == null) movementStatus = StockMovement.MovementStatus.DRAFT;
            if (unitPrice == null) unitPrice = BigDecimal.ZERO;
        }
    }

    /**
     * 재고이동 수정 요청 DTO
     */
    public record StockMovementUpdateDto(
            LocalDateTime movementDate,

            @DecimalMin(value = "0.0", inclusive = false, message = "수량은 0보다 커야 합니다")
            Double quantity,

            @Size(max = 10, message = "단위는 10자를 초과할 수 없습니다")
            String unit,

            @DecimalMin(value = "0.0", message = "단가는 0 이상이어야 합니다")
            BigDecimal unitPrice,

            // 이동 관련
            Long fromWarehouseId,
            String fromLocation,
            Long toWarehouseId,
            String toLocation,

            // 참조 정보
            @Size(max = 50, message = "참조 번호는 50자를 초과할 수 없습니다")
            String referenceNumber,

            @Size(max = 20, message = "참조 유형은 20자를 초과할 수 없습니다")
            String referenceType,

            @Size(max = 100, message = "거래처 정보는 100자를 초과할 수 없습니다")
            String businessPartner,

            // 추적 정보
            @Size(max = 50, message = "로트 번호는 50자를 초과할 수 없습니다")
            String lotNumber,

            @Size(max = 50, message = "시리얼 번호는 50자를 초과할 수 없습니다")
            String serialNumber,

            LocalDateTime expiryDate,
            LocalDateTime manufactureDate,

            // 설명
            @Size(max = 200, message = "이동 사유는 200자를 초과할 수 없습니다")
            String reason,

            @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
            String description,

            @Size(max = 500, message = "첨부파일 경로는 500자를 초과할 수 없습니다")
            String attachmentPath,

            String metadata
    ) {}

    /**
     * 재고이동 응답 DTO
     */
    public record StockMovementResponseDto(
            Long id,
            String movementNumber,
            Long companyId,
            String companyName,
            Long productId,
            String productCode,
            String productName,
            String categoryName,
            Long warehouseId,
            String warehouseName,
            Long inventoryId,
            StockMovement.MovementType movementType,
            String movementTypeDescription,
            StockMovement.MovementStatus movementStatus,
            String movementStatusDescription,
            LocalDateTime movementDate,
            Double quantity,
            String unit,
            BigDecimal unitPrice,
            BigDecimal totalAmount,
            Double beforeStock,
            Double afterStock,
            Long fromWarehouseId,
            String fromWarehouseName,
            String fromLocation,
            String fromLocationInfo,
            Long toWarehouseId,
            String toWarehouseName,
            String toLocation,
            String toLocationInfo,
            String referenceNumber,
            String referenceType,
            String businessPartner,
            String lotNumber,
            String serialNumber,
            LocalDateTime expiryDate,
            LocalDateTime manufactureDate,
            String reason,
            String description,
            Long processedById,
            String processedByName,
            LocalDateTime processedAt,
            Long approvedById,
            String approvedByName,
            LocalDateTime approvedAt,
            String cancelReason,
            LocalDateTime cancelledAt,
            String attachmentPath,
            String metadata,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            
            // 계산 필드들
            Boolean isInbound,
            Boolean isOutbound,
            Boolean isTransfer,
            Boolean isProcessed,
            Boolean isApproved,
            Boolean isCancellable,
            Double stockChange,
            String movementDirection,
            String movementSummary,
            String statusColorClass
    ) {
        /**
         * Entity에서 DTO로 변환
         */
        public static StockMovementResponseDto from(StockMovement movement) {
            return new StockMovementResponseDto(
                    movement.getId(),
                    movement.getMovementNumber(),
                    movement.getCompany().getId(),
                    movement.getCompany().getName(),
                    movement.getProduct().getId(),
                    movement.getProduct().getProductCode(),
                    movement.getProduct().getProductName(),
                    movement.getProduct().getCategory().getName(),
                    movement.getWarehouse().getId(),
                    movement.getWarehouse().getWarehouseName(),
                    movement.getInventory() != null ? movement.getInventory().getId() : null,
                    movement.getMovementType(),
                    movement.getMovementType().getDescription(),
                    movement.getMovementStatus(),
                    movement.getMovementStatus().getDescription(),
                    movement.getMovementDate(),
                    movement.getQuantity(),
                    movement.getUnit(),
                    movement.getUnitPrice(),
                    movement.getTotalAmount(),
                    movement.getBeforeStock(),
                    movement.getAfterStock(),
                    movement.getFromWarehouse() != null ? movement.getFromWarehouse().getId() : null,
                    movement.getFromWarehouse() != null ? movement.getFromWarehouse().getWarehouseName() : null,
                    movement.getFromLocation(),
                    movement.getFromLocationInfo(),
                    movement.getToWarehouse() != null ? movement.getToWarehouse().getId() : null,
                    movement.getToWarehouse() != null ? movement.getToWarehouse().getWarehouseName() : null,
                    movement.getToLocation(),
                    movement.getToLocationInfo(),
                    movement.getReferenceNumber(),
                    movement.getReferenceType(),
                    movement.getBusinessPartner(),
                    movement.getLotNumber(),
                    movement.getSerialNumber(),
                    movement.getExpiryDate(),
                    movement.getManufactureDate(),
                    movement.getReason(),
                    movement.getDescription(),
                    movement.getProcessedBy() != null ? movement.getProcessedBy().getId() : null,
                    movement.getProcessedBy() != null ? movement.getProcessedBy().getFullName() : null,
                    movement.getProcessedAt(),
                    movement.getApprovedBy() != null ? movement.getApprovedBy().getId() : null,
                    movement.getApprovedBy() != null ? movement.getApprovedBy().getFullName() : null,
                    movement.getApprovedAt(),
                    movement.getCancelReason(),
                    movement.getCancelledAt(),
                    movement.getAttachmentPath(),
                    movement.getMetadata(),
                    movement.getCreatedAt(),
                    movement.getUpdatedAt(),
                    movement.isInbound(),
                    movement.isOutbound(),
                    movement.isTransfer(),
                    movement.isProcessed(),
                    movement.isApproved(),
                    movement.isCancellable(),
                    movement.getStockChange(),
                    movement.getMovementDirection(),
                    movement.getMovementSummary(),
                    movement.getStatusColorClass()
            );
        }
    }

    /**
     * 재고이동 요약 DTO (목록용)
     */
    public record StockMovementSummaryDto(
            Long id,
            String movementNumber,
            StockMovement.MovementType movementType,
            String movementTypeDescription,
            StockMovement.MovementStatus movementStatus,
            String movementStatusDescription,
            LocalDateTime movementDate,
            String productCode,
            String productName,
            String warehouseName,
            Double quantity,
            String unit,
            BigDecimal totalAmount,
            String referenceNumber,
            String businessPartner,
            String processedByName,
            LocalDateTime processedAt,
            Boolean isInbound,
            Boolean isOutbound,
            String movementDirection,
            String statusColorClass
    ) {
        /**
         * Entity에서 요약 DTO로 변환
         */
        public static StockMovementSummaryDto from(StockMovement movement) {
            return new StockMovementSummaryDto(
                    movement.getId(),
                    movement.getMovementNumber(),
                    movement.getMovementType(),
                    movement.getMovementType().getDescription(),
                    movement.getMovementStatus(),
                    movement.getMovementStatus().getDescription(),
                    movement.getMovementDate(),
                    movement.getProduct().getProductCode(),
                    movement.getProduct().getProductName(),
                    movement.getWarehouse().getWarehouseName(),
                    movement.getQuantity(),
                    movement.getUnit(),
                    movement.getTotalAmount(),
                    movement.getReferenceNumber(),
                    movement.getBusinessPartner(),
                    movement.getProcessedBy() != null ? movement.getProcessedBy().getFullName() : null,
                    movement.getProcessedAt(),
                    movement.isInbound(),
                    movement.isOutbound(),
                    movement.getMovementDirection(),
                    movement.getStatusColorClass()
            );
        }
    }

    /**
     * 재고이동 승인 요청 DTO
     */
    public record MovementApprovalDto(
            @NotNull(message = "재고이동 ID는 필수입니다")
            Long movementId,

            @NotNull(message = "승인자 ID는 필수입니다")
            Long approverId,

            @Size(max = 200, message = "승인 의견은 200자를 초과할 수 없습니다")
            String approvalComment
    ) {}

    /**
     * 재고이동 처리 요청 DTO
     */
    public record MovementProcessDto(
            @NotNull(message = "재고이동 ID는 필수입니다")
            Long movementId,

            @NotNull(message = "처리자 ID는 필수입니다")
            Long processorId,

            @Size(max = 200, message = "처리 의견은 200자를 초과할 수 없습니다")
            String processComment
    ) {}

    /**
     * 재고이동 취소 요청 DTO
     */
    public record MovementCancelDto(
            @NotNull(message = "재고이동 ID는 필수입니다")
            Long movementId,

            @NotBlank(message = "취소 사유는 필수입니다")
            @Size(max = 200, message = "취소 사유는 200자를 초과할 수 없습니다")
            String cancelReason
    ) {}

    /**
     * 대량 재고이동 요청 DTO
     */
    public record BulkMovementDto(
            @NotNull(message = "회사 ID는 필수입니다")
            Long companyId,

            @NotNull(message = "이동 유형은 필수입니다")
            StockMovement.MovementType movementType,

            @NotNull(message = "이동 일시는 필수입니다")
            LocalDateTime movementDate,

            @Size(max = 50, message = "참조 번호는 50자를 초과할 수 없습니다")
            String referenceNumber,

            @Size(max = 20, message = "참조 유형은 20자를 초과할 수 없습니다")
            String referenceType,

            @Size(max = 100, message = "거래처 정보는 100자를 초과할 수 없습니다")
            String businessPartner,

            @Size(max = 200, message = "이동 사유는 200자를 초과할 수 없습니다")
            String reason,

            @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
            String description,

            @NotNull(message = "이동 항목 목록은 필수입니다")
            List<BulkMovementItemDto> items
    ) {
        /**
         * 대량 이동 항목 DTO
         */
        public record BulkMovementItemDto(
                @NotNull(message = "상품 ID는 필수입니다")
                Long productId,

                @NotNull(message = "창고 ID는 필수입니다")
                Long warehouseId,

                Long inventoryId,

                @NotNull(message = "수량은 필수입니다")
                @DecimalMin(value = "0.0", inclusive = false, message = "수량은 0보다 커야 합니다")
                Double quantity,

                @DecimalMin(value = "0.0", message = "단가는 0 이상이어야 합니다")
                BigDecimal unitPrice,

                String lotNumber,
                String serialNumber,
                LocalDateTime expiryDate,
                LocalDateTime manufactureDate,
                String remarks
        ) {}
    }

    /**
     * 재고이동 통계 DTO
     */
    public record MovementStatsDto(
            LocalDateTime periodStart,
            LocalDateTime periodEnd,
            Long totalMovements,
            Long inboundMovements,
            Long outboundMovements,
            Long transferMovements,
            Double totalInboundQuantity,
            Double totalOutboundQuantity,
            BigDecimal totalInboundValue,
            BigDecimal totalOutboundValue,
            Long pendingApprovals,
            Long processingErrors,
            List<MovementTypeStatsDto> movementTypeStats,
            List<ProductMovementStatsDto> topMovedProducts
    ) {
        /**
         * 이동 유형별 통계 DTO
         */
        public record MovementTypeStatsDto(
                StockMovement.MovementType movementType,
                String movementTypeDescription,
                Long count,
                Double totalQuantity,
                BigDecimal totalValue
        ) {}

        /**
         * 상품별 이동 통계 DTO
         */
        public record ProductMovementStatsDto(
                Long productId,
                String productCode,
                String productName,
                Long movementCount,
                Double totalQuantity,
                BigDecimal totalValue
        ) {}
    }

    /**
     * 재고이동 검색 조건 DTO
     */
    public record MovementSearchDto(
            String searchTerm,
            Long productId,
            Long warehouseId,
            StockMovement.MovementType movementType,
            StockMovement.MovementStatus movementStatus,
            LocalDateTime movementDateFrom,
            LocalDateTime movementDateTo,
            String referenceNumber,
            String referenceType,
            String businessPartner,
            String lotNumber,
            String serialNumber,
            Long processedById,
            Long approvedById,
            Boolean isInbound,
            Boolean isOutbound,
            Boolean isTransfer,
            String sortBy,
            String sortDirection
    ) {}

    /**
     * 재고이동 리포트 DTO
     */
    public record MovementReportDto(
            String reportTitle,
            LocalDateTime reportDate,
            LocalDateTime periodStart,
            LocalDateTime periodEnd,
            Long companyId,
            String companyName,
            MovementStatsDto summary,
            List<StockMovementSummaryDto> movements,
            List<MovementAnalysisDto> analysis
    ) {
        /**
         * 이동 분석 DTO
         */
        public record MovementAnalysisDto(
                String analysisType,
                String description,
                Object data
        ) {}
    }
}
