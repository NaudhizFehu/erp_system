package com.erp.sales.service.impl;

import com.erp.common.utils.ExceptionUtils;
import com.erp.sales.dto.OrderDto;
import com.erp.sales.entity.Order;
import com.erp.sales.entity.OrderItem;
import com.erp.sales.entity.Quote;
import com.erp.sales.repository.OrderRepository;
import com.erp.sales.repository.QuoteRepository;
import com.erp.sales.service.CustomerService;
import com.erp.sales.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 주문 관리 서비스 구현
 * 주문 관련 비즈니스 로직을 처리합니다
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final QuoteRepository quoteRepository;
    private final CustomerService customerService;

    @Override
    @Transactional
    public OrderDto.OrderResponseDto createOrder(OrderDto.OrderCreateDto createDto) {
        log.info("주문 생성 요청: 고객ID={}", createDto.customerId());
        
        // 주문번호 생성
        String orderNumber = generateOrderNumber(createDto.companyId());
        
        // Order 엔티티 생성
        Order order = createOrderEntity(createDto, orderNumber);
        
        // 주문 항목 추가
        final Order finalOrder = order; // final 변수로 생성
        createDto.orderItems().forEach(itemDto -> {
            OrderItem orderItem = createOrderItemEntity(itemDto);
            finalOrder.addOrderItem(orderItem);
        });
        
        order = orderRepository.save(order);
        
        // 고객 주문 통계 업데이트
        customerService.updateCustomerOrderStatistics(order.getCustomer().getId());
        
        log.info("주문 생성 완료: ID={}, 번호={}", order.getId(), order.getOrderNumber());
        return mapToResponseDto(order);
    }

    @Override
    @Transactional
    public OrderDto.OrderResponseDto createOrderFromQuote(OrderDto.OrderFromQuoteDto fromQuoteDto) {
        log.info("견적서에서 주문 생성: 견적ID={}", fromQuoteDto.quoteId());
        
        Quote quote = quoteRepository.findById(fromQuoteDto.quoteId())
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("견적서를 찾을 수 없습니다: " + fromQuoteDto.quoteId()));
        
        if (!quote.isConvertible()) {
            ExceptionUtils.throwBusinessException("주문으로 전환할 수 없는 견적서입니다");
        }
        
        // 견적서로부터 주문 생성
        Order order = Order.fromQuote(quote);
        order.setOrderDate(fromQuoteDto.orderDate());
        order.setRequiredDate(fromQuoteDto.requiredDate());
        order.setOrderNumber(generateOrderNumber(quote.getCompany().getId()));
        
        // 추가 정보 설정
        if (fromQuoteDto.deliveryAddress() != null) {
            order.setDeliveryAddress(fromQuoteDto.deliveryAddress());
        }
        if (fromQuoteDto.deliveryMemo() != null) {
            order.setDeliveryMemo(fromQuoteDto.deliveryMemo());
        }
        if (fromQuoteDto.paymentTerms() != null) {
            order.setPaymentTerms(fromQuoteDto.paymentTerms());
        }
        if (fromQuoteDto.specialInstructions() != null) {
            order.setSpecialInstructions(fromQuoteDto.specialInstructions());
        }
        if (fromQuoteDto.remarks() != null) {
            order.setRemarks(fromQuoteDto.remarks());
        }
        
        order = orderRepository.save(order);
        
        // 견적서 상태를 전환됨으로 변경
        quote.markAsConverted();
        quoteRepository.save(quote);
        
        // 고객 주문 통계 업데이트
        customerService.updateCustomerOrderStatistics(order.getCustomer().getId());
        
        log.info("견적서에서 주문 생성 완료: 견적ID={}, 주문ID={}", fromQuoteDto.quoteId(), order.getId());
        return mapToResponseDto(order);
    }

    @Override
    @Transactional
    public OrderDto.OrderResponseDto updateOrder(Long orderId, OrderDto.OrderUpdateDto updateDto) {
        log.info("주문 수정 요청: ID={}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        
        if (!order.isEditable()) {
            ExceptionUtils.throwBusinessException("수정할 수 없는 주문 상태입니다");
        }
        
        // Order 엔티티 업데이트
        updateOrderEntity(order, updateDto);
        order = orderRepository.save(order);
        
        log.info("주문 수정 완료: ID={}, 번호={}", order.getId(), order.getOrderNumber());
        return mapToResponseDto(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        log.info("주문 삭제 요청: ID={}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        
        if (!order.isCancellable()) {
            ExceptionUtils.throwBusinessException("삭제할 수 없는 주문 상태입니다");
        }
        
        order.softDelete(null);
        orderRepository.save(order);
        
        // 고객 주문 통계 업데이트
        customerService.updateCustomerOrderStatistics(order.getCustomer().getId());
        
        log.info("주문 삭제 완료: ID={}, 번호={}", order.getId(), order.getOrderNumber());
    }

    @Override
    public OrderDto.OrderResponseDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        
        return mapToResponseDto(order);
    }

    @Override
    public OrderDto.OrderResponseDto getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumberAndIsDeletedFalse(orderNumber)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("주문을 찾을 수 없습니다: " + orderNumber));
        
        return mapToResponseDto(order);
    }

    @Override
    public Page<OrderDto.OrderSummaryDto> getOrdersByCompany(Long companyId, Pageable pageable) {
        return orderRepository.findByCompanyIdAndIsDeletedFalse(companyId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<OrderDto.OrderSummaryDto> getOrdersByCustomer(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerIdAndIsDeletedFalse(customerId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<OrderDto.OrderSummaryDto> getOrdersBySalesRep(Long salesRepId, Pageable pageable) {
        return orderRepository.findBySalesRepIdAndIsDeletedFalse(salesRepId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<OrderDto.OrderSummaryDto> searchOrders(Long companyId, String searchTerm, Pageable pageable) {
        return orderRepository.searchOrders(companyId, searchTerm, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<OrderDto.OrderSummaryDto> searchOrdersAdvanced(Long companyId, OrderDto.OrderSearchDto searchDto, Pageable pageable) {
        return orderRepository.searchOrdersAdvanced(
                companyId,
                searchDto.searchTerm(),
                searchDto.customerId(),
                searchDto.orderStatus(),
                searchDto.orderType(),
                searchDto.paymentStatus(),
                searchDto.salesRepId(),
                searchDto.deliveryMethod(),
                pageable
        ).map(this::mapToSummaryDto);
    }

    @Override
    public Page<OrderDto.OrderSummaryDto> getOrdersByStatus(Long companyId, String orderStatus, Pageable pageable) {
        Order.OrderStatus status = Order.OrderStatus.valueOf(orderStatus);
        return orderRepository.findByCompanyIdAndOrderStatusAndIsDeletedFalse(companyId, status, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<OrderDto.OrderSummaryDto> getOrdersByType(Long companyId, String orderType, Pageable pageable) {
        Order.OrderType type = Order.OrderType.valueOf(orderType);
        return orderRepository.findByCompanyIdAndOrderTypeAndIsDeletedFalse(companyId, type, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<OrderDto.OrderSummaryDto> getOrdersByPaymentStatus(Long companyId, String paymentStatus, Pageable pageable) {
        Order.PaymentStatus status = Order.PaymentStatus.valueOf(paymentStatus);
        return orderRepository.findByCompanyIdAndPaymentStatusAndIsDeletedFalse(companyId, status, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<OrderDto.OrderSummaryDto> getOrdersWithOutstanding(Long companyId, Pageable pageable) {
        return orderRepository.findOrdersWithOutstanding(companyId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<OrderDto.OrderSummaryDto> getOverdueOrders(Long companyId, Pageable pageable) {
        return orderRepository.findOverdueOrders(companyId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public List<OrderDto.OrderSummaryDto> getUrgentOrders(Long companyId) {
        LocalDate urgentDate = LocalDate.now().plusDays(3); // 3일 이내 배송 예정
        return orderRepository.findUrgentOrders(companyId, urgentDate)
                .stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto.OrderSummaryDto> getDelayedOrders(Long companyId) {
        LocalDate currentDate = LocalDate.now();
        return orderRepository.findDelayedOrders(companyId, currentDate)
                .stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrderDto.OrderSummaryDto> getTopOrders(Long companyId, Pageable pageable) {
        return orderRepository.findTopOrdersByAmount(companyId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<OrderDto.OrderSummaryDto> getRecentOrders(Long companyId, Pageable pageable) {
        return orderRepository.findRecentOrders(companyId, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public List<OrderDto.OrderSummaryDto> getTodayOrders(Long companyId) {
        LocalDate today = LocalDate.now();
        return orderRepository.findTodayOrders(companyId, today)
                .stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDto.OrderSummaryDto> getOrdersForDelivery(Long companyId) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return orderRepository.findOrdersForDelivery(companyId, tomorrow)
                .stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto.OrderStatsDto getOrderStatistics(Long companyId) {
        Object[] stats = orderRepository.getOrderStatistics(companyId);
        
        return new OrderDto.OrderStatsDto(
                ((Number) stats[0]).longValue(),  // totalOrders
                ((Number) stats[1]).longValue(),  // pendingOrders
                ((Number) stats[2]).longValue(),  // confirmedOrders
                ((Number) stats[3]).longValue(),  // shippedOrders
                ((Number) stats[4]).longValue(),  // deliveredOrders
                ((Number) stats[5]).longValue(),  // completedOrders
                ((Number) stats[6]).longValue(),  // cancelledOrders
                (BigDecimal) stats[7],            // totalOrderAmount
                (BigDecimal) stats[8],            // averageOrderAmount
                (BigDecimal) stats[9],            // totalPaidAmount
                (BigDecimal) stats[10],           // totalOutstandingAmount
                calculateAverageItemsPerOrder(companyId), // averageItemsPerOrder
                ((Number) stats[11]).longValue(), // overdueOrders
                ((Number) stats[12]).longValue()  // urgentOrders
        );
    }

    @Override
    @Transactional
    public OrderDto.OrderResponseDto changeOrderStatus(Long orderId, OrderDto.OrderStatusChangeDto statusChangeDto) {
        log.info("주문 상태 변경: ID={}, 새상태={}", orderId, statusChangeDto.orderStatus());
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        
        Order.OrderStatus oldStatus = order.getOrderStatus();
        order.setOrderStatus(statusChangeDto.orderStatus());
        
        // 상태 변경 로그
        String statusChangeLog = String.format("상태변경: %s → %s (%s) [%s]", 
                oldStatus, statusChangeDto.orderStatus(), 
                statusChangeDto.reason(), LocalDate.now());
        
        String remarks = order.getRemarks();
        order.setRemarks(remarks != null ? remarks + "\n" + statusChangeLog : statusChangeLog);
        
        order = orderRepository.save(order);
        
        log.info("주문 상태 변경 완료: ID={}, {}→{}", orderId, oldStatus, statusChangeDto.orderStatus());
        return mapToResponseDto(order);
    }

    @Override
    @Transactional
    public OrderDto.OrderResponseDto confirmOrder(Long orderId) {
        log.info("주문 확정: ID={}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        
        order.confirm();
        order = orderRepository.save(order);
        
        log.info("주문 확정 완료: ID={}", orderId);
        return mapToResponseDto(order);
    }

    @Override
    @Transactional
    public OrderDto.OrderResponseDto shipOrder(Long orderId, OrderDto.OrderShipmentDto shipmentDto) {
        log.info("주문 배송 처리: ID={}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        
        order.ship(shipmentDto.courierCompany(), shipmentDto.trackingNumber());
        if (shipmentDto.remarks() != null) {
            order.setRemarks(order.getRemarks() + "\n배송처리: " + shipmentDto.remarks());
        }
        
        order = orderRepository.save(order);
        
        log.info("주문 배송 처리 완료: ID={}, 택배사={}, 송장번호={}", 
                orderId, shipmentDto.courierCompany(), shipmentDto.trackingNumber());
        return mapToResponseDto(order);
    }

    @Override
    @Transactional
    public OrderDto.OrderResponseDto markOrderAsDelivered(Long orderId) {
        log.info("주문 배송완료 처리: ID={}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        
        order.markAsDelivered();
        order = orderRepository.save(order);
        
        log.info("주문 배송완료 처리 완료: ID={}", orderId);
        return mapToResponseDto(order);
    }

    @Override
    @Transactional
    public OrderDto.OrderResponseDto completeOrder(Long orderId) {
        log.info("주문 완료 처리: ID={}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        
        order.complete();
        order = orderRepository.save(order);
        
        // 고객 주문 통계 업데이트
        customerService.updateCustomerOrderStatistics(order.getCustomer().getId());
        
        log.info("주문 완료 처리 완료: ID={}", orderId);
        return mapToResponseDto(order);
    }

    @Override
    @Transactional
    public OrderDto.OrderResponseDto cancelOrder(Long orderId, OrderDto.OrderCancellationDto cancellationDto) {
        log.info("주문 취소: ID={}, 사유={}", orderId, cancellationDto.cancellationReason());
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        
        order.cancel(cancellationDto.cancellationReason());
        order = orderRepository.save(order);
        
        // 고객 주문 통계 업데이트
        customerService.updateCustomerOrderStatistics(order.getCustomer().getId());
        
        log.info("주문 취소 완료: ID={}", orderId);
        return mapToResponseDto(order);
    }

    @Override
    @Transactional
    public OrderDto.OrderResponseDto processOrderPayment(Long orderId, OrderDto.OrderPaymentDto paymentDto) {
        log.info("주문 결제 처리: ID={}, 금액={}", orderId, paymentDto.paymentAmount());
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ExceptionUtils.throwEntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        
        order.processPayment(paymentDto.paymentAmount());
        
        // 결제 내역 기록
        String paymentLog = String.format("결제처리: %s원 (%s) [%s] - %s", 
                paymentDto.paymentAmount(), 
                paymentDto.paymentMethod(),
                paymentDto.paymentDate() != null ? paymentDto.paymentDate() : LocalDate.now(),
                paymentDto.remarks() != null ? paymentDto.remarks() : "");
        
        String remarks = order.getRemarks();
        order.setRemarks(remarks != null ? remarks + "\n" + paymentLog : paymentLog);
        
        order = orderRepository.save(order);
        
        // 고객 주문 통계 업데이트
        customerService.updateCustomerOrderStatistics(order.getCustomer().getId());
        
        log.info("주문 결제 처리 완료: ID={}, 결제금액={}", orderId, paymentDto.paymentAmount());
        return mapToResponseDto(order);
    }

    @Override
    public boolean isOrderNumberDuplicate(Long companyId, String orderNumber) {
        return orderRepository.existsByCompanyIdAndOrderNumberAndIsDeletedFalse(companyId, orderNumber);
    }

    @Override
    public String generateOrderNumber(Long companyId) {
        String prefix = "ORD";
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 오늘 날짜의 주문 수 조회
        List<Order> todayOrders = orderRepository.findTodayOrders(companyId, LocalDate.now());
        int sequence = todayOrders.size() + 1;
        
        return String.format("%s-%s-%04d", prefix, dateStr, sequence);
    }

    // 나머지 메서드들은 간단한 위임 또는 기본 구현
    @Override
    public List<Object[]> getOrderStatsBySalesRep(Long companyId) {
        return orderRepository.getOrderStatsBySalesRep(companyId);
    }

    @Override
    public List<Object[]> getOrderStatsByStatus(Long companyId) {
        return orderRepository.getOrderStatsByStatus(companyId);
    }

    @Override
    public List<Object[]> getOrderStatsByMonth(Long companyId, LocalDate fromDate) {
        return orderRepository.getOrderStatsByMonth(companyId, fromDate);
    }

    @Override
    public List<Object[]> getOrderStatsByDay(Long companyId, LocalDate fromDate) {
        return orderRepository.getOrderStatsByDay(companyId, fromDate);
    }

    @Override
    public List<Object[]> getOrderStatsByCustomer(Long companyId) {
        return orderRepository.getOrderStatsByCustomer(companyId);
    }

    @Override
    public List<Object[]> getOrderStatsByDeliveryMethod(Long companyId) {
        return orderRepository.getOrderStatsByDeliveryMethod(companyId);
    }

    @Override
    public List<Object[]> getOrderStatsByPaymentMethod(Long companyId) {
        return orderRepository.getOrderStatsByPaymentMethod(companyId);
    }

    @Override
    public Double getAverageProcessingDays(Long companyId, LocalDate fromDate) {
        // Repository 메서드가 주석 처리되어 임시로 0.0 반환
        // TODO: 서비스 레이어에서 직접 계산하도록 구현 필요
        return 0.0;
    }

    @Override
    public Double getCancellationRate(Long companyId, LocalDate fromDate) {
        return orderRepository.getCancellationRate(companyId, fromDate);
    }

    @Override
    public Long getReorderCustomerCount(Long companyId) {
        return orderRepository.getReorderCustomerCount(companyId);
    }

    // 나머지 메서드들은 기본 구현 (실제 프로젝트에서는 상세 구현 필요)
    @Override
    @Transactional
    public OrderDto.OrderResponseDto approveOrder(Long orderId, String approverNote) {
        // TODO: 승인 로직 구현
        return getOrder(orderId);
    }

    @Override
    @Transactional
    public OrderDto.OrderResponseDto returnOrder(Long orderId, String returnReason) {
        // TODO: 반품 로직 구현
        return getOrder(orderId);
    }

    @Override
    @Transactional
    public OrderDto.OrderResponseDto exchangeOrder(Long orderId, String exchangeReason) {
        // TODO: 교환 로직 구현
        return getOrder(orderId);
    }

    @Override
    @Transactional
    public OrderDto.OrderResponseDto partialShipOrder(Long orderId, List<OrderDto.OrderItemResponseDto> shippedItems) {
        // TODO: 부분 배송 로직 구현
        return getOrder(orderId);
    }

    @Override
    public void sendOrderNotification(Long orderId, String notificationType) {
        // TODO: 주문 알림 발송 구현
        log.info("주문 알림 발송: orderId={}, type={}", orderId, notificationType);
    }

    @Override
    public void sendOrderReminders(Long companyId) {
        // TODO: 주문 리마인더 발송 구현
        log.info("주문 리마인더 발송: companyId={}", companyId);
    }

    @Override
    public byte[] generateOrderReport(Long companyId, LocalDate fromDate, LocalDate toDate, String reportType) {
        // TODO: 주문 보고서 생성 구현
        log.info("주문 보고서 생성: companyId={}, type={}", companyId, reportType);
        return new byte[0];
    }

    @Override
    public byte[] exportOrderData(Long companyId, OrderDto.OrderSearchDto searchDto, String format) {
        // TODO: 주문 데이터 내보내기 구현
        log.info("주문 데이터 내보내기: companyId={}, format={}", companyId, format);
        return new byte[0];
    }

    @Override
    @Transactional
    public List<OrderDto.OrderResponseDto> bulkProcessOrders(List<Long> orderIds, String action, String note) {
        // TODO: 주문 일괄 처리 구현
        log.info("주문 일괄 처리: orderIds={}, action={}", orderIds.size(), action);
        return List.of();
    }

    @Override
    public List<Object[]> getOrderForecastAnalysis(Long companyId, int forecastDays) {
        // TODO: 주문 예측 분석 구현
        log.info("주문 예측 분석: companyId={}, days={}", companyId, forecastDays);
        return List.of();
    }

    @Override
    public List<Object[]> getOrderPatternAnalysis(Long companyId, LocalDate fromDate, LocalDate toDate) {
        // TODO: 주문 패턴 분석 구현
        log.info("주문 패턴 분석: companyId={}", companyId);
        return List.of();
    }

    @Override
    public List<Object[]> getSeasonalOrderAnalysis(Long companyId) {
        // TODO: 계절성 주문 분석 구현
        log.info("계절성 주문 분석: companyId={}", companyId);
        return List.of();
    }

    @Override
    public List<OrderDto.OrderSummaryDto> detectAnomalousOrders(Long companyId, LocalDate fromDate) {
        // TODO: 주문 이상 감지 구현
        log.info("주문 이상 감지: companyId={}", companyId);
        return List.of();
    }

    // Private helper methods
    private Order createOrderEntity(OrderDto.OrderCreateDto createDto, String orderNumber) {
        Order order = new Order();
        
        order.setOrderNumber(orderNumber);
        // Order 엔티티는 company, customer, quote 객체를 사용
        // 이들은 createOrder 메서드에서 설정됨
        order.setOrderDate(createDto.orderDate());
        order.setRequiredDate(createDto.requiredDate());
        order.setPromisedDate(createDto.promisedDate());
        order.setOrderType(createDto.orderType());
        order.setPaymentStatus(createDto.paymentStatus());
        order.setTitle(createDto.title());
        order.setDescription(createDto.description());
        
        // 영업 담당자 정보
        order.setSalesRepId(createDto.salesRepId());
        order.setSalesRepName(createDto.salesRepName());
        
        // 할인 정보
        order.setDiscountRate(createDto.discountRate());
        order.setDiscountAmount(createDto.discountAmount());
        order.setTaxRate(createDto.taxRate());
        
        // 배송 정보
        order.setDeliveryName(createDto.deliveryName());
        order.setDeliveryPhone(createDto.deliveryPhone());
        order.setDeliveryPostalCode(createDto.deliveryPostalCode());
        order.setDeliveryAddress(createDto.deliveryAddress());
        order.setDeliveryAddressDetail(createDto.deliveryAddressDetail());
        order.setDeliveryMethod(createDto.deliveryMethod());
        order.setDeliveryFee(createDto.deliveryFee());
        order.setDeliveryMemo(createDto.deliveryMemo());
        
        // 결제 정보
        order.setPaymentTerms(createDto.paymentTerms());
        order.setPaymentMethod(createDto.paymentMethod());
        order.setPaymentDueDate(createDto.paymentDueDate());
        
        // 특별 조건
        order.setSpecialInstructions(createDto.specialInstructions());
        order.setRemarks(createDto.remarks());
        
        // 추가 정보
        order.setTags(createDto.tags());
        order.setSortOrder(createDto.sortOrder());
        order.setMetadata(createDto.metadata());
        
        return order;
    }

    private OrderItem createOrderItemEntity(OrderDto.OrderItemCreateDto itemDto) {
        OrderItem orderItem = new OrderItem();
        
        orderItem.setProductId(itemDto.productId());
        orderItem.setProductCode(itemDto.productCode());
        orderItem.setProductName(itemDto.productName());
        orderItem.setProductDescription(itemDto.productDescription());
        orderItem.setQuantity(itemDto.quantity());
        orderItem.setUnit(itemDto.unit());
        orderItem.setUnitPrice(itemDto.unitPrice());
        orderItem.setDiscountRate(itemDto.discountRate());
        orderItem.setDiscountAmount(itemDto.discountAmount());
        orderItem.setRemarks(itemDto.remarks());
        orderItem.setSortOrder(itemDto.sortOrder());
        
        orderItem.calculateTotalPrice();
        
        return orderItem;
    }

    private void updateOrderEntity(Order order, OrderDto.OrderUpdateDto updateDto) {
        if (updateDto.requiredDate() != null) order.setRequiredDate(updateDto.requiredDate());
        if (updateDto.promisedDate() != null) order.setPromisedDate(updateDto.promisedDate());
        if (updateDto.orderType() != null) order.setOrderType(updateDto.orderType());
        if (updateDto.title() != null) order.setTitle(updateDto.title());
        if (updateDto.description() != null) order.setDescription(updateDto.description());
        
        // 영업 담당자 정보
        if (updateDto.salesRepId() != null) order.setSalesRepId(updateDto.salesRepId());
        if (updateDto.salesRepName() != null) order.setSalesRepName(updateDto.salesRepName());
        
        // 할인 정보
        if (updateDto.discountRate() != null) order.setDiscountRate(updateDto.discountRate());
        if (updateDto.discountAmount() != null) order.setDiscountAmount(updateDto.discountAmount());
        if (updateDto.taxRate() != null) order.setTaxRate(updateDto.taxRate());
        
        // 배송 정보
        if (updateDto.deliveryName() != null) order.setDeliveryName(updateDto.deliveryName());
        if (updateDto.deliveryPhone() != null) order.setDeliveryPhone(updateDto.deliveryPhone());
        if (updateDto.deliveryPostalCode() != null) order.setDeliveryPostalCode(updateDto.deliveryPostalCode());
        if (updateDto.deliveryAddress() != null) order.setDeliveryAddress(updateDto.deliveryAddress());
        if (updateDto.deliveryAddressDetail() != null) order.setDeliveryAddressDetail(updateDto.deliveryAddressDetail());
        if (updateDto.deliveryMethod() != null) order.setDeliveryMethod(updateDto.deliveryMethod());
        if (updateDto.deliveryFee() != null) order.setDeliveryFee(updateDto.deliveryFee());
        if (updateDto.deliveryMemo() != null) order.setDeliveryMemo(updateDto.deliveryMemo());
        
        // 결제 정보
        if (updateDto.paymentTerms() != null) order.setPaymentTerms(updateDto.paymentTerms());
        if (updateDto.paymentMethod() != null) order.setPaymentMethod(updateDto.paymentMethod());
        if (updateDto.paymentDueDate() != null) order.setPaymentDueDate(updateDto.paymentDueDate());
        
        // 특별 조건
        if (updateDto.specialInstructions() != null) order.setSpecialInstructions(updateDto.specialInstructions());
        if (updateDto.remarks() != null) order.setRemarks(updateDto.remarks());
        
        // 추가 정보
        if (updateDto.tags() != null) order.setTags(updateDto.tags());
        if (updateDto.sortOrder() != null) order.setSortOrder(updateDto.sortOrder());
        if (updateDto.metadata() != null) order.setMetadata(updateDto.metadata());
        
        // 금액 재계산
        order.recalculateAmounts();
    }

    private OrderDto.OrderResponseDto mapToResponseDto(Order order) {
        return new OrderDto.OrderResponseDto(
                order.getId(),
                order.getOrderNumber(),
                order.getCompany().getId(),
                order.getCompany() != null ? order.getCompany().getName() : null,
                order.getCustomer().getId(),
                order.getCustomer().getCustomerCode(),
                order.getCustomer().getCustomerName(),
                order.getQuote() != null ? order.getQuote().getId() : null,
                order.getQuote() != null ? order.getQuote().getQuoteNumber() : null,
                order.getOrderDate(),
                order.getRequiredDate(),
                order.getPromisedDate(),
                order.getDeliveryDate(),
                order.getOrderStatus(),
                order.getOrderStatusDescription(),
                order.getOrderType(),
                order.getOrderTypeDescription(),
                order.getPaymentStatus(),
                order.getPaymentStatusDescription(),
                order.getTitle(),
                order.getDescription(),
                order.getSalesRepId(),
                order.getSalesRepName(),
                order.getSubtotal(),
                order.getDiscountAmount(),
                order.getDiscountRate(),
                order.getTaxAmount(),
                order.getTaxRate(),
                order.getTotalAmount(),
                order.getPaidAmount(),
                order.getOutstandingAmount(),
                order.getDeliveryName(),
                order.getDeliveryPhone(),
                order.getDeliveryPostalCode(),
                order.getDeliveryAddress(),
                order.getDeliveryAddressDetail(),
                order.getFullDeliveryAddress(),
                order.getDeliveryMethod(),
                order.getDeliveryFee(),
                order.getDeliveryMemo(),
                order.getCourierCompany(),
                order.getTrackingNumber(),
                order.getPaymentTerms(),
                order.getPaymentMethod(),
                order.getPaymentDueDate(),
                order.getPaymentCompletedDate(),
                order.getConfirmedDate(),
                order.getShippedDate(),
                order.getDeliveredDate(),
                order.getCompletedDate(),
                order.getCancelledDate(),
                order.getCancellationReason(),
                order.getSpecialInstructions(),
                order.getRemarks(),
                order.getTags(),
                order.getSortOrder(),
                order.getMetadata(),
                order.getCreatedAt() != null ? order.getCreatedAt().toString() : null,
                order.getUpdatedAt() != null ? order.getUpdatedAt().toString() : null,
                mapToOrderItemResponseDtos(order.getOrderItems()),
                order.isEditable(),
                order.isConfirmable(),
                order.isCancellable(),
                order.isShippable(),
                order.getOrderSummary(),
                order.getTotalItemCount(),
                order.getDaysUntilDelivery()
        );
    }

    private List<OrderDto.OrderItemResponseDto> mapToOrderItemResponseDtos(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::mapToOrderItemResponseDto)
                .collect(Collectors.toList());
    }

    private OrderDto.OrderItemResponseDto mapToOrderItemResponseDto(OrderItem orderItem) {
        return new OrderDto.OrderItemResponseDto(
                orderItem.getId(),
                orderItem.getOrder().getId(),
                orderItem.getProductId(),
                orderItem.getLineNumber(),
                orderItem.getProductCode(),
                orderItem.getProductName(),
                orderItem.getProductDescription(),
                orderItem.getQuantity(),
                orderItem.getShippedQuantity(),
                orderItem.getDeliveredQuantity(),
                orderItem.getCancelledQuantity(),
                orderItem.getUnit(),
                orderItem.getUnitPrice(),
                orderItem.getDiscountRate(),
                orderItem.getDiscountAmount(),
                orderItem.getTotalPrice(),
                com.erp.sales.entity.OrderItem.DeliveryStatus.PENDING, // 기본값
                orderItem.getDeliveryStatusDescription(),
                orderItem.getRemarks(),
                orderItem.getSortOrder(),
                0, // pendingQuantity - 기본값
                false, // hasPendingQuantity - 기본값
                false, // isPartiallyShipped - 기본값
                false, // isFullyShipped - 기본값
                false, // isFullyDelivered - 기본값
                false, // hasDiscount - 기본값
                orderItem.getUnitPrice(), // discountedUnitPrice - 기본값
                "주문 항목", // lineSummary - 기본값
                0.0 // deliveryProgress - 기본값
        );
    }

    private OrderDto.OrderSummaryDto mapToSummaryDto(Order order) {
        return new OrderDto.OrderSummaryDto(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getCustomer().getCustomerCode(),
                order.getCustomer().getCustomerName(),
                order.getOrderDate(),
                order.getRequiredDate(),
                order.getDeliveryDate(),
                order.getOrderStatus(),
                order.getOrderStatusDescription(),
                order.getOrderType(),
                order.getOrderTypeDescription(),
                order.getPaymentStatus(),
                order.getPaymentStatusDescription(),
                order.getTotalAmount(),
                order.getPaidAmount(),
                order.getOutstandingAmount(),
                order.getSalesRepName(),
                order.getTotalItemCount(),
                order.getCreatedAt() != null ? order.getCreatedAt().toString() : null,
                order.isEditable(),
                order.isCancellable(),
                order.getDaysUntilDelivery()
        );
    }

    private Double calculateAverageItemsPerOrder(Long companyId) {
        List<Order> orders = orderRepository.findByCompanyIdAndIsDeletedFalse(companyId, Pageable.unpaged()).getContent();
        if (orders.isEmpty()) return 0.0;
        
        double totalItems = orders.stream()
                .mapToInt(Order::getTotalItemCount)
                .sum();
        
        return totalItems / orders.size();
    }
}
