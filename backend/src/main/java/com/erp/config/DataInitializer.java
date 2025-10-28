package com.erp.config;

import com.erp.common.entity.Company;
import com.erp.common.entity.User;
import com.erp.hr.entity.Department;
import com.erp.hr.entity.Position;
import com.erp.hr.entity.Employee;
import com.erp.inventory.entity.Product;
import com.erp.inventory.entity.ProductCategory;
import com.erp.common.repository.CompanyRepository;
import com.erp.hr.repository.DepartmentRepository;
import com.erp.hr.repository.PositionRepository;
import com.erp.hr.repository.EmployeeRepository;
import com.erp.common.repository.UserRepository;
import com.erp.inventory.repository.ProductRepository;
import com.erp.inventory.repository.ProductCategoryRepository;
import com.erp.sales.entity.Customer;
import com.erp.sales.repository.CustomerRepository;
import com.erp.sales.entity.Order;
import com.erp.sales.repository.OrderRepository;
import com.erp.common.service.NotificationService;
import com.erp.common.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

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
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        log.info("애플리케이션 시작 완료 후 데이터 초기화 시작");

        try {
            // 잠시 대기하여 DDL 강제 실행이 완료될 시간을 줍니다
            Thread.sleep(5000);
            
            // 회사 데이터 생성
            createCompany();
            
            // 부서 데이터 생성
            createDepartments();
            
            // 직급 데이터 생성
            createPositions();
            
            // 사용자 데이터 생성
            createUsers();
            
            // 직원 데이터 생성
            createEmployees();
            
            // 상품 카테고리 및 상품 데이터 생성
            createProductCategories();
            createProducts();
            
            // 고객 데이터 생성
            createCustomers();
            
            // 주문 데이터 생성
            createOrders();
            
            // 테스트 알림 생성
            createTestNotifications();

            log.info("초기 데이터 초기화 완료");
        } catch (Exception e) {
            log.error("데이터 초기화 중 오류 발생: {}", e.getMessage(), e);
        }
    }


    @Transactional
    private void createCompany() {
        // 3개 회사가 모두 존재하는지 확인
        boolean hasAbcCorp = companyRepository.findByCompanyCode("ABC_CORP").isPresent();
        boolean hasXyzGroup = companyRepository.findByCompanyCode("XYZ_GROUP").isPresent();
        boolean hasDefCorp = companyRepository.findByCompanyCode("DEF_CORP").isPresent();
        
        if (!hasAbcCorp || !hasXyzGroup || !hasDefCorp) {
            log.info("회사 데이터 생성 시작");
            
            // ABC기업 (IT 회사)
            Company abcCompany = new Company();
            abcCompany.setCompanyCode("ABC_CORP");
            abcCompany.setName("ABC기업");
            abcCompany.setBusinessNumber("123-45-67890");
            abcCompany.setCeoName("김대표");
            abcCompany.setAddress("서울시 강남구 테헤란로 123");
            abcCompany.setPhone("02-1234-5678");
            abcCompany.setEmail("info@abc.com");
            abcCompany.setWebsite("https://www.abc.com");
            abcCompany.setBusinessType("IT 서비스");
            abcCompany.setStatus(Company.CompanyStatus.ACTIVE);
            abcCompany.setCreatedBy(1L);
            abcCompany.setUpdatedBy(1L);
            abcCompany.setIsDeleted(false);

            // XYZ그룹 (제조업)
            Company xyzCompany = new Company();
            xyzCompany.setCompanyCode("XYZ_GROUP");
            xyzCompany.setName("XYZ그룹");
            xyzCompany.setBusinessNumber("234-56-78901");
            xyzCompany.setCeoName("이사장");
            xyzCompany.setAddress("경기도 성남시 분당구 판교역로 456");
            xyzCompany.setPhone("031-234-5678");
            xyzCompany.setEmail("info@xyz.com");
            xyzCompany.setWebsite("https://www.xyz.com");
            xyzCompany.setBusinessType("제조업");
            xyzCompany.setStatus(Company.CompanyStatus.ACTIVE);
            xyzCompany.setCreatedBy(1L);
            xyzCompany.setUpdatedBy(1L);
            xyzCompany.setIsDeleted(false);

            // DEF코퍼레이션 (서비스업)
            Company defCompany = new Company();
            defCompany.setCompanyCode("DEF_CORP");
            defCompany.setName("DEF코퍼레이션");
            defCompany.setBusinessNumber("345-67-89012");
            defCompany.setCeoName("박대표");
            defCompany.setAddress("서울시 서초구 서초대로 789");
            defCompany.setPhone("02-345-6789");
            defCompany.setEmail("info@def.com");
            defCompany.setWebsite("https://www.def.com");
            defCompany.setBusinessType("서비스업");
            defCompany.setStatus(Company.CompanyStatus.ACTIVE);
            defCompany.setCreatedBy(1L);
            defCompany.setUpdatedBy(1L);
            defCompany.setIsDeleted(false);

            // 회사 저장
            abcCompany = companyRepository.save(abcCompany);
            xyzCompany = companyRepository.save(xyzCompany);
            defCompany = companyRepository.save(defCompany);

            log.info("✅ 회사 데이터 생성 완료:");
            log.info("   - ABC기업 (ID: {}) - IT 서비스", abcCompany.getId());
            log.info("   - XYZ그룹 (ID: {}) - 제조업", xyzCompany.getId());
            log.info("   - DEF코퍼레이션 (ID: {}) - 서비스업", defCompany.getId());
            
            // 저장 후 실제로 조회되는지 확인
            try {
                Company savedAbc = companyRepository.findByCompanyCode("ABC_CORP")
                    .orElseThrow(() -> new RuntimeException("ABC기업 저장 후 조회 실패"));
                Company savedXyz = companyRepository.findByCompanyCode("XYZ_GROUP")
                    .orElseThrow(() -> new RuntimeException("XYZ그룹 저장 후 조회 실패"));
                Company savedDef = companyRepository.findByCompanyCode("DEF_CORP")
                    .orElseThrow(() -> new RuntimeException("DEF코퍼레이션 저장 후 조회 실패"));
                
                log.info("✅ 회사 데이터 저장 확인 완료:");
                log.info("   - ABC기업 조회 성공 (ID: {})", savedAbc.getId());
                log.info("   - XYZ그룹 조회 성공 (ID: {})", savedXyz.getId());
                log.info("   - DEF코퍼레이션 조회 성공 (ID: {})", savedDef.getId());
            } catch (Exception e) {
                log.error("회사 데이터 저장 확인 실패: {}", e.getMessage(), e);
                throw new RuntimeException("회사 데이터 저장 확인 실패: " + e.getMessage(), e);
            }
        } else {
            log.info("3개 회사 데이터가 모두 존재합니다. 건너뜀.");
            
            // 기존 회사 데이터 확인
            try {
                Company abcCompany = companyRepository.findByCompanyCode("ABC_CORP")
                    .orElseThrow(() -> new RuntimeException("ABC기업을 찾을 수 없습니다"));
                Company xyzCompany = companyRepository.findByCompanyCode("XYZ_GROUP")
                    .orElseThrow(() -> new RuntimeException("XYZ그룹을 찾을 수 없습니다"));
                Company defCompany = companyRepository.findByCompanyCode("DEF_CORP")
                    .orElseThrow(() -> new RuntimeException("DEF코퍼레이션을 찾을 수 없습니다"));
                
                log.info("✅ 기존 회사 데이터 확인 완료:");
                log.info("   - ABC기업 (ID: {})", abcCompany.getId());
                log.info("   - XYZ그룹 (ID: {})", xyzCompany.getId());
                log.info("   - DEF코퍼레이션 (ID: {})", defCompany.getId());
            } catch (Exception e) {
                log.error("기존 회사 데이터 확인 실패: {}", e.getMessage(), e);
                throw new RuntimeException("기존 회사 데이터 확인 실패: " + e.getMessage(), e);
            }
        }
    }

    @Transactional
    private void createDepartments() {
        // 기존 부서 데이터 확인 및 삭제
        long existingCount = departmentRepository.count();
        log.info("기존 부서 개수: {}", existingCount);
        
        if (existingCount > 0) {
            // 기존 부서 데이터 조회하여 코드 확인
            List<Department> existingDepts = departmentRepository.findAll();
            log.info("기존 부서 목록:");
            for (Department dept : existingDepts) {
                log.info("  - ID: {}, 코드: {}, 이름: {}", dept.getId(), dept.getDepartmentCode(), dept.getName());
            }
            
            // DEPT001이 없으면 기존 데이터 삭제 후 재생성
            // 단, 사용자가 참조하는 부서는 삭제하지 않음 (DdlForcer에서 생성한 부서)
            boolean hasDept001 = existingDepts.stream()
                .anyMatch(dept -> "DEPT001".equals(dept.getDepartmentCode()));
            
            if (!hasDept001 && existingCount == 0) {
                log.warn("DEPT001 부서가 없습니다. 부서 데이터를 생성합니다.");
                // deleteAll() 제거 - 외래키 제약조건 때문에 사용자가 참조하는 부서는 삭제 불가
                existingCount = 0;
            } else if (!hasDept001 && existingCount > 0) {
                log.warn("DEPT001 부서가 없지만 기존 부서 데이터가 존재합니다. DdlForcer에서 생성된 부서를 사용합니다.");
                return; // 기존 부서 유지
            }
        }
        
        if (existingCount == 0) {
            try {
                log.info("부서 생성 시작 - 3개 회사 조회 중...");
                
                // 3개 회사 조회
                log.info("ABC기업 조회 중...");
                Company abcCompany = companyRepository.findByCompanyCode("ABC_CORP")
                    .orElseThrow(() -> new RuntimeException("ABC기업을 찾을 수 없습니다"));
                log.info("ABC기업 조회 성공 (ID: {})", abcCompany.getId());
                
                log.info("XYZ그룹 조회 중...");
                Company xyzCompany = companyRepository.findByCompanyCode("XYZ_GROUP")
                    .orElseThrow(() -> new RuntimeException("XYZ그룹을 찾을 수 없습니다"));
                log.info("XYZ그룹 조회 성공 (ID: {})", xyzCompany.getId());
                
                log.info("DEF코퍼레이션 조회 중...");
                Company defCompany = companyRepository.findByCompanyCode("DEF_CORP")
                    .orElseThrow(() -> new RuntimeException("DEF코퍼레이션을 찾을 수 없습니다"));
                log.info("DEF코퍼레이션 조회 성공 (ID: {})", defCompany.getId());

                log.info("부서 생성 시작 - 3개 회사별 부서 생성");

                // ABC기업 부서들 (IT 회사)
                Department abcDevDept = createDepartment("ABC_DEV", "개발팀", "소프트웨어 개발을 담당하는 부서", abcCompany, 1);
                Department abcMktDept = createDepartment("ABC_MKT", "마케팅팀", "마케팅 및 홍보를 담당하는 부서", abcCompany, 2);
                Department abcHrDept = createDepartment("ABC_HR", "인사팀", "인사 관리를 담당하는 부서", abcCompany, 3);
                Department abcSalesDept = createDepartment("ABC_SALES", "영업팀", "영업 및 고객관리를 담당하는 부서", abcCompany, 4);

                // XYZ그룹 부서들 (제조업)
                Department xyzProdDept = createDepartment("XYZ_PROD", "생산팀", "제품 생산을 담당하는 부서", xyzCompany, 1);
                Department xyzQcDept = createDepartment("XYZ_QC", "품질관리팀", "품질 관리 및 검사를 담당하는 부서", xyzCompany, 2);
                Department xyzSalesDept = createDepartment("XYZ_SALES", "영업팀", "제품 영업을 담당하는 부서", xyzCompany, 3);
                Department xyzFinanceDept = createDepartment("XYZ_FIN", "재무팀", "재무 및 회계를 담당하는 부서", xyzCompany, 4);
                Department xyzHrDept = createDepartment("XYZ_HR", "인사팀", "인사 관리를 담당하는 부서", xyzCompany, 5);

                // DEF코퍼레이션 부서들 (서비스업)
                Department defCsDept = createDepartment("DEF_CS", "고객서비스팀", "고객 서비스를 담당하는 부서", defCompany, 1);
                Department defMktDept = createDepartment("DEF_MKT", "마케팅팀", "마케팅 및 홍보를 담당하는 부서", defCompany, 2);
                Department defDevDept = createDepartment("DEF_DEV", "개발팀", "서비스 개발을 담당하는 부서", defCompany, 3);
                Department defHrDept = createDepartment("DEF_HR", "인사팀", "인사 관리를 담당하는 부서", defCompany, 4);
                Department defFinanceDept = createDepartment("DEF_FIN", "재무팀", "재무 및 회계를 담당하는 부서", defCompany, 5);

                // 부서 저장
                List<Department> departments = Arrays.asList(
                    abcDevDept, abcMktDept, abcHrDept, abcSalesDept,
                    xyzProdDept, xyzQcDept, xyzSalesDept, xyzFinanceDept, xyzHrDept,
                    defCsDept, defMktDept, defDevDept, defHrDept, defFinanceDept
                );

                departmentRepository.saveAll(departments);

                log.info("✅ 부서 데이터 생성 완료 (총 {}개):", departments.size());
                log.info("   ABC기업: 개발팀, 마케팅팀, 인사팀, 영업팀");
                log.info("   XYZ그룹: 생산팀, 품질관리팀, 영업팀, 재무팀, 인사팀");
                log.info("   DEF코퍼레이션: 고객서비스팀, 마케팅팀, 개발팀, 인사팀, 재무팀");
                
            } catch (Exception e) {
                log.error("부서 생성 중 오류 발생: {}", e.getMessage(), e);
                throw new RuntimeException("부서 생성 실패: " + e.getMessage(), e);
            }
        } else {
            log.info("부서 데이터가 이미 존재합니다. 건너뜀.");
        }
    }

    /**
     * 부서 생성 헬퍼 메서드
     */
    private Department createDepartment(String code, String name, String description, Company company, int level) {
        Department department = new Department();
        department.setDepartmentCode(code);
        department.setName(name);
        department.setDescription(description);
        department.setCompany(company);
        department.setStatus(Department.DepartmentStatus.ACTIVE);
        department.setLevel(level);
        department.setCreatedBy(1L);
        department.setUpdatedBy(1L);
        department.setIsDeleted(false);
        return department;
    }

    @Transactional
    private void createPositions() {
        // 기존 직급 데이터 확인 및 삭제
        long existingCount = positionRepository.count();
        log.info("기존 직급 개수: {}", existingCount);
        
        if (existingCount > 0) {
            // 기존 직급 데이터 조회하여 코드 확인
            List<Position> existingPositions = positionRepository.findAll();
            log.info("기존 직급 목록:");
            for (Position pos : existingPositions) {
                log.info("  - ID: {}, 코드: {}, 이름: {}", pos.getId(), pos.getPositionCode(), pos.getName());
            }
            
            // POS001이 없으면 기존 데이터 삭제 후 재생성
            boolean hasPos001 = existingPositions.stream()
                .anyMatch(pos -> "POS001".equals(pos.getPositionCode()));
            
            if (!hasPos001) {
                log.warn("POS001 직급이 없습니다. 기존 직급 데이터를 삭제하고 재생성합니다.");
                positionRepository.deleteAll();
                existingCount = 0;
            }
        }
        
        if (existingCount == 0) {
            try {
                // 3개 회사 조회
                Company abcCompany = companyRepository.findByCompanyCode("ABC_CORP")
                    .orElseThrow(() -> new RuntimeException("ABC기업을 찾을 수 없습니다"));
                Company xyzCompany = companyRepository.findByCompanyCode("XYZ_GROUP")
                    .orElseThrow(() -> new RuntimeException("XYZ그룹을 찾을 수 없습니다"));
                Company defCompany = companyRepository.findByCompanyCode("DEF_CORP")
                    .orElseThrow(() -> new RuntimeException("DEF코퍼레이션을 찾을 수 없습니다"));

                log.info("직급 생성 시작 - 3개 회사별 직급 생성");

                // 3개 회사별 직급 생성
                List<Position> positions = new ArrayList<>();

                // ABC기업 직급들
                positions.add(createPosition("ABC_CEO", "대표이사", "회사의 최고 경영자", abcCompany, 1));
                positions.add(createPosition("ABC_DIRECTOR", "이사", "회사의 이사", abcCompany, 2));
                positions.add(createPosition("ABC_MANAGER", "부장", "부서의 부장", abcCompany, 3));
                positions.add(createPosition("ABC_CHIEF", "과장", "과의 과장", abcCompany, 4));
                positions.add(createPosition("ABC_DEPUTY", "대리", "과의 대리", abcCompany, 5));
                positions.add(createPosition("ABC_STAFF", "사원", "일반 사원", abcCompany, 6));

                // XYZ그룹 직급들
                positions.add(createPosition("XYZ_CEO", "대표이사", "회사의 최고 경영자", xyzCompany, 1));
                positions.add(createPosition("XYZ_DIRECTOR", "이사", "회사의 이사", xyzCompany, 2));
                positions.add(createPosition("XYZ_MANAGER", "부장", "부서의 부장", xyzCompany, 3));
                positions.add(createPosition("XYZ_CHIEF", "과장", "과의 과장", xyzCompany, 4));
                positions.add(createPosition("XYZ_DEPUTY", "대리", "과의 대리", xyzCompany, 5));
                positions.add(createPosition("XYZ_STAFF", "사원", "일반 사원", xyzCompany, 6));

                // DEF코퍼레이션 직급들
                positions.add(createPosition("DEF_CEO", "대표이사", "회사의 최고 경영자", defCompany, 1));
                positions.add(createPosition("DEF_DIRECTOR", "이사", "회사의 이사", defCompany, 2));
                positions.add(createPosition("DEF_MANAGER", "부장", "부서의 부장", defCompany, 3));
                positions.add(createPosition("DEF_CHIEF", "과장", "과의 과장", defCompany, 4));
                positions.add(createPosition("DEF_DEPUTY", "대리", "과의 대리", defCompany, 5));
                positions.add(createPosition("DEF_STAFF", "사원", "일반 사원", defCompany, 6));

                positionRepository.saveAll(positions);

                log.info("✅ 직급 데이터 생성 완료 (총 {}개):", positions.size());
                log.info("   ABC기업: 대표이사, 이사, 부장, 과장, 대리, 사원");
                log.info("   XYZ그룹: 대표이사, 이사, 부장, 과장, 대리, 사원");
                log.info("   DEF코퍼레이션: 대표이사, 이사, 부장, 과장, 대리, 사원");
                
            } catch (Exception e) {
                log.error("직급 생성 중 오류 발생: {}", e.getMessage(), e);
                throw new RuntimeException("직급 생성 실패: " + e.getMessage(), e);
            }
        } else {
            log.info("직급 데이터가 이미 존재합니다. 건너뜀.");
        }
    }

    /**
     * 직급 생성 헬퍼 메서드
     */
    private Position createPosition(String code, String name, String description, Company company, int level) {
        Position position = new Position();
        position.setPositionCode(code);
        position.setName(name);
        position.setDescription(description);
        position.setCompany(company);
        position.setLevel(level);
        position.setIsActive(true);
        position.setCreatedBy(1L);
        position.setUpdatedBy(1L);
        position.setIsDeleted(false);
        return position;
    }

    @Transactional
    private void createUsers() {
        // 기존 사용자 비밀번호 업데이트 (개발용)
        updateExistingUserPasswords();
        
        // 기존 사용자 데이터 확인
        long existingUserCount = userRepository.count();
        log.info("기존 사용자 개수: {}", existingUserCount);
        
        if (existingUserCount > 0) {
            // 기존 사용자 데이터 조회하여 확인
            List<User> existingUsers = userRepository.findAll();
            log.info("기존 사용자 목록:");
            for (User user : existingUsers) {
                log.info("  - ID: {}, 사용자명: {}, 역할: {}", user.getId(), user.getUsername(), user.getRole());
            }
            
            // DdlForcer에서 SUPER_ADMIN만 생성되었으면 나머지 계정 추가
            boolean hasSuperAdmin = existingUsers.stream()
                .anyMatch(user -> user.getRole() == User.UserRole.SUPER_ADMIN);
            boolean hasRegularUsers = existingUsers.stream()
                .anyMatch(user -> "admin".equals(user.getUsername()) || "hr_manager".equals(user.getUsername()));
            
            if (hasSuperAdmin && !hasRegularUsers) {
                log.info("SUPER_ADMIN 계정만 존재합니다. 나머지 계정들을 추가합니다.");
                // existingUserCount를 0으로 설정하여 아래 사용자 생성 로직 실행
                existingUserCount = 0;
            }
        }
        
        if (existingUserCount == 0) {
            try {
                // 3개 회사 조회
                Company abcCompany = companyRepository.findByCompanyCode("ABC_CORP")
                    .orElseThrow(() -> new RuntimeException("ABC기업을 찾을 수 없습니다"));
                Company xyzCompany = companyRepository.findByCompanyCode("XYZ_GROUP")
                    .orElseThrow(() -> new RuntimeException("XYZ그룹을 찾을 수 없습니다"));
                Company defCompany = companyRepository.findByCompanyCode("DEF_CORP")
                    .orElseThrow(() -> new RuntimeException("DEF코퍼레이션을 찾을 수 없습니다"));

                log.info("사용자 생성 시작 - 3개 회사별 사용자 생성");

                // 부서 조회
                Department abcHrDept = departmentRepository.findByDepartmentCode("ABC_HR")
                    .orElseThrow(() -> new RuntimeException("ABC 인사팀을 찾을 수 없습니다"));
                Department abcDevDept = departmentRepository.findByDepartmentCode("ABC_DEV")
                    .orElseThrow(() -> new RuntimeException("ABC 개발팀을 찾을 수 없습니다"));
                Department xyzHrDept = departmentRepository.findByDepartmentCode("XYZ_HR")
                    .orElseThrow(() -> new RuntimeException("XYZ 인사팀을 찾을 수 없습니다"));
                Department defHrDept = departmentRepository.findByDepartmentCode("DEF_HR")
                    .orElseThrow(() -> new RuntimeException("DEF 인사팀을 찾을 수 없습니다"));

                // 직급 조회
                Position abcCeoPosition = positionRepository.findByPositionCode("ABC_CEO")
                    .orElseThrow(() -> new RuntimeException("ABC 대표이사 직급을 찾을 수 없습니다"));
                Position abcManagerPosition = positionRepository.findByPositionCode("ABC_MANAGER")
                    .orElseThrow(() -> new RuntimeException("ABC 부장 직급을 찾을 수 없습니다"));
                Position abcDeputyPosition = positionRepository.findByPositionCode("ABC_DEPUTY")
                    .orElseThrow(() -> new RuntimeException("ABC 대리 직급을 찾을 수 없습니다"));

                // 사용자 생성
                List<User> users = new ArrayList<>();

                // 시스템 관리자 (SUPER_ADMIN) - 회사 소속 없음
                users.add(createUser("superadmin", "super123", "super@erp-system.com", "시스템관리자", "02-0000-0000", 
                    User.UserRole.SUPER_ADMIN, null, null, "시스템관리자"));

                // ABC기업 사용자들
                users.add(createUser("admin", "admin123", "admin@abc.com", "관리자", "02-1234-5678", 
                    User.UserRole.ADMIN, abcCompany, abcHrDept, abcCeoPosition.getName()));
                users.add(createUser("manager", "manager123", "manager@abc.com", "개발팀매니저", "02-3456-7890", 
                    User.UserRole.MANAGER, abcCompany, abcDevDept, abcManagerPosition.getName()));
                users.add(createUser("hr_manager", "hr123", "hr_manager@abc.com", "인사팀매니저", "02-3456-7891", 
                    User.UserRole.MANAGER, abcCompany, abcHrDept, abcManagerPosition.getName()));
                users.add(createUser("user", "user123", "user@abc.com", "일반사용자", "02-2345-6789", 
                    User.UserRole.USER, abcCompany, abcDevDept, abcDeputyPosition.getName()));

                // XYZ그룹 사용자들
                users.add(createUser("xyz_admin", "xyz123", "admin@xyz.com", "XYZ관리자", "031-234-5678", 
                    User.UserRole.ADMIN, xyzCompany, xyzHrDept, "대표이사"));
                users.add(createUser("xyz_manager", "xyz123", "manager@xyz.com", "XYZ인사팀매니저", "031-234-5679", 
                    User.UserRole.MANAGER, xyzCompany, xyzHrDept, "부장"));

                // DEF코퍼레이션 사용자들
                users.add(createUser("def_admin", "def123", "admin@def.com", "DEF관리자", "02-345-6789", 
                    User.UserRole.ADMIN, defCompany, defHrDept, "대표이사"));
                users.add(createUser("def_user", "def123", "user@def.com", "DEF사용자", "02-345-6790", 
                    User.UserRole.USER, defCompany, defHrDept, "사원"));

                userRepository.saveAll(users);

                log.info("✅ 로그인 계정 정보 (총 {}개):", users.size());
                log.info("   시스템: superadmin/super123 (SUPER_ADMIN)");
                log.info("   ABC기업: admin/admin123, manager/manager123, hr_manager/hr123, user/user123");
                log.info("   XYZ그룹: xyz_admin/xyz123, xyz_manager/xyz123");
                log.info("   DEF코퍼레이션: def_admin/def123, def_user/def123");
                
            } catch (Exception e) {
                log.error("사용자 생성 중 오류 발생: {}", e.getMessage(), e);
                throw new RuntimeException("사용자 생성 실패: " + e.getMessage(), e);
            }
        } else {
            log.info("사용자 데이터가 이미 존재합니다. 건너뜀.");
        }
    }

    /**
     * 사용자 생성 헬퍼 메서드
     */
    private User createUser(String username, String password, String email, String fullName, String phone, 
                           User.UserRole role, Company company, Department department, String position) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setRole(role);
        user.setIsActive(true);
        user.setIsLocked(false);
        user.setIsPasswordExpired(false);
        user.setCompany(company);
        user.setDepartment(department);
        user.setPosition(position);
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setCreatedBy(1L);
        user.setUpdatedBy(1L);
        user.setIsDeleted(false);
        return user;
    }

    /**
     * 직원 데이터 생성 - 각 회사별 10명씩, 5개 상태 모두 포함
     */
    @Transactional
    private void createEmployees() {
        // 기존 직원 데이터 확인
        long existingEmployeeCount = employeeRepository.count();
        log.info("기존 직원 개수: {}", existingEmployeeCount);
        
        if (existingEmployeeCount == 0) {
            try {
                // 3개 회사 조회
                Company abcCompany = companyRepository.findByCompanyCode("ABC_CORP")
                    .orElseThrow(() -> new RuntimeException("ABC기업을 찾을 수 없습니다"));
                Company xyzCompany = companyRepository.findByCompanyCode("XYZ_GROUP")
                    .orElseThrow(() -> new RuntimeException("XYZ그룹을 찾을 수 없습니다"));
                Company defCompany = companyRepository.findByCompanyCode("DEF_CORP")
                    .orElseThrow(() -> new RuntimeException("DEF코퍼레이션을 찾을 수 없습니다"));

                log.info("직원 데이터 생성 시작 - 각 회사별 10명씩, 5개 상태 모두 포함");

                List<Employee> employees = new ArrayList<>();
                
                // 부서 및 직급 조회
                List<Department> abcDepts = departmentRepository.findByCompanyId(abcCompany.getId());
                List<Department> xyzDepts = departmentRepository.findByCompanyId(xyzCompany.getId());
                List<Department> defDepts = departmentRepository.findByCompanyId(defCompany.getId());
                
                List<Position> abcPositions = positionRepository.findByCompanyId(abcCompany.getId());
                List<Position> xyzPositions = positionRepository.findByCompanyId(xyzCompany.getId());
                List<Position> defPositions = positionRepository.findByCompanyId(defCompany.getId());

                // 5개 상태 배열 (각 회사에서 최소 1개씩은 포함되도록)
                Employee.EmploymentStatus[] allStatuses = {
                    Employee.EmploymentStatus.ACTIVE,
                    Employee.EmploymentStatus.ON_LEAVE,
                    Employee.EmploymentStatus.INACTIVE,
                    Employee.EmploymentStatus.SUSPENDED,
                    Employee.EmploymentStatus.TERMINATED
                };

                // ABC기업 직원 10명 생성
                employees.addAll(createCompanyEmployees("ABC", abcCompany, abcDepts, abcPositions, allStatuses, 10));
                
                // XYZ그룹 직원 10명 생성
                employees.addAll(createCompanyEmployees("XYZ", xyzCompany, xyzDepts, xyzPositions, allStatuses, 10));
                
                // DEF코퍼레이션 직원 10명 생성
                employees.addAll(createCompanyEmployees("DEF", defCompany, defDepts, defPositions, allStatuses, 10));

                // 직원 데이터 저장
                employeeRepository.saveAll(employees);
                log.info("✅ 직원 데이터 생성 완료 - 총 {}명 (각 회사별 10명)", employees.size());
                
            } catch (Exception e) {
                log.error("직원 데이터 생성 중 오류 발생: {}", e.getMessage(), e);
            }
        } else {
            log.info("직원 데이터가 이미 존재합니다. 건너뜁니다.");
        }
    }

    /**
     * 특정 회사의 직원들을 생성하는 헬퍼 메서드
     */
    private List<Employee> createCompanyEmployees(String companyPrefix, Company company, 
            List<Department> departments, List<Position> positions, 
            Employee.EmploymentStatus[] allStatuses, int count) {
        
        List<Employee> employees = new ArrayList<>();
        Random random = new Random();
        
        // 이름 후보들
        String[] firstNames = {"김", "이", "박", "최", "정", "강", "조", "윤", "장", "임", "한", "오", "서", "신", "권"};
        String[] lastNames = {"민수", "지영", "현우", "서연", "준호", "미영", "성민", "예진", "동현", "수진", "재현", "지현", "민호", "유진", "태현"};
        String[] englishNames = {"Kim", "Lee", "Park", "Choi", "Jung", "Kang", "Cho", "Yoon", "Jang", "Lim", "Han", "Oh", "Seo", "Shin", "Kwon"};
        
        // 부서와 직급이 없으면 기본값 사용
        Department defaultDept = departments.isEmpty() ? null : departments.get(0);
        Position defaultPosition = positions.isEmpty() ? null : positions.get(0);
        
        // 5개 상태를 각각 최소 1개씩 포함하도록 먼저 할당
        for (int i = 0; i < Math.min(5, count); i++) {
            String employeeNumber = String.format("%s%03d", companyPrefix, i + 1);
            String koreanName = firstNames[random.nextInt(firstNames.length)] + lastNames[random.nextInt(lastNames.length)];
            String englishName = englishNames[random.nextInt(englishNames.length)] + " " + (i + 1);
            String email = String.format("%s%03d@%s.com", companyPrefix.toLowerCase(), i + 1, companyPrefix.toLowerCase());
            
            Department dept = departments.isEmpty() ? defaultDept : departments.get(random.nextInt(departments.size()));
            Position pos = positions.isEmpty() ? defaultPosition : positions.get(random.nextInt(positions.size()));
            
            Employee.EmploymentStatus status = allStatuses[i];
            LocalDate hireDate = LocalDate.now().minusYears(random.nextInt(10) + 1);
            LocalDate terminationDate = (status == Employee.EmploymentStatus.TERMINATED) ? 
                LocalDate.now().minusMonths(random.nextInt(12) + 1) : null;
            
            employees.add(createEmployee(
                employeeNumber, koreanName, englishName, email,
                generatePhoneNumber(), generateMobileNumber(),
                random.nextBoolean() ? Employee.Gender.MALE : Employee.Gender.FEMALE,
                LocalDate.of(1980 + random.nextInt(25), random.nextInt(12) + 1, random.nextInt(28) + 1),
                hireDate, company, dept, pos,
                generateAddress(), generatePostalCode(),
                generateEducation(), generateMajor(), generateCareer(), generateSkills(),
                status, Employee.EmploymentType.values()[random.nextInt(Employee.EmploymentType.values().length)],
                terminationDate
            ));
        }
        
        // 나머지 직원들을 랜덤 상태로 생성
        for (int i = 5; i < count; i++) {
            String employeeNumber = String.format("%s%03d", companyPrefix, i + 1);
            String koreanName = firstNames[random.nextInt(firstNames.length)] + lastNames[random.nextInt(lastNames.length)];
            String englishName = englishNames[random.nextInt(englishNames.length)] + " " + (i + 1);
            String email = String.format("%s%03d@%s.com", companyPrefix.toLowerCase(), i + 1, companyPrefix.toLowerCase());
            
            Department dept = departments.isEmpty() ? defaultDept : departments.get(random.nextInt(departments.size()));
            Position pos = positions.isEmpty() ? defaultPosition : positions.get(random.nextInt(positions.size()));
            
            Employee.EmploymentStatus status = allStatuses[random.nextInt(allStatuses.length)];
            LocalDate hireDate = LocalDate.now().minusYears(random.nextInt(10) + 1);
            LocalDate terminationDate = (status == Employee.EmploymentStatus.TERMINATED) ? 
                LocalDate.now().minusMonths(random.nextInt(12) + 1) : null;
            
            employees.add(createEmployee(
                employeeNumber, koreanName, englishName, email,
                generatePhoneNumber(), generateMobileNumber(),
                random.nextBoolean() ? Employee.Gender.MALE : Employee.Gender.FEMALE,
                LocalDate.of(1980 + random.nextInt(25), random.nextInt(12) + 1, random.nextInt(28) + 1),
                hireDate, company, dept, pos,
                generateAddress(), generatePostalCode(),
                generateEducation(), generateMajor(), generateCareer(), generateSkills(),
                status, Employee.EmploymentType.values()[random.nextInt(Employee.EmploymentType.values().length)],
                terminationDate
            ));
        }
        
        return employees;
    }

    /**
     * 랜덤 데이터 생성 헬퍼 메서드들
     */
    private String generatePhoneNumber() {
        Random random = new Random();
        String[] areaCodes = {"02", "031", "032", "033", "041", "042", "043", "044", "051", "052", "053", "054", "055", "061", "062", "063", "064"};
        String areaCode = areaCodes[random.nextInt(areaCodes.length)];
        return String.format("%s-%04d-%04d", areaCode, random.nextInt(10000), random.nextInt(10000));
    }

    private String generateMobileNumber() {
        Random random = new Random();
        return String.format("010-%04d-%04d", random.nextInt(10000), random.nextInt(10000));
    }

    private String generateAddress() {
        String[] cities = {"서울시", "부산시", "대구시", "인천시", "광주시", "대전시", "울산시", "세종시"};
        String[] districts = {"강남구", "서초구", "송파구", "강동구", "마포구", "영등포구", "서대문구", "노원구"};
        Random random = new Random();
        return String.format("%s %s", cities[random.nextInt(cities.length)], districts[random.nextInt(districts.length)]);
    }

    private String generatePostalCode() {
        Random random = new Random();
        return String.format("%05d", random.nextInt(100000));
    }

    private String generateEducation() {
        String[] educations = {"컴퓨터공학과", "경영학과", "경제학과", "심리학과", "회계학과", "마케팅학과", "영문학과", "수학과"};
        Random random = new Random();
        return educations[random.nextInt(educations.length)];
    }

    private String generateMajor() {
        String[] majors = {"소프트웨어공학", "경영학", "경제학", "심리학", "회계학", "마케팅", "영문학", "수학"};
        Random random = new Random();
        return majors[random.nextInt(majors.length)];
    }

    private String generateCareer() {
        Random random = new Random();
        int years = random.nextInt(15) + 1;
        return years + "년";
    }

    private String generateSkills() {
        String[] skills = {"Java, Spring", "React, TypeScript", "Python, Django", "Node.js, Express", "C#, .NET", "PHP, Laravel", "Ruby, Rails", "Go, Gin"};
        Random random = new Random();
        return skills[random.nextInt(skills.length)];
    }

    /**
     * 직원 생성 헬퍼 메서드
     */
    private Employee createEmployee(String empNumber, String name, String nameEn, String email, String phone, String mobile,
                                  Employee.Gender gender, LocalDate birthDate, LocalDate hireDate, Company company, 
                                  Department department, Position position, String address, String postalCode,
                                  String education, String major, String career, String skills,
                                  Employee.EmploymentStatus employmentStatus, Employee.EmploymentType employmentType) {
        return createEmployee(empNumber, name, nameEn, email, phone, mobile, gender, birthDate, hireDate, 
                            company, department, position, address, postalCode, education, major, career, skills,
                            employmentStatus, employmentType, null);
    }

    /**
     * 직원 생성 헬퍼 메서드 (퇴사일 포함)
     */
    private Employee createEmployee(String empNumber, String name, String nameEn, String email, String phone, String mobile,
                                  Employee.Gender gender, LocalDate birthDate, LocalDate hireDate, Company company, 
                                  Department department, Position position, String address, String postalCode,
                                  String education, String major, String career, String skills,
                                  Employee.EmploymentStatus employmentStatus, Employee.EmploymentType employmentType,
                                  LocalDate terminationDate) {
        Employee employee = new Employee();
        employee.setEmployeeNumber(empNumber);
        employee.setName(name);
        employee.setNameEn(nameEn);
        employee.setEmail(email);
        employee.setPhone(phone);
        employee.setMobile(mobile);
        employee.setGender(gender);
        employee.setBirthDate(birthDate);
        employee.setHireDate(hireDate);
        employee.setEmploymentStatus(employmentStatus);
        employee.setEmploymentType(employmentType);
        employee.setTerminationDate(terminationDate);
        employee.setCompany(company);
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setAddress(address);
        employee.setPostalCode(postalCode);
        employee.setEducation(education);
        employee.setMajor(major);
        employee.setCareer(career);
        employee.setSkills(skills);
        employee.setIsDeleted(false);
        employee.setCreatedBy(1L);
        employee.setUpdatedBy(1L);
        return employee;
    }

    /**
     * 기존 사용자 비밀번호를 개발용 비밀번호로 업데이트
     */
    @Transactional
    private void updateExistingUserPasswords() {
        try {
            // superadmin 사용자 비밀번호 업데이트
            userRepository.findByUsername("superadmin").ifPresent(superadmin -> {
                superadmin.setPassword(passwordEncoder.encode("super123"));
                userRepository.save(superadmin);
                log.info("✅ superadmin 사용자 비밀번호 업데이트 완료");
            });

            // admin 사용자 비밀번호 업데이트
            userRepository.findByUsername("admin").ifPresent(admin -> {
                admin.setPassword(passwordEncoder.encode("admin123"));
                userRepository.save(admin);
                log.info("✅ admin 사용자 비밀번호 업데이트 완료");
            });

            // manager 사용자 비밀번호 업데이트
            userRepository.findByUsername("manager").ifPresent(manager -> {
                manager.setPassword(passwordEncoder.encode("manager123"));
                userRepository.save(manager);
                log.info("✅ manager 사용자 비밀번호 업데이트 완료");
            });

            // hr_manager 사용자 비밀번호 업데이트
            userRepository.findByUsername("hr_manager").ifPresent(hrManager -> {
                hrManager.setPassword(passwordEncoder.encode("hr123"));
                userRepository.save(hrManager);
                log.info("✅ hr_manager 사용자 비밀번호 업데이트 완료");
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

    /**
     * 상품 카테고리 데이터 생성
     */
    @Transactional
    private void createProductCategories() {
        if (productCategoryRepository.count() == 0) {
            // 전자제품 카테고리
            ProductCategory electronics = new ProductCategory();
            electronics.setCategoryCode("ELECTRONICS");
            electronics.setName("전자제품");
            electronics.setDescription("전자제품 카테고리");
            electronics.setIsActive(true);
            electronics.setCreatedBy(1L);
            electronics.setUpdatedBy(1L);
            electronics.setIsDeleted(false);
            productCategoryRepository.save(electronics);

            // 컴퓨터 카테고리
            ProductCategory computer = new ProductCategory();
            computer.setCategoryCode("COMPUTER");
            computer.setName("컴퓨터");
            computer.setDescription("컴퓨터 및 주변기기");
            computer.setParentCategory(electronics);
            computer.setIsActive(true);
            computer.setCreatedBy(1L);
            computer.setUpdatedBy(1L);
            computer.setIsDeleted(false);
            productCategoryRepository.save(computer);

            // 사무용품 카테고리
            ProductCategory office = new ProductCategory();
            office.setCategoryCode("OFFICE");
            office.setName("사무용품");
            office.setDescription("사무용품 및 소모품");
            office.setIsActive(true);
            office.setCreatedBy(1L);
            office.setUpdatedBy(1L);
            office.setIsDeleted(false);
            productCategoryRepository.save(office);

            log.info("상품 카테고리 데이터 생성 완료: 전자제품, 컴퓨터, 사무용품");
        }
    }

    /**
     * 상품 데이터 생성
     */
    @Transactional
    private void createProducts() {
        if (productRepository.count() == 0) {
            // 회사가 없으면 생성
            Company company = companyRepository.findById(1L)
                .orElseGet(() -> {
                    log.warn("회사가 없어서 새로 생성합니다");
                    Company newCompany = new Company();
                    newCompany.setCompanyCode("COMP001");
                    newCompany.setName("ABC 기업");
                    newCompany.setBusinessNumber("123-45-67890");
                    newCompany.setCeoName("김철수");
                    newCompany.setAddress("서울시 강남구 테헤란로 123");
                    newCompany.setPhone("02-1234-5678");
                    newCompany.setEmail("info@abc.com");
                    newCompany.setWebsite("https://abc.com");
                    newCompany.setBusinessType("IT");
                    newCompany.setStatus(Company.CompanyStatus.ACTIVE);
                    newCompany.setCreatedBy(1L);
                    newCompany.setUpdatedBy(1L);
                    newCompany.setIsDeleted(false);
                    return companyRepository.save(newCompany);
                });

            ProductCategory computerCategory = productCategoryRepository.findByCategoryCode("COMPUTER")
                .orElseThrow(() -> new RuntimeException("컴퓨터 카테고리를 찾을 수 없습니다"));

            ProductCategory officeCategory = productCategoryRepository.findByCategoryCode("OFFICE")
                .orElseThrow(() -> new RuntimeException("사무용품 카테고리를 찾을 수 없습니다"));

            // 노트북
            Product laptop = new Product();
            laptop.setProductCode("LAPTOP001");
            laptop.setProductName("노트북");
            laptop.setDescription("고성능 노트북");
            laptop.setCategory(computerCategory);
            laptop.setCompany(company);
            laptop.setSellingPrice(new java.math.BigDecimal("1500000"));
            laptop.setMinStock(new java.math.BigDecimal("10"));
            laptop.setMaxStock(new java.math.BigDecimal("100"));
            laptop.setBaseUnit("대");
            laptop.setManufacturer("삼성");
            laptop.setBrand("Samsung");
            laptop.setProductStatus(Product.ProductStatus.ACTIVE);
            laptop.setCreatedBy(1L);
            laptop.setUpdatedBy(1L);
            laptop.setIsDeleted(false);
            productRepository.save(laptop);

            // 무선마우스
            Product mouse = new Product();
            mouse.setProductCode("MOUSE001");
            mouse.setProductName("무선마우스");
            mouse.setDescription("블루투스 무선마우스");
            mouse.setCategory(computerCategory);
            mouse.setCompany(company);
            mouse.setSellingPrice(new java.math.BigDecimal("50000"));
            mouse.setMinStock(new java.math.BigDecimal("50"));
            mouse.setMaxStock(new java.math.BigDecimal("500"));
            mouse.setBaseUnit("개");
            mouse.setManufacturer("로지텍");
            mouse.setBrand("Logitech");
            mouse.setProductStatus(Product.ProductStatus.ACTIVE);
            mouse.setCreatedBy(1L);
            mouse.setUpdatedBy(1L);
            mouse.setIsDeleted(false);
            productRepository.save(mouse);

            // 볼펜
            Product pen = new Product();
            pen.setProductCode("PEN001");
            pen.setProductName("볼펜");
            pen.setDescription("검은색 볼펜");
            pen.setCategory(officeCategory);
            pen.setCompany(company);
            pen.setSellingPrice(new java.math.BigDecimal("1000"));
            pen.setMinStock(new java.math.BigDecimal("100"));
            pen.setMaxStock(new java.math.BigDecimal("2000"));
            pen.setBaseUnit("자루");
            pen.setManufacturer("모나미");
            pen.setBrand("Monami");
            pen.setProductStatus(Product.ProductStatus.ACTIVE);
            pen.setCreatedBy(1L);
            pen.setUpdatedBy(1L);
            pen.setIsDeleted(false);
            productRepository.save(pen);

            // 키보드
            Product keyboard = new Product();
            keyboard.setProductCode("KEYBOARD001");
            keyboard.setProductName("키보드");
            keyboard.setDescription("기계식 키보드");
            keyboard.setCategory(computerCategory);
            keyboard.setCompany(company);
            keyboard.setSellingPrice(new java.math.BigDecimal("150000"));
            keyboard.setMinStock(new java.math.BigDecimal("5"));
            keyboard.setMaxStock(new java.math.BigDecimal("50"));
            keyboard.setBaseUnit("개");
            keyboard.setManufacturer("체리");
            keyboard.setBrand("Cherry");
            keyboard.setProductStatus(Product.ProductStatus.ACTIVE);
            keyboard.setCreatedBy(1L);
            keyboard.setUpdatedBy(1L);
            keyboard.setIsDeleted(false);
            productRepository.save(keyboard);

            // 모니터
            Product monitor = new Product();
            monitor.setProductCode("MONITOR001");
            monitor.setProductName("모니터");
            monitor.setDescription("27인치 4K 모니터");
            monitor.setCategory(computerCategory);
            monitor.setCompany(company);
            monitor.setSellingPrice(new java.math.BigDecimal("500000"));
            monitor.setMinStock(new java.math.BigDecimal("3"));
            monitor.setMaxStock(new java.math.BigDecimal("30"));
            monitor.setBaseUnit("대");
            monitor.setManufacturer("LG");
            monitor.setBrand("LG");
            monitor.setProductStatus(Product.ProductStatus.ACTIVE);
            monitor.setCreatedBy(1L);
            monitor.setUpdatedBy(1L);
            monitor.setIsDeleted(false);
            productRepository.save(monitor);

            // A4용지
            Product paper = new Product();
            paper.setProductCode("PAPER001");
            paper.setProductName("A4용지");
            paper.setDescription("복사용 A4용지");
            paper.setCategory(officeCategory);
            paper.setCompany(company);
            paper.setSellingPrice(new java.math.BigDecimal("5000"));
            paper.setMinStock(new java.math.BigDecimal("50"));
            paper.setMaxStock(new java.math.BigDecimal("1000"));
            paper.setBaseUnit("박스");
            paper.setManufacturer("한솔");
            paper.setBrand("Hansol");
            paper.setProductStatus(Product.ProductStatus.ACTIVE);
            paper.setCreatedBy(1L);
            paper.setUpdatedBy(1L);
            paper.setIsDeleted(false);
            productRepository.save(paper);

            // 추가 상품들
            String[] additionalProductNames = {
                "스마트폰", "태블릿", "이어폰", "스피커", "웹캠",
                "헤드셋", "충전기", "케이블", "메모리카드", "외장하드"
            };

            String[] additionalProductCodes = {
                "PHONE001", "TABLET001", "EARPHONE001", "SPEAKER001", "WEBCAM001",
                "HEADSET001", "CHARGER001", "CABLE001", "MEMORY001", "HDD001"
            };

            String[] additionalDescriptions = {
                "최신 스마트폰", "고성능 태블릿", "무선 이어폰", "블루투스 스피커", "4K 웹캠",
                "게이밍 헤드셋", "고속 충전기", "USB-C 케이블", "128GB 메모리카드", "1TB 외장하드"
            };

            java.math.BigDecimal[] additionalPrices = {
                new java.math.BigDecimal("800000"), new java.math.BigDecimal("600000"), new java.math.BigDecimal("150000"),
                new java.math.BigDecimal("200000"), new java.math.BigDecimal("100000"), new java.math.BigDecimal("300000"),
                new java.math.BigDecimal("50000"), new java.math.BigDecimal("20000"), new java.math.BigDecimal("80000"),
                new java.math.BigDecimal("120000")
            };

            String[] additionalManufacturers = {
                "애플", "삼성", "소니", "보스", "로지텍",
                "레이저", "애플", "삼성", "샌디스크", "시게이트"
            };

            String[] additionalBrands = {
                "iPhone", "Galaxy", "Sony", "Bose", "Logitech",
                "Razer", "Apple", "Samsung", "SanDisk", "Seagate"
            };

            for (int i = 0; i < additionalProductNames.length; i++) {
                Product product = new Product();
                product.setProductCode(additionalProductCodes[i]);
                product.setProductName(additionalProductNames[i]);
                product.setDescription(additionalDescriptions[i]);
                product.setCategory(computerCategory); // 모두 컴퓨터 카테고리로 분류
                product.setCompany(company);
                product.setSellingPrice(additionalPrices[i]);
                product.setMinStock(new java.math.BigDecimal("5"));
                product.setMaxStock(new java.math.BigDecimal("50"));
                product.setBaseUnit("개");
                product.setManufacturer(additionalManufacturers[i]);
                product.setBrand(additionalBrands[i]);
                product.setProductStatus(Product.ProductStatus.ACTIVE);
                product.setCreatedBy(1L);
                product.setUpdatedBy(1L);
                product.setIsDeleted(false);
                productRepository.save(product);
            }

            log.info("상품 데이터 생성 완료: 총 16개 (노트북, 무선마우스, 볼펜, 키보드, 모니터, A4용지 + 추가 10개)");
        }
    }

    /**
     * 고객 데이터 생성 (15개)
     */
    @Transactional
    private void createCustomers() {
        if (customerRepository.count() == 0) {
            // 회사가 없으면 생성
            Company company = companyRepository.findById(1L)
                .orElseGet(() -> {
                    log.warn("회사가 없어서 새로 생성합니다");
                    Company newCompany = new Company();
                    newCompany.setCompanyCode("COMP001");
                    newCompany.setName("ABC 기업");
                    newCompany.setBusinessNumber("123-45-67890");
                    newCompany.setCeoName("김철수");
                    newCompany.setAddress("서울시 강남구 테헤란로 123");
                    newCompany.setPhone("02-1234-5678");
                    newCompany.setEmail("info@abc.com");
                    newCompany.setWebsite("https://abc.com");
                    newCompany.setBusinessType("IT");
                    newCompany.setStatus(Company.CompanyStatus.ACTIVE);
                    newCompany.setCreatedBy(1L);
                    newCompany.setUpdatedBy(1L);
                    newCompany.setIsDeleted(false);
                    return companyRepository.save(newCompany);
                });

            String[] customerNames = {
                "삼성전자", "LG전자", "현대자동차", "SK하이닉스", "네이버",
                "카카오", "쿠팡", "배달의민족", "토스", "당근마켓",
                "라인", "야놀자", "직방", "마켓컬리", "무신사"
            };

            String[] customerCodes = {
                "CUST001", "CUST002", "CUST003", "CUST004", "CUST005",
                "CUST006", "CUST007", "CUST008", "CUST009", "CUST010",
                "CUST011", "CUST012", "CUST013", "CUST014", "CUST015"
            };

            String[] businessNumbers = {
                "123-45-67890", "234-56-78901", "345-67-89012", "456-78-90123", "567-89-01234",
                "678-90-12345", "789-01-23456", "890-12-34567", "901-23-45678", "012-34-56789",
                "111-22-33344", "222-33-44455", "333-44-55566", "444-55-66677", "555-66-77788"
            };

            String[] ceoNames = {
                "김삼성", "이엘지", "박현대", "최하이닉스", "정네이버",
                "강카카오", "윤쿠팡", "임배민", "조토스", "한당근",
                "서라인", "오야놀자", "신직방", "권컬리", "황무신사"
            };

            for (int i = 0; i < customerNames.length; i++) {
                Customer customer = new Customer();
                customer.setCustomerCode(customerCodes[i]);
                customer.setCustomerName(customerNames[i]);
                customer.setBusinessRegistrationNumber(businessNumbers[i]);
                customer.setCeoName(ceoNames[i]);
                customer.setAddress("서울시 강남구 테헤란로 " + (100 + i));
                customer.setPhoneNumber("02-" + String.format("%04d", 1000 + i));
                customer.setEmail("contact@" + customerNames[i].toLowerCase() + ".com");
                customer.setCompany(company);
                customer.setCustomerType(Customer.CustomerType.CORPORATE);
                customer.setCustomerStatus(Customer.CustomerStatus.ACTIVE);
                customer.setCreatedBy(1L);
                customer.setUpdatedBy(1L);
                customer.setIsDeleted(false);
                customerRepository.save(customer);
            }

            log.info("고객 데이터 생성 완료: {}개", customerNames.length);
        }
    }

    /**
     * 주문 데이터 생성 (15개)
     */
    @Transactional
    private void createOrders() {
        if (orderRepository.count() == 0) {
            // 회사가 없으면 생성
            Company company = companyRepository.findById(1L)
                .orElseGet(() -> {
                    log.warn("회사가 없어서 새로 생성합니다");
                    Company newCompany = new Company();
                    newCompany.setCompanyCode("COMP001");
                    newCompany.setName("ABC 기업");
                    newCompany.setBusinessNumber("123-45-67890");
                    newCompany.setCeoName("김철수");
                    newCompany.setAddress("서울시 강남구 테헤란로 123");
                    newCompany.setPhone("02-1234-5678");
                    newCompany.setEmail("info@abc.com");
                    newCompany.setWebsite("https://abc.com");
                    newCompany.setBusinessType("IT");
                    newCompany.setStatus(Company.CompanyStatus.ACTIVE);
                    newCompany.setCreatedBy(1L);
                    newCompany.setUpdatedBy(1L);
                    newCompany.setIsDeleted(false);
                    return companyRepository.save(newCompany);
                });

            // 고객 목록 가져오기
            List<Customer> customers = customerRepository.findByCompanyId(company.getId());
            if (customers.isEmpty()) {
                log.warn("고객 데이터가 없어 주문을 생성할 수 없습니다.");
                return;
            }

            String[] orderNumbers = {
                "ORD001", "ORD002", "ORD003", "ORD004", "ORD005",
                "ORD006", "ORD007", "ORD008", "ORD009", "ORD010",
                "ORD011", "ORD012", "ORD013", "ORD014", "ORD015"
            };

            Order.OrderStatus[] orderStatuses = {
                Order.OrderStatus.PENDING, Order.OrderStatus.CONFIRMED, Order.OrderStatus.PROCESSING,
                Order.OrderStatus.SHIPPED, Order.OrderStatus.DELIVERED, Order.OrderStatus.PENDING,
                Order.OrderStatus.CONFIRMED, Order.OrderStatus.PROCESSING, Order.OrderStatus.SHIPPED,
                Order.OrderStatus.DELIVERED, Order.OrderStatus.PENDING, Order.OrderStatus.CONFIRMED,
                Order.OrderStatus.PROCESSING, Order.OrderStatus.SHIPPED, Order.OrderStatus.DELIVERED
            };

            Order.PaymentStatus[] paymentStatuses = {
                Order.PaymentStatus.PENDING, Order.PaymentStatus.PAID, Order.PaymentStatus.PARTIAL,
                Order.PaymentStatus.PAID, Order.PaymentStatus.PAID, Order.PaymentStatus.PENDING,
                Order.PaymentStatus.PAID, Order.PaymentStatus.PARTIAL, Order.PaymentStatus.PAID,
                Order.PaymentStatus.PAID, Order.PaymentStatus.PENDING, Order.PaymentStatus.PAID,
                Order.PaymentStatus.PARTIAL, Order.PaymentStatus.PAID, Order.PaymentStatus.PAID
            };

            java.math.BigDecimal[] amounts = {
                new java.math.BigDecimal("1500000"), new java.math.BigDecimal("2500000"), new java.math.BigDecimal("3500000"),
                new java.math.BigDecimal("4500000"), new java.math.BigDecimal("5500000"), new java.math.BigDecimal("6500000"),
                new java.math.BigDecimal("7500000"), new java.math.BigDecimal("8500000"), new java.math.BigDecimal("9500000"),
                new java.math.BigDecimal("10500000"), new java.math.BigDecimal("11500000"), new java.math.BigDecimal("12500000"),
                new java.math.BigDecimal("13500000"), new java.math.BigDecimal("14500000"), new java.math.BigDecimal("15500000")
            };

            for (int i = 0; i < orderNumbers.length; i++) {
                Order order = new Order();
                order.setOrderNumber(orderNumbers[i]);
                order.setCompany(company);
                order.setCustomer(customers.get(i % customers.size())); // 고객 순환 할당
                order.setOrderDate(java.time.LocalDate.now().minusDays(i));
                order.setDeliveryDate(java.time.LocalDate.now().plusDays(i + 1));
                order.setOrderStatus(orderStatuses[i]);
                order.setPaymentStatus(paymentStatuses[i]);
                order.setTotalAmount(amounts[i]);
                order.setCreatedBy(1L);
                order.setUpdatedBy(1L);
                order.setIsDeleted(false);
                orderRepository.save(order);
            }

            log.info("주문 데이터 생성 완료: {}개", orderNumbers.length);
        }
    }

    /**
     * 테스트용 알림 생성 (admin과 user 각각 다른 알림)
     */
    private void createTestNotifications() {
        try {
            log.info("테스트 알림 생성 시작");

            // admin 사용자 조회
            User adminUser = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("admin 사용자를 찾을 수 없습니다"));

            // user 사용자 조회
            User normalUser = userRepository.findByUsername("user")
                    .orElseThrow(() -> new RuntimeException("user 사용자를 찾을 수 없습니다"));

            // 기존 알림 데이터 삭제 (테스트 환경에서만)
            deleteExistingNotifications(adminUser.getId());
            deleteExistingNotifications(normalUser.getId());

            // admin 전용 알림 생성
            createAdminNotifications(adminUser);
            
            // user 전용 알림 생성
            createUserNotifications(normalUser);

            log.info("테스트 알림 생성 완료: admin용 4개, user용 4개");
        } catch (Exception e) {
            log.error("테스트 알림 생성 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * 기존 알림 데이터 삭제
     */
    private void deleteExistingNotifications(Long userId) {
        try {
            // NotificationService를 통해 사용자의 모든 알림 삭제
            notificationService.deleteAllNotificationsByUser(userId);
            log.info("사용자 {}의 기존 알림 데이터 삭제 완료", userId);
        } catch (Exception e) {
            log.warn("사용자 {}의 기존 알림 데이터 삭제 실패: {}", userId, e.getMessage());
        }
    }

    /**
     * admin 전용 알림 생성
     */
    private void createAdminNotifications(User adminUser) {
        String[] adminTitles = {
            "관리자 승인 요청",
            "시스템 보안 경고",
            "사용자 계정 잠금",
            "데이터베이스 백업 완료"
        };

        String[] adminMessages = {
            "새로운 사용자 '김신입'의 계정 승인이 필요합니다.",
            "의심스러운 로그인 시도가 감지되었습니다. (IP: 192.168.1.100)",
            "사용자 'user2'의 계정이 5회 로그인 실패로 잠겼습니다.",
            "일일 데이터베이스 백업이 성공적으로 완료되었습니다."
        };

        Notification.NotificationType[] adminTypes = {
            Notification.NotificationType.INFO,
            Notification.NotificationType.WARNING,
            Notification.NotificationType.ERROR,
            Notification.NotificationType.SUCCESS
        };

        String[] adminActionUrls = {
            "/admin/users/approval",
            "/admin/security/logs",
            "/admin/users/locked",
            null
        };

        for (int i = 0; i < adminTitles.length; i++) {
            notificationService.createNotification(
                adminUser,
                adminTitles[i],
                adminMessages[i],
                adminTypes[i],
                adminActionUrls[i]
            );
        }

        log.info("admin 전용 알림 생성 완료: {}개", adminTitles.length);
    }

    /**
     * user 전용 알림 생성
     */
    private void createUserNotifications(User normalUser) {
        String[] userTitles = {
            "새로운 할당 업무",
            "근무 시간 확인",
            "휴가 신청 결과",
            "교육 과정 추천"
        };

        String[] userMessages = {
            "새로운 프로젝트 'ERP 시스템 개발'에 할당되었습니다.",
            "이번 주 근무 시간이 40시간을 초과했습니다. 확인해주세요.",
            "휴가 신청이 승인되었습니다. (2024-01-15 ~ 2024-01-17)",
            "새로운 교육 과정 'Spring Boot 고급'이 개설되었습니다."
        };

        Notification.NotificationType[] userTypes = {
            Notification.NotificationType.INFO,
            Notification.NotificationType.WARNING,
            Notification.NotificationType.SUCCESS,
            Notification.NotificationType.INFO
        };

        String[] userActionUrls = {
            "/projects/current",
            "/attendance/timesheet",
            "/hr/vacation/history",
            "/training/courses"
        };

        for (int i = 0; i < userTitles.length; i++) {
            notificationService.createNotification(
                normalUser,
                userTitles[i],
                userMessages[i],
                userTypes[i],
                userActionUrls[i]
            );
        }

        log.info("user 전용 알림 생성 완료: {}개", userTitles.length);
    }
}
