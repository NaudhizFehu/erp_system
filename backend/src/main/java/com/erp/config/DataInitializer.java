package com.erp.config;

import com.erp.common.entity.Company;
import com.erp.common.entity.User;
import com.erp.hr.entity.Department;
import com.erp.common.repository.CompanyRepository;
import com.erp.hr.repository.DepartmentRepository;
import com.erp.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

// LocalDate import는 더 이상 사용하지 않으므로 제거됨
import java.time.LocalDateTime;

/**
 * 초기 데이터 초기화 컴포넌트
 * 애플리케이션 시작 시 기본 데이터를 생성합니다
 */
@Slf4j
@Component  // 개발용 테스트 계정 자동 생성
@RequiredArgsConstructor
public class DataInitializer {

    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Order(2) // DdlForcer 이후에 실행
    public void initializeData() {
        log.info("애플리케이션 시작 완료 후 데이터 초기화 시작");

        try {
            // 잠시 대기하여 DDL 강제 실행이 완료될 시간을 줍니다
            Thread.sleep(5000);
            
            // 회사 데이터 생성
            createCompany();
            
            // 부서 데이터 생성
            createDepartments();
            
            // 사용자 데이터 생성
            createUsers();

            log.info("초기 데이터 초기화 완료");
        } catch (Exception e) {
            log.error("데이터 초기화 중 오류 발생: {}", e.getMessage(), e);
        }
    }


    @Transactional
    private void createCompany() {
        if (companyRepository.count() == 0) {
            Company company = new Company();
            company.setCompanyCode("COMP001");
            company.setName("ABC 기업");
            company.setBusinessNumber("123-45-67890");
            company.setCeoName("김철수");
            company.setAddress("서울시 강남구 테헤란로 123");
            company.setPhone("02-1234-5678");
            company.setEmail("info@abc.com");
            company.setWebsite("https://abc.com");
            company.setBusinessType("IT");
            // setEmployeeCount, setDescription, setEstablishedDate 메서드들은 해당 필드들이 실제 DB 스키마에 없으므로 제거됨
            company.setStatus(Company.CompanyStatus.ACTIVE);
            company.setCreatedBy(1L);
            company.setUpdatedBy(1L);
            company.setIsDeleted(false);

            companyRepository.save(company);
            log.info("회사 데이터 생성 완료: {}", company.getName());
        }
    }

    @Transactional
    private void createDepartments() {
        if (departmentRepository.count() == 0) {
            Company company = companyRepository.findByCompanyCode("COMP001")
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다"));

            // 개발팀
            Department devDept = new Department();
            devDept.setDepartmentCode("DEPT001");
            devDept.setName("개발팀");
            devDept.setDescription("소프트웨어 개발을 담당하는 부서");
            devDept.setCompany(company);
            devDept.setStatus(Department.DepartmentStatus.ACTIVE);
            devDept.setLevel(1);
            devDept.setCreatedBy(1L);
            devDept.setUpdatedBy(1L);
            devDept.setIsDeleted(false);

            // 마케팅팀
            Department mktDept = new Department();
            mktDept.setDepartmentCode("DEPT002");
            mktDept.setName("마케팅팀");
            mktDept.setDescription("마케팅 및 홍보를 담당하는 부서");
            mktDept.setCompany(company);
            mktDept.setStatus(Department.DepartmentStatus.ACTIVE);
            mktDept.setLevel(1);
            mktDept.setCreatedBy(1L);
            mktDept.setUpdatedBy(1L);
            mktDept.setIsDeleted(false);

            // 인사팀
            Department hrDept = new Department();
            hrDept.setDepartmentCode("DEPT003");
            hrDept.setName("인사팀");
            hrDept.setDescription("인사 관리를 담당하는 부서");
            hrDept.setCompany(company);
            hrDept.setStatus(Department.DepartmentStatus.ACTIVE);
            hrDept.setLevel(1);
            hrDept.setCreatedBy(1L);
            hrDept.setUpdatedBy(1L);
            hrDept.setIsDeleted(false);

            departmentRepository.save(devDept);
            departmentRepository.save(mktDept);
            departmentRepository.save(hrDept);

            log.info("부서 데이터 생성 완료: 개발팀, 마케팅팀, 인사팀");
        }
    }

    @Transactional
    private void createUsers() {
        // 기존 사용자 비밀번호 업데이트 (개발용)
        updateExistingUserPasswords();
        
        if (userRepository.count() == 0) {
            Company company = companyRepository.findByCompanyCode("COMP001")
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다"));

            Department devDept = departmentRepository.findByDepartmentCode("DEPT001")
                .orElseThrow(() -> new RuntimeException("개발팀을 찾을 수 없습니다"));

            Department hrDept = departmentRepository.findByDepartmentCode("DEPT003")
                .orElseThrow(() -> new RuntimeException("인사팀을 찾을 수 없습니다"));

            // 관리자 계정
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@abc.com");
            admin.setFullName("관리자");
            admin.setPhone("02-1234-5678");
            admin.setRole(User.UserRole.ADMIN);
            admin.setIsActive(true);
            admin.setIsLocked(false);
            admin.setIsPasswordExpired(false);
            admin.setCompany(company);
            admin.setDepartment(hrDept);
            admin.setPasswordChangedAt(LocalDateTime.now());
            admin.setCreatedBy(1L);
            admin.setUpdatedBy(1L);
            admin.setIsDeleted(false);

            // 일반 사용자 계정
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@abc.com");
            user.setFullName("일반사용자");
            user.setPhone("02-2345-6789");
            user.setRole(User.UserRole.USER);
            user.setIsActive(true);
            user.setIsLocked(false);
            user.setIsPasswordExpired(false);
            user.setCompany(company);
            user.setDepartment(devDept);
            user.setPasswordChangedAt(LocalDateTime.now());
            user.setCreatedBy(1L);
            user.setUpdatedBy(1L);
            user.setIsDeleted(false);

            userRepository.save(admin);
            userRepository.save(user);

            log.info("사용자 데이터 생성 완료: admin, user");
        }
    }

    /**
     * 기존 사용자 비밀번호를 개발용 비밀번호로 업데이트
     */
    @Transactional
    private void updateExistingUserPasswords() {
        try {
            // admin 사용자 비밀번호 업데이트
            userRepository.findByUsername("admin").ifPresent(admin -> {
                admin.setPassword(passwordEncoder.encode("admin123"));
                userRepository.save(admin);
                log.info("✅ admin 사용자 비밀번호 업데이트 완료");
            });

            // user 사용자 비밀번호 업데이트
            userRepository.findByUsername("user").ifPresent(user -> {
                user.setPassword(passwordEncoder.encode("user123"));
                userRepository.save(user);
                log.info("✅ user 사용자 비밀번호 업데이트 완료");
            });
        } catch (Exception e) {
            log.warn("사용자 비밀번호 업데이트 중 오류 발생: {}", e.getMessage());
        }
    }
}
