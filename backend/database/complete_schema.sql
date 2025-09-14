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

-- 직원 테이블
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    employee_number VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    hire_date DATE NOT NULL,
    department_id BIGINT REFERENCES departments(id),
    position_id BIGINT REFERENCES positions(id),
    salary DECIMAL(12,2),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'TERMINATED')),
    address TEXT,
    birth_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

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
CREATE INDEX idx_employees_status ON employees(status);
CREATE INDEX idx_employees_hire_date ON employees(hire_date);

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
-- 6. 테이블 코멘트
-- ==============================================

COMMENT ON TABLE departments IS '부서 정보 테이블';
COMMENT ON TABLE positions IS '직급 정보 테이블';
COMMENT ON TABLE employees IS '직원 정보 테이블';
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
