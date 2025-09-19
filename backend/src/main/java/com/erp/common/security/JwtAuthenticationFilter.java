package com.erp.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 * HTTP 요청에서 JWT 토큰을 추출하고 검증하여 Spring Security Context에 인증 정보를 설정합니다
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    /**
     * JWT 토큰 인증 필터링 로직
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException 입출력 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        log.debug("JWT 인증 필터 실행: {} {}", request.getMethod(), requestPath);
        
        try {
            // JWT 토큰 추출
            String jwt = parseJwt(request);
            
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // 토큰에서 사용자 ID 추출
                Long userId = jwtUtils.getUserIdFromJwtToken(jwt);
                
                // UserDetails 로드
                UserDetails userDetails = userDetailsService.loadUserById(userId);
                
                // 추가 토큰 검증 (비밀번호 변경 후 토큰 유효성 검사)
                if (userDetails instanceof UserPrincipal) {
                    UserPrincipal userPrincipal = (UserPrincipal) userDetails;
                    
                    // 사용자 정보가 변경된 후 발급된 토큰인지 확인
                    if (!isTokenValidForUser(jwt, userPrincipal)) {
                        log.warn("사용자 정보 변경으로 인한 토큰 무효화: userId={}", userId);
                        setUnauthorizedResponse(response, "토큰이 무효화되었습니다. 다시 로그인해주세요.");
                        return;
                    }
                }
                
                // Spring Security Context에 인증 정보 설정
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("JWT 인증 성공: userId={}, username={}", 
                    userId, userDetails.getUsername());
                
                // 마지막 로그인 시간 업데이트 (비동기로 처리하여 성능 영향 최소화)
                updateLastLoginTimeAsync(userId);
                
            } else if (jwt != null) {
                // 토큰이 있지만 유효하지 않은 경우
                log.warn("유효하지 않은 JWT 토큰: {}", requestPath);
                setUnauthorizedResponse(response, "유효하지 않은 토큰입니다.");
                return;
            }
            
        } catch (Exception e) {
            log.error("JWT 인증 처리 중 오류 발생: {}", requestPath, e);
            setUnauthorizedResponse(response, "인증 처리 중 오류가 발생했습니다.");
            return;
        }
        
        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     * 
     * @param request HTTP 요청
     * @return JWT 토큰 문자열 (없으면 null)
     */
    private String parseJwt(HttpServletRequest request) {
        // Authorization 헤더에서 토큰 추출
        String headerAuth = request.getHeader("Authorization");
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);
            log.debug("Authorization 헤더에서 JWT 토큰 추출 성공");
            return token;
        }
        
        // 쿠키에서 토큰 추출 (옵션)
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    log.debug("쿠키에서 JWT 토큰 추출 성공");
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }

    /**
     * 사용자에 대한 토큰 유효성 추가 검증
     * 
     * @param jwt JWT 토큰
     * @param userPrincipal 사용자 주체
     * @return 토큰 유효성 여부
     */
    private boolean isTokenValidForUser(String jwt, UserPrincipal userPrincipal) {
        try {
            // 비밀번호 변경 시간과 토큰 발급 시간 비교
            if (userPrincipal.getUser().getPasswordChangedAt() != null) {
                return jwtUtils.isTokenValidAfterPasswordChange(
                    jwt, 
                    java.sql.Timestamp.valueOf(userPrincipal.getUser().getPasswordChangedAt())
                );
            }
            
            // 계정 상태 재확인
            return userPrincipal.isEnabled() && 
                   userPrincipal.isAccountNonLocked() && 
                   userPrincipal.isAccountNonExpired();
                   
        } catch (Exception e) {
            log.error("토큰 유효성 검증 중 오류 발생", e);
            return false;
        }
    }

    /**
     * 마지막 로그인 시간 비동기 업데이트
     * 
     * @param userId 사용자 ID
     */
    private void updateLastLoginTimeAsync(Long userId) {
        // 별도 스레드에서 실행하여 메인 요청 처리에 영향을 주지 않음
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                userDetailsService.updateLastLoginTime(userId);
            } catch (Exception e) {
                log.warn("마지막 로그인 시간 업데이트 실패: userId={}", userId, e);
            }
        });
    }

    /**
     * 인증 실패 응답 설정
     * 
     * @param response HTTP 응답
     * @param message 오류 메시지
     * @throws IOException 입출력 예외
     */
    private void setUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        String jsonResponse = String.format(
            "{\"success\":false,\"message\":\"%s\",\"errorCode\":\"UNAUTHORIZED\",\"status\":401,\"timestamp\":\"%s\"}", 
            message, 
            java.time.LocalDateTime.now().toString()
        );
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    /**
     * 특정 경로에 대해 필터를 적용하지 않을지 결정
     * 
     * @param request HTTP 요청
     * @return 필터 적용 제외 여부
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // 인증이 필요없는 경로들
        String[] publicPaths = {
            "/api/auth/login",
            "/api/auth/register", 
            "/api/auth/refresh",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/public/",
            "/actuator/health",
            "/swagger-ui/",
            "/v3/api-docs",
            "/favicon.ico"
        };
        
        // /api/auth/me는 토큰이 있을 때만 처리, 없으면 401 반환
        if ("/api/auth/me".equals(path)) {
            return false; // 필터 적용
        }
        
        for (String publicPath : publicPaths) {
            if (path.startsWith(publicPath)) {
                log.debug("공개 경로로 JWT 필터 제외: {}", path);
                return true;
            }
        }
        
        // OPTIONS 요청은 CORS preflight이므로 제외
        if ("OPTIONS".equals(method)) {
            log.debug("OPTIONS 요청으로 JWT 필터 제외: {}", path);
            return true;
        }
        
        return false;
    }

    /**
     * 현재 인증된 사용자의 UserPrincipal 반환
     * 
     * @return 현재 사용자의 UserPrincipal (인증되지 않은 경우 null)
     */
    public static UserPrincipal getCurrentUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserPrincipal) {
                return (UserPrincipal) principal;
            }
        } catch (Exception e) {
            log.debug("현재 사용자 정보 조회 실패", e);
        }
        return null;
    }

    /**
     * 현재 인증된 사용자 ID 반환
     * 
     * @return 현재 사용자 ID (인증되지 않은 경우 null)
     */
    public static Long getCurrentUserId() {
        UserPrincipal currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getId() : null;
    }

    /**
     * 현재 인증된 사용자의 회사 ID 반환
     * 
     * @return 현재 사용자의 회사 ID (인증되지 않은 경우 null)
     */
    public static Long getCurrentCompanyId() {
        UserPrincipal currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getCompanyId() : null;
    }

    /**
     * 현재 인증된 사용자의 부서 ID 반환
     * 
     * @return 현재 사용자의 부서 ID (인증되지 않은 경우 null)
     */
    public static Long getCurrentDepartmentId() {
        UserPrincipal currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getDepartmentId() : null;
    }
}




