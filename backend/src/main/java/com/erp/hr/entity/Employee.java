package com.erp.hr.entity;

import com.erp.common.entity.BaseEntity;
import com.erp.common.entity.Company;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * 직원 엔티티
 * 인사관리 시스템의 핵심 직원 정보를 관리합니다
 */
@Entity
@Table(name = "employees")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity {

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

    @NotNull(message = "소속 회사는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;



    @Size(max = 100, message = "영문명은 100자 이하여야 합니다")
    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Pattern(regexp = "^01[0-9]-[0-9]{4}-[0-9]{4}$", message = "올바른 휴대폰 번호 형식이어야 합니다")
    @Column(name = "mobile", length = 13)
    private String mobile;

    @Pattern(regexp = "^[0-9]{6}-[0-9]{7}$", message = "올바른 주민등록번호 형식이어야 합니다")
    @Column(name = "resident_number", length = 14)
    private String residentNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "address")
    private String address;


    @Pattern(regexp = "^[0-9]{5}$", message = "올바른 우편번호 형식이어야 합니다")
    @Column(name = "postal_code", length = 5)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false)
    private EmploymentStatus employmentStatus = EmploymentStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false)
    private EmploymentType employmentType = EmploymentType.FULL_TIME;

    @Min(value = 0, message = "급여는 0 이상이어야 합니다")
    @Column(name = "salary", nullable = true)
    private Long baseSalary;

    @Size(max = 50, message = "은행명은 50자 이하여야 합니다")
    @Column(name = "bank_name", length = 50)
    private String bankName;

    @Size(max = 20, message = "계좌번호는 20자 이하여야 합니다")
    @Column(name = "account_number", length = 20)
    private String accountNumber;

    @Size(max = 50, message = "예금주명은 50자 이하여야 합니다")
    @Column(name = "account_holder", length = 50)
    private String accountHolder;

    @Size(max = 13, message = "비상연락처는 13자 이하여야 합니다")
    @Column(name = "emergency_contact", length = 13)
    private String emergencyContact;

    @Size(max = 20, message = "비상연락처 관계는 20자 이하여야 합니다")
    @Column(name = "emergency_relation", length = 20)
    private String emergencyRelation;

    @Size(max = 100, message = "학력은 100자 이하여야 합니다")
    @Column(name = "education", length = 100)
    private String education;

    @Size(max = 100, message = "전공은 100자 이하여야 합니다")
    @Column(name = "major", length = 100)
    private String major;

    @Size(max = 500, message = "경력은 500자 이하여야 합니다")
    @Column(name = "career", length = 500)
    private String career;

    @Size(max = 500, message = "기술은 500자 이하여야 합니다")
    @Column(name = "skills", length = 500)
    private String skills;

    @Size(max = 500, message = "자격증은 500자 이하여야 합니다")
    @Column(name = "certifications", length = 500)
    private String certifications;

    @Size(max = 1000, message = "메모는 1000자 이하여야 합니다")
    @Column(name = "memo", length = 1000)
    private String memo;

    @Size(max = 500, message = "프로필 이미지 URL은 500자 이하여야 합니다")
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Size(max = 500, message = "퇴사 사유는 500자 이하여야 합니다")
    // terminationReason 필드 제거됨

    /**
     * 성별 열거형
     */
    public enum Gender {
        MALE("남성"),
        FEMALE("여성"),
        OTHER("기타");

        private final String description;

        Gender(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 고용 상태 열거형
     */
    public enum EmploymentStatus {
        ACTIVE("재직"),
        ON_LEAVE("휴가"),       // 추가 - 단기 휴가/연차
        INACTIVE("휴직"),       // 기존 - 장기 휴직
        SUSPENDED("정직"),      // 추가 - 징계
        TERMINATED("퇴직");     // 기존
        
        private final String description;
        
        EmploymentStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }

    /**
     * 고용 유형 열거형
     */
    public enum EmploymentType {
        FULL_TIME("정규직"),
        PART_TIME("계약직"),
        TEMPORARY("임시직"),
        INTERN("인턴");

        private final String description;

        EmploymentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
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

    /**
     * 직원 퇴사 처리
     */
    public void terminate(LocalDate terminationDate, String reason) {
        this.terminationDate = terminationDate;
        // terminationReason 필드 제거됨
        this.employmentStatus = EmploymentStatus.TERMINATED;
    }

    /**
     * 직원 재활성화
     */
    public void reactivate() {
        this.terminationDate = null;
        // terminationReason 필드 제거됨
        this.employmentStatus = EmploymentStatus.ACTIVE;
    }

    /**
     * 근속년수 계산
     */
    public Integer getYearsOfService() {
        if (hireDate == null) {
            return 0;
        }
        
        LocalDate endDate = terminationDate != null ? terminationDate : LocalDate.now();
        return (int) java.time.temporal.ChronoUnit.YEARS.between(hireDate, endDate);
    }

    /**
     * 나이 계산
     */
    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        
        return (int) java.time.temporal.ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }
}

