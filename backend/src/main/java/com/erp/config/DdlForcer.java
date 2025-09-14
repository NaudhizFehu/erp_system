package com.erp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * DDL 강제 실행 컴포넌트
 * Hibernate DDL이 실행되지 않을 경우 강제로 실행합니다
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DdlForcer {

    @PersistenceContext
    private EntityManager entityManager;
    
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Order(1) // 가장 먼저 실행
    @Transactional
    public void forceDdlExecution() {
        try {
            // 기존 테이블들 삭제 (순서 중요: 외래키 제약조건 고려)
            log.info("기존 테이블 삭제 중...");
            String[] dropTables = {
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
                    jdbcTemplate.execute(sql);
                    log.info("✅ 테이블 삭제 완료: {}", sql);
                } catch (Exception e) {
                    log.warn("⚠️ 테이블 삭제 중 오류 (무시됨): {} - {}", sql, e.getMessage());
                }
            }
            log.info("=== DDL 강제 실행 시작 ===");
            
            // 1. Hibernate 스키마 생성 강제 실행
            log.info("Hibernate 스키마 생성 강제 실행...");
            entityManager.getEntityManagerFactory().getMetamodel().getEntities().forEach(entityType -> {
                log.info("엔티티 발견: {}", entityType.getName());
            });
            
            // 2. 직접 테이블 생성 (complete_schema.sql 대신)
            log.info("직접 테이블 생성 방식으로 전환...");
            createTablesWithHardcodedSql();
            
            // 3. 데이터 초기화 실행
            initializeData();
            
        } catch (Exception e) {
            log.error("DDL 강제 실행 중 오류 발생: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 하드코딩된 SQL로 테이블 생성 (폴백용)
     */
    private void createTablesWithHardcodedSql() {
        log.info("하드코딩된 SQL로 테이블 생성 중...");
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
                "established_date DATE, " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "company_type VARCHAR(20), " +
                "employee_count INTEGER, " +
                "capital_amount DECIMAL(15,0), " +
                "description TEXT, " +
                "logo_url VARCHAR(500), " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS departments (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_id BIGINT NOT NULL, " +
                "department_code VARCHAR(20) NOT NULL, " +
                "name VARCHAR(100) NOT NULL, " +
                "name_en VARCHAR(100), " +
                "description VARCHAR(500), " +
                "level INTEGER NOT NULL DEFAULT 1, " +
                "sort_order INTEGER DEFAULT 0, " +
                "parent_department_id BIGINT, " +
                "manager_id BIGINT, " +
                "department_type VARCHAR(20), " +
                "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                "cost_center_code VARCHAR(20), " +
                "phone VARCHAR(20), " +
                "fax VARCHAR(20), " +
                "email VARCHAR(100), " +
                "location VARCHAR(200), " +
                "budget_amount DECIMAL(15,2), " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP, " +
                "created_by BIGINT, " +
                "updated_by BIGINT, " +
                "is_deleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "deleted_at TIMESTAMP, " +
                "deleted_by BIGINT, " +
                "FOREIGN KEY (company_id) REFERENCES companies(id), " +
                "FOREIGN KEY (parent_department_id) REFERENCES departments(id)" +
                ")",
                
                "CREATE TABLE IF NOT EXISTS users (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "company_id BIGINT NOT NULL, " +
                "department_id BIGINT, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "full_name VARCHAR(50) NOT NULL, " +
                "phone VARCHAR(20), " +
                "role VARCHAR(20) NOT NULL DEFAULT 'USER', " +
                "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                "is_locked BOOLEAN NOT NULL DEFAULT FALSE, " +
                "is_password_expired BOOLEAN NOT NULL DEFAULT FALSE, " +
                "last_login_at TIMESTAMP, " +
                "password_changed_at TIMESTAMP, " +
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
                "base_salary DECIMAL(12,2), " +
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
                ")"
            };
            
        for (String sql : createTables) {
            try {
                jdbcTemplate.execute(sql);
                log.info("✅ 테이블 생성 성공");
            } catch (Exception e) {
                log.warn("⚠️ 테이블 생성 중 오류 (이미 존재할 수 있음): {}", e.getMessage());
            }
        }
        log.info("✅ 하드코딩된 테이블 생성 완료");
    }
    
    /**
     * 데이터 초기화 실행
     */
    private void initializeData() {
        log.info("=== 데이터 초기화 시작 ===");
        
        // 3-1. 회사 데이터 삽입
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO companies (id, company_code, name, name_en, business_number, corporation_number, ceo_name, business_type, business_item, address, detailed_address, postal_code, phone, fax, email, website, status, is_deleted)
                    VALUES (1, 'ABC_CORP', 'ABC 기업', 'ABC Corporation', '123-45-67890', '123456-1234567', '김대표', '제조업', '전자제품 제조', '서울특별시 강남구 테헤란로 123', 'ABC빌딩 10층', '06292', '02-1234-5678', '02-1234-5679', 'info@abc.com', 'www.abc.com', 'ACTIVE', false)
                    ON CONFLICT (id) DO NOTHING
                    """);
                log.info("✅ 회사 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 회사 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        // 3-2. 부서 데이터 삽입
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO departments (id, department_code, name, description, parent_department_id, manager_id, company_id)
                        VALUES (1, 'HR_DEPT', '인사부', '인사관리 및 채용업무', NULL, NULL, 1)
                    ON CONFLICT (id) DO NOTHING
                    """);
                log.info("✅ 인사부 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 인사부 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO departments (id, department_code, name, description, parent_department_id, manager_id, company_id)
                        VALUES (2, 'SALES_DEPT', '영업부', '영업 및 고객관리', NULL, NULL, 1)
                    ON CONFLICT (id) DO NOTHING
                    """);
                log.info("✅ 영업부 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 영업부 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO departments (id, department_code, name, description, parent_department_id, manager_id, company_id)
                        VALUES (3, 'IT_DEPT', 'IT부서', '시스템 개발 및 유지보수', NULL, NULL, 1)
                    ON CONFLICT (id) DO NOTHING
                    """);
                log.info("✅ IT부서 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ IT부서 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        // 3-3. 사용자 계정 데이터 삽입
        transactionTemplate.execute(status -> {
            try {
                String adminPassword = passwordEncoder.encode("admin123");
                log.info("🔐 admin123 해시: {}", adminPassword);
                
                // 비밀번호 검증
                boolean matches = passwordEncoder.matches("admin123", adminPassword);
                log.info("🔍 비밀번호 검증 결과: {}", matches);
                
                jdbcTemplate.execute(String.format("""
                    INSERT INTO users (id, username, password, email, full_name, phone, role, is_active, is_locked, is_password_expired, company_id, department_id, password_changed_at)
                    VALUES (1, 'admin', '%s', 'admin@abc.com', '관리자', '02-1234-5678', 'ADMIN', true, false, false, 1, 1, NOW())
                    ON CONFLICT (id) DO NOTHING
                    """, adminPassword));
                log.info("✅ admin 계정 삽입 성공 (비밀번호: admin123)");
            } catch (Exception e) {
                log.warn("⚠️ admin 계정 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                String userPassword = passwordEncoder.encode("admin123");
                jdbcTemplate.execute(String.format("""
                    INSERT INTO users (id, username, password, email, full_name, phone, role, is_active, is_locked, is_password_expired, company_id, department_id, password_changed_at)
                    VALUES (2, 'user', '%s', 'user@abc.com', '일반사용자', '02-2345-6789', 'USER', true, false, false, 1, 3, NOW())
                    ON CONFLICT (id) DO NOTHING
                    """, userPassword));
                log.info("✅ user 계정 삽입 성공 (비밀번호: admin123)");
            } catch (Exception e) {
                log.warn("⚠️ user 계정 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        // 3-4. 직급 데이터 삽입
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO positions (id, company_id, position_code, name, description, level, is_active, is_deleted)
                        VALUES (1, 1, 'CEO', '대표이사', '최고경영자', 1, true, false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 대표이사 직급 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 대표이사 직급 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO positions (id, company_id, position_code, name, description, level, is_active, is_deleted)
                        VALUES (2, 1, 'MANAGER', '부장', '부서장', 4, true, false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 부장 직급 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 부장 직급 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO positions (id, company_id, position_code, name, description, level, is_active, is_deleted)
                        VALUES (3, 1, 'STAFF', '대리', '대리급', 7, true, false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 대리 직급 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 대리 직급 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        // 3-5. 직원 데이터 삽입 (모든 직원)
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO employees (id, company_id, department_id, position_id, employee_number, name, email, phone, hire_date, employment_status, base_salary, address, birth_date, is_deleted)
                        VALUES (1, 1, 1, 1, 'EMP001', '김관리', 'admin@abc.com', '010-1234-5678', '2020-01-01', 'ACTIVE', 200000000, '서울특별시 강남구', '1980-01-01', false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 김관리 직원 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 김관리 직원 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO employees (id, company_id, department_id, position_id, employee_number, name, email, phone, hire_date, employment_status, base_salary, address, birth_date, is_deleted)
                        VALUES (2, 1, 2, 2, 'EMP002', '이영업', 'sales@abc.com', '010-2345-6789', '2020-02-01', 'ACTIVE', 150000000, '서울특별시 서초구', '1985-05-15', false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 이영업 직원 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 이영업 직원 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO employees (id, company_id, department_id, position_id, employee_number, name, email, phone, hire_date, employment_status, base_salary, address, birth_date, is_deleted)
                        VALUES (3, 1, 3, 3, 'EMP003', '박개발', 'dev@abc.com', '010-3456-7890', '2020-03-01', 'ACTIVE', 120000000, '서울특별시 마포구', '1990-08-20', false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 박개발 직원 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 박개발 직원 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO employees (id, company_id, department_id, position_id, employee_number, name, email, phone, hire_date, employment_status, base_salary, address, birth_date, is_deleted)
                        VALUES (4, 1, 1, 2, 'EMP004', '김철수', 'kim@abc.com', '010-4567-8901', '2021-01-15', 'ACTIVE', 80000000, '서울특별시 송파구', '1988-03-10', false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 김철수 직원 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 김철수 직원 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO employees (id, company_id, department_id, position_id, employee_number, name, email, phone, hire_date, employment_status, base_salary, address, birth_date, is_deleted)
                        VALUES (5, 1, 2, 3, 'EMP005', '이영희', 'lee@abc.com', '010-5678-9012', '2021-02-20', 'ACTIVE', 70000000, '서울특별시 강동구', '1992-07-25', false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 이영희 직원 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 이영희 직원 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO employees (id, company_id, department_id, position_id, employee_number, name, email, phone, hire_date, employment_status, base_salary, address, birth_date, is_deleted)
                        VALUES (6, 1, 3, 1, 'EMP006', '박민수', 'park@abc.com', '010-6789-0123', '2021-03-10', 'ACTIVE', 90000000, '서울특별시 영등포구', '1987-11-05', false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 박민수 직원 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 박민수 직원 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        // 3-6. 제품 카테고리 데이터 삽입
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO product_categories (id, company_id, category_code, name, description, parent_category_id, is_active, is_deleted)
                        VALUES (1, 1, 'ELECTRONICS', '전자제품', '전자제품 카테고리', NULL, true, false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 전자제품 카테고리 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 전자제품 카테고리 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO product_categories (id, company_id, category_code, name, description, parent_category_id, is_active, is_deleted)
                        VALUES (2, 1, 'COMPUTER', '컴퓨터', '컴퓨터 및 주변기기', 1, true, false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 컴퓨터 카테고리 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 컴퓨터 카테고리 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO product_categories (id, company_id, category_code, name, description, parent_category_id, is_active, is_deleted)
                        VALUES (3, 1, 'OFFICE', '사무용품', '사무용품 및 소모품', NULL, true, false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 사무용품 카테고리 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 사무용품 카테고리 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        // 3-7. 제품 데이터 삽입
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO products (id, company_id, category_id, product_code, product_name, description, product_type, product_status, base_unit, standard_cost, selling_price, is_active, is_deleted)
                        VALUES (1, 1, 2, 'LAPTOP001', '노트북', '고성능 노트북', 'FINISHED_GOODS', 'ACTIVE', '대', 1200000, 1500000, true, false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 노트북 제품 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 노트북 제품 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO products (id, company_id, category_id, product_code, product_name, description, product_type, product_status, base_unit, standard_cost, selling_price, is_active, is_deleted)
                        VALUES (2, 1, 2, 'MOUSE001', '무선마우스', '블루투스 무선마우스', 'FINISHED_GOODS', 'ACTIVE', '개', 30000, 50000, true, false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 무선마우스 제품 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 무선마우스 제품 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO products (id, company_id, category_id, product_code, product_name, description, product_type, product_status, base_unit, standard_cost, selling_price, is_active, is_deleted)
                        VALUES (3, 1, 3, 'PEN001', '볼펜', '검은색 볼펜', 'CONSUMABLE', 'ACTIVE', '자루', 500, 1000, true, false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 볼펜 제품 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 볼펜 제품 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO products (id, company_id, category_id, product_code, product_name, description, product_type, product_status, base_unit, standard_cost, selling_price, is_active, is_deleted)
                        VALUES (4, 1, 2, 'KEYBOARD001', '키보드', '기계식 키보드', 'FINISHED_GOODS', 'ACTIVE', '개', 100000, 150000, true, false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 키보드 제품 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 키보드 제품 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO products (id, company_id, category_id, product_code, product_name, description, product_type, product_status, base_unit, standard_cost, selling_price, is_active, is_deleted)
                        VALUES (5, 1, 2, 'MONITOR001', '모니터', '27인치 4K 모니터', 'FINISHED_GOODS', 'ACTIVE', '대', 400000, 500000, true, false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 모니터 제품 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 모니터 제품 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO products (id, company_id, category_id, product_code, product_name, description, product_type, product_status, base_unit, standard_cost, selling_price, is_active, is_deleted)
                        VALUES (6, 1, 3, 'PAPER001', 'A4용지', '복사용 A4용지', 'CONSUMABLE', 'ACTIVE', '박스', 3000, 5000, true, false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ A4용지 제품 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ A4용지 제품 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        // 3-8. 고객 데이터 삽입 (모든 고객)
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO customers (id, company_id, customer_code, customer_name, customer_type, customer_status, phone, email, address, is_deleted)
                        VALUES (1, 1, 'CUST001', 'ABC 기업', 'CORPORATE', 'ACTIVE', '02-1234-5678', 'info@abc.com', '서울특별시 강남구 테헤란로 123', false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ ABC 기업 고객 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ ABC 기업 고객 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO customers (id, company_id, customer_code, customer_name, customer_type, customer_status, phone, email, address, is_deleted)
                        VALUES (2, 1, 'CUST002', 'XYZ 주식회사', 'CORPORATE', 'ACTIVE', '02-2345-6789', 'contact@xyz.com', '서울특별시 서초구 서초대로 456', false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ XYZ 주식회사 고객 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ XYZ 주식회사 고객 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO customers (id, company_id, customer_code, customer_name, customer_type, customer_status, phone, email, address, is_deleted)
                        VALUES (3, 1, 'CUST003', '홍길동', 'INDIVIDUAL', 'ACTIVE', '010-5555-6666', 'hong@email.com', '서울특별시 마포구 홍대입구역', false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 홍길동 고객 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 홍길동 고객 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO customers (id, company_id, customer_code, customer_name, customer_type, customer_status, phone, email, address, is_deleted)
                        VALUES (4, 1, 'CUST004', '김철수', 'INDIVIDUAL', 'ACTIVE', '010-7777-8888', 'kim@email.com', '서울특별시 송파구 잠실동', false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 김철수 고객 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 김철수 고객 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO customers (id, company_id, customer_code, customer_name, customer_type, customer_status, phone, email, address, is_deleted)
                        VALUES (5, 1, 'CUST005', '이영희', 'INDIVIDUAL', 'ACTIVE', '010-9999-0000', 'lee@email.com', '서울특별시 강동구 천호동', false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 이영희 고객 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 이영희 고객 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        transactionTemplate.execute(status -> {
            try {
                jdbcTemplate.execute("""
                        INSERT INTO customers (id, company_id, customer_code, customer_name, customer_type, customer_status, phone, email, address, is_deleted)
                        VALUES (6, 1, 'CUST006', '박민수', 'INDIVIDUAL', 'ACTIVE', '010-1111-3333', 'park@email.com', '서울특별시 영등포구 여의도동', false)
                        ON CONFLICT (id) DO NOTHING
                        """);
                log.info("✅ 박민수 고객 데이터 삽입 성공");
            } catch (Exception e) {
                log.warn("⚠️ 박민수 고객 데이터 삽입 중 오류: {}", e.getMessage());
            }
            return null;
        });
        
        log.info("=== 데이터 초기화 완료 ===");
    }
}
