package com.erp.common.exception;

import com.erp.common.constants.ErrorCode;
import lombok.Getter;

/**
 * 엔티티 조회 실패 예외 클래스
 * 데이터베이스에서 요청한 엔티티를 찾을 수 없을 때 사용됩니다
 */
@Getter
public class EntityNotFoundException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final String entityName;
    private final Object entityId;
    
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.entityName = null;
        this.entityId = null;
    }
    
    public EntityNotFoundException(ErrorCode errorCode, String entityName, Object entityId) {
        super(String.format("%s (엔티티: %s, ID: %s)", errorCode.getMessage(), entityName, entityId));
        this.errorCode = errorCode;
        this.entityName = entityName;
        this.entityId = entityId;
    }
    
    public EntityNotFoundException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.entityName = null;
        this.entityId = null;
    }
    
    /**
     * 기본 엔티티 조회 실패 예외 생성
     */
    public static EntityNotFoundException of(String entityName, Object entityId) {
        return new EntityNotFoundException(
            ErrorCode.ENTITY_NOT_FOUND, 
            entityName, 
            entityId
        );
    }
    
    /**
     * 사용자 조회 실패 예외 생성
     */
    public static EntityNotFoundException userNotFound(Object userId) {
        return new EntityNotFoundException(
            ErrorCode.USER_NOT_FOUND,
            "User",
            userId
        );
    }
    
    /**
     * 회사 조회 실패 예외 생성
     */
    public static EntityNotFoundException companyNotFound(Object companyId) {
        return new EntityNotFoundException(
            ErrorCode.COMPANY_NOT_FOUND,
            "Company",
            companyId
        );
    }
    
    /**
     * 부서 조회 실패 예외 생성
     */
    public static EntityNotFoundException departmentNotFound(Object departmentId) {
        return new EntityNotFoundException(
            ErrorCode.DEPARTMENT_NOT_FOUND,
            "Department",
            departmentId
        );
    }
    
    /**
     * 공통코드 조회 실패 예외 생성
     */
    public static EntityNotFoundException codeNotFound(String groupCode, String codeValue) {
        return new EntityNotFoundException(
            ErrorCode.CODE_NOT_FOUND,
            "Code",
            String.format("%s.%s", groupCode, codeValue)
        );
    }
    
    /**
     * 직원 조회 실패 예외 생성
     */
    public static EntityNotFoundException employeeNotFound(Object employeeId) {
        return new EntityNotFoundException(
            ErrorCode.EMPLOYEE_NOT_FOUND,
            "Employee",
            employeeId
        );
    }
    
    /**
     * 제품 조회 실패 예외 생성
     */
    public static EntityNotFoundException productNotFound(Object productId) {
        return new EntityNotFoundException(
            ErrorCode.PRODUCT_NOT_FOUND,
            "Product",
            productId
        );
    }
    
    /**
     * 고객 조회 실패 예외 생성
     */
    public static EntityNotFoundException customerNotFound(Object customerId) {
        return new EntityNotFoundException(
            ErrorCode.CUSTOMER_NOT_FOUND,
            "Customer",
            customerId
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




