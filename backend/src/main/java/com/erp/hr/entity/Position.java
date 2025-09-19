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
    // position_category 인덱스 제거됨 (DB 스키마에 해당 컬럼이 없음)
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

    // nameEn 필드 제거됨 (데이터베이스 스키마에 없음)

    /**
     * 직급 설명
     */
    @Size(max = 500, message = "직급 설명은 500자 이하여야 합니다")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 직급 레벨 (1: 최고위, 숫자가 클수록 하위직급)
     */
    @NotNull(message = "직급 레벨은 필수입니다")
    @Min(value = 1, message = "직급 레벨은 1 이상이어야 합니다")
    @Max(value = 20, message = "직급 레벨은 20 이하여야 합니다")
    @Column(name = "level", nullable = false)
    private Integer level = 1;

    /**
     * 소속 회사
     */
    @NotNull(message = "소속 회사는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Department 참조 제거됨

    // position_category 필드 제거됨 (DB 스키마에 해당 컬럼이 없음)

    // positionType 필드 제거됨 (DB 스키마에 해당 컬럼이 없음)

    // min_salary, max_salary 필드들이 데이터베이스에 존재하지 않아 제거됨

    // sort_order 필드 제거됨 (DB 스키마에 해당 컬럼이 없음)

    /**
     * 사용 여부
     */
    @NotNull(message = "사용 여부는 필수입니다")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // promotion_targets, requirements 필드 제거됨 (DB 스키마에 해당 컬럼이 없음)


    /**
     * 해당 직급의 직원들
     */
    @OneToMany(mappedBy = "position", fetch = FetchType.LAZY)
    private List<Employee> employees;

    // PositionCategory enum 제거됨 (DB 스키마에 position_category 컬럼이 없음)

    // PositionType enum 제거됨 (DB 스키마에 position_type 컬럼이 없음)

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
        return true; // minSalary, maxSalary 필드가 제거되어 항상 유효
    }

    // getPromotionTargetArray 메서드 제거됨 (promotionTargets 필드가 제거됨)

    /**
     * 해당 직급의 직원 수 반환
     */
    public int getEmployeeCount() {
        return employees != null ? employees.size() : 0;
    }

    @PrePersist
    @PreUpdate
    private void validateSalaryRange() {
        // minSalary, maxSalary 필드가 제거되어 검증 로직 제거
    }

}