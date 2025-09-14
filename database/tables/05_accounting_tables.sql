-- 회계관리 모듈 테이블 생성 스크립트
-- 계정과목, 전표, 장부 관련 테이블을 생성합니다

-- 스키마 설정
SET search_path TO cursor_erp_system, public;

-- 계정과목 테이블
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    account_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('ASSET', 'LIABILITY', 'EQUITY', 'REVENUE', 'EXPENSE')),
    parent_account_id BIGINT REFERENCES accounts(id),
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 회계전표 테이블
CREATE TABLE vouchers (
    id BIGSERIAL PRIMARY KEY,
    voucher_number VARCHAR(50) NOT NULL UNIQUE,
    voucher_date DATE NOT NULL,
    voucher_type VARCHAR(20) NOT NULL CHECK (voucher_type IN ('GENERAL', 'CASH_RECEIPT', 'CASH_PAYMENT', 'BANK_RECEIPT', 'BANK_PAYMENT')),
    reference_type VARCHAR(50), -- 'SALE', 'PURCHASE', 'PAYMENT', 'RECEIPT' 등
    reference_id BIGINT,
    description TEXT,
    total_debit DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_credit DECIMAL(12,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'APPROVED', 'POSTED', 'CANCELLED')),
    created_by BIGINT NOT NULL REFERENCES employees(id),
    approved_by BIGINT REFERENCES employees(id),
    approved_at TIMESTAMP,
    posted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 회계전표 상세 테이블 (분개)
CREATE TABLE voucher_entries (
    id BIGSERIAL PRIMARY KEY,
    voucher_id BIGINT NOT NULL REFERENCES vouchers(id) ON DELETE CASCADE,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    debit_amount DECIMAL(12,2) DEFAULT 0,
    credit_amount DECIMAL(12,2) DEFAULT 0,
    description TEXT,
    line_order INTEGER NOT NULL DEFAULT 1
);

-- 총계정원장 테이블
CREATE TABLE general_ledger (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    voucher_entry_id BIGINT NOT NULL REFERENCES voucher_entries(id),
    transaction_date DATE NOT NULL,
    debit_amount DECIMAL(12,2) DEFAULT 0,
    credit_amount DECIMAL(12,2) DEFAULT 0,
    balance DECIMAL(12,2) NOT NULL DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 계정별 잔액 테이블 (성능 최적화용)
CREATE TABLE account_balances (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    balance_date DATE NOT NULL,
    debit_balance DECIMAL(12,2) NOT NULL DEFAULT 0,
    credit_balance DECIMAL(12,2) NOT NULL DEFAULT 0,
    net_balance DECIMAL(12,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(account_id, balance_date)
);

-- 예산 테이블
CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    budget_year INTEGER NOT NULL,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    budget_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    actual_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    variance_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    notes TEXT,
    created_by BIGINT NOT NULL REFERENCES employees(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(budget_year, account_id)
);

-- 인덱스 생성
CREATE INDEX idx_accounts_code ON accounts(account_code);
CREATE INDEX idx_accounts_type ON accounts(account_type);
CREATE INDEX idx_accounts_parent ON accounts(parent_account_id);

CREATE INDEX idx_vouchers_number ON vouchers(voucher_number);
CREATE INDEX idx_vouchers_date ON vouchers(voucher_date);
CREATE INDEX idx_vouchers_type ON vouchers(voucher_type);
CREATE INDEX idx_vouchers_status ON vouchers(status);
CREATE INDEX idx_vouchers_reference ON vouchers(reference_type, reference_id);

CREATE INDEX idx_voucher_entries_voucher ON voucher_entries(voucher_id);
CREATE INDEX idx_voucher_entries_account ON voucher_entries(account_id);

CREATE INDEX idx_general_ledger_account ON general_ledger(account_id);
CREATE INDEX idx_general_ledger_date ON general_ledger(transaction_date);
CREATE INDEX idx_general_ledger_voucher_entry ON general_ledger(voucher_entry_id);

CREATE INDEX idx_account_balances_account ON account_balances(account_id);
CREATE INDEX idx_account_balances_date ON account_balances(balance_date);

CREATE INDEX idx_budgets_year ON budgets(budget_year);
CREATE INDEX idx_budgets_account ON budgets(account_id);

-- 트리거 생성
CREATE TRIGGER update_accounts_updated_at BEFORE UPDATE ON accounts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_vouchers_updated_at BEFORE UPDATE ON vouchers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_account_balances_updated_at BEFORE UPDATE ON account_balances
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_budgets_updated_at BEFORE UPDATE ON budgets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 전표 합계 검증 트리거 함수
CREATE OR REPLACE FUNCTION validate_voucher_balance()
RETURNS TRIGGER AS $$
DECLARE
    v_total_debit DECIMAL(12,2);
    v_total_credit DECIMAL(12,2);
BEGIN
    -- 차변과 대변 합계 계산
    SELECT 
        COALESCE(SUM(debit_amount), 0),
        COALESCE(SUM(credit_amount), 0)
    INTO v_total_debit, v_total_credit
    FROM voucher_entries 
    WHERE voucher_id = COALESCE(NEW.voucher_id, OLD.voucher_id);
    
    -- 전표 테이블의 합계 업데이트
    UPDATE vouchers 
    SET total_debit = v_total_debit,
        total_credit = v_total_credit,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = COALESCE(NEW.voucher_id, OLD.voucher_id);
    
    -- 차변과 대변 불일치 시 오류 발생
    IF v_total_debit != v_total_credit THEN
        RAISE EXCEPTION '차변과 대변의 합계가 일치하지 않습니다. 차변: %, 대변: %', v_total_debit, v_total_credit;
    END IF;
    
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- 총계정원장 업데이트 트리거 함수
CREATE OR REPLACE FUNCTION update_general_ledger()
RETURNS TRIGGER AS $$
DECLARE
    v_voucher_date DATE;
    v_description TEXT;
BEGIN
    -- 전표 정보 조회
    SELECT voucher_date, description 
    INTO v_voucher_date, v_description
    FROM vouchers 
    WHERE id = NEW.voucher_id;
    
    -- 총계정원장에 입력
    INSERT INTO general_ledger (
        account_id, 
        voucher_entry_id, 
        transaction_date, 
        debit_amount, 
        credit_amount, 
        description
    ) VALUES (
        NEW.account_id,
        NEW.id,
        v_voucher_date,
        NEW.debit_amount,
        NEW.credit_amount,
        COALESCE(NEW.description, v_description)
    );
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 잔액 업데이트 트리거 함수
CREATE OR REPLACE FUNCTION update_account_balance()
RETURNS TRIGGER AS $$
DECLARE
    v_transaction_date DATE;
    v_current_balance DECIMAL(12,2) := 0;
    v_debit_total DECIMAL(12,2) := 0;
    v_credit_total DECIMAL(12,2) := 0;
BEGIN
    v_transaction_date := NEW.transaction_date;
    
    -- 해당 계정의 누적 잔액 계산
    SELECT 
        COALESCE(SUM(debit_amount), 0),
        COALESCE(SUM(credit_amount), 0)
    INTO v_debit_total, v_credit_total
    FROM general_ledger 
    WHERE account_id = NEW.account_id 
    AND transaction_date <= v_transaction_date;
    
    v_current_balance := v_debit_total - v_credit_total;
    
    -- 총계정원장 잔액 업데이트
    UPDATE general_ledger 
    SET balance = v_current_balance
    WHERE id = NEW.id;
    
    -- 계정별 잔액 테이블 업데이트 (당일 잔액)
    INSERT INTO account_balances (
        account_id, 
        balance_date, 
        debit_balance, 
        credit_balance, 
        net_balance
    ) VALUES (
        NEW.account_id,
        v_transaction_date,
        v_debit_total,
        v_credit_total,
        v_current_balance
    ) ON CONFLICT (account_id, balance_date) 
    DO UPDATE SET
        debit_balance = v_debit_total,
        credit_balance = v_credit_total,
        net_balance = v_current_balance,
        updated_at = CURRENT_TIMESTAMP;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거 등록
CREATE TRIGGER trigger_validate_voucher_balance
    AFTER INSERT OR UPDATE OR DELETE ON voucher_entries
    FOR EACH ROW EXECUTE FUNCTION validate_voucher_balance();

CREATE TRIGGER trigger_update_general_ledger
    AFTER INSERT ON voucher_entries
    FOR EACH ROW EXECUTE FUNCTION update_general_ledger();

CREATE TRIGGER trigger_update_account_balance
    AFTER INSERT ON general_ledger
    FOR EACH ROW EXECUTE FUNCTION update_account_balance();

-- 테이블 코멘트
COMMENT ON TABLE accounts IS '계정과목 테이블';
COMMENT ON TABLE vouchers IS '회계전표 테이블';
COMMENT ON TABLE voucher_entries IS '회계전표 상세 테이블 (분개)';
COMMENT ON TABLE general_ledger IS '총계정원장 테이블';
COMMENT ON TABLE account_balances IS '계정별 잔액 테이블';
COMMENT ON TABLE budgets IS '예산 테이블';

-- 컬럼 코멘트
COMMENT ON COLUMN accounts.account_type IS '계정 유형 (ASSET: 자산, LIABILITY: 부채, EQUITY: 자본, REVENUE: 수익, EXPENSE: 비용)';
COMMENT ON COLUMN vouchers.voucher_type IS '전표 유형 (GENERAL: 일반, CASH_RECEIPT: 현금입금, CASH_PAYMENT: 현금출금, BANK_RECEIPT: 은행입금, BANK_PAYMENT: 은행출금)';
COMMENT ON COLUMN vouchers.status IS '전표 상태 (DRAFT: 임시저장, APPROVED: 승인, POSTED: 전기, CANCELLED: 취소)';
COMMENT ON COLUMN general_ledger.balance IS '누적 잔액 (차변 - 대변)';
COMMENT ON COLUMN account_balances.net_balance IS '순잔액 (차변 - 대변)';
