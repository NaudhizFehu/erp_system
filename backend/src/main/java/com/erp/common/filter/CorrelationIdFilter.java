package com.erp.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Correlation ID 필터
 * 요청별로 고유한 ID를 생성하여 로그 추적을 용이하게 합니다
 * X-Correlation-Id 헤더가 있으면 사용하고, 없으면 새로 생성합니다
 */
@Slf4j
@Component
@Order(1)
public class CorrelationIdFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_MDC_KEY = "cid";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("CorrelationIdFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            // Correlation ID 추출 또는 생성
            String correlationId = extractOrGenerateCorrelationId(httpRequest);
            
            // MDC에 Correlation ID 설정
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            
            // 응답 헤더에 Correlation ID 추가
            httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);
            
            log.debug("Processing request with correlation ID: {}", correlationId);
            
            // 다음 필터로 전달
            chain.doFilter(request, response);
            
        } finally {
            // 요청 처리 완료 후 MDC에서 Correlation ID 제거
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }

    @Override
    public void destroy() {
        log.info("CorrelationIdFilter destroyed");
    }

    /**
     * 요청 헤더에서 Correlation ID를 추출하거나 새로 생성합니다
     * 
     * @param request HTTP 요청
     * @return Correlation ID
     */
    private String extractOrGenerateCorrelationId(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(CORRELATION_ID_HEADER))
                .filter(id -> !id.trim().isEmpty())
                .orElse(generateCorrelationId());
    }

    /**
     * 새로운 Correlation ID를 생성합니다
     * 
     * @return 새로 생성된 Correlation ID
     */
    private String generateCorrelationId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}

