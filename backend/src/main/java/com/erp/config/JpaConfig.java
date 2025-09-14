package com.erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA 설정 클래스
 * JPA Auditing 기능을 활성화하고 감사 정보를 자동으로 설정합니다
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    /**
     * 감사자 정보 제공자
     * 현재 로그인한 사용자의 ID를 반환합니다
     */
    @Bean
    public AuditorAware<Long> auditorProvider() {
        return new AuditorAwareImpl();
    }

    /**
     * 감사자 정보 제공 구현체
     */
    public static class AuditorAwareImpl implements AuditorAware<Long> {

        @Override
        public Optional<Long> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getPrincipal())) {
                // 인증되지 않은 경우 시스템 사용자 ID (1)을 반환
                return Optional.of(1L);
            }
            
            try {
                // 실제 구현에서는 UserDetails에서 사용자 ID를 추출
                // 현재는 임시로 하드코딩된 값 반환
                String username = authentication.getName();
                
                // TODO: 실제 구현시 사용자명으로 사용자 ID 조회
                // UserService를 통해 username으로 User 엔티티를 조회하고 ID 반환
                
                return Optional.of(1L); // 임시값
            } catch (Exception e) {
                // 오류 발생 시 시스템 사용자 ID 반환
                return Optional.of(1L);
            }
        }
    }
}




