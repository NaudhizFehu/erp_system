package com.erp.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 인증 진입점
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출됩니다
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 인증 실패 시 호출되는 메소드
     * 401 Unauthorized 응답을 반환합니다
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authException 인증 예외
     * @throws IOException 입출력 예외
     */
    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        log.warn("인증되지 않은 접근 시도: {} {} - {}", method, requestPath, authException.getMessage());
        
        // 응답 헤더 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // CORS 헤더 추가 (필요한 경우)
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        
        // 에러 응답 본문 생성
        Map<String, Object> errorResponse = createErrorResponse(request, authException);
        
        // JSON 응답 작성
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }

    /**
     * 에러 응답 객체 생성
     * 
     * @param request HTTP 요청
     * @param authException 인증 예외
     * @return 에러 응답 맵
     */
    private Map<String, Object> createErrorResponse(HttpServletRequest request, 
                                                   AuthenticationException authException) {
        
        Map<String, Object> errorResponse = new HashMap<>();
        
        // 기본 응답 정보
        errorResponse.put("success", false);
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("errorCode", "UNAUTHORIZED");
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("path", request.getRequestURI());
        
        // 요청 정보 추가
        Map<String, Object> requestInfo = new HashMap<>();
        requestInfo.put("method", request.getMethod());
        requestInfo.put("userAgent", request.getHeader("User-Agent"));
        requestInfo.put("remoteAddr", getClientIpAddress(request));
        errorResponse.put("request", requestInfo);
        
        // 에러 메시지 결정
        String message = determineErrorMessage(request, authException);
        errorResponse.put("message", message);
        
        // 추가 정보 (개발 환경에서만)
        if (isDevelopmentEnvironment()) {
            errorResponse.put("exception", authException.getClass().getSimpleName());
            errorResponse.put("exceptionMessage", authException.getMessage());
        }
        
        return errorResponse;
    }

    /**
     * 요청과 예외 정보를 바탕으로 적절한 에러 메시지 결정
     * 
     * @param request HTTP 요청
     * @param authException 인증 예외
     * @return 에러 메시지
     */
    private String determineErrorMessage(HttpServletRequest request, 
                                       AuthenticationException authException) {
        
        String authHeader = request.getHeader("Authorization");
        String requestPath = request.getRequestURI();
        
        // Authorization 헤더가 없는 경우
        if (authHeader == null || authHeader.trim().isEmpty()) {
            if (isApiRequest(requestPath)) {
                return "인증 토큰이 필요합니다. Authorization 헤더에 Bearer 토큰을 포함해주세요.";
            } else {
                return "로그인이 필요합니다.";
            }
        }
        
        // Bearer 토큰 형식이 아닌 경우
        if (!authHeader.startsWith("Bearer ")) {
            return "올바르지 않은 인증 토큰 형식입니다. 'Bearer {토큰}' 형식으로 전송해주세요.";
        }
        
        // 토큰은 있지만 유효하지 않은 경우
        String exceptionMessage = authException.getMessage();
        if (exceptionMessage != null) {
            if (exceptionMessage.contains("expired")) {
                return "인증 토큰이 만료되었습니다. 다시 로그인해주세요.";
            } else if (exceptionMessage.contains("invalid")) {
                return "유효하지 않은 인증 토큰입니다.";
            } else if (exceptionMessage.contains("malformed")) {
                return "손상된 인증 토큰입니다.";
            }
        }
        
        // 기본 메시지
        return "인증에 실패했습니다. 다시 로그인해주세요.";
    }

    /**
     * API 요청인지 확인
     * 
     * @param requestPath 요청 경로
     * @return API 요청 여부
     */
    private boolean isApiRequest(String requestPath) {
        return requestPath != null && requestPath.startsWith("/api/");
    }

    /**
     * 개발 환경인지 확인
     * 
     * @return 개발 환경 여부
     */
    private boolean isDevelopmentEnvironment() {
        String profile = System.getProperty("spring.profiles.active");
        return "dev".equals(profile) || "development".equals(profile) || "local".equals(profile);
    }

    /**
     * 클라이언트 IP 주소 추출
     * 프록시나 로드밸런서를 고려하여 실제 클라이언트 IP를 찾습니다
     * 
     * @param request HTTP 요청
     * @return 클라이언트 IP 주소
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP", 
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For 헤더는 여러 IP가 콤마로 구분될 수 있음
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 보안 헤더 추가
     * 
     * @param response HTTP 응답
     */
    private void addSecurityHeaders(HttpServletResponse response) {
        // XSS 보호
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        
        // HSTS (HTTPS 환경에서만)
        if (isHttpsEnvironment()) {
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }
        
        // 캐시 방지 (인증 오류 응답)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    /**
     * HTTPS 환경인지 확인
     * 
     * @return HTTPS 환경 여부
     */
    private boolean isHttpsEnvironment() {
        String protocol = System.getProperty("server.ssl.enabled");
        return "true".equals(protocol);
    }
}




