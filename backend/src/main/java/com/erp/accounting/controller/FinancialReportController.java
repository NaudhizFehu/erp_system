package com.erp.accounting.controller;

import com.erp.accounting.dto.FinancialReportDto;
import com.erp.accounting.entity.FinancialReport;
import com.erp.accounting.service.FinancialReportService;
import com.erp.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 재무보고서 관리 컨트롤러
 * 재무제표 생성 및 관리 기능을 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/accounting/reports")
@RequiredArgsConstructor
public class FinancialReportController {

    private final FinancialReportService financialReportService;

    /**
     * 재무상태표 생성
     */
    @PostMapping("/balance-sheet")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<FinancialReportDto>> generateBalanceSheet(
            @RequestParam Long companyId,
            @RequestParam Integer fiscalYear,
            @RequestParam String fiscalPeriod,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baseDate) {
        try {
            log.info("재무상태표 생성 요청 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);
            
            FinancialReportDto result = financialReportService.generateBalanceSheet(
                companyId, fiscalYear, fiscalPeriod, baseDate);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재무상태표가 성공적으로 생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재무상태표 생성 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재무상태표 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 손익계산서 생성
     */
    @PostMapping("/income-statement")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<FinancialReportDto>> generateIncomeStatement(
            @RequestParam Long companyId,
            @RequestParam Integer fiscalYear,
            @RequestParam String fiscalPeriod,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("손익계산서 생성 요청 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);
            
            FinancialReportDto result = financialReportService.generateIncomeStatement(
                companyId, fiscalYear, fiscalPeriod, startDate, endDate);
            
            return ResponseEntity.ok(ApiResponse.success(
                "손익계산서가 성공적으로 생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("손익계산서 생성 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("손익계산서 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 현금흐름표 생성
     */
    @PostMapping("/cash-flow-statement")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<FinancialReportDto>> generateCashFlowStatement(
            @RequestParam Long companyId,
            @RequestParam Integer fiscalYear,
            @RequestParam String fiscalPeriod,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("현금흐름표 생성 요청 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);
            
            FinancialReportDto result = financialReportService.generateCashFlowStatement(
                companyId, fiscalYear, fiscalPeriod, startDate, endDate);
            
            return ResponseEntity.ok(ApiResponse.success(
                "현금흐름표가 성공적으로 생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("현금흐름표 생성 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("현금흐름표 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 자본변동표 생성
     */
    @PostMapping("/equity-statement")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<FinancialReportDto>> generateEquityStatement(
            @RequestParam Long companyId,
            @RequestParam Integer fiscalYear,
            @RequestParam String fiscalPeriod,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("자본변동표 생성 요청 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);
            
            FinancialReportDto result = financialReportService.generateEquityStatement(
                companyId, fiscalYear, fiscalPeriod, startDate, endDate);
            
            return ResponseEntity.ok(ApiResponse.success(
                "자본변동표가 성공적으로 생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("자본변동표 생성 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("자본변동표 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 시산표 생성
     */
    @PostMapping("/trial-balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<FinancialReportDto>> generateTrialBalance(
            @RequestParam Long companyId,
            @RequestParam Integer fiscalYear,
            @RequestParam String fiscalPeriod,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("시산표 생성 요청 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);
            
            FinancialReportDto result = financialReportService.generateTrialBalance(
                companyId, fiscalYear, fiscalPeriod, startDate, endDate);
            
            return ResponseEntity.ok(ApiResponse.success(
                "시산표가 성공적으로 생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("시산표 생성 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("시산표 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 예산보고서 생성
     */
    @PostMapping("/budget-report")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<FinancialReportDto>> generateBudgetReport(
            @RequestParam Long companyId,
            @RequestParam Integer fiscalYear,
            @RequestParam String fiscalPeriod) {
        try {
            log.info("예산보고서 생성 요청 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);
            
            FinancialReportDto result = financialReportService.generateBudgetReport(
                companyId, fiscalYear, fiscalPeriod);
            
            return ResponseEntity.ok(ApiResponse.success(
                "예산보고서가 성공적으로 생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("예산보고서 생성 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("예산보고서 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 차이분석표 생성
     */
    @PostMapping("/variance-analysis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<FinancialReportDto>> generateVarianceAnalysis(
            @RequestParam Long companyId,
            @RequestParam Integer fiscalYear,
            @RequestParam String fiscalPeriod) {
        try {
            log.info("차이분석표 생성 요청 - 회사: {}, 연도: {}, 기간: {}", companyId, fiscalYear, fiscalPeriod);
            
            FinancialReportDto result = financialReportService.generateVarianceAnalysis(
                companyId, fiscalYear, fiscalPeriod);
            
            return ResponseEntity.ok(ApiResponse.success(
                "차이분석표가 성공적으로 생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("차이분석표 생성 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("차이분석표 생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 보고서 승인
     */
    @PostMapping("/{reportId}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<FinancialReportDto>> approveReport(
            @PathVariable Long reportId,
            @RequestParam Long approverId) {
        try {
            log.info("보고서 승인 요청 - 보고서 ID: {}, 승인자: {}", reportId, approverId);
            
            FinancialReportDto result = financialReportService.approveReport(reportId, approverId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "보고서가 성공적으로 승인되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("보고서 승인 실패 - 보고서 ID: {}", reportId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("보고서 승인에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 보고서 재생성
     */
    @PostMapping("/{reportId}/regenerate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<FinancialReportDto>> regenerateReport(@PathVariable Long reportId) {
        try {
            log.info("보고서 재생성 요청 - 보고서 ID: {}", reportId);
            
            FinancialReportDto result = financialReportService.regenerateReport(reportId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "보고서가 성공적으로 재생성되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("보고서 재생성 실패 - 보고서 ID: {}", reportId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("보고서 재생성에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 보고서 삭제
     */
    @DeleteMapping("/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteReport(@PathVariable Long reportId) {
        try {
            log.info("보고서 삭제 요청 - 보고서 ID: {}", reportId);
            
            financialReportService.deleteReport(reportId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "보고서가 성공적으로 삭제되었습니다"
            ));
        } catch (Exception e) {
            log.error("보고서 삭제 실패 - 보고서 ID: {}", reportId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("보고서 삭제에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 회사별 보고서 목록 조회
     */
    @GetMapping("/companies/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<FinancialReportDto>>> getReportsByCompany(@PathVariable Long companyId) {
        try {
            log.info("회사별 보고서 목록 조회 요청 - 회사: {}", companyId);
            
            List<FinancialReportDto> result = financialReportService.getReportsByCompany(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "회사별 보고서 목록을 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("회사별 보고서 목록 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("회사별 보고서 목록 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 보고서 유형별 조회
     */
    @GetMapping("/companies/{companyId}/type/{reportType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<List<FinancialReportDto>>> getReportsByType(
            @PathVariable Long companyId,
            @PathVariable FinancialReport.ReportType reportType) {
        try {
            log.info("보고서 유형별 조회 요청 - 회사: {}, 유형: {}", companyId, reportType);
            
            List<FinancialReportDto> result = financialReportService.getReportsByType(companyId, reportType);
            
            return ResponseEntity.ok(ApiResponse.success(
                "보고서 유형별 목록을 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("보고서 유형별 조회 실패 - 회사: {}, 유형: {}", companyId, reportType, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("보고서 유형별 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 최신 재무제표 조회
     */
    @GetMapping("/companies/{companyId}/latest")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, FinancialReportDto>>> getLatestFinancialStatements(@PathVariable Long companyId) {
        try {
            log.info("최신 재무제표 조회 요청 - 회사: {}", companyId);
            
            Map<String, FinancialReportDto> result = financialReportService.getLatestFinancialStatements(companyId);
            
            return ResponseEntity.ok(ApiResponse.success(
                "최신 재무제표를 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("최신 재무제표 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("최신 재무제표 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재무 트렌드 데이터 조회
     */
    @GetMapping("/companies/{companyId}/trends")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFinancialTrends(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "12") Integer periods) {
        try {
            log.info("재무 트렌드 데이터 조회 요청 - 회사: {}, 기간: {}", companyId, periods);
            
            Map<String, Object> result = financialReportService.getFinancialTrends(companyId, periods);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재무 트렌드 데이터를 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재무 트렌드 데이터 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재무 트렌드 데이터 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 재무비율 분석
     */
    @GetMapping("/companies/{companyId}/ratios")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFinancialRatioAnalysis(
            @PathVariable Long companyId,
            @RequestParam Integer fiscalYear) {
        try {
            log.info("재무비율 분석 요청 - 회사: {}, 연도: {}", companyId, fiscalYear);
            
            Map<String, Object> result = financialReportService.getFinancialRatioAnalysis(companyId, fiscalYear);
            
            return ResponseEntity.ok(ApiResponse.success(
                "재무비율 분석을 성공적으로 완료했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("재무비율 분석 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("재무비율 분석에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 보고서 검색
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<FinancialReportDto>>> searchReports(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("보고서 검색 요청 - 검색어: {}", searchTerm);
            
            Page<FinancialReportDto> result = financialReportService.searchReports(searchTerm, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(
                "보고서 검색이 완료되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("보고서 검색 실패 - 검색어: {}", searchTerm, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("보고서 검색에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 회사별 보고서 검색
     */
    @GetMapping("/companies/{companyId}/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<FinancialReportDto>>> searchReportsByCompany(
            @PathVariable Long companyId,
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("회사별 보고서 검색 요청 - 회사: {}, 검색어: {}", companyId, searchTerm);
            
            Page<FinancialReportDto> result = financialReportService.searchReportsByCompany(companyId, searchTerm, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(
                "회사별 보고서 검색이 완료되었습니다",
                result
            ));
        } catch (Exception e) {
            log.error("회사별 보고서 검색 실패 - 회사: {}, 검색어: {}", companyId, searchTerm, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("회사별 보고서 검색에 실패했습니다: " + e.getMessage())
            );
        }
    }

    /**
     * 보고서 통계
     */
    @GetMapping("/companies/{companyId}/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReportStatistics(
            @PathVariable Long companyId,
            @RequestParam Integer fiscalYear) {
        try {
            log.info("보고서 통계 요청 - 회사: {}, 연도: {}", companyId, fiscalYear);
            
            Map<String, Object> result = financialReportService.getReportStatistics(companyId, fiscalYear);
            
            return ResponseEntity.ok(ApiResponse.success(
                "보고서 통계를 성공적으로 조회했습니다",
                result
            ));
        } catch (Exception e) {
            log.error("보고서 통계 조회 실패 - 회사: {}", companyId, e);
            return ResponseEntity.badRequest().body(
                ApiResponse.error("보고서 통계 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }
}
