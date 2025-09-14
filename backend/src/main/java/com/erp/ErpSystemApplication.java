package com.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * ERP 시스템 메인 애플리케이션 클래스
 * Spring Boot 애플리케이션의 진입점입니다
 */
@SpringBootApplication
@EnableTransactionManagement
@EntityScan(basePackages = "com.erp")
@EnableJpaRepositories(basePackages = "com.erp")
public class ErpSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpSystemApplication.class, args);
    }
}

