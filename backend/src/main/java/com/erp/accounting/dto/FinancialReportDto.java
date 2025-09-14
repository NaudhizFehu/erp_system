package com.erp.accounting.dto;

import com.erp.common.dto.CompanyDto;
import com.erp.hr.dto.EmployeeDto;
import com.erp.accounting.entity.FinancialReport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

/**
 * 재무보고서 정보 응답 DTO
 * 재무보고서 정보 조회 시 사용됩니다
 */
public record FinancialReportDto(
        Long id,
        CompanyDto company,
        FinancialReport.ReportType reportType,
        String reportTitle,
        Integer fiscalYear,
        String fiscalPeriod,
        LocalDate baseDate,
        FinancialReport.ReportStatus reportStatus,
        String reportData,
        String summaryData,
        BigDecimal totalAssets,
        BigDecimal totalLiabilities,
        BigDecimal totalEquity,
        BigDecimal totalRevenue,
        BigDecimal totalExpenses,
        BigDecimal netIncome,
        BigDecimal operatingIncome,
        BigDecimal incomeBeforeTax,
        BigDecimal cashAndEquivalents,
        BigDecimal currentAssets,
        BigDecimal nonCurrentAssets,
        BigDecimal currentLiabilities,
        BigDecimal nonCurrentLiabilities,
        EmployeeDto generatedBy,
        LocalDateTime generatedAt,
        EmployeeDto approvedBy,
        LocalDateTime approvedAt,
        String filePath,
        String remarks,
        List<FinancialReportItemDto> reportItems,
        // 재무비율
        BigDecimal currentRatio,
        BigDecimal debtRatio,
        BigDecimal equityRatio,
        BigDecimal roa,
        BigDecimal roe,
        BigDecimal grossMargin,
        BigDecimal netMargin,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public FinancialReportDto {
        if (company == null) {
            throw new IllegalArgumentException("소속 회사는 필수입니다");
        }
        if (reportType == null) {
            throw new IllegalArgumentException("보고서 유형은 필수입니다");
        }
        if (reportTitle == null || reportTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("보고서 제목은 필수입니다");
        }
        if (fiscalYear == null || fiscalYear < 2000 || fiscalYear > 2100) {
            throw new IllegalArgumentException("회계연도는 2000~2100년 사이여야 합니다");
        }
        if (fiscalPeriod == null || fiscalPeriod.trim().isEmpty()) {
            throw new IllegalArgumentException("회계기간은 필수입니다");
        }
        if (baseDate == null) {
            throw new IllegalArgumentException("기준일은 필수입니다");
        }
        if (reportStatus == null) {
            throw new IllegalArgumentException("보고서 상태는 필수입니다");
        }
    }
    
    /**
     * FinancialReport 엔티티로부터 FinancialReportDto 생성
     */
    public static FinancialReportDto from(FinancialReport report) {
        return new FinancialReportDto(
            report.getId(),
            report.getCompany() != null ? CompanyDto.from(report.getCompany()) : null,
            report.getReportType(),
            report.getReportTitle(),
            report.getFiscalYear(),
            report.getFiscalPeriod(),
            report.getBaseDate(),
            report.getReportStatus(),
            report.getReportData(),
            report.getSummaryData(),
            report.getTotalAssets(),
            report.getTotalLiabilities(),
            report.getTotalEquity(),
            report.getTotalRevenue(),
            report.getTotalExpenses(),
            report.getNetIncome(),
            report.getOperatingIncome(),
            report.getIncomeBeforeTax(),
            report.getCashAndEquivalents(),
            report.getCurrentAssets(),
            report.getNonCurrentAssets(),
            report.getCurrentLiabilities(),
            report.getNonCurrentLiabilities(),
            report.getGeneratedBy() != null ? EmployeeDto.from(report.getGeneratedBy()) : null,
            report.getGeneratedAt(),
            report.getApprovedBy() != null ? EmployeeDto.from(report.getApprovedBy()) : null,
            report.getApprovedAt(),
            report.getFilePath(),
            report.getRemarks(),
            report.getReportItems() != null ? 
                report.getReportItems().stream().map(FinancialReportItemDto::from).toList() : null,
            report.getCurrentRatio(),
            report.getDebtRatio(),
            report.getEquityRatio(),
            report.getROA(),
            report.getROE(),
            report.getGrossMargin(),
            report.getNetMargin(),
            report.getCreatedAt(),
            report.getUpdatedAt()
        );
    }
}




