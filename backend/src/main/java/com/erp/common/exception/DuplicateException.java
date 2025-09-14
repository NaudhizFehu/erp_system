package com.erp.common.exception;

import com.erp.common.constants.ErrorCode;
import lombok.Getter;

/**
 * 중복 데이터 예외 클래스
 * 유니크 제약조건 위반이나 중복 데이터 생성 시 사용됩니다
 */
@Getter
public class DuplicateException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final String fieldName;
    private final Object fieldValue;
    
    public DuplicateException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.fieldName = null;
        this.fieldValue = null;
    }
    
    public DuplicateException(ErrorCode errorCode, String fieldName, Object fieldValue) {
        super(String.format("%s (필드: %s, 값: %s)", errorCode.getMessage(), fieldName, fieldValue));
        this.errorCode = errorCode;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public DuplicateException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.fieldName = null;
        this.fieldValue = null;
    }
    
    /**
     * 사용자명 중복 예외 생성
     */
    public static DuplicateException username(String username) {
        return new DuplicateException(
            ErrorCode.DUPLICATE_USERNAME,
            "username",
            username
        );
    }
    
    /**
     * 이메일 중복 예외 생성
     */
    public static DuplicateException email(String email) {
        return new DuplicateException(
            ErrorCode.DUPLICATE_EMAIL,
            "email",
            email
        );
    }
    
    /**
     * 회사 코드 중복 예외 생성
     */
    public static DuplicateException companyCode(String companyCode) {
        return new DuplicateException(
            ErrorCode.DUPLICATE_COMPANY_CODE,
            "companyCode",
            companyCode
        );
    }
    
    /**
     * 사업자등록번호 중복 예외 생성
     */
    public static DuplicateException businessNumber(String businessNumber) {
        return new DuplicateException(
            ErrorCode.DUPLICATE_BUSINESS_NUMBER,
            "businessNumber",
            businessNumber
        );
    }
    
    /**
     * 법인등록번호 중복 예외 생성
     */
    public static DuplicateException corporationNumber(String corporationNumber) {
        return new DuplicateException(
            ErrorCode.DUPLICATE_CORPORATION_NUMBER,
            "corporationNumber",
            corporationNumber
        );
    }
    
    /**
     * 부서 코드 중복 예외 생성
     */
    public static DuplicateException departmentCode(String departmentCode) {
        return new DuplicateException(
            ErrorCode.DUPLICATE_DEPARTMENT_CODE,
            "departmentCode",
            departmentCode
        );
    }
    
    /**
     * 공통코드 중복 예외 생성
     */
    public static DuplicateException codeValue(String groupCode, String codeValue) {
        return new DuplicateException(
            ErrorCode.DUPLICATE_CODE_VALUE,
            "codeValue",
            String.format("%s.%s", groupCode, codeValue)
        );
    }
    
    /**
     * 직원번호 중복 예외 생성
     */
    public static DuplicateException employeeNumber(String employeeNumber) {
        return new DuplicateException(
            ErrorCode.DUPLICATE_EMPLOYEE_NUMBER,
            "employeeNumber",
            employeeNumber
        );
    }
    
    /**
     * 제품 코드 중복 예외 생성
     */
    public static DuplicateException productCode(String productCode) {
        return new DuplicateException(
            ErrorCode.DUPLICATE_PRODUCT_CODE,
            "productCode",
            productCode
        );
    }
    
    /**
     * 고객 코드 중복 예외 생성
     */
    public static DuplicateException customerCode(String customerCode) {
        return new DuplicateException(
            ErrorCode.DUPLICATE_CUSTOMER_CODE,
            "customerCode",
            customerCode
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




