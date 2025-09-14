package com.erp.common.exception;

import com.erp.common.constants.ErrorCode;
import lombok.Getter;

import java.util.Map;

/**
 * 유효성 검증 예외 클래스
 * 입력값 검증 실패 시 사용됩니다
 */
@Getter
public class ValidationException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final Map<String, String> fieldErrors;
    
    public ValidationException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_INPUT_VALUE;
        this.fieldErrors = null;
    }
    
    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.errorCode = ErrorCode.INVALID_INPUT_VALUE;
        this.fieldErrors = fieldErrors;
    }
    
    public ValidationException(ErrorCode errorCode, Map<String, String> fieldErrors) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.fieldErrors = fieldErrors;
    }
    
    public ValidationException(ErrorCode errorCode, String customMessage, Map<String, String> fieldErrors) {
        super(customMessage);
        this.errorCode = errorCode;
        this.fieldErrors = fieldErrors;
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
    
    /**
     * 필드 에러가 있는지 확인
     */
    public boolean hasFieldErrors() {
        return fieldErrors != null && !fieldErrors.isEmpty();
    }
}




