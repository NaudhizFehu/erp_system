package com.erp.sales.repository;

import com.erp.sales.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 주문 Repository
 * 주문 데이터 액세스를 담당합니다
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 회사별 주문 조회 (페이징)
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Order> findByCompanyIdAndIsDeletedFalse(Long companyId, Pageable pageable);

    /**
     * 주문번호로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer", "quote"})
    Optional<Order> findByOrderNumberAndIsDeletedFalse(String orderNumber);

    /**
     * 회사별 주문번호 중복 확인
     */
    boolean existsByCompanyIdAndOrderNumberAndIsDeletedFalse(Long companyId, String orderNumber);

    /**
     * 고객별 주문 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Order> findByCustomerIdAndIsDeletedFalse(Long customerId, Pageable pageable);

    /**
     * 견적서별 주문 조회
     */
    @EntityGraph(attributePaths = {"company", "customer", "quote"})
    Optional<Order> findByQuoteIdAndIsDeletedFalse(Long quoteId);

    /**
     * 영업담당자별 주문 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Order> findBySalesRepIdAndIsDeletedFalse(Long salesRepId, Pageable pageable);

    /**
     * 주문 상태별 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Order> findByCompanyIdAndOrderStatusAndIsDeletedFalse(Long companyId, Order.OrderStatus orderStatus, Pageable pageable);

    /**
     * 주문 유형별 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Order> findByCompanyIdAndOrderTypeAndIsDeletedFalse(Long companyId, Order.OrderType orderType, Pageable pageable);

    /**
     * 결제 상태별 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Order> findByCompanyIdAndPaymentStatusAndIsDeletedFalse(Long companyId, Order.PaymentStatus paymentStatus, Pageable pageable);

    /**
     * 주문 검색 (주문번호, 고객명)
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o JOIN o.customer c WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND (LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Order> searchOrders(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 고급 검색
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o JOIN o.customer c WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND (:searchTerm IS NULL OR " +
           "     LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(c.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:customerId IS NULL OR o.customer.id = :customerId) " +
           "AND (:orderStatus IS NULL OR o.orderStatus = :orderStatus) " +
           "AND (:orderType IS NULL OR o.orderType = :orderType) " +
           "AND (:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus) " +
           "AND (:salesRepId IS NULL OR o.salesRepId = :salesRepId) " +
           "AND (:deliveryMethod IS NULL OR LOWER(o.deliveryMethod) LIKE LOWER(CONCAT('%', :deliveryMethod, '%')))")
    Page<Order> searchOrdersAdvanced(
            @Param("companyId") Long companyId,
            @Param("searchTerm") String searchTerm,
            @Param("customerId") Long customerId,
            @Param("orderStatus") Order.OrderStatus orderStatus,
            @Param("orderType") Order.OrderType orderType,
            @Param("paymentStatus") Order.PaymentStatus paymentStatus,
            @Param("salesRepId") Long salesRepId,
            @Param("deliveryMethod") String deliveryMethod,
            Pageable pageable);

    /**
     * 주문일 범위로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND ((:orderDateFrom IS NULL OR o.orderDate >= :orderDateFrom) " +
           "AND (:orderDateTo IS NULL OR o.orderDate <= :orderDateTo))")
    Page<Order> findByOrderDateRange(
            @Param("companyId") Long companyId,
            @Param("orderDateFrom") LocalDate orderDateFrom,
            @Param("orderDateTo") LocalDate orderDateTo,
            Pageable pageable);

    /**
     * 납기일 범위로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND ((:requiredDateFrom IS NULL OR o.requiredDate >= :requiredDateFrom) " +
           "AND (:requiredDateTo IS NULL OR o.requiredDate <= :requiredDateTo))")
    Page<Order> findByRequiredDateRange(
            @Param("companyId") Long companyId,
            @Param("requiredDateFrom") LocalDate requiredDateFrom,
            @Param("requiredDateTo") LocalDate requiredDateTo,
            Pageable pageable);

    /**
     * 배송일 범위로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND ((:deliveryDateFrom IS NULL OR o.deliveryDate >= :deliveryDateFrom) " +
           "AND (:deliveryDateTo IS NULL OR o.deliveryDate <= :deliveryDateTo))")
    Page<Order> findByDeliveryDateRange(
            @Param("companyId") Long companyId,
            @Param("deliveryDateFrom") LocalDate deliveryDateFrom,
            @Param("deliveryDateTo") LocalDate deliveryDateTo,
            Pageable pageable);

    /**
     * 주문금액 범위로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND ((:totalAmountFrom IS NULL OR o.totalAmount >= :totalAmountFrom) " +
           "AND (:totalAmountTo IS NULL OR o.totalAmount <= :totalAmountTo))")
    Page<Order> findByTotalAmountRange(
            @Param("companyId") Long companyId,
            @Param("totalAmountFrom") BigDecimal totalAmountFrom,
            @Param("totalAmountTo") BigDecimal totalAmountTo,
            Pageable pageable);

    /**
     * 미수금 있는 주문 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.outstandingAmount > 0")
    Page<Order> findOrdersWithOutstanding(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 연체된 주문 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.paymentStatus = 'OVERDUE'")
    Page<Order> findOverdueOrders(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 긴급 주문 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND (o.orderType = 'RUSH' OR (o.requiredDate IS NOT NULL AND o.requiredDate <= :urgentDate))")
    List<Order> findUrgentOrders(@Param("companyId") Long companyId, @Param("urgentDate") LocalDate urgentDate);

    /**
     * 배송 지연 주문 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.requiredDate IS NOT NULL AND o.requiredDate < :currentDate " +
           "AND o.orderStatus NOT IN ('DELIVERED', 'COMPLETED', 'CANCELLED')")
    List<Order> findDelayedOrders(@Param("companyId") Long companyId, @Param("currentDate") LocalDate currentDate);

    /**
     * 태그로 주문 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.tags LIKE CONCAT('%', :tag, '%')")
    Page<Order> findByTag(@Param("companyId") Long companyId, @Param("tag") String tag, Pageable pageable);

    /**
     * 회사별 주문 통계
     */
    @Query("SELECT " +
           "COUNT(o.id) as totalOrders, " +
           "COUNT(CASE WHEN o.orderStatus = 'PENDING' THEN 1 END) as pendingOrders, " +
           "COUNT(CASE WHEN o.orderStatus = 'CONFIRMED' THEN 1 END) as confirmedOrders, " +
           "COUNT(CASE WHEN o.orderStatus = 'SHIPPED' THEN 1 END) as shippedOrders, " +
           "COUNT(CASE WHEN o.orderStatus = 'DELIVERED' THEN 1 END) as deliveredOrders, " +
           "COUNT(CASE WHEN o.orderStatus = 'COMPLETED' THEN 1 END) as completedOrders, " +
           "COUNT(CASE WHEN o.orderStatus = 'CANCELLED' THEN 1 END) as cancelledOrders, " +
           "COALESCE(SUM(o.totalAmount), 0) as totalOrderAmount, " +
           "COALESCE(AVG(o.totalAmount), 0) as averageOrderAmount, " +
           "COALESCE(SUM(o.paidAmount), 0) as totalPaidAmount, " +
           "COALESCE(SUM(o.outstandingAmount), 0) as totalOutstandingAmount, " +
           "COUNT(CASE WHEN o.paymentStatus = 'OVERDUE' THEN 1 END) as overdueOrders, " +
           "COUNT(CASE WHEN o.orderType = 'RUSH' THEN 1 END) as urgentOrders " +
           "FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false")
    Object[] getOrderStatistics(@Param("companyId") Long companyId);

    /**
     * 영업담당자별 주문 통계
     */
    @Query("SELECT o.salesRepId, o.salesRepName, COUNT(o.id), COALESCE(SUM(o.totalAmount), 0) " +
           "FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.salesRepId IS NOT NULL " +
           "GROUP BY o.salesRepId, o.salesRepName " +
           "ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> getOrderStatsBySalesRep(@Param("companyId") Long companyId);

    /**
     * 주문 상태별 통계
     */
    @Query("SELECT o.orderStatus, COUNT(o.id), COALESCE(SUM(o.totalAmount), 0) " +
           "FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "GROUP BY o.orderStatus " +
           "ORDER BY COUNT(o.id) DESC")
    List<Object[]> getOrderStatsByStatus(@Param("companyId") Long companyId);

    /**
     * 월별 주문 통계
     */
    @Query("SELECT YEAR(o.orderDate), MONTH(o.orderDate), COUNT(o.id), COALESCE(SUM(o.totalAmount), 0) " +
           "FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.orderDate >= :fromDate " +
           "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate) " +
           "ORDER BY YEAR(o.orderDate), MONTH(o.orderDate)")
    List<Object[]> getOrderStatsByMonth(@Param("companyId") Long companyId, @Param("fromDate") LocalDate fromDate);

    /**
     * 일별 주문 통계
     */
    @Query("SELECT o.orderDate, COUNT(o.id), COALESCE(SUM(o.totalAmount), 0) " +
           "FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.orderDate >= :fromDate " +
           "GROUP BY o.orderDate " +
           "ORDER BY o.orderDate")
    List<Object[]> getOrderStatsByDay(@Param("companyId") Long companyId, @Param("fromDate") LocalDate fromDate);

    /**
     * 고객별 주문 통계
     */
    @Query("SELECT c.id, c.customerName, COUNT(o.id), COALESCE(SUM(o.totalAmount), 0) " +
           "FROM Order o JOIN o.customer c WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "GROUP BY c.id, c.customerName " +
           "ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> getOrderStatsByCustomer(@Param("companyId") Long companyId);

    /**
     * 상위 주문 (금액 기준)
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "ORDER BY o.totalAmount DESC")
    Page<Order> findTopOrdersByAmount(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 최근 주문
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "ORDER BY o.orderDate DESC, o.createdAt DESC")
    Page<Order> findRecentOrders(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 오늘 주문
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.orderDate = :today")
    List<Order> findTodayOrders(@Param("companyId") Long companyId, @Param("today") LocalDate today);

    /**
     * 이번 주 주문
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.orderDate >= :weekStart AND o.orderDate <= :weekEnd")
    List<Order> findThisWeekOrders(
            @Param("companyId") Long companyId, 
            @Param("weekStart") LocalDate weekStart, 
            @Param("weekEnd") LocalDate weekEnd);

    /**
     * 이번 달 주문
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month")
    List<Order> findThisMonthOrders(@Param("companyId") Long companyId, @Param("year") int year, @Param("month") int month);

    /**
     * 배송 예정 주문 (내일까지)
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.requiredDate IS NOT NULL AND o.requiredDate <= :tomorrow " +
           "AND o.orderStatus IN ('CONFIRMED', 'PROCESSING')")
    List<Order> findOrdersForDelivery(@Param("companyId") Long companyId, @Param("tomorrow") LocalDate tomorrow);

    /**
     * 평균 주문 처리 시간 (일)
     * FUNCTION 호출 문제로 인해 주석 처리 - 서비스 레이어에서 계산하도록 수정 필요
     */
    // @Query("SELECT AVG(FUNCTION('TIMESTAMPDIFF', 'DAY', o.orderDate, o.completedDate)) " +
    //        "FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
    //        "AND o.completedDate IS NOT NULL AND o.orderDate >= :fromDate")
    // Double getAverageProcessingDays(@Param("companyId") Long companyId, @Param("fromDate") LocalDate fromDate);

    /**
     * 주문 취소율
     */
    @Query("SELECT " +
           "(COUNT(CASE WHEN o.orderStatus = 'CANCELLED' THEN 1 END) * 100.0 / COUNT(o.id)) as cancellationRate " +
           "FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.orderDate >= :fromDate")
    Double getCancellationRate(@Param("companyId") Long companyId, @Param("fromDate") LocalDate fromDate);

    /**
     * 재주문 고객 수
     */
    @Query("SELECT COUNT(DISTINCT o.customer.id) " +
           "FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.customer.id IN (SELECT o2.customer.id FROM Order o2 WHERE o2.company.id = :companyId AND o2.isDeleted = false GROUP BY o2.customer.id HAVING COUNT(o2.id) > 1)")
    Long getReorderCustomerCount(@Param("companyId") Long companyId);

    /**
     * 배송방법별 주문 통계
     */
    @Query("SELECT o.deliveryMethod, COUNT(o.id), COALESCE(SUM(o.totalAmount), 0) " +
           "FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.deliveryMethod IS NOT NULL " +
           "GROUP BY o.deliveryMethod " +
           "ORDER BY COUNT(o.id) DESC")
    List<Object[]> getOrderStatsByDeliveryMethod(@Param("companyId") Long companyId);

    /**
     * 결제방법별 주문 통계
     */
    @Query("SELECT o.paymentMethod, COUNT(o.id), COALESCE(SUM(o.totalAmount), 0) " +
           "FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.paymentMethod IS NOT NULL " +
           "GROUP BY o.paymentMethod " +
           "ORDER BY COUNT(o.id) DESC")
    List<Object[]> getOrderStatsByPaymentMethod(@Param("companyId") Long companyId);

    // ==================== Dashboard용 메서드들 ====================
    
    /**
     * 회사별 삭제되지 않은 주문 수
     */
    long countByCompanyIdAndIsDeletedFalse(Long companyId);
    
    /**
     * 기간별 주문 수
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.orderDate BETWEEN :startDate AND :endDate")
    long countOrdersByDateRange(@Param("companyId") Long companyId, 
                               @Param("startDate") LocalDate startDate, 
                               @Param("endDate") LocalDate endDate);
    
    /**
     * 대기중인 주문 수
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.orderStatus = 'PENDING'")
    long countPendingOrders(@Param("companyId") Long companyId);
    
    /**
     * 주문 유형별 통계
     */
    @Query("SELECT o.orderStatus, COUNT(o) FROM Order o WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "GROUP BY o.orderStatus")
    List<Object[]> getOrderStatsByType(@Param("companyId") Long companyId);

    /**
     * 전역 검색용 - 회사별 주문번호로 검색
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    List<Order> findByCompanyIdAndOrderNumberContainingIgnoreCase(Long companyId, String orderNumber);
}
