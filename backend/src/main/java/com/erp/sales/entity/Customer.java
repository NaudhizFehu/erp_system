package com.erp.sales.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import com.erp.hr.entity.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

/**
 * 고객 엔티티
 * ERP 시스템의 고객 정보를 관리합니다
 */
@Data
@Entity
@Table(name = "customers")
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity {

    /**
     * 고객 유형 열거형
     */
    public enum CustomerType {
        INDIVIDUAL,     // 개인
        CORPORATE       // 법인
    }

    /**
     * 고객 상태 열거형
     */
    public enum CustomerStatus {
        ACTIVE,         // 활성
        INACTIVE,       // 비활성
        SUSPENDED,      // 정지
        DORMANT         // 휴면
    }

    /**
     * 고객 등급 열거형
     */
    public enum CustomerGrade {
        BRONZE,         // 브론즈
        SILVER,         // 실버
        GOLD,           // 골드
        PLATINUM,       // 플래티넘
        DIAMOND         // 다이아몬드
    }

    @NotBlank(message = "고객코드는 필수입니다")
    @Size(max = 20, message = "고객코드는 20자 이내여야 합니다")
    @Column(name = "customer_code", nullable = false, unique = true, length = 20)
    private String customerCode;

    @NotBlank(message = "고객명은 필수입니다")
    @Size(max = 200, message = "고객명은 200자 이내여야 합니다")
    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;

    @NotNull(message = "회사는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_customer_company"))
    private Company company;

    @NotNull(message = "고객유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false, length = 20)
    private CustomerType customerType;

    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Size(max = 100, message = "이메일은 100자 이내여야 합니다")
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 20, message = "전화번호는 20자 이내여야 합니다")
    @Column(name = "phone", length = 20)
    private String phoneNumber;

    @Size(max = 200, message = "주소는 200자 이내여야 합니다")
    @Column(name = "address", length = 200)
    private String address;

    /**
     * 고객 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_status", nullable = false, length = 20)
    @Builder.Default
    private CustomerStatus customerStatus = CustomerStatus.ACTIVE;

    /**
     * 고객 등급
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_grade", length = 20)
    private CustomerGrade customerGrade;

    /**
     * 사업자등록번호
     */
    @Size(max = 12, message = "사업자등록번호는 12자 이내여야 합니다")
    @Column(name = "business_registration_number", length = 12)
    private String businessRegistrationNumber;

    /**
     * 대표자명
     */
    @Size(max = 50, message = "대표자명은 50자 이내여야 합니다")
    @Column(name = "ceo_name", length = 50)
    private String ceoName;

    /**
     * 영업담당자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_manager_id", foreignKey = @ForeignKey(name = "fk_customer_sales_manager"))
    private Employee salesManager;

    /**
     * 신용한도
     */
    @DecimalMin(value = "0.0", message = "신용한도는 0 이상이어야 합니다")
    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    /**
     * 결제조건
     */
    @Size(max = 50, message = "결제조건은 50자 이내여야 합니다")
    @Column(name = "payment_terms", length = 50)
    private String paymentTerms;

    /**
     * 고객 유형 설명 반환
     */
    public String getCustomerTypeDescription() {
        return switch (customerType) {
            case INDIVIDUAL -> "개인";
            case CORPORATE -> "법인";
        };
    }

    /**
     * 전체 주소 반환
     */
    public String getFullAddress() {
        return address != null ? address.trim() : "";
    }

    /**
     * 고객 상태 설명 반환
     */
    public String getCustomerStatusDescription() {
        return switch (customerStatus) {
            case ACTIVE -> "활성";
            case INACTIVE -> "비활성";
            case SUSPENDED -> "정지";
            case DORMANT -> "휴면";
        };
    }

    /**
     * 고객 등급 설명 반환
     */
    public String getCustomerGradeDescription() {
        if (customerGrade == null) return "미분류";
        return switch (customerGrade) {
            case BRONZE -> "브론즈";
            case SILVER -> "실버";
            case GOLD -> "골드";
            case PLATINUM -> "플래티넘";
            case DIAMOND -> "다이아몬드";
        };
    }

    /**
     * 활성 고객 여부 확인
     */
    public boolean isActive() {
        return customerStatus == CustomerStatus.ACTIVE;
    }

    /**
     * 신용한도 초과 여부 확인
     */
    public boolean isOverCreditLimit(BigDecimal amount) {
        if (creditLimit == null || amount == null) return false;
        return amount.compareTo(creditLimit) > 0;
    }
}