package com.erp.sales.repository;

import com.erp.sales.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 주문 레포지토리
 * 실제 DB 스키마와 완전히 일치하도록 수정됨
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 회사별 주문 목록 조회 (삭제되지 않은 것만)
     */
    Page<Order> findByCompanyIdAndIsDeletedFalse(Long companyId, Pageable pageable);

    /**
     * 주문 상세 조회 (삭제되지 않은 것만)
     */
    Optional<Order> findByIdAndIsDeletedFalse(Long id);

    /**
     * 주문번호로 검색 (삭제되지 않은 것만) - Page
     */
    Page<Order> findByCompanyIdAndOrderNumberContainingIgnoreCaseAndIsDeletedFalse(
            Long companyId, String orderNumber, Pageable pageable);

    /**
     * 주문번호로 검색 (삭제되지 않은 것만) - List
     */
    List<Order> findByCompanyIdAndOrderNumberContainingIgnoreCaseAndIsDeletedFalse(
            Long companyId, String orderNumber);

    /**
     * 상태별 주문 조회 (삭제되지 않은 것만)
     */
    List<Order> findByCompanyIdAndOrderStatusAndIsDeletedFalse(Long companyId, Order.OrderStatus status);

    /**
     * 고객별 주문 조회 (삭제되지 않은 것만)
     */
    List<Order> findByCompanyIdAndCustomerIdAndIsDeletedFalse(Long companyId, Long customerId);

    /**
     * 기간별 주문 조회 (삭제되지 않은 것만)
     */
    List<Order> findByCompanyIdAndOrderDateBetweenAndIsDeletedFalse(
            Long companyId, LocalDate startDate, LocalDate endDate);

    /**
     * 결제상태별 주문 조회 (삭제되지 않은 것만)
     */
    List<Order> findByCompanyIdAndPaymentStatusAndIsDeletedFalse(Long companyId, Order.PaymentStatus paymentStatus);

    /**
     * 회사별 주문 통계
     */
    @Query("SELECT o.orderStatus, COUNT(o) FROM Order o " +
           "WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "GROUP BY o.orderStatus")
    List<Object[]> getOrderStatsByStatus(@Param("companyId") Long companyId);

    /**
     * 회사별 결제상태 통계
     */
    @Query("SELECT o.paymentStatus, COUNT(o) FROM Order o " +
           "WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "GROUP BY o.paymentStatus")
    List<Object[]> getOrderStatsByPaymentStatus(@Param("companyId") Long companyId);

    /**
     * 회사별 월별 주문 통계
     */
    @Query("SELECT YEAR(o.orderDate), MONTH(o.orderDate), COUNT(o), SUM(o.totalAmount) FROM Order o " +
           "WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate) " +
           "ORDER BY YEAR(o.orderDate) DESC, MONTH(o.orderDate) DESC")
    List<Object[]> getMonthlyOrderStats(@Param("companyId") Long companyId);

    /**
     * 회사별 고객별 주문 통계
     */
    @Query("SELECT o.customer.customerName, COUNT(o), SUM(o.totalAmount) FROM Order o " +
           "WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "GROUP BY o.customer.id, o.customer.customerName " +
           "ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> getOrderStatsByCustomer(@Param("companyId") Long companyId);

    /**
     * 회사별 총 주문 수
     */
    long countByCompanyIdAndIsDeletedFalse(Long companyId);

    /**
     * 회사별 총 주문 금액
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
           "WHERE o.company.id = :companyId AND o.isDeleted = false")
    Double getTotalOrderAmount(@Param("companyId") Long companyId);

    /**
     * 회사별 평균 주문 금액
     */
    @Query("SELECT COALESCE(AVG(o.totalAmount), 0) FROM Order o " +
           "WHERE o.company.id = :companyId AND o.isDeleted = false")
    Double getAverageOrderAmount(@Param("companyId") Long companyId);

    /**
     * 기간별 주문 수 조회 (Dashboard용)
     */
    @Query("SELECT COUNT(o) FROM Order o " +
           "WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.orderDate BETWEEN :startDate AND :endDate")
    Long countOrdersByDateRange(@Param("companyId") Long companyId, 
                               @Param("startDate") LocalDate startDate, 
                               @Param("endDate") LocalDate endDate);

    /**
     * 대기 중인 주문 수 조회 (Dashboard용)
     */
    @Query("SELECT COUNT(o) FROM Order o " +
           "WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "AND o.orderStatus = 'PENDING'")
    Long countPendingOrders(@Param("companyId") Long companyId);

    /**
     * 주문 유형별 통계 (Dashboard용)
     */
    @Query("SELECT 'STANDARD' as orderType, COUNT(o) as count FROM Order o " +
           "WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "GROUP BY 'STANDARD'")
    List<Object[]> getOrderStatsByType(@Param("companyId") Long companyId);

    /**
     * 영업담당자별 주문 통계 (Dashboard용)
     */
    @Query("SELECT '관리자' as salesRepName, COUNT(o) as count FROM Order o " +
           "WHERE o.company.id = :companyId AND o.isDeleted = false " +
           "GROUP BY '관리자'")
    List<Object[]> getOrderStatsBySalesRep(@Param("companyId") Long companyId);
}