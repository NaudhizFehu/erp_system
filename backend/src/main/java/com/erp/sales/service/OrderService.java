package com.erp.sales.service;

import com.erp.sales.dto.OrderDto;
import com.erp.sales.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 주문 서비스 인터페이스
 * 실제 DB 스키마와 완전히 일치하도록 수정됨
 */
public interface OrderService {

    /**
     * 주문 목록 조회
     */
    Page<OrderDto.OrderResponseDto> getAllOrders(Long companyId, Pageable pageable);

    /**
     * 주문 상세 조회
     */
    Optional<OrderDto.OrderResponseDto> getOrderById(Long id);

    /**
     * 주문 생성
     */
    OrderDto.OrderResponseDto createOrder(OrderDto.OrderCreateDto createDto);

    /**
     * 주문 수정
     */
    OrderDto.OrderResponseDto updateOrder(Long id, OrderDto.OrderUpdateDto updateDto);

    /**
     * 주문 삭제
     */
    void deleteOrder(Long id);

    /**
     * 주문 검색
     */
    Page<OrderDto.OrderResponseDto> searchOrders(Long companyId, String searchTerm, Pageable pageable);

    /**
     * 상태별 주문 조회
     */
    List<OrderDto.OrderSummaryDto> getOrdersByStatus(Long companyId, Order.OrderStatus status);

    /**
     * 고객별 주문 조회
     */
    List<OrderDto.OrderSummaryDto> getOrdersByCustomer(Long companyId, Long customerId);

    /**
     * 기간별 주문 조회
     */
    List<OrderDto.OrderSummaryDto> getOrdersByDateRange(Long companyId, LocalDate startDate, LocalDate endDate);

    /**
     * 견적으로부터 주문 생성
     */
    OrderDto.OrderResponseDto createOrderFromQuote(Long quoteId);

    /**
     * 주문 확정
     */
    OrderDto.OrderResponseDto confirmOrder(Long id);

    /**
     * 주문 취소
     */
    OrderDto.OrderResponseDto cancelOrder(Long id);
}