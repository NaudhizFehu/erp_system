package com.erp.accounting.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 계정 엔티티
 * 회계 시스템의 계정과목을 관리합니다
 */
@Entity
@Table(name = "accounts", indexes = {
    @Index(name = "idx_accounts_code", columnList = "account_code"),
    @Index(name = "idx_accounts_type", columnList = "account_type"),
    @Index(name = "idx_accounts_parent", columnList = "parent_account_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity {

    /**
     * 계정 코드
     */
    @NotBlank(message = "계정 코드는 필수입니다")
    @Size(max = 20, message = "계정 코드는 20자 이하여야 합니다")
    @Pattern(regexp = "^[0-9]+$", message = "계정 코드는 숫자만 사용 가능합니다")
    @Column(name = "account_code", unique = true, nullable = false, length = 20)
    private String accountCode;

    /**
     * 계정명
     */
    @NotBlank(message = "계정명은 필수입니다")
    @Size(max = 100, message = "계정명은 100자 이하여야 합니다")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 계정명 (영문)
     */
    @Size(max = 100, message = "영문 계정명은 100자 이하여야 합니다")
    @Column(name = "name_en", length = 100)
    private String nameEn;

    /**
     * 계정 유형
     */
    @NotNull(message = "계정 유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    /**
     * 계정 분류
     */
    @NotNull(message = "계정 분류는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "account_category", nullable = false, length = 20)
    private AccountCategory accountCategory;

    /**
     * 차대변 유형
     */
    @NotNull(message = "차대변 유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "debit_credit_type", nullable = false, length = 10)
    private DebitCreditType debitCreditType;

    /**
     * 상위 계정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_account_id")
    private Account parentAccount;

    /**
     * 하위 계정들
     */
    @OneToMany(mappedBy = "parentAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> subAccounts;

    /**
     * 계정 레벨 (1: 최상위, 숫자가 클수록 하위)
     */
    @NotNull(message = "계정 레벨은 필수입니다")
    @Min(value = 1, message = "계정 레벨은 1 이상이어야 합니다")
    @Max(value = 10, message = "계정 레벨은 10 이하여야 합니다")
    @Column(name = "account_level", nullable = false)
    private Integer accountLevel;

    /**
     * 계정 설명
     */
    @Size(max = 500, message = "계정 설명은 500자 이하여야 합니다")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 소속 회사
     */
    @NotNull(message = "소속 회사는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /**
     * 차변 잔액
     */
    @DecimalMin(value = "0", message = "차변 잔액은 0 이상이어야 합니다")
    @Column(name = "debit_balance", precision = 15, scale = 2)
    private BigDecimal debitBalance = BigDecimal.ZERO;

    /**
     * 대변 잔액
     */
    @DecimalMin(value = "0", message = "대변 잔액은 0 이상이어야 합니다")
    @Column(name = "credit_balance", precision = 15, scale = 2)
    private BigDecimal creditBalance = BigDecimal.ZERO;

    /**
     * 정렬 순서
     */
    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 사용 여부
     */
    @NotNull(message = "사용 여부는 필수입니다")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 잔액 추적 여부
     */
    @NotNull(message = "잔액 추적 여부는 필수입니다")
    @Column(name = "track_balance", nullable = false)
    private Boolean trackBalance = true;

    /**
     * 현재 잔액
     */
    @DecimalMin(value = "0", message = "현재 잔액은 0 이상이어야 합니다")
    @Column(name = "current_balance", precision = 15, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    /**
     * 기초 잔액
     */
    @DecimalMin(value = "0", message = "기초 잔액은 0 이상이어야 합니다")
    @Column(name = "opening_balance", precision = 15, scale = 2)
    private BigDecimal openingBalance = BigDecimal.ZERO;

    /**
     * 예산 금액
     */
    @DecimalMin(value = "0", message = "예산 금액은 0 이상이어야 합니다")
    @Column(name = "budget_amount", precision = 15, scale = 2)
    private BigDecimal budgetAmount = BigDecimal.ZERO;

    /**
     * 세금 코드
     */
    @Size(max = 20, message = "세금 코드는 20자 이하여야 합니다")
    @Column(name = "tax_code", length = 20)
    private String taxCode;

    /**
     * 제어 필드 1
     */
    @Size(max = 50, message = "제어 필드 1은 50자 이하여야 합니다")
    @Column(name = "control_field1", length = 50)
    private String controlField1;

    /**
     * 제어 필드 2
     */
    @Size(max = 50, message = "제어 필드 2는 50자 이하여야 합니다")
    @Column(name = "control_field2", length = 50)
    private String controlField2;

    /**
     * 전체 경로
     */
    @Size(max = 500, message = "전체 경로는 500자 이하여야 합니다")
    @Column(name = "full_path", length = 500)
    private String fullPath;

    /**
     * 전체 코드 경로
     */
    @Size(max = 500, message = "전체 코드 경로는 500자 이하여야 합니다")
    @Column(name = "full_code_path", length = 500)
    private String fullCodePath;

    /**
     * 리프 계정 여부 (하위 계정이 없는 계정)
     */
    @NotNull(message = "리프 계정 여부는 필수입니다")
    @Column(name = "is_leaf_account", nullable = false)
    private Boolean isLeafAccount = true;

    /**
     * 해당 계정의 거래들
     */
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    /**
     * 계정 유형 열거형
     */
    public enum AccountType {
        ASSET("자산"),
        LIABILITY("부채"),
        EQUITY("자본"),
        REVENUE("수익"),
        EXPENSE("비용");

        private final String description;

        AccountType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 계정 분류 열거형
     */
    public enum AccountCategory {
        CURRENT_ASSET("유동자산"),
        FIXED_ASSET("고정자산"),
        CURRENT_LIABILITY("유동부채"),
        LONG_TERM_LIABILITY("장기부채"),
        CAPITAL("자본"),
        OPERATING_REVENUE("영업수익"),
        NON_OPERATING_REVENUE("영업외수익"),
        OPERATING_EXPENSE("영업비용"),
        NON_OPERATING_EXPENSE("영업외비용");

        private final String description;

        AccountCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 차대변 유형 열거형
     */
    public enum DebitCreditType {
        DEBIT("차변"),
        CREDIT("대변"),
        BOTH("차대변");

        private final String description;

        DebitCreditType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 계정이 활성 상태인지 확인
     */
    public boolean isActiveAccount() {
        return isActive && !getIsDeleted();
    }

    /**
     * 잔액 계산 (차변 - 대변)
     */
    public BigDecimal getBalance() {
        if (accountType == AccountType.ASSET || accountType == AccountType.EXPENSE) {
            return debitBalance.subtract(creditBalance);
        } else {
            return creditBalance.subtract(debitBalance);
        }
    }

    /**
     * 잔액이 차변인지 확인
     */
    public boolean isDebitBalance() {
        return getBalance().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 잔액이 대변인지 확인
     */
    public boolean isCreditBalance() {
        return getBalance().compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * 하위 계정 수 반환
     */
    public int getSubAccountCount() {
        return subAccounts != null ? subAccounts.size() : 0;
    }

    /**
     * 거래 수 반환
     */
    public int getTransactionCount() {
        return transactions != null ? transactions.size() : 0;
    }

    /**
     * 계정명 반환 (name 필드의 getter)
     */
    public String getAccountName() {
        return name;
    }

    /**
     * 잔액 업데이트
     */
    public void updateBalance(BigDecimal amount, boolean isDebit) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        if (isDebit) {
            this.debitBalance = this.debitBalance.add(amount);
        } else {
            this.creditBalance = this.creditBalance.add(amount);
        }
    }

    /**
     * 잔액 초기화
     */
    public void resetBalance() {
        this.debitBalance = BigDecimal.ZERO;
        this.creditBalance = BigDecimal.ZERO;
        this.currentBalance = BigDecimal.ZERO;
    }

    /**
     * 리프 계정 여부 확인
     */
    public boolean isLeafAccount() {
        return subAccounts == null || subAccounts.isEmpty();
    }
}