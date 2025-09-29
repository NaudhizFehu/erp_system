package com.erp.common.exception;

import com.erp.common.constants.ErrorCode;
import com.erp.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 전역 예외 처리기
 * 애플리케이션에서 발생하는 모든 예외를 중앙에서 처리합니다
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        log.warn("Business exception occurred: {} (Code: {})", ex.getMessage(), ex.getErrorCodeString());
        
        String path = getRequestPath(request);
        ApiResponse<Object> response = ApiResponse.error(ex.getErrorCode(), path, ex.getData());
        
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    /**
     * 검증 예외 처리
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        log.warn("Validation exception occurred: {} (Code: {})", ex.getMessage(), ex.getErrorCodeString());
        
        String path = getRequestPath(request);
        Map<String, Object> details = new HashMap<>();
        if (ex.hasFieldErrors()) {
            details.put("fieldErrors", ex.getFieldErrors());
        }
        
        ApiResponse<Object> response = ApiResponse.error(ex.getErrorCode(), path, details);
        
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    /**
     * 엔티티 조회 실패 예외 처리
     */
    @ExceptionHandler(com.erp.common.exception.EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEntityNotFoundException(
            com.erp.common.exception.EntityNotFoundException ex, WebRequest request) {
        
        log.warn("Entity not found: {} (Code: {})", ex.getMessage(), ex.getErrorCodeString());
        
        String path = getRequestPath(request);
        Map<String, Object> details = new HashMap<>();
        if (ex.getEntityName() != null) {
            details.put("entityName", ex.getEntityName());
            details.put("entityId", ex.getEntityId());
        }
        
        ApiResponse<Object> response = ApiResponse.error(ex.getErrorCode(), path, details);
        
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    /**
     * 중복 데이터 예외 처리
     */
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateException(
            DuplicateException ex, WebRequest request) {
        
        log.warn("Duplicate data exception: {} (Code: {})", ex.getMessage(), ex.getErrorCodeString());
        
        String path = getRequestPath(request);
        Map<String, Object> details = new HashMap<>();
        if (ex.getFieldName() != null) {
            details.put("fieldName", ex.getFieldName());
            details.put("fieldValue", ex.getFieldValue());
        }
        
        ApiResponse<Object> response = ApiResponse.error(ex.getErrorCode(), path, details);
        
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    /**
     * 인증 예외 처리 (커스텀)
     */
    @ExceptionHandler(com.erp.common.exception.AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            com.erp.common.exception.AuthenticationException ex, WebRequest request) {
        
        log.warn("Authentication exception: {} (Code: {}, Username: {})", 
            ex.getMessage(), ex.getErrorCodeString(), ex.getUsername());
        
        String path = getRequestPath(request);
        Map<String, Object> details = new HashMap<>();
        if (ex.getUsername() != null) {
            details.put("username", ex.getUsername());
        }
        
        ApiResponse<Object> response = ApiResponse.error(ex.getErrorCode(), path, details);
        
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    /**
     * 인가 예외 처리 (커스텀)
     */
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorizationException(
            AuthorizationException ex, WebRequest request) {
        
        log.warn("Authorization exception: {} (Code: {}, Required: {}, Resource: {})", 
            ex.getMessage(), ex.getErrorCodeString(), ex.getRequiredPermission(), ex.getResource());
        
        String path = getRequestPath(request);
        Map<String, Object> details = new HashMap<>();
        if (ex.getRequiredPermission() != null) {
            details.put("requiredPermission", ex.getRequiredPermission());
        }
        if (ex.getResource() != null) {
            details.put("resource", ex.getResource());
        }
        
        ApiResponse<Object> response = ApiResponse.error(ex.getErrorCode(), path, details);
        
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    /**
     * Spring Security 인증 예외 처리
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleSpringAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        log.warn("Spring Security authentication exception: {}", ex.getMessage());
        
        String path = getRequestPath(request);
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.UNAUTHORIZED, path);
        
        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getHttpStatus()).body(response);
    }

    /**
     * Spring Security 인가 예외 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        log.warn("Access denied exception: {}", ex.getMessage());
        
        String path = getRequestPath(request);
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.ACCESS_DENIED, path);
        
        return ResponseEntity.status(ErrorCode.ACCESS_DENIED.getHttpStatus()).body(response);
    }

    /**
     * JPA EntityNotFoundException 처리
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleJpaEntityNotFoundException(
            EntityNotFoundException ex, WebRequest request) {
        
        log.warn("JPA Entity not found exception: {}", ex.getMessage());
        
        String path = getRequestPath(request);
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.ENTITY_NOT_FOUND, path);
        
        return ResponseEntity.status(ErrorCode.ENTITY_NOT_FOUND.getHttpStatus()).body(response);
    }

    /**
     * 메소드 인수 검증 예외 처리 (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.error("Method argument validation failed: {}", ex.getMessage());
        log.error("Validation errors count: {}", ex.getBindingResult().getFieldErrors().size());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            log.error("Field validation error - Field: {}, RejectedValue: {}, Message: {}", 
                    error.getField(), error.getRejectedValue(), error.getDefaultMessage());
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });
        
        String path = getRequestPath(request);
        log.error("Request path: {}", path);
        
        ApiResponse<Object> response = ApiResponse.validationError(
            "입력값 검증에 실패했습니다", 
            fieldErrors
        );
        response.setPath(path);
        
        return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getHttpStatus()).body(response);
    }

    /**
     * 바인딩 예외 처리
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindException(
            BindException ex, WebRequest request) {
        
        log.warn("Bind exception occurred: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });
        
        String path = getRequestPath(request);
        ApiResponse<Object> response = ApiResponse.validationError(
            "데이터 바인딩에 실패했습니다", 
            fieldErrors
        );
        response.setPath(path);
        
        return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getHttpStatus()).body(response);
    }

    /**
     * 제약조건 위반 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        log.warn("Constraint violation exception: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            fieldErrors.put(fieldName, message);
        }
        
        String path = getRequestPath(request);
        ApiResponse<Object> response = ApiResponse.validationError(
            "제약조건 위반이 발생했습니다", 
            fieldErrors
        );
        response.setPath(path);
        
        return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getHttpStatus()).body(response);
    }

    /**
     * 메소드 인수 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        log.warn("Method argument type mismatch: {}", ex.getMessage());
        
        String path = getRequestPath(request);
        Map<String, Object> details = new HashMap<>();
        details.put("parameter", ex.getName());
        details.put("value", ex.getValue());
        details.put("requiredType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.INVALID_TYPE_VALUE, path, details);
        
        return ResponseEntity.status(ErrorCode.INVALID_TYPE_VALUE.getHttpStatus()).body(response);
    }

    /**
     * 요청 파라미터 누락 예외 처리
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, WebRequest request) {
        
        log.warn("Missing request parameter: {}", ex.getMessage());
        
        String path = getRequestPath(request);
        Map<String, Object> details = new HashMap<>();
        details.put("parameter", ex.getParameterName());
        details.put("type", ex.getParameterType());
        
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.MISSING_REQUEST_PARAMETER, path, details);
        
        return ResponseEntity.status(ErrorCode.MISSING_REQUEST_PARAMETER.getHttpStatus()).body(response);
    }

    /**
     * HTTP 메소드 지원하지 않음 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        
        log.warn("HTTP method not supported: {}", ex.getMessage());
        
        String path = getRequestPath(request);
        Map<String, Object> details = new HashMap<>();
        details.put("method", ex.getMethod());
        details.put("supportedMethods", ex.getSupportedMethods());
        
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.METHOD_NOT_ALLOWED, path, details);
        
        return ResponseEntity.status(ErrorCode.METHOD_NOT_ALLOWED.getHttpStatus()).body(response);
    }

    /**
     * HTTP 메시지 읽기 불가 예외 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        log.warn("HTTP message not readable: {}", ex.getMessage());
        
        String path = getRequestPath(request);
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.INVALID_JSON_FORMAT, path);
        
        return ResponseEntity.status(ErrorCode.INVALID_JSON_FORMAT.getHttpStatus()).body(response);
    }

    /**
     * 데이터 무결성 위반 예외 처리
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        
        log.warn("Data integrity violation: {}", ex.getMessage());
        
        String path = getRequestPath(request);
        String message = "데이터 무결성 제약조건을 위반했습니다";
        
        // 특정 제약조건에 따른 메시지 커스터마이징
        String rootCauseMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "";
        if (rootCauseMessage.contains("unique") || rootCauseMessage.contains("duplicate")) {
            message = "중복된 데이터가 존재합니다";
        } else if (rootCauseMessage.contains("foreign key")) {
            message = "참조 무결성 제약조건을 위반했습니다";
        }
        
        ApiResponse<Object> response = ApiResponse.error(message, "DATA_INTEGRITY_VIOLATION");
        response.setPath(path);
        response.setTimestamp(java.time.LocalDateTime.now());
        response.setStatus(400);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 데이터 액세스 예외 처리
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataAccessException(
            DataAccessException ex, WebRequest request) {
        
        log.error("Data access exception occurred", ex);
        
        String path = getRequestPath(request);
        ApiResponse<Object> response = ApiResponse.error(
            "데이터베이스 처리 중 오류가 발생했습니다", 
            "DATA_ACCESS_ERROR"
        );
        response.setPath(path);
        response.setTimestamp(java.time.LocalDateTime.now());
        response.setStatus(500);
        
        return ResponseEntity.internalServerError().body(response);
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        log.warn("Illegal argument exception: {}", ex.getMessage());
        
        String path = getRequestPath(request);
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE, path);
        
        return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getHttpStatus()).body(response);
    }

    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(
            Exception ex, WebRequest request) {
        
        log.error("Unexpected exception occurred", ex);
        
        String path = getRequestPath(request);
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, path);
        
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
    }

    /**
     * 런타임 예외 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        log.error("Runtime exception occurred", ex);
        
        String path = getRequestPath(request);
        ApiResponse<Object> response = ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, path);
        
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
    }

    /**
     * 요청 경로 추출
     */
    private String getRequestPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}