-- ERP 시스템 완전한 데이터베이스 스키마 생성 스크립트
-- cursor_erp_system 데이터베이스에 모든 테이블을 생성합니다

-- 데이터베이스 연결 확인
\c cursor_erp_system;

-- 스키마 설정
SET search_path TO public;

-- 공통 함수 생성 (updated_at 자동 업데이트)
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ==============================================
-- 0. 공통 테이블
-- ==============================================

-- 회사 테이블
CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    company_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    name_en VARCHAR(100),
    business_number VARCHAR(20),
    corporation_number VARCHAR(20),
    ceo_name VARCHAR(50),
    business_type VARCHAR(50),
    business_item VARCHAR(100),
    address VARCHAR(200),
    detailed_address VARCHAR(200),
    postal_code VARCHAR(10),
    phone VARCHAR(20),
    fax VARCHAR(20),
    email VARCHAR(100),
    website VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 회사 테이블 코멘트
COMMENT ON TABLE companies IS '회사 정보 테이블';
COMMENT ON COLUMN companies.id IS '회사 ID (Primary Key)';
COMMENT ON COLUMN companies.company_code IS '회사 코드 (고유값)';
COMMENT ON COLUMN companies.name IS '회사명 (한글)';
COMMENT ON COLUMN companies.name_en IS '회사명 (영문)';
COMMENT ON COLUMN companies.business_number IS '사업자등록번호';
COMMENT ON COLUMN companies.corporation_number IS '법인등록번호';
COMMENT ON COLUMN companies.ceo_name IS '대표자명';
COMMENT ON COLUMN companies.business_type IS '업종';
COMMENT ON COLUMN companies.business_item IS '업태';
COMMENT ON COLUMN companies.address IS '주소';
COMMENT ON COLUMN companies.detailed_address IS '상세주소';
COMMENT ON COLUMN companies.postal_code IS '우편번호';
COMMENT ON COLUMN companies.phone IS '대표 전화번호';
COMMENT ON COLUMN companies.fax IS '팩스번호';
COMMENT ON COLUMN companies.email IS '대표 이메일';
COMMENT ON COLUMN companies.website IS '웹사이트 URL';
COMMENT ON COLUMN companies.status IS '회사 상태 (ACTIVE/INACTIVE)';
COMMENT ON COLUMN companies.is_deleted IS '삭제 여부';
COMMENT ON COLUMN companies.created_at IS '생성일시';
COMMENT ON COLUMN companies.updated_at IS '수정일시';

-- ==============================================
-- 1. 인사관리 모듈 테이블
-- ==============================================

-- 부서 테이블
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    department_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_department_id BIGINT REFERENCES departments(id),
    manager_id BIGINT, -- 순환 참조로 인해 나중에 외래키 추가
    company_id BIGINT REFERENCES companies(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 부서 테이블 코멘트
COMMENT ON TABLE departments IS '부서 정보 테이블';
COMMENT ON COLUMN departments.id IS '부서 ID (Primary Key)';
COMMENT ON COLUMN departments.department_code IS '부서 코드 (고유값)';
COMMENT ON COLUMN departments.name IS '부서명';
COMMENT ON COLUMN departments.description IS '부서 설명';
COMMENT ON COLUMN departments.parent_department_id IS '상위 부서 ID';
COMMENT ON COLUMN departments.manager_id IS '부서장 직원 ID';
COMMENT ON COLUMN departments.company_id IS '소속 회사 ID';
COMMENT ON COLUMN departments.created_at IS '생성일시';
COMMENT ON COLUMN departments.updated_at IS '수정일시';

-- 직급 테이블
CREATE TABLE positions (
    id BIGSERIAL PRIMARY KEY,
    position_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    level_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 직급 테이블 코멘트
COMMENT ON TABLE positions IS '직급 정보 테이블';
COMMENT ON COLUMN positions.id IS '직급 ID (Primary Key)';
COMMENT ON COLUMN positions.position_code IS '직급 코드 (고유값)';
COMMENT ON COLUMN positions.name IS '직급명';
COMMENT ON COLUMN positions.description IS '직급 설명';
COMMENT ON COLUMN positions.level_order IS '직급 레벨 (숫자가 작을수록 높은 직급)';
COMMENT ON COLUMN positions.created_at IS '생성일시';
COMMENT ON COLUMN positions.updated_at IS '수정일시';

-- 직원 테이블
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    employee_number VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    name_en VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    mobile VARCHAR(20),
    birth_date DATE,
    gender VARCHAR(10),
    address TEXT,
    address_detail TEXT,
    postal_code VARCHAR(10),
    hire_date DATE NOT NULL,
    termination_date DATE,
    department_id BIGINT REFERENCES departments(id),
    position_id BIGINT REFERENCES positions(id),
    company_id BIGINT REFERENCES companies(id),
    employment_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    employment_type VARCHAR(20) NOT NULL DEFAULT 'FULL_TIME',
    salary BIGINT,
    bank_name VARCHAR(50),
    account_number VARCHAR(20),
    account_holder VARCHAR(50),
    emergency_contact VARCHAR(20),
    emergency_relation VARCHAR(20),
    education VARCHAR(100),
    major VARCHAR(100),
    career TEXT,
    skills TEXT,
    certifications TEXT,
    memo TEXT,
    profile_image_url VARCHAR(500),
    resident_number VARCHAR(20),
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    deleted_at TIMESTAMP,
    deleted_by BIGINT
);

-- 직원 테이블 코멘트
COMMENT ON TABLE employees IS '직원 정보 테이블';
COMMENT ON COLUMN employees.id IS '직원 ID (Primary Key)';
COMMENT ON COLUMN employees.employee_number IS '사번 (고유값)';
COMMENT ON COLUMN employees.name IS '성명 (한글)';
COMMENT ON COLUMN employees.name_en IS '성명 (영문)';
COMMENT ON COLUMN employees.email IS '이메일';
COMMENT ON COLUMN employees.phone IS '전화번호';
COMMENT ON COLUMN employees.mobile IS '휴대폰번호';
COMMENT ON COLUMN employees.birth_date IS '생년월일';
COMMENT ON COLUMN employees.gender IS '성별 (MALE/FEMALE)';
COMMENT ON COLUMN employees.address IS '주소';
COMMENT ON COLUMN employees.address_detail IS '상세주소';
COMMENT ON COLUMN employees.postal_code IS '우편번호';
COMMENT ON COLUMN employees.hire_date IS '입사일';
COMMENT ON COLUMN employees.termination_date IS '퇴사일';
COMMENT ON COLUMN employees.department_id IS '소속 부서 ID';
COMMENT ON COLUMN employees.position_id IS '직급 ID';
COMMENT ON COLUMN employees.company_id IS '소속 회사 ID';
COMMENT ON COLUMN employees.employment_status IS '근무 상태 (ACTIVE/ON_LEAVE/INACTIVE/SUSPENDED/TERMINATED)';
COMMENT ON COLUMN employees.employment_type IS '고용 형태 (FULL_TIME/PART_TIME/TEMPORARY/CONTRACT)';
COMMENT ON COLUMN employees.salary IS '기본급 (원 단위, 정수)';
COMMENT ON COLUMN employees.bank_name IS '은행명';
COMMENT ON COLUMN employees.account_number IS '계좌번호';
COMMENT ON COLUMN employees.account_holder IS '예금주명';
COMMENT ON COLUMN employees.emergency_contact IS '비상연락처';
COMMENT ON COLUMN employees.emergency_relation IS '비상연락처 관계';
COMMENT ON COLUMN employees.education IS '학력';
COMMENT ON COLUMN employees.major IS '전공';
COMMENT ON COLUMN employees.career IS '경력사항';
COMMENT ON COLUMN employees.skills IS '보유 기술';
COMMENT ON COLUMN employees.certifications IS '자격증';
COMMENT ON COLUMN employees.memo IS '메모';
COMMENT ON COLUMN employees.profile_image_url IS '프로필 이미지 URL';
COMMENT ON COLUMN employees.resident_number IS '주민등록번호 (암호화)';
COMMENT ON COLUMN employees.is_deleted IS '삭제 여부';
COMMENT ON COLUMN employees.created_at IS '생성일시';
COMMENT ON COLUMN employees.created_by IS '생성자 ID';
COMMENT ON COLUMN employees.updated_at IS '수정일시';
COMMENT ON COLUMN employees.updated_by IS '수정자 ID';
COMMENT ON COLUMN employees.deleted_at IS '삭제일시';
COMMENT ON COLUMN employees.deleted_by IS '삭제자 ID';

-- 사용자 테이블
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL CHECK (role IN ('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'USER', 'READONLY')),
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_locked BOOLEAN NOT NULL DEFAULT false,
    is_password_expired BOOLEAN NOT NULL DEFAULT false,
    company_id BIGINT REFERENCES companies(id),
    department_id BIGINT REFERENCES departments(id),
    last_login_at TIMESTAMP,
    password_changed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

-- 사용자 테이블 코멘트
COMMENT ON TABLE users IS '시스템 사용자 계정 테이블';
COMMENT ON COLUMN users.id IS '사용자 ID (Primary Key)';
COMMENT ON COLUMN users.username IS '사용자명 (로그인 ID, 고유값)';
COMMENT ON COLUMN users.password IS '비밀번호 (암호화)';
COMMENT ON COLUMN users.email IS '이메일 (고유값)';
COMMENT ON COLUMN users.full_name IS '전체 이름';
COMMENT ON COLUMN users.phone IS '전화번호';
COMMENT ON COLUMN users.role IS '사용자 역할 (SUPER_ADMIN/ADMIN/MANAGER/USER/READONLY)';
COMMENT ON COLUMN users.is_active IS '활성 여부';
COMMENT ON COLUMN users.is_locked IS '잠금 여부';
COMMENT ON COLUMN users.is_password_expired IS '비밀번호 만료 여부';
COMMENT ON COLUMN users.company_id IS '소속 회사 ID';
COMMENT ON COLUMN users.department_id IS '소속 부서 ID';
COMMENT ON COLUMN users.last_login_at IS '마지막 로그인 일시';
COMMENT ON COLUMN users.password_changed_at IS '비밀번호 변경 일시';
COMMENT ON COLUMN users.created_at IS '생성일시';
COMMENT ON COLUMN users.updated_at IS '수정일시';
COMMENT ON COLUMN users.created_by IS '생성자 ID';
COMMENT ON COLUMN users.updated_by IS '수정자 ID';
COMMENT ON COLUMN users.is_deleted IS '삭제 여부';

-- 부서 관리자 외래키 추가 (순환 참조 해결)
ALTER TABLE departments ADD CONSTRAINT fk_departments_manager 
FOREIGN KEY (manager_id) REFERENCES employees(id);

-- ==============================================
-- 2. 재고관리 모듈 테이블
-- ==============================================

-- 제품 카테고리 테이블
CREATE TABLE product_categories (
    id BIGSERIAL PRIMARY KEY,
    category_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_category_id BIGINT REFERENCES product_categories(id),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 제품 테이블
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category_id BIGINT REFERENCES product_categories(id),
    unit_price DECIMAL(12,2) NOT NULL,
    current_stock INTEGER NOT NULL DEFAULT 0,
    minimum_stock INTEGER DEFAULT 0,
    maximum_stock INTEGER,
    unit VARCHAR(20),
    manufacturer VARCHAR(100),
    brand VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DISCONTINUED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 재고 이동 이력 테이블
CREATE TABLE stock_movements (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    movement_type VARCHAR(20) NOT NULL CHECK (movement_type IN ('IN', 'OUT', 'ADJUSTMENT')),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(12,2),
    total_amount DECIMAL(12,2),
    reference_type VARCHAR(50), -- 'PURCHASE', 'SALE', 'ADJUSTMENT' 등
    reference_id BIGINT,
    notes TEXT,
    movement_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES employees(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================
-- 3. 영업관리 모듈 테이블
-- ==============================================

-- 고객 테이블
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    customer_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    customer_type VARCHAR(20) NOT NULL CHECK (customer_type IN ('INDIVIDUAL', 'CORPORATE')),
    email VARCHAR(100),
    phone VARCHAR(20),
    fax VARCHAR(20),
    address TEXT,
    detailed_address TEXT,
    postal_code VARCHAR(10),
    contact_person VARCHAR(100),
    contact_position VARCHAR(100),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    business_number VARCHAR(20),
    notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 견적서 테이블
CREATE TABLE quotes (
    id BIGSERIAL PRIMARY KEY,
    quote_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    quote_date DATE NOT NULL,
    valid_until DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'SENT', 'ACCEPTED', 'REJECTED', 'EXPIRED')),
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    notes TEXT,
    terms_conditions TEXT,
    created_by BIGINT NOT NULL REFERENCES employees(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 견적서 상세 테이블
CREATE TABLE quote_details (
    id BIGSERIAL PRIMARY KEY,
    quote_id BIGINT NOT NULL REFERENCES quotes(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    discount_rate DECIMAL(5,2) DEFAULT 0,
    line_total DECIMAL(12,2) NOT NULL,
    notes TEXT
);

-- 주문 테이블
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    quote_id BIGINT REFERENCES quotes(id),
    order_date DATE NOT NULL,
    required_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    shipping_address TEXT,
    billing_address TEXT,
    notes TEXT,
    created_by BIGINT NOT NULL REFERENCES employees(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 주문 상세 테이블
CREATE TABLE order_details (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    discount_rate DECIMAL(5,2) DEFAULT 0,
    line_total DECIMAL(12,2) NOT NULL,
    notes TEXT
);

-- ==============================================
-- 4. 인덱스 생성
-- ==============================================

-- 부서 관련 인덱스
CREATE INDEX idx_departments_code ON departments(department_code);
CREATE INDEX idx_departments_parent ON departments(parent_department_id);

-- 직급 관련 인덱스
CREATE INDEX idx_positions_code ON positions(position_code);
CREATE INDEX idx_positions_level ON positions(level_order);

-- 사용자 관련 인덱스
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_company ON users(company_id);
CREATE INDEX idx_users_department ON users(department_id);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(is_active);

-- 직원 관련 인덱스
CREATE INDEX idx_employees_employee_number ON employees(employee_number);
CREATE INDEX idx_employees_email ON employees(email);
CREATE INDEX idx_employees_department_id ON employees(department_id);
CREATE INDEX idx_employees_position_id ON employees(position_id);
CREATE INDEX idx_employees_company_id ON employees(company_id);
CREATE INDEX idx_employees_employment_status ON employees(employment_status);
CREATE INDEX idx_employees_employment_type ON employees(employment_type);
CREATE INDEX idx_employees_hire_date ON employees(hire_date);
CREATE INDEX idx_employees_is_deleted ON employees(is_deleted);

-- 제품 관련 인덱스
CREATE INDEX idx_products_code ON products(product_code);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_name ON products(name);

CREATE INDEX idx_product_categories_code ON product_categories(category_code);
CREATE INDEX idx_product_categories_parent ON product_categories(parent_category_id);

-- 재고 관련 인덱스
CREATE INDEX idx_stock_movements_product ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_type ON stock_movements(movement_type);
CREATE INDEX idx_stock_movements_date ON stock_movements(movement_date);
CREATE INDEX idx_stock_movements_reference ON stock_movements(reference_type, reference_id);

-- 고객 관련 인덱스
CREATE INDEX idx_customers_code ON customers(customer_code);
CREATE INDEX idx_customers_type ON customers(customer_type);
CREATE INDEX idx_customers_status ON customers(status);
CREATE INDEX idx_customers_name ON customers(name);
CREATE INDEX idx_customers_email ON customers(email);

-- 견적서 관련 인덱스
CREATE INDEX idx_quotes_number ON quotes(quote_number);
CREATE INDEX idx_quotes_customer ON quotes(customer_id);
CREATE INDEX idx_quotes_date ON quotes(quote_date);
CREATE INDEX idx_quotes_status ON quotes(status);

-- 주문 관련 인덱스
CREATE INDEX idx_orders_number ON orders(order_number);
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_quote ON orders(quote_id);

-- ==============================================
-- 5. 트리거 생성
-- ==============================================

-- updated_at 자동 업데이트 트리거
CREATE TRIGGER update_departments_updated_at BEFORE UPDATE ON departments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_positions_updated_at BEFORE UPDATE ON positions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_employees_updated_at BEFORE UPDATE ON employees
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_product_categories_updated_at BEFORE UPDATE ON product_categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON customers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_quotes_updated_at BEFORE UPDATE ON quotes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ==============================================
-- 6. 테이블 코멘트 (추가 테이블들)
-- ==============================================

COMMENT ON TABLE product_categories IS '제품 카테고리 테이블';
COMMENT ON TABLE products IS '제품 정보 테이블';
COMMENT ON TABLE stock_movements IS '재고 이동 이력 테이블';
COMMENT ON TABLE customers IS '고객 정보 테이블';
COMMENT ON TABLE quotes IS '견적서 테이블';
COMMENT ON TABLE quote_details IS '견적서 상세 테이블';
COMMENT ON TABLE orders IS '주문서 테이블';
COMMENT ON TABLE order_details IS '주문서 상세 테이블';

-- 완료 메시지
SELECT 'ERP 시스템 데이터베이스 스키마 생성이 완료되었습니다.' AS message;
