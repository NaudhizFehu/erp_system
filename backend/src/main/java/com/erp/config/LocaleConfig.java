package com.erp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Arrays;
import java.util.Locale;

/**
 * 다국어 지원 설정
 * AcceptHeaderLocaleResolver를 사용하여 Accept-Language 헤더 기반으로 로케일을 결정합니다
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    /**
     * 로케일 리졸버 설정
     * 기본 로케일을 한국어(ko-KR)로 설정하고, 지원하는 로케일을 정의합니다
     * 
     * @return AcceptHeaderLocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        
        // 기본 로케일을 한국어로 설정
        resolver.setDefaultLocale(Locale.KOREA);
        
        // 지원하는 로케일 목록
        resolver.setSupportedLocales(Arrays.asList(
            Locale.KOREA,    // ko-KR
            Locale.US,       // en-US
            Locale.JAPAN     // ja-JP
        ));
        
        return resolver;
    }

    /**
     * 로케일 변경 인터셉터
     * URL 파라미터를 통해 로케일을 변경할 수 있도록 합니다
     * 
     * @return LocaleChangeInterceptor
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    /**
     * 인터셉터 등록
     *
     * @param registry InterceptorRegistry
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
