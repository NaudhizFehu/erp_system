package com.erp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 웹 설정 클래스
 * CORS, 정적 리소스 등 웹 관련 설정을 구성합니다
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${erp.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://127.0.0.1:3000,http://127.0.0.1:5173}")
    private List<String> allowedOrigins;

    @Value("${erp.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private List<String> allowedMethods;

    @Value("${erp.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${erp.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${erp.file.upload-path:./uploads}")
    private String uploadPath;

    /**
     * CORS 설정
     * 프론트엔드에서 백엔드 API 호출을 위한 CORS 정책을 설정합니다
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.toArray(new String[0]))
                .allowedMethods(allowedMethods.toArray(new String[0]))
                .allowedHeaders(allowedHeaders)
                .allowCredentials(allowCredentials)
                .maxAge(3600); // 1시간
    }

    /**
     * 정적 리소스 핸들러 설정
     * 파일 업로드 경로를 정적 리소스로 제공합니다
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}

