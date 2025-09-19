package com.erp.accounting.repository;

import com.erp.accounting.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 거래 레포지토리
 * 거래 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * 거래번호로 조회
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "LEFT JOIN FETCH t.inputBy ib " +
           "LEFT JOIN FETCH t.approvedBy ab " +
           "WHERE t.transactionNumber = :transactionNumber AND t.isDeleted = false")
    Optional<Transaction> findByTransactionNumber(@Param("transactionNumber") String transactionNumber);

    /**
     * 회사별 거래 목록 조회
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "LEFT JOIN FETCH t.inputBy ib " +
           "WHERE t.company.id = :companyId AND t.isDeleted = false " +
           "ORDER BY t.transactionDate DESC, t.transactionNumber DESC")
    List<Transaction> findByCompanyId(@Param("companyId") Long companyId);

    /**
     * 계정과목별 거래 목록 조회
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "LEFT JOIN FETCH t.inputBy ib " +
           "WHERE t.account.id = :accountId AND t.isDeleted = false " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);

    /**
     * 거래일자 범위로 조회
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "LEFT JOIN FETCH t.inputBy ib " +
           "WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.isDeleted = false " +
           "ORDER BY t.transactionDate DESC, t.transactionNumber DESC")
    List<Transaction> findByTransactionDateBetween(@Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate);

    /**
     * 회사별 거래일자 범위로 조회
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "LEFT JOIN FETCH t.inputBy ib " +
           "WHERE t.company.id = :companyId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.isDeleted = false " +
           "ORDER BY t.transactionDate DESC, t.transactionNumber DESC")
    List<Transaction> findByCompanyIdAndTransactionDateBetween(@Param("companyId") Long companyId,
                                                             @Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

    /**
     * 거래 유형별 조회
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "WHERE t.transactionType = :transactionType AND t.isDeleted = false " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findByTransactionType(@Param("transactionType") Transaction.TransactionType transactionType);

    /**
     * 거래 상태별 조회
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "WHERE t.transactionStatus = :transactionStatus AND t.isDeleted = false " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findByTransactionStatus(@Param("transactionStatus") Transaction.TransactionStatus transactionStatus);

    /**
     * 승인 대기 중인 거래 조회
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "LEFT JOIN FETCH t.inputBy ib " +
           "WHERE t.transactionStatus = 'PENDING' AND t.isDeleted = false " +
           "ORDER BY t.transactionDate, t.createdAt")
    List<Transaction> findPendingTransactions();

    /**
     * 회사별 승인 대기 중인 거래 조회
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "LEFT JOIN FETCH t.inputBy ib " +
           "WHERE t.company.id = :companyId AND t.transactionStatus = 'PENDING' " +
           "AND t.isDeleted = false " +
           "ORDER BY t.transactionDate, t.createdAt")
    List<Transaction> findPendingTransactionsByCompany(@Param("companyId") Long companyId);

    /**
     * 회계연도/월별 거래 조회
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "WHERE t.company.id = :companyId " +
           "AND t.fiscalYear = :fiscalYear AND t.fiscalMonth = :fiscalMonth " +
           "AND t.isDeleted = false " +
           "ORDER BY t.transactionDate, t.transactionNumber")
    List<Transaction> findByCompanyIdAndFiscalYearAndFiscalMonth(@Param("companyId") Long companyId,
                                                               @Param("fiscalYear") Integer fiscalYear,
                                                               @Param("fiscalMonth") Integer fiscalMonth);

    /**
     * 회계연도별 거래 조회
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "WHERE t.company.id = :companyId AND t.fiscalYear = :fiscalYear " +
           "AND t.isDeleted = false " +
           "ORDER BY t.transactionDate, t.transactionNumber")
    List<Transaction> findByCompanyIdAndFiscalYear(@Param("companyId") Long companyId,
                                                  @Param("fiscalYear") Integer fiscalYear);

    /**
     * 거래 검색
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "LEFT JOIN FETCH t.inputBy ib " +
           "WHERE (t.transactionNumber LIKE %:searchTerm% " +
           "OR t.description LIKE %:searchTerm% " +
           "OR t.memo LIKE %:searchTerm% " +
           "OR a.name LIKE %:searchTerm%) " +
           "AND t.isDeleted = false")
    Page<Transaction> searchTransactions(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 회사별 거래 검색
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "LEFT JOIN FETCH t.inputBy ib " +
           "WHERE t.company.id = :companyId " +
           "AND (t.transactionNumber LIKE %:searchTerm% " +
           "OR t.description LIKE %:searchTerm% " +
           "OR t.memo LIKE %:searchTerm% " +
           "OR a.name LIKE %:searchTerm%) " +
           "AND t.isDeleted = false")
    Page<Transaction> searchTransactionsByCompany(@Param("companyId") Long companyId,
                                                @Param("searchTerm") String searchTerm,
                                                Pageable pageable);

    /**
     * 거래번호 중복 확인
     */
    boolean existsByTransactionNumberAndIsDeletedFalse(String transactionNumber);

    /**
     * 전체 거래 목록 조회 (페이징)
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "LEFT JOIN FETCH t.inputBy ib " +
           "WHERE t.isDeleted = false")
    Page<Transaction> findAllWithDetails(Pageable pageable);

    /**
     * 회사별 거래 목록 조회 (페이징)
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "LEFT JOIN FETCH t.inputBy ib " +
           "WHERE t.company.id = :companyId AND t.isDeleted = false")
    Page<Transaction> findByCompanyIdWithDetails(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 계정과목별 차변 합계
     */
    @Query("SELECT COALESCE(SUM(t.debitAmount), 0) FROM Transaction t " +
           "WHERE t.account.id = :accountId " +
           "AND t.transactionStatus = 'POSTED' " +
           "AND t.isDeleted = false")
    BigDecimal sumDebitAmountByAccountId(@Param("accountId") Long accountId);

    /**
     * 계정과목별 대변 합계
     */
    @Query("SELECT COALESCE(SUM(t.creditAmount), 0) FROM Transaction t " +
           "WHERE t.account.id = :accountId " +
           "AND t.transactionStatus = 'POSTED' " +
           "AND t.isDeleted = false")
    BigDecimal sumCreditAmountByAccountId(@Param("accountId") Long accountId);

    /**
     * 계정과목별 기간별 차변 합계
     */
    @Query("SELECT COALESCE(SUM(t.debitAmount), 0) FROM Transaction t " +
           "WHERE t.account.id = :accountId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.transactionStatus = 'POSTED' " +
           "AND t.isDeleted = false")
    BigDecimal sumDebitAmountByAccountIdAndDateBetween(@Param("accountId") Long accountId,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    /**
     * 계정과목별 기간별 대변 합계
     */
    @Query("SELECT COALESCE(SUM(t.creditAmount), 0) FROM Transaction t " +
           "WHERE t.account.id = :accountId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.transactionStatus = 'POSTED' " +
           "AND t.isDeleted = false")
    BigDecimal sumCreditAmountByAccountIdAndDateBetween(@Param("accountId") Long accountId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    /**
     * 일별 거래 통계
     */
    @Query("SELECT t.transactionDate, COUNT(t), SUM(t.debitAmount + t.creditAmount) " +
           "FROM Transaction t " +
           "WHERE t.company.id = :companyId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.isDeleted = false " +
           "GROUP BY t.transactionDate " +
           "ORDER BY t.transactionDate")
    List<Object[]> getDailyTransactionStats(@Param("companyId") Long companyId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    /**
     * 월별 거래 통계
     */
    @Query("SELECT t.fiscalYear, t.fiscalMonth, COUNT(t), SUM(t.debitAmount + t.creditAmount) " +
           "FROM Transaction t " +
           "WHERE t.company.id = :companyId " +
           "AND t.fiscalYear = :fiscalYear " +
           "AND t.isDeleted = false " +
           "GROUP BY t.fiscalYear, t.fiscalMonth " +
           "ORDER BY t.fiscalMonth")
    List<Object[]> getMonthlyTransactionStats(@Param("companyId") Long companyId,
                                             @Param("fiscalYear") Integer fiscalYear);

    /**
     * 거래 유형별 통계
     */
    @Query("SELECT t.transactionType, COUNT(t), SUM(t.debitAmount + t.creditAmount) " +
           "FROM Transaction t " +
           "WHERE t.company.id = :companyId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.isDeleted = false " +
           "GROUP BY t.transactionType")
    List<Object[]> getTransactionStatsByType(@Param("companyId") Long companyId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    /**
     * 거래 상태별 통계
     */
    @Query("SELECT t.transactionStatus, COUNT(t) " +
           "FROM Transaction t " +
           "WHERE t.company.id = :companyId AND t.isDeleted = false " +
           "GROUP BY t.transactionStatus")
    List<Object[]> getTransactionCountByStatus(@Param("companyId") Long companyId);

    /**
     * 계정과목별 거래 건수 통계
     */
    @Query("SELECT a.name, COUNT(t) " +
           "FROM Transaction t " +
           "JOIN t.account a " +
           "WHERE t.company.id = :companyId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.isDeleted = false " +
           "GROUP BY a.id, a.name " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> getTransactionCountByAccount(@Param("companyId") Long companyId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 입력자별 거래 건수 통계
     */
    @Query("SELECT e.name, COUNT(t) " +
           "FROM Transaction t " +
           "JOIN t.inputBy e " +
           "WHERE t.company.id = :companyId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.isDeleted = false " +
           "GROUP BY e.id, e.name " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> getTransactionCountByInputBy(@Param("companyId") Long companyId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 거래 금액 TOP 10
     */
    @Query("SELECT t FROM Transaction t " +
           "JOIN FETCH t.company c " +
           "JOIN FETCH t.account a " +
           "WHERE t.company.id = :companyId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.isDeleted = false " +
           "ORDER BY (t.debitAmount + t.creditAmount) DESC")
    List<Transaction> findTopTransactionsByAmount(@Param("companyId") Long companyId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 Pageable pageable);

    /**
     * 시산표 데이터 조회
     */
    @Query("SELECT a.accountCode, a.name, a.accountType, " +
           "COALESCE(SUM(t.debitAmount), 0), COALESCE(SUM(t.creditAmount), 0) " +
           "FROM Account a " +
           "LEFT JOIN Transaction t ON t.account = a " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.transactionStatus = 'POSTED' " +
           "AND t.isDeleted = false " +
           "WHERE a.company.id = :companyId AND a.trackBalance = true " +
           "AND a.isDeleted = false " +
           "GROUP BY a.id, a.accountCode, a.name, a.accountType " +
           "ORDER BY a.accountType, a.sortOrder, a.accountCode")
    List<Object[]> getTrialBalanceData(@Param("companyId") Long companyId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
}
