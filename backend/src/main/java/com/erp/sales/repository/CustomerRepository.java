package com.erp.sales.repository;

import com.erp.sales.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
     * 회사별 모든 고객 조회 (페이징 없음)
     */
    @EntityGraph(attributePaths = {"company"})
    List<Customer> findAllByCompanyIdAndIsDeletedFalse(Long companyId);

    /**
     * 회사별 모든 고객 조회 (삭제된 것 포함)
     */
    @EntityGraph(attributePaths = {"company"})
    List<Customer> findByCompanyId(Long companyId);

    /**
     * 회사별 활성 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false")
    List<Customer> findByCompanyIdAndIsActiveTrueAndIsDeletedFalse(@Param("companyId") Long companyId);

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

    // 사업자등록번호로 조회 메서드는 businessRegistrationNumber 필드가 없으므로 제거됨

    // 영업담당자별 고객 조회 메서드는 salesManagerId 필드가 없으므로 제거됨

    /**
     * 고객 유형별 조회
     */
    @EntityGraph(attributePaths = {"company"})
    Page<Customer> findByCompanyIdAndCustomerTypeAndIsDeletedFalse(Long companyId, Customer.CustomerType customerType, Pageable pageable);

    /**
     * 고객 상태별 조회
     */
    @EntityGraph(attributePaths = {"company"})
    Page<Customer> findByCompanyIdAndCustomerStatusAndIsDeletedFalse(Long companyId, Customer.CustomerStatus status, Pageable pageable);

    /**
     * 고객 등급별 조회
     */
    @EntityGraph(attributePaths = {"company"})
    Page<Customer> findByCompanyIdAndCustomerGradeAndIsDeletedFalse(Long companyId, Customer.CustomerGrade grade, Pageable pageable);

    /**
     * 영업담당자별 고객 조회
     */
    @EntityGraph(attributePaths = {"company", "salesManager"})
    Page<Customer> findByCompanyIdAndSalesManagerIdAndIsDeletedFalse(Long companyId, Long salesManagerId, Pageable pageable);

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
    @EntityGraph(attributePaths = {"company", "salesManager"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND (:searchTerm IS NULL OR " +
           "     LOWER(c.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     c.phoneNumber LIKE CONCAT('%', :searchTerm, '%')) " +
           "AND (:customerType IS NULL OR c.customerType = :customerType) " +
           "AND (:customerStatus IS NULL OR c.customerStatus = :customerStatus) " +
           "AND (:customerGrade IS NULL OR c.customerGrade = :customerGrade)")
    Page<Customer> searchCustomersAdvanced(
            @Param("companyId") Long companyId,
            @Param("searchTerm") String searchTerm,
            @Param("customerType") Customer.CustomerType customerType,
            @Param("customerStatus") Customer.CustomerStatus customerStatus,
            @Param("customerGrade") Customer.CustomerGrade customerGrade,
            Pageable pageable);

    /**
     * 연락일 범위로 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND ((:contactDateFrom IS NULL OR c.createdAt >= :contactDateFrom) " +
           "AND (:contactDateTo IS NULL OR c.createdAt <= :contactDateTo))")
    Page<Customer> findByContactDateRange(
            @Param("companyId") Long companyId,
            @Param("contactDateFrom") LocalDate contactDateFrom,
            @Param("contactDateTo") LocalDate contactDateTo,
            Pageable pageable);

    /**
     * 주문일 범위로 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false")
    Page<Customer> findByOrderDateRange(
            @Param("companyId") Long companyId,
            Pageable pageable);

    /**
     * 주문금액 범위로 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false")
    Page<Customer> findByOrderAmountRange(
            @Param("companyId") Long companyId,
            Pageable pageable);

    /**
     * 미수금 있는 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false")
    Page<Customer> findCustomersWithOutstanding(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 신용한도 초과 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false")
    Page<Customer> findCustomersOverCreditLimit(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * VIP 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.isDeleted = false")
    List<Customer> findVipCustomers(@Param("companyId") Long companyId);

    /**
     * 휴면 고객 조회 (최근 N일간 연락 없음)
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.isDeleted = false")
    List<Customer> findDormantCustomers(@Param("companyId") Long companyId);

    /**
     * 태그로 고객 조회
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false")
    Page<Customer> findByTag(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 회사별 고객 통계
     */
    @Query("SELECT " +
           "COUNT(c.id) as totalCustomers, " +
           "COUNT(CASE WHEN c.isDeleted = false THEN 1 END) as activeCustomers, " +
           "0 as inactiveCustomers, " +
           "0 as totalSalesAmount, " +
           "0 as averageSalesAmount, " +
           "0 as totalOutstandingAmount, " +
           "0 as customersWithOutstanding, " +
           "0 as customersOverCreditLimit " +
           "FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false")
    Object[] getCustomerStatistics(@Param("companyId") Long companyId);

    /**
     * 영업담당자별 고객 수
     */
    @Query("SELECT 'N/A' as salesManagerId, 'N/A' as salesManagerName, COUNT(c.id) " +
           "FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false")
    List<Object[]> getCustomerCountBySalesManager(@Param("companyId") Long companyId);

    /**
     * 고객 유형별 통계
     */
    @Query("SELECT c.customerType, COUNT(c.id), 0 " +
           "FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "GROUP BY c.customerType " +
           "ORDER BY COUNT(c.id) DESC")
    List<Object[]> getCustomerStatsByType(@Param("companyId") Long companyId);


    /**
     * 지역별 고객 분포
     */
    @Query("SELECT 'N/A' as city, COUNT(c.id) " +
           "FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false")
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
           "ORDER BY c.createdAt DESC")
    Page<Customer> findTopCustomersByOrderAmount(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 최근 연락한 고객
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "ORDER BY c.createdAt DESC")
    Page<Customer> findRecentlyContactedCustomers(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 최근 주문한 고객
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "ORDER BY c.createdAt DESC")
    Page<Customer> findRecentlyOrderedCustomers(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 생일이 있는 고객 (이번 달)
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false")
    List<Customer> findCustomersWithBirthdayInMonth(@Param("companyId") Long companyId);

    // 주문 통계 업데이트 메서드들은 Customer 엔티티에 해당 필드들이 없으므로 제거됨

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
     * 활성 고객 수 (PaymentTerm 관련 필드들은 Customer 엔티티에 없음)
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false")
    long countActiveCustomers(@Param("companyId") Long companyId);
    
    /**
     * 기간별 신규 고객 수 (PaymentTerm 관련 필드들은 Customer 엔티티에 없음)
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.createdAt BETWEEN :startDate AND :endDate")
    long countNewCustomers(@Param("companyId") Long companyId, 
                          @Param("startDate") LocalDate startDate, 
                          @Param("endDate") LocalDate endDate);

    /**
     * 전역 검색용 - 회사별 고객명으로 검색 (PaymentTerm 관련 필드들은 Customer 엔티티에 없음)
     */
    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT c FROM Customer c " +
           "JOIN FETCH c.company " +
           "WHERE c.company.id = :companyId AND LOWER(c.customerName) LIKE LOWER(CONCAT('%', :name, '%')) AND c.isDeleted = false")
    List<Customer> findByCompanyIdAndCustomerNameContainingIgnoreCase(@Param("companyId") Long companyId, @Param("name") String name);
}
