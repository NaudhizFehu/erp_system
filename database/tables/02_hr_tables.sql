-- 인사관리 모듈 테이블 생성 스크립트
-- 직원, 부서, 직급 관련 테이블을 생성합니다

-- 스키마 설정
SET search_path TO cursor_erp_system, public;

-- 부서 테이블
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    department_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_department_id BIGINT REFERENCES departments(id),
    manager_id BIGINT, -- 순환 참조로 인해 나중에 외래키 추가
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

-- 부서 관리자 외래키 추가 (순환 참조 해결)
ALTER TABLE departments ADD CONSTRAINT fk_departments_manager 
FOREIGN KEY (manager_id) REFERENCES employees(id);

-- 인덱스 생성
CREATE INDEX idx_employees_employee_number ON employees(employee_number);
CREATE INDEX idx_employees_email ON employees(email);
CREATE INDEX idx_employees_department_id ON employees(department_id);
CREATE INDEX idx_employees_position_id ON employees(position_id);
CREATE INDEX idx_employees_status ON employees(status);
CREATE INDEX idx_employees_hire_date ON employees(hire_date);

CREATE INDEX idx_departments_code ON departments(department_code);
CREATE INDEX idx_departments_parent ON departments(parent_department_id);
CREATE INDEX idx_positions_code ON positions(position_code);
CREATE INDEX idx_positions_level ON positions(level_order);

-- 트리거 생성 (업데이트 시간 자동 설정)
CREATE TRIGGER update_departments_updated_at BEFORE UPDATE ON departments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_positions_updated_at BEFORE UPDATE ON positions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_employees_updated_at BEFORE UPDATE ON employees
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 테이블 코멘트
COMMENT ON TABLE departments IS '부서 정보 테이블';
COMMENT ON TABLE positions IS '직급 정보 테이블';
COMMENT ON TABLE employees IS '직원 정보 테이블';

-- 컬럼 코멘트
COMMENT ON COLUMN employees.employee_number IS '직원번호 (고유)';
COMMENT ON COLUMN employees.status IS '직원 상태 (ACTIVE: 재직, INACTIVE: 휴직, TERMINATED: 퇴사)';
COMMENT ON COLUMN departments.department_code IS '부서코드 (고유)';
COMMENT ON COLUMN positions.position_code IS '직급코드 (고유)';
COMMENT ON COLUMN positions.level_order IS '직급 순서 (낮을수록 높은 직급)';
