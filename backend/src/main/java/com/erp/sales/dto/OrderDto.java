package com.erp.sales.dto;

import com.erp.sales.entity.Order;
import com.erp.sales.entity.OrderItem;
import jakarta.validation.constraints.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 주문 DTO
 * 실제 DB 스키마와 완전히 일치하도록 수정됨
 */
@Slf4j
public class OrderDto {

    /**
     * 주문 생성 요청 DTO
     */
    public record OrderCreateDto(
            @NotBlank(message = "주문번호는 필수입니다")
            @Size(max = 50, message = "주문번호는 50자 이내여야 합니다")
            String orderNumber,

            @NotNull(message = "회사 ID는 필수입니다")
            Long companyId,

            @NotNull(message = "고객 ID는 필수입니다")
            Long customerId,

            Long quoteId,

            @NotNull(message = "주문일자는 필수입니다")
            LocalDate orderDate,

            LocalDate deliveryDate,

            @NotNull(message = "주문상태는 필수입니다")
            Order.OrderStatus orderStatus,

            @NotNull(message = "결제상태는 필수입니다")
            Order.PaymentStatus paymentStatus,

            @NotNull(message = "총금액은 필수입니다")
            @DecimalMin(value = "0", message = "총금액은 0 이상이어야 합니다")
            BigDecimal totalAmount
    ) {
        public OrderCreateDto {
            if (orderNumber == null || orderNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("주문번호는 필수입니다");
            }
            if (companyId == null) {
                throw new IllegalArgumentException("회사 ID는 필수입니다");
            }
            if (customerId == null) {
                throw new IllegalArgumentException("고객 ID는 필수입니다");
            }
            if (orderDate == null) {
                throw new IllegalArgumentException("주문일자는 필수입니다");
            }
            if (orderStatus == null) {
                throw new IllegalArgumentException("주문상태는 필수입니다");
            }
            if (paymentStatus == null) {
                throw new IllegalArgumentException("결제상태는 필수입니다");
            }
            if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("총금액은 0 이상이어야 합니다");
            }
        }
    }

    /**
     * 주문 수정 요청 DTO
     */
    public record OrderUpdateDto(
            LocalDate deliveryDate,

            Order.OrderStatus orderStatus,

            Order.PaymentStatus paymentStatus,

            @DecimalMin(value = "0", message = "총금액은 0 이상이어야 합니다")
            BigDecimal totalAmount
    ) {
        public OrderUpdateDto {
            if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("총금액은 0 이상이어야 합니다");
            }
        }
    }

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
            LocalDate orderDate,
            LocalDate deliveryDate,
            Order.OrderStatus orderStatus,
            String orderStatusDescription,
            Order.PaymentStatus paymentStatus,
            String paymentStatusDescription,
            BigDecimal totalAmount,
            List<OrderItemResponseDto> orderItems,
            boolean isEditable,
            boolean isConfirmable,
            boolean isCancellable,
            boolean isShippable,
            String orderSummary,
            int totalItemCount,
            long daysUntilDelivery
    ) {
        public static OrderResponseDto from(Order order) {
            return new OrderResponseDto(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getCompany() != null ? order.getCompany().getId() : null,
                    order.getCompany() != null ? order.getCompany().getName() : null,
                    order.getCustomer() != null ? order.getCustomer().getId() : null,
                    order.getCustomer() != null ? order.getCustomer().getCustomerCode() : null,
                    order.getCustomer() != null ? order.getCustomer().getCustomerName() : null,
                    order.getQuote() != null ? order.getQuote().getId() : null,
                    order.getOrderDate(),
                    order.getDeliveryDate(),
                    order.getOrderStatus(),
                    order.getOrderStatus() != null ? order.getOrderStatus().getDescription() : null,
                    order.getPaymentStatus(),
                    order.getPaymentStatus() != null ? order.getPaymentStatus().getDescription() : null,
                    order.getTotalAmount(),
                    order.getOrderItems() != null ? 
                            order.getOrderItems().stream()
                                    .map(OrderItemResponseDto::from)
                                    .toList() : List.of(),
                    order.isEditable(),
                    order.isConfirmable(),
                    order.isCancellable(),
                    order.isShippable(),
                    order.getOrderSummary(),
                    order.getTotalItemCount(),
                    order.getDaysUntilDelivery()
            );
        }
    }

    /**
     * 주문 아이템 응답 DTO
     */
    public record OrderItemResponseDto(
            Long id,
            Long productId,
            String productName,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice
    ) {
        public static OrderItemResponseDto from(OrderItem orderItem) {
            return new OrderItemResponseDto(
                    orderItem.getId(),
                    orderItem.getProduct() != null ? orderItem.getProduct().getId() : null,
                    orderItem.getProduct() != null ? orderItem.getProduct().getProductName() : null,
                    orderItem.getQuantity(),
                    orderItem.getUnitPrice(),
                    orderItem.getTotalPrice()
            );
        }
    }

    /**
     * 주문 요약 DTO
     */
    public record OrderSummaryDto(
            Long id,
            String orderNumber,
            String customerName,
            LocalDate orderDate,
            Order.OrderStatus orderStatus,
            BigDecimal totalAmount
    ) {
        public static OrderSummaryDto from(Order order) {
            return new OrderSummaryDto(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getCustomer() != null ? order.getCustomer().getCustomerName() : null,
                    order.getOrderDate(),
                    order.getOrderStatus(),
                    order.getTotalAmount()
            );
        }
    }
}