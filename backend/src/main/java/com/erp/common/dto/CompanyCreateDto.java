package com.erp.common.dto;

import com.erp.common.entity.Company;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
// LocalDate, BigDecimal import는 더 이상 사용하지 않으므로 제거됨

/**
 * 회사 생성 DTO
 * 새로운 회사 등록 시 사용됩니다
 */
public record CompanyCreateDto(
        @NotBlank(message = "회사 코드는 필수입니다")
        @Size(max = 20, message = "회사 코드는 20자 이하여야 합니다")
        @Pattern(regexp = "^[A-Z0-9_]+$", message = "회사 코드는 대문자, 숫자, 언더스코어만 사용 가능합니다")
        String companyCode,
        
        @NotBlank(message = "회사명은 필수입니다")
        @Size(max = 200, message = "회사명은 200자 이하여야 합니다")
        String name,
        
        @Size(max = 200, message = "영문 회사명은 200자 이하여야 합니다")
        String nameEn,
        
        @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$", message = "올바른 사업자등록번호 형식이어야 합니다 (000-00-00000)")
        String businessNumber,
        
        @Pattern(regexp = "^\\d{6}-\\d{7}$", message = "올바른 법인등록번호 형식이어야 합니다 (000000-0000000)")
        String corporationNumber,
        
        @Size(max = 50, message = "대표자명은 50자 이하여야 합니다")
        String ceoName,
        
        @Size(max = 100, message = "업종은 100자 이하여야 합니다")
        String businessType,
        
        @Size(max = 100, message = "업태는 100자 이하여야 합니다")
        String businessItem,
        
        @Size(max = 500, message = "주소는 500자 이하여야 합니다")
        String address,
        
        @Size(max = 200, message = "상세 주소는 200자 이하여야 합니다")
        String addressDetail,
        
        @Pattern(regexp = "^\\d{5}$", message = "올바른 우편번호 형식이어야 합니다 (00000)")
        String postalCode,
        
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다")
        String phone,
        
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 팩스번호 형식이어야 합니다")
        String fax,
        
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
        String email,
        
        @Size(max = 200, message = "홈페이지 URL은 200자 이하여야 합니다")
        String website,
        
        Company.CompanyStatus status
        // establishedDate 필드는 실제 DB 스키마에 없으므로 제거됨
        
        // companyType, employeeCount, capitalAmount, description, logoUrl 필드들은 실제 DB 스키마에 없으므로 제거됨
) {
    public CompanyCreateDto {
        if (companyCode == null || companyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("회사 코드는 필수입니다");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("회사명은 필수입니다");
        }
        
        // 기본값 설정
        if (status == null) {
            status = Company.CompanyStatus.ACTIVE;
        }
    }
}




