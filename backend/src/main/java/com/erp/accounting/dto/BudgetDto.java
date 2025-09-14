package com.erp.accounting.dto;

import com.erp.common.dto.CompanyDto;
import com.erp.hr.dto.EmployeeDto;
import com.erp.accounting.entity.Budget;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 예산 정보 응답 DTO
 * 예산 정보 조회 시 사용됩니다
 */
public record BudgetDto(
        Long id,
        CompanyDto company,
        AccountDto account,
        Integer fiscalYear,
        Budget.BudgetPeriod budgetPeriod,
        Integer periodNumber,
        Budget.BudgetType budgetType,
        Budget.BudgetStatus budgetStatus,
        BigDecimal budgetAmount,
        BigDecimal previousActual,
        BigDecimal currentActual,
        BigDecimal achievementRate,
        BigDecimal varianceAmount,
        BigDecimal varianceRate,
        String description,
        String budgetBasis,
        EmployeeDto responsiblePerson,
        String departmentCode,
        String projectCode,
        EmployeeDto approvedBy,
        LocalDateTime approvedAt,
        Boolean isOverBudget,
        BigDecimal overBudgetAmount,
        BigDecimal remainingBudget,
        BigDecimal progressRate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public BudgetDto {
        if (company == null) {
            throw new IllegalArgumentException("소속 회사는 필수입니다");
        }
        if (account == null) {
            throw new IllegalArgumentException("계정과목은 필수입니다");
        }
        if (fiscalYear == null || fiscalYear < 2000 || fiscalYear > 2100) {
            throw new IllegalArgumentException("회계연도는 2000~2100년 사이여야 합니다");
        }
        if (budgetPeriod == null) {
            throw new IllegalArgumentException("예산 기간은 필수입니다");
        }
        if (budgetType == null) {
            throw new IllegalArgumentException("예산 유형은 필수입니다");
        }
        if (budgetStatus == null) {
            throw new IllegalArgumentException("예산 상태는 필수입니다");
        }
        if (budgetAmount == null || budgetAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("예산 금액은 0 이상이어야 합니다");
        }
    }
    
    /**
     * Budget 엔티티로부터 BudgetDto 생성
     */
    public static BudgetDto from(Budget budget) {
        return new BudgetDto(
            budget.getId(),
            budget.getCompany() != null ? CompanyDto.from(budget.getCompany()) : null,
            budget.getAccount() != null ? AccountDto.from(budget.getAccount()) : null,
            budget.getFiscalYear(),
            budget.getBudgetPeriod(),
            budget.getPeriodNumber(),
            budget.getBudgetType(),
            budget.getBudgetStatus(),
            budget.getBudgetAmount(),
            budget.getPreviousActual(),
            budget.getCurrentActual(),
            budget.getAchievementRate(),
            budget.getVarianceAmount(),
            budget.getVarianceRate(),
            budget.getDescription(),
            budget.getBudgetBasis(),
            budget.getResponsiblePerson() != null ? EmployeeDto.from(budget.getResponsiblePerson()) : null,
            budget.getDepartmentCode(),
            budget.getProjectCode(),
            budget.getApprovedBy() != null ? EmployeeDto.from(budget.getApprovedBy()) : null,
            budget.getApprovedAt(),
            budget.isOverBudget(),
            budget.getOverBudgetAmount(),
            budget.getRemainingBudget(),
            budget.getProgressRate(),
            budget.getCreatedAt(),
            budget.getUpdatedAt()
        );
    }
}




