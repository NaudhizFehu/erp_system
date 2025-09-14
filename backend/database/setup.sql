-- PostgreSQL 데이터베이스 및 사용자 생성 스크립트
-- 이 스크립트를 PostgreSQL에서 실행하여 데이터베이스와 사용자를 생성하세요

-- 1. 데이터베이스 생성
CREATE DATABASE cursor_erp_system
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'ko_KR.UTF-8'
    LC_CTYPE = 'ko_KR.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- 2. 사용자 생성
CREATE USER cursor_erp_system WITH PASSWORD 'cursor_erp_system';

-- 3. 사용자에게 권한 부여
GRANT ALL PRIVILEGES ON DATABASE cursor_erp_system TO cursor_erp_system;

-- 4. 데이터베이스에 연결하여 스키마 권한 부여
\c cursor_erp_system;

-- 5. 스키마 권한 부여
GRANT ALL ON SCHEMA public TO cursor_erp_system;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO cursor_erp_system;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO cursor_erp_system;

-- 6. 기본 권한 설정 (향후 생성될 테이블에 대한 권한)
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO cursor_erp_system;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO cursor_erp_system;

-- 완료 메시지
SELECT 'PostgreSQL 데이터베이스 및 사용자 설정이 완료되었습니다.' AS message;

