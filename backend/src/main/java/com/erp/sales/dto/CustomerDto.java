package com.erp.sales.dto;

import com.erp.sales.entity.Customer;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 고객 관련 DTO 클래스
 */
public class CustomerDto {

    /**
     * 고객 생성 DTO
     */
    public record CustomerCreateDto(
            @NotNull(message = "회사 ID는 필수입니다")
            Long companyId,
            
            @NotBlank(message = "고객코드는 필수입니다")
            @Size(max = 20, message = "고객코드는 20자 이내여야 합니다")
            String customerCode,
            
            @NotBlank(message = "고객명은 필수입니다")
            @Size(max = 200, message = "고객명은 200자 이내여야 합니다")
            String customerName,
            
            @NotNull(message = "고객유형은 필수입니다")
            Customer.CustomerType customerType,
            
            @Email(message = "올바른 이메일 형식이어야 합니다")
            @Size(max = 100, message = "이메일은 100자 이내여야 합니다")
            String email,
            
            @Size(max = 20, message = "전화번호는 20자 이내여야 합니다")
            String phoneNumber,
            
            @Size(max = 200, message = "주소는 200자 이내여야 합니다")
            String address,
            
            Customer.CustomerStatus customerStatus,
            
            Customer.CustomerGrade customerGrade,
            
            @Size(max = 12, message = "사업자등록번호는 12자 이내여야 합니다")
            String businessRegistrationNumber,
            
            @Size(max = 50, message = "대표자명은 50자 이내여야 합니다")
            String ceoName,
            
            Long salesManagerId,
            
            @DecimalMin(value = "0.0", message = "신용한도는 0 이상이어야 합니다")
            BigDecimal creditLimit,
            
            @Size(max = 50, message = "결제조건은 50자 이내여야 합니다")
            String paymentTerms
    ) {
        public Customer toEntity() {
            return Customer.builder()
                    .customerCode(customerCode)
                    .customerName(customerName)
                    .customerType(customerType)
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .address(address)
                    .customerStatus(customerStatus != null ? customerStatus : Customer.CustomerStatus.ACTIVE)
                    .customerGrade(customerGrade)
                    .businessRegistrationNumber(businessRegistrationNumber)
                    .ceoName(ceoName)
                    .creditLimit(creditLimit)
                    .paymentTerms(paymentTerms)
                    .build();
        }
    }

    /**
     * 고객 수정 DTO
     */
    public record CustomerUpdateDto(
            String customerName,
            String email,
            String phoneNumber,
            String address,
            Customer.CustomerStatus customerStatus,
            Customer.CustomerGrade customerGrade,
            String businessRegistrationNumber,
            String ceoName,
            Long salesManagerId,
            BigDecimal creditLimit,
            String paymentTerms
    ) {
        public void updateEntity(Customer customer) {
            if (customerName != null) customer.setCustomerName(customerName);
            if (email != null) customer.setEmail(email);
            if (phoneNumber != null) customer.setPhoneNumber(phoneNumber);
            if (address != null) customer.setAddress(address);
            if (customerStatus != null) customer.setCustomerStatus(customerStatus);
            if (customerGrade != null) customer.setCustomerGrade(customerGrade);
            if (businessRegistrationNumber != null) customer.setBusinessRegistrationNumber(businessRegistrationNumber);
            if (ceoName != null) customer.setCeoName(ceoName);
            if (creditLimit != null) customer.setCreditLimit(creditLimit);
            if (paymentTerms != null) customer.setPaymentTerms(paymentTerms);
        }
    }

    /**
     * 고객 응답 DTO
     */
    public record CustomerResponseDto(
            Long id,
            String customerCode,
            String customerName,
            Long companyId,
            String companyName,
            Customer.CustomerType customerType,
            String customerTypeDescription,
            Customer.CustomerStatus customerStatus,
            String customerStatusDescription,
            Customer.CustomerGrade customerGrade,
            String customerGradeDescription,
            String businessRegistrationNumber,
            String ceoName,
            Long salesManagerId,
            String salesManagerName,
            BigDecimal creditLimit,
            String paymentTerms,
            String email,
            String phoneNumber,
            String address,
            String fullAddress,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static CustomerResponseDto from(Customer customer) {
            return new CustomerResponseDto(
                    customer.getId(),
                    customer.getCustomerCode(),
                    customer.getCustomerName(),
                    customer.getCompany() != null ? customer.getCompany().getId() : null,
                    customer.getCompany() != null ? customer.getCompany().getName() : null,
                    customer.getCustomerType(),
                    customer.getCustomerTypeDescription(),
                    customer.getCustomerStatus(),
                    customer.getCustomerStatusDescription(),
                    customer.getCustomerGrade(),
                    customer.getCustomerGradeDescription(),
                    customer.getBusinessRegistrationNumber(),
                    customer.getCeoName(),
                    customer.getSalesManager() != null ? customer.getSalesManager().getId() : null,
                    customer.getSalesManager() != null ? customer.getSalesManager().getName() : null,
                    customer.getCreditLimit(),
                    customer.getPaymentTerms(),
                    customer.getEmail(),
                    customer.getPhoneNumber(),
                    customer.getAddress(),
                    customer.getFullAddress(),
                    customer.getCreatedAt(),
                    customer.getUpdatedAt()
            );
        }
    }

    /**
     * 고객 요약 DTO
     */
    public record CustomerSummaryDto(
            Long id,
            String customerCode,
            String customerName,
            Customer.CustomerType customerType,
            Customer.CustomerStatus customerStatus,
            Customer.CustomerGrade customerGrade,
            String email,
            String phoneNumber,
            String companyName
    ) {
        public static CustomerSummaryDto from(Customer customer) {
            return new CustomerSummaryDto(
                    customer.getId(),
                    customer.getCustomerCode(),
                    customer.getCustomerName(),
                    customer.getCustomerType(),
                    customer.getCustomerStatus(),
                    customer.getCustomerGrade(),
                    customer.getEmail(),
                    customer.getPhoneNumber(),
                    customer.getCompany() != null ? customer.getCompany().getName() : null
            );
        }
    }

    /**
     * 고객 검색 DTO
     */
    public record CustomerSearchDto(
            String searchTerm,
            Customer.CustomerType customerType,
            Customer.CustomerStatus customerStatus,
            Customer.CustomerGrade customerGrade,
            Long companyId
    ) {}

    /**
     * 고객 통계 DTO
     */
    public record CustomerStatsDto(
            Long totalCustomers,
            Long activeCustomers,
            Long newCustomers,
            Long totalSalesAmount,
            Long averageSalesAmount,
            Long totalOutstandingAmount,
            Long customersWithOutstanding,
            Long customersOverCreditLimit
    ) {}

    /**
     * 고객 거래내역 DTO
     */
    public record CustomerTransactionDto(
            Long id,
            String transactionNumber,
            String description,
            String amount,
            LocalDateTime transactionDate
    ) {}

    /**
     * 고객 연락처 업데이트 DTO
     */
    public record CustomerContactUpdateDto(
            String phoneNumber,
            String email
    ) {}

    /**
     * 고객 주소 업데이트 DTO
     */
    public record CustomerAddressUpdateDto(
            String address
    ) {}

    /**
     * 고객 거래조건 업데이트 DTO
     */
    public record CustomerTermsUpdateDto(
            BigDecimal creditLimit,
            String paymentTerms
    ) {}

    /**
     * 고객 상태 변경 DTO
     */
    public record CustomerStatusChangeDto(
            Customer.CustomerStatus customerStatus,
            String reason
    ) {}

    /**
     * 고객 등급 변경 DTO
     */
    public record CustomerGradeChangeDto(
            Customer.CustomerGrade customerGrade,
            String reason
    ) {}
}