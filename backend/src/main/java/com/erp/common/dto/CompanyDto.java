package com.erp.common.dto;

import com.erp.common.entity.Company;
// LocalDate, BigDecimal import는 더 이상 사용하지 않으므로 제거됨

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
    String status
    // establishedDate 필드는 실제 DB 스키마에 없으므로 제거됨
    // companyType, employeeCount, capitalAmount, description, logoUrl 필드들은 실제 DB 스키마에 없으므로 제거됨
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
            company.getStatus() != null ? company.getStatus().name() : null
            // establishedDate 필드는 실제 DB 스키마에 없으므로 제거됨
            // companyType, employeeCount, capitalAmount, description, logoUrl 필드들은 실제 DB 스키마에 없으므로 제거됨
        );
    }
}