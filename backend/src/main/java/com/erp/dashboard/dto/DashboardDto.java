package com.erp.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 대시보드 관련 DTO 클래스들
 * 대시보드 데이터 전송을 위한 DTO들을 정의합니다
 */
public class DashboardDto {

    /**
     * 전체 현황 요약 DTO
     */
    public record OverviewSummaryDto(
            // 매출 관련
            BigDecimal totalRevenue,
            BigDecimal monthlyRevenue,
            Double revenueGrowthRate,
            
            // 주문 관련
            Long totalOrders,
            Long monthlyOrders,
            Double orderGrowthRate,
            Long pendingOrders,
            
            // 고객 관련
            Long totalCustomers,
            Long activeCustomers,
            Long newCustomers,
            Double customerGrowthRate,
            
            // 재고 관련
            Long totalProducts,
            Long lowStockProducts,
            BigDecimal totalInventoryValue,
            Double inventoryTurnover,
            
            // 인력 관련
            Long totalEmployees,
            Long activeEmployees,
            Long newEmployees,
            Double attendanceRate,
            
            // 회계 관련
            BigDecimal totalAssets,
            BigDecimal totalLiabilities,
            BigDecimal netIncome,
            BigDecimal cashFlow
    ) {}

    /**
     * 차트 데이터 포인트 DTO
     */
    public record ChartDataPointDto(
            String label,
            BigDecimal value,
            String category,
            LocalDate date,
            String color
    ) {}

    /**
     * 매출 차트 데이터 DTO
     */
    public record RevenueChartDto(
            List<ChartDataPointDto> monthlyRevenue,
            List<ChartDataPointDto> dailyRevenue,
            List<ChartDataPointDto> revenueByCategory,
            List<ChartDataPointDto> revenueByCustomer
    ) {}

    /**
     * 주문 차트 데이터 DTO
     */
    public record OrderChartDto(
            List<ChartDataPointDto> ordersByStatus,
            List<ChartDataPointDto> ordersByMonth,
            List<ChartDataPointDto> ordersByType,
            List<ChartDataPointDto> ordersBySalesRep
    ) {}

    /**
     * 재고 차트 데이터 DTO
     */
    public record InventoryChartDto(
            List<ChartDataPointDto> stockLevels,
            List<ChartDataPointDto> inventoryByCategory,
            List<ChartDataPointDto> topSellingProducts,
            List<ChartDataPointDto> slowMovingProducts
    ) {}

    /**
     * 인사 차트 데이터 DTO
     */
    public record HrChartDto(
            List<ChartDataPointDto> attendanceByMonth,
            List<ChartDataPointDto> employeesByDepartment,
            List<ChartDataPointDto> salaryByDepartment,
            List<ChartDataPointDto> newHiresByMonth
    ) {}

    /**
     * 활동 로그 DTO
     */
    public record ActivityLogDto(
            Long id,
            String activityType,
            String activityDescription,
            String module,
            String userName,
            Long userId,
            LocalDateTime timestamp,
            String severity,
            String details,
            String ipAddress
    ) {}

    /**
     * 알림 DTO
     */
    public record NotificationDto(
            Long id,
            String title,
            String message,
            String type,
            String priority,
            String module,
            LocalDateTime createdAt,
            Boolean isRead,
            String actionUrl,
            String actionLabel
    ) {}

    /**
     * 할일 DTO
     */
    public record TodoItemDto(
            Long id,
            String title,
            String description,
            String priority,
            String status,
            LocalDate dueDate,
            String assignedTo,
            Long assignedToId,
            String module,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    /**
     * 빠른 액션 DTO
     */
    public record QuickActionDto(
            String id,
            String title,
            String description,
            String icon,
            String url,
            String permission,
            String category,
            Integer sortOrder,
            Boolean isEnabled
    ) {}

    /**
     * 대시보드 위젯 설정 DTO
     */
    public record WidgetConfigDto(
            String id,
            String title,
            String type,
            Integer width,
            Integer height,
            Integer positionX,
            Integer positionY,
            Boolean isVisible,
            String dataSource,
            String chartType,
            String timeRange,
            Object settings
    ) {}

    /**
     * 사용자 대시보드 설정 DTO
     */
    public record UserDashboardConfigDto(
            Long userId,
            String userRole,
            List<WidgetConfigDto> widgets,
            String theme,
            String layout,
            LocalDateTime lastUpdated
    ) {}

    /**
     * KPI 지표 DTO
     */
    public record KpiMetricDto(
            String name,
            String displayName,
            BigDecimal currentValue,
            BigDecimal previousValue,
            Double changePercentage,
            String trend,
            String unit,
            String format,
            String category,
            String description,
            LocalDateTime lastUpdated
    ) {}

    /**
     * 대시보드 전체 데이터 DTO
     */
    public record DashboardDataDto(
            OverviewSummaryDto overview,
            RevenueChartDto revenueChart,
            OrderChartDto orderChart,
            InventoryChartDto inventoryChart,
            HrChartDto hrChart,
            List<ActivityLogDto> recentActivities,
            List<NotificationDto> notifications,
            List<TodoItemDto> todoItems,
            List<QuickActionDto> quickActions,
            List<KpiMetricDto> kpiMetrics,
            UserDashboardConfigDto userConfig
    ) {}

    /**
     * 대시보드 필터 DTO
     */
    public record DashboardFilterDto(
            LocalDate startDate,
            LocalDate endDate,
            String timeRange,
            List<String> modules,
            List<String> departments,
            List<String> categories,
            String userRole
    ) {}

    /**
     * 알림 통계 DTO
     */
    public record NotificationStatsDto(
            Long totalNotifications,
            Long unreadNotifications,
            Long criticalNotifications,
            Long warningNotifications,
            Long infoNotifications
    ) {}

    /**
     * 성과 지표 DTO
     */
    public record PerformanceMetricDto(
            String metricName,
            BigDecimal value,
            String unit,
            Double targetValue,
            Double achievementRate,
            String status,
            String trend,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {}

    /**
     * 부서별 성과 DTO
     */
    public record DepartmentPerformanceDto(
            Long departmentId,
            String departmentName,
            List<PerformanceMetricDto> metrics,
            Double overallScore,
            String ranking,
            Integer employeeCount
    ) {}

    /**
     * 시스템 상태 DTO
     */
    public record SystemStatusDto(
            String status,
            Double cpuUsage,
            Double memoryUsage,
            Double diskUsage,
            Integer activeUsers,
            Integer totalSessions,
            LocalDateTime lastUpdate,
            List<String> systemAlerts
    ) {}
}




