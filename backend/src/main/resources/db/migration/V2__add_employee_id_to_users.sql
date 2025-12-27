-- User 테이블에 employee_id 컬럼 추가
-- SUPER_ADMIN은 null, ADMIN/MANAGER/USER는 Employee와 매핑

ALTER TABLE users ADD COLUMN employee_id BIGINT;

-- employee_id에 대한 외래키 제약조건 추가
ALTER TABLE users ADD CONSTRAINT fk_users_employee
    FOREIGN KEY (employee_id) REFERENCES employees(id);

-- employee_id에 대한 인덱스 추가
CREATE INDEX idx_users_employee_id ON users(employee_id);

-- 기존 사용자들과 직원 매핑
-- 각 회사의 첫 번째 직원을 admin에 매핑

-- ABC 회사 admin (이예진 - employee_id=1)
UPDATE users SET employee_id = 1 WHERE username = 'admin' AND role = 'ADMIN';

-- XYZ 회사 admin (신지현 - employee_id=11)
UPDATE users SET employee_id = 11 WHERE username = 'xyz_admin' AND role = 'ADMIN';

-- XYZ 회사 manager (서미영 - employee_id=12)
UPDATE users SET employee_id = 12 WHERE username = 'xyz_manager' AND role = 'MANAGER';

-- DEF 회사의 직원 찾기 (company_id=3)
UPDATE users SET employee_id = (
    SELECT id FROM employees WHERE company_id = 3 ORDER BY id LIMIT 1
) WHERE username = 'def_admin' AND role = 'ADMIN';

UPDATE users SET employee_id = (
    SELECT id FROM employees WHERE company_id = 3 ORDER BY id LIMIT 1 OFFSET 1
) WHERE username = 'def_user' AND role = 'USER';

-- SUPER_ADMIN은 employee_id를 null로 유지 (이미 null이지만 명시적으로)
UPDATE users SET employee_id = NULL WHERE role = 'SUPER_ADMIN';
