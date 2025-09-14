package com.erp.sales.repository;

import com.erp.sales.entity.Customer;
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
 * 고객 Repository
 * 고객 데이터 액세스를 담당합니다
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * 회사별 고객 조회 (페이징)
     */
    @EntityGraph(attributePaths = {"company"})
    Page<Customer> findByCompanyIdAndIsDeletedFalse(Long companyId, Pageable pageable);

    /**
     * 회사별 활성 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    List<Customer> findByCompanyIdAndIsActiveTrueAndIsDeletedFalse(Long companyId);

    /**
     * 고객코드로 조회
     */
    @EntityGraph(attributePaths = {"company"})
    Optional<Customer> findByCustomerCodeAndIsDeletedFalse(String customerCode);

    /**
     * 회사별 고객코드 중복 확인
     */
    boolean existsByCompanyIdAndCustomerCodeAndIsDeletedFalse(Long companyId, String customerCode);

    /**
     * 회사별 고객코드 중복 확인 (수정시 자신 제외)
     */
    boolean existsByCompanyIdAndCustomerCodeAndIdNotAndIsDeletedFalse(Long companyId, String customerCode, Long excludeId);

    /**
     * 사업자등록번호로 조회
     */
    @EntityGraph(attributePaths = {"company"})
    Optional<Customer> findByBusinessRegistrationNumberAndIsDeletedFalse(String businessRegistrationNumber);

    /**
     * 영업담당자별 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    Page<Customer> findBySalesManagerIdAndIsDeletedFalse(Long salesManagerId, Pageable pageable);

    /**
     * 고객 유형별 조회
     */
    @EntityGraph(attributePaths = {"company"})
    Page<Customer> findByCompanyIdAndCustomerTypeAndIsDeletedFalse(Long companyId, Customer.CustomerType customerType, Pageable pageable);

    /**
     * 고객 상태별 조회
     */
    @EntityGraph(attributePaths = {"company"})
    Page<Customer> findByCompanyIdAndCustomerStatusAndIsDeletedFalse(Long companyId, Customer.CustomerStatus customerStatus, Pageable pageable);

    /**
     * 고객 등급별 조회
     */
    @EntityGraph(attributePaths = {"company"})
    Page<Customer> findByCompanyIdAndCustomerGradeAndIsDeletedFalse(Long companyId, Customer.CustomerGrade customerGrade, Pageable pageable);

    /**
     * 고객 검색 (이름, 코드, 이메일, 전화번호)
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND (LOWER(c.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR c.phoneNumber LIKE CONCAT('%', :searchTerm, '%'))")
    Page<Customer> searchCustomers(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 고급 검색
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND (:searchTerm IS NULL OR " +
           "     LOWER(c.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     c.phoneNumber LIKE CONCAT('%', :searchTerm, '%')) " +
           "AND (:customerType IS NULL OR c.customerType = :customerType) " +
           "AND (:customerStatus IS NULL OR c.customerStatus = :customerStatus) " +
           "AND (:customerGrade IS NULL OR c.customerGrade = :customerGrade) " +
           "AND (:isActive IS NULL OR c.isActive = :isActive) " +
           "AND (:salesManagerId IS NULL OR c.salesManagerId = :salesManagerId) " +
           "AND (:city IS NULL OR LOWER(c.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
           "AND (:businessType IS NULL OR LOWER(c.businessType) LIKE LOWER(CONCAT('%', :businessType, '%')))")
    Page<Customer> searchCustomersAdvanced(
            @Param("companyId") Long companyId,
            @Param("searchTerm") String searchTerm,
            @Param("customerType") Customer.CustomerType customerType,
            @Param("customerStatus") Customer.CustomerStatus customerStatus,
            @Param("customerGrade") Customer.CustomerGrade customerGrade,
            @Param("isActive") Boolean isActive,
            @Param("salesManagerId") Long salesManagerId,
            @Param("city") String city,
            @Param("businessType") String businessType,
            Pageable pageable);

    /**
     * 연락일 범위로 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND ((:contactDateFrom IS NULL OR c.lastContactDate >= :contactDateFrom) " +
           "AND (:contactDateTo IS NULL OR c.lastContactDate <= :contactDateTo))")
    Page<Customer> findByContactDateRange(
            @Param("companyId") Long companyId,
            @Param("contactDateFrom") LocalDate contactDateFrom,
            @Param("contactDateTo") LocalDate contactDateTo,
            Pageable pageable);

    /**
     * 주문일 범위로 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND ((:orderDateFrom IS NULL OR c.lastOrderDate >= :orderDateFrom) " +
           "AND (:orderDateTo IS NULL OR c.lastOrderDate <= :orderDateTo))")
    Page<Customer> findByOrderDateRange(
            @Param("companyId") Long companyId,
            @Param("orderDateFrom") LocalDate orderDateFrom,
            @Param("orderDateTo") LocalDate orderDateTo,
            Pageable pageable);

    /**
     * 주문금액 범위로 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND ((:orderAmountFrom IS NULL OR c.totalOrderAmount >= :orderAmountFrom) " +
           "AND (:orderAmountTo IS NULL OR c.totalOrderAmount <= :orderAmountTo))")
    Page<Customer> findByOrderAmountRange(
            @Param("companyId") Long companyId,
            @Param("orderAmountFrom") BigDecimal orderAmountFrom,
            @Param("orderAmountTo") BigDecimal orderAmountTo,
            Pageable pageable);

    /**
     * 미수금 있는 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.outstandingAmount > 0")
    Page<Customer> findCustomersWithOutstanding(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 신용한도 초과 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.creditLimit > 0 AND c.outstandingAmount > c.creditLimit")
    Page<Customer> findCustomersOverCreditLimit(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * VIP 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND (c.customerStatus = 'VIP' OR c.customerGrade = 'PLATINUM')")
    List<Customer> findVipCustomers(@Param("companyId") Long companyId);

    /**
     * 휴면 고객 조회 (최근 N일간 연락 없음)
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.isActive = true " +
           "AND (c.lastContactDate IS NULL OR c.lastContactDate < :cutoffDate)")
    List<Customer> findDormantCustomers(@Param("companyId") Long companyId, @Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 태그로 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.tags LIKE CONCAT('%', :tag, '%')")
    Page<Customer> findByTag(@Param("companyId") Long companyId, @Param("tag") String tag, Pageable pageable);

    /**
     * 회사별 고객 통계
     */
    @Query("SELECT " +
           "COUNT(c.id) as totalCustomers, " +
           "COUNT(CASE WHEN c.isActive = true THEN 1 END) as activeCustomers, " +
           "COUNT(CASE WHEN c.isActive = false THEN 1 END) as inactiveCustomers, " +
           "COUNT(CASE WHEN c.customerStatus = 'VIP' OR c.customerGrade = 'PLATINUM' THEN 1 END) as vipCustomers, " +
           "COUNT(CASE WHEN c.customerStatus = 'PROSPECT' THEN 1 END) as prospectCustomers, " +
           "COUNT(CASE WHEN c.customerStatus = 'DORMANT' THEN 1 END) as dormantCustomers, " +
           "COALESCE(SUM(c.totalOrderAmount), 0) as totalSalesAmount, " +
           "COALESCE(AVG(c.totalOrderAmount), 0) as averageSalesAmount, " +
           "COALESCE(SUM(c.outstandingAmount), 0) as totalOutstandingAmount, " +
           "COUNT(CASE WHEN c.outstandingAmount > 0 THEN 1 END) as customersWithOutstanding, " +
           "COUNT(CASE WHEN c.creditLimit > 0 AND c.outstandingAmount > c.creditLimit THEN 1 END) as customersOverCreditLimit " +
           "FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false")
    Object[] getCustomerStatistics(@Param("companyId") Long companyId);

    /**
     * 영업담당자별 고객 수
     */
    @Query("SELECT c.salesManagerId, c.salesManagerName, COUNT(c.id) " +
           "FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.salesManagerId IS NOT NULL " +
           "GROUP BY c.salesManagerId, c.salesManagerName " +
           "ORDER BY COUNT(c.id) DESC")
    List<Object[]> getCustomerCountBySalesManager(@Param("companyId") Long companyId);

    /**
     * 고객 유형별 통계
     */
    @Query("SELECT c.customerType, COUNT(c.id), COALESCE(SUM(c.totalOrderAmount), 0) " +
           "FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "GROUP BY c.customerType " +
           "ORDER BY COUNT(c.id) DESC")
    List<Object[]> getCustomerStatsByType(@Param("companyId") Long companyId);

    /**
     * 고객 등급별 통계
     */
    @Query("SELECT c.customerGrade, COUNT(c.id), COALESCE(SUM(c.totalOrderAmount), 0) " +
           "FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "GROUP BY c.customerGrade " +
           "ORDER BY COUNT(c.id) DESC")
    List<Object[]> getCustomerStatsByGrade(@Param("companyId") Long companyId);

    /**
     * 지역별 고객 분포
     */
    @Query("SELECT c.city, COUNT(c.id) " +
           "FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.city IS NOT NULL " +
           "GROUP BY c.city " +
           "ORDER BY COUNT(c.id) DESC")
    List<Object[]> getCustomerDistributionByCity(@Param("companyId") Long companyId);

    /**
     * 월별 신규 고객 수
     */
    @Query("SELECT YEAR(c.createdAt), MONTH(c.createdAt), COUNT(c.id) " +
           "FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.createdAt >= :fromDate " +
           "GROUP BY YEAR(c.createdAt), MONTH(c.createdAt) " +
           "ORDER BY YEAR(c.createdAt), MONTH(c.createdAt)")
    List<Object[]> getNewCustomersByMonth(@Param("companyId") Long companyId, @Param("fromDate") LocalDate fromDate);

    /**
     * 상위 고객 (주문금액 기준)
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.totalOrderAmount > 0 " +
           "ORDER BY c.totalOrderAmount DESC")
    Page<Customer> findTopCustomersByOrderAmount(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 최근 연락한 고객
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.lastContactDate IS NOT NULL " +
           "ORDER BY c.lastContactDate DESC")
    Page<Customer> findRecentlyContactedCustomers(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 최근 주문한 고객
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.lastOrderDate IS NOT NULL " +
           "ORDER BY c.lastOrderDate DESC")
    Page<Customer> findRecentlyOrderedCustomers(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 생일이 있는 고객 (이번 달)
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.metadata LIKE CONCAT('%birthday%', :month, '%')")
    List<Customer> findCustomersWithBirthdayInMonth(@Param("companyId") Long companyId, @Param("month") String month);

    /**
     * 고객 주문 통계 업데이트
     */
    @Query("UPDATE Customer c SET " +
           "c.totalOrderCount = (SELECT COUNT(o.id) FROM Order o WHERE o.customer.id = c.id AND o.isDeleted = false), " +
           "c.totalOrderAmount = (SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.customer.id = c.id AND o.isDeleted = false), " +
           "c.lastOrderDate = (SELECT MAX(o.orderDate) FROM Order o WHERE o.customer.id = c.id AND o.isDeleted = false), " +
           "c.outstandingAmount = (SELECT COALESCE(SUM(o.outstandingAmount), 0) FROM Order o WHERE o.customer.id = c.id AND o.isDeleted = false AND o.paymentStatus != 'PAID') " +
           "WHERE c.id = :customerId")
    void updateCustomerOrderStatistics(@Param("customerId") Long customerId);

    /**
     * 모든 고객의 주문 통계 일괄 업데이트
     */
    @Query("UPDATE Customer c SET " +
           "c.totalOrderCount = (SELECT COUNT(o.id) FROM Order o WHERE o.customer.id = c.id AND o.isDeleted = false), " +
           "c.totalOrderAmount = (SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.customer.id = c.id AND o.isDeleted = false), " +
           "c.lastOrderDate = (SELECT MAX(o.orderDate) FROM Order o WHERE o.customer.id = c.id AND o.isDeleted = false), " +
           "c.outstandingAmount = (SELECT COALESCE(SUM(o.outstandingAmount), 0) FROM Order o WHERE o.customer.id = c.id AND o.isDeleted = false AND o.paymentStatus != 'PAID') " +
           "WHERE c.company.id = :companyId AND c.isDeleted = false")
    void updateAllCustomerOrderStatistics(@Param("companyId") Long companyId);

    // ==================== 기본 카운트 메서드들 ====================
    
    /**
     * 회사별 삭제되지 않은 고객 수
     */
    long countByCompanyIdAndIsDeletedFalse(Long companyId);
    
    /**
     * 재주문이 필요한 고객 수 (기본 구현)
     */
    default long getReorderCustomerCount(Long companyId) {
        return 0; // 기본 구현
    }
    
    /**
     * 활성 고객 수
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false AND c.isActive = true")
    long countActiveCustomers(@Param("companyId") Long companyId);
    
    /**
     * 기간별 신규 고객 수
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.createdAt BETWEEN :startDate AND :endDate")
    long countNewCustomers(@Param("companyId") Long companyId, 
                          @Param("startDate") LocalDate startDate, 
                          @Param("endDate") LocalDate endDate);

    /**
     * 전역 검색용 - 회사별 고객명으로 검색
     */
    @EntityGraph(attributePaths = {"company"})
    List<Customer> findByCompanyIdAndCustomerNameContainingIgnoreCase(Long companyId, String customerName);
}
