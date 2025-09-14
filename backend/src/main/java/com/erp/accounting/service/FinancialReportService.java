package com.erp.accounting.service;

import com.erp.accounting.dto.FinancialReportDto;
import com.erp.accounting.entity.FinancialReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 재무보고서 서비스 인터페이스
 * 재무제표 생성 및 관리 기능을 정의합니다
 */
public interface FinancialReportService {

    /**
     * 재무상태표 생성
     */
    FinancialReportDto generateBalanceSheet(Long companyId, Integer fiscalYear, String fiscalPeriod, LocalDate baseDate);

    /**
     * 손익계산서 생성
     */
    FinancialReportDto generateIncomeStatement(Long companyId, Integer fiscalYear, String fiscalPeriod, 
                                             LocalDate startDate, LocalDate endDate);

    /**
     * 현금흐름표 생성
     */
    FinancialReportDto generateCashFlowStatement(Long companyId, Integer fiscalYear, String fiscalPeriod, 
                                               LocalDate startDate, LocalDate endDate);

    /**
     * 자본변동표 생성
     */
    FinancialReportDto generateEquityStatement(Long companyId, Integer fiscalYear, String fiscalPeriod, 
                                             LocalDate startDate, LocalDate endDate);

    /**
     * 시산표 생성
     */
    FinancialReportDto generateTrialBalance(Long companyId, Integer fiscalYear, String fiscalPeriod, 
                                          LocalDate startDate, LocalDate endDate);

    /**
     * 예산보고서 생성
     */
    FinancialReportDto generateBudgetReport(Long companyId, Integer fiscalYear, String fiscalPeriod);

    /**
     * 차이분석표 생성
     */
    FinancialReportDto generateVarianceAnalysis(Long companyId, Integer fiscalYear, String fiscalPeriod);

    /**
     * 보고서 승인
     */
    FinancialReportDto approveReport(Long reportId, Long approverId);

    /**
     * 보고서 재생성
     */
    FinancialReportDto regenerateReport(Long reportId);

    /**
     * 보고서 삭제
     */
    void deleteReport(Long reportId);

    /**
     * 회사별 보고서 목록 조회
     */
    List<FinancialReportDto> getReportsByCompany(Long companyId);

    /**
     * 보고서 유형별 조회
     */
    List<FinancialReportDto> getReportsByType(Long companyId, FinancialReport.ReportType reportType);

    /**
     * 최신 재무제표 조회
     */
    Map<String, FinancialReportDto> getLatestFinancialStatements(Long companyId);

    /**
     * 재무 트렌드 데이터 조회
     */
    Map<String, Object> getFinancialTrends(Long companyId, Integer periods);

    /**
     * 재무비율 분석
     */
    Map<String, Object> getFinancialRatioAnalysis(Long companyId, Integer fiscalYear);

    /**
     * 보고서 검색
     */
    Page<FinancialReportDto> searchReports(String searchTerm, Pageable pageable);

    /**
     * 회사별 보고서 검색
     */
    Page<FinancialReportDto> searchReportsByCompany(Long companyId, String searchTerm, Pageable pageable);

    /**
     * 보고서 통계
     */
    Map<String, Object> getReportStatistics(Long companyId, Integer fiscalYear);
}




