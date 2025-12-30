package com.erp.config;

import com.erp.common.entity.Company;
import com.erp.hr.entity.Employee;
import com.erp.hr.entity.Department;
import com.erp.common.repository.CompanyRepository;
import com.erp.hr.repository.EmployeeRepository;
import com.erp.hr.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// LocalDate import는 더 이상 사용하지 않으므로 제거됨
import java.time.LocalDateTime;

/**
 * 테스트용 데이터 초기화
 * 테스트 환경에서 admin/admin123과 user/user123 계정을 생성합니다
 * 주의: 이 Initializer는 비활성화되어 있습니다. 실제 DB를 사용하므로 데이터는 수동으로 관리됩니다.
 */
@Slf4j
@Component
@Profile("!test")  // test 프로파일에서는 실행하지 않음 (실제 DB 사용)
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("=== 테스트 데이터 초기화 시작 ===");

        // 기존 데이터 삭제
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        companyRepository.deleteAll();
        
        // 회사 생성
        Company company = createTestCompany();
        company = companyRepository.save(company);
        log.info("✅ 테스트 회사 생성 완료: {}", company.getName());
        
        // 부서 생성
        Department adminDept = createTestDepartment(company, "관리부서", "DEPARTMENT");
        Department userDept = createTestDepartment(company, "일반부서", "DEPARTMENT");
        
        adminDept = departmentRepository.save(adminDept);
        userDept = departmentRepository.save(userDept);
        log.info("✅ 테스트 부서 생성 완료: {}, {}", adminDept.getName(), userDept.getName());
        
        // 직원 생성
        Employee adminEmployee = createTestEmployee(company, adminDept, "admin", "admin123", "ADMIN");
        Employee normalEmployee = createTestEmployee(company, userDept, "user", "user123", "USER");

        adminEmployee = employeeRepository.save(adminEmployee);
        normalEmployee = employeeRepository.save(normalEmployee);
        log.info("✅ 테스트 직원 생성 완료: {}, {}", adminEmployee.getUsername(), normalEmployee.getUsername());

        // 비밀번호 검증 테스트
        boolean adminPasswordMatches = passwordEncoder.matches("admin123", adminEmployee.getPassword());
        boolean userPasswordMatches = passwordEncoder.matches("user123", normalEmployee.getPassword());
        log.info("🔐 비밀번호 검증 결과 - admin: {}, user: {}", adminPasswordMatches, userPasswordMatches);
        
        log.info("=== 테스트 데이터 초기화 완료 ===");
        log.info("✅ 로그인 계정 정보:");
        log.info("   👤 admin 계정 - 사용자명: admin, 비밀번호: admin123, 역할: ADMIN");
        log.info("   👤 user 계정 - 사용자명: user, 비밀번호: user123, 역할: USER");
    }

    private Company createTestCompany() {
        Company company = new Company();
        company.setCompanyCode("TEST_CORP");
        company.setName("테스트 회사");
        company.setNameEn("Test Corporation");
        company.setBusinessNumber("123-45-67890");
        company.setCorporationNumber("123456-1234567");
        company.setCeoName("김테스트");
        company.setBusinessType("IT");
        company.setBusinessItem("소프트웨어 개발");
        company.setAddress("서울특별시 강남구 테헤란로 123");
        company.setAddressDetail("테스트빌딩 10층");
        company.setPostalCode("06292");
        company.setPhone("02-1234-5678");
        company.setFax("02-1234-5679");
        company.setEmail("info@test.com");
        company.setWebsite("www.test.com");
        // setEstablishedDate 메서드는 해당 필드가 실제 DB 스키마에 없으므로 제거됨
        company.setStatus(Company.CompanyStatus.ACTIVE);
        company.setCreatedAt(LocalDateTime.now());
        company.setUpdatedAt(LocalDateTime.now());
        company.setCreatedBy(1L);
        company.setUpdatedBy(1L);
        company.setIsDeleted(false);
        return company;
    }

    private Department createTestDepartment(Company company, String name, String type) {
        Department department = new Department();
        department.setCompany(company);
        department.setDepartmentCode(type.equals("ADMIN") ? "ADMIN_DEPT" : "USER_DEPT");
        department.setName(name);
        department.setNameEn(name + " Department");
        department.setDepartmentType(Department.DepartmentType.valueOf(type));
        department.setStatus(Department.DepartmentStatus.ACTIVE);
        department.setLevel(1);
        department.setSortOrder(1);
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        department.setCreatedBy(1L);
        department.setUpdatedBy(1L);
        department.setIsDeleted(false);
        return department;
    }

    private Employee createTestEmployee(Company company, Department department, String username, String password, String role) {
        Employee employee = new Employee();
        employee.setUsername(username);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setEmail(username + "@test.com");
        employee.setName(username.equals("admin") ? "관리자" : "일반사용자");
        employee.setMobile("010-1234-5678");
        employee.setRole(Employee.UserRole.valueOf(role));
        employee.setIsActive(true);
        employee.setIsLocked(false);
        employee.setIsPasswordExpired(false);
        employee.setCompany(company);
        employee.setDepartment(department);
        employee.setPasswordChangedAt(LocalDateTime.now());
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        employee.setCreatedBy(1L);
        employee.setUpdatedBy(1L);
        employee.setIsDeleted(false);
        return employee;
    }
}
