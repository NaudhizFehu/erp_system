-- ERP 시스템 테스트 데이터 초기화 (실제 DB 스키마에 맞춤)
-- 주의: 이 스크립트는 개발/테스트 환경에서만 사용하세요

-- 1. 회사 데이터 삽입 (중복 방지)
INSERT INTO companies (id, company_code, name, name_en, business_number, corporation_number, ceo_name, business_type, business_item, address, detailed_address, postal_code, phone, fax, email, website, status, is_deleted) 
VALUES (1, 'ABC_CORP', 'ABC 기업', 'ABC Corporation', '123-45-67890', '123456-1234567', '김대표', '제조업', '전자제품 제조', '서울특별시 강남구 테헤란로 123', 'ABC빌딩 10층', '06292', '02-1234-5678', '02-1234-5679', 'info@abc.com', 'www.abc.com', 'ACTIVE', false)
ON CONFLICT (id) DO NOTHING;

-- 2. 부서 데이터 삽입 (중복 방지)
INSERT INTO departments (id, department_code, name, description, parent_department_id, manager_id, company_id) 
VALUES (1, 'HR_DEPT', '인사부', '인사관리 및 채용업무', NULL, NULL, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO departments (id, department_code, name, description, parent_department_id, manager_id, company_id) 
VALUES (2, 'SALES_DEPT', '영업부', '영업 및 고객관리', NULL, NULL, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO departments (id, department_code, name, description, parent_department_id, manager_id, company_id) 
VALUES (3, 'IT_DEPT', 'IT부서', '시스템 개발 및 유지보수', NULL, NULL, 1)
ON CONFLICT (id) DO NOTHING;

-- 2. 직급 데이터 삽입 (중복 방지)
INSERT INTO positions (id, position_code, name, description, level_order) 
VALUES (1, 'CEO', '대표이사', '최고경영자', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO positions (id, position_code, name, description, level_order) 
VALUES (2, 'MANAGER', '부장', '부서장', 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO positions (id, position_code, name, description, level_order) 
VALUES (3, 'STAFF', '대리', '대리급', 7)
ON CONFLICT (id) DO NOTHING;

-- 3. 사용자 계정 데이터 삽입 (중복 방지)
INSERT INTO users (id, username, password, email, full_name, phone, role, is_active, is_locked, is_password_expired, company_id, department_id, password_changed_at) 
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin@abc.com', '관리자', '02-1234-5678', 'ADMIN', true, false, false, 1, 1, NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, username, password, email, full_name, phone, role, is_active, is_locked, is_password_expired, company_id, department_id, password_changed_at) 
VALUES (2, 'user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'user@abc.com', '일반사용자', '02-2345-6789', 'USER', true, false, false, 1, 3, NOW())
ON CONFLICT (id) DO NOTHING;

-- 4. 직원 데이터 삽입 (강제 삽입)
DELETE FROM employees WHERE id IN (1,2,3,4,5,6);
INSERT INTO employees (id, employee_number, name, email, phone, hire_date, department_id, position_id, salary, status, address, birth_date) 
VALUES (1, 'EMP001', '김관리', 'admin@abc.com', '010-1234-5678', '2020-01-01', 1, 1, 200000000, 'ACTIVE', '서울특별시 강남구', '1980-01-01');

INSERT INTO employees (id, employee_number, name, email, phone, hire_date, department_id, position_id, salary, status, address, birth_date) 
VALUES (2, 'EMP002', '이영업', 'sales@abc.com', '010-2345-6789', '2020-02-01', 2, 2, 150000000, 'ACTIVE', '서울특별시 서초구', '1985-05-15');

INSERT INTO employees (id, employee_number, name, email, phone, hire_date, department_id, position_id, salary, status, address, birth_date) 
VALUES (3, 'EMP003', '박개발', 'dev@abc.com', '010-3456-7890', '2020-03-01', 3, 3, 120000000, 'ACTIVE', '서울특별시 마포구', '1990-08-20');

INSERT INTO employees (id, employee_number, name, email, phone, hire_date, department_id, position_id, salary, status, address, birth_date) 
VALUES (4, 'EMP004', '김철수', 'kim@abc.com', '010-4567-8901', '2021-01-15', 1, 2, 80000000, 'ACTIVE', '서울특별시 송파구', '1988-03-10');

INSERT INTO employees (id, employee_number, name, email, phone, hire_date, department_id, position_id, salary, status, address, birth_date) 
VALUES (5, 'EMP005', '이영희', 'lee@abc.com', '010-5678-9012', '2021-02-20', 2, 3, 70000000, 'ACTIVE', '서울특별시 강동구', '1992-07-25');

INSERT INTO employees (id, employee_number, name, email, phone, hire_date, department_id, position_id, salary, status, address, birth_date) 
VALUES (6, 'EMP006', '박민수', 'park@abc.com', '010-6789-0123', '2021-03-10', 3, 1, 90000000, 'ACTIVE', '서울특별시 영등포구', '1987-11-05');

-- 5. 제품 카테고리 데이터 삽입 (중복 방지)
INSERT INTO product_categories (id, category_code, name, description, parent_category_id, is_active) 
VALUES (1, 'ELECTRONICS', '전자제품', '전자제품 카테고리', NULL, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_categories (id, category_code, name, description, parent_category_id, is_active) 
VALUES (2, 'COMPUTER', '컴퓨터', '컴퓨터 및 주변기기', 1, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_categories (id, category_code, name, description, parent_category_id, is_active) 
VALUES (3, 'OFFICE', '사무용품', '사무용품 및 소모품', NULL, true)
ON CONFLICT (id) DO NOTHING;

-- 6. 제품 데이터 삽입 (중복 방지)
INSERT INTO products (id, product_code, name, description, category_id, unit_price, current_stock, minimum_stock, maximum_stock, unit, manufacturer, brand, status) 
VALUES (1, 'LAPTOP001', '노트북', '고성능 노트북', 2, 1500000, 50, 10, 100, '대', '삼성', 'Samsung', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, product_code, name, description, category_id, unit_price, current_stock, minimum_stock, maximum_stock, unit, manufacturer, brand, status) 
VALUES (2, 'MOUSE001', '무선마우스', '블루투스 무선마우스', 2, 50000, 200, 50, 500, '개', '로지텍', 'Logitech', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, product_code, name, description, category_id, unit_price, current_stock, minimum_stock, maximum_stock, unit, manufacturer, brand, status) 
VALUES (3, 'PEN001', '볼펜', '검은색 볼펜', 3, 1000, 1000, 100, 2000, '자루', '모나미', 'Monami', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, product_code, name, description, category_id, unit_price, current_stock, minimum_stock, maximum_stock, unit, manufacturer, brand, status) 
VALUES (4, 'KEYBOARD001', '키보드', '기계식 키보드', 2, 150000, 30, 5, 50, '개', '체리', 'Cherry', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, product_code, name, description, category_id, unit_price, current_stock, minimum_stock, maximum_stock, unit, manufacturer, brand, status) 
VALUES (5, 'MONITOR001', '모니터', '27인치 4K 모니터', 2, 500000, 20, 3, 30, '대', 'LG', 'LG', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, product_code, name, description, category_id, unit_price, current_stock, minimum_stock, maximum_stock, unit, manufacturer, brand, status) 
VALUES (6, 'PAPER001', 'A4용지', '복사용 A4용지', 3, 5000, 500, 50, 1000, '박스', '한솔', 'Hansol', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- 7. 고객 데이터 삽입 (강제 삽입)
DELETE FROM customers WHERE id IN (1,2,3,4,5,6);
INSERT INTO customers (id, customer_code, name, customer_type, email, phone, fax, address, detailed_address, postal_code, contact_person, contact_position, contact_phone, contact_email, business_number, notes, status) 
VALUES (1, 'CUST001', 'ABC 기업', 'CORPORATE', 'info@abc.com', '02-1234-5678', '02-1234-5679', '서울특별시 강남구 테헤란로 123', 'ABC빌딩 10층', '06292', '김대표', '대표이사', '010-1111-2222', 'ceo@abc.com', '123-45-67890', '주요 고객사', 'ACTIVE');

INSERT INTO customers (id, customer_code, name, customer_type, email, phone, fax, address, detailed_address, postal_code, contact_person, contact_position, contact_phone, contact_email, business_number, notes, status) 
VALUES (2, 'CUST002', 'XYZ 주식회사', 'CORPORATE', 'contact@xyz.com', '02-2345-6789', '02-2345-6790', '서울특별시 서초구 서초대로 456', 'XYZ타워 15층', '06578', '이사장', '사장', '010-3333-4444', 'president@xyz.com', '234-56-78901', '신규 고객사', 'ACTIVE');

INSERT INTO customers (id, customer_code, name, customer_type, email, phone, fax, address, detailed_address, postal_code, contact_person, contact_position, contact_phone, contact_email, business_number, notes, status) 
VALUES (3, 'CUST003', '홍길동', 'INDIVIDUAL', 'hong@email.com', '010-5555-6666', NULL, '서울특별시 마포구 홍대입구역', '홍대아파트 101동 1001호', '04050', '홍길동', '개인', '010-5555-6666', 'hong@email.com', NULL, '개인 고객', 'ACTIVE');

INSERT INTO customers (id, customer_code, name, customer_type, email, phone, fax, address, detailed_address, postal_code, contact_person, contact_position, contact_phone, contact_email, business_number, notes, status) 
VALUES (4, 'CUST004', '김철수', 'INDIVIDUAL', 'kim@email.com', '010-7777-8888', NULL, '서울특별시 송파구 잠실동', '잠실아파트 201동 2001호', '05551', '김철수', '개인', '010-7777-8888', 'kim@email.com', NULL, '개인 고객', 'ACTIVE');

INSERT INTO customers (id, customer_code, name, customer_type, email, phone, fax, address, detailed_address, postal_code, contact_person, contact_position, contact_phone, contact_email, business_number, notes, status) 
VALUES (5, 'CUST005', '이영희', 'INDIVIDUAL', 'lee@email.com', '010-9999-0000', NULL, '서울특별시 강동구 천호동', '천호아파트 301동 3001호', '05200', '이영희', '개인', '010-9999-0000', 'lee@email.com', NULL, '개인 고객', 'ACTIVE');

INSERT INTO customers (id, customer_code, name, customer_type, email, phone, fax, address, detailed_address, postal_code, contact_person, contact_position, contact_phone, contact_email, business_number, notes, status) 
VALUES (6, 'CUST006', '박민수', 'INDIVIDUAL', 'park@email.com', '010-1111-3333', NULL, '서울특별시 영등포구 여의도동', '여의도아파트 401동 4001호', '07345', '박민수', '개인', '010-1111-3333', 'park@email.com', NULL, '개인 고객', 'ACTIVE');

-- 8. 견적서 데이터 삽입 (중복 방지)
INSERT INTO quotes (id, quote_number, customer_id, quote_date, valid_until, status, subtotal, tax_amount, total_amount, notes, terms_conditions, created_by) 
VALUES (1, 'QUO-2024-001', 1, '2024-01-15', '2024-02-15', 'SENT', 1500000, 150000, 1650000, '노트북 견적서', '30일 내 납기', 2)
ON CONFLICT (id) DO NOTHING;

-- 9. 견적서 상세 데이터 삽입 (중복 방지)
INSERT INTO quote_details (id, quote_id, product_id, quantity, unit_price, discount_rate, line_total, notes) 
VALUES (1, 1, 1, 1, 1500000, 0, 1500000, '노트북 1대')
ON CONFLICT (id) DO NOTHING;

-- 10. 주문 데이터 삽입 (중복 방지)
INSERT INTO orders (id, order_number, customer_id, quote_id, order_date, required_date, status, subtotal, tax_amount, total_amount, shipping_address, billing_address, notes, created_by) 
VALUES (1, 'ORD-2024-001', 1, 1, '2024-01-20', '2024-02-20', 'CONFIRMED', 1500000, 150000, 1650000, '서울특별시 강남구 테헤란로 123 ABC빌딩 10층', '서울특별시 강남구 테헤란로 123 ABC빌딩 10층', '노트북 주문', 2)
ON CONFLICT (id) DO NOTHING;

-- 11. 주문 상세 데이터 삽입 (중복 방지)
INSERT INTO order_details (id, order_id, product_id, quantity, unit_price, discount_rate, line_total, notes) 
VALUES (1, 1, 1, 1, 1500000, 0, 1500000, '노트북 1대')
ON CONFLICT (id) DO NOTHING;

-- 12. 추가 주문 데이터 삽입 (중복 방지)
INSERT INTO orders (id, order_number, customer_id, quote_id, order_date, required_date, status, subtotal, tax_amount, total_amount, shipping_address, billing_address, notes, created_by) 
VALUES (2, 'ORD-2024-002', 2, NULL, '2024-01-25', '2024-02-25', 'PROCESSING', 200000, 20000, 220000, '서울특별시 서초구 서초대로 456 XYZ타워 15층', '서울특별시 서초구 서초대로 456 XYZ타워 15층', '마우스 주문', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_details (id, order_id, product_id, quantity, unit_price, discount_rate, line_total, notes) 
VALUES (2, 2, 2, 4, 50000, 0, 200000, '무선마우스 4개')
ON CONFLICT (id) DO NOTHING;

INSERT INTO orders (id, order_number, customer_id, quote_id, order_date, required_date, status, subtotal, tax_amount, total_amount, shipping_address, billing_address, notes, created_by) 
VALUES (3, 'ORD-2024-003', 3, NULL, '2024-02-01', '2024-03-01', 'SHIPPED', 1000000, 100000, 1100000, '서울특별시 마포구 홍대입구역 홍대아파트 101동 1001호', '서울특별시 마포구 홍대입구역 홍대아파트 101동 1001호', '키보드 주문', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_details (id, order_id, product_id, quantity, unit_price, discount_rate, line_total, notes) 
VALUES (3, 3, 4, 2, 150000, 0, 300000, '키보드 2개')
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_details (id, order_id, product_id, quantity, unit_price, discount_rate, line_total, notes) 
VALUES (4, 3, 5, 1, 500000, 0, 500000, '모니터 1대')
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_details (id, order_id, product_id, quantity, unit_price, discount_rate, line_total, notes) 
VALUES (5, 3, 2, 4, 50000, 0, 200000, '무선마우스 4개')
ON CONFLICT (id) DO NOTHING;

-- 12. 재고 이동 이력 데이터 삽입 (중복 방지)
INSERT INTO stock_movements (id, product_id, movement_type, quantity, unit_price, total_amount, reference_type, reference_id, notes, movement_date, created_by) 
VALUES (1, 1, 'IN', 100, 1200000, 120000000, 'PURCHASE', 1, '초기 재고 입고', '2024-01-01 09:00:00', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO stock_movements (id, product_id, movement_type, quantity, unit_price, total_amount, reference_type, reference_id, notes, movement_date, created_by) 
VALUES (2, 1, 'OUT', 1, 1500000, 1500000, 'SALE', 1, '주문 출고', '2024-01-20 14:30:00', 2)
ON CONFLICT (id) DO NOTHING;

-- 13. 회계 계정과목 데이터 삽입 (중복 방지)
INSERT INTO accounts (id, account_code, account_name, account_type, parent_account_id, is_active, description) 
VALUES (1, '1000', '자산', 'ASSET', NULL, true, '자산 계정')
ON CONFLICT (id) DO NOTHING;

INSERT INTO accounts (id, account_code, account_name, account_type, parent_account_id, is_active, description) 
VALUES (2, '1100', '현금및현금성자산', 'ASSET', 1, true, '현금 및 예금')
ON CONFLICT (id) DO NOTHING;

INSERT INTO accounts (id, account_code, account_name, account_type, parent_account_id, is_active, description) 
VALUES (3, '1200', '매출채권', 'ASSET', 1, true, '매출채권')
ON CONFLICT (id) DO NOTHING;

INSERT INTO accounts (id, account_code, account_name, account_type, parent_account_id, is_active, description) 
VALUES (4, '2000', '부채', 'LIABILITY', NULL, true, '부채 계정')
ON CONFLICT (id) DO NOTHING;

INSERT INTO accounts (id, account_code, account_name, account_type, parent_account_id, is_active, description) 
VALUES (5, '3000', '자본', 'EQUITY', NULL, true, '자본 계정')
ON CONFLICT (id) DO NOTHING;

INSERT INTO accounts (id, account_code, account_name, account_type, parent_account_id, is_active, description) 
VALUES (6, '4000', '수익', 'REVENUE', NULL, true, '수익 계정')
ON CONFLICT (id) DO NOTHING;

INSERT INTO accounts (id, account_code, account_name, account_type, parent_account_id, is_active, description) 
VALUES (7, '5000', '비용', 'EXPENSE', NULL, true, '비용 계정')
ON CONFLICT (id) DO NOTHING;

-- 14. 거래 데이터 삽입 (중복 방지)
INSERT INTO transactions (id, transaction_date, description, reference_type, reference_id, total_amount, status, created_by) 
VALUES (1, '2024-01-20', '노트북 판매', 'SALE', 1, 1650000, 'POSTED', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO transactions (id, transaction_date, description, reference_type, reference_id, total_amount, status, created_by) 
VALUES (2, '2024-01-25', '마우스 판매', 'SALE', 2, 220000, 'POSTED', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO transactions (id, transaction_date, description, reference_type, reference_id, total_amount, status, created_by) 
VALUES (3, '2024-02-01', '키보드 및 모니터 판매', 'SALE', 3, 1100000, 'POSTED', 2)
ON CONFLICT (id) DO NOTHING;

-- 15. 거래 상세 데이터 삽입 (중복 방지)
INSERT INTO transaction_details (id, transaction_id, account_id, debit_amount, credit_amount, description) 
VALUES (1, 1, 2, 1650000, 0, '현금 수취')
ON CONFLICT (id) DO NOTHING;

INSERT INTO transaction_details (id, transaction_id, account_id, debit_amount, credit_amount, description) 
VALUES (2, 1, 6, 0, 1500000, '매출')
ON CONFLICT (id) DO NOTHING;

INSERT INTO transaction_details (id, transaction_id, account_id, debit_amount, credit_amount, description) 
VALUES (3, 1, 4, 0, 150000, '부가세')
ON CONFLICT (id) DO NOTHING;

-- 시퀀스 값 업데이트
SELECT setval('companies_id_seq', (SELECT MAX(id) FROM companies));
SELECT setval('departments_id_seq', (SELECT MAX(id) FROM departments));
SELECT setval('positions_id_seq', (SELECT MAX(id) FROM positions));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('employees_id_seq', (SELECT MAX(id) FROM employees));
SELECT setval('product_categories_id_seq', (SELECT MAX(id) FROM product_categories));
SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));
SELECT setval('customers_id_seq', (SELECT MAX(id) FROM customers));
SELECT setval('quotes_id_seq', (SELECT MAX(id) FROM quotes));
SELECT setval('quote_details_id_seq', (SELECT MAX(id) FROM quote_details));
SELECT setval('orders_id_seq', (SELECT MAX(id) FROM orders));
SELECT setval('order_details_id_seq', (SELECT MAX(id) FROM order_details));
SELECT setval('stock_movements_id_seq', (SELECT MAX(id) FROM stock_movements));
SELECT setval('accounts_id_seq', (SELECT MAX(id) FROM accounts));
SELECT setval('transactions_id_seq', (SELECT MAX(id) FROM transactions));
SELECT setval('transaction_details_id_seq', (SELECT MAX(id) FROM transaction_details));

-- 완료 메시지
SELECT 'ERP 시스템 테스트 데이터 초기화가 완료되었습니다.' AS message;