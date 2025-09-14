package com.erp.common.exception;

import com.erp.common.constants.ErrorCode;
import lombok.Getter;

/**
 * 인가 예외 클래스
 * 권한 부족이나 접근 거부 시 사용됩니다
 */
@Getter
public class AuthorizationException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final String requiredPermission;
    private final String resource;
    
    public AuthorizationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.requiredPermission = null;
        this.resource = null;
    }
    
    public AuthorizationException(ErrorCode errorCode, String requiredPermission, String resource) {
        super(String.format("%s (필요 권한: %s, 리소스: %s)", 
            errorCode.getMessage(), requiredPermission, resource));
        this.errorCode = errorCode;
        this.requiredPermission = requiredPermission;
        this.resource = resource;
    }
    
    public AuthorizationException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.requiredPermission = null;
        this.resource = null;
    }
    
    /**
     * 접근 거부 예외 생성
     */
    public static AuthorizationException accessDenied() {
        return new AuthorizationException(ErrorCode.ACCESS_DENIED);
    }
    
    /**
     * 접근 거부 예외 생성 (리소스 지정)
     */
    public static AuthorizationException accessDenied(String resource) {
        return new AuthorizationException(
            ErrorCode.ACCESS_DENIED,
            null,
            resource
        );
    }
    
    /**
     * 권한 부족 예외 생성
     */
    public static AuthorizationException insufficientPermission(String requiredPermission, String resource) {
        return new AuthorizationException(
            ErrorCode.INSUFFICIENT_PERMISSION,
            requiredPermission,
            resource
        );
    }
    
    /**
     * 관리자 권한 필요 예외 생성
     */
    public static AuthorizationException adminRequired() {
        return new AuthorizationException(
            ErrorCode.INSUFFICIENT_PERMISSION,
            "ADMIN",
            "관리자 기능"
        );
    }
    
    /**
     * 매니저 권한 필요 예외 생성
     */
    public static AuthorizationException managerRequired() {
        return new AuthorizationException(
            ErrorCode.INSUFFICIENT_PERMISSION,
            "MANAGER",
            "매니저 기능"
        );
    }
    
    /**
     * 회사 내 접근 권한 부족 예외 생성
     */
    public static AuthorizationException companyAccessDenied(String companyName) {
        return new AuthorizationException(
            ErrorCode.ACCESS_DENIED,
            "COMPANY_ACCESS",
            companyName
        );
    }
    
    /**
     * 부서 내 접근 권한 부족 예외 생성
     */
    public static AuthorizationException departmentAccessDenied(String departmentName) {
        return new AuthorizationException(
            ErrorCode.ACCESS_DENIED,
            "DEPARTMENT_ACCESS",
            departmentName
        );
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




