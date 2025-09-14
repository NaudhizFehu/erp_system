package com.erp.common.exception;

import com.erp.common.constants.ErrorCode;
import lombok.Getter;

/**
 * 비즈니스 로직 예외 클래스
 * 업무 규칙 위반이나 비즈니스 로직 오류 시 사용됩니다
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final Object data;
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = null;
    }
    
    public BusinessException(ErrorCode errorCode, Object data) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.data = data;
    }
    
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.data = null;
    }
    
    public BusinessException(ErrorCode errorCode, String customMessage, Object data) {
        super(customMessage);
        this.errorCode = errorCode;
        this.data = data;
    }
    
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.data = null;
    }
    
    public BusinessException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
        this.data = null;
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

