package com.erp.sales.dto;

import com.erp.sales.entity.Customer;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 고객 관련 DTO 클래스들
 */
public class CustomerDto {

    /**
     * 고객 생성 요청 DTO
     */
    public record CustomerCreateDto(
            @NotBlank(message = "고객코드는 필수입니다")
            @Size(max = 50, message = "고객코드는 50자 이내여야 합니다")
            String customerCode,

            @NotBlank(message = "고객명은 필수입니다")
            @Size(max = 200, message = "고객명은 200자 이내여야 합니다")
            String customerName,

            @Size(max = 200, message = "영문 고객명은 200자 이내여야 합니다")
            String customerNameEn,

            @NotNull(message = "회사 ID는 필수입니다")
            Long companyId,

            @NotNull(message = "고객유형은 필수입니다")
            Customer.CustomerType customerType,

            Customer.CustomerStatus customerStatus,
            Customer.CustomerGrade customerGrade,
            Boolean isActive,

            // 사업자 정보
            @Size(max = 20, message = "사업자등록번호는 20자 이내여야 합니다")
            String businessRegistrationNumber,

            @Size(max = 100, message = "대표자명은 100자 이내여야 합니다")
            String representativeName,

            @Size(max = 100, message = "업종은 100자 이내여야 합니다")
            String businessType,

            @Size(max = 100, message = "업태는 100자 이내여야 합니다")
            String businessItem,

            // 연락처 정보
            @Size(max = 20, message = "전화번호는 20자 이내여야 합니다")
            String phoneNumber,

            @Size(max = 20, message = "팩스번호는 20자 이내여야 합니다")
            String faxNumber,

            @Email(message = "올바른 이메일 형식이어야 합니다")
            @Size(max = 100, message = "이메일은 100자 이내여야 합니다")
            String email,

            @Size(max = 200, message = "웹사이트는 200자 이내여야 합니다")
            String website,

            // 주소 정보
            @Size(max = 10, message = "우편번호는 10자 이내여야 합니다")
            String postalCode,

            @Size(max = 200, message = "주소는 200자 이내여야 합니다")
            String address,

            @Size(max = 200, message = "상세주소는 200자 이내여야 합니다")
            String addressDetail,

            @Size(max = 100, message = "시/도는 100자 이내여야 합니다")
            String city,

            @Size(max = 100, message = "구/군은 100자 이내여야 합니다")
            String district,

            @Size(max = 100, message = "국가는 100자 이내여야 합니다")
            String country,

            // 영업 관리 정보
            Long salesManagerId,
            String salesManagerName,
            LocalDate firstContactDate,
            
            @Size(max = 1000, message = "고객 설명은 1000자 이내여야 합니다")
            String description,

            // 거래 조건
            Customer.PaymentTerm paymentTerm,
            Integer customPaymentDays,

            @DecimalMin(value = "0", message = "신용한도는 0 이상이어야 합니다")
            BigDecimal creditLimit,

            @DecimalMin(value = "0", message = "할인율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "할인율은 100 이하여야 합니다")
            BigDecimal discountRate,

            @DecimalMin(value = "0", message = "세율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "세율은 100 이하여야 합니다")
            BigDecimal taxRate,

            // 추가 정보
            @Size(max = 500, message = "태그는 500자 이내여야 합니다")
            String tags,

            Integer sortOrder,
            String metadata
    ) {
        public CustomerCreateDto {
            if (customerStatus == null) customerStatus = Customer.CustomerStatus.PROSPECT;
            if (customerGrade == null) customerGrade = Customer.CustomerGrade.GENERAL;
            if (isActive == null) isActive = true;
            if (country == null || country.trim().isEmpty()) country = "대한민국";
            if (paymentTerm == null) paymentTerm = Customer.PaymentTerm.NET_30;
            if (creditLimit == null) creditLimit = BigDecimal.ZERO;
            if (discountRate == null) discountRate = BigDecimal.ZERO;
            if (taxRate == null) taxRate = new BigDecimal("10.00");
            if (sortOrder == null) sortOrder = 0;
        }
    }

    /**
     * 고객 수정 요청 DTO
     */
    public record CustomerUpdateDto(
            @Size(max = 200, message = "고객명은 200자 이내여야 합니다")
            String customerName,

            @Size(max = 200, message = "영문 고객명은 200자 이내여야 합니다")
            String customerNameEn,

            Customer.CustomerType customerType,
            Customer.CustomerStatus customerStatus,
            Customer.CustomerGrade customerGrade,
            Boolean isActive,

            // 사업자 정보
            @Size(max = 20, message = "사업자등록번호는 20자 이내여야 합니다")
            String businessRegistrationNumber,
            
            @Size(max = 100, message = "대표자명은 100자 이내여야 합니다")
            String representativeName,

            @Size(max = 100, message = "업종은 100자 이내여야 합니다")
            String businessType,

            @Size(max = 100, message = "업태는 100자 이내여야 합니다")
            String businessItem,

            // 연락처 정보
            @Size(max = 20, message = "전화번호는 20자 이내여야 합니다")
            String phoneNumber,

            @Size(max = 20, message = "팩스번호는 20자 이내여야 합니다")
            String faxNumber,

            @Email(message = "올바른 이메일 형식이어야 합니다")
            @Size(max = 100, message = "이메일은 100자 이내여야 합니다")
            String email,

            @Size(max = 200, message = "웹사이트는 200자 이내여야 합니다")
            String website,

            // 주소 정보
            @Size(max = 10, message = "우편번호는 10자 이내여야 합니다")
            String postalCode,

            @Size(max = 200, message = "주소는 200자 이내여야 합니다")
            String address,

            @Size(max = 200, message = "상세주소는 200자 이내여야 합니다")
            String addressDetail,

            @Size(max = 100, message = "시/도는 100자 이내여야 합니다")
            String city,

            @Size(max = 100, message = "구/군은 100자 이내여야 합니다")
            String district,

            @Size(max = 100, message = "국가는 100자 이내여야 합니다")
            String country,

            // 영업 관리 정보
            Long salesManagerId,
            String salesManagerName,
            LocalDate lastContactDate,
            
            @Size(max = 1000, message = "고객 설명은 1000자 이내여야 합니다")
            String description,

            // 거래 조건
            Customer.PaymentTerm paymentTerm,
            Integer customPaymentDays,

            @DecimalMin(value = "0", message = "신용한도는 0 이상이어야 합니다")
            BigDecimal creditLimit,

            @DecimalMin(value = "0", message = "할인율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "할인율은 100 이하여야 합니다")
            BigDecimal discountRate,

            @DecimalMin(value = "0", message = "세율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "세율은 100 이하여야 합니다")
            BigDecimal taxRate,

            // 추가 정보
            @Size(max = 500, message = "태그는 500자 이내여야 합니다")
            String tags,

            Integer sortOrder,
            String metadata
    ) {}

    /**
     * 고객 응답 DTO
     */
    public record CustomerResponseDto(
            Long id,
            String customerCode,
            String customerName,
            String customerNameEn,
            Long companyId,
            String companyName,
            Customer.CustomerType customerType,
            String customerTypeDescription,
            Customer.CustomerStatus customerStatus,
            String customerStatusDescription,
            Customer.CustomerGrade customerGrade,
            String customerGradeDescription,
            Boolean isActive,

            // 사업자 정보
            String businessRegistrationNumber,
            String representativeName,
            String businessType,
            String businessItem,

            // 연락처 정보
            String phoneNumber,
            String faxNumber,
            String email,
            String website,

            // 주소 정보
            String postalCode,
            String address,
            String addressDetail,
            String city,
            String district,
            String country,
            String fullAddress,

            // 영업 관리 정보
            Long salesManagerId,
            String salesManagerName,
            LocalDate firstContactDate,
            LocalDate lastContactDate,
            String description,

            // 거래 조건
            Customer.PaymentTerm paymentTerm,
            String paymentTermDescription,
            Integer customPaymentDays,
            BigDecimal creditLimit,
            BigDecimal discountRate,
            BigDecimal taxRate,

            // 통계 정보
            Integer totalOrderCount,
            BigDecimal totalOrderAmount,
            LocalDate lastOrderDate,
            BigDecimal averageOrderAmount,
            BigDecimal outstandingAmount,

            // 추가 정보
            String tags,
            Integer sortOrder,
            String metadata,
            String createdAt,
            String updatedAt,

            // 계산 필드
            Boolean isVipCustomer,
            Boolean isActiveCustomer,
            Boolean isCreditLimitExceeded,
            String customerSummary
    ) {}

    /**
     * 고객 요약 DTO (목록용)
     */
    public record CustomerSummaryDto(
            Long id,
            String customerCode,
            String customerName,
            Customer.CustomerType customerType,
            String customerTypeDescription,
            Customer.CustomerStatus customerStatus,
            String customerStatusDescription,
            Customer.CustomerGrade customerGrade,
            String customerGradeDescription,
            Boolean isActive,
            String phoneNumber,
            String email,
            String salesManagerName,
            Integer totalOrderCount,
            BigDecimal totalOrderAmount,
            LocalDate lastOrderDate,
            BigDecimal outstandingAmount,
            String createdAt,
            Boolean isVipCustomer,
            Boolean isCreditLimitExceeded
    ) {}

    /**
     * 고객 검색 DTO
     */
    public record CustomerSearchDto(
            String searchTerm,
            Customer.CustomerType customerType,
            Customer.CustomerStatus customerStatus,
            Customer.CustomerGrade customerGrade,
            Boolean isActive,
            Long salesManagerId,
            String city,
            String businessType,
            LocalDate contactDateFrom,
            LocalDate contactDateTo,
            LocalDate orderDateFrom,
            LocalDate orderDateTo,
            BigDecimal orderAmountFrom,
            BigDecimal orderAmountTo,
            Boolean hasOutstanding,
            Boolean isCreditLimitExceeded,
            String tags
    ) {}

    /**
     * 고객 통계 DTO
     */
    public record CustomerStatsDto(
            Long totalCustomers,
            Long activeCustomers,
            Long inactiveCustomers,
            Long vipCustomers,
            Long prospectCustomers,
            Long dormantCustomers,
            BigDecimal totalSalesAmount,
            BigDecimal averageSalesAmount,
            BigDecimal totalOutstandingAmount,
            Long customersWithOutstanding,
            Long customersOverCreditLimit,
            Double averageOrdersPerCustomer,
            BigDecimal averageOrderAmount
    ) {}

    /**
     * 고객별 거래내역 DTO
     */
    public record CustomerTransactionDto(
            Long customerId,
            String customerCode,
            String customerName,
            String transactionType,
            String referenceNumber,
            LocalDate transactionDate,
            BigDecimal amount,
            String description,
            String status
    ) {}

    /**
     * 고객 연락처 업데이트 DTO
     */
    public record CustomerContactUpdateDto(
            @Size(max = 20, message = "전화번호는 20자 이내여야 합니다")
            String phoneNumber,

            @Size(max = 20, message = "팩스번호는 20자 이내여야 합니다")
            String faxNumber,

            @Email(message = "올바른 이메일 형식이어야 합니다")
            @Size(max = 100, message = "이메일은 100자 이내여야 합니다")
            String email,

            @Size(max = 200, message = "웹사이트는 200자 이내여야 합니다")
            String website,

            LocalDate lastContactDate
    ) {}

    /**
     * 고객 주소 업데이트 DTO
     */
    public record CustomerAddressUpdateDto(
            @Size(max = 10, message = "우편번호는 10자 이내여야 합니다")
            String postalCode,

            @Size(max = 200, message = "주소는 200자 이내여야 합니다")
            String address,

            @Size(max = 200, message = "상세주소는 200자 이내여야 합니다")
            String addressDetail,

            @Size(max = 100, message = "시/도는 100자 이내여야 합니다")
            String city,

            @Size(max = 100, message = "구/군은 100자 이내여야 합니다")
            String district,

            @Size(max = 100, message = "국가는 100자 이내여야 합니다")
            String country
    ) {}

    /**
     * 고객 거래조건 업데이트 DTO
     */
    public record CustomerTermsUpdateDto(
            Customer.PaymentTerm paymentTerm,
            Integer customPaymentDays,

            @DecimalMin(value = "0", message = "신용한도는 0 이상이어야 합니다")
            BigDecimal creditLimit,

            @DecimalMin(value = "0", message = "할인율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "할인율은 100 이하여야 합니다")
            BigDecimal discountRate,

            @DecimalMin(value = "0", message = "세율은 0 이상이어야 합니다")
            @DecimalMax(value = "100", message = "세율은 100 이하여야 합니다")
            BigDecimal taxRate
    ) {}

    /**
     * 고객 등급 변경 DTO
     */
    public record CustomerGradeChangeDto(
            @NotNull(message = "고객등급은 필수입니다")
            Customer.CustomerGrade customerGrade,

            @Size(max = 500, message = "변경사유는 500자 이내여야 합니다")
            String reason
    ) {}

    /**
     * 고객 상태 변경 DTO
     */
    public record CustomerStatusChangeDto(
            @NotNull(message = "고객상태는 필수입니다")
            Customer.CustomerStatus customerStatus,

            @Size(max = 500, message = "변경사유는 500자 이내여야 합니다")
            String reason
    ) {}
}
