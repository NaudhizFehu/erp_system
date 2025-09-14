-- ERP 시스템 샘플 데이터 삽입 스크립트
-- 개발 및 테스트를 위한 기본 데이터를 생성합니다

-- 스키마 설정
SET search_path TO cursor_erp_system, public;

-- 직급 데이터
INSERT INTO positions (position_code, name, description, level_order) VALUES
('CEO', '대표이사', '최고경영자', 1),
('EXEC', '임원', '임원급', 2),
('MGR', '팀장', '팀 관리자', 3),
('SENIOR', '선임', '선임급', 4),
('JUNIOR', '주니어', '주니어급', 5),
('INTERN', '인턴', '인턴사원', 6);

-- 부서 데이터
INSERT INTO departments (department_code, name, description) VALUES
('CEO', '경영진', '최고경영진'),
('HR', '인사팀', '인사관리 및 총무'),
('DEV', '개발팀', '소프트웨어 개발'),
('MKT', '마케팅팀', '마케팅 및 홍보'),
('SALES', '영업팀', '영업 및 고객관리'),
('FIN', '재무팀', '재무 및 회계'),
('IT', 'IT팀', 'IT 인프라 및 시스템 관리');

-- 직원 데이터 (부서장 설정 전)
INSERT INTO employees (employee_number, name, email, phone, hire_date, department_id, position_id, salary, status, address) VALUES
('EMP001', '김대표', 'ceo@company.com', '010-1111-1111', '2020-01-01', 1, 1, 12000000, 'ACTIVE', '서울시 강남구'),
('EMP002', '박인사', 'hr@company.com', '010-2222-2222', '2020-03-01', 2, 3, 5500000, 'ACTIVE', '서울시 서초구'),
('EMP003', '이개발', 'dev@company.com', '010-3333-3333', '2020-06-01', 3, 3, 6000000, 'ACTIVE', '서울시 마포구'),
('EMP004', '최마케팅', 'mkt@company.com', '010-4444-4444', '2021-01-01', 4, 3, 5000000, 'ACTIVE', '서울시 용산구'),
('EMP005', '정영업', 'sales@company.com', '010-5555-5555', '2021-03-01', 5, 3, 4800000, 'ACTIVE', '서울시 송파구'),
('EMP006', '김재무', 'fin@company.com', '010-6666-6666', '2021-06-01', 6, 3, 5200000, 'ACTIVE', '서울시 강동구'),
('EMP007', '장개발자', 'dev1@company.com', '010-7777-7777', '2022-01-01', 3, 4, 4500000, 'ACTIVE', '서울시 성동구'),
('EMP008', '윤개발자', 'dev2@company.com', '010-8888-8888', '2022-03-01', 3, 5, 3500000, 'ACTIVE', '서울시 광진구'),
('EMP009', '한영업사원', 'sales1@company.com', '010-9999-9999', '2022-06-01', 5, 4, 4000000, 'ACTIVE', '경기도 성남시'),
('EMP010', '오마케터', 'mkt1@company.com', '010-1010-1010', '2023-01-01', 4, 4, 3800000, 'ACTIVE', '경기도 수원시');

-- 부서 관리자 설정
UPDATE departments SET manager_id = 1 WHERE department_code = 'CEO';
UPDATE departments SET manager_id = 2 WHERE department_code = 'HR';
UPDATE departments SET manager_id = 3 WHERE department_code = 'DEV';
UPDATE departments SET manager_id = 4 WHERE department_code = 'MKT';
UPDATE departments SET manager_id = 5 WHERE department_code = 'SALES';
UPDATE departments SET manager_id = 6 WHERE department_code = 'FIN';

-- 제품 카테고리 데이터
INSERT INTO product_categories (category_code, name, description) VALUES
('ELEC', '전자제품', '전자기기 및 부품'),
('FURN', '가구', '사무용 가구'),
('STAT', '문구류', '사무용품 및 문구'),
('COMP', '컴퓨터', '컴퓨터 및 주변기기'),
('SOFT', '소프트웨어', '소프트웨어 라이선스');

-- 제품 데이터
INSERT INTO products (product_code, name, description, category_id, unit_price, current_stock, minimum_stock, maximum_stock, unit, manufacturer, brand, status) VALUES
('PRD001', '노트북 컴퓨터', '업무용 노트북', 4, 1500000.00, 25, 10, 50, '대', 'Samsung', 'Galaxy Book', 'ACTIVE'),
('PRD002', '무선 마우스', 'Bluetooth 무선 마우스', 4, 45000.00, 150, 50, 200, '개', 'Logitech', 'MX Master', 'ACTIVE'),
('PRD003', '사무용 의자', '인체공학적 사무용 의자', 2, 250000.00, 30, 15, 60, '개', '한샘', 'Ergonomic', 'ACTIVE'),
('PRD004', '모니터', '27인치 4K 모니터', 1, 400000.00, 40, 20, 80, '대', 'LG', 'UltraFine', 'ACTIVE'),
('PRD005', '키보드', '기계식 키보드', 4, 120000.00, 80, 30, 150, '개', 'Leopold', 'FC980M', 'ACTIVE'),
('PRD006', 'A4 용지', '복사용지 A4 500매', 3, 5000.00, 200, 100, 500, '박스', '한국제지', 'Double A', 'ACTIVE'),
('PRD007', '볼펜', '3색 볼펜', 3, 2000.00, 500, 200, 1000, '자루', '모나미', '153', 'ACTIVE'),
('PRD008', '데스크탑 컴퓨터', '고성능 워크스테이션', 4, 2500000.00, 15, 5, 30, '대', 'HP', 'Z4', 'ACTIVE'),
('PRD009', '프린터', '레이저 프린터', 1, 350000.00, 20, 10, 40, '대', 'Canon', 'ImageClass', 'ACTIVE'),
('PRD010', '책상', '사무용 책상 1200mm', 2, 180000.00, 35, 15, 70, '개', '이케아', 'BEKANT', 'ACTIVE');

-- 고객 데이터
INSERT INTO customers (customer_code, name, customer_type, email, phone, address, contact_person, contact_email, business_number, status) VALUES
('CUS001', '(주)테크솔루션', 'CORPORATE', 'contact@techsolution.co.kr', '02-1234-5678', '서울시 강남구 테헤란로 123', '김담당', 'kim@techsolution.co.kr', '123-45-67890', 'ACTIVE'),
('CUS002', '스타트업코리아', 'CORPORATE', 'info@startup.kr', '02-2345-6789', '서울시 서초구 서초대로 456', '이매니저', 'manager@startup.kr', '234-56-78901', 'ACTIVE'),
('CUS003', '김개인', 'INDIVIDUAL', 'kim.individual@gmail.com', '010-1111-2222', '경기도 성남시 분당구', NULL, NULL, NULL, 'ACTIVE'),
('CUS004', '(주)글로벌트레이딩', 'CORPORATE', 'trade@global.com', '02-3456-7890', '서울시 마포구 월드컵북로 789', '박팀장', 'park@global.com', '345-67-89012', 'ACTIVE'),
('CUS005', '이소상공인', 'INDIVIDUAL', 'small.business@naver.com', '010-3333-4444', '부산시 해운대구', NULL, NULL, NULL, 'ACTIVE');

-- 계정과목 데이터 (기본 계정과목 체계)
INSERT INTO accounts (account_code, name, account_type, description) VALUES
-- 자산
('1000', '자산', 'ASSET', '자산 총계'),
('1100', '유동자산', 'ASSET', '1년 이내 현금화 가능한 자산'),
('1110', '현금및현금성자산', 'ASSET', '현금 및 현금성 자산'),
('1111', '현금', 'ASSET', '보유 현금'),
('1112', '보통예금', 'ASSET', '은행 보통예금'),
('1120', '매출채권', 'ASSET', '고객으로부터 받을 돈'),
('1130', '재고자산', 'ASSET', '판매를 위한 상품'),
('1200', '비유동자산', 'ASSET', '1년 이상 보유할 자산'),
('1210', '유형자산', 'ASSET', '물리적 형태가 있는 자산'),
('1211', '건물', 'ASSET', '건물 및 구축물'),
('1212', '차량운반구', 'ASSET', '차량 및 운반구'),
('1213', '비품', 'ASSET', '사무용 비품'),

-- 부채
('2000', '부채', 'LIABILITY', '부채 총계'),
('2100', '유동부채', 'LIABILITY', '1년 이내 상환할 부채'),
('2110', '매입채무', 'LIABILITY', '공급업체에 지불할 돈'),
('2120', '미지급금', 'LIABILITY', '미지급 비용'),
('2130', '단기차입금', 'LIABILITY', '1년 이내 상환할 차입금'),

-- 자본
('3000', '자본', 'EQUITY', '자본 총계'),
('3100', '자본금', 'EQUITY', '납입자본금'),
('3200', '이익잉여금', 'EQUITY', '누적 이익'),

-- 수익
('4000', '수익', 'REVENUE', '수익 총계'),
('4100', '매출액', 'REVENUE', '제품 및 서비스 매출'),
('4200', '기타수익', 'REVENUE', '영업외 수익'),

-- 비용
('5000', '비용', 'EXPENSE', '비용 총계'),
('5100', '매출원가', 'EXPENSE', '매출과 직접 관련된 비용'),
('5200', '판매비와관리비', 'EXPENSE', '판매 및 관리 비용'),
('5210', '급여', 'EXPENSE', '직원 급여'),
('5220', '임차료', 'EXPENSE', '사무실 임대료'),
('5230', '통신비', 'EXPENSE', '전화 및 인터넷 비용'),
('5240', '소모품비', 'EXPENSE', '사무용품 등 소모품'),
('5300', '기타비용', 'EXPENSE', '영업외 비용');

-- 계정과목 계층 구조 설정
UPDATE accounts SET parent_account_id = (SELECT id FROM accounts WHERE account_code = '1000') WHERE account_code IN ('1100', '1200');
UPDATE accounts SET parent_account_id = (SELECT id FROM accounts WHERE account_code = '1100') WHERE account_code IN ('1110', '1120', '1130');
UPDATE accounts SET parent_account_id = (SELECT id FROM accounts WHERE account_code = '1110') WHERE account_code IN ('1111', '1112');
UPDATE accounts SET parent_account_id = (SELECT id FROM accounts WHERE account_code = '1200') WHERE account_code = '1210';
UPDATE accounts SET parent_account_id = (SELECT id FROM accounts WHERE account_code = '1210') WHERE account_code IN ('1211', '1212', '1213');

UPDATE accounts SET parent_account_id = (SELECT id FROM accounts WHERE account_code = '2000') WHERE account_code = '2100';
UPDATE accounts SET parent_account_id = (SELECT id FROM accounts WHERE account_code = '2100') WHERE account_code IN ('2110', '2120', '2130');

UPDATE accounts SET parent_account_id = (SELECT id FROM accounts WHERE account_code = '3000') WHERE account_code IN ('3100', '3200');

UPDATE accounts SET parent_account_id = (SELECT id FROM accounts WHERE account_code = '4000') WHERE account_code IN ('4100', '4200');

UPDATE accounts SET parent_account_id = (SELECT id FROM accounts WHERE account_code = '5000') WHERE account_code IN ('5100', '5200', '5300');
UPDATE accounts SET parent_account_id = (SELECT id FROM accounts WHERE account_code = '5200') WHERE account_code IN ('5210', '5220', '5230', '5240');

-- 샘플 견적서 데이터
INSERT INTO quotes (quote_number, customer_id, quote_date, valid_until, status, notes, created_by) VALUES
('QT2024001', 1, '2024-01-15', '2024-02-14', 'SENT', '노트북 및 주변기기 견적', 5),
('QT2024002', 2, '2024-01-20', '2024-02-19', 'DRAFT', '사무용품 일괄 견적', 5);

-- 견적서 상세 데이터
INSERT INTO quote_details (quote_id, product_id, quantity, unit_price, line_total) VALUES
(1, 1, 5, 1500000.00, 7500000.00),  -- 노트북 5대
(1, 2, 10, 45000.00, 450000.00),    -- 마우스 10개
(1, 5, 5, 120000.00, 600000.00),    -- 키보드 5개
(2, 6, 50, 5000.00, 250000.00),     -- A4 용지 50박스
(2, 7, 100, 2000.00, 200000.00);    -- 볼펜 100자루

-- 샘플 주문서 데이터
INSERT INTO orders (order_number, customer_id, quote_id, order_date, required_date, status, shipping_address, notes, created_by) VALUES
('ORD2024001', 1, 1, '2024-01-25', '2024-02-10', 'CONFIRMED', '서울시 강남구 테헤란로 123', '긴급 주문', 5);

-- 주문서 상세 데이터
INSERT INTO order_details (order_id, product_id, quantity, unit_price, line_total) VALUES
(1, 1, 5, 1500000.00, 7500000.00),
(1, 2, 10, 45000.00, 450000.00),
(1, 5, 5, 120000.00, 600000.00);

-- 샘플 재고 이동 데이터 (초기 입고)
INSERT INTO stock_movements (product_id, movement_type, quantity, unit_price, total_amount, reference_type, notes, created_by) VALUES
(1, 'IN', 30, 1500000.00, 45000000.00, 'INITIAL', '초기 재고 입고', 3),
(2, 'IN', 200, 45000.00, 9000000.00, 'INITIAL', '초기 재고 입고', 3),
(3, 'IN', 45, 250000.00, 11250000.00, 'INITIAL', '초기 재고 입고', 3),
(4, 'IN', 60, 400000.00, 24000000.00, 'INITIAL', '초기 재고 입고', 3),
(5, 'IN', 110, 120000.00, 13200000.00, 'INITIAL', '초기 재고 입고', 3);

-- 샘플 회계전표 데이터
INSERT INTO vouchers (voucher_number, voucher_date, voucher_type, description, status, created_by) VALUES
('V2024001', '2024-01-01', 'GENERAL', '초기 자본금 설정', 'POSTED', 6),
('V2024002', '2024-01-15', 'GENERAL', '사무용품 구매', 'POSTED', 6),
('V2024003', '2024-01-25', 'GENERAL', '제품 매출', 'POSTED', 6);

-- 회계전표 상세 데이터 (분개)
INSERT INTO voucher_entries (voucher_id, account_id, debit_amount, credit_amount, description, line_order) VALUES
-- 초기 자본금 설정
(1, (SELECT id FROM accounts WHERE account_code = '1112'), 100000000.00, 0, '초기 자본금 입금', 1),
(1, (SELECT id FROM accounts WHERE account_code = '3100'), 0, 100000000.00, '자본금 설정', 2),

-- 사무용품 구매
(2, (SELECT id FROM accounts WHERE account_code = '5240'), 500000.00, 0, '사무용품 구매', 1),
(2, (SELECT id FROM accounts WHERE account_code = '1112'), 0, 500000.00, '구매대금 지급', 2),

-- 제품 매출
(3, (SELECT id FROM accounts WHERE account_code = '1120'), 8800000.00, 0, '제품 매출 채권', 1),
(3, (SELECT id FROM accounts WHERE account_code = '4100'), 0, 8000000.00, '제품 매출액', 2),
(3, (SELECT id FROM accounts WHERE account_code = '2120'), 0, 800000.00, '부가세 예수금', 3);

COMMIT;
