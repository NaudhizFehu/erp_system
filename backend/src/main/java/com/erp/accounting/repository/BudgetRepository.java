package com.erp.accounting.repository;

import com.erp.accounting.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 예산 레포지토리
 * 예산 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /**
     * 회사별 예산 목록 조회
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "LEFT JOIN FETCH b.responsiblePerson rp " +
           "WHERE b.company.id = :companyId AND b.isDeleted = false " +
           "ORDER BY b.fiscalYear DESC, b.budgetPeriod, b.periodNumber")
    List<Budget> findByCompanyId(@Param("companyId") Long companyId);

    /**
     * 회계연도별 예산 조회
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "LEFT JOIN FETCH b.responsiblePerson rp " +
           "WHERE b.company.id = :companyId AND b.fiscalYear = :fiscalYear " +
           "AND b.isDeleted = false " +
           "ORDER BY b.budgetPeriod, b.periodNumber, b.account.accountCode")
    List<Budget> findByCompanyIdAndFiscalYear(@Param("companyId") Long companyId,
                                             @Param("fiscalYear") Integer fiscalYear);

    /**
     * 회계연도/기간별 예산 조회
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "LEFT JOIN FETCH b.responsiblePerson rp " +
           "WHERE b.company.id = :companyId " +
           "AND b.fiscalYear = :fiscalYear " +
           "AND b.budgetPeriod = :budgetPeriod " +
           "AND (:periodNumber IS NULL OR b.periodNumber = :periodNumber) " +
           "AND b.isDeleted = false " +
           "ORDER BY b.account.accountCode")
    List<Budget> findByCompanyIdAndFiscalYearAndBudgetPeriodAndPeriodNumber(
            @Param("companyId") Long companyId,
            @Param("fiscalYear") Integer fiscalYear,
            @Param("budgetPeriod") Budget.BudgetPeriod budgetPeriod,
            @Param("periodNumber") Integer periodNumber);

    /**
     * 계정과목별 예산 조회
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "LEFT JOIN FETCH b.responsiblePerson rp " +
           "WHERE b.account.id = :accountId AND b.isDeleted = false " +
           "ORDER BY b.fiscalYear DESC, b.budgetPeriod, b.periodNumber")
    List<Budget> findByAccountId(@Param("accountId") Long accountId);

    /**
     * 예산 유형별 조회
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "LEFT JOIN FETCH b.responsiblePerson rp " +
           "WHERE b.company.id = :companyId AND b.budgetType = :budgetType " +
           "AND b.isDeleted = false " +
           "ORDER BY b.fiscalYear DESC, b.budgetPeriod, b.periodNumber")
    List<Budget> findByCompanyIdAndBudgetType(@Param("companyId") Long companyId,
                                             @Param("budgetType") Budget.BudgetType budgetType);

    /**
     * 예산 상태별 조회
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "LEFT JOIN FETCH b.responsiblePerson rp " +
           "WHERE b.company.id = :companyId AND b.budgetStatus = :budgetStatus " +
           "AND b.isDeleted = false " +
           "ORDER BY b.fiscalYear DESC, b.budgetPeriod, b.periodNumber")
    List<Budget> findByCompanyIdAndBudgetStatus(@Param("companyId") Long companyId,
                                               @Param("budgetStatus") Budget.BudgetStatus budgetStatus);

    /**
     * 활성 예산 조회
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "LEFT JOIN FETCH b.responsiblePerson rp " +
           "WHERE b.company.id = :companyId AND b.budgetStatus = 'ACTIVE' " +
           "AND b.isDeleted = false " +
           "ORDER BY b.budgetPeriod, b.periodNumber, b.account.accountCode")
    List<Budget> findActiveBudgetsByCompanyId(@Param("companyId") Long companyId);

    /**
     * 승인 대기 중인 예산 조회
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "LEFT JOIN FETCH b.responsiblePerson rp " +
           "WHERE b.budgetStatus IN ('DRAFT', 'SUBMITTED') AND b.isDeleted = false " +
           "ORDER BY b.createdAt")
    List<Budget> findPendingBudgets();

    /**
     * 예산 초과 조회
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "LEFT JOIN FETCH b.responsiblePerson rp " +
           "WHERE b.company.id = :companyId " +
           "AND b.currentActual > b.budgetAmount " +
           "AND b.budgetStatus = 'ACTIVE' " +
           "AND b.isDeleted = false " +
           "ORDER BY (b.currentActual - b.budgetAmount) DESC")
    List<Budget> findOverBudgetsByCompanyId(@Param("companyId") Long companyId);

    /**
     * 특정 계정과목의 특정 기간 예산 조회
     */
    @Query("SELECT b FROM Budget b " +
           "WHERE b.company.id = :companyId " +
           "AND b.account.id = :accountId " +
           "AND b.fiscalYear = :fiscalYear " +
           "AND b.budgetPeriod = :budgetPeriod " +
           "AND (:periodNumber IS NULL OR b.periodNumber = :periodNumber) " +
           "AND b.isDeleted = false")
    Optional<Budget> findByCompanyIdAndAccountIdAndFiscalYearAndBudgetPeriodAndPeriodNumber(
            @Param("companyId") Long companyId,
            @Param("accountId") Long accountId,
            @Param("fiscalYear") Integer fiscalYear,
            @Param("budgetPeriod") Budget.BudgetPeriod budgetPeriod,
            @Param("periodNumber") Integer periodNumber);

    /**
     * 예산 검색
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "LEFT JOIN FETCH b.responsiblePerson rp " +
           "WHERE (a.name LIKE %:searchTerm% " +
           "OR b.description LIKE %:searchTerm%) " +
           "AND b.isDeleted = false")
    Page<Budget> searchBudgets(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 회사별 예산 검색
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "LEFT JOIN FETCH b.responsiblePerson rp " +
           "WHERE b.company.id = :companyId " +
           "AND (a.name LIKE %:searchTerm% " +
           "OR b.description LIKE %:searchTerm%) " +
           "AND b.isDeleted = false")
    Page<Budget> searchBudgetsByCompany(@Param("companyId") Long companyId,
                                       @Param("searchTerm") String searchTerm,
                                       Pageable pageable);

    /**
     * 전체 예산 목록 조회 (페이징)
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "LEFT JOIN FETCH b.responsiblePerson rp " +
           "WHERE b.isDeleted = false")
    Page<Budget> findAllWithDetails(Pageable pageable);

    /**
     * 회사별 예산 목록 조회 (페이징)
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "LEFT JOIN FETCH b.responsiblePerson rp " +
           "WHERE b.company.id = :companyId AND b.isDeleted = false")
    Page<Budget> findByCompanyIdWithDetails(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 예산 유형별 총 예산액 통계
     */
    @Query("SELECT b.budgetType, SUM(b.budgetAmount), SUM(b.currentActual) " +
           "FROM Budget b " +
           "WHERE b.company.id = :companyId " +
           "AND b.fiscalYear = :fiscalYear " +
           "AND b.budgetStatus = 'ACTIVE' " +
           "AND b.isDeleted = false " +
           "GROUP BY b.budgetType")
    List<Object[]> getBudgetSummaryByType(@Param("companyId") Long companyId,
                                         @Param("fiscalYear") Integer fiscalYear);

    /**
     * 예산 기간별 총 예산액 통계
     */
    @Query("SELECT b.budgetPeriod, b.periodNumber, SUM(b.budgetAmount), SUM(b.currentActual) " +
           "FROM Budget b " +
           "WHERE b.company.id = :companyId " +
           "AND b.fiscalYear = :fiscalYear " +
           "AND b.budgetStatus = 'ACTIVE' " +
           "AND b.isDeleted = false " +
           "GROUP BY b.budgetPeriod, b.periodNumber " +
           "ORDER BY b.budgetPeriod, b.periodNumber")
    List<Object[]> getBudgetSummaryByPeriod(@Param("companyId") Long companyId,
                                           @Param("fiscalYear") Integer fiscalYear);

    /**
     * 예산 상태별 통계
     */
    @Query("SELECT b.budgetStatus, COUNT(b), SUM(b.budgetAmount) " +
           "FROM Budget b " +
           "WHERE b.company.id = :companyId " +
           "AND b.fiscalYear = :fiscalYear " +
           "AND b.isDeleted = false " +
           "GROUP BY b.budgetStatus")
    List<Object[]> getBudgetCountByStatus(@Param("companyId") Long companyId,
                                         @Param("fiscalYear") Integer fiscalYear);

    /**
     * 부서별 예산 통계
     */
    @Query("SELECT b.departmentCode, COUNT(b), SUM(b.budgetAmount), SUM(b.currentActual) " +
           "FROM Budget b " +
           "WHERE b.company.id = :companyId " +
           "AND b.fiscalYear = :fiscalYear " +
           "AND b.departmentCode IS NOT NULL " +
           "AND b.budgetStatus = 'ACTIVE' " +
           "AND b.isDeleted = false " +
           "GROUP BY b.departmentCode")
    List<Object[]> getBudgetSummaryByDepartment(@Param("companyId") Long companyId,
                                               @Param("fiscalYear") Integer fiscalYear);

    /**
     * 담당자별 예산 통계
     */
    @Query("SELECT rp.name, COUNT(b), SUM(b.budgetAmount), SUM(b.currentActual) " +
           "FROM Budget b " +
           "JOIN b.responsiblePerson rp " +
           "WHERE b.company.id = :companyId " +
           "AND b.fiscalYear = :fiscalYear " +
           "AND b.budgetStatus = 'ACTIVE' " +
           "AND b.isDeleted = false " +
           "GROUP BY rp.id, rp.name")
    List<Object[]> getBudgetSummaryByResponsiblePerson(@Param("companyId") Long companyId,
                                                      @Param("fiscalYear") Integer fiscalYear);

    /**
     * 예산 달성률 TOP 10
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "WHERE b.company.id = :companyId " +
           "AND b.fiscalYear = :fiscalYear " +
           "AND b.budgetStatus = 'ACTIVE' " +
           "AND b.budgetAmount > 0 " +
           "AND b.isDeleted = false " +
           "ORDER BY b.achievementRate DESC")
    List<Budget> findTopBudgetsByAchievementRate(@Param("companyId") Long companyId,
                                                @Param("fiscalYear") Integer fiscalYear,
                                                Pageable pageable);

    /**
     * 예산 초과 TOP 10
     */
    @Query("SELECT b FROM Budget b " +
           "JOIN FETCH b.company c " +
           "JOIN FETCH b.account a " +
           "WHERE b.company.id = :companyId " +
           "AND b.fiscalYear = :fiscalYear " +
           "AND b.currentActual > b.budgetAmount " +
           "AND b.budgetStatus = 'ACTIVE' " +
           "AND b.isDeleted = false " +
           "ORDER BY (b.currentActual - b.budgetAmount) DESC")
    List<Budget> findTopOverBudgets(@Param("companyId") Long companyId,
                                   @Param("fiscalYear") Integer fiscalYear,
                                   Pageable pageable);

    /**
     * 총 예산액 조회
     */
    @Query("SELECT COALESCE(SUM(b.budgetAmount), 0) FROM Budget b " +
           "WHERE b.company.id = :companyId " +
           "AND b.fiscalYear = :fiscalYear " +
           "AND b.budgetStatus = 'ACTIVE' " +
           "AND b.isDeleted = false")
    BigDecimal getTotalBudgetAmount(@Param("companyId") Long companyId,
                                   @Param("fiscalYear") Integer fiscalYear);

    /**
     * 총 실적액 조회
     */
    @Query("SELECT COALESCE(SUM(b.currentActual), 0) FROM Budget b " +
           "WHERE b.company.id = :companyId " +
           "AND b.fiscalYear = :fiscalYear " +
           "AND b.budgetStatus = 'ACTIVE' " +
           "AND b.isDeleted = false")
    BigDecimal getTotalActualAmount(@Param("companyId") Long companyId,
                                   @Param("fiscalYear") Integer fiscalYear);

    /**
     * 예산 대비 실적률 조회
     */
    @Query("SELECT CASE WHEN SUM(b.budgetAmount) > 0 " +
           "THEN (SUM(b.currentActual) / SUM(b.budgetAmount)) * 100 " +
           "ELSE 0 END " +
           "FROM Budget b " +
           "WHERE b.company.id = :companyId " +
           "AND b.fiscalYear = :fiscalYear " +
           "AND b.budgetStatus = 'ACTIVE' " +
           "AND b.isDeleted = false")
    BigDecimal getTotalAchievementRate(@Param("companyId") Long companyId,
                                      @Param("fiscalYear") Integer fiscalYear);

    /**
     * 예산 초과 건수 조회
     */
    @Query("SELECT COUNT(b) FROM Budget b " +
           "WHERE b.company.id = :companyId " +
           "AND b.fiscalYear = :fiscalYear " +
           "AND b.currentActual > b.budgetAmount " +
           "AND b.budgetStatus = 'ACTIVE' " +
           "AND b.isDeleted = false")
    Long countOverBudgets(@Param("companyId") Long companyId,
                         @Param("fiscalYear") Integer fiscalYear);
}




