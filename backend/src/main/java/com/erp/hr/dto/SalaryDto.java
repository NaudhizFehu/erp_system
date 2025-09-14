package com.erp.hr.dto;

import com.erp.hr.entity.Salary;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 급여 정보 응답 DTO
 * 급여 정보 조회 시 사용됩니다
 */
public record SalaryDto(
        Long id,
        EmployeeDto employee,
        Integer payYear,
        Integer payMonth,
        LocalDate payDate,
        BigDecimal baseSalary,
        BigDecimal positionAllowance,
        BigDecimal mealAllowance,
        BigDecimal transportAllowance,
        BigDecimal familyAllowance,
        BigDecimal overtimeAllowance,
        BigDecimal holidayAllowance,
        BigDecimal nightAllowance,
        BigDecimal otherAllowance,
        BigDecimal performanceBonus,
        BigDecimal specialAllowance,
        BigDecimal grossPay,
        BigDecimal nationalPension,
        BigDecimal healthInsurance,
        BigDecimal longTermCare,
        BigDecimal employmentInsurance,
        BigDecimal incomeTax,
        BigDecimal localIncomeTax,
        BigDecimal otherDeduction,
        BigDecimal totalDeduction,
        BigDecimal netPay,
        Integer workDays,
        BigDecimal workHours,
        BigDecimal overtimeHours,
        BigDecimal nightHours,
        BigDecimal holidayHours,
        Salary.PaymentStatus paymentStatus,
        Salary.SalaryType salaryType,
        LocalDate calculationDate,
        EmployeeDto approvedBy,
        LocalDateTime approvedAt,
        String remarks,
        String payrollTitle,
        Boolean isMonthlySalary,
        Boolean isPaid,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public SalaryDto {
        if (employee == null) {
            throw new IllegalArgumentException("직원 정보는 필수입니다");
        }
        if (payYear == null || payYear < 2000 || payYear > 2100) {
            throw new IllegalArgumentException("급여 연도는 2000~2100년 사이여야 합니다");
        }
        if (payMonth == null || payMonth < 1 || payMonth > 12) {
            throw new IllegalArgumentException("급여 월은 1~12월 사이여야 합니다");
        }
        if (baseSalary == null || baseSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("기본급은 0 이상이어야 합니다");
        }
        if (paymentStatus == null) {
            throw new IllegalArgumentException("지급 상태는 필수입니다");
        }
    }
    
    /**
     * Salary 엔티티로부터 SalaryDto 생성
     */
    public static SalaryDto from(Salary salary) {
        return new SalaryDto(
            salary.getId(),
            salary.getEmployee() != null ? EmployeeDto.from(salary.getEmployee()) : null,
            salary.getPayYear(),
            salary.getPayMonth(),
            salary.getPayDate(),
            salary.getBaseSalary(),
            salary.getPositionAllowance(),
            salary.getMealAllowance(),
            salary.getTransportAllowance(),
            salary.getFamilyAllowance(),
            salary.getOvertimeAllowance(),
            salary.getHolidayAllowance(),
            salary.getNightAllowance(),
            salary.getOtherAllowance(),
            salary.getPerformanceBonus(),
            salary.getSpecialAllowance(),
            salary.getGrossPay(),
            salary.getNationalPension(),
            salary.getHealthInsurance(),
            salary.getLongTermCare(),
            salary.getEmploymentInsurance(),
            salary.getIncomeTax(),
            salary.getLocalIncomeTax(),
            salary.getOtherDeduction(),
            salary.getTotalDeduction(),
            salary.getNetPay(),
            salary.getWorkDays(),
            salary.getWorkHours(),
            salary.getOvertimeHours(),
            salary.getNightHours(),
            salary.getHolidayHours(),
            salary.getPaymentStatus(),
            salary.getSalaryType(),
            salary.getCalculationDate(),
            salary.getApprovedBy() != null ? EmployeeDto.from(salary.getApprovedBy()) : null,
            salary.getApprovedAt(),
            salary.getRemarks(),
            salary.getPayrollTitle(),
            salary.isMonthlySalary(),
            salary.isPaid(),
            salary.getCreatedAt(),
            salary.getUpdatedAt()
        );
    }
}




