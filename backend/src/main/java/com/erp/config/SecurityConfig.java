package com.erp.config;

import com.erp.common.security.CustomUserDetailsService;
import com.erp.common.security.JwtAuthenticationEntryPoint;
import com.erp.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 보안 설정
 * JWT 기반 인증, 역할 기반 권한 관리, CORS 설정을 포함합니다
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 비밀번호 암호화 Bean
     * BCrypt 알고리즘을 사용하여 비밀번호를 암호화합니다
     * 
     * @return BCrypt 패스워드 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // strength 12로 설정 (보안 강화)
    }

    /**
     * 인증 제공자 설정
     * 사용자 정보와 비밀번호 검증을 담당합니다
     * 
     * @return DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false); // 사용자 없음 예외 노출 (개발용)
        return authProvider;
    }

    /**
     * 인증 매니저 Bean
     * 
     * @param authConfig 인증 설정
     * @return AuthenticationManager
     * @throws Exception 설정 예외
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * CORS 설정
     * Cross-Origin 요청을 허용하기 위한 설정입니다
     * 
     * @return CORS 설정 소스
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 Origin 설정 (개발/운영 환경별로 설정)
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",    // React 개발 서버
            "http://localhost:5173",    // Vite 개발 서버
            "https://erp.company.com",  // 운영 도메인
            "https://*.company.com"     // 서브도메인
        ));
        
        // 허용할 HTTP 메소드
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));
        
        // 허용할 헤더
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With",
            "Accept",
            "Origin",
            "Cache-Control",
            "X-File-Name",
            "X-CSRF-TOKEN"
        ));
        
        // 노출할 헤더 (클라이언트가 접근 가능한 헤더)
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "X-Total-Count",
            "X-Page-Number",
            "X-Page-Size"
        ));
        
        // 자격증명(쿠키, 인증헤더 등) 허용
        configuration.setAllowCredentials(true);
        
        // preflight 요청 캐시 시간 (초)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * HTTP 보안 설정
     * 주요 보안 필터체인을 구성합니다
     * 
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 설정 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS 설정 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // CSRF 비활성화 (JWT 사용으로 불필요)
            .csrf(AbstractHttpConfigurer::disable)
            
            // 보안 헤더 설정
            .headers(headers -> headers
                .xssProtection(xss -> xss.disable()) // Spring Security 6.x에서는 block() 대신 disable() 사용
                .contentTypeOptions(contentTypeOptions -> {})
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .preload(true)
                    .maxAgeInSeconds(31536000) // 365일을 초 단위로 변환
                )
            )
            
            // 세션 관리 정책 (Stateless)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 인증 진입점 설정
            .exceptionHandling(exceptions -> 
                exceptions.authenticationEntryPoint(unauthorizedHandler)
            )
            
            // 요청별 권한 설정
            .authorizeHttpRequests(authz -> authz
                // 공개 엔드포인트 (인증 불필요)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Actuator 엔드포인트
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // API 문서화 엔드포인트
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/favicon.ico", "/error").permitAll()
                
                // 관리자 전용 엔드포인트
                .requestMatchers("/api/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER")
                
                // 사용자 관리 엔드포인트
                .requestMatchers(HttpMethod.POST, "/api/users").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                .requestMatchers(HttpMethod.PUT, "/api/auth/profile").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                
                // 회사 관리 엔드포인트
                .requestMatchers("/api/companies/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                
                // 부서 관리 엔드포인트
                .requestMatchers("/api/departments/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                
                // 공통코드 관리 엔드포인트
                .requestMatchers(HttpMethod.POST, "/api/codes").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/codes/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/codes/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                
                // 인사관리 모듈
                .requestMatchers("/api/hr/employees/*/salary").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER")
                .requestMatchers("/api/hr/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                
                // 재고관리 모듈 (상품 조회는 공개, 나머지는 인증 필요)
                .requestMatchers(HttpMethod.POST, "/api/inventory/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/inventory/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER")
                .requestMatchers("/api/inventory/warehouses/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                .requestMatchers("/api/inventory/stock/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                .requestMatchers("/api/inventory/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                
                // 상품 관리 모듈
                .requestMatchers("/api/products/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                
                // 인사관리 모듈
                .requestMatchers("/api/hr/employees/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                .requestMatchers("/api/employees/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                
                // 영업관리 모듈
                .requestMatchers("/api/sales/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                
                // 회계관리 모듈
                .requestMatchers("/api/accounting/reports/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER")
                .requestMatchers("/api/accounting/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                
                // 파일 업로드/다운로드
                .requestMatchers("/api/files/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                
                // 전역 검색 API
                .requestMatchers("/api/search/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "USER")
                
                // 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // 인증 제공자 설정
            .authenticationProvider(authenticationProvider())
            
            // JWT 필터 추가
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /**
     * 메소드 레벨 보안을 위한 표현식 핸들러 설정
     */
    /*
    @Bean
    public DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return expressionHandler;
    }
    */

    /**
     * 비밀번호 정책 검증기
     * 강력한 비밀번호 정책을 적용합니다
     */
    /*
    @Bean
    public PasswordValidator passwordValidator() {
        return new PasswordValidator(Arrays.asList(
            new LengthRule(8, 50),
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1),
            new CharacterRule(EnglishCharacterData.Special, 1),
            new WhitespaceRule(),
            new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 3, false),
            new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false),
            new IllegalSequenceRule(EnglishSequenceData.USQwerty, 3, false),
            new DictionaryRule(new WordListDictionary(WordLists.createFromReader(
                new FileReader("src/main/resources/security/dictionary.txt")
            )))
        ));
    }
    */
}
