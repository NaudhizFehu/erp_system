package com.erp.sales.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.sales.dto.OrderDto;
import com.erp.sales.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 주문 관리 REST Controller
 * 실제 DB 스키마와 완전히 일치하도록 수정됨
 */
@Slf4j
@RestController
@RequestMapping("/api/sales/orders")
@RequiredArgsConstructor
@Tag(name = "주문 관리", description = "주문 정보 관리 API")
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 목록 조회
     */
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 목록 조회", description = "회사별 주문 목록을 조회합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderResponseDto>>> getAllOrders(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("주문 목록 조회 API 호출: companyId={}, page={}, size={}", 
                    companyId, pageable.getPageNumber(), pageable.getPageSize());
            Page<OrderDto.OrderResponseDto> response = orderService.getAllOrders(companyId, pageable);
            return ResponseEntity.ok(ApiResponse.success("주문 목록 조회 성공", response));
        } catch (Exception e) {
            log.error("주문 목록 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 주문 상세 조회
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 상세 조회", description = "주문 상세 정보를 조회합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> getOrderById(
            @Parameter(description = "주문 ID") @PathVariable Long orderId) {
        try {
            log.info("주문 상세 조회 API 호출: orderId={}", orderId);
            return orderService.getOrderById(orderId)
                    .map(order -> ResponseEntity.ok(ApiResponse.success("주문 상세 조회 성공", order)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("주문 상세 조회 실패: orderId={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 상세 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 주문 생성
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> createOrder(
            @Valid @RequestBody OrderDto.OrderCreateDto createDto) {
        try {
            log.info("주문 생성 API 호출: orderNumber={}, companyId={}, customerId={}", 
                    createDto.orderNumber(), createDto.companyId(), createDto.customerId());
            OrderDto.OrderResponseDto response = orderService.createOrder(createDto);
            return ResponseEntity.ok(ApiResponse.success("주문 생성 성공", response));
        } catch (Exception e) {
            log.error("주문 생성 실패: orderNumber={}, {}", createDto.orderNumber(), e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 생성에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 주문 수정
     */
    @PutMapping("/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 수정", description = "기존 주문을 수정합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> updateOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId,
            @Valid @RequestBody OrderDto.OrderUpdateDto updateDto) {
        try {
            log.info("주문 수정 API 호출: orderId={}", orderId);
            OrderDto.OrderResponseDto response = orderService.updateOrder(orderId, updateDto);
            return ResponseEntity.ok(ApiResponse.success("주문 수정 성공", response));
        } catch (Exception e) {
            log.error("주문 수정 실패: orderId={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 수정에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 주문 삭제
     */
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 삭제", description = "주문을 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId) {
        try {
            log.info("주문 삭제 API 호출: orderId={}", orderId);
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("주문 삭제 성공", null));
        } catch (Exception e) {
            log.error("주문 삭제 실패: orderId={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 삭제에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 주문 검색
     */
    @GetMapping("/company/{companyId}/search")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 검색", description = "주문번호로 주문을 검색합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderResponseDto>>> searchOrders(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "검색어") @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("주문 검색 API 호출: companyId={}, searchTerm={}", companyId, searchTerm);
            Page<OrderDto.OrderResponseDto> response = orderService.searchOrders(companyId, searchTerm, pageable);
            return ResponseEntity.ok(ApiResponse.success("주문 검색 성공", response));
        } catch (Exception e) {
            log.error("주문 검색 실패: companyId={}, searchTerm={}, {}", companyId, searchTerm, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 검색에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 상태별 주문 조회
     */
    @GetMapping("/company/{companyId}/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "상태별 주문 조회", description = "특정 상태의 주문을 조회합니다")
    public ResponseEntity<ApiResponse<List<OrderDto.OrderSummaryDto>>> getOrdersByStatus(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "주문 상태") @PathVariable String status) {
        try {
            log.info("상태별 주문 조회 API 호출: companyId={}, status={}", companyId, status);
            // String을 enum으로 변환
            com.erp.sales.entity.Order.OrderStatus orderStatus = 
                    com.erp.sales.entity.Order.OrderStatus.valueOf(status.toUpperCase());
            List<OrderDto.OrderSummaryDto> response = orderService.getOrdersByStatus(companyId, orderStatus);
            return ResponseEntity.ok(ApiResponse.success("상태별 주문 조회 성공", response));
        } catch (Exception e) {
            log.error("상태별 주문 조회 실패: companyId={}, status={}, {}", companyId, status, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("상태별 주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 고객별 주문 조회
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객별 주문 조회", description = "특정 고객의 주문을 조회합니다")
    public ResponseEntity<ApiResponse<List<OrderDto.OrderSummaryDto>>> getOrdersByCustomer(
            @Parameter(description = "고객 ID") @PathVariable Long customerId) {
        try {
            log.info("고객별 주문 조회 API 호출: customerId={}", customerId);
            List<OrderDto.OrderSummaryDto> response = orderService.getOrdersByCustomer(1L, customerId);
            return ResponseEntity.ok(ApiResponse.success("고객별 주문 조회 성공", response));
        } catch (Exception e) {
            log.error("고객별 주문 조회 실패: customerId={}, {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객별 주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 기간별 주문 조회
     */
    @GetMapping("/company/{companyId}/date-range")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "기간별 주문 조회", description = "특정 기간의 주문을 조회합니다")
    public ResponseEntity<ApiResponse<List<OrderDto.OrderSummaryDto>>> getOrdersByDateRange(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "시작일") @RequestParam LocalDate startDate,
            @Parameter(description = "종료일") @RequestParam LocalDate endDate) {
        try {
            log.info("기간별 주문 조회 API 호출: companyId={}, startDate={}, endDate={}", companyId, startDate, endDate);
            List<OrderDto.OrderSummaryDto> response = orderService.getOrdersByDateRange(companyId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("기간별 주문 조회 성공", response));
        } catch (Exception e) {
            log.error("기간별 주문 조회 실패: companyId={}, startDate={}, endDate={}, {}", 
                    companyId, startDate, endDate, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("기간별 주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }


    /**
     * 주문 확정
     */
    @PutMapping("/{orderId}/confirm")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 확정", description = "주문을 확정합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> confirmOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId) {
        try {
            log.info("주문 확정 API 호출: orderId={}", orderId);
            OrderDto.OrderResponseDto response = orderService.confirmOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("주문 확정 성공", response));
        } catch (Exception e) {
            log.error("주문 확정 실패: orderId={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 확정에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 주문 취소
     */
    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 취소", description = "주문을 취소합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> cancelOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId) {
        try {
            log.info("주문 취소 API 호출: orderId={}", orderId);
            OrderDto.OrderResponseDto response = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("주문 취소 성공", response));
        } catch (Exception e) {
            log.error("주문 취소 실패: orderId={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 취소에 실패했습니다: " + e.getMessage()));
        }
    }
}