package com.erp.accounting.repository;

import com.erp.accounting.entity.FinancialReport;
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
 * 재무보고서 레포지토리
 * 재무보고서 정보에 대한 데이터베이스 접근을 담당합니다
 */
@Repository
public interface FinancialReportRepository extends JpaRepository<FinancialReport, Long> {

    /**
     * 회사별 재무보고서 목록 조회
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "LEFT JOIN FETCH fr.generatedBy gb " +
           "LEFT JOIN FETCH fr.approvedBy ab " +
           "WHERE fr.company.id = :companyId AND fr.isDeleted = false " +
           "ORDER BY fr.fiscalYear DESC, fr.fiscalPeriod DESC, fr.reportType")
    List<FinancialReport> findByCompanyId(@Param("companyId") Long companyId);

    /**
     * 보고서 유형별 조회
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "LEFT JOIN FETCH fr.generatedBy gb " +
           "WHERE fr.company.id = :companyId AND fr.reportType = :reportType " +
           "AND fr.isDeleted = false " +
           "ORDER BY fr.fiscalYear DESC, fr.fiscalPeriod DESC")
    List<FinancialReport> findByCompanyIdAndReportType(@Param("companyId") Long companyId,
                                                       @Param("reportType") FinancialReport.ReportType reportType);

    /**
     * 회계연도별 보고서 조회
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "LEFT JOIN FETCH fr.generatedBy gb " +
           "WHERE fr.company.id = :companyId AND fr.fiscalYear = :fiscalYear " +
           "AND fr.isDeleted = false " +
           "ORDER BY fr.fiscalPeriod DESC, fr.reportType")
    List<FinancialReport> findByCompanyIdAndFiscalYear(@Param("companyId") Long companyId,
                                                       @Param("fiscalYear") Integer fiscalYear);

    /**
     * 회계연도/기간별 보고서 조회
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "LEFT JOIN FETCH fr.generatedBy gb " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.fiscalYear = :fiscalYear " +
           "AND fr.fiscalPeriod = :fiscalPeriod " +
           "AND fr.isDeleted = false " +
           "ORDER BY fr.reportType")
    List<FinancialReport> findByCompanyIdAndFiscalYearAndFiscalPeriod(
            @Param("companyId") Long companyId,
            @Param("fiscalYear") Integer fiscalYear,
            @Param("fiscalPeriod") String fiscalPeriod);

    /**
     * 특정 보고서 조회 (중복 방지)
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.reportType = :reportType " +
           "AND fr.fiscalYear = :fiscalYear " +
           "AND fr.fiscalPeriod = :fiscalPeriod " +
           "AND fr.isDeleted = false")
    Optional<FinancialReport> findByCompanyIdAndReportTypeAndFiscalYearAndFiscalPeriod(
            @Param("companyId") Long companyId,
            @Param("reportType") FinancialReport.ReportType reportType,
            @Param("fiscalYear") Integer fiscalYear,
            @Param("fiscalPeriod") String fiscalPeriod);

    /**
     * 보고서 상태별 조회
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "LEFT JOIN FETCH fr.generatedBy gb " +
           "WHERE fr.company.id = :companyId AND fr.reportStatus = :reportStatus " +
           "AND fr.isDeleted = false " +
           "ORDER BY fr.fiscalYear DESC, fr.fiscalPeriod DESC")
    List<FinancialReport> findByCompanyIdAndReportStatus(@Param("companyId") Long companyId,
                                                         @Param("reportStatus") FinancialReport.ReportStatus reportStatus);

    /**
     * 승인된 보고서 조회
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "LEFT JOIN FETCH fr.generatedBy gb " +
           "LEFT JOIN FETCH fr.approvedBy ab " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.reportStatus IN ('APPROVED', 'PUBLISHED') " +
           "AND fr.isDeleted = false " +
           "ORDER BY fr.fiscalYear DESC, fr.fiscalPeriod DESC")
    List<FinancialReport> findApprovedReportsByCompanyId(@Param("companyId") Long companyId);

    /**
     * 승인 대기 중인 보고서 조회
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "LEFT JOIN FETCH fr.generatedBy gb " +
           "WHERE fr.reportStatus IN ('GENERATED', 'REVIEWED') " +
           "AND fr.isDeleted = false " +
           "ORDER BY fr.generatedAt")
    List<FinancialReport> findPendingReports();

    /**
     * 기준일 범위로 보고서 조회
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "LEFT JOIN FETCH fr.generatedBy gb " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.baseDate BETWEEN :startDate AND :endDate " +
           "AND fr.isDeleted = false " +
           "ORDER BY fr.baseDate DESC")
    List<FinancialReport> findByCompanyIdAndBaseDateBetween(@Param("companyId") Long companyId,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    /**
     * 보고서 검색
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "LEFT JOIN FETCH fr.generatedBy gb " +
           "WHERE (fr.reportTitle LIKE %:searchTerm% " +
           "OR fr.remarks LIKE %:searchTerm%) " +
           "AND fr.isDeleted = false")
    Page<FinancialReport> searchReports(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * 회사별 보고서 검색
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "LEFT JOIN FETCH fr.generatedBy gb " +
           "WHERE fr.company.id = :companyId " +
           "AND (fr.reportTitle LIKE %:searchTerm% " +
           "OR fr.remarks LIKE %:searchTerm%) " +
           "AND fr.isDeleted = false")
    Page<FinancialReport> searchReportsByCompany(@Param("companyId") Long companyId,
                                                @Param("searchTerm") String searchTerm,
                                                Pageable pageable);

    /**
     * 전체 보고서 목록 조회 (페이징)
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "LEFT JOIN FETCH fr.generatedBy gb " +
           "WHERE fr.isDeleted = false")
    Page<FinancialReport> findAllWithDetails(Pageable pageable);

    /**
     * 회사별 보고서 목록 조회 (페이징)
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "LEFT JOIN FETCH fr.generatedBy gb " +
           "WHERE fr.company.id = :companyId AND fr.isDeleted = false")
    Page<FinancialReport> findByCompanyIdWithDetails(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 최신 재무상태표 조회
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.reportType = 'BALANCE_SHEET' " +
           "AND fr.reportStatus IN ('APPROVED', 'PUBLISHED') " +
           "AND fr.isDeleted = false " +
           "ORDER BY fr.fiscalYear DESC, fr.fiscalPeriod DESC " +
           "LIMIT 1")
    Optional<FinancialReport> findLatestBalanceSheet(@Param("companyId") Long companyId);

    /**
     * 최신 손익계산서 조회
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.reportType = 'INCOME_STATEMENT' " +
           "AND fr.reportStatus IN ('APPROVED', 'PUBLISHED') " +
           "AND fr.isDeleted = false " +
           "ORDER BY fr.fiscalYear DESC, fr.fiscalPeriod DESC " +
           "LIMIT 1")
    Optional<FinancialReport> findLatestIncomeStatement(@Param("companyId") Long companyId);

    /**
     * 최신 현금흐름표 조회
     */
    @Query("SELECT fr FROM FinancialReport fr " +
           "JOIN FETCH fr.company c " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.reportType = 'CASH_FLOW_STATEMENT' " +
           "AND fr.reportStatus IN ('APPROVED', 'PUBLISHED') " +
           "AND fr.isDeleted = false " +
           "ORDER BY fr.fiscalYear DESC, fr.fiscalPeriod DESC " +
           "LIMIT 1")
    Optional<FinancialReport> findLatestCashFlowStatement(@Param("companyId") Long companyId);

    /**
     * 보고서 유형별 통계
     */
    @Query("SELECT fr.reportType, COUNT(fr) FROM FinancialReport fr " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.fiscalYear = :fiscalYear " +
           "AND fr.isDeleted = false " +
           "GROUP BY fr.reportType")
    List<Object[]> getReportCountByType(@Param("companyId") Long companyId,
                                       @Param("fiscalYear") Integer fiscalYear);

    /**
     * 보고서 상태별 통계
     */
    @Query("SELECT fr.reportStatus, COUNT(fr) FROM FinancialReport fr " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.fiscalYear = :fiscalYear " +
           "AND fr.isDeleted = false " +
           "GROUP BY fr.reportStatus")
    List<Object[]> getReportCountByStatus(@Param("companyId") Long companyId,
                                         @Param("fiscalYear") Integer fiscalYear);

    /**
     * 월별 보고서 생성 통계
     */
    @Query("SELECT MONTH(fr.generatedAt), COUNT(fr) FROM FinancialReport fr " +
           "WHERE fr.company.id = :companyId " +
           "AND YEAR(fr.generatedAt) = :year " +
           "AND fr.generatedAt IS NOT NULL " +
           "AND fr.isDeleted = false " +
           "GROUP BY MONTH(fr.generatedAt) " +
           "ORDER BY MONTH(fr.generatedAt)")
    List<Object[]> getMonthlyReportGenerationStats(@Param("companyId") Long companyId,
                                                   @Param("year") Integer year);

    /**
     * 생성자별 보고서 통계
     */
    @Query("SELECT gb.name, COUNT(fr) FROM FinancialReport fr " +
           "JOIN fr.generatedBy gb " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.fiscalYear = :fiscalYear " +
           "AND fr.isDeleted = false " +
           "GROUP BY gb.id, gb.name " +
           "ORDER BY COUNT(fr) DESC")
    List<Object[]> getReportCountByGenerator(@Param("companyId") Long companyId,
                                            @Param("fiscalYear") Integer fiscalYear);

    /**
     * 재무 지표 히스토리 조회 (최근 12개월)
     */
    @Query("SELECT fr.fiscalYear, fr.fiscalPeriod, fr.totalAssets, fr.totalLiabilities, " +
           "fr.totalEquity, fr.totalRevenue, fr.totalExpenses, fr.netIncome " +
           "FROM FinancialReport fr " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.reportType = 'BALANCE_SHEET' " +
           "AND fr.reportStatus IN ('APPROVED', 'PUBLISHED') " +
           "AND fr.isDeleted = false " +
           "ORDER BY fr.fiscalYear DESC, fr.fiscalPeriod DESC " +
           "LIMIT 12")
    List<Object[]> getFinancialTrendsData(@Param("companyId") Long companyId);

    /**
     * 손익 트렌드 데이터 조회
     */
    @Query("SELECT fr.fiscalYear, fr.fiscalPeriod, fr.totalRevenue, fr.totalExpenses, " +
           "fr.netIncome, fr.operatingIncome " +
           "FROM FinancialReport fr " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.reportType = 'INCOME_STATEMENT' " +
           "AND fr.reportStatus IN ('APPROVED', 'PUBLISHED') " +
           "AND fr.isDeleted = false " +
           "ORDER BY fr.fiscalYear DESC, fr.fiscalPeriod DESC " +
           "LIMIT 12")
    List<Object[]> getIncomeTrendsData(@Param("companyId") Long companyId);

    /**
     * 재무비율 트렌드 데이터 조회
     */
    @Query("SELECT fr.fiscalYear, fr.fiscalPeriod, " +
           "CASE WHEN fr.currentLiabilities > 0 THEN fr.currentAssets / fr.currentLiabilities ELSE 0 END, " +
           "CASE WHEN fr.totalAssets > 0 THEN fr.totalLiabilities / fr.totalAssets * 100 ELSE 0 END, " +
           "CASE WHEN fr.totalAssets > 0 THEN fr.totalEquity / fr.totalAssets * 100 ELSE 0 END, " +
           "CASE WHEN fr.totalAssets > 0 THEN fr.netIncome / fr.totalAssets * 100 ELSE 0 END, " +
           "CASE WHEN fr.totalEquity > 0 THEN fr.netIncome / fr.totalEquity * 100 ELSE 0 END " +
           "FROM FinancialReport fr " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.reportType = 'BALANCE_SHEET' " +
           "AND fr.reportStatus IN ('APPROVED', 'PUBLISHED') " +
           "AND fr.isDeleted = false " +
           "ORDER BY fr.fiscalYear DESC, fr.fiscalPeriod DESC " +
           "LIMIT 12")
    List<Object[]> getFinancialRatioTrendsData(@Param("companyId") Long companyId);

    /**
     * 보고서 생성 빈도 조회
     */
    @Query("SELECT COUNT(fr) FROM FinancialReport fr " +
           "WHERE fr.company.id = :companyId " +
           "AND fr.generatedAt BETWEEN :startDate AND :endDate " +
           "AND fr.isDeleted = false")
    Long countReportsByGeneratedAtBetween(@Param("companyId") Long companyId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}




