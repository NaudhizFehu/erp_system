-- 영업관리 모듈 테이블 생성 스크립트
-- 고객, 주문, 견적 관련 테이블을 생성합니다

-- 스키마 설정
SET search_path TO cursor_erp_system, public;

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

-- 배송 테이블
CREATE TABLE shipments (
    id BIGSERIAL PRIMARY KEY,
    shipment_number VARCHAR(50) NOT NULL UNIQUE,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    shipment_date DATE NOT NULL,
    carrier VARCHAR(100),
    tracking_number VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'PREPARING' CHECK (status IN ('PREPARING', 'SHIPPED', 'IN_TRANSIT', 'DELIVERED', 'RETURNED')),
    shipping_address TEXT NOT NULL,
    notes TEXT,
    created_by BIGINT NOT NULL REFERENCES employees(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 배송 상세 테이블
CREATE TABLE shipment_details (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    notes TEXT
);

-- 인덱스 생성
CREATE INDEX idx_customers_code ON customers(customer_code);
CREATE INDEX idx_customers_type ON customers(customer_type);
CREATE INDEX idx_customers_status ON customers(status);
CREATE INDEX idx_customers_name ON customers(name);
CREATE INDEX idx_customers_email ON customers(email);

CREATE INDEX idx_quotes_number ON quotes(quote_number);
CREATE INDEX idx_quotes_customer ON quotes(customer_id);
CREATE INDEX idx_quotes_date ON quotes(quote_date);
CREATE INDEX idx_quotes_status ON quotes(status);

CREATE INDEX idx_orders_number ON orders(order_number);
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_quote ON orders(quote_id);

CREATE INDEX idx_shipments_number ON shipments(shipment_number);
CREATE INDEX idx_shipments_order ON shipments(order_id);
CREATE INDEX idx_shipments_date ON shipments(shipment_date);
CREATE INDEX idx_shipments_status ON shipments(status);

-- 트리거 생성
CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON customers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_quotes_updated_at BEFORE UPDATE ON quotes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_shipments_updated_at BEFORE UPDATE ON shipments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 견적서 금액 계산 트리거 함수
CREATE OR REPLACE FUNCTION calculate_quote_totals()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE quotes 
    SET subtotal = (
            SELECT COALESCE(SUM(line_total), 0) 
            FROM quote_details 
            WHERE quote_id = COALESCE(NEW.quote_id, OLD.quote_id)
        ),
        tax_amount = (
            SELECT COALESCE(SUM(line_total), 0) * 0.1
            FROM quote_details 
            WHERE quote_id = COALESCE(NEW.quote_id, OLD.quote_id)
        ),
        updated_at = CURRENT_TIMESTAMP
    WHERE id = COALESCE(NEW.quote_id, OLD.quote_id);
    
    UPDATE quotes 
    SET total_amount = subtotal + tax_amount,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = COALESCE(NEW.quote_id, OLD.quote_id);
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- 주문서 금액 계산 트리거 함수
CREATE OR REPLACE FUNCTION calculate_order_totals()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE orders 
    SET subtotal = (
            SELECT COALESCE(SUM(line_total), 0) 
            FROM order_details 
            WHERE order_id = COALESCE(NEW.order_id, OLD.order_id)
        ),
        tax_amount = (
            SELECT COALESCE(SUM(line_total), 0) * 0.1
            FROM order_details 
            WHERE order_id = COALESCE(NEW.order_id, OLD.order_id)
        ),
        updated_at = CURRENT_TIMESTAMP
    WHERE id = COALESCE(NEW.order_id, OLD.order_id);
    
    UPDATE orders 
    SET total_amount = subtotal + tax_amount,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = COALESCE(NEW.order_id, OLD.order_id);
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- 금액 계산 트리거
CREATE TRIGGER trigger_calculate_quote_totals
    AFTER INSERT OR UPDATE OR DELETE ON quote_details
    FOR EACH ROW EXECUTE FUNCTION calculate_quote_totals();

CREATE TRIGGER trigger_calculate_order_totals
    AFTER INSERT OR UPDATE OR DELETE ON order_details
    FOR EACH ROW EXECUTE FUNCTION calculate_order_totals();

-- 테이블 코멘트
COMMENT ON TABLE customers IS '고객 정보 테이블';
COMMENT ON TABLE quotes IS '견적서 테이블';
COMMENT ON TABLE quote_details IS '견적서 상세 테이블';
COMMENT ON TABLE orders IS '주문서 테이블';
COMMENT ON TABLE order_details IS '주문서 상세 테이블';
COMMENT ON TABLE shipments IS '배송 테이블';
COMMENT ON TABLE shipment_details IS '배송 상세 테이블';

-- 컬럼 코멘트
COMMENT ON COLUMN customers.customer_type IS '고객 유형 (INDIVIDUAL: 개인, CORPORATE: 법인)';
COMMENT ON COLUMN customers.status IS '고객 상태 (ACTIVE: 활성, INACTIVE: 비활성, SUSPENDED: 정지)';
COMMENT ON COLUMN quotes.status IS '견적서 상태 (DRAFT: 임시저장, SENT: 발송, ACCEPTED: 수락, REJECTED: 거절, EXPIRED: 만료)';
COMMENT ON COLUMN orders.status IS '주문 상태 (PENDING: 대기, CONFIRMED: 확인, PROCESSING: 처리중, SHIPPED: 배송, DELIVERED: 완료, CANCELLED: 취소)';
COMMENT ON COLUMN shipments.status IS '배송 상태 (PREPARING: 준비중, SHIPPED: 배송, IN_TRANSIT: 운송중, DELIVERED: 완료, RETURNED: 반품)';
