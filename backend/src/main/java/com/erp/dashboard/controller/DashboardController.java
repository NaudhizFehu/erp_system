package com.erp.dashboard.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.common.entity.User;
import com.erp.common.repository.UserRepository;
import com.erp.dashboard.dto.DashboardDto;
import com.erp.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.responses.ApiResponse; // 별칭 대신 완전한 클래스명 사용
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 대시보드 REST Controller
 * 대시보드 관련 API 엔드포인트를 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "대시보드", description = "통계 및 현황 조회 API")
@SecurityRequirement(name = "bearer-jwt")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    @GetMapping("/overview/{companyId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "전체 현황 요약 조회", description = "대시보드 전체 현황 요약 정보를 조회합니다")
    public ResponseEntity<ApiResponse<DashboardDto.OverviewSummaryDto>> getOverviewSummary(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "시작 날짜") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "종료 날짜") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "시간 범위") @RequestParam(required = false) String timeRange) {
        try {
            log.info("전체 현황 요약 조회 API 호출: companyId={}", companyId);
            
            DashboardDto.DashboardFilterDto filter = new DashboardDto.DashboardFilterDto(
                    startDate, endDate, timeRange, null, null, null, null
            );
            
            DashboardDto.OverviewSummaryDto response = dashboardService.getOverviewSummary(companyId, filter);
            return ResponseEntity.ok(ApiResponse.success("전체 현황 요약 조회 성공", response));
        } catch (Exception e) {
            log.error("전체 현황 요약 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("전체 현황 요약 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/charts/revenue/{companyId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "매출 차트 데이터 조회", description = "매출 관련 차트 데이터를 조회합니다")
    public ResponseEntity<ApiResponse<DashboardDto.RevenueChartDto>> getRevenueChartData(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "시작 날짜") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "종료 날짜") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "시간 범위") @RequestParam(required = false) String timeRange) {
        try {
            log.info("매출 차트 데이터 조회 API 호출: companyId={}", companyId);
            
            DashboardDto.DashboardFilterDto filter = new DashboardDto.DashboardFilterDto(
                    startDate, endDate, timeRange, null, null, null, null
            );
            
            DashboardDto.RevenueChartDto response = dashboardService.getRevenueChartData(companyId, filter);
            return ResponseEntity.ok(ApiResponse.success("매출 차트 데이터 조회 성공", response));
        } catch (Exception e) {
            log.error("매출 차트 데이터 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("매출 차트 데이터 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/charts/orders/{companyId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "주문 차트 데이터 조회", description = "주문 관련 차트 데이터를 조회합니다")
    public ResponseEntity<ApiResponse<DashboardDto.OrderChartDto>> getOrderChartData(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "시작 날짜") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "종료 날짜") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "시간 범위") @RequestParam(required = false) String timeRange) {
        try {
            log.info("주문 차트 데이터 조회 API 호출: companyId={}", companyId);
            
            DashboardDto.DashboardFilterDto filter = new DashboardDto.DashboardFilterDto(
                    startDate, endDate, timeRange, null, null, null, null
            );
            
            DashboardDto.OrderChartDto response = dashboardService.getOrderChartData(companyId, filter);
            return ResponseEntity.ok(ApiResponse.success("주문 차트 데이터 조회 성공", response));
        } catch (Exception e) {
            log.error("주문 차트 데이터 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("주문 차트 데이터 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/charts/inventory/{companyId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "재고 차트 데이터 조회", description = "재고 관련 차트 데이터를 조회합니다")
    public ResponseEntity<ApiResponse<DashboardDto.InventoryChartDto>> getInventoryChartData(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "시작 날짜") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "종료 날짜") @RequestParam(required = false) LocalDate endDate) {
        try {
            log.info("재고 차트 데이터 조회 API 호출: companyId={}", companyId);
            
            DashboardDto.DashboardFilterDto filter = new DashboardDto.DashboardFilterDto(
                    startDate, endDate, null, null, null, null, null
            );
            
            DashboardDto.InventoryChartDto response = dashboardService.getInventoryChartData(companyId, filter);
            return ResponseEntity.ok(ApiResponse.success("재고 차트 데이터 조회 성공", response));
        } catch (Exception e) {
            log.error("재고 차트 데이터 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("재고 차트 데이터 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/charts/hr/{companyId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "인사 차트 데이터 조회", description = "인사 관련 차트 데이터를 조회합니다")
    public ResponseEntity<ApiResponse<DashboardDto.HrChartDto>> getHrChartData(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "시작 날짜") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "종료 날짜") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "시간 범위") @RequestParam(required = false) String timeRange) {
        try {
            log.info("인사 차트 데이터 조회 API 호출: companyId={}", companyId);
            
            DashboardDto.DashboardFilterDto filter = new DashboardDto.DashboardFilterDto(
                    startDate, endDate, timeRange, null, null, null, null
            );
            
            DashboardDto.HrChartDto response = dashboardService.getHrChartData(companyId, filter);
            return ResponseEntity.ok(ApiResponse.success("인사 차트 데이터 조회 성공", response));
        } catch (Exception e) {
            log.error("인사 차트 데이터 조회 실패: companyId={}, {}", companyId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("인사 차트 데이터 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/activities/{companyId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "최근 활동 로그 조회", description = "최근 활동 로그를 조회합니다")
    public ResponseEntity<ApiResponse<List<DashboardDto.ActivityLogDto>>> getRecentActivities(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @Parameter(description = "조회 건수") @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("최근 활동 로그 조회 API 호출: companyId={}, userId={}", companyId, userId);
            
            List<DashboardDto.ActivityLogDto> response = dashboardService.getRecentActivities(companyId, userId, limit);
            return ResponseEntity.ok(ApiResponse.success("최근 활동 로그 조회 성공", response));
        } catch (Exception e) {
            log.error("최근 활동 로그 조회 실패: companyId={}, userId={}, {}", companyId, userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("최근 활동 로그 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/notifications/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "사용자 알림 조회", description = "사용자의 알림 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<DashboardDto.NotificationDto>>> getUserNotifications(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "읽지 않은 알림만") @RequestParam(defaultValue = "false") Boolean unreadOnly,
            @Parameter(description = "조회 건수") @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("사용자 알림 조회 API 호출: userId={}, unreadOnly={}", userId, unreadOnly);
            
            List<DashboardDto.NotificationDto> response = dashboardService.getUserNotifications(userId, unreadOnly, limit);
            return ResponseEntity.ok(ApiResponse.success("사용자 알림 조회 성공", response));
        } catch (Exception e) {
            log.error("사용자 알림 조회 실패: userId={}, {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("사용자 알림 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/todos/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "사용자 할일 목록 조회", description = "사용자의 할일 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<DashboardDto.TodoItemDto>>> getUserTodoItems(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "상태 필터") @RequestParam(required = false) String status,
            @Parameter(description = "조회 건수") @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("사용자 할일 목록 조회 API 호출: userId={}, status={}", userId, status);
            
            List<DashboardDto.TodoItemDto> response = dashboardService.getUserTodoItems(userId, status, limit);
            return ResponseEntity.ok(ApiResponse.success("사용자 할일 목록 조회 성공", response));
        } catch (Exception e) {
            log.error("사용자 할일 목록 조회 실패: userId={}, {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("사용자 할일 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/quick-actions")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "빠른 액션 조회", description = "사용자 역할에 따른 빠른 액션 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<DashboardDto.QuickActionDto>>> getQuickActions(
            @Parameter(description = "사용자 역할") @RequestParam String userRole) {
        try {
            log.info("빠른 액션 조회 API 호출: userRole={}", userRole);
            
            List<DashboardDto.QuickActionDto> response = dashboardService.getQuickActions(userRole);
            return ResponseEntity.ok(ApiResponse.success("빠른 액션 조회 성공", response));
        } catch (Exception e) {
            log.error("빠른 액션 조회 실패: userRole={}, {}", userRole, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("빠른 액션 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/kpi/{companyId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "KPI 지표 조회", description = "KPI 지표를 조회합니다")
    public ResponseEntity<ApiResponse<List<DashboardDto.KpiMetricDto>>> getKpiMetrics(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "카테고리") @RequestParam(required = false) String category,
            @Parameter(description = "시작 날짜") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "종료 날짜") @RequestParam(required = false) LocalDate endDate) {
        try {
            log.info("KPI 지표 조회 API 호출: companyId={}, category={}", companyId, category);
            
            DashboardDto.DashboardFilterDto filter = new DashboardDto.DashboardFilterDto(
                    startDate, endDate, null, null, null, null, null
            );
            
            List<DashboardDto.KpiMetricDto> response = dashboardService.getKpiMetrics(companyId, category, filter);
            return ResponseEntity.ok(ApiResponse.success("KPI 지표 조회 성공", response));
        } catch (Exception e) {
            log.error("KPI 지표 조회 실패: companyId={}, category={}, {}", companyId, category, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("KPI 지표 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/config/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "사용자 대시보드 설정 조회", description = "사용자의 대시보드 설정을 조회합니다")
    public ResponseEntity<ApiResponse<DashboardDto.UserDashboardConfigDto>> getUserDashboardConfig(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        try {
            log.info("사용자 대시보드 설정 조회 API 호출: userId={}", userId);
            
            DashboardDto.UserDashboardConfigDto response = dashboardService.getUserDashboardConfig(userId);
            return ResponseEntity.ok(ApiResponse.success("사용자 대시보드 설정 조회 성공", response));
        } catch (Exception e) {
            log.error("사용자 대시보드 설정 조회 실패: userId={}, {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("사용자 대시보드 설정 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/config/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "사용자 대시보드 설정 저장", description = "사용자의 대시보드 설정을 저장합니다")
    public ResponseEntity<ApiResponse<DashboardDto.UserDashboardConfigDto>> saveUserDashboardConfig(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Valid @RequestBody DashboardDto.UserDashboardConfigDto config) {
        try {
            log.info("사용자 대시보드 설정 저장 API 호출: userId={}", userId);
            
            DashboardDto.UserDashboardConfigDto response = dashboardService.saveUserDashboardConfig(userId, config);
            return ResponseEntity.ok(ApiResponse.success("사용자 대시보드 설정이 성공적으로 저장되었습니다", response));
        } catch (Exception e) {
            log.error("사용자 대시보드 설정 저장 실패: userId={}, {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("사용자 대시보드 설정 저장에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/data/{companyId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "대시보드 전체 데이터 조회", description = "대시보드의 모든 데이터를 한번에 조회합니다")
    public ResponseEntity<ApiResponse<DashboardDto.DashboardDataDto>> getDashboardData(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @Parameter(description = "시작 날짜") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "종료 날짜") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "시간 범위") @RequestParam(required = false) String timeRange,
            @Parameter(description = "사용자 역할") @RequestParam(required = false) String userRole) {
        try {
            log.info("대시보드 전체 데이터 조회 API 호출: companyId={}, userId={}", companyId, userId);
            
            DashboardDto.DashboardFilterDto filter = new DashboardDto.DashboardFilterDto(
                    startDate, endDate, timeRange, null, null, null, userRole
            );
            
            DashboardDto.DashboardDataDto response = dashboardService.getDashboardData(companyId, userId, filter);
            return ResponseEntity.ok(ApiResponse.success("대시보드 전체 데이터 조회 성공", response));
        } catch (Exception e) {
            log.error("대시보드 전체 데이터 조회 실패: companyId={}, userId={}, {}", companyId, userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("대시보드 전체 데이터 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/notifications/stats/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "알림 통계 조회", description = "사용자의 알림 통계를 조회합니다")
    public ResponseEntity<ApiResponse<DashboardDto.NotificationStatsDto>> getNotificationStats(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        try {
            log.info("알림 통계 조회 API 호출: userId={}", userId);
            
            DashboardDto.NotificationStatsDto response = dashboardService.getNotificationStats(userId);
            return ResponseEntity.ok(ApiResponse.success("알림 통계 조회 성공", response));
        } catch (Exception e) {
            log.error("알림 통계 조회 실패: userId={}, {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("알림 통계 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/notifications/unread-count")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "읽지 않은 알림 개수 조회", description = "현재 사용자의 읽지 않은 알림 개수를 조회합니다")
    public ResponseEntity<ApiResponse<Long>> getUnreadNotificationCount(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("읽지 않은 알림 개수 조회 API 호출: username={}", username);
            
            // 현재 사용자 ID 조회
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));
            Long userId = currentUser.getId();
            
            DashboardDto.NotificationStatsDto stats = dashboardService.getNotificationStats(userId);
            Long unreadCount = stats.unreadNotifications();
            
            log.info("읽지 않은 알림 개수: userId={}, count={}", userId, unreadCount);
            return ResponseEntity.ok(ApiResponse.success("읽지 않은 알림 개수 조회 완료", unreadCount));
        } catch (Exception e) {
            log.error("읽지 않은 알림 개수 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("읽지 않은 알림 개수 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/notifications/{notificationId}/read")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다")
    public ResponseEntity<ApiResponse<Void>> markNotificationAsRead(
            @Parameter(description = "알림 ID") @PathVariable Long notificationId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        try {
            log.info("알림 읽음 처리 API 호출: notificationId={}, userId={}", notificationId, userId);
            
            dashboardService.markNotificationAsRead(notificationId, userId);
            return ResponseEntity.ok(ApiResponse.success("알림이 읽음 처리되었습니다"));
        } catch (Exception e) {
            log.error("알림 읽음 처리 실패: notificationId={}, userId={}, {}", notificationId, userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("알림 읽음 처리에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/notifications/read-all/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림을 읽음 처리합니다")
    public ResponseEntity<ApiResponse<Void>> markAllNotificationsAsRead(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        try {
            log.info("모든 알림 읽음 처리 API 호출: userId={}", userId);
            
            dashboardService.markAllNotificationsAsRead(userId);
            return ResponseEntity.ok(ApiResponse.success("모든 알림이 읽음 처리되었습니다"));
        } catch (Exception e) {
            log.error("모든 알림 읽음 처리 실패: userId={}, {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("모든 알림 읽음 처리에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/todos/{todoId}/status")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "할일 상태 변경", description = "할일의 상태를 변경합니다")
    public ResponseEntity<ApiResponse<DashboardDto.TodoItemDto>> updateTodoStatus(
            @Parameter(description = "할일 ID") @PathVariable Long todoId,
            @Parameter(description = "새 상태") @RequestParam String status,
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        try {
            log.info("할일 상태 변경 API 호출: todoId={}, status={}, userId={}", todoId, status, userId);
            
            DashboardDto.TodoItemDto response = dashboardService.updateTodoStatus(todoId, status, userId);
            return ResponseEntity.ok(ApiResponse.success("할일 상태가 성공적으로 변경되었습니다", response));
        } catch (Exception e) {
            log.error("할일 상태 변경 실패: todoId={}, status={}, userId={}, {}", todoId, status, userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("할일 상태 변경에 실패했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/todos")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "새 할일 생성", description = "새로운 할일을 생성합니다")
    public ResponseEntity<ApiResponse<DashboardDto.TodoItemDto>> createTodoItem(
            @Valid @RequestBody DashboardDto.TodoItemDto todoItem,
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        try {
            log.info("새 할일 생성 API 호출: userId={}", userId);
            
            DashboardDto.TodoItemDto response = dashboardService.createTodoItem(todoItem, userId);
            return ResponseEntity.ok(ApiResponse.success("새 할일이 성공적으로 생성되었습니다", response));
        } catch (Exception e) {
            log.error("새 할일 생성 실패: userId={}, {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("새 할일 생성에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/system/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "시스템 상태 조회", description = "시스템 상태 정보를 조회합니다 (관리자 전용)")
    public ResponseEntity<ApiResponse<DashboardDto.SystemStatusDto>> getSystemStatus() {
        try {
            log.info("시스템 상태 조회 API 호출");
            
            DashboardDto.SystemStatusDto response = dashboardService.getSystemStatus();
            return ResponseEntity.ok(ApiResponse.success("시스템 상태 조회 성공", response));
        } catch (Exception e) {
            log.error("시스템 상태 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("시스템 상태 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/widget/{widgetId}/refresh")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "위젯 데이터 새로고침", description = "특정 위젯의 데이터를 새로고침합니다")
    public ResponseEntity<ApiResponse<Object>> refreshWidgetData(
            @Parameter(description = "위젯 ID") @PathVariable String widgetId,
            @Parameter(description = "회사 ID") @RequestParam Long companyId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId,
            @Parameter(description = "파라미터") @RequestParam(required = false) Object parameters) {
        try {
            log.info("위젯 데이터 새로고침 API 호출: widgetId={}, companyId={}, userId={}", widgetId, companyId, userId);
            
            Object response = dashboardService.refreshWidgetData(widgetId, companyId, userId, parameters);
            return ResponseEntity.ok(ApiResponse.success("위젯 데이터 새로고침 성공", response));
        } catch (Exception e) {
            log.error("위젯 데이터 새로고침 실패: widgetId={}, companyId={}, userId={}, {}", widgetId, companyId, userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("위젯 데이터 새로고침에 실패했습니다: " + e.getMessage()));
        }
    }
}
