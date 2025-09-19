package com.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * ERP 시스템 메인 애플리케이션 클래스
 * Spring Boot 애플리케이션의 진입점입니다
 * WAR 배포를 위해 SpringBootServletInitializer를 상속합니다
 */
@SpringBootApplication
@EnableTransactionManagement
@EntityScan(basePackages = "com.erp")
@EnableJpaRepositories(basePackages = "com.erp")
public class ErpSystemApplication extends SpringBootServletInitializer {

    /**
     * WAR 배포를 위한 configure 메서드
     * 외부 톰캣에서 실행될 때 사용됩니다
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ErpSystemApplication.class);
    }

    /**
     * JAR 실행을 위한 main 메서드
     * 개발 환경에서 직접 실행할 때 사용됩니다
     */
    public static void main(String[] args) {
        SpringApplication.run(ErpSystemApplication.class, args);
    }
}

