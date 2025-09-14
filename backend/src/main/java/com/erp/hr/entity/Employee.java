package com.erp.hr.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 직원 엔티티
 * 인사관리 시스템의 핵심 직원 정보를 관리합니다
 */
@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "직원번호는 필수입니다")
    @Size(max = 20, message = "직원번호는 20자 이하여야 합니다")
    @Column(name = "employee_number", unique = true, nullable = false)
    private String employeeNumber;

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다")
    @Column(name = "name", nullable = false)
    private String name;

    @Email(message = "올바른 이메일 형식이어야 합니다")
    @NotBlank(message = "이메일은 필수입니다")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이어야 합니다")
    @Column(name = "phone")
    private String phone;

    @NotNull(message = "입사일은 필수입니다")
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    @DecimalMin(value = "0", message = "급여는 0 이상이어야 합니다")
    @Column(name = "salary", precision = 12, scale = 2)
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    @Column(name = "address")
    private String address;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 직원 상태 열거형
     */
    public enum EmployeeStatus {
        ACTIVE("재직"),
        INACTIVE("휴직"),
        TERMINATED("퇴사");

        private final String description;

        EmployeeStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}

