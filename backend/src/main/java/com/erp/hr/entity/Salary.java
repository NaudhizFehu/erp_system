package com.erp.hr.entity;

import com.erp.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 급여 엔티티
 * 직원의 급여 정보를 관리합니다
 */
@Entity
@Table(name = "salaries", indexes = {
    @Index(name = "idx_salaries_employee", columnList = "employee_id"),
    @Index(name = "idx_salaries_pay_date", columnList = "pay_date"),
    @Index(name = "idx_salaries_year_month", columnList = "pay_year, pay_month"),
    @Index(name = "idx_salaries_status", columnList = "payment_status"),
    @Index(name = "idx_salaries_employee_year_month", columnList = "employee_id, pay_year, pay_month")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_salaries_employee_year_month", columnNames = {"employee_id", "pay_year", "pay_month"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Salary extends BaseEntity {

    /**
     * 직원
     */
    @NotNull(message = "직원 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    /**
     * 급여 연도
     */
    @NotNull(message = "급여 연도는 필수입니다")
    @Min(value = 2000, message = "급여 연도는 2000년 이상이어야 합니다")
    @Max(value = 2100, message = "급여 연도는 2100년 이하여야 합니다")
    @Column(name = "pay_year", nullable = false)
    private Integer payYear;

    /**
     * 급여 월
     */
    @NotNull(message = "급여 월은 필수입니다")
    @Min(value = 1, message = "급여 월은 1 이상이어야 합니다")
    @Max(value = 12, message = "급여 월은 12 이하여야 합니다")
    @Column(name = "pay_month", nullable = false)
    private Integer payMonth;

    /**
     * 급여 지급일
     */
    @Column(name = "pay_date")
    private LocalDate payDate;

    /**
     * 기본급
     */
    @NotNull(message = "기본급은 필수입니다")
    @Min(value = 0, message = "기본급은 0 이상이어야 합니다")
    @Column(name = "base_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseSalary;

    /**
     * 직책수당
     */
    @Min(value = 0, message = "직책수당은 0 이상이어야 합니다")
    @Column(name = "position_allowance", precision = 10, scale = 2)
    private BigDecimal positionAllowance = BigDecimal.ZERO;

    /**
     * 식대
     */
    @Min(value = 0, message = "식대는 0 이상이어야 합니다")
    @Column(name = "meal_allowance", precision = 10, scale = 2)
    private BigDecimal mealAllowance = BigDecimal.ZERO;

    /**
     * 교통비
     */
    @Min(value = 0, message = "교통비는 0 이상이어야 합니다")
    @Column(name = "transport_allowance", precision = 10, scale = 2)
    private BigDecimal transportAllowance = BigDecimal.ZERO;

    /**
     * 가족수당
     */
    @Min(value = 0, message = "가족수당은 0 이상이어야 합니다")
    @Column(name = "family_allowance", precision = 10, scale = 2)
    private BigDecimal familyAllowance = BigDecimal.ZERO;

    /**
     * 야근수당
     */
    @Min(value = 0, message = "야근수당은 0 이상이어야 합니다")
    @Column(name = "overtime_allowance", precision = 10, scale = 2)
    private BigDecimal overtimeAllowance = BigDecimal.ZERO;

    /**
     * 휴일근무수당
     */
    @Min(value = 0, message = "휴일근무수당은 0 이상이어야 합니다")
    @Column(name = "holiday_allowance", precision = 10, scale = 2)
    private BigDecimal holidayAllowance = BigDecimal.ZERO;

    /**
     * 야간근무수당
     */
    @Min(value = 0, message = "야간근무수당은 0 이상이어야 합니다")
    @Column(name = "night_allowance", precision = 10, scale = 2)
    private BigDecimal nightAllowance = BigDecimal.ZERO;

    /**
     * 기타수당
     */
    @Min(value = 0, message = "기타수당은 0 이상이어야 합니다")
    @Column(name = "other_allowance", precision = 10, scale = 2)
    private BigDecimal otherAllowance = BigDecimal.ZERO;

    /**
     * 성과급
     */
    @Min(value = 0, message = "성과급은 0 이상이어야 합니다")
    @Column(name = "performance_bonus", precision = 10, scale = 2)
    private BigDecimal performanceBonus = BigDecimal.ZERO;

    /**
     * 특별수당
     */
    @Min(value = 0, message = "특별수당은 0 이상이어야 합니다")
    @Column(name = "special_allowance", precision = 10, scale = 2)
    private BigDecimal specialAllowance = BigDecimal.ZERO;

    /**
     * 총 지급액 (세전)
     */
    @Min(value = 0, message = "총 지급액은 0 이상이어야 합니다")
    @Column(name = "gross_pay", precision = 12, scale = 2)
    private BigDecimal grossPay;

    /**
     * 국민연금
     */
    @Min(value = 0, message = "국민연금은 0 이상이어야 합니다")
    @Column(name = "national_pension", precision = 10, scale = 2)
    private BigDecimal nationalPension = BigDecimal.ZERO;

    /**
     * 건강보험
     */
    @Min(value = 0, message = "건강보험은 0 이상이어야 합니다")
    @Column(name = "health_insurance", precision = 10, scale = 2)
    private BigDecimal healthInsurance = BigDecimal.ZERO;

    /**
     * 장기요양보험
     */
    @Min(value = 0, message = "장기요양보험은 0 이상이어야 합니다")
    @Column(name = "long_term_care", precision = 10, scale = 2)
    private BigDecimal longTermCare = BigDecimal.ZERO;

    /**
     * 고용보험
     */
    @Min(value = 0, message = "고용보험은 0 이상이어야 합니다")
    @Column(name = "employment_insurance", precision = 10, scale = 2)
    private BigDecimal employmentInsurance = BigDecimal.ZERO;

    /**
     * 소득세
     */
    @Min(value = 0, message = "소득세는 0 이상이어야 합니다")
    @Column(name = "income_tax", precision = 10, scale = 2)
    private BigDecimal incomeTax = BigDecimal.ZERO;

    /**
     * 지방소득세
     */
    @Min(value = 0, message = "지방소득세는 0 이상이어야 합니다")
    @Column(name = "local_income_tax", precision = 10, scale = 2)
    private BigDecimal localIncomeTax = BigDecimal.ZERO;

    /**
     * 기타 공제
     */
    @Min(value = 0, message = "기타 공제는 0 이상이어야 합니다")
    @Column(name = "other_deduction", precision = 10, scale = 2)
    private BigDecimal otherDeduction = BigDecimal.ZERO;

    /**
     * 총 공제액
     */
    @Min(value = 0, message = "총 공제액은 0 이상이어야 합니다")
    @Column(name = "total_deduction", precision = 12, scale = 2)
    private BigDecimal totalDeduction;

    /**
     * 실수령액
     */
    @Min(value = 0, message = "실수령액은 0 이상이어야 합니다")
    @Column(name = "net_pay", precision = 12, scale = 2)
    private BigDecimal netPay;

    /**
     * 근무일수
     */
    @Min(value = 0, message = "근무일수는 0 이상이어야 합니다")
    @Column(name = "work_days")
    private Integer workDays;

    /**
     * 실제 근무시간 (시간 단위)
     */
    @Min(value = 0, message = "실제 근무시간은 0 이상이어야 합니다")
    @Column(name = "work_hours", precision = 6, scale = 2)
    private BigDecimal workHours;

    /**
     * 초과근무시간 (시간 단위)
     */
    @Min(value = 0, message = "초과근무시간은 0 이상이어야 합니다")
    @Column(name = "overtime_hours", precision = 6, scale = 2)
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    /**
     * 야간근무시간 (시간 단위)
     */
    @Min(value = 0, message = "야간근무시간은 0 이상이어야 합니다")
    @Column(name = "night_hours", precision = 6, scale = 2)
    private BigDecimal nightHours = BigDecimal.ZERO;

    /**
     * 휴일근무시간 (시간 단위)
     */
    @Min(value = 0, message = "휴일근무시간은 0 이상이어야 합니다")
    @Column(name = "holiday_hours", precision = 6, scale = 2)
    private BigDecimal holidayHours = BigDecimal.ZERO;

    /**
     * 지급 상태
     */
    @NotNull(message = "지급 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    /**
     * 급여 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "salary_type", length = 20)
    private SalaryType salaryType = SalaryType.MONTHLY;

    /**
     * 계산 기준일
     */
    @Column(name = "calculation_date")
    private LocalDate calculationDate;

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
     * 비고
     */
    @Size(max = 1000, message = "비고는 1000자 이하여야 합니다")
    @Column(name = "remarks", length = 1000)
    private String remarks;

    /**
     * 지급 상태 열거형
     */
    public enum PaymentStatus {
        PENDING("지급대기"),
        CALCULATED("계산완료"),
        APPROVED("승인완료"),
        PAID("지급완료"),
        CANCELLED("취소");

        private final String description;

        PaymentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 급여 유형 열거형
     */
    public enum SalaryType {
        MONTHLY("월급"),
        HOURLY("시급"),
        DAILY("일급"),
        BONUS("상여금"),
        SEVERANCE("퇴직금");

        private final String description;

        SalaryType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 총 지급액 계산
     */
    public void calculateGrossPay() {
        this.grossPay = baseSalary
            .add(positionAllowance)
            .add(mealAllowance)
            .add(transportAllowance)
            .add(familyAllowance)
            .add(overtimeAllowance)
            .add(holidayAllowance)
            .add(nightAllowance)
            .add(otherAllowance)
            .add(performanceBonus)
            .add(specialAllowance);
    }

    /**
     * 총 공제액 계산
     */
    public void calculateTotalDeduction() {
        this.totalDeduction = nationalPension
            .add(healthInsurance)
            .add(longTermCare)
            .add(employmentInsurance)
            .add(incomeTax)
            .add(localIncomeTax)
            .add(otherDeduction);
    }

    /**
     * 실수령액 계산
     */
    public void calculateNetPay() {
        if (this.grossPay == null) {
            calculateGrossPay();
        }
        if (this.totalDeduction == null) {
            calculateTotalDeduction();
        }
        
        this.netPay = this.grossPay.subtract(this.totalDeduction);
    }

    /**
     * 4대보험 자동 계산
     */
    public void calculateSocialInsurance() {
        if (grossPay == null) {
            return;
        }
        
        // 국민연금 (4.5%, 상한액 적용)
        BigDecimal pensionBase = grossPay.min(new BigDecimal("5530000")); // 2024년 기준
        this.nationalPension = pensionBase.multiply(new BigDecimal("0.045"));
        
        // 건강보험 (3.545%)
        this.healthInsurance = grossPay.multiply(new BigDecimal("0.03545"));
        
        // 장기요양보험 (건강보험료의 12.81%)
        this.longTermCare = this.healthInsurance.multiply(new BigDecimal("0.1281"));
        
        // 고용보험 (0.9%)
        this.employmentInsurance = grossPay.multiply(new BigDecimal("0.009"));
    }

    /**
     * 소득세 간이 계산 (실제로는 더 복잡한 로직 필요)
     */
    public void calculateIncomeTax() {
        if (grossPay == null) {
            return;
        }
        
        // 간이세액표 기준 (매우 단순화된 계산)
        BigDecimal taxableIncome = grossPay.subtract(
            nationalPension.add(healthInsurance).add(employmentInsurance)
        );
        
        if (taxableIncome.compareTo(new BigDecimal("1000000")) <= 0) {
            this.incomeTax = BigDecimal.ZERO;
        } else if (taxableIncome.compareTo(new BigDecimal("3000000")) <= 0) {
            this.incomeTax = taxableIncome.multiply(new BigDecimal("0.06"));
        } else {
            this.incomeTax = taxableIncome.multiply(new BigDecimal("0.15"));
        }
        
        // 지방소득세 (소득세의 10%)
        this.localIncomeTax = this.incomeTax.multiply(new BigDecimal("0.1"));
    }

    /**
     * 전체 급여 계산 수행
     */
    public void calculateAll() {
        calculateGrossPay();
        calculateSocialInsurance();
        calculateIncomeTax();
        calculateTotalDeduction();
        calculateNetPay();
        
        this.calculationDate = LocalDate.now();
        this.paymentStatus = PaymentStatus.CALCULATED;
    }

    /**
     * 급여 승인
     */
    public void approve(Employee approver) {
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.APPROVED;
    }

    /**
     * 급여 지급 완료 처리
     */
    public void markAsPaid(LocalDate payDate) {
        this.payDate = payDate;
        this.paymentStatus = PaymentStatus.PAID;
    }

    /**
     * 급여 취소
     */
    public void cancel(String reason) {
        this.paymentStatus = PaymentStatus.CANCELLED;
        this.remarks = (this.remarks != null ? this.remarks + "\n" : "") + 
                      "취소 사유: " + reason;
    }

    /**
     * 급여명세서 제목 생성
     */
    public String getPayrollTitle() {
        return String.format("%d년 %d월 급여명세서", payYear, payMonth);
    }

    /**
     * 월급여 여부 확인
     */
    public boolean isMonthlySalary() {
        return salaryType == SalaryType.MONTHLY;
    }

    /**
     * 지급 완료 여부 확인
     */
    public boolean isPaid() {
        return paymentStatus == PaymentStatus.PAID;
    }
}




