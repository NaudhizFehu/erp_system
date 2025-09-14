package com.erp.common.dto;

import com.erp.common.entity.Company;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * 회사 정보 DTO
 */
public record CompanyDto(
    Long id,
    String companyCode,
    String name,
    String nameEn,
    String businessNumber,
    String corporationNumber,
    String ceoName,
    String businessType,
    String businessItem,
    String address,
    String addressDetail,
    String postalCode,
    String phone,
    String fax,
    String email,
    String website,
    LocalDate establishedDate,
    String status,
    String companyType,
    Integer employeeCount,
    BigDecimal capitalAmount,
    String description,
    String logoUrl
) {
    /**
     * Company 엔티티를 CompanyDto로 변환
     */
    public static CompanyDto from(Company company) {
        return new CompanyDto(
            company.getId(),
            company.getCompanyCode(),
            company.getName(),
            company.getNameEn(),
            company.getBusinessNumber(),
            company.getCorporationNumber(),
            company.getCeoName(),
            company.getBusinessType(),
            company.getBusinessItem(),
            company.getAddress(),
            company.getAddressDetail(),
            company.getPostalCode(),
            company.getPhone(),
            company.getFax(),
            company.getEmail(),
            company.getWebsite(),
            company.getEstablishedDate(),
            company.getStatus() != null ? company.getStatus().name() : null,
            company.getCompanyType() != null ? company.getCompanyType().name() : null,
            company.getEmployeeCount(),
            company.getCapitalAmount(),
            company.getDescription(),
            company.getLogoUrl()
        );
    }
}