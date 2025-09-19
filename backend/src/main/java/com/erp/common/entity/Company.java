package com.erp.common.entity;

import com.erp.hr.entity.Department;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 회사 엔티티
 * 시스템에서 관리하는 회사 정보를 저장합니다
 */
@Entity
@Table(name = "companies", indexes = {
    @Index(name = "idx_companies_code", columnList = "company_code"),
    @Index(name = "idx_companies_business_number", columnList = "business_number"),
    @Index(name = "idx_companies_name", columnList = "name")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Company extends BaseEntity {

    /**
     * 회사 코드 (고유 식별자)
     */
    @NotBlank(message = "회사 코드는 필수입니다")
    @Size(max = 20, message = "회사 코드는 20자 이하여야 합니다")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "회사 코드는 대문자, 숫자, 언더스코어만 사용 가능합니다")
    @Column(name = "company_code", unique = true, nullable = false, length = 20)
    private String companyCode;

    /**
     * 회사명
     */
    @NotBlank(message = "회사명은 필수입니다")
    @Size(max = 200, message = "회사명은 200자 이하여야 합니다")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * 회사명 (영문)
     */
    @Size(max = 200, message = "영문 회사명은 200자 이하여야 합니다")
    @Column(name = "name_en", length = 200)
    private String nameEn;

    /**
     * 사업자등록번호
     */
    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$", message = "올바른 사업자등록번호 형식이어야 합니다 (000-00-00000)")
    @Column(name = "business_number", unique = true, length = 12)
    private String businessNumber;

    /**
     * 법인등록번호
     */
    @Pattern(regexp = "^\\d{6}-\\d{7}$", message = "올바른 법인등록번호 형식이어야 합니다 (000000-0000000)")
    @Column(name = "corporation_number", length = 14)
    private String corporationNumber;

    /**
     * 대표자명
     */
    @Size(max = 50, message = "대표자명은 50자 이하여야 합니다")
    @Column(name = "ceo_name", length = 50)
    private String ceoName;

    /**
     * 업종
     */
    @Size(max = 100, message = "업종은 100자 이하여야 합니다")
    @Column(name = "business_type", length = 100)
    private String businessType;

    /**
     * 업태
     */
    @Size(max = 100, message = "업태는 100자 이하여야 합니다")
    @Column(name = "business_item", length = 100)
    private String businessItem;

    /**
     * 본사 주소
     */
    @Size(max = 500, message = "주소는 500자 이하여야 합니다")
    @Column(name = "address", length = 500)
    private String address;

    /**
     * 상세 주소
     */
    @Size(max = 200, message = "상세 주소는 200자 이하여야 합니다")
    @Column(name = "detailed_address", length = 200)
    private String addressDetail;

    /**
     * 우편번호
     */
    @Pattern(regexp = "^\\d{5}$", message = "올바른 우편번호 형식이어야 합니다 (00000)")
    @Column(name = "postal_code", length = 5)
    private String postalCode;

    /**
     * 대표 전화번호
     */
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다")
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 팩스번호
     */
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 팩스번호 형식이어야 합니다")
    @Column(name = "fax", length = 20)
    private String fax;

    /**
     * 이메일
     */
    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 홈페이지 URL
     */
    @Size(max = 200, message = "홈페이지 URL은 200자 이하여야 합니다")
    @Column(name = "website", length = 200)
    private String website;

    // established_date 필드는 실제 DB 스키마에 없으므로 제거됨

    /**
     * 회사 상태
     */
    @NotNull(message = "회사 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CompanyStatus status = CompanyStatus.ACTIVE;

    // company_type, employee_count, capital_amount, description, logo_url 필드들은 
    // 실제 DB 스키마에 없으므로 제거됨

    /**
     * 회사 소속 부서들
     */
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Department> departments;

    /**
     * 회사 소속 직원들
     */
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<com.erp.hr.entity.Employee> employees;

    /**
     * 회사 소속 사용자들
     */
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<User> users;

    /**
     * 회사 상태 열거형
     */
    public enum CompanyStatus {
        ACTIVE("활성"),
        INACTIVE("비활성"),
        SUSPENDED("정지"),
        CLOSED("폐업");

        private final String description;

        CompanyStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // CompanyType enum은 실제 DB 스키마에 company_type 컬럼이 없으므로 제거됨

    /**
     * 회사가 활성 상태인지 확인
     */
    public boolean isActive() {
        return status == CompanyStatus.ACTIVE && !getIsDeleted();
    }
}
