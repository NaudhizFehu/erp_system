package com.erp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * DDL 테스터
 * Hibernate DDL이 정상적으로 실행되는지 확인합니다
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DdlTester {

    @PersistenceContext
    private EntityManager entityManager;
    
    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    @Transactional
    public void testDdl() {
        try {
            log.info("=== DDL 테스트 시작 ===");
            
            // 1. 직접 JDBC로 테이블 존재 확인
            try {
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM companies", Integer.class);
                log.info("✅ companies 테이블이 존재합니다 (JDBC 확인)");
            } catch (Exception e) {
                log.error("❌ companies 테이블이 존재하지 않습니다 (JDBC 확인): {}", e.getMessage());
            }
            
            // 2. Hibernate로 테이블 존재 확인
            try {
                Long count = entityManager.createQuery("SELECT COUNT(c) FROM Company c", Long.class)
                        .getSingleResult();
                log.info("✅ Hibernate로 companies 테이블 조회 성공: {} 개의 레코드", count);
            } catch (Exception e) {
                log.error("❌ Hibernate로 companies 테이블 조회 실패: {}", e.getMessage());
            }
            
            // 3. 다른 테이블들도 확인
            String[] tables = {
                "common_departments", "users", "employees", "products", "accounts", 
                "product_categories", "positions", "customers", "orders", 
                "inventories", "warehouses", "stock_movements"
            };
            for (String table : tables) {
                try {
                    jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
                    log.info("✅ {} 테이블이 존재합니다", table);
                } catch (Exception e) {
                    log.error("❌ {} 테이블이 존재하지 않습니다: {}", table, e.getMessage());
                }
            }
            
            log.info("=== DDL 테스트 완료 ===");
            
        } catch (Exception e) {
            log.error("DDL 테스트 중 예외 발생: {}", e.getMessage(), e);
        }
    }
}
