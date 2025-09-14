-- ERP 시스템 데이터베이스 초기화 스크립트
-- PostgreSQL 데이터베이스를 생성하고 기본 설정을 구성합니다

-- 데이터베이스 생성 (필요한 경우)
-- CREATE DATABASE cursor_erp_system WITH ENCODING 'UTF8';

-- 확장 기능 활성화
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 타임존 설정
SET timezone = 'Asia/Seoul';

-- 기본 스키마 생성
CREATE SCHEMA IF NOT EXISTS cursor_erp_system;
SET search_path TO cursor_erp_system, public;

-- 공통 함수 생성
-- 업데이트 시간 자동 설정 함수
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';





