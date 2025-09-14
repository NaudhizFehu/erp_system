package com.erp.sales.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 고객 엔티티
 * 영업관리의 핵심 고객 정보를 관리합니다
 */
@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_customer_company", columnList = "company_id"),
    @Index(name = "idx_customer_code", columnList = "customer_code"),
    @Index(name = "idx_customer_name", columnList = "customer_name"),
    @Index(name = "idx_customer_type", columnList = "customer_type"),
    @Index(name = "idx_customer_status", columnList = "customer_status"),
    @Index(name = "idx_customer_manager", columnList = "sales_manager_id"),
    @Index(name = "idx_customer_grade", columnList = "customer_grade"),
    @Index(name = "idx_customer_business", columnList = "business_registration_number")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Customer extends BaseEntity {

    /**
     * 고객 유형 열거형
     */
    public enum CustomerType {
        INDIVIDUAL,     // 개인
        CORPORATE,      // 법인
        GOVERNMENT,     // 공공기관
        NONPROFIT,      // 비영리단체
        PARTNER,        // 파트너사
        DISTRIBUTOR,    // 유통업체
        RETAILER,       // 소매업체
        WHOLESALER      // 도매업체
    }

    /**
     * 고객 상태 열거형
     */
    public enum CustomerStatus {
        PROSPECT,       // 잠재고객
        ACTIVE,         // 활성고객
        INACTIVE,       // 비활성고객
        VIP,           // VIP고객
        DORMANT,       // 휴면고객
        BLACKLIST      // 블랙리스트
    }

    /**
     * 고객 등급 열거형
     */
    public enum CustomerGrade {
        PLATINUM,      // 플래티넘
        GOLD,          // 골드
        SILVER,        // 실버
        BRONZE,        // 브론즈
        GENERAL        // 일반
    }

    /**
     * 결제 조건 열거형
     */
    public enum PaymentTerm {
        CASH,          // 현금
        NET_7,         // 7일
        NET_15,        // 15일
        NET_30,        // 30일
        NET_45,        // 45일
        NET_60,        // 60일
        NET_90,        // 90일
        CUSTOM         // 사용자정의
    }

    @NotBlank(message = "고객코드는 필수입니다")
    @Size(max = 50, message = "고객코드는 50자 이내여야 합니다")
    @Column(name = "customer_code", nullable = false, unique = true, length = 50)
    private String customerCode;

    @NotBlank(message = "고객명은 필수입니다")
    @Size(max = 200, message = "고객명은 200자 이내여야 합니다")
    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;

    @Size(max = 200, message = "영문 고객명은 200자 이내여야 합니다")
    @Column(name = "customer_name_en", length = 200)
    private String customerNameEn;

    @NotNull(message = "회사는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_customer_company"))
    private Company company;

    @NotNull(message = "고객유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false, length = 20)
    private CustomerType customerType;

    @NotNull(message = "고객상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_status", nullable = false, length = 20)
    private CustomerStatus customerStatus = CustomerStatus.PROSPECT;

    @NotNull(message = "고객등급은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_grade", nullable = false, length = 20)
    private CustomerGrade customerGrade = CustomerGrade.GENERAL;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 사업자 정보
    @Size(max = 20, message = "사업자등록번호는 20자 이내여야 합니다")
    @Column(name = "business_registration_number", length = 20)
    private String businessRegistrationNumber;

    @Size(max = 100, message = "대표자명은 100자 이내여야 합니다")
    @Column(name = "representative_name", length = 100)
    private String representativeName;

    @Size(max = 100, message = "업종은 100자 이내여야 합니다")
    @Column(name = "business_type", length = 100)
    private String businessType;

    @Size(max = 100, message = "업태는 100자 이내여야 합니다")
    @Column(name = "business_item", length = 100)
    private String businessItem;

    // 연락처 정보
    @Size(max = 20, message = "전화번호는 20자 이내여야 합니다")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Size(max = 20, message = "팩스번호는 20자 이내여야 합니다")
    @Column(name = "fax_number", length = 20)
    private String faxNumber;

    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Size(max = 100, message = "이메일은 100자 이내여야 합니다")
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 200, message = "웹사이트는 200자 이내여야 합니다")
    @Column(name = "website", length = 200)
    private String website;

    // 주소 정보
    @Size(max = 10, message = "우편번호는 10자 이내여야 합니다")
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Size(max = 200, message = "주소는 200자 이내여야 합니다")
    @Column(name = "address", length = 200)
    private String address;

    @Size(max = 200, message = "상세주소는 200자 이내여야 합니다")
    @Column(name = "address_detail", length = 200)
    private String addressDetail;

    @Size(max = 100, message = "시/도는 100자 이내여야 합니다")
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 100, message = "구/군은 100자 이내여야 합니다")
    @Column(name = "district", length = 100)
    private String district;

    @Size(max = 100, message = "국가는 100자 이내여야 합니다")
    @Column(name = "country", length = 100)
    private String country = "대한민국";

    // 영업 관리 정보
    @Column(name = "sales_manager_id")
    private Long salesManagerId;

    @Size(max = 100, message = "영업담당자명은 100자 이내여야 합니다")
    @Column(name = "sales_manager_name", length = 100)
    private String salesManagerName;

    @Column(name = "first_contact_date")
    private LocalDate firstContactDate;

    @Column(name = "last_contact_date")
    private LocalDate lastContactDate;

    @Size(max = 1000, message = "고객 설명은 1000자 이내여야 합니다")
    @Column(name = "description", length = 1000)
    private String description;

    // 거래 조건
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_term", length = 20)
    private PaymentTerm paymentTerm = PaymentTerm.NET_30;

    @Column(name = "custom_payment_days")
    private Integer customPaymentDays;

    @DecimalMin(value = "0", message = "신용한도는 0 이상이어야 합니다")
    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "할인율은 0 이상이어야 합니다")
    @DecimalMax(value = "100", message = "할인율은 100 이하여야 합니다")
    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "세율은 0 이상이어야 합니다")
    @DecimalMax(value = "100", message = "세율은 100 이하여야 합니다")
    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate = new BigDecimal("10.00");

    // 통계 정보 (계산 필드)
    @Column(name = "total_order_count")
    private Integer totalOrderCount = 0;

    @Column(name = "total_order_amount", precision = 15, scale = 2)
    private BigDecimal totalOrderAmount = BigDecimal.ZERO;

    @Column(name = "last_order_date")
    private LocalDate lastOrderDate;

    @Column(name = "average_order_amount", precision = 15, scale = 2)
    private BigDecimal averageOrderAmount = BigDecimal.ZERO;

    @Column(name = "outstanding_amount", precision = 15, scale = 2)
    private BigDecimal outstandingAmount = BigDecimal.ZERO;

    // 추가 정보
    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    // 연관관계
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Quote> quotes = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contract> contracts = new ArrayList<>();

    // 비즈니스 메서드
    /**
     * 전체 주소 반환
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (postalCode != null && !postalCode.trim().isEmpty()) {
            sb.append("(").append(postalCode).append(") ");
        }
        if (address != null && !address.trim().isEmpty()) {
            sb.append(address);
        }
        if (addressDetail != null && !addressDetail.trim().isEmpty()) {
            sb.append(" ").append(addressDetail);
        }
        return sb.toString().trim();
    }

    /**
     * 고객 상태 설명 반환
     */
    public String getCustomerStatusDescription() {
        return switch (customerStatus) {
            case PROSPECT -> "잠재고객";
            case ACTIVE -> "활성고객";
            case INACTIVE -> "비활성고객";
            case VIP -> "VIP고객";
            case DORMANT -> "휴면고객";
            case BLACKLIST -> "블랙리스트";
        };
    }

    /**
     * 고객 유형 설명 반환
     */
    public String getCustomerTypeDescription() {
        return switch (customerType) {
            case INDIVIDUAL -> "개인";
            case CORPORATE -> "법인";
            case GOVERNMENT -> "공공기관";
            case NONPROFIT -> "비영리단체";
            case PARTNER -> "파트너사";
            case DISTRIBUTOR -> "유통업체";
            case RETAILER -> "소매업체";
            case WHOLESALER -> "도매업체";
        };
    }

    /**
     * 고객 등급 설명 반환
     */
    public String getCustomerGradeDescription() {
        return switch (customerGrade) {
            case PLATINUM -> "플래티넘";
            case GOLD -> "골드";
            case SILVER -> "실버";
            case BRONZE -> "브론즈";
            case GENERAL -> "일반";
        };
    }

    /**
     * 결제 조건 설명 반환
     */
    public String getPaymentTermDescription() {
        return switch (paymentTerm) {
            case CASH -> "현금";
            case NET_7 -> "7일";
            case NET_15 -> "15일";
            case NET_30 -> "30일";
            case NET_45 -> "45일";
            case NET_60 -> "60일";
            case NET_90 -> "90일";
            case CUSTOM -> customPaymentDays != null ? customPaymentDays + "일" : "사용자정의";
        };
    }

    /**
     * VIP 고객 여부 확인
     */
    public boolean isVipCustomer() {
        return customerStatus == CustomerStatus.VIP || customerGrade == CustomerGrade.PLATINUM;
    }

    /**
     * 활성 고객 여부 확인
     */
    public boolean isActiveCustomer() {
        return isActive && (customerStatus == CustomerStatus.ACTIVE || customerStatus == CustomerStatus.VIP);
    }

    /**
     * 신용한도 초과 여부 확인
     */
    public boolean isCreditLimitExceeded() {
        return creditLimit != null && outstandingAmount != null && 
               outstandingAmount.compareTo(creditLimit) > 0;
    }

    /**
     * 고객 요약 정보 반환
     */
    public String getCustomerSummary() {
        return String.format("%s (%s) - %s", 
                customerName, 
                customerCode, 
                getCustomerStatusDescription());
    }

    /**
     * 평균 주문 금액 업데이트
     */
    public void updateAverageOrderAmount() {
        if (totalOrderCount != null && totalOrderCount > 0 && totalOrderAmount != null) {
            this.averageOrderAmount = totalOrderAmount.divide(
                new BigDecimal(totalOrderCount), 2, java.math.RoundingMode.HALF_UP);
        } else {
            this.averageOrderAmount = BigDecimal.ZERO;
        }
    }

    /**
     * 주문 통계 업데이트
     */
    public void updateOrderStatistics(BigDecimal orderAmount, LocalDate orderDate) {
        if (totalOrderCount == null) totalOrderCount = 0;
        if (totalOrderAmount == null) totalOrderAmount = BigDecimal.ZERO;

        totalOrderCount++;
        totalOrderAmount = totalOrderAmount.add(orderAmount);
        lastOrderDate = orderDate;
        updateAverageOrderAmount();
    }
}