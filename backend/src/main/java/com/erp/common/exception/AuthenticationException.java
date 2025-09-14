package com.erp.common.exception;

import com.erp.common.constants.ErrorCode;
import lombok.Getter;

/**
 * 인증 예외 클래스
 * 로그인, 토큰 검증 등 인증 관련 오류 시 사용됩니다
 */
@Getter
public class AuthenticationException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final String username;
    
    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.username = null;
    }
    
    public AuthenticationException(ErrorCode errorCode, String username) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.username = username;
    }
    
    public AuthenticationException(ErrorCode errorCode, String customMessage, String username) {
        super(customMessage);
        this.errorCode = errorCode;
        this.username = username;
    }
    
    /**
     * 로그인 실패 예외 생성
     */
    public static AuthenticationException loginFailed(String username) {
        return new AuthenticationException(
            ErrorCode.LOGIN_FAILED,
            username
        );
    }
    
    /**
     * 계정 잠금 예외 생성
     */
    public static AuthenticationException accountLocked(String username) {
        return new AuthenticationException(
            ErrorCode.ACCOUNT_LOCKED,
            username
        );
    }
    
    /**
     * 계정 비활성화 예외 생성
     */
    public static AuthenticationException accountInactive(String username) {
        return new AuthenticationException(
            ErrorCode.ACCOUNT_INACTIVE,
            username
        );
    }
    
    /**
     * 계정 만료 예외 생성
     */
    public static AuthenticationException accountExpired(String username) {
        return new AuthenticationException(
            ErrorCode.ACCOUNT_EXPIRED,
            username
        );
    }
    
    /**
     * 비밀번호 만료 예외 생성
     */
    public static AuthenticationException passwordExpired(String username) {
        return new AuthenticationException(
            ErrorCode.PASSWORD_EXPIRED,
            username
        );
    }
    
    /**
     * 토큰 만료 예외 생성
     */
    public static AuthenticationException tokenExpired() {
        return new AuthenticationException(ErrorCode.TOKEN_EXPIRED);
    }
    
    /**
     * 유효하지 않은 토큰 예외 생성
     */
    public static AuthenticationException invalidToken() {
        return new AuthenticationException(ErrorCode.INVALID_TOKEN);
    }
    
    /**
     * 인증 필요 예외 생성
     */
    public static AuthenticationException unauthorized() {
        return new AuthenticationException(ErrorCode.UNAUTHORIZED);
    }
    
    /**
     * 에러 코드 문자열 반환
     */
    public String getErrorCodeString() {
        return errorCode.getCode();
    }
    
    /**
     * HTTP 상태 코드 반환
     */
    public int getHttpStatus() {
        return errorCode.getStatus();
    }
}




