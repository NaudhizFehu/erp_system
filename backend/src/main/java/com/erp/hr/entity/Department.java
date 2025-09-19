package com.erp.hr.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 부서 엔티티
 * 인사관리 시스템의 부서 정보를 관리합니다
 */
@Entity
@Table(name = "departments")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Department extends BaseEntity {

    @NotBlank(message = "부서코드는 필수입니다")
    @Size(max = 20, message = "부서코드는 20자 이하여야 합니다")
    @Column(name = "department_code", unique = true, nullable = false)
    private String departmentCode;

    @NotBlank(message = "부서명은 필수입니다")
    @Size(max = 100, message = "부서명은 100자 이하여야 합니다")
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 100, message = "영문 부서명은 100자 이하여야 합니다")
    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Size(max = 500, message = "부서 설명은 500자 이하여야 합니다")
    @Column(name = "description")
    private String description;

    @NotNull(message = "소속 회사는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotNull(message = "부서 레벨은 필수입니다")
    @Min(value = 1, message = "부서 레벨은 1 이상이어야 합니다")
    @Max(value = 10, message = "부서 레벨은 10 이하여야 합니다")
    @Column(name = "level", nullable = false)
    private Integer level = 1;

    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_department_id")
    private Department parentDepartment;

    @OneToMany(mappedBy = "parentDepartment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Department> subDepartments = new ArrayList<>();

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();

    // Position 관계 제거됨

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @Enumerated(EnumType.STRING)
    @Column(name = "department_type", nullable = false)
    private DepartmentType departmentType = DepartmentType.DEPARTMENT;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DepartmentStatus status = DepartmentStatus.ACTIVE;

    /**
     * 부서 유형 열거형
     */
    public enum DepartmentType {
        HEADQUARTERS("본사"),
        BRANCH("지점"),
        DEPARTMENT("부서"),
        TEAM("팀"),
        DIVISION("사업부");

        private final String description;

        DepartmentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 부서 상태 열거형
     */
    public enum DepartmentStatus {
        ACTIVE("활성"),
        INACTIVE("비활성"),
        DISSOLVED("해체");

        private final String description;

        DepartmentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 부서가 활성 상태인지 확인
     */
    public boolean isActive() {
        return status == DepartmentStatus.ACTIVE && !getIsDeleted();
    }
}
