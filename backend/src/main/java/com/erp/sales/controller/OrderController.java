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
 * 주문 관련 API 엔드포인트를 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/sales/orders")
@RequiredArgsConstructor
@Tag(name = "주문 관리", description = "주문 정보 관리 API")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> createOrder(
            @Valid @RequestBody OrderDto.OrderCreateDto createDto) {
        try {
            log.info("주문 생성 API 호출: 고객ID={}", createDto.customerId());
            OrderDto.OrderResponseDto response = orderService.createOrder(createDto);
            return ResponseEntity.ok(ApiResponse.success("주문이 성공적으로 생성되었습니다", response));
        } catch (Exception e) {
            log.error("주문 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 생성에 실패했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/from-quote")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    @Operation(summary = "견적서에서 주문 생성", description = "견적서를 기반으로 주문을 생성합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> createOrderFromQuote(
            @Valid @RequestBody OrderDto.OrderFromQuoteDto fromQuoteDto) {
        try {
            log.info("견적서에서 주문 생성 API 호출: 견적ID={}", fromQuoteDto.quoteId());
            OrderDto.OrderResponseDto response = orderService.createOrderFromQuote(fromQuoteDto);
            return ResponseEntity.ok(ApiResponse.success("견적서에서 주문이 성공적으로 생성되었습니다", response));
        } catch (Exception e) {
            log.error("견적서에서 주문 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("견적서에서 주문 생성에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "주문 수정", description = "기존 주문 정보를 수정합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> updateOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId,
            @Valid @RequestBody OrderDto.OrderUpdateDto updateDto) {
        try {
            log.info("주문 수정 API 호출: ID={}", orderId);
            OrderDto.OrderResponseDto response = orderService.updateOrder(orderId, updateDto);
            return ResponseEntity.ok(ApiResponse.success("주문 정보가 성공적으로 수정되었습니다", response));
        } catch (Exception e) {
            log.error("주문 수정 실패: ID={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 수정에 실패했습니다: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "주문 삭제", description = "주문을 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId) {
        try {
            log.info("주문 삭제 API 호출: ID={}", orderId);
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("주문이 성공적으로 삭제되었습니다"));
        } catch (Exception e) {
            log.error("주문 삭제 실패: ID={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 삭제에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 상세 조회", description = "주문 상세 정보를 조회합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> getOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId) {
        try {
            log.info("주문 상세 조회 API 호출: ID={}", orderId);
            OrderDto.OrderResponseDto response = orderService.getOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("주문 정보 조회 성공", response));
        } catch (Exception e) {
            log.error("주문 조회 실패: ID={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문번호로 조회", description = "주문번호로 주문 정보를 조회합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> getOrderByNumber(
            @Parameter(description = "주문 번호") @PathVariable String orderNumber) {
        try {
            log.info("주문번호 조회 API 호출: {}", orderNumber);
            OrderDto.OrderResponseDto response = orderService.getOrderByNumber(orderNumber);
            return ResponseEntity.ok(ApiResponse.success("주문 정보 조회 성공", response));
        } catch (Exception e) {
            log.error("주문번호 조회 실패: {}, {}", orderNumber, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "회사별 주문 목록 조회", description = "특정 회사의 주문 목록을 조회합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderSummaryDto>>> getOrdersByCompany(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("회사별 주문 목록 조회 API 호출: companyId={}", companyId);
            Page<OrderDto.OrderSummaryDto> response = orderService.getOrdersByCompany(companyId, pageable);
            return ResponseEntity.ok(ApiResponse.success("주문 목록 조회 성공", response));
        } catch (Exception e) {
            log.error("주문 목록 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객별 주문 조회", description = "특정 고객의 주문을 조회합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderSummaryDto>>> getOrdersByCustomer(
            @Parameter(description = "고객 ID") @PathVariable Long customerId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("고객별 주문 조회 API 호출: customerId={}", customerId);
            Page<OrderDto.OrderSummaryDto> response = orderService.getOrdersByCustomer(customerId, pageable);
            return ResponseEntity.ok(ApiResponse.success("고객별 주문 조회 성공", response));
        } catch (Exception e) {
            log.error("고객별 주문 조회 실패: customerId={}, {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객별 주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/sales-rep/{salesRepId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "영업담당자별 주문 조회", description = "특정 영업담당자의 주문을 조회합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderSummaryDto>>> getOrdersBySalesRep(
            @Parameter(description = "영업담당자 ID") @PathVariable Long salesRepId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("영업담당자별 주문 조회 API 호출: salesRepId={}", salesRepId);
            Page<OrderDto.OrderSummaryDto> response = orderService.getOrdersBySalesRep(salesRepId, pageable);
            return ResponseEntity.ok(ApiResponse.success("영업담당자별 주문 조회 성공", response));
        } catch (Exception e) {
            log.error("영업담당자별 주문 조회 실패: salesRepId={}, {}", salesRepId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("영업담당자별 주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/search")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 검색", description = "주문번호, 고객명으로 주문을 검색합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderSummaryDto>>> searchOrders(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "검색어") @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("주문 검색 API 호출: companyId={}, searchTerm={}", companyId, searchTerm);
            Page<OrderDto.OrderSummaryDto> response = orderService.searchOrders(companyId, searchTerm, pageable);
            return ResponseEntity.ok(ApiResponse.success("주문 검색 성공", response));
        } catch (Exception e) {
            log.error("주문 검색 실패: companyId={}, searchTerm={}, {}", companyId, searchTerm, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 검색에 실패했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/company/{companyId}/search/advanced")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 고급 검색", description = "다양한 조건으로 주문을 검색합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderSummaryDto>>> searchOrdersAdvanced(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Valid @RequestBody OrderDto.OrderSearchDto searchDto,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("주문 고급 검색 API 호출: companyId={}", companyId);
            Page<OrderDto.OrderSummaryDto> response = orderService.searchOrdersAdvanced(companyId, searchDto, pageable);
            return ResponseEntity.ok(ApiResponse.success("주문 고급 검색 성공", response));
        } catch (Exception e) {
            log.error("주문 고급 검색 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 고급 검색에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/status/{orderStatus}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 상태별 조회", description = "특정 상태의 주문을 조회합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderSummaryDto>>> getOrdersByStatus(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "주문 상태") @PathVariable String orderStatus,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("주문 상태별 조회 API 호출: companyId={}, status={}", companyId, orderStatus);
            Page<OrderDto.OrderSummaryDto> response = orderService.getOrdersByStatus(companyId, orderStatus, pageable);
            return ResponseEntity.ok(ApiResponse.success("주문 상태별 조회 성공", response));
        } catch (Exception e) {
            log.error("주문 상태별 조회 실패: companyId={}, status={}, {}", companyId, orderStatus, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 상태별 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/type/{orderType}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 유형별 조회", description = "특정 유형의 주문을 조회합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderSummaryDto>>> getOrdersByType(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "주문 유형") @PathVariable String orderType,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("주문 유형별 조회 API 호출: companyId={}, type={}", companyId, orderType);
            Page<OrderDto.OrderSummaryDto> response = orderService.getOrdersByType(companyId, orderType, pageable);
            return ResponseEntity.ok(ApiResponse.success("주문 유형별 조회 성공", response));
        } catch (Exception e) {
            log.error("주문 유형별 조회 실패: companyId={}, type={}, {}", companyId, orderType, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 유형별 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/payment-status/{paymentStatus}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "결제 상태별 조회", description = "특정 결제 상태의 주문을 조회합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderSummaryDto>>> getOrdersByPaymentStatus(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "결제 상태") @PathVariable String paymentStatus,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("결제 상태별 조회 API 호출: companyId={}, paymentStatus={}", companyId, paymentStatus);
            Page<OrderDto.OrderSummaryDto> response = orderService.getOrdersByPaymentStatus(companyId, paymentStatus, pageable);
            return ResponseEntity.ok(ApiResponse.success("결제 상태별 조회 성공", response));
        } catch (Exception e) {
            log.error("결제 상태별 조회 실패: companyId={}, paymentStatus={}, {}", companyId, paymentStatus, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("결제 상태별 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/outstanding")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "미수금 주문 조회", description = "미수금이 있는 주문을 조회합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderSummaryDto>>> getOrdersWithOutstanding(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("미수금 주문 조회 API 호출: companyId={}", companyId);
            Page<OrderDto.OrderSummaryDto> response = orderService.getOrdersWithOutstanding(companyId, pageable);
            return ResponseEntity.ok(ApiResponse.success("미수금 주문 조회 성공", response));
        } catch (Exception e) {
            log.error("미수금 주문 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("미수금 주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/overdue")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "연체 주문 조회", description = "연체된 주문을 조회합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderSummaryDto>>> getOverdueOrders(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("연체 주문 조회 API 호출: companyId={}", companyId);
            Page<OrderDto.OrderSummaryDto> response = orderService.getOverdueOrders(companyId, pageable);
            return ResponseEntity.ok(ApiResponse.success("연체 주문 조회 성공", response));
        } catch (Exception e) {
            log.error("연체 주문 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("연체 주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/urgent")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "긴급 주문 조회", description = "긴급 주문을 조회합니다")
    public ResponseEntity<ApiResponse<List<OrderDto.OrderSummaryDto>>> getUrgentOrders(
            @Parameter(description = "회사 ID") @PathVariable Long companyId) {
        try {
            log.info("긴급 주문 조회 API 호출: companyId={}", companyId);
            List<OrderDto.OrderSummaryDto> response = orderService.getUrgentOrders(companyId);
            return ResponseEntity.ok(ApiResponse.success("긴급 주문 조회 성공", response));
        } catch (Exception e) {
            log.error("긴급 주문 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("긴급 주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/delayed")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "배송 지연 주문 조회", description = "배송이 지연된 주문을 조회합니다")
    public ResponseEntity<ApiResponse<List<OrderDto.OrderSummaryDto>>> getDelayedOrders(
            @Parameter(description = "회사 ID") @PathVariable Long companyId) {
        try {
            log.info("배송 지연 주문 조회 API 호출: companyId={}", companyId);
            List<OrderDto.OrderSummaryDto> response = orderService.getDelayedOrders(companyId);
            return ResponseEntity.ok(ApiResponse.success("배송 지연 주문 조회 성공", response));
        } catch (Exception e) {
            log.error("배송 지연 주문 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("배송 지연 주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/top")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "상위 주문 조회", description = "주문금액 기준 상위 주문을 조회합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderSummaryDto>>> getTopOrders(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            log.info("상위 주문 조회 API 호출: companyId={}", companyId);
            Page<OrderDto.OrderSummaryDto> response = orderService.getTopOrders(companyId, pageable);
            return ResponseEntity.ok(ApiResponse.success("상위 주문 조회 성공", response));
        } catch (Exception e) {
            log.error("상위 주문 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("상위 주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/recent")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "최근 주문 조회", description = "최근 주문을 조회합니다")
    public ResponseEntity<ApiResponse<Page<OrderDto.OrderSummaryDto>>> getRecentOrders(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("최근 주문 조회 API 호출: companyId={}", companyId);
            Page<OrderDto.OrderSummaryDto> response = orderService.getRecentOrders(companyId, pageable);
            return ResponseEntity.ok(ApiResponse.success("최근 주문 조회 성공", response));
        } catch (Exception e) {
            log.error("최근 주문 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("최근 주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/today")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "오늘 주문 조회", description = "오늘 접수된 주문을 조회합니다")
    public ResponseEntity<ApiResponse<List<OrderDto.OrderSummaryDto>>> getTodayOrders(
            @Parameter(description = "회사 ID") @PathVariable Long companyId) {
        try {
            log.info("오늘 주문 조회 API 호출: companyId={}", companyId);
            List<OrderDto.OrderSummaryDto> response = orderService.getTodayOrders(companyId);
            return ResponseEntity.ok(ApiResponse.success("오늘 주문 조회 성공", response));
        } catch (Exception e) {
            log.error("오늘 주문 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("오늘 주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/for-delivery")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "배송 예정 주문 조회", description = "배송 예정인 주문을 조회합니다")
    public ResponseEntity<ApiResponse<List<OrderDto.OrderSummaryDto>>> getOrdersForDelivery(
            @Parameter(description = "회사 ID") @PathVariable Long companyId) {
        try {
            log.info("배송 예정 주문 조회 API 호출: companyId={}", companyId);
            List<OrderDto.OrderSummaryDto> response = orderService.getOrdersForDelivery(companyId);
            return ResponseEntity.ok(ApiResponse.success("배송 예정 주문 조회 성공", response));
        } catch (Exception e) {
            log.error("배송 예정 주문 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("배송 예정 주문 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/statistics")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 통계 조회", description = "주문 관련 통계 정보를 조회합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderStatsDto>> getOrderStatistics(
            @Parameter(description = "회사 ID") @PathVariable Long companyId) {
        try {
            log.info("주문 통계 조회 API 호출: companyId={}", companyId);
            OrderDto.OrderStatsDto response = orderService.getOrderStatistics(companyId);
            return ResponseEntity.ok(ApiResponse.success("주문 통계 조회 성공", response));
        } catch (Exception e) {
            log.error("주문 통계 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 상태 변경", description = "주문의 상태를 변경합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> changeOrderStatus(
            @Parameter(description = "주문 ID") @PathVariable Long orderId,
            @Valid @RequestBody OrderDto.OrderStatusChangeDto statusChangeDto) {
        try {
            log.info("주문 상태 변경 API 호출: ID={}, 새상태={}", orderId, statusChangeDto.orderStatus());
            OrderDto.OrderResponseDto response = orderService.changeOrderStatus(orderId, statusChangeDto);
            return ResponseEntity.ok(ApiResponse.success("주문 상태가 성공적으로 변경되었습니다", response));
        } catch (Exception e) {
            log.error("주문 상태 변경 실패: ID={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 상태 변경에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/confirm")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 확정", description = "주문을 확정합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> confirmOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId) {
        try {
            log.info("주문 확정 API 호출: ID={}", orderId);
            OrderDto.OrderResponseDto response = orderService.confirmOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("주문이 성공적으로 확정되었습니다", response));
        } catch (Exception e) {
            log.error("주문 확정 실패: ID={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 확정에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/ship")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 배송 처리", description = "주문의 배송을 처리합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> shipOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId,
            @Valid @RequestBody OrderDto.OrderShipmentDto shipmentDto) {
        try {
            log.info("주문 배송 처리 API 호출: ID={}", orderId);
            OrderDto.OrderResponseDto response = orderService.shipOrder(orderId, shipmentDto);
            return ResponseEntity.ok(ApiResponse.success("주문 배송이 성공적으로 처리되었습니다", response));
        } catch (Exception e) {
            log.error("주문 배송 처리 실패: ID={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 배송 처리에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/delivered")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 배송완료 처리", description = "주문의 배송완료를 처리합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> markOrderAsDelivered(
            @Parameter(description = "주문 ID") @PathVariable Long orderId) {
        try {
            log.info("주문 배송완료 처리 API 호출: ID={}", orderId);
            OrderDto.OrderResponseDto response = orderService.markOrderAsDelivered(orderId);
            return ResponseEntity.ok(ApiResponse.success("주문 배송완료가 성공적으로 처리되었습니다", response));
        } catch (Exception e) {
            log.error("주문 배송완료 처리 실패: ID={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 배송완료 처리에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/complete")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 완료 처리", description = "주문을 완료 처리합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> completeOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId) {
        try {
            log.info("주문 완료 처리 API 호출: ID={}", orderId);
            OrderDto.OrderResponseDto response = orderService.completeOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("주문이 성공적으로 완료되었습니다", response));
        } catch (Exception e) {
            log.error("주문 완료 처리 실패: ID={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 완료 처리에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 취소", description = "주문을 취소합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> cancelOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId,
            @Valid @RequestBody OrderDto.OrderCancellationDto cancellationDto) {
        try {
            log.info("주문 취소 API 호출: ID={}, 사유={}", orderId, cancellationDto.cancellationReason());
            OrderDto.OrderResponseDto response = orderService.cancelOrder(orderId, cancellationDto);
            return ResponseEntity.ok(ApiResponse.success("주문이 성공적으로 취소되었습니다", response));
        } catch (Exception e) {
            log.error("주문 취소 실패: ID={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 취소에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/payment")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문 결제 처리", description = "주문의 결제를 처리합니다")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponseDto>> processOrderPayment(
            @Parameter(description = "주문 ID") @PathVariable Long orderId,
            @Valid @RequestBody OrderDto.OrderPaymentDto paymentDto) {
        try {
            log.info("주문 결제 처리 API 호출: ID={}, 금액={}", orderId, paymentDto.paymentAmount());
            OrderDto.OrderResponseDto response = orderService.processOrderPayment(orderId, paymentDto);
            return ResponseEntity.ok(ApiResponse.success("주문 결제가 성공적으로 처리되었습니다", response));
        } catch (Exception e) {
            log.error("주문 결제 처리 실패: ID={}, {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 결제 처리에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/check-duplicate/number")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문번호 중복 확인", description = "주문번호의 중복 여부를 확인합니다")
    public ResponseEntity<ApiResponse<Boolean>> checkOrderNumberDuplicate(
            @Parameter(description = "회사 ID") @RequestParam Long companyId,
            @Parameter(description = "주문 번호") @RequestParam String orderNumber) {
        try {
            log.info("주문번호 중복 확인 API 호출: companyId={}, orderNumber={}", companyId, orderNumber);
            boolean isDuplicate = orderService.isOrderNumberDuplicate(companyId, orderNumber);
            return ResponseEntity.ok(ApiResponse.success("주문번호 중복 확인 완료", isDuplicate));
        } catch (Exception e) {
            log.error("주문번호 중복 확인 실패: companyId={}, orderNumber={}, {}", companyId, orderNumber, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문번호 중복 확인에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/generate-number")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "주문번호 생성", description = "새로운 주문번호를 생성합니다")
    public ResponseEntity<ApiResponse<String>> generateOrderNumber(
            @Parameter(description = "회사 ID") @PathVariable Long companyId) {
        try {
            log.info("주문번호 생성 API 호출: companyId={}", companyId);
            String orderNumber = orderService.generateOrderNumber(companyId);
            return ResponseEntity.ok(ApiResponse.success("주문번호 생성 완료", orderNumber));
        } catch (Exception e) {
            log.error("주문번호 생성 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문번호 생성에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/stats/sales-rep")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "영업담당자별 주문 통계", description = "영업담당자별 주문 통계를 조회합니다")
    public ResponseEntity<ApiResponse<List<Object[]>>> getOrderStatsBySalesRep(
            @Parameter(description = "회사 ID") @PathVariable Long companyId) {
        try {
            log.info("영업담당자별 주문 통계 API 호출: companyId={}", companyId);
            List<Object[]> response = orderService.getOrderStatsBySalesRep(companyId);
            return ResponseEntity.ok(ApiResponse.success("영업담당자별 주문 통계 조회 성공", response));
        } catch (Exception e) {
            log.error("영업담당자별 주문 통계 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("영업담당자별 주문 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/stats/monthly")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "월별 주문 통계", description = "월별 주문 통계를 조회합니다")
    public ResponseEntity<ApiResponse<List<Object[]>>> getOrderStatsByMonth(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "시작 날짜") @RequestParam(required = false) LocalDate fromDate) {
        try {
            if (fromDate == null) fromDate = LocalDate.now().minusYears(1);
            log.info("월별 주문 통계 API 호출: companyId={}, fromDate={}", companyId, fromDate);
            List<Object[]> response = orderService.getOrderStatsByMonth(companyId, fromDate);
            return ResponseEntity.ok(ApiResponse.success("월별 주문 통계 조회 성공", response));
        } catch (Exception e) {
            log.error("월별 주문 통계 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("월별 주문 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/company/{companyId}/stats/customer")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "고객별 주문 통계", description = "고객별 주문 통계를 조회합니다")
    public ResponseEntity<ApiResponse<List<Object[]>>> getOrderStatsByCustomer(
            @Parameter(description = "회사 ID") @PathVariable Long companyId) {
        try {
            log.info("고객별 주문 통계 API 호출: companyId={}", companyId);
            List<Object[]> response = orderService.getOrderStatsByCustomer(companyId);
            return ResponseEntity.ok(ApiResponse.success("고객별 주문 통계 조회 성공", response));
        } catch (Exception e) {
            log.error("고객별 주문 통계 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("고객별 주문 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }
}




