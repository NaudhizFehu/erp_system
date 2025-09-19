package com.erp.sales.service.impl;

import com.erp.common.entity.Company;
import com.erp.common.entity.User;
import com.erp.common.service.NotificationService;
import com.erp.sales.entity.Customer;
import com.erp.sales.entity.Order;
import com.erp.sales.dto.OrderDto;
import com.erp.sales.repository.OrderRepository;
import com.erp.sales.service.OrderService;
import com.erp.common.repository.CompanyRepository;
import com.erp.sales.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 주문 서비스 구현체
 * 실제 DB 스키마와 완전히 일치하도록 수정됨
 */
@Service
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private NotificationService notificationService;


    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto.OrderResponseDto> getAllOrders(Long companyId, Pageable pageable) {
        log.info("주문 목록 조회 요청: companyId={}, page={}, size={}", companyId, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Order> orders = orderRepository.findByCompanyIdAndIsDeletedFalse(companyId, pageable);
        return orders.map(OrderDto.OrderResponseDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto.OrderResponseDto> getOrderById(Long id) {
        log.info("주문 상세 조회 요청: id={}", id);
        
        return orderRepository.findByIdAndIsDeletedFalse(id)
                .map(OrderDto.OrderResponseDto::from);
    }

    @Override
    public OrderDto.OrderResponseDto createOrder(OrderDto.OrderCreateDto createDto) {
        log.info("주문 생성 요청: orderNumber={}, companyId={}, customerId={}", 
                createDto.orderNumber(), createDto.companyId(), createDto.customerId());

        // 회사 조회
        Company company = companyRepository.findById(createDto.companyId())
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다: " + createDto.companyId()));

        // 고객 조회
        Customer customer = customerRepository.findById(createDto.customerId())
                .orElseThrow(() -> new IllegalArgumentException("고객을 찾을 수 없습니다: " + createDto.customerId()));

        // 견적 조회 (선택사항)

        // 주문 생성
        Order order = new Order();
        order.setOrderNumber(createDto.orderNumber());
        order.setCompany(company);
        order.setCustomer(customer);
        order.setOrderDate(createDto.orderDate());
        order.setDeliveryDate(createDto.deliveryDate());
        order.setOrderStatus(createDto.orderStatus());
        order.setPaymentStatus(createDto.paymentStatus());
        order.setTotalAmount(createDto.totalAmount());

        Order savedOrder = orderRepository.save(order);
        log.info("주문 생성 완료: id={}, orderNumber={}", savedOrder.getId(), savedOrder.getOrderNumber());

        // 주문 생성 알림 발송 (관리자에게)
        try {
            // 임시로 사용자 ID 1 (admin)에게 알림 발송
            // 실제로는 SecurityContext에서 현재 사용자 정보를 가져와야 함
            User adminUser = new User();
            adminUser.setId(1L);
            
            String title = "새로운 주문 생성";
            String message = String.format("고객 '%s'님이 새로운 주문을 생성했습니다. (주문번호: %s)", 
                    customer.getCustomerName(), savedOrder.getOrderNumber());
            String actionUrl = "/orders/" + savedOrder.getId();
            
            notificationService.createOrderNotification(adminUser, title, message, actionUrl);
            log.info("주문 생성 알림 발송 완료: orderId={}", savedOrder.getId());
        } catch (Exception e) {
            log.error("주문 생성 알림 발송 실패: orderId={}, error={}", savedOrder.getId(), e.getMessage());
            // 알림 발송 실패는 주문 생성에 영향을 주지 않음
        }

        return OrderDto.OrderResponseDto.from(savedOrder);
    }

    @Override
    public OrderDto.OrderResponseDto updateOrder(Long id, OrderDto.OrderUpdateDto updateDto) {
        log.info("주문 수정 요청: id={}", id);

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + id));

        // 주문 수정
        if (updateDto.deliveryDate() != null) {
            order.setDeliveryDate(updateDto.deliveryDate());
        }
        if (updateDto.orderStatus() != null) {
            order.setOrderStatus(updateDto.orderStatus());
        }
        if (updateDto.paymentStatus() != null) {
            order.setPaymentStatus(updateDto.paymentStatus());
        }
        if (updateDto.totalAmount() != null) {
            order.setTotalAmount(updateDto.totalAmount());
        }

        Order savedOrder = orderRepository.save(order);
        log.info("주문 수정 완료: id={}, orderNumber={}", savedOrder.getId(), savedOrder.getOrderNumber());

        return OrderDto.OrderResponseDto.from(savedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        log.info("주문 삭제 요청: id={}", id);

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + id));

        order.setIsDeleted(true);
        orderRepository.save(order);
        log.info("주문 삭제 완료: id={}, orderNumber={}", order.getId(), order.getOrderNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto.OrderResponseDto> searchOrders(Long companyId, String searchTerm, Pageable pageable) {
        log.info("주문 검색 요청: companyId={}, searchTerm={}, page={}, size={}", 
                companyId, searchTerm, pageable.getPageNumber(), pageable.getPageSize());

        Page<Order> orders = orderRepository.findByCompanyIdAndOrderNumberContainingIgnoreCaseAndIsDeletedFalse(
                companyId, searchTerm, pageable);
        return orders.map(OrderDto.OrderResponseDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto.OrderSummaryDto> getOrdersByStatus(Long companyId, Order.OrderStatus status) {
        log.info("상태별 주문 조회 요청: companyId={}, status={}", companyId, status);

        List<Order> orders = orderRepository.findByCompanyIdAndOrderStatusAndIsDeletedFalse(companyId, status);
        return orders.stream()
                .map(OrderDto.OrderSummaryDto::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto.OrderSummaryDto> getOrdersByCustomer(Long companyId, Long customerId) {
        log.info("고객별 주문 조회 요청: companyId={}, customerId={}", companyId, customerId);

        List<Order> orders = orderRepository.findByCompanyIdAndCustomerIdAndIsDeletedFalse(companyId, customerId);
        return orders.stream()
                .map(OrderDto.OrderSummaryDto::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto.OrderSummaryDto> getOrdersByDateRange(Long companyId, LocalDate startDate, LocalDate endDate) {
        log.info("기간별 주문 조회 요청: companyId={}, startDate={}, endDate={}", companyId, startDate, endDate);

        List<Order> orders = orderRepository.findByCompanyIdAndOrderDateBetweenAndIsDeletedFalse(
                companyId, startDate, endDate);
        return orders.stream()
                .map(OrderDto.OrderSummaryDto::from)
                .toList();
    }


    @Override
    public OrderDto.OrderResponseDto confirmOrder(Long id) {
        log.info("주문 확정 요청: id={}", id);

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + id));

        if (!order.isConfirmable()) {
            throw new IllegalStateException("주문을 확정할 수 없습니다. 현재 상태: " + order.getOrderStatus());
        }

        order.updateStatus(Order.OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(order);
        
        log.info("주문 확정 완료: id={}, orderNumber={}", savedOrder.getId(), savedOrder.getOrderNumber());
        return OrderDto.OrderResponseDto.from(savedOrder);
    }

    @Override
    public OrderDto.OrderResponseDto cancelOrder(Long id) {
        log.info("주문 취소 요청: id={}", id);

        Order order = orderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + id));

        if (!order.isCancellable()) {
            throw new IllegalStateException("주문을 취소할 수 없습니다. 현재 상태: " + order.getOrderStatus());
        }

        order.updateStatus(Order.OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        
        log.info("주문 취소 완료: id={}, orderNumber={}", savedOrder.getId(), savedOrder.getOrderNumber());
        return OrderDto.OrderResponseDto.from(savedOrder);
    }
}