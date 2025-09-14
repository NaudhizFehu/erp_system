package com.erp.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 부서 엔티티
 * 조직의 부서 구조를 관리합니다
 */
@Entity
@Table(name = "departments")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"company", "parentDepartment", "subDepartments", "users", "manager"})
@NoArgsConstructor
@AllArgsConstructor
public class Department extends BaseEntity {

    /**
     * 부서 코드 (고유 식별자)
     */
    @NotBlank(message = "부서 코드는 필수입니다")
    @Size(max = 20, message = "부서 코드는 20자 이하여야 합니다")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "부서 코드는 대문자, 숫자, 언더스코어만 사용 가능합니다")
    @Column(name = "department_code", nullable = false, length = 20)
    private String departmentCode;

    /**
     * 부서명
     */
    @NotBlank(message = "부서명은 필수입니다")
    @Size(max = 100, message = "부서명은 100자 이하여야 합니다")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 부서명 (영문)
     */
    @Size(max = 100, message = "영문 부서명은 100자 이하여야 합니다")
    @Column(name = "name_en", length = 100)
    private String nameEn;

    /**
     * 부서 설명
     */
    @Size(max = 500, message = "부서 설명은 500자 이하여야 합니다")
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
     * 상위 부서
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_department_id")
    private Department parentDepartment;

    /**
     * 하위 부서들
     */
    @OneToMany(mappedBy = "parentDepartment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Department> subDepartments;

    /**
     * 부서 관리자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    /**
     * 부서 소속 사용자들
     */
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<User> users;

    /**
     * 부서 레벨 (최상위: 1)
     */
    @Min(value = 1, message = "부서 레벨은 1 이상이어야 합니다")
    @Column(name = "level", nullable = false)
    private Integer level = 1;

    /**
     * 정렬 순서
     */
    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 부서 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "department_type", length = 20)
    private DepartmentType departmentType;

    /**
     * 부서 상태
     */
    @NotNull(message = "부서 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DepartmentStatus status = DepartmentStatus.ACTIVE;

    /**
     * 비용센터 코드
     */
    @Size(max = 20, message = "비용센터 코드는 20자 이하여야 합니다")
    @Column(name = "cost_center_code", length = 20)
    private String costCenterCode;

    /**
     * 부서 전화번호
     */
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다")
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 부서 팩스번호
     */
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 팩스번호 형식이어야 합니다")
    @Column(name = "fax", length = 20)
    private String fax;

    /**
     * 부서 이메일
     */
    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 부서 위치
     */
    @Size(max = 200, message = "부서 위치는 200자 이하여야 합니다")
    @Column(name = "location", length = 200)
    private String location;

    /**
     * 예산 금액
     */
    @Min(value = 0, message = "예산 금액은 0 이상이어야 합니다")
    @Column(name = "budget_amount", precision = 15, scale = 2)
    private java.math.BigDecimal budgetAmount;

    /**
     * 부서 유형 열거형
     */
    public enum DepartmentType {
        HEADQUARTERS("본사"),
        BRANCH("지사"),
        DIVISION("사업부"),
        DEPARTMENT("부서"),
        TEAM("팀"),
        OFFICE("사무소");

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
        REORGANIZING("조직개편중"),
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

    /**
     * 최상위 부서인지 확인
     */
    public boolean isRootDepartment() {
        return parentDepartment == null;
    }

    /**
     * 하위 부서가 있는지 확인
     */
    public boolean hasSubDepartments() {
        return subDepartments != null && !subDepartments.isEmpty();
    }

    /**
     * 부서 전체 경로 생성 (상위부서 > 현재부서)
     */
    public String getFullPath() {
        if (parentDepartment == null) {
            return name;
        }
        return parentDepartment.getFullPath() + " > " + name;
    }

    @PrePersist
    @PreUpdate
    private void validateHierarchy() {
        if (parentDepartment != null) {
            // 자기 자신을 상위 부서로 설정할 수 없음
            if (parentDepartment.getId() != null && parentDepartment.getId().equals(this.getId())) {
                throw new IllegalArgumentException("부서는 자기 자신을 상위 부서로 설정할 수 없습니다");
            }
            
            // 레벨 자동 설정
            this.level = parentDepartment.getLevel() + 1;
            
            // 같은 회사 소속이어야 함
            if (!parentDepartment.getCompany().getId().equals(this.company.getId())) {
                throw new IllegalArgumentException("상위 부서와 같은 회사 소속이어야 합니다");
            }
        }
    }
}
