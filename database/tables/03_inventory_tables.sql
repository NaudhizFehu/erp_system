-- 재고관리 모듈 테이블 생성 스크립트
-- 제품, 카테고리, 재고 관련 테이블을 생성합니다

-- 스키마 설정
SET search_path TO cursor_erp_system, public;

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

-- 재고 조정 테이블
CREATE TABLE stock_adjustments (
    id BIGSERIAL PRIMARY KEY,
    adjustment_number VARCHAR(50) NOT NULL UNIQUE,
    adjustment_date DATE NOT NULL,
    reason VARCHAR(200),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'APPROVED', 'CANCELLED')),
    created_by BIGINT NOT NULL REFERENCES employees(id),
    approved_by BIGINT REFERENCES employees(id),
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 재고 조정 상세 테이블
CREATE TABLE stock_adjustment_details (
    id BIGSERIAL PRIMARY KEY,
    adjustment_id BIGINT NOT NULL REFERENCES stock_adjustments(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    current_quantity INTEGER NOT NULL,
    adjusted_quantity INTEGER NOT NULL,
    difference_quantity INTEGER NOT NULL,
    unit_price DECIMAL(12,2),
    notes TEXT
);

-- 인덱스 생성
CREATE INDEX idx_products_code ON products(product_code);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_name ON products(name);

CREATE INDEX idx_product_categories_code ON product_categories(category_code);
CREATE INDEX idx_product_categories_parent ON product_categories(parent_category_id);

CREATE INDEX idx_stock_movements_product ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_type ON stock_movements(movement_type);
CREATE INDEX idx_stock_movements_date ON stock_movements(movement_date);
CREATE INDEX idx_stock_movements_reference ON stock_movements(reference_type, reference_id);

CREATE INDEX idx_stock_adjustments_number ON stock_adjustments(adjustment_number);
CREATE INDEX idx_stock_adjustments_date ON stock_adjustments(adjustment_date);
CREATE INDEX idx_stock_adjustments_status ON stock_adjustments(status);

-- 트리거 생성
CREATE TRIGGER update_product_categories_updated_at BEFORE UPDATE ON product_categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_stock_adjustments_updated_at BEFORE UPDATE ON stock_adjustments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 재고 업데이트 트리거 함수
CREATE OR REPLACE FUNCTION update_product_stock()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- 재고 이동 시 제품 재고 업데이트
        IF NEW.movement_type = 'IN' THEN
            UPDATE products 
            SET current_stock = current_stock + NEW.quantity,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = NEW.product_id;
        ELSIF NEW.movement_type = 'OUT' THEN
            UPDATE products 
            SET current_stock = current_stock - NEW.quantity,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = NEW.product_id;
        ELSIF NEW.movement_type = 'ADJUSTMENT' THEN
            UPDATE products 
            SET current_stock = current_stock + NEW.quantity,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = NEW.product_id;
        END IF;
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 재고 업데이트 트리거
CREATE TRIGGER trigger_update_product_stock
    AFTER INSERT ON stock_movements
    FOR EACH ROW EXECUTE FUNCTION update_product_stock();

-- 테이블 코멘트
COMMENT ON TABLE product_categories IS '제품 카테고리 테이블';
COMMENT ON TABLE products IS '제품 정보 테이블';
COMMENT ON TABLE stock_movements IS '재고 이동 이력 테이블';
COMMENT ON TABLE stock_adjustments IS '재고 조정 테이블';
COMMENT ON TABLE stock_adjustment_details IS '재고 조정 상세 테이블';

-- 컬럼 코멘트
COMMENT ON COLUMN products.status IS '제품 상태 (ACTIVE: 활성, INACTIVE: 비활성, DISCONTINUED: 단종)';
COMMENT ON COLUMN stock_movements.movement_type IS '재고 이동 유형 (IN: 입고, OUT: 출고, ADJUSTMENT: 조정)';
COMMENT ON COLUMN stock_adjustments.status IS '조정 상태 (DRAFT: 임시저장, APPROVED: 승인, CANCELLED: 취소)';
