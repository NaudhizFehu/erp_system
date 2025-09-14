package com.erp.common.security;

import com.erp.common.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 유틸리티 클래스
 * JWT 토큰의 생성, 검증, 파싱 기능을 제공합니다
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * JWT 비밀키 (application.yml에서 설정)
     */
    @Value("${app.jwt.secret:defaultSecretKeyForJwtTokenGenerationThatShouldBeAtLeast256BitsLong}")
    private String jwtSecret;

    /**
     * JWT 토큰 만료 시간 (밀리초, 기본값: 24시간)
     */
    @Value("${app.jwt.expiration-ms:86400000}")
    private int jwtExpirationMs;

    /**
     * Refresh 토큰 만료 시간 (밀리초, 기본값: 7일)
     */
    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private int refreshTokenExpirationMs;

    /**
     * JWT 토큰 생성
     * 
     * @param authentication Spring Security Authentication 객체
     * @return JWT 토큰 문자열
     */
    public String generateJwtToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateTokenFromUser(userPrincipal.getUser(), jwtExpirationMs);
    }

    /**
     * 사용자 정보로 JWT 토큰 생성
     * 
     * @param user 사용자 엔티티
     * @return JWT 토큰 문자열
     */
    public String generateTokenFromUser(User user) {
        return generateTokenFromUser(user, jwtExpirationMs);
    }

    /**
     * 사용자 정보와 만료시간으로 JWT 토큰 생성
     * 
     * @param user 사용자 엔티티
     * @param expirationMs 만료시간 (밀리초)
     * @return JWT 토큰 문자열
     */
    private String generateTokenFromUser(User user, int expirationMs) {
        Date expiryDate = new Date(System.currentTimeMillis() + expirationMs);
        
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(expiryDate)
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("fullName", user.getFullName())
                .claim("role", user.getRole().name())
                .claim("companyId", user.getCompany() != null ? user.getCompany().getId() : null)
                .claim("departmentId", user.getDepartment() != null ? user.getDepartment().getId() : null)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Refresh 토큰 생성
     * 
     * @param user 사용자 엔티티
     * @return Refresh 토큰 문자열
     */
    public String generateRefreshToken(User user) {
        return generateTokenFromUser(user, refreshTokenExpirationMs);
    }

    /**
     * JWT 토큰에서 사용자명 추출
     * 
     * @param token JWT 토큰
     * @return 사용자명
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserIdFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.get("userId", Long.class);
    }

    /**
     * JWT 토큰에서 사용자 역할 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 역할
     */
    public String getRoleFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.get("role", String.class);
    }

    /**
     * JWT 토큰에서 회사 ID 추출
     * 
     * @param token JWT 토큰
     * @return 회사 ID
     */
    public Long getCompanyIdFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.get("companyId", Long.class);
    }

    /**
     * JWT 토큰에서 부서 ID 추출
     * 
     * @param token JWT 토큰
     * @return 부서 ID
     */
    public Long getDepartmentIdFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.get("departmentId", Long.class);
    }

    /**
     * JWT 토큰의 만료 시간 추출
     * 
     * @param token JWT 토큰
     * @return 만료 시간
     */
    public Date getExpirationDateFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    /**
     * JWT 토큰 유효성 검증
     * 
     * @param authToken JWT 토큰
     * @return 유효성 여부
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (SecurityException e) {
            log.error("잘못된 JWT 서명입니다: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("유효하지 않은 JWT 토큰입니다: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT 토큰이 만료되었습니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 비어있습니다: {}", e.getMessage());
        }
        return false;
    }

    /**
     * JWT 토큰 만료 여부 확인
     * 
     * @param token JWT 토큰
     * @return 만료 여부
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromJwtToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("토큰 만료 확인 중 오류 발생: {}", e.getMessage());
            return true;
        }
    }

    /**
     * JWT 토큰의 남은 유효 시간 계산 (초 단위)
     * 
     * @param token JWT 토큰
     * @return 남은 유효 시간 (초)
     */
    public long getTokenRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromJwtToken(token);
            long remainingTime = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            return Math.max(remainingTime, 0);
        } catch (Exception e) {
            log.error("토큰 남은 시간 계산 중 오류 발생: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Authorization 헤더에서 JWT 토큰 추출
     * 
     * @param authHeader Authorization 헤더 값
     * @return JWT 토큰 (Bearer 제거)
     */
    public String parseJwtFromAuthHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * JWT 토큰 갱신 가능 여부 확인
     * 토큰이 만료되기 1시간 전부터 갱신 가능
     * 
     * @param token JWT 토큰
     * @return 갱신 가능 여부
     */
    public boolean canTokenBeRefreshed(String token) {
        try {
            Date expiration = getExpirationDateFromJwtToken(token);
            long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
            long oneHourInMs = 60 * 60 * 1000; // 1시간
            
            return timeUntilExpiration <= oneHourInMs && timeUntilExpiration > 0;
        } catch (Exception e) {
            log.error("토큰 갱신 가능 여부 확인 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT 서명키 생성
     * 
     * @return 서명키
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT 토큰에서 모든 클레임 추출
     * 
     * @param token JWT 토큰
     * @return 클레임 맵
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 토큰 생성 시간 추출
     * 
     * @param token JWT 토큰
     * @return 생성 시간
     */
    public Date getIssuedAtDateFromToken(String token) {
        return getAllClaimsFromToken(token).getIssuedAt();
    }

    /**
     * 사용자 정보가 변경된 후 토큰이 생성되었는지 확인
     * 
     * @param token JWT 토큰
     * @param lastPasswordReset 마지막 비밀번호 변경 시간
     * @return 토큰 유효성 여부
     */
    public boolean isTokenValidAfterPasswordChange(String token, Date lastPasswordReset) {
        if (lastPasswordReset == null) {
            return true;
        }
        
        try {
            Date tokenIssuedAt = getIssuedAtDateFromToken(token);
            return tokenIssuedAt.after(lastPasswordReset);
        } catch (Exception e) {
            log.error("토큰 유효성 확인 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }
}
