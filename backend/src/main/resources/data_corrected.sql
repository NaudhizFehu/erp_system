-- ERP 시스템 테스트 데이터 초기화 (실제 DB 스키마에 맞춤)
-- 주의: 이 스크립트는 개발/테스트 환경에서만 사용하세요

-- 1. 부서 데이터 삽입 (중복 방지)
INSERT INTO departments (id, department_code, name, description, parent_department_id, manager_id, created_at, updated_at) 
VALUES (1, 'HR_DEPT', '인사부', '인사관리 및 채용업무', NULL, NULL, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO departments (id, department_code, name, description, parent_department_id, manager_id, created_at, updated_at) 
VALUES (2, 'SALES_DEPT', '영업부', '영업 및 고객관리', NULL, NULL, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO departments (id, department_code, name, description, parent_department_id, manager_id, created_at, updated_at) 
VALUES (3, 'IT_DEPT', 'IT부서', '시스템 개발 및 유지보수', NULL, NULL, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 2. 직급 데이터 삽입 (중복 방지)
INSERT INTO positions (id, position_code, name, description, level_order, created_at, updated_at) 
VALUES (1, 'CEO', '대표이사', '최고경영자', 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO positions (id, position_code, name, description, level_order, created_at, updated_at) 
VALUES (2, 'MANAGER', '부장', '부서장', 4, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO positions (id, position_code, name, description, level_order, created_at, updated_at) 
VALUES (3, 'STAFF', '대리', '대리급', 7, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 3. 직원 데이터 삽입 (중복 방지)
INSERT INTO employees (id, employee_number, name, email, phone, hire_date, department_id, position_id, salary, status, address, birth_date, created_at, updated_at) 
VALUES (1, 'EMP001', '김관리', 'admin@abc.com', '010-1234-5678', '2020-01-01', 1, 1, 200000000, 'ACTIVE', '서울특별시 강남구', '1980-01-01', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO employees (id, employee_number, name, email, phone, hire_date, department_id, position_id, salary, status, address, birth_date, created_at, updated_at) 
VALUES (2, 'EMP002', '이영업', 'leesales@abc.com', '010-3456-7890', '2021-06-01', 2, 2, 80000000, 'ACTIVE', '서울특별시 서초구', '1985-05-15', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO employees (id, employee_number, name, email, phone, hire_date, department_id, position_id, salary, status, address, birth_date, created_at, updated_at) 
VALUES (3, 'EMP003', '박개발', 'parkit@abc.com', '010-4567-8901', '2022-01-10', 3, 3, 60000000, 'ACTIVE', '경기도 성남시', '1990-03-20', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 4. 고객 데이터 삽입 (중복 방지)
INSERT INTO customers (id, customer_code, name, customer_type, email, phone, fax, address, detailed_address, postal_code, contact_person, contact_position, contact_phone, contact_email, business_number, notes, status, created_at, updated_at) 
VALUES (1, 'CUST001', 'ABC 회사', 'CORPORATE', 'contact@abc-customer.com', '02-1111-2222', '02-1111-2223', '서울특별시 중구 명동 123', 'ABC빌딩 5층', '04532', '김고객', '대표이사', '010-1111-2222', 'ceo@abc-customer.com', '111-11-11111', '주요 고객사', 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO customers (id, customer_code, name, customer_type, email, phone, fax, address, detailed_address, postal_code, contact_person, contact_position, contact_phone, contact_email, business_number, notes, status, created_at, updated_at) 
VALUES (2, 'CUST002', 'XYZ 기업', 'CORPORATE', 'info@xyz-customer.com', '051-3333-4444', '051-3333-4445', '부산광역시 해운대구 센텀동 456', 'XYZ센터 10층', '48099', '이고객', '영업부장', '010-3333-4444', 'sales@xyz-customer.com', '222-22-22222', '정기 거래처', 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO customers (id, customer_code, name, customer_type, email, phone, fax, address, detailed_address, postal_code, contact_person, contact_position, contact_phone, contact_email, business_number, notes, status, created_at, updated_at) 
VALUES (3, 'CUST003', 'DEF 주식회사', 'CORPORATE', 'sales@def-customer.com', '053-5555-6666', '053-5555-6667', '대구광역시 수성구 동대구로 789', 'DEF타워 20층', '42100', '박고객', '구매담당자', '010-5555-6666', 'purchase@def-customer.com', '333-33-33333', 'VIP 고객', 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO customers (id, customer_code, name, customer_type, email, phone, address, detailed_address, postal_code, contact_person, contact_phone, contact_email, notes, status, created_at, updated_at) 
VALUES (4, 'CUST004', 'GHI 개인', 'INDIVIDUAL', 'personal@ghi-customer.com', '032-7777-8888', '인천광역시 연수구 컨벤시아대로 321', 'GHI오피스 3층', '21999', '최고객', '010-7777-8888', 'personal@ghi-customer.com', '개인 고객', 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 5. 상품 카테고리 데이터 삽입 (중복 방지)
INSERT INTO product_categories (id, category_code, name, description, parent_category_id, is_active, created_at, updated_at) 
VALUES (1, 'CAT001', '전자제품', '전자기기 및 부품', NULL, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_categories (id, category_code, name, description, parent_category_id, is_active, created_at, updated_at) 
VALUES (2, 'CAT002', '컴퓨터', 'PC 및 주변기기', 1, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_categories (id, category_code, name, description, parent_category_id, is_active, created_at, updated_at) 
VALUES (3, 'CAT003', '모바일', '스마트폰 및 태블릿', 1, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 6. 상품 데이터 삽입 (중복 방지)
INSERT INTO products (id, product_code, name, description, category_id, unit_price, current_stock, minimum_stock, maximum_stock, unit, manufacturer, brand, status, created_at, updated_at) 
VALUES (1, 'PROD001', '노트북 컴퓨터', '고성능 비즈니스 노트북', 2, 1500000, 50, 10, 100, '대', '삼성전자', 'Samsung', 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, product_code, name, description, category_id, unit_price, current_stock, minimum_stock, maximum_stock, unit, manufacturer, brand, status, created_at, updated_at) 
VALUES (2, 'PROD002', '스마트폰', '최신 스마트폰 모델', 3, 800000, 100, 20, 200, '대', '애플', 'iPhone', 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, product_code, name, description, category_id, unit_price, current_stock, minimum_stock, maximum_stock, unit, manufacturer, brand, status, created_at, updated_at) 
VALUES (3, 'PROD003', '무선 마우스', '블루투스 무선 마우스', 2, 50000, 200, 50, 500, '개', '로지텍', 'Logitech', 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, product_code, name, description, category_id, unit_price, current_stock, minimum_stock, maximum_stock, unit, manufacturer, brand, status, created_at, updated_at) 
VALUES (4, 'PROD004', '프린터', '레이저 프린터', 2, 300000, 30, 5, 50, '대', 'HP', 'HP', 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 7. 주문 데이터 삽입 (중복 방지)
INSERT INTO orders (id, order_number, customer_id, quote_id, order_date, required_date, status, subtotal, tax_amount, total_amount, shipping_address, billing_address, notes, created_by, created_at, updated_at) 
VALUES (1, 'ORD-2024-001', 1, NULL, '2024-01-15', '2024-01-20', 'CONFIRMED', 2300000, 230000, 2530000, '서울특별시 중구 명동 123 ABC빌딩 5층', '서울특별시 중구 명동 123 ABC빌딩 5층', '긴급 주문', 2, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO orders (id, order_number, customer_id, quote_id, order_date, required_date, status, subtotal, tax_amount, total_amount, shipping_address, billing_address, notes, created_by, created_at, updated_at) 
VALUES (2, 'ORD-2024-002', 2, NULL, '2024-01-16', '2024-01-22', 'PROCESSING', 850000, 85000, 935000, '부산광역시 해운대구 센텀동 456 XYZ센터 10층', '부산광역시 해운대구 센텀동 456 XYZ센터 10층', '정기 주문', 2, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 8. 주문 상세 데이터 삽입 (중복 방지)
INSERT INTO order_details (id, order_id, product_id, quantity, unit_price, discount_rate, line_total, notes) 
VALUES (1, 1, 1, 1, 1500000, 0, 1500000, '노트북 1대')
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_details (id, order_id, product_id, quantity, unit_price, discount_rate, line_total, notes) 
VALUES (2, 1, 2, 1, 800000, 0, 800000, '스마트폰 1대')
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_details (id, order_id, product_id, quantity, unit_price, discount_rate, line_total, notes) 
VALUES (3, 2, 3, 10, 50000, 10, 450000, '무선 마우스 10개 (10% 할인)')
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_details (id, order_id, product_id, quantity, unit_price, discount_rate, line_total, notes) 
VALUES (4, 2, 4, 1, 300000, 0, 300000, '프린터 1대')
ON CONFLICT (id) DO NOTHING;

-- 시퀀스 값 업데이트 (PostgreSQL의 경우)
-- 다음 INSERT 시 올바른 ID가 생성되도록 시퀀스를 업데이트
SELECT setval('departments_id_seq', (SELECT MAX(id) FROM departments));
SELECT setval('positions_id_seq', (SELECT MAX(id) FROM positions));
SELECT setval('employees_id_seq', (SELECT MAX(id) FROM employees));
SELECT setval('customers_id_seq', (SELECT MAX(id) FROM customers));
SELECT setval('product_categories_id_seq', (SELECT MAX(id) FROM product_categories));
SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));
SELECT setval('orders_id_seq', (SELECT MAX(id) FROM orders));
SELECT setval('order_details_id_seq', (SELECT MAX(id) FROM order_details));



