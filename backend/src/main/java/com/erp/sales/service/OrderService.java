package com.erp.sales.service;

import com.erp.sales.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 주문 관리 서비스 인터페이스
 * 주문 관련 비즈니스 로직을 정의합니다
 */
public interface OrderService {

    /**
     * 주문 생성
     */
    OrderDto.OrderResponseDto createOrder(OrderDto.OrderCreateDto createDto);

    /**
     * 견적서에서 주문 생성
     */
    OrderDto.OrderResponseDto createOrderFromQuote(OrderDto.OrderFromQuoteDto fromQuoteDto);

    /**
     * 주문 수정
     */
    OrderDto.OrderResponseDto updateOrder(Long orderId, OrderDto.OrderUpdateDto updateDto);

    /**
     * 주문 삭제 (소프트 삭제)
     */
    void deleteOrder(Long orderId);

    /**
     * 주문 상세 조회
     */
    OrderDto.OrderResponseDto getOrder(Long orderId);

    /**
     * 주문번호로 조회
     */
    OrderDto.OrderResponseDto getOrderByNumber(String orderNumber);

    /**
     * 회사별 주문 목록 조회
     */
    Page<OrderDto.OrderSummaryDto> getOrdersByCompany(Long companyId, Pageable pageable);

    /**
     * 고객별 주문 조회
     */
    Page<OrderDto.OrderSummaryDto> getOrdersByCustomer(Long customerId, Pageable pageable);

    /**
     * 영업담당자별 주문 조회
     */
    Page<OrderDto.OrderSummaryDto> getOrdersBySalesRep(Long salesRepId, Pageable pageable);

    /**
     * 주문 검색
     */
    Page<OrderDto.OrderSummaryDto> searchOrders(Long companyId, String searchTerm, Pageable pageable);

    /**
     * 고급 검색
     */
    Page<OrderDto.OrderSummaryDto> searchOrdersAdvanced(Long companyId, OrderDto.OrderSearchDto searchDto, Pageable pageable);

    /**
     * 주문 상태별 조회
     */
    Page<OrderDto.OrderSummaryDto> getOrdersByStatus(Long companyId, String orderStatus, Pageable pageable);

    /**
     * 주문 유형별 조회
     */
    Page<OrderDto.OrderSummaryDto> getOrdersByType(Long companyId, String orderType, Pageable pageable);

    /**
     * 결제 상태별 조회
     */
    Page<OrderDto.OrderSummaryDto> getOrdersByPaymentStatus(Long companyId, String paymentStatus, Pageable pageable);

    /**
     * 미수금 있는 주문 조회
     */
    Page<OrderDto.OrderSummaryDto> getOrdersWithOutstanding(Long companyId, Pageable pageable);

    /**
     * 연체된 주문 조회
     */
    Page<OrderDto.OrderSummaryDto> getOverdueOrders(Long companyId, Pageable pageable);

    /**
     * 긴급 주문 조회
     */
    List<OrderDto.OrderSummaryDto> getUrgentOrders(Long companyId);

    /**
     * 배송 지연 주문 조회
     */
    List<OrderDto.OrderSummaryDto> getDelayedOrders(Long companyId);

    /**
     * 상위 주문 조회 (금액 기준)
     */
    Page<OrderDto.OrderSummaryDto> getTopOrders(Long companyId, Pageable pageable);

    /**
     * 최근 주문 조회
     */
    Page<OrderDto.OrderSummaryDto> getRecentOrders(Long companyId, Pageable pageable);

    /**
     * 오늘 주문 조회
     */
    List<OrderDto.OrderSummaryDto> getTodayOrders(Long companyId);

    /**
     * 배송 예정 주문 조회
     */
    List<OrderDto.OrderSummaryDto> getOrdersForDelivery(Long companyId);

    /**
     * 주문 통계 조회
     */
    OrderDto.OrderStatsDto getOrderStatistics(Long companyId);

    /**
     * 주문 상태 변경
     */
    OrderDto.OrderResponseDto changeOrderStatus(Long orderId, OrderDto.OrderStatusChangeDto statusChangeDto);

    /**
     * 주문 확정
     */
    OrderDto.OrderResponseDto confirmOrder(Long orderId);

    /**
     * 주문 배송 처리
     */
    OrderDto.OrderResponseDto shipOrder(Long orderId, OrderDto.OrderShipmentDto shipmentDto);

    /**
     * 주문 배송완료 처리
     */
    OrderDto.OrderResponseDto markOrderAsDelivered(Long orderId);

    /**
     * 주문 완료 처리
     */
    OrderDto.OrderResponseDto completeOrder(Long orderId);

    /**
     * 주문 취소
     */
    OrderDto.OrderResponseDto cancelOrder(Long orderId, OrderDto.OrderCancellationDto cancellationDto);

    /**
     * 주문 결제 처리
     */
    OrderDto.OrderResponseDto processOrderPayment(Long orderId, OrderDto.OrderPaymentDto paymentDto);

    /**
     * 주문번호 중복 확인
     */
    boolean isOrderNumberDuplicate(Long companyId, String orderNumber);

    /**
     * 주문번호 생성
     */
    String generateOrderNumber(Long companyId);

    /**
     * 영업담당자별 주문 통계
     */
    List<Object[]> getOrderStatsBySalesRep(Long companyId);

    /**
     * 주문 상태별 통계
     */
    List<Object[]> getOrderStatsByStatus(Long companyId);

    /**
     * 월별 주문 통계
     */
    List<Object[]> getOrderStatsByMonth(Long companyId, LocalDate fromDate);

    /**
     * 일별 주문 통계
     */
    List<Object[]> getOrderStatsByDay(Long companyId, LocalDate fromDate);

    /**
     * 고객별 주문 통계
     */
    List<Object[]> getOrderStatsByCustomer(Long companyId);

    /**
     * 배송방법별 주문 통계
     */
    List<Object[]> getOrderStatsByDeliveryMethod(Long companyId);

    /**
     * 결제방법별 주문 통계
     */
    List<Object[]> getOrderStatsByPaymentMethod(Long companyId);

    /**
     * 평균 주문 처리 시간 조회
     */
    Double getAverageProcessingDays(Long companyId, LocalDate fromDate);

    /**
     * 주문 취소율 계산
     */
    Double getCancellationRate(Long companyId, LocalDate fromDate);

    /**
     * 재주문 고객 수 조회
     */
    Long getReorderCustomerCount(Long companyId);

    /**
     * 주문 승인 처리 (필요시)
     */
    OrderDto.OrderResponseDto approveOrder(Long orderId, String approverNote);

    /**
     * 주문 반품 처리
     */
    OrderDto.OrderResponseDto returnOrder(Long orderId, String returnReason);

    /**
     * 주문 교환 처리
     */
    OrderDto.OrderResponseDto exchangeOrder(Long orderId, String exchangeReason);

    /**
     * 주문 부분 배송 처리
     */
    OrderDto.OrderResponseDto partialShipOrder(Long orderId, List<OrderDto.OrderItemResponseDto> shippedItems);

    /**
     * 주문 알림 발송 (고객에게)
     */
    void sendOrderNotification(Long orderId, String notificationType);

    /**
     * 주문 리마인더 발송
     */
    void sendOrderReminders(Long companyId);

    /**
     * 주문 보고서 생성
     */
    byte[] generateOrderReport(Long companyId, LocalDate fromDate, LocalDate toDate, String reportType);

    /**
     * 주문 데이터 내보내기
     */
    byte[] exportOrderData(Long companyId, OrderDto.OrderSearchDto searchDto, String format);

    /**
     * 주문 일괄 처리
     */
    List<OrderDto.OrderResponseDto> bulkProcessOrders(List<Long> orderIds, String action, String note);

    /**
     * 주문 예측 분석
     */
    List<Object[]> getOrderForecastAnalysis(Long companyId, int forecastDays);

    /**
     * 주문 패턴 분석
     */
    List<Object[]> getOrderPatternAnalysis(Long companyId, LocalDate fromDate, LocalDate toDate);

    /**
     * 계절성 주문 분석
     */
    List<Object[]> getSeasonalOrderAnalysis(Long companyId);

    /**
     * 주문 이상 감지
     */
    List<OrderDto.OrderSummaryDto> detectAnomalousOrders(Long companyId, LocalDate fromDate);
}




