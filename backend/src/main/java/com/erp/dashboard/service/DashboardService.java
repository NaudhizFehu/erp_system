package com.erp.dashboard.service;

import com.erp.dashboard.dto.DashboardDto;
import java.time.LocalDate;
import java.util.List;

/**
 * 대시보드 서비스 인터페이스
 * 대시보드 관련 비즈니스 로직을 정의합니다
 */
public interface DashboardService {

    /**
     * 전체 현황 요약 조회
     */
    DashboardDto.OverviewSummaryDto getOverviewSummary(Long companyId, DashboardDto.DashboardFilterDto filter);

    /**
     * 매출 차트 데이터 조회
     */
    DashboardDto.RevenueChartDto getRevenueChartData(Long companyId, DashboardDto.DashboardFilterDto filter);

    /**
     * 주문 차트 데이터 조회
     */
    DashboardDto.OrderChartDto getOrderChartData(Long companyId, DashboardDto.DashboardFilterDto filter);

    /**
     * 재고 차트 데이터 조회
     */
    DashboardDto.InventoryChartDto getInventoryChartData(Long companyId, DashboardDto.DashboardFilterDto filter);

    /**
     * 인사 차트 데이터 조회
     */
    DashboardDto.HrChartDto getHrChartData(Long companyId, DashboardDto.DashboardFilterDto filter);

    /**
     * 최근 활동 로그 조회
     */
    List<DashboardDto.ActivityLogDto> getRecentActivities(Long companyId, Long userId, int limit);

    /**
     * 사용자 알림 조회
     */
    List<DashboardDto.NotificationDto> getUserNotifications(Long userId, Boolean unreadOnly, int limit);

    /**
     * 사용자 할일 목록 조회
     */
    List<DashboardDto.TodoItemDto> getUserTodoItems(Long userId, String status, int limit);

    /**
     * 사용자별 빠른 액션 조회
     */
    List<DashboardDto.QuickActionDto> getQuickActions(String userRole);

    /**
     * KPI 지표 조회
     */
    List<DashboardDto.KpiMetricDto> getKpiMetrics(Long companyId, String category, DashboardDto.DashboardFilterDto filter);

    /**
     * 사용자 대시보드 설정 조회
     */
    DashboardDto.UserDashboardConfigDto getUserDashboardConfig(Long userId);

    /**
     * 사용자 대시보드 설정 저장
     */
    DashboardDto.UserDashboardConfigDto saveUserDashboardConfig(Long userId, DashboardDto.UserDashboardConfigDto config);

    /**
     * 대시보드 전체 데이터 조회
     */
    DashboardDto.DashboardDataDto getDashboardData(Long companyId, Long userId, DashboardDto.DashboardFilterDto filter);

    /**
     * 알림 통계 조회
     */
    DashboardDto.NotificationStatsDto getNotificationStats(Long userId);

    /**
     * 부서별 성과 조회
     */
    List<DashboardDto.DepartmentPerformanceDto> getDepartmentPerformance(Long companyId, DashboardDto.DashboardFilterDto filter);

    /**
     * 시스템 상태 조회 (관리자용)
     */
    DashboardDto.SystemStatusDto getSystemStatus();

    /**
     * 알림 읽음 처리
     */
    void markNotificationAsRead(Long notificationId, Long userId);

    /**
     * 모든 알림 읽음 처리
     */
    void markAllNotificationsAsRead(Long userId);

    /**
     * 할일 상태 변경
     */
    DashboardDto.TodoItemDto updateTodoStatus(Long todoId, String status, Long userId);

    /**
     * 새 할일 생성
     */
    DashboardDto.TodoItemDto createTodoItem(DashboardDto.TodoItemDto todoItem, Long userId);

    /**
     * 활동 로그 기록
     */
    void logActivity(String activityType, String description, String module, Long userId, String details);

    /**
     * 알림 생성
     */
    void createNotification(String title, String message, String type, String priority, String module, Long userId);

    /**
     * 대시보드 위젯 데이터 새로고침
     */
    Object refreshWidgetData(String widgetId, Long companyId, Long userId, Object parameters);

    /**
     * 실시간 데이터 조회 (WebSocket용)
     */
    Object getRealTimeData(String dataType, Long companyId, Long userId);

    /**
     * 대시보드 내보내기 (PDF/Excel)
     */
    byte[] exportDashboard(Long companyId, Long userId, String format, DashboardDto.DashboardFilterDto filter);
}




