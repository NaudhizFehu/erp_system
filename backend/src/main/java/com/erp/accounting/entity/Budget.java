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
import java.time.LocalDateTime;
import java.util.List;

/**
 * 예산 엔티티
 * 회계연도별 예산 계획 및 관리를 담당합니다
 */
@Entity
@Table(name = "budgets", indexes = {
    @Index(name = "idx_budgets_company", columnList = "company_id"),
    @Index(name = "idx_budgets_account", columnList = "account_id"),
    @Index(name = "idx_budgets_period", columnList = "fiscal_year, budget_period"),
    @Index(name = "idx_budgets_status", columnList = "budget_status"),
    @Index(name = "idx_budgets_type", columnList = "budget_type")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_budgets_account_period", 
                     columnNames = {"company_id", "account_id", "fiscal_year", "budget_period"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Budget extends BaseEntity {

    /**
     * 소속 회사
     */
    @NotNull(message = "소속 회사는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /**
     * 계정과목
     */
    @NotNull(message = "계정과목은 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /**
     * 회계연도
     */
    @NotNull(message = "회계연도는 필수입니다")
    @Min(value = 2000, message = "회계연도는 2000년 이상이어야 합니다")
    @Max(value = 2100, message = "회계연도는 2100년 이하여야 합니다")
    @Column(name = "fiscal_year", nullable = false)
    private Integer fiscalYear;

    /**
     * 예산 기간 유형
     */
    @NotNull(message = "예산 기간은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "budget_period", nullable = false, length = 20)
    private BudgetPeriod budgetPeriod;

    /**
     * 예산 기간 번호 (월: 1-12, 분기: 1-4)
     */
    @Min(value = 1, message = "예산 기간 번호는 1 이상이어야 합니다")
    @Max(value = 12, message = "예산 기간 번호는 12 이하여야 합니다")
    @Column(name = "period_number")
    private Integer periodNumber;

    /**
     * 예산 유형
     */
    @NotNull(message = "예산 유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "budget_type", nullable = false, length = 20)
    private BudgetType budgetType;

    /**
     * 예산 상태
     */
    @NotNull(message = "예산 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "budget_status", nullable = false, length = 20)
    private BudgetStatus budgetStatus = BudgetStatus.DRAFT;

    /**
     * 예산 금액
     */
    @NotNull(message = "예산 금액은 필수입니다")
    @Min(value = 0, message = "예산 금액은 0 이상이어야 합니다")
    @Column(name = "budget_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal budgetAmount;

    /**
     * 전년도 실적
     */
    @Column(name = "previous_actual", precision = 15, scale = 2)
    private BigDecimal previousActual = BigDecimal.ZERO;

    /**
     * 현재 실적 (누적)
     */
    @Column(name = "current_actual", precision = 15, scale = 2)
    private BigDecimal currentActual = BigDecimal.ZERO;

    /**
     * 예산 대비 실적률 (%)
     */
    @Column(name = "achievement_rate", precision = 5, scale = 2)
    private BigDecimal achievementRate = BigDecimal.ZERO;

    /**
     * 예산 차이 (실적 - 예산)
     */
    @Column(name = "variance_amount", precision = 15, scale = 2)
    private BigDecimal varianceAmount = BigDecimal.ZERO;

    /**
     * 예산 차이율 (%)
     */
    @Column(name = "variance_rate", precision = 5, scale = 2)
    private BigDecimal varianceRate = BigDecimal.ZERO;

    /**
     * 예산 설명
     */
    @Size(max = 500, message = "예산 설명은 500자 이하여야 합니다")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 예산 근거
     */
    @Size(max = 1000, message = "예산 근거는 1000자 이하여야 합니다")
    @Column(name = "budget_basis", length = 1000)
    private String budgetBasis;

    /**
     * 담당자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_person")
    private Employee responsiblePerson;

    /**
     * 부서 코드
     */
    @Size(max = 50, message = "부서 코드는 50자 이하여야 합니다")
    @Column(name = "department_code", length = 50)
    private String departmentCode;

    /**
     * 프로젝트 코드
     */
    @Size(max = 50, message = "프로젝트 코드는 50자 이하여야 합니다")
    @Column(name = "project_code", length = 50)
    private String projectCode;

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
     * 예산 수정 이력
     */
    @OneToMany(mappedBy = "budget", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BudgetRevision> revisions;

    /**
     * 예산 기간 열거형
     */
    public enum BudgetPeriod {
        ANNUAL("연간"),
        QUARTERLY("분기"),
        MONTHLY("월간");

        private final String description;

        BudgetPeriod(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 예산 유형 열거형
     */
    public enum BudgetType {
        REVENUE("수익예산"),
        EXPENSE("비용예산"),
        CAPITAL("자본예산"),
        CASH_FLOW("현금흐름예산");

        private final String description;

        BudgetType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 예산 상태 열거형
     */
    public enum BudgetStatus {
        DRAFT("임시저장"),
        SUBMITTED("제출"),
        APPROVED("승인"),
        ACTIVE("활성"),
        CLOSED("마감"),
        CANCELLED("취소");

        private final String description;

        BudgetStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 예산 승인
     */
    public void approve(Employee approver) {
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
        this.budgetStatus = BudgetStatus.APPROVED;
    }

    /**
     * 예산 활성화
     */
    public void activate() {
        if (budgetStatus != BudgetStatus.APPROVED) {
            throw new IllegalStateException("승인된 예산만 활성화할 수 있습니다");
        }
        this.budgetStatus = BudgetStatus.ACTIVE;
    }

    /**
     * 실적 업데이트
     */
    public void updateActual(BigDecimal actualAmount) {
        this.currentActual = actualAmount;
        calculateVariance();
    }

    /**
     * 예산 대비 실적 계산
     */
    public void calculateVariance() {
        if (budgetAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 달성률 계산
            this.achievementRate = currentActual
                .divide(budgetAmount, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
            
            // 차이 계산
            this.varianceAmount = currentActual.subtract(budgetAmount);
            this.varianceRate = varianceAmount
                .divide(budgetAmount, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
        }
    }

    /**
     * 예산 수정
     */
    public BudgetRevision revise(BigDecimal newAmount, String reason, Employee revisedBy) {
        BudgetRevision revision = new BudgetRevision();
        revision.setBudget(this);
        revision.setOldAmount(this.budgetAmount);
        revision.setNewAmount(newAmount);
        revision.setRevisionReason(reason);
        revision.setRevisedBy(revisedBy);
        revision.setRevisedAt(LocalDateTime.now());
        
        this.budgetAmount = newAmount;
        calculateVariance();
        
        return revision;
    }

    /**
     * 예산 초과 여부 확인
     */
    public boolean isOverBudget() {
        return currentActual.compareTo(budgetAmount) > 0;
    }

    /**
     * 예산 초과 금액
     */
    public BigDecimal getOverBudgetAmount() {
        if (isOverBudget()) {
            return currentActual.subtract(budgetAmount);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 예산 잔액
     */
    public BigDecimal getRemainingBudget() {
        BigDecimal remaining = budgetAmount.subtract(currentActual);
        return remaining.max(BigDecimal.ZERO);
    }

    /**
     * 예산 진행률 (%)
     */
    public BigDecimal getProgressRate() {
        if (budgetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal progress = currentActual.divide(budgetAmount, 4, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal("100"));
        
        return progress.min(new BigDecimal("100"));
    }

    @PrePersist
    @PreUpdate
    private void validateBudget() {
        // 기간 번호 검증
        if (budgetPeriod == BudgetPeriod.MONTHLY && (periodNumber < 1 || periodNumber > 12)) {
            throw new IllegalArgumentException("월간 예산의 기간 번호는 1-12여야 합니다");
        }
        if (budgetPeriod == BudgetPeriod.QUARTERLY && (periodNumber < 1 || periodNumber > 4)) {
            throw new IllegalArgumentException("분기 예산의 기간 번호는 1-4여야 합니다");
        }
        if (budgetPeriod == BudgetPeriod.ANNUAL && periodNumber != null && periodNumber != 1) {
            throw new IllegalArgumentException("연간 예산의 기간 번호는 1이어야 합니다");
        }

        // 계정과목 유형과 예산 유형 일치성 검증
        if (account != null) {
            Account.AccountType accountType = account.getAccountType();
            switch (budgetType) {
                case REVENUE:
                    if (accountType != Account.AccountType.REVENUE) {
                        throw new IllegalArgumentException("수익예산은 수익 계정과목만 사용할 수 있습니다");
                    }
                    break;
                case EXPENSE:
                    if (accountType != Account.AccountType.EXPENSE) {
                        throw new IllegalArgumentException("비용예산은 비용 계정과목만 사용할 수 있습니다");
                    }
                    break;
                case CAPITAL:
                    if (accountType != Account.AccountType.ASSET && accountType != Account.AccountType.EQUITY) {
                        throw new IllegalArgumentException("자본예산은 자산 또는 자본 계정과목만 사용할 수 있습니다");
                    }
                    break;
            }
        }
    }
}




