package com.erp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 설정 클래스
 * 정적 리소스 등 웹 관련 설정을 구성합니다
 * (CORS 설정은 SecurityConfig에서 처리)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${erp.file.upload-path:./uploads}")
    private String uploadPath;

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
