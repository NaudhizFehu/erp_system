package com.erp.inventory.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import com.erp.common.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 재고이동 엔티티
 * 재고의 입출고 및 이동 내역을 관리합니다
 */
@Entity
@Table(
    name = "stock_movements",
    indexes = {
        @Index(name = "idx_stock_movement_company", columnList = "company_id"),
        @Index(name = "idx_stock_movement_product", columnList = "product_id"),
        @Index(name = "idx_stock_movement_warehouse", columnList = "warehouse_id"),
        @Index(name = "idx_stock_movement_inventory", columnList = "inventory_id"),
        @Index(name = "idx_stock_movement_type", columnList = "company_id, movement_type"),
        @Index(name = "idx_stock_movement_date", columnList = "company_id, movement_date"),
        @Index(name = "idx_stock_movement_reference", columnList = "reference_number"),
        @Index(name = "idx_stock_movement_user", columnList = "processed_by_id"),
        @Index(name = "idx_stock_movement_status", columnList = "company_id, movement_status")
    }
)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"product", "warehouse", "inventory", "processedBy", "approvedBy"})
@ToString(exclude = {"product", "warehouse", "inventory", "processedBy", "approvedBy"})
public class StockMovement extends BaseEntity {

    /**
     * 재고이동 유형 열거형
     */
    public enum MovementType {
        // 입고 관련
        RECEIPT("입고"),
        PURCHASE_RECEIPT("매입입고"),
        PRODUCTION_RECEIPT("생산입고"),
        RETURN_RECEIPT("반품입고"),
        TRANSFER_IN("이고입고"),
        ADJUSTMENT_IN("조정입고"),
        
        // 출고 관련
        ISSUE("출고"),
        SALES_ISSUE("매출출고"),
        PRODUCTION_ISSUE("생산출고"),
        RETURN_ISSUE("반품출고"),
        TRANSFER_OUT("이고출고"),
        ADJUSTMENT_OUT("조정출고"),
        DISPOSAL("폐기"),
        
        // 이동 관련
        WAREHOUSE_TRANSFER("창고간이동"),
        LOCATION_TRANSFER("위치이동"),
        
        // 실사 관련
        STOCKTAKING_INCREASE("실사증가"),
        STOCKTAKING_DECREASE("실사감소"),
        
        // 상태 변경
        RESERVE("예약"),
        UNRESERVE("예약해제"),
        QUARANTINE("격리"),
        UNQUARANTINE("격리해제"),
        DEFECTIVE("불량처리"),
        REPAIR("수리완료");

        private final String description;

        MovementType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 입고 유형 여부
         */
        public boolean isInbound() {
            return this == RECEIPT || this == PURCHASE_RECEIPT || this == PRODUCTION_RECEIPT || 
                   this == RETURN_RECEIPT || this == TRANSFER_IN || this == ADJUSTMENT_IN ||
                   this == STOCKTAKING_INCREASE || this == UNRESERVE || this == UNQUARANTINE ||
                   this == REPAIR;
        }

        /**
         * 출고 유형 여부
         */
        public boolean isOutbound() {
            return this == ISSUE || this == SALES_ISSUE || this == PRODUCTION_ISSUE ||
                   this == RETURN_ISSUE || this == TRANSFER_OUT || this == ADJUSTMENT_OUT ||
                   this == DISPOSAL || this == STOCKTAKING_DECREASE || this == RESERVE ||
                   this == QUARANTINE || this == DEFECTIVE;
        }

        /**
         * 이동 유형 여부
         */
        public boolean isTransfer() {
            return this == WAREHOUSE_TRANSFER || this == LOCATION_TRANSFER;
        }
    }

    /**
     * 이동 상태 열거형
     */
    public enum MovementStatus {
        DRAFT("임시저장"),
        PENDING("승인대기"),
        APPROVED("승인완료"),
        PROCESSED("처리완료"),
        CANCELLED("취소됨"),
        REJECTED("반려됨");

        private final String description;

        MovementStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 이동 번호 (회사별 고유)
     */
    @NotBlank(message = "이동 번호는 필수입니다")
    @Size(max = 30, message = "이동 번호는 30자를 초과할 수 없습니다")
    @Column(name = "movement_number", nullable = false, length = 30)
    private String movementNumber;

    /**
     * 회사 정보
     */
    @NotNull(message = "회사 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_stock_movement_company"))
    private Company company;

    /**
     * 상품 정보
     */
    @NotNull(message = "상품 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_stock_movement_product"))
    private Product product;

    /**
     * 창고 정보
     */
    @NotNull(message = "창고 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_stock_movement_warehouse"))
    private Warehouse warehouse;

    /**
     * 재고 정보 (재고가 있는 경우)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", foreignKey = @ForeignKey(name = "fk_stock_movement_inventory"))
    private Inventory inventory;

    /**
     * 이동 유형
     */
    @NotNull(message = "이동 유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 30)
    private MovementType movementType;

    /**
     * 이동 상태
     */
    @NotNull(message = "이동 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_status", nullable = false, length = 20)
    private MovementStatus movementStatus = MovementStatus.DRAFT;

    /**
     * 이동 일시
     */
    @NotNull(message = "이동 일시는 필수입니다")
    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    /**
     * 수량
     */
    @NotNull(message = "수량은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "수량은 0보다 커야 합니다")
    @Column(name = "quantity", nullable = false)
    private Double quantity;

    /**
     * 단위
     */
    @NotBlank(message = "단위는 필수입니다")
    @Size(max = 10, message = "단위는 10자를 초과할 수 없습니다")
    @Column(name = "unit", nullable = false, length = 10)
    private String unit;

    /**
     * 단가
     */
    @DecimalMin(value = "0.0", message = "단가는 0 이상이어야 합니다")
    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    /**
     * 총 금액
     */
    @DecimalMin(value = "0.0", message = "총 금액은 0 이상이어야 합니다")
    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * 이동 전 재고 수량
     */
    @Column(name = "before_stock")
    private Double beforeStock;

    /**
     * 이동 후 재고 수량
     */
    @Column(name = "after_stock")
    private Double afterStock;

    /**
     * 출발지 창고 (창고간 이동시)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_warehouse_id", foreignKey = @ForeignKey(name = "fk_stock_movement_from_warehouse"))
    private Warehouse fromWarehouse;

    /**
     * 출발지 위치 (위치 이동시)
     */
    @Size(max = 50, message = "출발지 위치는 50자를 초과할 수 없습니다")
    @Column(name = "from_location", length = 50)
    private String fromLocation;

    /**
     * 도착지 창고 (창고간 이동시)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_warehouse_id", foreignKey = @ForeignKey(name = "fk_stock_movement_to_warehouse"))
    private Warehouse toWarehouse;

    /**
     * 도착지 위치 (위치 이동시)
     */
    @Size(max = 50, message = "도착지 위치는 50자를 초과할 수 없습니다")
    @Column(name = "to_location", length = 50)
    private String toLocation;

    /**
     * 참조 번호 (주문번호, 생산번호 등)
     */
    @Size(max = 50, message = "참조 번호는 50자를 초과할 수 없습니다")
    @Column(name = "reference_number", length = 50)
    private String referenceNumber;

    /**
     * 참조 유형 (ORDER, PRODUCTION, RETURN 등)
     */
    @Size(max = 20, message = "참조 유형은 20자를 초과할 수 없습니다")
    @Column(name = "reference_type", length = 20)
    private String referenceType;

    /**
     * 거래처 정보
     */
    @Size(max = 100, message = "거래처 정보는 100자를 초과할 수 없습니다")
    @Column(name = "business_partner", length = 100)
    private String businessPartner;

    /**
     * 로트 번호
     */
    @Size(max = 50, message = "로트 번호는 50자를 초과할 수 없습니다")
    @Column(name = "lot_number", length = 50)
    private String lotNumber;

    /**
     * 시리얼 번호
     */
    @Size(max = 50, message = "시리얼 번호는 50자를 초과할 수 없습니다")
    @Column(name = "serial_number", length = 50)
    private String serialNumber;

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
     * 이동 사유
     */
    @Size(max = 200, message = "이동 사유는 200자를 초과할 수 없습니다")
    @Column(name = "reason", length = 200)
    private String reason;

    /**
     * 설명
     */
    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 처리자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_id", foreignKey = @ForeignKey(name = "fk_stock_movement_processed_by"))
    private User processedBy;

    /**
     * 처리 일시
     */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    /**
     * 승인자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id", foreignKey = @ForeignKey(name = "fk_stock_movement_approved_by"))
    private User approvedBy;

    /**
     * 승인 일시
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * 취소 사유
     */
    @Size(max = 200, message = "취소 사유는 200자를 초과할 수 없습니다")
    @Column(name = "cancel_reason", length = 200)
    private String cancelReason;

    /**
     * 취소 일시
     */
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    /**
     * 첨부파일 경로
     */
    @Size(max = 500, message = "첨부파일 경로는 500자를 초과할 수 없습니다")
    @Column(name = "attachment_path", length = 500)
    private String attachmentPath;

    /**
     * 메타데이터 (JSON 형태)
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    // 편의 메서드들

    /**
     * 총 금액 계산
     */
    public void calculateTotalAmount() {
        if (quantity != null && unitPrice != null) {
            this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.totalAmount = BigDecimal.ZERO;
        }
    }

    /**
     * 입고 유형 여부
     */
    public boolean isInbound() {
        return movementType != null && movementType.isInbound();
    }

    /**
     * 출고 유형 여부
     */
    public boolean isOutbound() {
        return movementType != null && movementType.isOutbound();
    }

    /**
     * 이동 유형 여부
     */
    public boolean isTransfer() {
        return movementType != null && movementType.isTransfer();
    }

    /**
     * 처리 완료 여부
     */
    public boolean isProcessed() {
        return movementStatus == MovementStatus.PROCESSED;
    }

    /**
     * 승인 완료 여부
     */
    public boolean isApproved() {
        return movementStatus == MovementStatus.APPROVED || movementStatus == MovementStatus.PROCESSED;
    }

    /**
     * 취소 가능 여부
     */
    public boolean isCancellable() {
        return movementStatus == MovementStatus.DRAFT || movementStatus == MovementStatus.PENDING;
    }

    /**
     * 승인 처리
     */
    public void approve(User approver) {
        if (movementStatus != MovementStatus.PENDING) {
            throw new IllegalStateException("승인 대기 상태가 아닙니다");
        }
        
        this.movementStatus = MovementStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * 처리 완료
     */
    public void process(User processor) {
        if (movementStatus != MovementStatus.APPROVED && movementStatus != MovementStatus.PENDING) {
            throw new IllegalStateException("처리할 수 없는 상태입니다");
        }
        
        this.movementStatus = MovementStatus.PROCESSED;
        this.processedBy = processor;
        this.processedAt = LocalDateTime.now();
        
        // 승인자가 없으면 처리자로 설정
        if (this.approvedBy == null) {
            this.approvedBy = processor;
            this.approvedAt = LocalDateTime.now();
        }
    }

    /**
     * 취소 처리
     */
    public void cancel(String reason) {
        if (!isCancellable()) {
            throw new IllegalStateException("취소할 수 없는 상태입니다");
        }
        
        this.movementStatus = MovementStatus.CANCELLED;
        this.cancelReason = reason;
        this.cancelledAt = LocalDateTime.now();
    }

    /**
     * 반려 처리
     */
    public void reject(String reason) {
        if (movementStatus != MovementStatus.PENDING) {
            throw new IllegalStateException("승인 대기 상태가 아닙니다");
        }
        
        this.movementStatus = MovementStatus.REJECTED;
        this.cancelReason = reason;
        this.cancelledAt = LocalDateTime.now();
    }

    /**
     * 재고 수량 변화량 계산 (+ : 증가, - : 감소)
     */
    public Double getStockChange() {
        if (isInbound()) {
            return quantity;
        } else if (isOutbound()) {
            return -quantity;
        } else {
            return 0.0; // 이동의 경우
        }
    }

    /**
     * 이동 방향 설명
     */
    public String getMovementDirection() {
        if (isInbound()) {
            return "입고";
        } else if (isOutbound()) {
            return "출고";
        } else if (isTransfer()) {
            return "이동";
        } else {
            return "기타";
        }
    }

    /**
     * 출발지 정보 조회
     */
    public String getFromLocationInfo() {
        StringBuilder info = new StringBuilder();
        
        if (fromWarehouse != null) {
            info.append(fromWarehouse.getWarehouseName());
        }
        
        if (fromLocation != null) {
            if (info.length() > 0) {
                info.append(" - ");
            }
            info.append(fromLocation);
        }
        
        return info.toString();
    }

    /**
     * 도착지 정보 조회
     */
    public String getToLocationInfo() {
        StringBuilder info = new StringBuilder();
        
        if (toWarehouse != null) {
            info.append(toWarehouse.getWarehouseName());
        } else if (warehouse != null) {
            info.append(warehouse.getWarehouseName());
        }
        
        if (toLocation != null) {
            if (info.length() > 0) {
                info.append(" - ");
            }
            info.append(toLocation);
        }
        
        return info.toString();
    }

    /**
     * 이동 요약 정보
     */
    public String getMovementSummary() {
        StringBuilder summary = new StringBuilder();
        
        summary.append(movementType.getDescription());
        summary.append(" - ");
        summary.append(String.format("%.3f", quantity));
        summary.append(unit);
        
        if (product != null) {
            summary.append(" (").append(product.getProductName()).append(")");
        }
        
        return summary.toString();
    }

    /**
     * 상태 색상 클래스 조회 (UI용)
     */
    public String getStatusColorClass() {
        switch (movementStatus) {
            case DRAFT: return "text-gray-600";
            case PENDING: return "text-yellow-600";
            case APPROVED: return "text-blue-600";
            case PROCESSED: return "text-green-600";
            case CANCELLED:
            case REJECTED: return "text-red-600";
            default: return "text-gray-600";
        }
    }

    @PrePersist
    @PreUpdate
    private void calculateFields() {
        calculateTotalAmount();
        
        if (movementDate == null) {
            movementDate = LocalDateTime.now();
        }
    }
}
