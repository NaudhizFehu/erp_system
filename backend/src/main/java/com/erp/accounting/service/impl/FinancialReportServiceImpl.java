package com.erp.accounting.service.impl;

import com.erp.accounting.dto.FinancialReportDto;
import com.erp.accounting.dto.FinancialReportItemDto;
import com.erp.accounting.entity.Account;
import com.erp.accounting.entity.FinancialReport;
import com.erp.accounting.entity.FinancialReportItem;
import com.erp.accounting.repository.AccountRepository;
import com.erp.accounting.repository.FinancialReportRepository;
import com.erp.accounting.repository.TransactionRepository;
import com.erp.accounting.service.AccountingService;
import com.erp.accounting.service.FinancialReportService;
import com.erp.common.entity.Company;
import com.erp.common.repository.CompanyRepository;
import com.erp.common.utils.ExceptionUtils;
import com.erp.hr.entity.Employee;
import com.erp.hr.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 재무보고서 서비스 구현체
 * 재무제표 생성 및 관리 기능을 구현합니다
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialReportServiceImpl implements FinancialReportService {

    private final FinancialReportRepository reportRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final AccountingService accountingService;

    /**
     * 재무상태표 생성
     */
    @Override
    @Transactional
    public FinancialReportDto generateBalanceSheet(Long companyId, Integer fiscalYear, String fiscalPeriod, LocalDate baseDate) {
        log.info("재무상태표 생성 시작 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);

        Company company = getCompanyById(companyId);
        
        // 기존 보고서 확인
        Optional<FinancialReport> existingReport = reportRepository.findByCompanyIdAndReportTypeAndFiscalYearAndFiscalPeriod(
            companyId, FinancialReport.ReportType.BALANCE_SHEET, fiscalYear, fiscalPeriod);

        FinancialReport report;
        if (existingReport.isPresent()) {
            report = existingReport.get();
            log.info("기존 재무상태표 업데이트 - ID: {}", report.getId());
        } else {
            report = new FinancialReport();
            report.setCompany(company);
            report.setReportType(FinancialReport.ReportType.BALANCE_SHEET);
            report.setFiscalYear(fiscalYear);
            report.setFiscalPeriod(fiscalPeriod);
            report.setBaseDate(baseDate);
            report.setReportStatus(FinancialReport.ReportStatus.DRAFT);
        }

        // 계정과목별 잔액 조회
        List<Account> accounts = accountRepository.findTrackingBalanceAccountsByCompanyId(companyId);
        
        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal currentAssets = BigDecimal.ZERO;
        BigDecimal nonCurrentAssets = BigDecimal.ZERO;
        BigDecimal totalLiabilities = BigDecimal.ZERO;
        BigDecimal currentLiabilities = BigDecimal.ZERO;
        BigDecimal nonCurrentLiabilities = BigDecimal.ZERO;
        BigDecimal totalEquity = BigDecimal.ZERO;
        BigDecimal cashAndEquivalents = BigDecimal.ZERO;

        // 계정과목별 잔액 계산
        for (Account account : accounts) {
            BigDecimal balance = accountingService.getAccountBalance(account.getId(), baseDate);
            
            switch (account.getAccountType()) {
                case ASSET:
                    totalAssets = totalAssets.add(balance);
                    if (account.getAccountCategory() == Account.AccountCategory.CURRENT_ASSET) {
                        currentAssets = currentAssets.add(balance);
                        // 현금성 자산 계산 (계정과목명 기준)
                        if (account.getAccountName().contains("현금") || account.getAccountName().contains("예금")) {
                            cashAndEquivalents = cashAndEquivalents.add(balance);
                        }
                    } else {
                        nonCurrentAssets = nonCurrentAssets.add(balance);
                    }
                    break;
                case LIABILITY:
                    totalLiabilities = totalLiabilities.add(balance);
                    if (account.getAccountCategory() == Account.AccountCategory.CURRENT_LIABILITY) {
                        currentLiabilities = currentLiabilities.add(balance);
                    } else {
                        nonCurrentLiabilities = nonCurrentLiabilities.add(balance);
                    }
                    break;
                case EQUITY:
                    totalEquity = totalEquity.add(balance);
                    break;
            }
        }

        // 재무상태표 데이터 설정
        report.setTotalAssets(totalAssets);
        report.setCurrentAssets(currentAssets);
        report.setNonCurrentAssets(nonCurrentAssets);
        report.setTotalLiabilities(totalLiabilities);
        report.setCurrentLiabilities(currentLiabilities);
        report.setNonCurrentLiabilities(nonCurrentLiabilities);
        report.setTotalEquity(totalEquity);
        report.setCashAndEquivalents(cashAndEquivalents);

        // 보고서 완료 처리
        report.completeGeneration(null); // 실제로는 현재 사용자 정보 필요

        FinancialReport savedReport = reportRepository.save(report);
        
        log.info("재무상태표 생성 완료 - ID: {}", savedReport.getId());
        return FinancialReportDto.from(savedReport);
    }

    /**
     * 손익계산서 생성
     */
    @Override
    @Transactional
    public FinancialReportDto generateIncomeStatement(Long companyId, Integer fiscalYear, String fiscalPeriod, 
                                                     LocalDate startDate, LocalDate endDate) {
        log.info("손익계산서 생성 시작 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);

        Company company = getCompanyById(companyId);
        
        // 기존 보고서 확인
        Optional<FinancialReport> existingReport = reportRepository.findByCompanyIdAndReportTypeAndFiscalYearAndFiscalPeriod(
            companyId, FinancialReport.ReportType.INCOME_STATEMENT, fiscalYear, fiscalPeriod);

        FinancialReport report;
        if (existingReport.isPresent()) {
            report = existingReport.get();
        } else {
            report = new FinancialReport();
            report.setCompany(company);
            report.setReportType(FinancialReport.ReportType.INCOME_STATEMENT);
            report.setFiscalYear(fiscalYear);
            report.setFiscalPeriod(fiscalPeriod);
            report.setBaseDate(endDate);
            report.setReportStatus(FinancialReport.ReportStatus.DRAFT);
        }

        // 수익/비용 계정 조회
        List<Account> revenueAccounts = accountRepository.findByAccountType(Account.AccountType.REVENUE)
            .stream().filter(a -> a.getCompany().getId().equals(companyId)).collect(Collectors.toList());
        List<Account> expenseAccounts = accountRepository.findByAccountType(Account.AccountType.EXPENSE)
            .stream().filter(a -> a.getCompany().getId().equals(companyId)).collect(Collectors.toList());

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal operatingRevenue = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        BigDecimal operatingExpenses = BigDecimal.ZERO;

        // 수익 계산
        for (Account account : revenueAccounts) {
            BigDecimal amount = calculatePeriodAmount(account.getId(), startDate, endDate);
            totalRevenue = totalRevenue.add(amount);
            
            if (account.getAccountCategory() == Account.AccountCategory.OPERATING_REVENUE) {
                operatingRevenue = operatingRevenue.add(amount);
            }
        }

        // 비용 계산
        for (Account account : expenseAccounts) {
            BigDecimal amount = calculatePeriodAmount(account.getId(), startDate, endDate);
            totalExpenses = totalExpenses.add(amount);
            
            if (account.getAccountCategory() == Account.AccountCategory.OPERATING_EXPENSE) {
                operatingExpenses = operatingExpenses.add(amount);
            }
        }

        // 손익 계산
        BigDecimal operatingIncome = operatingRevenue.subtract(operatingExpenses);
        BigDecimal netIncome = totalRevenue.subtract(totalExpenses);
        BigDecimal incomeBeforeTax = netIncome; // 세금 계산 로직 추가 필요

        // 손익계산서 데이터 설정
        report.setTotalRevenue(totalRevenue);
        report.setTotalExpenses(totalExpenses);
        report.setOperatingIncome(operatingIncome);
        report.setNetIncome(netIncome);
        report.setIncomeBeforeTax(incomeBeforeTax);

        // 보고서 완료 처리
        report.completeGeneration(null);

        FinancialReport savedReport = reportRepository.save(report);
        
        log.info("손익계산서 생성 완료 - ID: {}", savedReport.getId());
        return FinancialReportDto.from(savedReport);
    }

    /**
     * 현금흐름표 생성
     */
    @Override
    @Transactional
    public FinancialReportDto generateCashFlowStatement(Long companyId, Integer fiscalYear, String fiscalPeriod, 
                                                       LocalDate startDate, LocalDate endDate) {
        log.info("현금흐름표 생성 시작 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);

        Company company = getCompanyById(companyId);
        
        FinancialReport report = new FinancialReport();
        report.setCompany(company);
        report.setReportType(FinancialReport.ReportType.CASH_FLOW_STATEMENT);
        report.setFiscalYear(fiscalYear);
        report.setFiscalPeriod(fiscalPeriod);
        report.setBaseDate(endDate);
        report.setReportStatus(FinancialReport.ReportStatus.DRAFT);

        // 현금흐름 계산 로직 (간소화)
        // 실제로는 직접법 또는 간접법으로 현금흐름을 계산해야 함
        
        // 보고서 완료 처리
        report.completeGeneration(null);

        FinancialReport savedReport = reportRepository.save(report);
        
        log.info("현금흐름표 생성 완료 - ID: {}", savedReport.getId());
        return FinancialReportDto.from(savedReport);
    }

    /**
     * 자본변동표 생성
     */
    @Override
    @Transactional
    public FinancialReportDto generateEquityStatement(Long companyId, Integer fiscalYear, String fiscalPeriod, 
                                                     LocalDate startDate, LocalDate endDate) {
        log.info("자본변동표 생성 시작 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);

        Company company = getCompanyById(companyId);
        
        FinancialReport report = new FinancialReport();
        report.setCompany(company);
        report.setReportType(FinancialReport.ReportType.EQUITY_STATEMENT);
        report.setFiscalYear(fiscalYear);
        report.setFiscalPeriod(fiscalPeriod);
        report.setBaseDate(endDate);
        report.setReportStatus(FinancialReport.ReportStatus.DRAFT);

        // 자본 변동 계산 로직
        
        // 보고서 완료 처리
        report.completeGeneration(null);

        FinancialReport savedReport = reportRepository.save(report);
        
        log.info("자본변동표 생성 완료 - ID: {}", savedReport.getId());
        return FinancialReportDto.from(savedReport);
    }

    /**
     * 시산표 생성
     */
    @Override
    @Transactional
    public FinancialReportDto generateTrialBalance(Long companyId, Integer fiscalYear, String fiscalPeriod, 
                                                  LocalDate startDate, LocalDate endDate) {
        log.info("시산표 생성 시작 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);

        Company company = getCompanyById(companyId);
        
        FinancialReport report = new FinancialReport();
        report.setCompany(company);
        report.setReportType(FinancialReport.ReportType.TRIAL_BALANCE);
        report.setFiscalYear(fiscalYear);
        report.setFiscalPeriod(fiscalPeriod);
        report.setBaseDate(endDate);
        report.setReportStatus(FinancialReport.ReportStatus.DRAFT);

        // 시산표 데이터 생성
        var trialBalanceData = accountingService.generateTrialBalance(companyId, startDate, endDate);
        
        // JSON 형태로 데이터 저장 (실제로는 Jackson 등을 사용)
        report.setReportData(trialBalanceData.toString());

        // 보고서 완료 처리
        report.completeGeneration(null);

        FinancialReport savedReport = reportRepository.save(report);
        
        log.info("시산표 생성 완료 - ID: {}", savedReport.getId());
        return FinancialReportDto.from(savedReport);
    }

    /**
     * 예산보고서 생성
     */
    @Override
    @Transactional
    public FinancialReportDto generateBudgetReport(Long companyId, Integer fiscalYear, String fiscalPeriod) {
        log.info("예산보고서 생성 시작 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);

        Company company = getCompanyById(companyId);
        
        FinancialReport report = new FinancialReport();
        report.setCompany(company);
        report.setReportType(FinancialReport.ReportType.BUDGET_REPORT);
        report.setFiscalYear(fiscalYear);
        report.setFiscalPeriod(fiscalPeriod);
        report.setBaseDate(LocalDate.now());
        report.setReportStatus(FinancialReport.ReportStatus.DRAFT);

        // 예산 vs 실적 분석 로직
        
        // 보고서 완료 처리
        report.completeGeneration(null);

        FinancialReport savedReport = reportRepository.save(report);
        
        log.info("예산보고서 생성 완료 - ID: {}", savedReport.getId());
        return FinancialReportDto.from(savedReport);
    }

    /**
     * 차이분석표 생성
     */
    @Override
    @Transactional
    public FinancialReportDto generateVarianceAnalysis(Long companyId, Integer fiscalYear, String fiscalPeriod) {
        log.info("차이분석표 생성 시작 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);

        Company company = getCompanyById(companyId);
        
        FinancialReport report = new FinancialReport();
        report.setCompany(company);
        report.setReportType(FinancialReport.ReportType.VARIANCE_ANALYSIS);
        report.setFiscalYear(fiscalYear);
        report.setFiscalPeriod(fiscalPeriod);
        report.setBaseDate(LocalDate.now());
        report.setReportStatus(FinancialReport.ReportStatus.DRAFT);

        // 차이분석 로직
        
        // 보고서 완료 처리
        report.completeGeneration(null);

        FinancialReport savedReport = reportRepository.save(report);
        
        log.info("차이분석표 생성 완료 - ID: {}", savedReport.getId());
        return FinancialReportDto.from(savedReport);
    }

    /**
     * 보고서 승인
     */
    @Override
    @Transactional
    public FinancialReportDto approveReport(Long reportId, Long approverId) {
        log.info("보고서 승인 시작 - 보고서 ID: {}, 승인자: {}", reportId, approverId);

        FinancialReport report = reportRepository.findById(reportId)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("보고서를 찾을 수 없습니다"));

        Employee approver = employeeRepository.findById(approverId)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("승인자를 찾을 수 없습니다"));

        report.approve(approver);
        FinancialReport savedReport = reportRepository.save(report);
        
        log.info("보고서 승인 완료 - ID: {}", savedReport.getId());
        return FinancialReportDto.from(savedReport);
    }

    /**
     * 보고서 재생성
     */
    @Override
    @Transactional
    public FinancialReportDto regenerateReport(Long reportId) {
        log.info("보고서 재생성 시작 - ID: {}", reportId);

        FinancialReport report = reportRepository.findById(reportId)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("보고서를 찾을 수 없습니다"));

        // 보고서 유형에 따른 재생성
        return switch (report.getReportType()) {
            case BALANCE_SHEET -> generateBalanceSheet(
                report.getCompany().getId(), 
                report.getFiscalYear(), 
                report.getFiscalPeriod(), 
                report.getBaseDate()
            );
            case INCOME_STATEMENT -> generateIncomeStatement(
                report.getCompany().getId(), 
                report.getFiscalYear(), 
                report.getFiscalPeriod(), 
                LocalDate.of(report.getFiscalYear(), 1, 1),
                report.getBaseDate()
            );
            default -> throw ExceptionUtils.businessException("지원하지 않는 보고서 유형입니다");
        };
    }

    /**
     * 보고서 삭제
     */
    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        log.info("보고서 삭제 시작 - ID: {}", reportId);

        FinancialReport report = reportRepository.findById(reportId)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("보고서를 찾을 수 없습니다"));

        if (report.getReportStatus() == FinancialReport.ReportStatus.PUBLISHED) {
            throw ExceptionUtils.businessException("공시된 보고서는 삭제할 수 없습니다");
        }

        report.softDelete(null);
        reportRepository.save(report);
        
        log.info("보고서 삭제 완료 - ID: {}", reportId);
    }

    /**
     * 회사별 보고서 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialReportDto> getReportsByCompany(Long companyId) {
        List<FinancialReport> reports = reportRepository.findByCompanyId(companyId);
        return reports.stream()
            .map(FinancialReportDto::from)
            .collect(Collectors.toList());
    }

    /**
     * 보고서 유형별 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialReportDto> getReportsByType(Long companyId, FinancialReport.ReportType reportType) {
        List<FinancialReport> reports = reportRepository.findByCompanyIdAndReportType(companyId, reportType);
        return reports.stream()
            .map(FinancialReportDto::from)
            .collect(Collectors.toList());
    }

    /**
     * 최신 재무제표 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, FinancialReportDto> getLatestFinancialStatements(Long companyId) {
        Map<String, FinancialReportDto> result = new HashMap<>();

        reportRepository.findLatestBalanceSheet(companyId)
            .ifPresent(report -> result.put("balanceSheet", FinancialReportDto.from(report)));

        reportRepository.findLatestIncomeStatement(companyId)
            .ifPresent(report -> result.put("incomeStatement", FinancialReportDto.from(report)));

        reportRepository.findLatestCashFlowStatement(companyId)
            .ifPresent(report -> result.put("cashFlowStatement", FinancialReportDto.from(report)));

        return result;
    }

    /**
     * 재무 트렌드 데이터 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getFinancialTrends(Long companyId, Integer periods) {
        List<Object[]> trendsData = reportRepository.getFinancialTrendsData(companyId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("periods", periods);
        result.put("data", trendsData);
        
        return result;
    }

    /**
     * 재무비율 분석
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getFinancialRatioAnalysis(Long companyId, Integer fiscalYear) {
        // 최신 재무상태표와 손익계산서 조회
        Optional<FinancialReport> balanceSheet = reportRepository.findLatestBalanceSheet(companyId);
        Optional<FinancialReport> incomeStatement = reportRepository.findLatestIncomeStatement(companyId);

        Map<String, Object> result = new HashMap<>();

        if (balanceSheet.isPresent()) {
            FinancialReport bs = balanceSheet.get();
            result.put("currentRatio", bs.getCurrentRatio());
            result.put("debtRatio", bs.getDebtRatio());
            result.put("equityRatio", bs.getEquityRatio());
            result.put("roa", bs.getROA());
            result.put("roe", bs.getROE());
        }

        if (incomeStatement.isPresent()) {
            FinancialReport is = incomeStatement.get();
            result.put("grossMargin", is.getGrossMargin());
            result.put("netMargin", is.getNetMargin());
        }

        return result;
    }

    /**
     * 보고서 검색
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FinancialReportDto> searchReports(String searchTerm, Pageable pageable) {
        Page<FinancialReport> reports = reportRepository.searchReports(searchTerm, pageable);
        List<FinancialReportDto> reportDtos = reports.getContent().stream()
            .map(FinancialReportDto::from)
            .collect(Collectors.toList());
        
        return new PageImpl<>(reportDtos, pageable, reports.getTotalElements());
    }

    /**
     * 회사별 보고서 검색
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FinancialReportDto> searchReportsByCompany(Long companyId, String searchTerm, Pageable pageable) {
        Page<FinancialReport> reports = reportRepository.searchReportsByCompany(companyId, searchTerm, pageable);
        List<FinancialReportDto> reportDtos = reports.getContent().stream()
            .map(FinancialReportDto::from)
            .collect(Collectors.toList());
        
        return new PageImpl<>(reportDtos, pageable, reports.getTotalElements());
    }

    /**
     * 보고서 통계
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getReportStatistics(Long companyId, Integer fiscalYear) {
        Map<String, Object> result = new HashMap<>();

        List<Object[]> countByType = reportRepository.getReportCountByType(companyId, fiscalYear);
        List<Object[]> countByStatus = reportRepository.getReportCountByStatus(companyId, fiscalYear);

        result.put("countByType", countByType);
        result.put("countByStatus", countByStatus);

        return result;
    }

    // 유틸리티 메소드들

    private Company getCompanyById(Long companyId) {
        return companyRepository.findById(companyId)
            .orElseThrow(() -> ExceptionUtils.entityNotFoundException("회사를 찾을 수 없습니다"));
    }

    private BigDecimal calculatePeriodAmount(Long accountId, LocalDate startDate, LocalDate endDate) {
        BigDecimal debitSum = transactionRepository.sumDebitAmountByAccountIdAndDateBetween(accountId, startDate, endDate);
        BigDecimal creditSum = transactionRepository.sumCreditAmountByAccountIdAndDateBetween(accountId, startDate, endDate);
        
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) return BigDecimal.ZERO;

        // 계정 유형에 따른 금액 계산
        return switch (account.getAccountType()) {
            case REVENUE -> creditSum.subtract(debitSum); // 수익은 대변이 증가
            case EXPENSE -> debitSum.subtract(creditSum);  // 비용은 차변이 증가
            default -> debitSum.subtract(creditSum);
        };
    }
}
