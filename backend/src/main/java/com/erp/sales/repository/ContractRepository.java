package com.erp.sales.repository;

import com.erp.sales.entity.Contract;
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
 * 계약 Repository
 * 계약 데이터 액세스를 담당합니다
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    /**
     * 회사별 계약 조회 (페이징)
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Contract> findByCompanyIdAndIsDeletedFalse(Long companyId, Pageable pageable);

    /**
     * 계약번호로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer", "order"})
    Optional<Contract> findByContractNumberAndIsDeletedFalse(String contractNumber);

    /**
     * 회사별 계약번호 중복 확인
     */
    boolean existsByCompanyIdAndContractNumberAndIsDeletedFalse(Long companyId, String contractNumber);

    /**
     * 고객별 계약 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Contract> findByCustomerIdAndIsDeletedFalse(Long customerId, Pageable pageable);

    /**
     * 주문별 계약 조회
     */
    @EntityGraph(attributePaths = {"company", "customer", "order"})
    Optional<Contract> findByOrderIdAndIsDeletedFalse(Long orderId);

    /**
     * 담당자별 계약 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Contract> findByOurRepresentativeIdAndIsDeletedFalse(Long representativeId, Pageable pageable);

    /**
     * 계약 상태별 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Contract> findByCompanyIdAndContractStatusAndIsDeletedFalse(Long companyId, Contract.ContractStatus contractStatus, Pageable pageable);

    /**
     * 계약 유형별 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Contract> findByCompanyIdAndContractTypeAndIsDeletedFalse(Long companyId, Contract.ContractType contractType, Pageable pageable);

    /**
     * 갱신 유형별 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    Page<Contract> findByCompanyIdAndRenewalTypeAndIsDeletedFalse(Long companyId, Contract.RenewalType renewalType, Pageable pageable);

    /**
     * 계약 검색 (계약번호, 고객명, 제목)
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c JOIN c.customer cu WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND (LOWER(c.contractNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(cu.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(cu.customerCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Contract> searchContracts(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 고급 검색
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c JOIN c.customer cu WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND (:searchTerm IS NULL OR " +
           "     LOWER(c.contractNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(cu.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(cu.customerCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "     LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:customerId IS NULL OR c.customer.id = :customerId) " +
           "AND (:contractStatus IS NULL OR c.contractStatus = :contractStatus) " +
           "AND (:contractType IS NULL OR c.contractType = :contractType) " +
           "AND (:renewalType IS NULL OR c.renewalType = :renewalType) " +
           "AND (:ourRepresentativeId IS NULL OR c.ourRepresentativeId = :ourRepresentativeId)")
    Page<Contract> searchContractsAdvanced(
            @Param("companyId") Long companyId,
            @Param("searchTerm") String searchTerm,
            @Param("customerId") Long customerId,
            @Param("contractStatus") Contract.ContractStatus contractStatus,
            @Param("contractType") Contract.ContractType contractType,
            @Param("renewalType") Contract.RenewalType renewalType,
            @Param("ourRepresentativeId") Long ourRepresentativeId,
            Pageable pageable);

    /**
     * 시작일 범위로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND ((:startDateFrom IS NULL OR c.startDate >= :startDateFrom) " +
           "AND (:startDateTo IS NULL OR c.startDate <= :startDateTo))")
    Page<Contract> findByStartDateRange(
            @Param("companyId") Long companyId,
            @Param("startDateFrom") LocalDate startDateFrom,
            @Param("startDateTo") LocalDate startDateTo,
            Pageable pageable);

    /**
     * 종료일 범위로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND ((:endDateFrom IS NULL OR c.endDate >= :endDateFrom) " +
           "AND (:endDateTo IS NULL OR c.endDate <= :endDateTo))")
    Page<Contract> findByEndDateRange(
            @Param("companyId") Long companyId,
            @Param("endDateFrom") LocalDate endDateFrom,
            @Param("endDateTo") LocalDate endDateTo,
            Pageable pageable);

    /**
     * 서명일 범위로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND ((:signedDateFrom IS NULL OR c.signedDate >= :signedDateFrom) " +
           "AND (:signedDateTo IS NULL OR c.signedDate <= :signedDateTo))")
    Page<Contract> findBySignedDateRange(
            @Param("companyId") Long companyId,
            @Param("signedDateFrom") LocalDate signedDateFrom,
            @Param("signedDateTo") LocalDate signedDateTo,
            Pageable pageable);

    /**
     * 계약금액 범위로 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND ((:contractAmountFrom IS NULL OR c.totalAmount >= :contractAmountFrom) " +
           "AND (:contractAmountTo IS NULL OR c.totalAmount <= :contractAmountTo))")
    Page<Contract> findByContractAmountRange(
            @Param("companyId") Long companyId,
            @Param("contractAmountFrom") BigDecimal contractAmountFrom,
            @Param("contractAmountTo") BigDecimal contractAmountTo,
            Pageable pageable);

    /**
     * 활성 계약 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.contractStatus = 'ACTIVE' " +
           "AND c.endDate >= :currentDate " +
           "AND (c.effectiveDate IS NULL OR c.effectiveDate <= :currentDate)")
    List<Contract> findActiveContracts(@Param("companyId") Long companyId, @Param("currentDate") LocalDate currentDate);

    /**
     * 만료된 계약 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.endDate < :currentDate " +
           "AND c.contractStatus NOT IN ('EXPIRED', 'TERMINATED', 'COMPLETED')")
    List<Contract> findExpiredContracts(@Param("companyId") Long companyId, @Param("currentDate") LocalDate currentDate);

    /**
     * 만료 임박 계약 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.endDate BETWEEN :currentDate AND :expiryDate " +
           "AND c.contractStatus = 'ACTIVE'")
    List<Contract> findExpiringSoonContracts(
            @Param("companyId") Long companyId, 
            @Param("currentDate") LocalDate currentDate, 
            @Param("expiryDate") LocalDate expiryDate);

    /**
     * 갱신 알림 필요 계약 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.contractStatus = 'ACTIVE' " +
           "AND c.renewalType != 'NONE' " +
           "AND c.renewalNoticeDays IS NOT NULL " +
           "AND c.endDate <= :noticeDate " +
           "AND c.endDate > :currentDate")
    List<Contract> findContractsNeedingRenewalNotice(@Param("companyId") Long companyId, @Param("currentDate") LocalDate currentDate, @Param("noticeDate") LocalDate noticeDate);

    /**
     * 자동 갱신 계약 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.autoRenewalEnabled = true " +
           "AND c.contractStatus = 'ACTIVE' " +
           "AND c.endDate BETWEEN :currentDate AND :renewalDate")
    List<Contract> findAutoRenewalContracts(
            @Param("companyId") Long companyId, 
            @Param("currentDate") LocalDate currentDate, 
            @Param("renewalDate") LocalDate renewalDate);

    /**
     * 중단된 계약 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.contractStatus = 'SUSPENDED'")
    List<Contract> findSuspendedContracts(@Param("companyId") Long companyId);

    /**
     * 태그로 계약 조회
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.tags LIKE CONCAT('%', :tag, '%')")
    Page<Contract> findByTag(@Param("companyId") Long companyId, @Param("tag") String tag, Pageable pageable);

    /**
     * 회사별 계약 통계
     */
    @Query("SELECT " +
           "COUNT(c.id) as totalContracts, " +
           "COUNT(CASE WHEN c.contractStatus = 'ACTIVE' THEN 1 END) as activeContracts, " +
           "COUNT(CASE WHEN c.endDate < CURRENT_DATE THEN 1 END) as expiredContracts, " +
           "COUNT(CASE WHEN c.endDate BETWEEN CURRENT_DATE AND :expiryDate AND c.contractStatus = 'ACTIVE' THEN 1 END) as expiringSoonContracts, " +
           "COUNT(CASE WHEN c.contractStatus = 'COMPLETED' THEN 1 END) as completedContracts, " +
           "COUNT(CASE WHEN c.contractStatus = 'TERMINATED' THEN 1 END) as terminatedContracts, " +
           "COUNT(CASE WHEN c.contractStatus = 'ACTIVE' AND c.renewalType != 'NONE' AND c.renewalNoticeDays IS NOT NULL AND c.endDate <= :noticeDate AND c.endDate > CURRENT_DATE THEN 1 END) as needsRenewalNoticeContracts, " +
           "COALESCE(SUM(c.totalAmount), 0) as totalContractValue, " +
           "COALESCE(SUM(CASE WHEN c.contractStatus = 'ACTIVE' THEN c.totalAmount END), 0) as activeContractValue, " +
           "COALESCE(AVG(c.totalAmount), 0) as averageContractValue, " +
           "0 as averageContractDuration, " +
           "COUNT(CASE WHEN c.autoRenewalEnabled = true THEN 1 END) as autoRenewalContracts " +
           "FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false")
    Object[] getContractStatistics(@Param("companyId") Long companyId, @Param("expiryDate") LocalDate expiryDate, @Param("noticeDate") LocalDate noticeDate);

    /**
     * 담당자별 계약 통계
     */
    @Query("SELECT c.ourRepresentativeId, c.ourRepresentativeName, COUNT(c.id), COALESCE(SUM(c.totalAmount), 0) " +
           "FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.ourRepresentativeId IS NOT NULL " +
           "GROUP BY c.ourRepresentativeId, c.ourRepresentativeName " +
           "ORDER BY SUM(c.totalAmount) DESC")
    List<Object[]> getContractStatsByRepresentative(@Param("companyId") Long companyId);

    /**
     * 계약 상태별 통계
     */
    @Query("SELECT c.contractStatus, COUNT(c.id), COALESCE(SUM(c.totalAmount), 0) " +
           "FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "GROUP BY c.contractStatus " +
           "ORDER BY COUNT(c.id) DESC")
    List<Object[]> getContractStatsByStatus(@Param("companyId") Long companyId);

    /**
     * 계약 유형별 통계
     */
    @Query("SELECT c.contractType, COUNT(c.id), COALESCE(SUM(c.totalAmount), 0) " +
           "FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "GROUP BY c.contractType " +
           "ORDER BY COUNT(c.id) DESC")
    List<Object[]> getContractStatsByType(@Param("companyId") Long companyId);

    /**
     * 월별 계약 통계
     */
    @Query("SELECT YEAR(c.startDate), MONTH(c.startDate), COUNT(c.id), COALESCE(SUM(c.totalAmount), 0) " +
           "FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.startDate >= :fromDate " +
           "GROUP BY YEAR(c.startDate), MONTH(c.startDate) " +
           "ORDER BY YEAR(c.startDate), MONTH(c.startDate)")
    List<Object[]> getContractStatsByMonth(@Param("companyId") Long companyId, @Param("fromDate") LocalDate fromDate);

    /**
     * 고객별 계약 통계
     */
    @Query("SELECT cu.id, cu.customerName, COUNT(c.id), COALESCE(SUM(c.totalAmount), 0) " +
           "FROM Contract c JOIN c.customer cu WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "GROUP BY cu.id, cu.customerName " +
           "ORDER BY SUM(c.totalAmount) DESC")
    List<Object[]> getContractStatsByCustomer(@Param("companyId") Long companyId);

    /**
     * 평균 계약 기간 (일)
     * FUNCTION 호출 문제로 인해 주석 처리 - 서비스 레이어에서 계산하도록 수정 필요
     */
    // @Query("SELECT AVG(FUNCTION('TIMESTAMPDIFF', 'DAY', c.startDate, c.endDate)) " +
    //        "FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
    //        "AND c.startDate >= :fromDate")
    // Double getAverageContractDuration(@Param("companyId") Long companyId, @Param("fromDate") LocalDate fromDate);

    /**
     * 계약 갱신율
     */
    @Query("SELECT " +
           "(COUNT(CASE WHEN c.contractStatus = 'COMPLETED' AND EXISTS (SELECT 1 FROM Contract c2 WHERE c2.customer.id = c.customer.id AND c2.startDate > c.endDate) THEN 1 END) * 100.0 / " +
           " NULLIF(COUNT(CASE WHEN c.contractStatus = 'COMPLETED' THEN 1 END), 0)) as renewalRate " +
           "FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.endDate >= :fromDate")
    Double getRenewalRate(@Param("companyId") Long companyId, @Param("fromDate") LocalDate fromDate);

    /**
     * 상위 계약 (금액 기준)
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "ORDER BY c.totalAmount DESC")
    Page<Contract> findTopContractsByAmount(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 최근 계약
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "ORDER BY c.startDate DESC, c.createdAt DESC")
    Page<Contract> findRecentContracts(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 오늘 시작하는 계약
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.startDate = :today")
    List<Contract> findTodayStartingContracts(@Param("companyId") Long companyId, @Param("today") LocalDate today);

    /**
     * 오늘 종료하는 계약
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.endDate = :today")
    List<Contract> findTodayEndingContracts(@Param("companyId") Long companyId, @Param("today") LocalDate today);

    /**
     * 이번 달 시작하는 계약
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND YEAR(c.startDate) = :year AND MONTH(c.startDate) = :month")
    List<Contract> findThisMonthStartingContracts(@Param("companyId") Long companyId, @Param("year") int year, @Param("month") int month);

    /**
     * 이번 달 종료하는 계약
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND YEAR(c.endDate) = :year AND MONTH(c.endDate) = :month")
    List<Contract> findThisMonthEndingContracts(@Param("companyId") Long companyId, @Param("year") int year, @Param("month") int month);

    /**
     * 갱신 유형별 계약 통계
     */
    @Query("SELECT c.renewalType, COUNT(c.id), COALESCE(SUM(c.totalAmount), 0) " +
           "FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "GROUP BY c.renewalType " +
           "ORDER BY COUNT(c.id) DESC")
    List<Object[]> getContractStatsByRenewalType(@Param("companyId") Long companyId);

    /**
     * 계약 기간별 분포
     * FUNCTION 호출 문제로 인해 주석 처리 - 서비스 레이어에서 계산하도록 수정 필요
     */
    // @Query("SELECT " +
    //        "CASE " +
    //        "  WHEN FUNCTION('TIMESTAMPDIFF', 'DAY', c.startDate, c.endDate) <= 30 THEN '1개월 이하' " +
    //        "  WHEN FUNCTION('TIMESTAMPDIFF', 'DAY', c.startDate, c.endDate) <= 90 THEN '3개월 이하' " +
    //        "  WHEN FUNCTION('TIMESTAMPDIFF', 'DAY', c.startDate, c.endDate) <= 180 THEN '6개월 이하' " +
    //        "  WHEN FUNCTION('TIMESTAMPDIFF', 'DAY', c.startDate, c.endDate) <= 365 THEN '1년 이하' " +
    //        "  ELSE '1년 초과' " +
    //        "END as durationRange, " +
    //        "COUNT(c.id), COALESCE(AVG(c.totalAmount), 0) " +
    //        "FROM Contract c WHERE c.company.id = :companyId AND c.isDeleted = false " +
    //        "GROUP BY " +
    //        "CASE " +
    //        "  WHEN FUNCTION('TIMESTAMPDIFF', 'DAY', c.startDate, c.endDate) <= 30 THEN '1개월 이하' " +
    //        "  WHEN FUNCTION('TIMESTAMPDIFF', 'DAY', c.startDate, c.endDate) <= 90 THEN '3개월 이하' " +
    //        "  WHEN FUNCTION('TIMESTAMPDIFF', 'DAY', c.startDate, c.endDate) <= 180 THEN '6개월 이하' " +
    //        "  WHEN FUNCTION('TIMESTAMPDIFF', 'DAY', c.startDate, c.endDate) <= 365 THEN '1년 이하' " +
    //        "  ELSE '1년 초과' " +
    //        "END " +
    //        "ORDER BY COUNT(c.id) DESC")
    // List<Object[]> getContractDistributionByDuration(@Param("companyId") Long companyId);

    /**
     * 고객의 최근 계약
     */
    @EntityGraph(attributePaths = {"company", "customer"})
    @Query("SELECT c FROM Contract c WHERE c.customer.id = :customerId AND c.isDeleted = false " +
           "ORDER BY c.startDate DESC")
    Page<Contract> findRecentContractsByCustomer(@Param("customerId") Long customerId, Pageable pageable);

    /**
     * 계약 만료 상태 일괄 업데이트
     */
    @Query("UPDATE Contract c SET c.contractStatus = 'EXPIRED' " +
           "WHERE c.company.id = :companyId AND c.isDeleted = false " +
           "AND c.endDate < :currentDate " +
           "AND c.contractStatus = 'ACTIVE'")
    void updateExpiredContracts(@Param("companyId") Long companyId, @Param("currentDate") LocalDate currentDate);
}
