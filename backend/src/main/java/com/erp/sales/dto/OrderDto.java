package com.erp.sales.dto;

import com.erp.sales.entity.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 관련 DTO 클래스들
 */
public class OrderDto {

    /**
     * 주문 생성 요청 DTO
     */
    public record OrderCreateDto(
            @NotNull(message = "회사 ID는 필수입니다")
            Long companyId,

            @NotNull(message = "고객 ID는 필수입니다")
            Long customerId,

            Long quoteId,

            @NotNull(message = "주문일자는 필수입니다")
            LocalDate orderDate,

            LocalDate requiredDate,
            LocalDate promisedDate,

            Order.OrderType orderType,
            Order.PaymentStatus paymentStatus,

            @Size(max = 200, message = "제목은 200자 이내여야 합니다")
            String title,

            @Size(max = 1000, message = "설명은 1000자 이내여야 합니다")
            String description,

            // 영업 담당자 정보
            Long salesRepId,
            String salesRepName,

            // 할인 정보
            @DecimalMin(value = "0", message = "할인율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "할인율은 100 이하여야 합니다")
            BigDecimal discountRate,

            @DecimalMin(value = "0", message = "할인금액은 0 이상이어야 합니다")
            BigDecimal discountAmount,

            @DecimalMin(value = "0", message = "세율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "세율은 100 이하여야 합니다")
            BigDecimal taxRate,

            // 배송 정보
            @Size(max = 100, message = "배송지명은 100자 이내여야 합니다")
            String deliveryName,

            @Size(max = 20, message = "배송지 연락처는 20자 이내여야 합니다")
            String deliveryPhone,

            @Size(max = 10, message = "배송지 우편번호는 10자 이내여야 합니다")
            String deliveryPostalCode,

            @Size(max = 200, message = "배송지 주소는 200자 이내여야 합니다")
            String deliveryAddress,

            @Size(max = 200, message = "배송지 상세주소는 200자 이내여야 합니다")
            String deliveryAddressDetail,

            @Size(max = 100, message = "배송방법은 100자 이내여야 합니다")
            String deliveryMethod,

            @DecimalMin(value = "0", message = "배송비는 0 이상이어야 합니다")
            BigDecimal deliveryFee,

            @Size(max = 500, message = "배송메모는 500자 이내여야 합니다")
            String deliveryMemo,

            // 결제 정보
            @Size(max = 100, message = "결제조건은 100자 이내여야 합니다")
            String paymentTerms,

            @Size(max = 100, message = "결제방법은 100자 이내여야 합니다")
            String paymentMethod,

            LocalDate paymentDueDate,

            // 특별 조건
            @Size(max = 1000, message = "특별조건은 1000자 이내여야 합니다")
            String specialInstructions,

            @Size(max = 1000, message = "비고는 1000자 이내여야 합니다")
            String remarks,

            // 주문 항목들
            @Valid
            @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다")
            List<OrderItemCreateDto> orderItems,

            // 추가 정보
            @Size(max = 500, message = "태그는 500자 이내여야 합니다")
            String tags,

            Integer sortOrder,
            String metadata
    ) {
        public OrderCreateDto {
            if (orderType == null) orderType = Order.OrderType.NORMAL;
            if (paymentStatus == null) paymentStatus = Order.PaymentStatus.PENDING;
            if (discountRate == null) discountRate = BigDecimal.ZERO;
            if (discountAmount == null) discountAmount = BigDecimal.ZERO;
            if (taxRate == null) taxRate = new BigDecimal("10.00");
            if (deliveryFee == null) deliveryFee = BigDecimal.ZERO;
            if (sortOrder == null) sortOrder = 0;
        }
    }

    /**
     * 주문 항목 생성 DTO
     */
    public record OrderItemCreateDto(
            Long productId,

            @NotBlank(message = "상품코드는 필수입니다")
            String productCode,

            @NotBlank(message = "상품명은 필수입니다")
            String productName,

            String productDescription,

            @NotNull(message = "수량은 필수입니다")
            @Min(value = 1, message = "수량은 1 이상이어야 합니다")
            Integer quantity,

            @NotBlank(message = "단위는 필수입니다")
            String unit,

            @NotNull(message = "단가는 필수입니다")
            @DecimalMin(value = "0", message = "단가는 0 이상이어야 합니다")
            BigDecimal unitPrice,

            @DecimalMin(value = "0", message = "할인율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "할인율은 100 이하여야 합니다")
            BigDecimal discountRate,

            @DecimalMin(value = "0", message = "할인금액은 0 이상이어야 합니다")
            BigDecimal discountAmount,

            String remarks,
            Integer sortOrder
    ) {
        public OrderItemCreateDto {
            if (discountRate == null) discountRate = BigDecimal.ZERO;
            if (discountAmount == null) discountAmount = BigDecimal.ZERO;
            if (sortOrder == null) sortOrder = 0;
        }
    }

    /**
     * 주문 수정 요청 DTO
     */
    public record OrderUpdateDto(
            LocalDate requiredDate,
            LocalDate promisedDate,
            Order.OrderType orderType,

            @Size(max = 200, message = "제목은 200자 이내여야 합니다")
            String title,

            @Size(max = 1000, message = "설명은 1000자 이내여야 합니다")
            String description,

            // 영업 담당자 정보
            Long salesRepId,
            String salesRepName,

            // 할인 정보
            @DecimalMin(value = "0", message = "할인율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "할인율은 100 이하여야 합니다")
            BigDecimal discountRate,

            @DecimalMin(value = "0", message = "할인금액은 0 이상이어야 합니다")
            BigDecimal discountAmount,

            @DecimalMin(value = "0", message = "세율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "세율은 100 이하여야 합니다")
            BigDecimal taxRate,

            // 배송 정보
            @Size(max = 100, message = "배송지명은 100자 이내여야 합니다")
            String deliveryName,

            @Size(max = 20, message = "배송지 연락처는 20자 이내여야 합니다")
            String deliveryPhone,

            @Size(max = 10, message = "배송지 우편번호는 10자 이내여야 합니다")
            String deliveryPostalCode,

            @Size(max = 200, message = "배송지 주소는 200자 이내여야 합니다")
            String deliveryAddress,

            @Size(max = 200, message = "배송지 상세주소는 200자 이내여야 합니다")
            String deliveryAddressDetail,

            @Size(max = 100, message = "배송방법은 100자 이내여야 합니다")
            String deliveryMethod,

            @DecimalMin(value = "0", message = "배송비는 0 이상이어야 합니다")
            BigDecimal deliveryFee,

            @Size(max = 500, message = "배송메모는 500자 이내여야 합니다")
            String deliveryMemo,

            // 결제 정보
            @Size(max = 100, message = "결제조건은 100자 이내여야 합니다")
            String paymentTerms,

            @Size(max = 100, message = "결제방법은 100자 이내여야 합니다")
            String paymentMethod,

            LocalDate paymentDueDate,

            // 특별 조건
            @Size(max = 1000, message = "특별조건은 1000자 이내여야 합니다")
            String specialInstructions,

            @Size(max = 1000, message = "비고는 1000자 이내여야 합니다")
            String remarks,

            // 추가 정보
            @Size(max = 500, message = "태그는 500자 이내여야 합니다")
            String tags,

            Integer sortOrder,
            String metadata
    ) {}

    /**
     * 주문 응답 DTO
     */
    public record OrderResponseDto(
            Long id,
            String orderNumber,
            Long companyId,
            String companyName,
            Long customerId,
            String customerCode,
            String customerName,
            Long quoteId,
            String quoteNumber,
            LocalDate orderDate,
            LocalDate requiredDate,
            LocalDate promisedDate,
            LocalDate deliveryDate,
            Order.OrderStatus orderStatus,
            String orderStatusDescription,
            Order.OrderType orderType,
            String orderTypeDescription,
            Order.PaymentStatus paymentStatus,
            String paymentStatusDescription,
            String title,
            String description,

            // 영업 담당자 정보
            Long salesRepId,
            String salesRepName,

            // 금액 정보
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal discountRate,
            BigDecimal taxAmount,
            BigDecimal taxRate,
            BigDecimal totalAmount,
            BigDecimal paidAmount,
            BigDecimal outstandingAmount,

            // 배송 정보
            String deliveryName,
            String deliveryPhone,
            String deliveryPostalCode,
            String deliveryAddress,
            String deliveryAddressDetail,
            String fullDeliveryAddress,
            String deliveryMethod,
            BigDecimal deliveryFee,
            String deliveryMemo,
            String courierCompany,
            String trackingNumber,

            // 결제 정보
            String paymentTerms,
            String paymentMethod,
            LocalDate paymentDueDate,
            LocalDateTime paymentCompletedDate,

            // 상태 관리
            LocalDateTime confirmedDate,
            LocalDateTime shippedDate,
            LocalDateTime deliveredDate,
            LocalDateTime completedDate,
            LocalDateTime cancelledDate,
            String cancellationReason,

            // 특별 조건
            String specialInstructions,
            String remarks,

            // 추가 정보
            String tags,
            Integer sortOrder,
            String metadata,
            String createdAt,
            String updatedAt,

            // 주문 항목들
            List<OrderItemResponseDto> orderItems,

            // 계산 필드
            Boolean isEditable,
            Boolean isConfirmable,
            Boolean isCancellable,
            Boolean isShippable,
            String orderSummary,
            Integer totalItemCount,
            Long daysUntilDelivery
    ) {}

    /**
     * 주문 항목 응답 DTO
     */
    public record OrderItemResponseDto(
            Long id,
            Long orderId,
            Long productId,
            Integer lineNumber,
            String productCode,
            String productName,
            String productDescription,
            Integer quantity,
            Integer shippedQuantity,
            Integer deliveredQuantity,
            Integer cancelledQuantity,
            String unit,
            BigDecimal unitPrice,
            BigDecimal discountRate,
            BigDecimal discountAmount,
            BigDecimal totalPrice,
            com.erp.sales.entity.OrderItem.DeliveryStatus deliveryStatus,
            String deliveryStatusDescription,
            String remarks,
            Integer sortOrder,

            // 계산 필드
            Integer pendingQuantity,
            Boolean hasPendingQuantity,
            Boolean isPartiallyShipped,
            Boolean isFullyShipped,
            Boolean isFullyDelivered,
            Boolean hasDiscount,
            BigDecimal discountedUnitPrice,
            String lineSummary,
            Double deliveryProgress
    ) {}

    /**
     * 주문 요약 DTO (목록용)
     */
    public record OrderSummaryDto(
            Long id,
            String orderNumber,
            Long customerId,
            String customerCode,
            String customerName,
            LocalDate orderDate,
            LocalDate requiredDate,
            LocalDate deliveryDate,
            Order.OrderStatus orderStatus,
            String orderStatusDescription,
            Order.OrderType orderType,
            String orderTypeDescription,
            Order.PaymentStatus paymentStatus,
            String paymentStatusDescription,
            BigDecimal totalAmount,
            BigDecimal paidAmount,
            BigDecimal outstandingAmount,
            String salesRepName,
            Integer totalItemCount,
            String createdAt,
            Boolean isEditable,
            Boolean isCancellable,
            Long daysUntilDelivery
    ) {}

    /**
     * 주문 검색 DTO
     */
    public record OrderSearchDto(
            String searchTerm,
            Long customerId,
            Order.OrderStatus orderStatus,
            Order.OrderType orderType,
            Order.PaymentStatus paymentStatus,
            Long salesRepId,
            LocalDate orderDateFrom,
            LocalDate orderDateTo,
            LocalDate requiredDateFrom,
            LocalDate requiredDateTo,
            LocalDate deliveryDateFrom,
            LocalDate deliveryDateTo,
            BigDecimal totalAmountFrom,
            BigDecimal totalAmountTo,
            Boolean hasOutstanding,
            String deliveryMethod,
            String tags
    ) {}

    /**
     * 주문 통계 DTO
     */
    public record OrderStatsDto(
            Long totalOrders,
            Long pendingOrders,
            Long confirmedOrders,
            Long shippedOrders,
            Long deliveredOrders,
            Long completedOrders,
            Long cancelledOrders,
            BigDecimal totalOrderAmount,
            BigDecimal averageOrderAmount,
            BigDecimal totalPaidAmount,
            BigDecimal totalOutstandingAmount,
            Double averageItemsPerOrder,
            Long overDueOrders,
            Long urgentOrders
    ) {}

    /**
     * 주문 상태 변경 DTO
     */
    public record OrderStatusChangeDto(
            @NotNull(message = "주문상태는 필수입니다")
            Order.OrderStatus orderStatus,

            @Size(max = 500, message = "변경사유는 500자 이내여야 합니다")
            String reason
    ) {}

    /**
     * 주문 배송 처리 DTO
     */
    public record OrderShipmentDto(
            @NotBlank(message = "택배사는 필수입니다")
            String courierCompany,

            @NotBlank(message = "송장번호는 필수입니다")
            String trackingNumber,

            LocalDate shippedDate,
            String remarks
    ) {}

    /**
     * 주문 결제 처리 DTO
     */
    public record OrderPaymentDto(
            @NotNull(message = "결제금액은 필수입니다")
            @DecimalMin(value = "0.01", message = "결제금액은 0보다 커야 합니다")
            BigDecimal paymentAmount,

            @NotBlank(message = "결제방법은 필수입니다")
            String paymentMethod,

            LocalDateTime paymentDate,
            String paymentReference,
            String remarks
    ) {}

    /**
     * 주문 취소 DTO
     */
    public record OrderCancellationDto(
            @NotBlank(message = "취소사유는 필수입니다")
            @Size(max = 500, message = "취소사유는 500자 이내여야 합니다")
            String cancellationReason
    ) {}

    /**
     * 견적서에서 주문 생성 DTO
     */
    public record OrderFromQuoteDto(
            @NotNull(message = "견적서 ID는 필수입니다")
            Long quoteId,

            @NotNull(message = "주문일자는 필수입니다")
            LocalDate orderDate,

            LocalDate requiredDate,
            String deliveryAddress,
            String deliveryMemo,
            String paymentTerms,
            String specialInstructions,
            String remarks
    ) {}
}
