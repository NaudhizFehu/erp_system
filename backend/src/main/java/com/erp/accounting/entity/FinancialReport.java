package com.erp.accounting.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import com.erp.hr.entity.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 재무보고서 엔티티
 * 재무제표 및 각종 회계 보고서를 관리합니다
 */
@Entity
@Table(name = "financial_reports", indexes = {
    @Index(name = "idx_financial_reports_company", columnList = "company_id"),
    @Index(name = "idx_financial_reports_type", columnList = "report_type"),
    @Index(name = "idx_financial_reports_period", columnList = "fiscal_year, fiscal_period"),
    @Index(name = "idx_financial_reports_status", columnList = "report_status"),
    @Index(name = "idx_financial_reports_generated_at", columnList = "generated_at")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_financial_reports_company_type_period", 
                     columnNames = {"company_id", "report_type", "fiscal_year", "fiscal_period"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReport extends BaseEntity {

    /**
     * 소속 회사
     */
    @NotNull(message = "소속 회사는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /**
     * 보고서 유형
     */
    @NotNull(message = "보고서 유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 30)
    private ReportType reportType;

    /**
     * 보고서 제목
     */
    @NotBlank(message = "보고서 제목은 필수입니다")
    @Size(max = 200, message = "보고서 제목은 200자 이하여야 합니다")
    @Column(name = "report_title", nullable = false, length = 200)
    private String reportTitle;

    /**
     * 회계연도
     */
    @NotNull(message = "회계연도는 필수입니다")
    @Min(value = 2000, message = "회계연도는 2000년 이상이어야 합니다")
    @Max(value = 2100, message = "회계연도는 2100년 이하여야 합니다")
    @Column(name = "fiscal_year", nullable = false)
    private Integer fiscalYear;

    /**
     * 회계기간 (분기, 월 등)
     */
    @NotBlank(message = "회계기간은 필수입니다")
    @Size(max = 20, message = "회계기간은 20자 이하여야 합니다")
    @Column(name = "fiscal_period", nullable = false, length = 20)
    private String fiscalPeriod;

    /**
     * 기준일 (보고서 작성 기준일)
     */
    @NotNull(message = "기준일은 필수입니다")
    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate;

    /**
     * 보고서 상태
     */
    @NotNull(message = "보고서 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false, length = 20)
    private ReportStatus reportStatus = ReportStatus.DRAFT;

    /**
     * 보고서 데이터 (JSON 형태)
     */
    @Lob
    @Column(name = "report_data", columnDefinition = "TEXT")
    private String reportData;

    /**
     * 요약 정보 (JSON 형태)
     */
    @Lob
    @Column(name = "summary_data", columnDefinition = "TEXT")
    private String summaryData;

    /**
     * 총 자산
     */
    @Column(name = "total_assets")
    private BigDecimal totalAssets = BigDecimal.ZERO;

    /**
     * 총 부채
     */
    @Column(name = "total_liabilities")
    private BigDecimal totalLiabilities = BigDecimal.ZERO;

    /**
     * 총 자본
     */
    @Column(name = "total_equity")
    private BigDecimal totalEquity = BigDecimal.ZERO;

    /**
     * 총 수익
     */
    @Column(name = "total_revenue")
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    /**
     * 총 비용
     */
    @Column(name = "total_expenses")
    private BigDecimal totalExpenses = BigDecimal.ZERO;

    /**
     * 순이익
     */
    @Column(name = "net_income")
    private BigDecimal netIncome = BigDecimal.ZERO;

    /**
     * 영업이익
     */
    @Column(name = "operating_income")
    private BigDecimal operatingIncome = BigDecimal.ZERO;

    /**
     * 세전이익
     */
    @Column(name = "income_before_tax")
    private BigDecimal incomeBeforeTax = BigDecimal.ZERO;

    /**
     * 현금및현금성자산
     */
    @Column(name = "cash_and_equivalents")
    private BigDecimal cashAndEquivalents = BigDecimal.ZERO;

    /**
     * 유동자산
     */
    @Column(name = "current_assets")
    private BigDecimal currentAssets = BigDecimal.ZERO;

    /**
     * 비유동자산
     */
    @Column(name = "non_current_assets")
    private BigDecimal nonCurrentAssets = BigDecimal.ZERO;

    /**
     * 유동부채
     */
    @Column(name = "current_liabilities")
    private BigDecimal currentLiabilities = BigDecimal.ZERO;

    /**
     * 비유동부채
     */
    @Column(name = "non_current_liabilities")
    private BigDecimal nonCurrentLiabilities = BigDecimal.ZERO;

    /**
     * 생성자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by")
    private Employee generatedBy;

    /**
     * 생성 시간
     */
    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    /**
     * 승인자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    /**
     * 승인 시간
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * 파일 경로 (PDF, Excel 등)
     */
    @Size(max = 500, message = "파일 경로는 500자 이하여야 합니다")
    @Column(name = "file_path", length = 500)
    private String filePath;

    /**
     * 비고
     */
    @Size(max = 1000, message = "비고는 1000자 이하여야 합니다")
    @Column(name = "remarks", length = 1000)
    private String remarks;

    /**
     * 보고서 항목들
     */
    @OneToMany(mappedBy = "report", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FinancialReportItem> reportItems;

    /**
     * 보고서 유형 열거형
     */
    public enum ReportType {
        BALANCE_SHEET("재무상태표"),
        INCOME_STATEMENT("손익계산서"),
        CASH_FLOW_STATEMENT("현금흐름표"),
        EQUITY_STATEMENT("자본변동표"),
        TRIAL_BALANCE("시산표"),
        GENERAL_LEDGER("총계정원장"),
        BUDGET_REPORT("예산보고서"),
        VARIANCE_ANALYSIS("차이분석표"),
        AGING_REPORT("연령분석표"),
        TAX_REPORT("세무보고서");

        private final String description;

        ReportType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 보고서 상태 열거형
     */
    public enum ReportStatus {
        DRAFT("임시저장"),
        GENERATED("생성완료"),
        REVIEWED("검토완료"),
        APPROVED("승인완료"),
        PUBLISHED("공시완료");

        private final String description;

        ReportStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 보고서 생성 완료
     */
    public void completeGeneration(Employee generator) {
        this.generatedBy = generator;
        this.generatedAt = LocalDateTime.now();
        this.reportStatus = ReportStatus.GENERATED;
        
        // 기본 재무 지표 계산
        calculateFinancialRatios();
    }

    /**
     * 보고서 승인
     */
    public void approve(Employee approver) {
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
        this.reportStatus = ReportStatus.APPROVED;
    }

    /**
     * 재무 지표 계산
     */
    public void calculateFinancialRatios() {
        // 순이익 계산
        this.netIncome = totalRevenue.subtract(totalExpenses);
        
        // 자산 = 부채 + 자본 (대차평형 확인)
        if (totalAssets.compareTo(totalLiabilities.add(totalEquity)) != 0) {
            // 차이가 있는 경우 로그 기록 또는 예외 처리
        }
        
        // 유동자산 + 비유동자산 = 총자산
        if (currentAssets.add(nonCurrentAssets).compareTo(totalAssets) != 0) {
            totalAssets = currentAssets.add(nonCurrentAssets);
        }
        
        // 유동부채 + 비유동부채 = 총부채
        if (currentLiabilities.add(nonCurrentLiabilities).compareTo(totalLiabilities) != 0) {
            totalLiabilities = currentLiabilities.add(nonCurrentLiabilities);
        }
    }

    /**
     * 유동비율 계산
     */
    public BigDecimal getCurrentRatio() {
        if (currentLiabilities.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAssets.divide(currentLiabilities, 4, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 부채비율 계산
     */
    public BigDecimal getDebtRatio() {
        if (totalAssets.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return totalLiabilities.divide(totalAssets, 4, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal("100"));
    }

    /**
     * 자기자본비율 계산
     */
    public BigDecimal getEquityRatio() {
        if (totalAssets.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return totalEquity.divide(totalAssets, 4, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal("100"));
    }

    /**
     * 총자산순이익률(ROA) 계산
     */
    public BigDecimal getROA() {
        if (totalAssets.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return netIncome.divide(totalAssets, 4, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal("100"));
    }

    /**
     * 자기자본순이익률(ROE) 계산
     */
    public BigDecimal getROE() {
        if (totalEquity.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return netIncome.divide(totalEquity, 4, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal("100"));
    }

    /**
     * 매출총이익률 계산
     */
    public BigDecimal getGrossMargin() {
        if (totalRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return operatingIncome.divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal("100"));
    }

    /**
     * 순이익률 계산
     */
    public BigDecimal getNetMargin() {
        if (totalRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return netIncome.divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal("100"));
    }

    /**
     * 보고서 제목 자동 생성
     */
    @PrePersist
    @PreUpdate
    private void generateTitle() {
        if (reportTitle == null || reportTitle.isEmpty()) {
            if (company != null && company.getName() != null) {
                this.reportTitle = String.format("%s %d년 %s %s", 
                    company.getName(), 
                    fiscalYear, 
                    fiscalPeriod, 
                    reportType.getDescription());
            } else {
                this.reportTitle = String.format("%d년 %s %s", 
                    fiscalYear, 
                    fiscalPeriod, 
                    reportType.getDescription());
            }
        }
    }
}
