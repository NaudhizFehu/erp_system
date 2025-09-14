package com.erp.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 공통코드 엔티티
 * 시스템에서 사용하는 공통코드를 관리합니다
 */
@Entity
@Table(name = "codes", indexes = {
    @Index(name = "idx_codes_group_code", columnList = "group_code"),
    @Index(name = "idx_codes_code_value", columnList = "code_value"),
    @Index(name = "idx_codes_parent", columnList = "parent_code_id"),
    @Index(name = "idx_codes_company", columnList = "company_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_codes_group_value", columnNames = {"group_code", "code_value"})
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"parentCode", "subCodes"})
@NoArgsConstructor
@AllArgsConstructor
public class Code extends BaseEntity {

    /**
     * 그룹 코드 (코드 분류)
     */
    @NotBlank(message = "그룹 코드는 필수입니다")
    @Size(max = 50, message = "그룹 코드는 50자 이하여야 합니다")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "그룹 코드는 대문자, 숫자, 언더스코어만 사용 가능합니다")
    @Column(name = "group_code", nullable = false, length = 50)
    private String groupCode;

    /**
     * 코드 값
     */
    @NotBlank(message = "코드 값은 필수입니다")
    @Size(max = 50, message = "코드 값은 50자 이하여야 합니다")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "코드 값은 대문자, 숫자, 언더스코어만 사용 가능합니다")
    @Column(name = "code_value", nullable = false, length = 50)
    private String codeValue;

    /**
     * 코드명 (한글)
     */
    @NotBlank(message = "코드명은 필수입니다")
    @Size(max = 100, message = "코드명은 100자 이하여야 합니다")
    @Column(name = "code_name", nullable = false, length = 100)
    private String codeName;

    /**
     * 코드명 (영문)
     */
    @Size(max = 100, message = "영문 코드명은 100자 이하여야 합니다")
    @Column(name = "code_name_en", length = 100)
    private String codeNameEn;

    /**
     * 코드 설명
     */
    @Size(max = 500, message = "코드 설명은 500자 이하여야 합니다")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 상위 코드
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_code_id")
    private Code parentCode;

    /**
     * 하위 코드들
     */
    @OneToMany(mappedBy = "parentCode", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Code> subCodes;

    /**
     * 정렬 순서
     */
    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /**
     * 코드 레벨 (최상위: 1)
     */
    @Min(value = 1, message = "코드 레벨은 1 이상이어야 합니다")
    @Column(name = "level", nullable = false)
    private Integer level = 1;

    /**
     * 사용 여부
     */
    @NotNull(message = "사용 여부는 필수입니다")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 시스템 코드 여부 (시스템에서 사용하는 필수 코드)
     */
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    /**
     * 수정 가능 여부
     */
    @Column(name = "is_editable", nullable = false)
    private Boolean isEditable = true;

    /**
     * 회사별 코드 여부 (null이면 전체 공통)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    /**
     * 코드 타입
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "code_type", length = 20)
    private CodeType codeType = CodeType.COMMON;

    /**
     * 추가 속성 1
     */
    @Size(max = 100, message = "추가 속성1은 100자 이하여야 합니다")
    @Column(name = "attribute1", length = 100)
    private String attribute1;

    /**
     * 추가 속성 2
     */
    @Size(max = 100, message = "추가 속성2는 100자 이하여야 합니다")
    @Column(name = "attribute2", length = 100)
    private String attribute2;

    /**
     * 추가 속성 3
     */
    @Size(max = 100, message = "추가 속성3은 100자 이하여야 합니다")
    @Column(name = "attribute3", length = 100)
    private String attribute3;

    /**
     * 숫자 속성 1
     */
    @Column(name = "numeric_attribute1")
    private java.math.BigDecimal numericAttribute1;

    /**
     * 숫자 속성 2
     */
    @Column(name = "numeric_attribute2")
    private java.math.BigDecimal numericAttribute2;

    /**
     * 날짜 속성 1
     */
    @Column(name = "date_attribute1")
    private java.time.LocalDate dateAttribute1;

    /**
     * 날짜 속성 2
     */
    @Column(name = "date_attribute2")
    private java.time.LocalDate dateAttribute2;

    /**
     * 코드 타입 열거형
     */
    public enum CodeType {
        COMMON("공통코드"),
        BUSINESS("업무코드"),
        SYSTEM("시스템코드"),
        USER_DEFINED("사용자정의코드");

        private final String description;

        CodeType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 코드가 활성 상태인지 확인
     */
    public boolean isActiveCode() {
        return isActive && !getIsDeleted();
    }

    /**
     * 최상위 코드인지 확인
     */
    public boolean isRootCode() {
        return parentCode == null;
    }

    /**
     * 하위 코드가 있는지 확인
     */
    public boolean hasSubCodes() {
        return subCodes != null && !subCodes.isEmpty();
    }

    /**
     * 시스템 필수 코드인지 확인
     */
    public boolean isSystemCode() {
        return isSystem;
    }

    /**
     * 수정 가능한 코드인지 확인
     */
    public boolean isEditableCode() {
        return isEditable && !isSystem;
    }

    /**
     * 전사 공통 코드인지 확인
     */
    public boolean isGlobalCode() {
        return company == null;
    }

    /**
     * 회사별 코드인지 확인
     */
    public boolean isCompanySpecificCode() {
        return company != null;
    }

    /**
     * 코드 전체 경로 생성 (상위코드 > 현재코드)
     */
    public String getFullPath() {
        if (parentCode == null) {
            return codeName;
        }
        return parentCode.getFullPath() + " > " + codeName;
    }

    /**
     * 코드 전체 값 경로 생성 (상위코드값.현재코드값)
     */
    public String getFullCodeValue() {
        if (parentCode == null) {
            return codeValue;
        }
        return parentCode.getFullCodeValue() + "." + codeValue;
    }

    @PrePersist
    @PreUpdate
    private void validateHierarchy() {
        if (parentCode != null) {
            // 자기 자신을 상위 코드로 설정할 수 없음
            if (parentCode.getId() != null && parentCode.getId().equals(this.getId())) {
                throw new IllegalArgumentException("코드는 자기 자신을 상위 코드로 설정할 수 없습니다");
            }
            
            // 레벨 자동 설정
            this.level = parentCode.getLevel() + 1;
            
            // 같은 그룹 코드여야 함
            if (!parentCode.getGroupCode().equals(this.groupCode)) {
                throw new IllegalArgumentException("상위 코드와 같은 그룹 코드여야 합니다");
            }
            
            // 같은 회사 소속이어야 함 (전사 공통 코드 제외)
            if (parentCode.getCompany() != null && this.company != null &&
                !parentCode.getCompany().getId().equals(this.company.getId())) {
                throw new IllegalArgumentException("상위 코드와 같은 회사 소속이어야 합니다");
            }
        }
    }
}
