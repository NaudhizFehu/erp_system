package com.erp.dashboard.service.impl;

import com.erp.dashboard.dto.DashboardDto;
import com.erp.dashboard.service.DashboardService;
import com.erp.sales.repository.CustomerRepository;
import com.erp.sales.repository.OrderRepository;
import com.erp.hr.repository.EmployeeRepository;
import com.erp.hr.repository.AttendanceRepository;
import com.erp.inventory.repository.ProductRepository;
import com.erp.inventory.repository.InventoryRepository;
import com.erp.accounting.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 대시보드 서비스 구현
 * 대시보드 관련 비즈니스 로직을 처리합니다
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public DashboardDto.OverviewSummaryDto getOverviewSummary(Long companyId, DashboardDto.DashboardFilterDto filter) {
        log.info("대시보드 전체 현황 요약 조회: companyId={}", companyId);

        LocalDate startDate = filter.startDate() != null ? filter.startDate() : LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = filter.endDate() != null ? filter.endDate() : LocalDate.now();
        
        // 이전 기간 계산 (비교용)
        LocalDate prevStartDate = startDate.minusMonths(1);
        LocalDate prevEndDate = endDate.minusMonths(1);

        // 매출 관련 데이터
        BigDecimal totalRevenue = getTotalRevenue(companyId, null, null);
        BigDecimal monthlyRevenue = getTotalRevenue(companyId, startDate, endDate);
        BigDecimal prevMonthlyRevenue = getTotalRevenue(companyId, prevStartDate, prevEndDate);
        Double revenueGrowthRate = calculateGrowthRate(monthlyRevenue, prevMonthlyRevenue);

        // 주문 관련 데이터
        Long totalOrders = orderRepository.countByCompanyIdAndIsDeletedFalse(companyId);
        Long monthlyOrders = orderRepository.countOrdersByDateRange(companyId, startDate, endDate);
        Long prevMonthlyOrders = orderRepository.countOrdersByDateRange(companyId, prevStartDate, prevEndDate);
        Double orderGrowthRate = calculateGrowthRate(BigDecimal.valueOf(monthlyOrders), BigDecimal.valueOf(prevMonthlyOrders));
        Long pendingOrders = orderRepository.countPendingOrders(companyId);

        // 고객 관련 데이터
        Long totalCustomers = customerRepository.countByCompanyIdAndIsDeletedFalse(companyId);
        Long activeCustomers = customerRepository.countActiveCustomers(companyId);
        Long newCustomers = customerRepository.countNewCustomers(companyId, startDate, endDate);
        Long prevNewCustomers = customerRepository.countNewCustomers(companyId, prevStartDate, prevEndDate);
        Double customerGrowthRate = calculateGrowthRate(BigDecimal.valueOf(newCustomers), BigDecimal.valueOf(prevNewCustomers));

        // 재고 관련 데이터
        Long totalProducts = productRepository.countByCompanyIdAndIsDeletedFalse(companyId);
        Long lowStockProducts = inventoryRepository.countLowStockProducts(companyId);
        BigDecimal totalInventoryValue = inventoryRepository.getTotalInventoryValue(companyId);
        Double inventoryTurnover = calculateInventoryTurnover(companyId, startDate, endDate);

        // 인력 관련 데이터
        Long totalEmployees = employeeRepository.countByCompanyIdAndIsDeletedFalse(companyId);
        Long activeEmployees = employeeRepository.countActiveEmployees(companyId);
        Long newEmployees = employeeRepository.countNewEmployees(companyId, startDate, endDate);
        Double attendanceRate = calculateAttendanceRate(companyId, startDate, endDate);

        // 회계 관련 데이터 (기본값)
        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal totalLiabilities = BigDecimal.ZERO;
        BigDecimal netIncome = monthlyRevenue;
        BigDecimal cashFlow = BigDecimal.ZERO;

        return new DashboardDto.OverviewSummaryDto(
                totalRevenue, monthlyRevenue, revenueGrowthRate,
                totalOrders, monthlyOrders, orderGrowthRate, pendingOrders,
                totalCustomers, activeCustomers, newCustomers, customerGrowthRate,
                totalProducts, lowStockProducts, totalInventoryValue, inventoryTurnover,
                totalEmployees, activeEmployees, newEmployees, attendanceRate,
                totalAssets, totalLiabilities, netIncome, cashFlow
        );
    }

    @Override
    public DashboardDto.RevenueChartDto getRevenueChartData(Long companyId, DashboardDto.DashboardFilterDto filter) {
        log.info("매출 차트 데이터 조회: companyId={}", companyId);

        LocalDate startDate = filter.startDate() != null ? filter.startDate() : LocalDate.now().minusMonths(12);
        LocalDate endDate = filter.endDate() != null ? filter.endDate() : LocalDate.now();

        // 월별 매출
        List<DashboardDto.ChartDataPointDto> monthlyRevenue = getMonthlyRevenueData(companyId, startDate, endDate);
        
        // 일별 매출 (최근 30일)
        List<DashboardDto.ChartDataPointDto> dailyRevenue = getDailyRevenueData(companyId, LocalDate.now().minusDays(30), LocalDate.now());
        
        // 카테고리별 매출 (기본값)
        List<DashboardDto.ChartDataPointDto> revenueByCategory = List.of(
                new DashboardDto.ChartDataPointDto("제품", new BigDecimal("5000000"), "product", LocalDate.now(), "#8884d8"),
                new DashboardDto.ChartDataPointDto("서비스", new BigDecimal("3000000"), "service", LocalDate.now(), "#82ca9d"),
                new DashboardDto.ChartDataPointDto("기타", new BigDecimal("1000000"), "other", LocalDate.now(), "#ffc658")
        );
        
        // 고객별 매출 (상위 10개)
        List<DashboardDto.ChartDataPointDto> revenueByCustomer = getTopCustomerRevenueData(companyId, startDate, endDate, 10);

        return new DashboardDto.RevenueChartDto(monthlyRevenue, dailyRevenue, revenueByCategory, revenueByCustomer);
    }

    @Override
    public DashboardDto.OrderChartDto getOrderChartData(Long companyId, DashboardDto.DashboardFilterDto filter) {
        log.info("주문 차트 데이터 조회: companyId={}", companyId);

        // 주문 상태별 분포
        List<Object[]> orderStatusData = orderRepository.getOrderStatsByStatus(companyId);
        List<DashboardDto.ChartDataPointDto> ordersByStatus = orderStatusData.stream()
                .map(data -> new DashboardDto.ChartDataPointDto(
                        (String) data[0], // status
                        BigDecimal.valueOf(((Number) data[1]).longValue()), // count
                        "status",
                        LocalDate.now(),
                        getStatusColor((String) data[0])
                ))
                .collect(Collectors.toList());

        // 월별 주문 수
        LocalDate startDate = filter.startDate() != null ? filter.startDate() : LocalDate.now().minusMonths(12);
        LocalDate endDate = filter.endDate() != null ? filter.endDate() : LocalDate.now();
        List<DashboardDto.ChartDataPointDto> ordersByMonth = getMonthlyOrderData(companyId, startDate, endDate);

        // 주문 유형별 분포
        List<Object[]> orderTypeData = orderRepository.getOrderStatsByType(companyId);
        List<DashboardDto.ChartDataPointDto> ordersByType = orderTypeData.stream()
                .map(data -> new DashboardDto.ChartDataPointDto(
                        (String) data[0], // type
                        BigDecimal.valueOf(((Number) data[1]).longValue()), // count
                        "type",
                        LocalDate.now(),
                        getTypeColor((String) data[0])
                ))
                .collect(Collectors.toList());

        // 영업담당자별 주문 수
        List<Object[]> salesRepData = orderRepository.getOrderStatsBySalesRep(companyId);
        List<DashboardDto.ChartDataPointDto> ordersBySalesRep = salesRepData.stream()
                .limit(10) // 상위 10명
                .map(data -> new DashboardDto.ChartDataPointDto(
                        (String) data[0], // salesRepName
                        BigDecimal.valueOf(((Number) data[1]).longValue()), // count
                        "salesRep",
                        LocalDate.now(),
                        "#8884d8"
                ))
                .collect(Collectors.toList());

        return new DashboardDto.OrderChartDto(ordersByStatus, ordersByMonth, ordersByType, ordersBySalesRep);
    }

    @Override
    public DashboardDto.InventoryChartDto getInventoryChartData(Long companyId, DashboardDto.DashboardFilterDto filter) {
        log.info("재고 차트 데이터 조회: companyId={}", companyId);

        // 재고 수준별 제품 분포
        List<DashboardDto.ChartDataPointDto> stockLevels = List.of(
                new DashboardDto.ChartDataPointDto("정상 재고", new BigDecimal("150"), "normal", LocalDate.now(), "#82ca9d"),
                new DashboardDto.ChartDataPointDto("부족 재고", new BigDecimal("25"), "low", LocalDate.now(), "#ffc658"),
                new DashboardDto.ChartDataPointDto("과다 재고", new BigDecimal("30"), "excess", LocalDate.now(), "#ff7300"),
                new DashboardDto.ChartDataPointDto("품절", new BigDecimal("5"), "out", LocalDate.now(), "#ff0000")
        );

        // 카테고리별 재고 가치 (기본값)
        List<DashboardDto.ChartDataPointDto> inventoryByCategory = List.of(
                new DashboardDto.ChartDataPointDto("전자제품", new BigDecimal("50000000"), "electronics", LocalDate.now(), "#8884d8"),
                new DashboardDto.ChartDataPointDto("의류", new BigDecimal("30000000"), "clothing", LocalDate.now(), "#82ca9d"),
                new DashboardDto.ChartDataPointDto("식품", new BigDecimal("20000000"), "food", LocalDate.now(), "#ffc658"),
                new DashboardDto.ChartDataPointDto("기타", new BigDecimal("10000000"), "other", LocalDate.now(), "#ff7300")
        );

        // 인기 상품 (기본값)
        List<DashboardDto.ChartDataPointDto> topSellingProducts = List.of(
                new DashboardDto.ChartDataPointDto("상품A", new BigDecimal("1000"), "product", LocalDate.now(), "#8884d8"),
                new DashboardDto.ChartDataPointDto("상품B", new BigDecimal("800"), "product", LocalDate.now(), "#82ca9d"),
                new DashboardDto.ChartDataPointDto("상품C", new BigDecimal("600"), "product", LocalDate.now(), "#ffc658"),
                new DashboardDto.ChartDataPointDto("상품D", new BigDecimal("400"), "product", LocalDate.now(), "#ff7300"),
                new DashboardDto.ChartDataPointDto("상품E", new BigDecimal("200"), "product", LocalDate.now(), "#ff0000")
        );

        // 저회전 상품 (기본값)
        List<DashboardDto.ChartDataPointDto> slowMovingProducts = List.of(
                new DashboardDto.ChartDataPointDto("상품X", new BigDecimal("10"), "product", LocalDate.now(), "#ff0000"),
                new DashboardDto.ChartDataPointDto("상품Y", new BigDecimal("15"), "product", LocalDate.now(), "#ff7300"),
                new DashboardDto.ChartDataPointDto("상품Z", new BigDecimal("20"), "product", LocalDate.now(), "#ffc658")
        );

        return new DashboardDto.InventoryChartDto(stockLevels, inventoryByCategory, topSellingProducts, slowMovingProducts);
    }

    @Override
    public DashboardDto.HrChartDto getHrChartData(Long companyId, DashboardDto.DashboardFilterDto filter) {
        log.info("인사 차트 데이터 조회: companyId={}", companyId);

        LocalDate startDate = filter.startDate() != null ? filter.startDate() : LocalDate.now().minusMonths(12);
        LocalDate endDate = filter.endDate() != null ? filter.endDate() : LocalDate.now();

        // 월별 출석률
        List<DashboardDto.ChartDataPointDto> attendanceByMonth = getMonthlyAttendanceData(companyId, startDate, endDate);

        // 부서별 직원 수
        List<Object[]> employeeByDeptData = employeeRepository.getEmployeeCountByDepartment(companyId);
        List<DashboardDto.ChartDataPointDto> employeesByDepartment = employeeByDeptData.stream()
                .map(data -> new DashboardDto.ChartDataPointDto(
                        (String) data[0], // departmentName
                        BigDecimal.valueOf(((Number) data[1]).longValue()), // count
                        "department",
                        LocalDate.now(),
                        "#8884d8"
                ))
                .collect(Collectors.toList());

        // 부서별 평균 급여 (기본값)
        List<DashboardDto.ChartDataPointDto> salaryByDepartment = List.of(
                new DashboardDto.ChartDataPointDto("개발팀", new BigDecimal("4500000"), "salary", LocalDate.now(), "#8884d8"),
                new DashboardDto.ChartDataPointDto("영업팀", new BigDecimal("4000000"), "salary", LocalDate.now(), "#82ca9d"),
                new DashboardDto.ChartDataPointDto("마케팅팀", new BigDecimal("3800000"), "salary", LocalDate.now(), "#ffc658"),
                new DashboardDto.ChartDataPointDto("인사팀", new BigDecimal("3500000"), "salary", LocalDate.now(), "#ff7300")
        );

        // 월별 신규 입사자 수
        List<DashboardDto.ChartDataPointDto> newHiresByMonth = getMonthlyNewHiresData(companyId, startDate, endDate);

        return new DashboardDto.HrChartDto(attendanceByMonth, employeesByDepartment, salaryByDepartment, newHiresByMonth);
    }

    @Override
    public List<DashboardDto.ActivityLogDto> getRecentActivities(Long companyId, Long userId, int limit) {
        log.info("최근 활동 로그 조회: companyId={}, userId={}, limit={}", companyId, userId, limit);

        // 실제 구현에서는 ActivityLog 엔티티에서 조회
        // 현재는 샘플 데이터 반환
        List<DashboardDto.ActivityLogDto> activities = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        activities.add(new DashboardDto.ActivityLogDto(1L, "ORDER_CREATED", "새 주문이 생성되었습니다", "sales", "김철수", 1L, now.minusMinutes(5), "INFO", "주문번호: ORD-20231201-001", "192.168.1.100"));
        activities.add(new DashboardDto.ActivityLogDto(2L, "CUSTOMER_UPDATED", "고객 정보가 수정되었습니다", "sales", "이영희", 2L, now.minusMinutes(15), "INFO", "고객: ABC 회사", "192.168.1.101"));
        activities.add(new DashboardDto.ActivityLogDto(3L, "INVENTORY_LOW", "재고 부족 알림", "inventory", "시스템", null, now.minusMinutes(30), "WARNING", "상품: 노트북 A형", "system"));
        activities.add(new DashboardDto.ActivityLogDto(4L, "EMPLOYEE_CREATED", "새 직원이 등록되었습니다", "hr", "박관리", 3L, now.minusHours(1), "INFO", "직원: 홍길동", "192.168.1.102"));
        activities.add(new DashboardDto.ActivityLogDto(5L, "PAYMENT_RECEIVED", "결제가 완료되었습니다", "accounting", "최회계", 4L, now.minusHours(2), "INFO", "금액: 1,500,000원", "192.168.1.103"));

        return activities.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<DashboardDto.NotificationDto> getUserNotifications(Long userId, Boolean unreadOnly, int limit) {
        log.info("사용자 알림 조회: userId={}, unreadOnly={}, limit={}", userId, unreadOnly, limit);

        // 실제 구현에서는 Notification 엔티티에서 조회
        // 현재는 샘플 데이터 반환
        List<DashboardDto.NotificationDto> notifications = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        notifications.add(new DashboardDto.NotificationDto(1L, "재고 부족 알림", "노트북 A형의 재고가 10개 이하입니다", "warning", "high", "inventory", now.minusMinutes(10), false, "/inventory/products/1", "재고 확인"));
        notifications.add(new DashboardDto.NotificationDto(2L, "신규 주문", "ABC 회사에서 새 주문을 요청했습니다", "info", "medium", "sales", now.minusMinutes(30), false, "/sales/orders/123", "주문 확인"));
        notifications.add(new DashboardDto.NotificationDto(3L, "급여 처리 완료", "2023년 12월 급여 처리가 완료되었습니다", "success", "low", "hr", now.minusHours(2), true, "/hr/salary", "급여 내역"));
        notifications.add(new DashboardDto.NotificationDto(4L, "시스템 점검 예정", "오늘 밤 12시부터 시스템 점검이 예정되어 있습니다", "warning", "high", "system", now.minusHours(4), false, null, null));
        notifications.add(new DashboardDto.NotificationDto(5L, "월말 결산", "월말 결산 작업을 진행해주세요", "info", "medium", "accounting", now.minusHours(6), true, "/accounting/reports", "결산 보기"));

        return notifications.stream()
                .filter(n -> !unreadOnly || !n.isRead())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardDto.TodoItemDto> getUserTodoItems(Long userId, String status, int limit) {
        log.info("사용자 할일 목록 조회: userId={}, status={}, limit={}", userId, status, limit);

        // 실제 구현에서는 TodoItem 엔티티에서 조회
        // 현재는 샘플 데이터 반환
        List<DashboardDto.TodoItemDto> todoItems = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        todoItems.add(new DashboardDto.TodoItemDto(1L, "고객 미팅 준비", "ABC 회사와의 계약 미팅 자료 준비", "high", "pending", LocalDate.now().plusDays(1), "김영업", 1L, "sales", now.minusDays(1), now.minusDays(1)));
        todoItems.add(new DashboardDto.TodoItemDto(2L, "재고 실사", "창고 A동 재고 실사 진행", "medium", "in_progress", LocalDate.now().plusDays(3), "이창고", 2L, "inventory", now.minusDays(2), now.minusHours(2)));
        todoItems.add(new DashboardDto.TodoItemDto(3L, "직원 평가", "2023년 하반기 직원 평가 완료", "medium", "pending", LocalDate.now().plusDays(7), "박인사", 3L, "hr", now.minusDays(3), now.minusDays(3)));
        todoItems.add(new DashboardDto.TodoItemDto(4L, "월말 보고서", "11월 매출 보고서 작성", "low", "completed", LocalDate.now().minusDays(1), "최회계", 4L, "accounting", now.minusDays(5), now.minusDays(1)));
        todoItems.add(new DashboardDto.TodoItemDto(5L, "시스템 백업", "데이터베이스 백업 및 점검", "high", "pending", LocalDate.now(), "관리자", 5L, "system", now.minusDays(1), now.minusDays(1)));

        return todoItems.stream()
                .filter(t -> status == null || status.equals(t.status()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardDto.QuickActionDto> getQuickActions(String userRole) {
        log.info("빠른 액션 조회: userRole={}", userRole);

        List<DashboardDto.QuickActionDto> actions = new ArrayList<>();

        // 공통 액션
        actions.add(new DashboardDto.QuickActionDto("new-order", "새 주문", "새로운 주문을 생성합니다", "ShoppingCart", "/sales/orders/new", "USER", "sales", 1, true));
        actions.add(new DashboardDto.QuickActionDto("new-customer", "고객 등록", "새로운 고객을 등록합니다", "UserPlus", "/sales/customers/new", "USER", "sales", 2, true));
        actions.add(new DashboardDto.QuickActionDto("inventory-check", "재고 확인", "재고 현황을 확인합니다", "Package", "/inventory/dashboard", "USER", "inventory", 3, true));

        // 매니저 이상 액션
        if ("MANAGER".equals(userRole) || "ADMIN".equals(userRole)) {
            actions.add(new DashboardDto.QuickActionDto("sales-report", "매출 보고서", "매출 보고서를 확인합니다", "TrendingUp", "/reports/sales", "MANAGER", "reports", 4, true));
            actions.add(new DashboardDto.QuickActionDto("employee-manage", "직원 관리", "직원 정보를 관리합니다", "Users", "/hr/employees", "MANAGER", "hr", 5, true));
        }

        // 관리자 전용 액션
        if ("ADMIN".equals(userRole)) {
            actions.add(new DashboardDto.QuickActionDto("system-settings", "시스템 설정", "시스템 설정을 관리합니다", "Settings", "/admin/settings", "ADMIN", "system", 6, true));
            actions.add(new DashboardDto.QuickActionDto("user-manage", "사용자 관리", "시스템 사용자를 관리합니다", "Shield", "/admin/users", "ADMIN", "system", 7, true));
        }

        return actions.stream()
                .filter(a -> hasPermission(userRole, a.permission()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardDto.KpiMetricDto> getKpiMetrics(Long companyId, String category, DashboardDto.DashboardFilterDto filter) {
        log.info("KPI 지표 조회: companyId={}, category={}", companyId, category);

        List<DashboardDto.KpiMetricDto> metrics = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 매출 관련 KPI
        if (category == null || "sales".equals(category)) {
            metrics.add(new DashboardDto.KpiMetricDto("monthly_revenue", "월매출", new BigDecimal("15000000"), new BigDecimal("12000000"), 25.0, "up", "원", "currency", "sales", "이번 달 총 매출액", now));
            metrics.add(new DashboardDto.KpiMetricDto("conversion_rate", "전환율", new BigDecimal("3.2"), new BigDecimal("2.8"), 14.3, "up", "%", "percentage", "sales", "견적서 대비 주문 전환율", now));
        }

        // 고객 관련 KPI
        if (category == null || "customer".equals(category)) {
            metrics.add(new DashboardDto.KpiMetricDto("customer_satisfaction", "고객만족도", new BigDecimal("4.2"), new BigDecimal("4.0"), 5.0, "up", "점", "decimal", "customer", "고객 만족도 평균 점수", now));
            metrics.add(new DashboardDto.KpiMetricDto("customer_retention", "고객유지율", new BigDecimal("85.5"), new BigDecimal("82.0"), 4.3, "up", "%", "percentage", "customer", "기존 고객 유지율", now));
        }

        // 운영 관련 KPI
        if (category == null || "operations".equals(category)) {
            metrics.add(new DashboardDto.KpiMetricDto("order_fulfillment", "주문처리율", new BigDecimal("96.8"), new BigDecimal("94.5"), 2.4, "up", "%", "percentage", "operations", "주문 처리 완료율", now));
            metrics.add(new DashboardDto.KpiMetricDto("inventory_turnover", "재고회전율", new BigDecimal("6.2"), new BigDecimal("5.8"), 6.9, "up", "회", "decimal", "operations", "연간 재고 회전율", now));
        }

        return metrics;
    }

    @Override
    public DashboardDto.UserDashboardConfigDto getUserDashboardConfig(Long userId) {
        log.info("사용자 대시보드 설정 조회: userId={}", userId);

        // 실제 구현에서는 UserDashboardConfig 엔티티에서 조회
        // 현재는 기본 설정 반환
        List<DashboardDto.WidgetConfigDto> defaultWidgets = List.of(
                new DashboardDto.WidgetConfigDto("overview", "전체 현황", "summary", 12, 4, 0, 0, true, "overview", "card", "monthly", null),
                new DashboardDto.WidgetConfigDto("revenue-chart", "매출 차트", "chart", 6, 6, 0, 4, true, "revenue", "line", "monthly", null),
                new DashboardDto.WidgetConfigDto("order-status", "주문 현황", "chart", 6, 6, 6, 4, true, "orders", "pie", "monthly", null),
                new DashboardDto.WidgetConfigDto("recent-activities", "최근 활동", "list", 8, 8, 0, 10, true, "activities", "list", "daily", null),
                new DashboardDto.WidgetConfigDto("notifications", "알림", "list", 4, 8, 8, 10, true, "notifications", "list", "realtime", null)
        );

        return new DashboardDto.UserDashboardConfigDto(userId, "USER", defaultWidgets, "light", "grid", LocalDateTime.now());
    }

    @Override
    @Transactional
    public DashboardDto.UserDashboardConfigDto saveUserDashboardConfig(Long userId, DashboardDto.UserDashboardConfigDto config) {
        log.info("사용자 대시보드 설정 저장: userId={}", userId);
        
        // 실제 구현에서는 UserDashboardConfig 엔티티에 저장
        // 현재는 입력받은 설정을 그대로 반환
        return new DashboardDto.UserDashboardConfigDto(
                userId,
                config.userRole(),
                config.widgets(),
                config.theme(),
                config.layout(),
                LocalDateTime.now()
        );
    }

    @Override
    public DashboardDto.DashboardDataDto getDashboardData(Long companyId, Long userId, DashboardDto.DashboardFilterDto filter) {
        log.info("대시보드 전체 데이터 조회: companyId={}, userId={}", companyId, userId);

        DashboardDto.OverviewSummaryDto overview = getOverviewSummary(companyId, filter);
        DashboardDto.RevenueChartDto revenueChart = getRevenueChartData(companyId, filter);
        DashboardDto.OrderChartDto orderChart = getOrderChartData(companyId, filter);
        DashboardDto.InventoryChartDto inventoryChart = getInventoryChartData(companyId, filter);
        DashboardDto.HrChartDto hrChart = getHrChartData(companyId, filter);
        List<DashboardDto.ActivityLogDto> recentActivities = getRecentActivities(companyId, userId, 10);
        List<DashboardDto.NotificationDto> notifications = getUserNotifications(userId, false, 10);
        List<DashboardDto.TodoItemDto> todoItems = getUserTodoItems(userId, null, 10);
        List<DashboardDto.QuickActionDto> quickActions = getQuickActions(filter.userRole());
        List<DashboardDto.KpiMetricDto> kpiMetrics = getKpiMetrics(companyId, null, filter);
        DashboardDto.UserDashboardConfigDto userConfig = getUserDashboardConfig(userId);

        return new DashboardDto.DashboardDataDto(
                overview, revenueChart, orderChart, inventoryChart, hrChart,
                recentActivities, notifications, todoItems, quickActions, kpiMetrics, userConfig
        );
    }

    // 나머지 메서드들은 기본 구현으로 처리
    @Override
    public DashboardDto.NotificationStatsDto getNotificationStats(Long userId) {
        return new DashboardDto.NotificationStatsDto(50L, 12L, 3L, 5L, 4L);
    }

    @Override
    public List<DashboardDto.DepartmentPerformanceDto> getDepartmentPerformance(Long companyId, DashboardDto.DashboardFilterDto filter) {
        return List.of();
    }

    @Override
    public DashboardDto.SystemStatusDto getSystemStatus() {
        return new DashboardDto.SystemStatusDto("healthy", 45.2, 68.5, 32.1, 25, 47, LocalDateTime.now(), List.of());
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Long notificationId, Long userId) {
        log.info("알림 읽음 처리: notificationId={}, userId={}", notificationId, userId);
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead(Long userId) {
        log.info("모든 알림 읽음 처리: userId={}", userId);
    }

    @Override
    @Transactional
    public DashboardDto.TodoItemDto updateTodoStatus(Long todoId, String status, Long userId) {
        log.info("할일 상태 변경: todoId={}, status={}, userId={}", todoId, status, userId);
        return getUserTodoItems(userId, null, 1).get(0);
    }

    @Override
    @Transactional
    public DashboardDto.TodoItemDto createTodoItem(DashboardDto.TodoItemDto todoItem, Long userId) {
        log.info("새 할일 생성: userId={}", userId);
        return todoItem;
    }

    @Override
    @Transactional
    public void logActivity(String activityType, String description, String module, Long userId, String details) {
        log.info("활동 로그 기록: type={}, module={}, userId={}", activityType, module, userId);
    }

    @Override
    @Transactional
    public void createNotification(String title, String message, String type, String priority, String module, Long userId) {
        log.info("알림 생성: title={}, type={}, userId={}", title, type, userId);
    }

    @Override
    public Object refreshWidgetData(String widgetId, Long companyId, Long userId, Object parameters) {
        log.info("위젯 데이터 새로고침: widgetId={}, companyId={}, userId={}", widgetId, companyId, userId);
        return null;
    }

    @Override
    public Object getRealTimeData(String dataType, Long companyId, Long userId) {
        log.info("실시간 데이터 조회: dataType={}, companyId={}, userId={}", dataType, companyId, userId);
        return null;
    }

    @Override
    public byte[] exportDashboard(Long companyId, Long userId, String format, DashboardDto.DashboardFilterDto filter) {
        log.info("대시보드 내보내기: companyId={}, userId={}, format={}", companyId, userId, format);
        return new byte[0];
    }

    // Private helper methods

    private BigDecimal getTotalRevenue(Long companyId, LocalDate startDate, LocalDate endDate) {
        // 실제 구현에서는 Order 엔티티에서 매출 합계 조회
        if (startDate == null && endDate == null) {
            return new BigDecimal("150000000"); // 전체 매출
        } else {
            return new BigDecimal("15000000"); // 월 매출
        }
    }

    private Double calculateGrowthRate(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.equals(BigDecimal.ZERO)) {
            return 0.0;
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private Double calculateInventoryTurnover(Long companyId, LocalDate startDate, LocalDate endDate) {
        // 재고 회전율 = 매출원가 / 평균재고
        return 6.2;
    }

    private Double calculateAttendanceRate(Long companyId, LocalDate startDate, LocalDate endDate) {
        // 출석률 = 실제출근일 / 총근무일 * 100
        return 94.5;
    }

    private List<DashboardDto.ChartDataPointDto> getMonthlyRevenueData(Long companyId, LocalDate startDate, LocalDate endDate) {
        List<DashboardDto.ChartDataPointDto> data = new ArrayList<>();
        LocalDate current = startDate.withDayOfMonth(1);
        
        while (!current.isAfter(endDate)) {
            String monthLabel = current.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            BigDecimal revenue = new BigDecimal(String.valueOf(10000000 + (Math.random() * 10000000))); // 랜덤 매출
            data.add(new DashboardDto.ChartDataPointDto(monthLabel, revenue, "revenue", current, "#8884d8"));
            current = current.plusMonths(1);
        }
        
        return data;
    }

    private List<DashboardDto.ChartDataPointDto> getDailyRevenueData(Long companyId, LocalDate startDate, LocalDate endDate) {
        List<DashboardDto.ChartDataPointDto> data = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            String dayLabel = current.format(DateTimeFormatter.ofPattern("MM-dd"));
            BigDecimal revenue = new BigDecimal(String.valueOf(300000 + (Math.random() * 500000))); // 랜덤 일매출
            data.add(new DashboardDto.ChartDataPointDto(dayLabel, revenue, "revenue", current, "#8884d8"));
            current = current.plusDays(1);
        }
        
        return data;
    }

    private List<DashboardDto.ChartDataPointDto> getTopCustomerRevenueData(Long companyId, LocalDate startDate, LocalDate endDate, int limit) {
        // 실제 구현에서는 Customer와 Order 조인하여 조회
        return List.of(
                new DashboardDto.ChartDataPointDto("ABC 회사", new BigDecimal("5000000"), "customer", LocalDate.now(), "#8884d8"),
                new DashboardDto.ChartDataPointDto("XYZ 기업", new BigDecimal("3500000"), "customer", LocalDate.now(), "#82ca9d"),
                new DashboardDto.ChartDataPointDto("123 상사", new BigDecimal("2800000"), "customer", LocalDate.now(), "#ffc658"),
                new DashboardDto.ChartDataPointDto("DEF 회사", new BigDecimal("2200000"), "customer", LocalDate.now(), "#ff7300"),
                new DashboardDto.ChartDataPointDto("GHI 기업", new BigDecimal("1800000"), "customer", LocalDate.now(), "#ff0000")
        );
    }

    private List<DashboardDto.ChartDataPointDto> getMonthlyOrderData(Long companyId, LocalDate startDate, LocalDate endDate) {
        List<DashboardDto.ChartDataPointDto> data = new ArrayList<>();
        LocalDate current = startDate.withDayOfMonth(1);
        
        while (!current.isAfter(endDate)) {
            String monthLabel = current.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            BigDecimal orderCount = new BigDecimal(String.valueOf(50 + (Math.random() * 100))); // 랜덤 주문 수
            data.add(new DashboardDto.ChartDataPointDto(monthLabel, orderCount, "orders", current, "#82ca9d"));
            current = current.plusMonths(1);
        }
        
        return data;
    }

    private List<DashboardDto.ChartDataPointDto> getMonthlyAttendanceData(Long companyId, LocalDate startDate, LocalDate endDate) {
        List<DashboardDto.ChartDataPointDto> data = new ArrayList<>();
        LocalDate current = startDate.withDayOfMonth(1);
        
        while (!current.isAfter(endDate)) {
            String monthLabel = current.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            BigDecimal attendanceRate = new BigDecimal(String.valueOf(90 + (Math.random() * 10))); // 90-100% 출석률
            data.add(new DashboardDto.ChartDataPointDto(monthLabel, attendanceRate, "attendance", current, "#ffc658"));
            current = current.plusMonths(1);
        }
        
        return data;
    }

    private List<DashboardDto.ChartDataPointDto> getMonthlyNewHiresData(Long companyId, LocalDate startDate, LocalDate endDate) {
        List<DashboardDto.ChartDataPointDto> data = new ArrayList<>();
        LocalDate current = startDate.withDayOfMonth(1);
        
        while (!current.isAfter(endDate)) {
            String monthLabel = current.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            BigDecimal newHires = new BigDecimal(String.valueOf((int)(Math.random() * 10))); // 0-10명
            data.add(new DashboardDto.ChartDataPointDto(monthLabel, newHires, "newHires", current, "#ff7300"));
            current = current.plusMonths(1);
        }
        
        return data;
    }

    private String getStatusColor(String status) {
        return switch (status) {
            case "PENDING" -> "#ffc658";
            case "CONFIRMED" -> "#82ca9d";
            case "SHIPPED" -> "#8884d8";
            case "DELIVERED" -> "#00C49F";
            case "COMPLETED" -> "#0088FE";
            case "CANCELLED" -> "#FF8042";
            default -> "#8884d8";
        };
    }

    private String getTypeColor(String type) {
        return switch (type) {
            case "NORMAL" -> "#8884d8";
            case "RUSH" -> "#ff7300";
            case "BACKORDER" -> "#ffc658";
            case "PREORDER" -> "#82ca9d";
            default -> "#8884d8";
        };
    }

    private boolean hasPermission(String userRole, String requiredPermission) {
        return switch (requiredPermission) {
            case "USER" -> true;
            case "MANAGER" -> "MANAGER".equals(userRole) || "ADMIN".equals(userRole);
            case "ADMIN" -> "ADMIN".equals(userRole);
            default -> false;
        };
    }
}
