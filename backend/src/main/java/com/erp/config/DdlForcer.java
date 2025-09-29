package com.erp.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * DDL 강제 실행 컴포넌트
 * Hibernate DDL이 실행되지 않을 경우 강제로 실행합니다
 */
@Component
@RequiredArgsConstructor
public class DdlForcer {

    private static final Logger log = LoggerFactory.getLogger(DdlForcer.class);

    @PersistenceContext
    private EntityManager entityManager;
    
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Order(1) // 가장 먼저 실행
    public void forceDdlExecution() {
        try {
            log.info("=== DDL 강제 실행 시작 ===");
            
            // 1. 기존 테이블들 삭제 (개별 트랜잭션으로 처리)
            dropExistingTables();
            
            // 2. Hibernate 스키마 생성 강제 실행
            log.info("Hibernate 스키마 생성 강제 실행...");
            entityManager.getEntityManagerFactory().getMetamodel().getEntities().forEach(entityType -> {
                log.info("엔티티 발견: {}", entityType.getName());
            });
            
            // 3. 직접 테이블 생성 (complete_schema.sql 대신)
            log.info("직접 테이블 생성 방식으로 전환...");
            createTablesWithHardcodedSql();
            
            // 4. 데이터 초기화 실행
            initializeData();
            
            // 5. DB 코멘트 추가
            addDatabaseComments();
            
            log.info("=== DDL 강제 실행 완료 ===");
            
        } catch (Exception e) {
            log.error("DDL 강제 실행 중 오류 발생: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 기존 테이블들 삭제 (개별 트랜잭션으로 처리)
     */
    private void dropExistingTables() {
            log.info("기존 테이블 삭제 중...");
            String[] dropTables = {
                "DROP TABLE IF EXISTS notifications CASCADE",
                "DROP TABLE IF EXISTS stock_movements CASCADE",
                "DROP TABLE IF EXISTS inventories CASCADE", 
                "DROP TABLE IF EXISTS orders CASCADE",
                "DROP TABLE IF EXISTS warehouses CASCADE",
                "DROP TABLE IF EXISTS customers CASCADE",
                "DROP TABLE IF EXISTS positions CASCADE",
                "DROP TABLE IF EXISTS employees CASCADE",
                "DROP TABLE IF EXISTS users CASCADE",
                "DROP TABLE IF EXISTS products CASCADE",
                "DROP TABLE IF EXISTS product_categories CASCADE",
                "DROP TABLE IF EXISTS accounts CASCADE",
                "DROP TABLE IF EXISTS departments CASCADE",
                "DROP TABLE IF EXISTS companies CASCADE"
            };
            
            for (String sql : dropTables) {
            try {
                // 각 삭제 작업을 개별 트랜잭션으로 처리
                transactionTemplate.execute(status -> {
                try {
                    jdbcTemplate.execute(sql);
                    log.info("✅ 테이블 삭제 완료: {}", sql);
                        return null;
                } catch (Exception e) {
                    log.warn("⚠️ 테이블 삭제 중 오류 (무시됨): {} - {}", sql, e.getMessage());
                        return null;
                    }
                });
        } catch (Exception e) {
                log.warn("⚠️ 테이블 삭제 트랜잭션 중 오류: {} - {}", sql, e.getMessage());
            }
        }
    }
    
    /**
     * 하드코딩된 SQL로 테이블 생성 (폴백용)
     */
    private void createTablesWithHardcodedSql() {
        log.info("하드코딩된 SQL로 테이블 생성 중...");
        
        // 각 테이블을 개별 트랜잭션으로 생성
        String[] createTables = {
                "CREATE TABLE IF NOT EXISTS companies (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_code VARCHAR(20) UNIQUE NOT NULL, " +
                "name VARCHAR(200) NOT NULL, " +
                "name_en VARCHAR(200), " +
                "business_number VARCHAR(12) UNIQUE, " +
                "corporation_number VARCHAR(14), " +
                "ceo_name VARCHAR(50), " +
                "business_type VARCHAR(100), " +
                "business_item VARCHAR(100), " +
                "address VARCHAR(500), " +
                "detailed_address VARCHAR(200), " +
                "postal_code VARCHAR(5), " +
                "phone VARCHAR(20), " +
                "fax VARCHAR(20), " +
                "email VARCHAR(100), " +
                "website VARCHAR(200), " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS departments (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_id BIGINT NOT NULL, " +
                "department_code VARCHAR(20) NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "name_en VARCHAR(200), " +
                "description TEXT, " +
                "parent_department_id BIGINT, " +
                "manager_id BIGINT, " +
                "level INTEGER NOT NULL DEFAULT 1, " +
                "sort_order INTEGER DEFAULT 0, " +
                "department_type VARCHAR(20) NOT NULL DEFAULT 'DEPARTMENT', " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (company_id) REFERENCES companies(id)" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS users (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(200) NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "full_name VARCHAR(100) NOT NULL, " +
                "phone VARCHAR(20), " +
                "phone_number VARCHAR(20), " +
                "position VARCHAR(100), " +
                "role VARCHAR(20) NOT NULL DEFAULT 'USER', " +
                "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                "is_locked BOOLEAN NOT NULL DEFAULT FALSE, " +
                "is_password_expired BOOLEAN NOT NULL DEFAULT FALSE, " +
                "last_login_at TIMESTAMP, " +
                "password_changed_at TIMESTAMP, " +
                "company_id BIGINT NOT NULL, " +
                "department_id BIGINT, " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (company_id) REFERENCES companies(id), " +
                "FOREIGN KEY (department_id) REFERENCES departments(id)" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS positions (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_id BIGINT NOT NULL, " +
                "position_code VARCHAR(20) NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "description TEXT, " +
                "level INTEGER NOT NULL DEFAULT 1, " +
                "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (company_id) REFERENCES companies(id)" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS employees (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_id BIGINT NOT NULL, " +
                "department_id BIGINT NOT NULL, " +
                "position_id BIGINT, " +
                "employee_number VARCHAR(20) UNIQUE NOT NULL, " +
                "name VARCHAR(50) NOT NULL, " +
                "name_en VARCHAR(100), " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "phone VARCHAR(20), " +
                "mobile VARCHAR(20), " +
                "resident_number VARCHAR(200), " +
                "birth_date DATE, " +
                "gender VARCHAR(10), " +
                "address VARCHAR(500), " +
                "detailed_address VARCHAR(200), " +
                "postal_code VARCHAR(5), " +
                "hire_date DATE NOT NULL, " +
                "termination_date DATE, " +
                "employment_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "employment_type VARCHAR(20), " +
                "bank_name VARCHAR(50), " +
                "account_number VARCHAR(50), " +
                "account_holder VARCHAR(50), " +
                "emergency_contact VARCHAR(20), " +
                "emergency_relation VARCHAR(20), " +
                "education VARCHAR(100), " +
                "major VARCHAR(100), " +
                "career VARCHAR(1000), " +
                "skills VARCHAR(500), " +
                "certifications VARCHAR(500), " +
                "memo TEXT, " +
                "profile_image_url VARCHAR(500), " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (company_id) REFERENCES companies(id), " +
                "FOREIGN KEY (department_id) REFERENCES departments(id)" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS product_categories (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_id BIGINT NOT NULL, " +
                "parent_category_id BIGINT, " +
                "category_code VARCHAR(20) UNIQUE NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "description TEXT, " +
                "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (company_id) REFERENCES companies(id), " +
                "FOREIGN KEY (parent_category_id) REFERENCES product_categories(id)" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS products (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_id BIGINT NOT NULL, " +
                "category_id BIGINT NOT NULL, " +
                "product_code VARCHAR(30) NOT NULL, " +
                "product_name VARCHAR(200) NOT NULL, " +
                "product_name_en VARCHAR(200), " +
                "description VARCHAR(1000), " +
                "detailed_description TEXT, " +
                "product_type VARCHAR(20) NOT NULL DEFAULT 'FINISHED_GOODS', " +
                "product_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "stock_management_type VARCHAR(20) NOT NULL DEFAULT 'FIFO', " +
                "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                "track_inventory BOOLEAN NOT NULL DEFAULT TRUE, " +
                "barcode VARCHAR(50), " +
                "qr_code VARCHAR(200), " +
                "sku VARCHAR(50), " +
                "base_unit VARCHAR(10) NOT NULL, " +
                "sub_unit VARCHAR(10), " +
                "unit_conversion_rate DECIMAL(10,4) DEFAULT 1, " +
                "standard_cost DECIMAL(15,2) DEFAULT 0, " +
                "average_cost DECIMAL(15,2) DEFAULT 0, " +
                "last_purchase_price DECIMAL(15,2) DEFAULT 0, " +
                "selling_price DECIMAL(15,2) DEFAULT 0, " +
                "min_selling_price DECIMAL(15,2) DEFAULT 0, " +
                "safety_stock DECIMAL(12,3) DEFAULT 0, " +
                "min_stock DECIMAL(12,3) DEFAULT 0, " +
                "max_stock DECIMAL(12,3) DEFAULT 0, " +
                "reorder_point DECIMAL(12,3) DEFAULT 0, " +
                "reorder_quantity DECIMAL(12,3) DEFAULT 0, " +
                "lead_time_days INTEGER DEFAULT 0, " +
                "shelf_life_days INTEGER, " +
                "width DECIMAL(8,2), " +
                "height DECIMAL(8,2), " +
                "depth DECIMAL(8,2), " +
                "weight DECIMAL(8,3), " +
                "volume DECIMAL(12,3), " +
                "color VARCHAR(30), " +
                "size VARCHAR(30), " +
                "brand VARCHAR(50), " +
                "manufacturer VARCHAR(100), " +
                "supplier VARCHAR(100), " +
                "origin_country VARCHAR(50), " +
                "hs_code VARCHAR(20), " +
                "tax_rate DECIMAL(5,2) DEFAULT 0, " +
                "image_paths TEXT, " +
                "attachment_paths TEXT, " +
                "tags VARCHAR(500), " +
                "sort_order INTEGER DEFAULT 0, " +
                "metadata TEXT, " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (company_id) REFERENCES companies(id), " +
                "FOREIGN KEY (category_id) REFERENCES product_categories(id)" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS accounts (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_id BIGINT NOT NULL, " +
                "parent_account_id BIGINT, " +
                "account_code VARCHAR(20) NOT NULL, " +
                "account_name VARCHAR(100) NOT NULL, " +
                "account_name_en VARCHAR(200), " +
                "description VARCHAR(500), " +
                "account_type VARCHAR(50) NOT NULL, " +
                "account_category VARCHAR(50) NOT NULL, " +
                "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (company_id) REFERENCES companies(id), " +
                "FOREIGN KEY (parent_account_id) REFERENCES accounts(id)" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS customers (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_id BIGINT NOT NULL, " +
                "customer_code VARCHAR(20) NOT NULL, " +
                "customer_name VARCHAR(200) NOT NULL, " +
                "customer_type VARCHAR(20) NOT NULL DEFAULT 'INDIVIDUAL', " +
                "customer_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "customer_grade VARCHAR(20), " +
                "business_registration_number VARCHAR(12), " +
                "ceo_name VARCHAR(50), " +
                "phone VARCHAR(20), " +
                "email VARCHAR(100), " +
                "address VARCHAR(500), " +
                "sales_manager_id BIGINT, " +
                "credit_limit DECIMAL(15,2), " +
                "payment_terms VARCHAR(50), " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (company_id) REFERENCES companies(id)" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS orders (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_id BIGINT NOT NULL, " +
                "customer_id BIGINT NOT NULL, " +
                "order_number VARCHAR(50) NOT NULL, " +
                "order_date DATE NOT NULL, " +
                "order_status VARCHAR(20) NOT NULL DEFAULT 'PENDING', " +
                "total_amount DECIMAL(15,2), " +
                "payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID', " +
                "delivery_date DATE, " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (company_id) REFERENCES companies(id), " +
                "FOREIGN KEY (customer_id) REFERENCES customers(id)" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS warehouses (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_id BIGINT NOT NULL, " +
                "warehouse_code VARCHAR(20) NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "location VARCHAR(200), " +
                "capacity INTEGER, " +
                "warehouse_type VARCHAR(20) NOT NULL DEFAULT 'MAIN', " +
                "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (company_id) REFERENCES companies(id)" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS inventories (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_id BIGINT NOT NULL, " +
                "product_id BIGINT NOT NULL, " +
                "warehouse_id BIGINT NOT NULL, " +
                "quantity INTEGER NOT NULL DEFAULT 0, " +
                "reserved_quantity INTEGER NOT NULL DEFAULT 0, " +
                "available_quantity INTEGER NOT NULL DEFAULT 0, " +
                "reorder_point INTEGER DEFAULT 0, " +
                "max_stock INTEGER, " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (company_id) REFERENCES companies(id), " +
                "FOREIGN KEY (product_id) REFERENCES products(id), " +
                "FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS stock_movements (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_id BIGINT NOT NULL, " +
                "product_id BIGINT NOT NULL, " +
                "warehouse_id BIGINT NOT NULL, " +
                "movement_type VARCHAR(20) NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "reference_type VARCHAR(20), " +
                "reference_id BIGINT, " +
                "movement_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "reason VARCHAR(200), " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (company_id) REFERENCES companies(id), " +
                "FOREIGN KEY (product_id) REFERENCES products(id), " +
                "FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS notifications (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "title VARCHAR(200) NOT NULL, " +
                "message VARCHAR(1000) NOT NULL, " +
                "type VARCHAR(20) NOT NULL, " +
                "is_read BOOLEAN NOT NULL DEFAULT FALSE, " +
                "action_url VARCHAR(500), " +
                "read_at TIMESTAMP, " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")"
            };
            
        for (String sql : createTables) {
            int retryCount = 0;
            int maxRetries = 3;
            boolean success = false;
            
            while (retryCount < maxRetries && !success) {
                try {
                    // 각 테이블을 개별 트랜잭션으로 생성
                    transactionTemplate.execute(status -> {
                        try {
                            jdbcTemplate.execute(sql);
                            log.info("✅ 테이블 생성 성공");
                            return null;
                        } catch (Exception e) {
                            log.warn("⚠️ 테이블 생성 중 오류 (이미 존재할 수 있음): {}", e.getMessage());
                            return null;
                        }
                    });
                    success = true;
                } catch (Exception e) {
                    retryCount++;
                    if (retryCount < maxRetries) {
                        log.warn("⚠️ 테이블 생성 실패, 재시도 {}/{}: {}", retryCount, maxRetries, e.getMessage());
                        try {
                            Thread.sleep(2000); // 2초 대기
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    } else {
                        log.error("❌ 테이블 생성 최종 실패 (재시도 {}회 후): {}", maxRetries, e.getMessage());
                    }
                }
            }
        }
        log.info("✅ 하드코딩된 테이블 생성 완료");
    }
    
    /**
     * 데이터 초기화 실행 (안전한 개별 트랜잭션 처리)
     */
    private void initializeData() {
        log.info("=== 데이터 초기화 시작 ===");
        
        // 각 데이터 삽입을 개별적으로 안전하게 처리 (외래키 의존성 순서 고려)
        insertCompanyData();
        insertDepartmentData();
        insertUserData();
        insertPositionData();
        insertEmployeeData();
        insertProductCategoryData();
        insertWarehouseData();  // 상품보다 먼저 삽입
        insertProductData();
        insertInventoryData();  // 상품과 창고 후에 삽입
        insertCustomerData();
        
        log.info("=== 데이터 초기화 완료 ===");
    }
    
    /**
     * 회사 데이터 삽입
     */
    private void insertCompanyData() {
        String[] companyInserts = {
            "INSERT INTO companies (id, company_code, name, name_en, business_number, corporation_number, ceo_name, business_type, business_item, address, detailed_address, postal_code, phone, fax, email, website, status, is_deleted) VALUES (1, 'ABC_CORP', 'ABC기업', 'ABC Corporation', '123-45-67890', '123456-1234567', '김대표', 'IT 서비스', '소프트웨어 개발', '서울시 강남구 테헤란로 123', 'ABC빌딩 10층', '06292', '02-1234-5678', '02-1234-5679', 'info@abc.com', 'https://www.abc.com', 'ACTIVE', false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO companies (id, company_code, name, name_en, business_number, corporation_number, ceo_name, business_type, business_item, address, detailed_address, postal_code, phone, fax, email, website, status, is_deleted) VALUES (2, 'XYZ_GROUP', 'XYZ그룹', 'XYZ Group', '234-56-78901', '234567-2345678', '이사장', '제조업', '제품 제조', '경기도 성남시 분당구 판교역로 456', 'XYZ빌딩 15층', '13494', '031-234-5678', '031-234-5679', 'info@xyz.com', 'https://www.xyz.com', 'ACTIVE', false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO companies (id, company_code, name, name_en, business_number, corporation_number, ceo_name, business_type, business_item, address, detailed_address, postal_code, phone, fax, email, website, status, is_deleted) VALUES (3, 'DEF_CORP', 'DEF코퍼레이션', 'DEF Corporation', '345-67-89012', '345678-3456789', '박대표', '서비스업', '고객 서비스', '서울시 서초구 서초대로 789', 'DEF빌딩 8층', '06620', '02-345-6789', '02-345-6790', 'info@def.com', 'https://www.def.com', 'ACTIVE', false) ON CONFLICT (id) DO NOTHING"
        };
        
        for (String sql : companyInserts) {
            executeSafeInsert(sql, "회사 데이터");
        }
    }
    
    /**
     * 부서 데이터 삽입
     */
    private void insertDepartmentData() {
        String[] departmentInserts = {
            "INSERT INTO departments (id, department_code, name, description, parent_department_id, manager_id, company_id, department_type) VALUES (1, 'HR_DEPT', '인사부', '인사관리 및 채용업무', NULL, NULL, 1, 'DEPARTMENT') ON CONFLICT (id) DO NOTHING",
            "INSERT INTO departments (id, department_code, name, description, parent_department_id, manager_id, company_id, department_type) VALUES (2, 'SALES_DEPT', '영업부', '영업 및 고객관리', NULL, NULL, 1, 'DEPARTMENT') ON CONFLICT (id) DO NOTHING",
            "INSERT INTO departments (id, department_code, name, description, parent_department_id, manager_id, company_id, department_type) VALUES (3, 'IT_DEPT', 'IT부서', '시스템 개발 및 유지보수', NULL, NULL, 1, 'DEPARTMENT') ON CONFLICT (id) DO NOTHING"
        };
        
        for (String sql : departmentInserts) {
            executeSafeInsert(sql, "부서 데이터");
        }
    }
    
    /**
     * 사용자 데이터 삽입
     */
    private void insertUserData() {
            try {
                String adminPassword = passwordEncoder.encode("admin123");
            String userPassword = passwordEncoder.encode("user123");
                
                // 비밀번호 검증
            boolean adminMatches = passwordEncoder.matches("admin123", adminPassword);
            boolean userMatches = passwordEncoder.matches("user123", userPassword);
            log.info("🔐 admin 비밀번호 검증 결과: {}", adminMatches);
            log.info("🔐 user 비밀번호 검증 결과: {}", userMatches);
            
            String[] userInserts = {
                String.format("INSERT INTO users (id, username, password, email, full_name, phone, role, is_active, is_locked, is_password_expired, company_id, department_id, position_id, password_changed_at) VALUES (1, 'admin', '%s', 'admin@abc.com', '관리자', '02-1234-5678', 'ADMIN', true, false, false, 1, 1, 1, NOW()) ON CONFLICT (id) DO NOTHING", adminPassword),
                String.format("INSERT INTO users (id, username, password, email, full_name, phone, role, is_active, is_locked, is_password_expired, company_id, department_id, position_id, password_changed_at) VALUES (2, 'user', '%s', 'user@abc.com', '일반사용자', '02-2345-6789', 'USER', true, false, false, 1, 3, 3, NOW()) ON CONFLICT (id) DO NOTHING", userPassword)
            };
            
            for (String sql : userInserts) {
                executeSafeInsert(sql, "사용자 데이터");
            }
            
            log.info("✅ 로그인 계정 정보:");
            log.info("   👤 admin 계정 - 사용자명: admin, 비밀번호: admin123, 역할: ADMIN");
            log.info("   👤 user 계정 - 사용자명: user, 비밀번호: user123, 역할: USER");
            
            } catch (Exception e) {
            log.warn("⚠️ 사용자 데이터 삽입 중 오류: {}", e.getMessage());
        }
    }
    
    /**
     * 직급 데이터 삽입
     */
    private void insertPositionData() {
        String[] positionInserts = {
            "INSERT INTO positions (id, company_id, position_code, name, description, level, is_active, is_deleted) VALUES (1, 1, 'CEO', '대표이사', '최고경영자', 1, true, false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO positions (id, company_id, position_code, name, description, level, is_active, is_deleted) VALUES (2, 1, 'MANAGER', '부장', '부서장', 4, true, false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO positions (id, company_id, position_code, name, description, level, is_active, is_deleted) VALUES (3, 1, 'STAFF', '대리', '대리급', 7, true, false) ON CONFLICT (id) DO NOTHING"
        };
        
        for (String sql : positionInserts) {
            executeSafeInsert(sql, "직급 데이터");
        }
    }
    
    /**
     * 직원 데이터 삽입
     */
    private void insertEmployeeData() {
        String[] employeeInserts = {
            "INSERT INTO employees (id, company_id, department_id, position_id, employee_number, name, email, phone, hire_date, employment_status, address, birth_date, is_deleted) VALUES (1, 1, 1, 1, 'EMP001', '김관리', 'admin@abc.com', '010-1234-5678', '2020-01-01', 'ACTIVE', '서울특별시 강남구', '1980-01-01', false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO employees (id, company_id, department_id, position_id, employee_number, name, email, phone, hire_date, employment_status, address, birth_date, is_deleted) VALUES (2, 1, 2, 2, 'EMP002', '이영업', 'sales@abc.com', '010-2345-6789', '2020-02-01', 'ACTIVE', '서울특별시 서초구', '1985-05-15', false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO employees (id, company_id, department_id, position_id, employee_number, name, email, phone, hire_date, employment_status, address, birth_date, is_deleted) VALUES (3, 1, 3, 3, 'EMP003', '박개발', 'dev@abc.com', '010-3456-7890', '2020-03-01', 'ACTIVE', '서울특별시 마포구', '1990-08-20', false) ON CONFLICT (id) DO NOTHING"
        };
        
        for (String sql : employeeInserts) {
            executeSafeInsert(sql, "직원 데이터");
        }
    }
    
    /**
     * 상품 카테고리 데이터 삽입
     */
    private void insertProductCategoryData() {
        String[] categoryInserts = {
            "INSERT INTO product_categories (id, company_id, category_code, name, description, parent_category_id, is_active, is_deleted) VALUES (1, 1, 'ELECTRONICS', '전자제품', '전자제품 카테고리', NULL, true, false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO product_categories (id, company_id, category_code, name, description, parent_category_id, is_active, is_deleted) VALUES (2, 1, 'COMPUTER', '컴퓨터', '컴퓨터 및 주변기기', 1, true, false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO product_categories (id, company_id, category_code, name, description, parent_category_id, is_active, is_deleted) VALUES (3, 1, 'OFFICE', '사무용품', '사무용품 및 소모품', NULL, true, false) ON CONFLICT (id) DO NOTHING"
        };
        
        for (String sql : categoryInserts) {
            executeSafeInsert(sql, "상품 카테고리 데이터");
        }
    }
    
    /**
     * 상품 데이터 삽입
     */
    private void insertProductData() {
        String[] productInserts = {
            "INSERT INTO products (id, company_id, category_id, product_code, product_name, description, product_type, product_status, base_unit, standard_cost, selling_price, is_active, is_deleted) VALUES (1, 1, 2, 'LAPTOP001', '노트북', '고성능 노트북', 'FINISHED_GOODS', 'ACTIVE', '대', 1200000, 1500000, true, false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO products (id, company_id, category_id, product_code, product_name, description, product_type, product_status, base_unit, standard_cost, selling_price, is_active, is_deleted) VALUES (2, 1, 2, 'MOUSE001', '무선마우스', '블루투스 무선마우스', 'FINISHED_GOODS', 'ACTIVE', '개', 30000, 50000, true, false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO products (id, company_id, category_id, product_code, product_name, description, product_type, product_status, base_unit, standard_cost, selling_price, is_active, is_deleted) VALUES (3, 1, 3, 'PEN001', '볼펜', '검은색 볼펜', 'CONSUMABLE', 'ACTIVE', '자루', 500, 1000, true, false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO products (id, company_id, category_id, product_code, product_name, description, product_type, product_status, base_unit, standard_cost, selling_price, is_active, is_deleted) VALUES (4, 1, 3, 'A4PAPER001', 'A4용지', 'A4 복사용지 80g', 'CONSUMABLE', 'ACTIVE', '박스', 15000, 25000, true, false) ON CONFLICT (id) DO NOTHING"
        };
        
        for (String sql : productInserts) {
            executeSafeInsert(sql, "상품 데이터");
        }
    }
    
    /**
     * 창고 데이터 삽입
     */
    private void insertWarehouseData() {
        String[] warehouseInserts = {
            "INSERT INTO warehouses (id, company_id, warehouse_code, name, location, capacity, warehouse_type, is_active, is_deleted) VALUES (1, 1, 'MAIN_WH', '본사창고', '서울특별시 강남구 테헤란로 123', 1000, 'MAIN', true, false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO warehouses (id, company_id, warehouse_code, name, location, capacity, warehouse_type, is_active, is_deleted) VALUES (2, 1, 'SUB_WH', '지점창고', '서울특별시 서초구 서초대로 456', 500, 'BRANCH', true, false) ON CONFLICT (id) DO NOTHING"
        };
        
        for (String sql : warehouseInserts) {
            executeSafeInsert(sql, "창고 데이터");
        }
    }
    
    /**
     * 재고 데이터 삽입
     */
    private void insertInventoryData() {
        String[] inventoryInserts = {
            "INSERT INTO inventories (id, company_id, product_id, warehouse_id, quantity, reserved_quantity, available_quantity, reorder_point, max_stock, is_deleted) VALUES (1, 1, 1, 1, 10, 0, 10, 2, 50, false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO inventories (id, company_id, product_id, warehouse_id, quantity, reserved_quantity, available_quantity, reorder_point, max_stock, is_deleted) VALUES (2, 1, 2, 1, 50, 5, 45, 10, 100, false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO inventories (id, company_id, product_id, warehouse_id, quantity, reserved_quantity, available_quantity, reorder_point, max_stock, is_deleted) VALUES (3, 1, 3, 1, 200, 0, 200, 50, 500, false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO inventories (id, company_id, product_id, warehouse_id, quantity, reserved_quantity, available_quantity, reorder_point, max_stock, is_deleted) VALUES (4, 1, 4, 1, 30, 0, 30, 5, 100, false) ON CONFLICT (id) DO NOTHING"
        };
        
        for (String sql : inventoryInserts) {
            executeSafeInsert(sql, "재고 데이터");
        }
    }
    
    /**
     * 고객 데이터 삽입
     */
    private void insertCustomerData() {
        String[] customerInserts = {
            "INSERT INTO customers (id, company_id, customer_code, customer_name, customer_type, phone, email, address, is_deleted) VALUES (1, 1, 'CUST001', 'ABC 기업', 'CORPORATE', '02-1234-5678', 'info@abc.com', '서울특별시 강남구 테헤란로 123', false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO customers (id, company_id, customer_code, customer_name, customer_type, phone, email, address, is_deleted) VALUES (2, 1, 'CUST002', 'XYZ 주식회사', 'CORPORATE', '02-2345-6789', 'contact@xyz.com', '서울특별시 서초구 서초대로 456', false) ON CONFLICT (id) DO NOTHING",
            "INSERT INTO customers (id, company_id, customer_code, customer_name, customer_type, phone, email, address, is_deleted) VALUES (3, 1, 'CUST003', '홍길동', 'INDIVIDUAL', '010-5555-6666', 'hong@email.com', '서울특별시 마포구 홍대입구역', false) ON CONFLICT (id) DO NOTHING"
        };
        
        for (String sql : customerInserts) {
            executeSafeInsert(sql, "고객 데이터");
        }
    }
    
    /**
     * 안전한 데이터 삽입 실행 (개별 트랜잭션 + 재시도 로직)
     */
    private void executeSafeInsert(String sql, String dataType) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                // 개별 트랜잭션으로 실행하여 연결 문제 격리
                transactionTemplate.execute(status -> {
                    try {
                        jdbcTemplate.execute(sql);
                        log.info("✅ {} 삽입 성공", dataType);
                        return null;
                    } catch (Exception e) {
                        log.warn("⚠️ {} 삽입 중 오류: {}", dataType, e.getMessage());
                        // 트랜잭션 롤백
                        status.setRollbackOnly();
                        return null;
                    }
                });
                // 성공 시 루프 종료
                break;
            } catch (Exception e) {
                retryCount++;
                log.warn("⚠️ {} 삽입 트랜잭션 중 오류 (시도 {}/{}): {}", dataType, retryCount, maxRetries, e.getMessage());
                
                // 연결 문제 시 재시도
                if (e.getMessage().contains("I/O error") || e.getMessage().contains("Connection")) {
                    if (retryCount < maxRetries) {
                        log.info("🔄 데이터베이스 연결 문제 감지, 2초 후 재시도...");
                        try {
                            Thread.sleep(2000); // 2초 대기
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    } else {
                        log.warn("⚠️ 최대 재시도 횟수 초과, {} 삽입 건너뜀", dataType);
                    }
                } else {
                    // 연결 문제가 아닌 경우 재시도하지 않음
                    break;
                }
            }
        }
    }

    /**
     * DB 테이블과 컬럼에 코멘트 추가
     */
    private void addDatabaseComments() {
        log.info("=== DB 코멘트 추가 시작 ===");
        
        try {
            // 테이블 코멘트 추가 (개별 트랜잭션)
            addTableComments();
            
            // 컬럼 코멘트 추가 (개별 트랜잭션)
            addColumnComments();
            
            log.info("✅ DB 코멘트 추가 완료");
            } catch (Exception e) {
            log.warn("⚠️ DB 코멘트 추가 중 오류: {}", e.getMessage());
        }
    }

    /**
     * 테이블 코멘트 추가
     */
    private void addTableComments() {
        String[] tableComments = {
            "COMMENT ON TABLE companies IS '회사 정보 테이블'",
            "COMMENT ON TABLE departments IS '부서 정보 테이블'",
            "COMMENT ON TABLE positions IS '직급 정보 테이블'",
            "COMMENT ON TABLE employees IS '직원 정보 테이블'",
            "COMMENT ON TABLE users IS '사용자 계정 테이블'",
            "COMMENT ON TABLE accounts IS '계정 정보 테이블'",
            "COMMENT ON TABLE customers IS '고객 정보 테이블'",
            "COMMENT ON TABLE products IS '상품 정보 테이블'",
            "COMMENT ON TABLE product_categories IS '상품 카테고리 테이블'",
            "COMMENT ON TABLE orders IS '주문 정보 테이블'",
            "COMMENT ON TABLE inventories IS '재고 정보 테이블'",
            "COMMENT ON TABLE warehouses IS '창고 정보 테이블'",
            "COMMENT ON TABLE stock_movements IS '재고 이동 이력 테이블'",
            "COMMENT ON TABLE notifications IS '알림 정보 테이블'"
        };

        for (String comment : tableComments) {
            executeSafeComment(comment, "테이블 코멘트");
        }
    }

    /**
     * 컬럼 코멘트 추가
     */
    private void addColumnComments() {
        String[] columnComments = {
            // companies 테이블
            "COMMENT ON COLUMN companies.id IS '회사 고유 ID'",
            "COMMENT ON COLUMN companies.company_code IS '회사 코드'",
            "COMMENT ON COLUMN companies.name IS '회사명'",
            "COMMENT ON COLUMN companies.name_en IS '회사명(영문)'",
            "COMMENT ON COLUMN companies.business_number IS '사업자등록번호'",
            "COMMENT ON COLUMN companies.corporation_number IS '법인등록번호'",
            "COMMENT ON COLUMN companies.ceo_name IS '대표자명'",
            "COMMENT ON COLUMN companies.business_type IS '업종'",
            "COMMENT ON COLUMN companies.business_item IS '업태'",
            "COMMENT ON COLUMN companies.address IS '주소'",
            "COMMENT ON COLUMN companies.detailed_address IS '상세주소'",
            "COMMENT ON COLUMN companies.postal_code IS '우편번호'",
            "COMMENT ON COLUMN companies.phone IS '전화번호'",
            "COMMENT ON COLUMN companies.fax IS '팩스번호'",
            "COMMENT ON COLUMN companies.email IS '이메일'",
            "COMMENT ON COLUMN companies.website IS '웹사이트'",
            "COMMENT ON COLUMN companies.status IS '회사 상태'",

            // departments 테이블
            "COMMENT ON COLUMN departments.id IS '부서 고유 ID'",
            "COMMENT ON COLUMN departments.company_id IS '소속 회사 ID'",
            "COMMENT ON COLUMN departments.department_code IS '부서 코드'",
            "COMMENT ON COLUMN departments.name IS '부서명'",
            "COMMENT ON COLUMN departments.name_en IS '부서명(영문)'",
            "COMMENT ON COLUMN departments.description IS '부서 설명'",
            "COMMENT ON COLUMN departments.parent_department_id IS '상위 부서 ID'",
            "COMMENT ON COLUMN departments.manager_id IS '부서장 ID'",
            "COMMENT ON COLUMN departments.level IS '부서 레벨'",
            "COMMENT ON COLUMN departments.sort_order IS '정렬 순서'",
            "COMMENT ON COLUMN departments.department_type IS '부서 유형'",
            "COMMENT ON COLUMN departments.status IS '부서 상태'",

            // positions 테이블
            "COMMENT ON COLUMN positions.id IS '직급 고유 ID'",
            "COMMENT ON COLUMN positions.company_id IS '소속 회사 ID'",
            "COMMENT ON COLUMN positions.position_code IS '직급 코드'",
            "COMMENT ON COLUMN positions.name IS '직급명'",
            "COMMENT ON COLUMN positions.description IS '직급 설명'",
            "COMMENT ON COLUMN positions.level IS '직급 레벨'",
            "COMMENT ON COLUMN positions.is_active IS '활성화 여부'",

            // employees 테이블
            "COMMENT ON COLUMN employees.id IS '직원 고유 ID'",
            "COMMENT ON COLUMN employees.company_id IS '소속 회사 ID'",
            "COMMENT ON COLUMN employees.department_id IS '소속 부서 ID'",
            "COMMENT ON COLUMN employees.position_id IS '직급 ID'",
            "COMMENT ON COLUMN employees.employee_number IS '직원번호'",
            "COMMENT ON COLUMN employees.name IS '직원명'",
            "COMMENT ON COLUMN employees.name_en IS '직원명(영문)'",
            "COMMENT ON COLUMN employees.email IS '이메일'",
            "COMMENT ON COLUMN employees.phone IS '전화번호'",
            "COMMENT ON COLUMN employees.mobile IS '휴대폰번호'",
            "COMMENT ON COLUMN employees.resident_number IS '주민등록번호'",
            "COMMENT ON COLUMN employees.birth_date IS '생년월일'",
            "COMMENT ON COLUMN employees.gender IS '성별'",
            "COMMENT ON COLUMN employees.address IS '주소'",
            "COMMENT ON COLUMN employees.detailed_address IS '상세주소'",
            "COMMENT ON COLUMN employees.postal_code IS '우편번호'",
            "COMMENT ON COLUMN employees.hire_date IS '입사일'",
            "COMMENT ON COLUMN employees.termination_date IS '퇴사일'",
            "COMMENT ON COLUMN employees.employment_status IS '고용 상태'",
            "COMMENT ON COLUMN employees.employment_type IS '고용 유형'",

            // users 테이블 (기본 컬럼만)
            "COMMENT ON COLUMN users.id IS '사용자 고유 ID'",
            "COMMENT ON COLUMN users.username IS '사용자명'",
            "COMMENT ON COLUMN users.password IS '암호화된 비밀번호'",
            "COMMENT ON COLUMN users.email IS '이메일'",
            "COMMENT ON COLUMN users.role IS '사용자 역할'",
            "COMMENT ON COLUMN users.is_active IS '활성화 여부'",

            // customers 테이블 (기본 컬럼만)
            "COMMENT ON COLUMN customers.id IS '고객 고유 ID'",
            "COMMENT ON COLUMN customers.company_id IS '소속 회사 ID'",
            "COMMENT ON COLUMN customers.customer_code IS '고객 코드'",
            "COMMENT ON COLUMN customers.customer_name IS '고객명'",
            "COMMENT ON COLUMN customers.customer_type IS '고객 유형'",
            "COMMENT ON COLUMN customers.customer_status IS '고객 상태'",
            "COMMENT ON COLUMN customers.email IS '이메일'",
            "COMMENT ON COLUMN customers.phone IS '전화번호'",
            "COMMENT ON COLUMN customers.address IS '주소'",

            // products 테이블 (기본 컬럼만)
            "COMMENT ON COLUMN products.id IS '상품 고유 ID'",
            "COMMENT ON COLUMN products.company_id IS '소속 회사 ID'",
            "COMMENT ON COLUMN products.product_code IS '상품 코드'",
            "COMMENT ON COLUMN products.product_name IS '상품명'",
            "COMMENT ON COLUMN products.description IS '상품 설명'",
            "COMMENT ON COLUMN products.category_id IS '카테고리 ID'",
            "COMMENT ON COLUMN products.selling_price IS '판매가격'",
            "COMMENT ON COLUMN products.standard_cost IS '표준원가'",

            // orders 테이블 (기본 컬럼만)
            "COMMENT ON COLUMN orders.id IS '주문 고유 ID'",
            "COMMENT ON COLUMN orders.company_id IS '소속 회사 ID'",
            "COMMENT ON COLUMN orders.customer_id IS '고객 ID'",
            "COMMENT ON COLUMN orders.order_number IS '주문번호'",
            "COMMENT ON COLUMN orders.order_date IS '주문일'",
            "COMMENT ON COLUMN orders.order_status IS '주문 상태'",
            "COMMENT ON COLUMN orders.total_amount IS '총 주문금액'",
            "COMMENT ON COLUMN orders.payment_status IS '결제 상태'",
            "COMMENT ON COLUMN orders.delivery_date IS '배송일'",

            // inventories 테이블 (기본 컬럼만)
            "COMMENT ON COLUMN inventories.id IS '재고 고유 ID'",
            "COMMENT ON COLUMN inventories.company_id IS '소속 회사 ID'",
            "COMMENT ON COLUMN inventories.product_id IS '상품 ID'",
            "COMMENT ON COLUMN inventories.warehouse_id IS '창고 ID'",
            "COMMENT ON COLUMN inventories.quantity IS '재고 수량'",

            // warehouses 테이블 (기본 컬럼만)
            "COMMENT ON COLUMN warehouses.id IS '창고 고유 ID'",
            "COMMENT ON COLUMN warehouses.company_id IS '소속 회사 ID'",
            "COMMENT ON COLUMN warehouses.warehouse_code IS '창고 코드'",
            "COMMENT ON COLUMN warehouses.name IS '창고명'",
            "COMMENT ON COLUMN warehouses.location IS '창고 위치'",
            "COMMENT ON COLUMN warehouses.capacity IS '창고 용량'",
            "COMMENT ON COLUMN warehouses.warehouse_type IS '창고 유형'",

            // notifications 테이블
            "COMMENT ON COLUMN notifications.id IS '알림 고유 ID'",
            "COMMENT ON COLUMN notifications.user_id IS '사용자 ID'",
            "COMMENT ON COLUMN notifications.title IS '알림 제목'",
            "COMMENT ON COLUMN notifications.message IS '알림 메시지'",
            "COMMENT ON COLUMN notifications.type IS '알림 타입'",
            "COMMENT ON COLUMN notifications.is_read IS '읽음 여부'",
            "COMMENT ON COLUMN notifications.action_url IS '액션 URL'",
            "COMMENT ON COLUMN notifications.read_at IS '읽은 시간'"
        };

        for (String comment : columnComments) {
            executeSafeComment(comment, "컬럼 코멘트");
        }
    }

    /**
     * 안전한 코멘트 실행 (개별 트랜잭션)
     */
    private void executeSafeComment(String sql, String commentType) {
        int retryCount = 0;
        int maxRetries = 2;
        boolean success = false;
        
        while (retryCount < maxRetries && !success) {
            try {
                transactionTemplate.execute(status -> {
                    try {
                        jdbcTemplate.execute(sql);
                        log.debug("✅ {} 추가 성공: {}", commentType, sql);
                        return null;
                    } catch (Exception e) {
                        log.warn("⚠️ {} 추가 중 오류: {} - {}", commentType, sql, e.getMessage());
                        return null;
                    }
                });
                success = true;
            } catch (Exception e) {
                retryCount++;
                if (retryCount < maxRetries) {
                    log.warn("⚠️ {} 추가 실패, 재시도 {}/{}: {}", commentType, retryCount, maxRetries, e.getMessage());
                    try {
                        Thread.sleep(1000); // 1초 대기
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    log.error("❌ {} 추가 최종 실패 (재시도 {}회 후): {} - {}", commentType, maxRetries, sql, e.getMessage());
                    // 운영 환경에서는 중요한 설정이므로 애플리케이션 시작 실패 처리
                    if (commentType.contains("테이블") || commentType.contains("기본")) {
                        throw new RuntimeException("중요한 DDL 작업 실패: " + commentType, e);
                    }
                }
            }
        }
    }
}