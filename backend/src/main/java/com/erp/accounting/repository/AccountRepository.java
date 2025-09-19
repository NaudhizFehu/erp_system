package com.erp.accounting.repository;

import com.erp.accounting.entity.Account;
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
 * 계정과목 레포지토리
 * 계정과목 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * 계정과목 코드로 조회
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "LEFT JOIN FETCH a.parentAccount p " +
           "WHERE a.accountCode = :accountCode AND a.isDeleted = false")
    Optional<Account> findByAccountCode(@Param("accountCode") String accountCode);

    /**
     * 회사별 계정과목 코드로 조회
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "LEFT JOIN FETCH a.parentAccount p " +
           "WHERE a.company.id = :companyId AND a.accountCode = :accountCode AND a.isDeleted = false")
    Optional<Account> findByCompanyIdAndAccountCode(@Param("companyId") Long companyId, 
                                                   @Param("accountCode") String accountCode);

    /**
     * 회사별 계정과목 목록 조회
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "LEFT JOIN FETCH a.parentAccount p " +
           "WHERE a.company.id = :companyId AND a.isDeleted = false " +
           "ORDER BY a.accountLevel, a.sortOrder, a.accountCode")
    List<Account> findByCompanyId(@Param("companyId") Long companyId);

    /**
     * 회사별 활성 계정과목 목록 조회
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "LEFT JOIN FETCH a.parentAccount p " +
           "WHERE a.company.id = :companyId AND a.isActive = true AND a.isDeleted = false " +
           "ORDER BY a.accountLevel, a.sortOrder, a.accountCode")
    List<Account> findActiveByCompanyId(@Param("companyId") Long companyId);

    /**
     * 계정과목 유형별 조회
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "LEFT JOIN FETCH a.parentAccount p " +
           "WHERE a.accountType = :accountType AND a.isDeleted = false " +
           "ORDER BY a.company.id, a.accountLevel, a.sortOrder")
    List<Account> findByAccountType(@Param("accountType") Account.AccountType accountType);

    /**
     * 계정과목 분류별 조회
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "LEFT JOIN FETCH a.parentAccount p " +
           "WHERE a.accountCategory = :accountCategory AND a.isDeleted = false " +
           "ORDER BY a.company.id, a.accountLevel, a.sortOrder")
    List<Account> findByAccountCategory(@Param("accountCategory") Account.AccountCategory accountCategory);

    /**
     * 상위 계정과목별 하위 계정 조회
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "WHERE a.parentAccount.id = :parentAccountId AND a.isDeleted = false " +
           "ORDER BY a.sortOrder, a.accountCode")
    List<Account> findByParentAccountId(@Param("parentAccountId") Long parentAccountId);

    /**
     * 레벨별 계정과목 조회
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "LEFT JOIN FETCH a.parentAccount p " +
           "WHERE a.accountLevel = :level AND a.isDeleted = false " +
           "ORDER BY a.company.id, a.sortOrder, a.accountCode")
    List<Account> findByAccountLevel(@Param("level") Integer level);

    /**
     * 말단 계정과목 조회 (거래 입력 가능한 계정)
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "WHERE a.company.id = :companyId " +
           "AND a.isActive = true AND a.isDeleted = false " +
           "AND NOT EXISTS (SELECT 1 FROM Account child WHERE child.parentAccount = a AND child.isDeleted = false) " +
           "ORDER BY a.accountType, a.sortOrder, a.accountCode")
    List<Account> findLeafAccountsByCompanyId(@Param("companyId") Long companyId);

    /**
     * 잔액 추적 계정과목 조회
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "WHERE a.company.id = :companyId AND a.trackBalance = true AND a.isDeleted = false " +
           "ORDER BY a.accountType, a.sortOrder")
    List<Account> findTrackingBalanceAccountsByCompanyId(@Param("companyId") Long companyId);

    /**
     * 계정과목 검색
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "LEFT JOIN FETCH a.parentAccount p " +
           "WHERE (a.accountCode LIKE %:searchTerm% OR a.name LIKE %:searchTerm%) " +
           "AND a.isDeleted = false")
    Page<Account> searchAccounts(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 회사별 계정과목 검색
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "LEFT JOIN FETCH a.parentAccount p " +
           "WHERE a.company.id = :companyId " +
           "AND (a.accountCode LIKE %:searchTerm% OR a.name LIKE %:searchTerm%) " +
           "AND a.isDeleted = false")
    Page<Account> searchAccountsByCompany(@Param("companyId") Long companyId, 
                                        @Param("searchTerm") String searchTerm, 
                                        Pageable pageable);

    /**
     * 계정과목 코드 중복 확인
     */
    boolean existsByAccountCodeAndCompanyIdAndIsDeletedFalse(String accountCode, Long companyId);

    /**
     * 계정과목 코드 중복 확인 (본인 제외)
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Account a " +
           "WHERE a.accountCode = :accountCode AND a.company.id = :companyId " +
           "AND a.id != :excludeId AND a.isDeleted = false")
    boolean existsByAccountCodeAndCompanyIdAndIdNotAndIsDeletedFalse(
            @Param("accountCode") String accountCode, 
            @Param("companyId") Long companyId, 
            @Param("excludeId") Long excludeId);

    /**
     * 전체 계정과목 목록 조회 (페이징)
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "LEFT JOIN FETCH a.parentAccount p " +
           "WHERE a.isDeleted = false")
    Page<Account> findAllWithDetails(Pageable pageable);

    /**
     * 회사별 계정과목 목록 조회 (페이징)
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "LEFT JOIN FETCH a.parentAccount p " +
           "WHERE a.company.id = :companyId AND a.isDeleted = false")
    Page<Account> findByCompanyIdWithDetails(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 예산이 설정된 계정과목 조회
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "WHERE a.budgetAmount > 0 AND a.isDeleted = false " +
           "ORDER BY a.company.id, a.accountType, a.sortOrder")
    List<Account> findAccountsWithBudget();

    /**
     * 잔액이 있는 계정과목 조회
     */
    @Query("SELECT a FROM Account a " +
           "JOIN FETCH a.company c " +
           "WHERE a.currentBalance != 0 AND a.trackBalance = true AND a.isDeleted = false " +
           "ORDER BY a.company.id, a.accountType, a.sortOrder")
    List<Account> findAccountsWithBalance();

    /**
     * 계정과목별 거래 건수 조회
     */
    @Query("SELECT a, COUNT(t) FROM Account a " +
           "LEFT JOIN Transaction t ON t.account = a AND t.isDeleted = false " +
           "WHERE a.company.id = :companyId AND a.isDeleted = false " +
           "GROUP BY a " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> getAccountTransactionCounts(@Param("companyId") Long companyId);

    /**
     * 계정과목 유형별 통계
     */
    @Query("SELECT a.accountType, COUNT(a) FROM Account a " +
           "WHERE a.company.id = :companyId AND a.isDeleted = false " +
           "GROUP BY a.accountType")
    List<Object[]> getAccountCountByType(@Param("companyId") Long companyId);

    /**
     * 계정과목 분류별 통계
     */
    @Query("SELECT a.accountCategory, COUNT(a) FROM Account a " +
           "WHERE a.company.id = :companyId AND a.isDeleted = false " +
           "GROUP BY a.accountCategory")
    List<Object[]> getAccountCountByCategory(@Param("companyId") Long companyId);

    /**
     * 레벨별 계정과목 수 통계
     */
    @Query("SELECT a.accountLevel, COUNT(a) FROM Account a " +
           "WHERE a.company.id = :companyId AND a.isDeleted = false " +
           "GROUP BY a.accountLevel " +
           "ORDER BY a.accountLevel")
    List<Object[]> getAccountCountByLevel(@Param("companyId") Long companyId);

    /**
     * 활성/비활성 계정과목 수 통계
     */
    @Query("SELECT a.isActive, COUNT(a) FROM Account a " +
           "WHERE a.company.id = :companyId AND a.isDeleted = false " +
           "GROUP BY a.isActive")
    List<Object[]> getAccountCountByActiveStatus(@Param("companyId") Long companyId);

    /**
     * 잔액 추적 계정과목 수 통계
     */
    @Query("SELECT a.trackBalance, COUNT(a) FROM Account a " +
           "WHERE a.company.id = :companyId AND a.isDeleted = false " +
           "GROUP BY a.trackBalance")
    List<Object[]> getAccountCountByTrackBalance(@Param("companyId") Long companyId);

    /**
     * 계정과목별 총 잔액 조회 (자산 계정)
     */
    @Query("SELECT SUM(a.currentBalance) FROM Account a " +
           "WHERE a.company.id = :companyId AND a.accountType = 'ASSET' " +
           "AND a.trackBalance = true AND a.isDeleted = false")
    BigDecimal getTotalAssetBalance(@Param("companyId") Long companyId);

    /**
     * 계정과목별 총 잔액 조회 (부채 계정)
     */
    @Query("SELECT SUM(a.currentBalance) FROM Account a " +
           "WHERE a.company.id = :companyId AND a.accountType = 'LIABILITY' " +
           "AND a.trackBalance = true AND a.isDeleted = false")
    BigDecimal getTotalLiabilityBalance(@Param("companyId") Long companyId);

    /**
     * 계정과목별 총 잔액 조회 (자본 계정)
     */
    @Query("SELECT SUM(a.currentBalance) FROM Account a " +
           "WHERE a.company.id = :companyId AND a.accountType = 'EQUITY' " +
           "AND a.trackBalance = true AND a.isDeleted = false")
    BigDecimal getTotalEquityBalance(@Param("companyId") Long companyId);

    /**
     * 특정 기간 수익 계정 잔액 합계
     */
    @Query("SELECT SUM(a.currentBalance) FROM Account a " +
           "WHERE a.company.id = :companyId AND a.accountType = 'REVENUE' " +
           "AND a.trackBalance = true AND a.isDeleted = false")
    BigDecimal getTotalRevenueBalance(@Param("companyId") Long companyId);

    /**
     * 특정 기간 비용 계정 잔액 합계
     */
    @Query("SELECT SUM(a.currentBalance) FROM Account a " +
           "WHERE a.company.id = :companyId AND a.accountType = 'EXPENSE' " +
           "AND a.trackBalance = true AND a.isDeleted = false")
    BigDecimal getTotalExpenseBalance(@Param("companyId") Long companyId);
}
