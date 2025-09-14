package com.erp.hr.entity;

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
 * 직급 엔티티
 * 회사의 직급 체계를 관리합니다
 */
@Entity
@Table(name = "positions", indexes = {
    @Index(name = "idx_positions_code", columnList = "position_code"),
    @Index(name = "idx_positions_company", columnList = "company_id"),
    @Index(name = "idx_positions_level", columnList = "position_level"),
    @Index(name = "idx_positions_category", columnList = "position_category")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_positions_company_code", columnNames = {"company_id", "position_code"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Position extends BaseEntity {

    /**
     * 직급 코드
     */
    @NotBlank(message = "직급 코드는 필수입니다")
    @Size(max = 20, message = "직급 코드는 20자 이하여야 합니다")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "직급 코드는 대문자, 숫자, 언더스코어만 사용 가능합니다")
    @Column(name = "position_code", nullable = false, length = 20)
    private String positionCode;

    /**
     * 직급명
     */
    @NotBlank(message = "직급명은 필수입니다")
    @Size(max = 50, message = "직급명은 50자 이하여야 합니다")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 직급명 (영문)
     */
    @Size(max = 100, message = "영문 직급명은 100자 이하여야 합니다")
    @Column(name = "name_en", length = 100)
    private String nameEn;

    /**
     * 직급 설명
     */
    @Size(max = 500, message = "직급 설명은 500자 이하여야 합니다")
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
     * 직급 레벨 (1: 최고위, 숫자가 클수록 하위직급)
     */
    @NotNull(message = "직급 레벨은 필수입니다")
    @Min(value = 1, message = "직급 레벨은 1 이상이어야 합니다")
    @Max(value = 20, message = "직급 레벨은 20 이하여야 합니다")
    @Column(name = "position_level", nullable = false)
    private Integer positionLevel;

    /**
     * 직급 분류
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "position_category", length = 20)
    private PositionCategory positionCategory;

    /**
     * 직급 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "position_type", length = 20)
    private PositionType positionType;

    /**
     * 최소 기본급
     */
    @Min(value = 0, message = "최소 기본급은 0 이상이어야 합니다")
    @Column(name = "min_salary", precision = 12, scale = 2)
    private BigDecimal minSalary;

    /**
     * 최대 기본급
     */
    @Min(value = 0, message = "최대 기본급은 0 이상이어야 합니다")
    @Column(name = "max_salary", precision = 12, scale = 2)
    private BigDecimal maxSalary;

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
     * 승진 가능 직급들 (다음 단계 직급)
     */
    @Size(max = 200, message = "승진 가능 직급은 200자 이하여야 합니다")
    @Column(name = "promotion_targets", length = 200)
    private String promotionTargets;

    /**
     * 필요 자격 요건
     */
    @Size(max = 1000, message = "필요 자격 요건은 1000자 이하여야 합니다")
    @Column(name = "requirements", length = 1000)
    private String requirements;

    /**
     * 직무 권한
     */
    @Size(max = 1000, message = "직무 권한은 1000자 이하여야 합니다")
    @Column(name = "authorities", length = 1000)
    private String authorities;

    /**
     * 해당 직급의 직원들
     */
    @OneToMany(mappedBy = "position", fetch = FetchType.LAZY)
    private List<Employee> employees;

    /**
     * 직급 분류 열거형
     */
    public enum PositionCategory {
        EXECUTIVE("임원"),
        MANAGEMENT("관리직"),
        SENIOR("선임"),
        JUNIOR("주니어"),
        INTERN("인턴");

        private final String description;

        PositionCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 직급 유형 열거형
     */
    public enum PositionType {
        PERMANENT("정규직"),
        CONTRACT("계약직"),
        TEMPORARY("임시직"),
        CONSULTANT("컨설턴트");

        private final String description;

        PositionType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 직급이 활성 상태인지 확인
     */
    public boolean isActivePosition() {
        return isActive && !getIsDeleted();
    }

    /**
     * 급여 범위 검증
     */
    public boolean isValidSalaryRange(BigDecimal salary) {
        if (salary == null) {
            return true; // null은 허용
        }
        
        boolean aboveMin = minSalary == null || salary.compareTo(minSalary) >= 0;
        boolean belowMax = maxSalary == null || salary.compareTo(maxSalary) <= 0;
        
        return aboveMin && belowMax;
    }

    /**
     * 승진 가능 직급 목록 반환
     */
    public String[] getPromotionTargetArray() {
        if (promotionTargets == null || promotionTargets.trim().isEmpty()) {
            return new String[0];
        }
        return promotionTargets.split(",");
    }

    /**
     * 해당 직급의 직원 수 반환
     */
    public int getEmployeeCount() {
        return employees != null ? employees.size() : 0;
    }

    @PrePersist
    @PreUpdate
    private void validateSalaryRange() {
        if (minSalary != null && maxSalary != null && 
            minSalary.compareTo(maxSalary) > 0) {
            throw new IllegalArgumentException("최소 급여가 최대 급여보다 클 수 없습니다");
        }
    }
}