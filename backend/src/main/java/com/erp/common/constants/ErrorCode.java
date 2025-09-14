package com.erp.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 시스템 에러 코드 정의
 * 모든 에러에 대한 코드와 메시지를 중앙 집중식으로 관리합니다
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ==================== 공통 에러 (1000번대) ====================
    SUCCESS(HttpStatus.OK, "C1000", "정상 처리되었습니다"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C1001", "입력값이 올바르지 않습니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C1002", "입력 타입이 올바르지 않습니다"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C1003", "요청하신 데이터를 찾을 수 없습니다"),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "C1004", "이미 존재하는 리소스입니다"),
    BUSINESS_LOGIC_ERROR(HttpStatus.BAD_REQUEST, "C1005", "비즈니스 로직 오류가 발생했습니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C1006", "허용되지 않은 HTTP 메소드입니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C1007", "접근 권한이 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C1008", "시스템 오류가 발생했습니다. 관리자에게 문의해주세요"),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "C1009", "JSON 형식이 올바르지 않습니다"),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "C1010", "필수 요청 파라미터가 누락되었습니다"),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "C1011", "요청 본문이 올바르지 않습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C1012", "인증이 필요합니다"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "C1013", "토큰이 만료되었습니다. 다시 로그인해주세요"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "C1014", "유효하지 않은 토큰입니다"),
    
    // ==================== 사용자 관련 에러 (2000번대) ====================
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U2001", "사용자를 찾을 수 없습니다"),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "U2002", "이미 사용 중인 사용자명입니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U2003", "이미 등록된 이메일 주소입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U2004", "비밀번호가 올바르지 않습니다"),
    WEAK_PASSWORD(HttpStatus.BAD_REQUEST, "U2005", "비밀번호는 8자 이상이며, 대소문자, 숫자, 특수문자를 포함해야 합니다"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "U2006", "계정이 잠겨있습니다. 관리자에게 문의해주세요"),
    ACCOUNT_INACTIVE(HttpStatus.FORBIDDEN, "U2007", "비활성화된 계정입니다"),
    ACCOUNT_EXPIRED(HttpStatus.FORBIDDEN, "U2008", "계정이 만료되었습니다"),
    PASSWORD_EXPIRED(HttpStatus.FORBIDDEN, "U2009", "비밀번호가 만료되었습니다. 비밀번호를 변경해주세요"),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "U2010", "사용자명 또는 비밀번호가 올바르지 않습니다"),
    INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, "U2011", "해당 작업을 수행할 권한이 없습니다"),
    
    // ==================== 회사 관련 에러 (3000번대) ====================
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "CO3001", "회사 정보를 찾을 수 없습니다"),
    DUPLICATE_COMPANY_CODE(HttpStatus.CONFLICT, "CO3002", "이미 사용 중인 회사 코드입니다"),
    DUPLICATE_BUSINESS_NUMBER(HttpStatus.CONFLICT, "CO3003", "이미 등록된 사업자등록번호입니다"),
    DUPLICATE_CORPORATION_NUMBER(HttpStatus.CONFLICT, "CO3004", "이미 등록된 법인등록번호입니다"),
    INVALID_BUSINESS_NUMBER(HttpStatus.BAD_REQUEST, "CO3005", "사업자등록번호 형식이 올바르지 않습니다"),
    INVALID_CORPORATION_NUMBER(HttpStatus.BAD_REQUEST, "CO3006", "법인등록번호 형식이 올바르지 않습니다"),
    COMPANY_INACTIVE(HttpStatus.BAD_REQUEST, "CO3007", "비활성화된 회사입니다"),
    COMPANY_HAS_DEPARTMENTS(HttpStatus.BAD_REQUEST, "CO3008", "소속 부서가 있는 회사는 삭제할 수 없습니다"),
    COMPANY_HAS_USERS(HttpStatus.BAD_REQUEST, "CO3009", "소속 직원이 있는 회사는 삭제할 수 없습니다"),
    
    // ==================== 부서 관련 에러 (4000번대) ====================
    DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "D4001", "부서 정보를 찾을 수 없습니다"),
    DUPLICATE_DEPARTMENT_CODE(HttpStatus.CONFLICT, "D4002", "이미 사용 중인 부서 코드입니다"),
    CIRCULAR_DEPARTMENT_REFERENCE(HttpStatus.BAD_REQUEST, "D4003", "부서 계층 구조에서 순환 참조가 발생했습니다"),
    DEPARTMENT_HAS_SUBDEPARTMENTS(HttpStatus.BAD_REQUEST, "D4004", "하위 부서가 있는 부서는 삭제할 수 없습니다"),
    DEPARTMENT_HAS_USERS(HttpStatus.BAD_REQUEST, "D4005", "소속 직원이 있는 부서는 삭제할 수 없습니다"),
    INVALID_DEPARTMENT_HIERARCHY(HttpStatus.BAD_REQUEST, "D4006", "올바르지 않은 부서 계층 구조입니다"),
    DEPARTMENT_INACTIVE(HttpStatus.BAD_REQUEST, "D4007", "비활성화된 부서입니다"),
    PARENT_DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "D4008", "상위 부서를 찾을 수 없습니다"),
    DEPARTMENT_LEVEL_EXCEEDED(HttpStatus.BAD_REQUEST, "D4009", "부서 계층 레벨이 초과되었습니다"),
    
    // ==================== 공통코드 관련 에러 (5000번대) ====================
    CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "CD5001", "공통코드를 찾을 수 없습니다"),
    DUPLICATE_CODE_VALUE(HttpStatus.CONFLICT, "CD5002", "이미 사용 중인 코드값입니다"),
    SYSTEM_CODE_MODIFY_DENIED(HttpStatus.FORBIDDEN, "CD5003", "시스템 코드는 수정할 수 없습니다"),
    SYSTEM_CODE_DELETE_DENIED(HttpStatus.FORBIDDEN, "CD5004", "시스템 코드는 삭제할 수 없습니다"),
    CODE_HAS_SUBCODES(HttpStatus.BAD_REQUEST, "CD5005", "하위 코드가 있는 코드는 삭제할 수 없습니다"),
    CODE_IN_USE(HttpStatus.BAD_REQUEST, "CD5006", "사용 중인 코드는 삭제할 수 없습니다"),
    INVALID_CODE_HIERARCHY(HttpStatus.BAD_REQUEST, "CD5007", "올바르지 않은 코드 계층 구조입니다"),
    CIRCULAR_CODE_REFERENCE(HttpStatus.BAD_REQUEST, "CD5008", "코드 계층 구조에서 순환 참조가 발생했습니다"),
    
    // ==================== 인사관리 에러 (6000번대) ====================
    EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND, "E6001", "직원 정보를 찾을 수 없습니다"),
    DUPLICATE_EMPLOYEE_NUMBER(HttpStatus.CONFLICT, "E6002", "이미 사용 중인 직원번호입니다"),
    EMPLOYEE_ALREADY_TERMINATED(HttpStatus.BAD_REQUEST, "E6003", "이미 퇴사 처리된 직원입니다"),
    INVALID_HIRE_DATE(HttpStatus.BAD_REQUEST, "E6004", "입사일이 올바르지 않습니다"),
    SALARY_CANNOT_BE_NEGATIVE(HttpStatus.BAD_REQUEST, "E6005", "급여는 음수일 수 없습니다"),
    
    // ==================== 재고관리 에러 (7000번대) ====================
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P7001", "제품 정보를 찾을 수 없습니다"),
    DUPLICATE_PRODUCT_CODE(HttpStatus.CONFLICT, "P7002", "이미 사용 중인 제품 코드입니다"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "P7003", "재고가 부족합니다"),
    INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "P7004", "재고 수량이 올바르지 않습니다"),
    PRODUCT_DISCONTINUED(HttpStatus.BAD_REQUEST, "P7005", "단종된 제품입니다"),
    NEGATIVE_PRICE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "P7006", "제품 가격은 음수일 수 없습니다"),
    
    // ==================== 영업관리 에러 (8000번대) ====================
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "CU8001", "고객 정보를 찾을 수 없습니다"),
    DUPLICATE_CUSTOMER_CODE(HttpStatus.CONFLICT, "CU8002", "이미 사용 중인 고객 코드입니다"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O8003", "주문 정보를 찾을 수 없습니다"),
    ORDER_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "O8004", "이미 취소된 주문입니다"),
    ORDER_CANNOT_BE_MODIFIED(HttpStatus.BAD_REQUEST, "O8005", "주문 상태상 수정할 수 없습니다"),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O8006", "올바르지 않은 주문 상태입니다"),
    
    // ==================== 회계관리 에러 (9000번대) ====================
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "A9001", "계정과목을 찾을 수 없습니다"),
    DUPLICATE_ACCOUNT_CODE(HttpStatus.CONFLICT, "A9002", "이미 사용 중인 계정 코드입니다"),
    VOUCHER_NOT_FOUND(HttpStatus.NOT_FOUND, "V9003", "전표를 찾을 수 없습니다"),
    VOUCHER_ALREADY_POSTED(HttpStatus.BAD_REQUEST, "V9004", "이미 전기된 전표입니다"),
    UNBALANCED_VOUCHER(HttpStatus.BAD_REQUEST, "V9005", "차변과 대변의 합계가 일치하지 않습니다"),
    INVALID_ACCOUNTING_DATE(HttpStatus.BAD_REQUEST, "V9006", "회계 일자가 올바르지 않습니다"),
    
    // ==================== 파일 관련 에러 (F000번대) ====================
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "F1001", "파일을 찾을 수 없습니다"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F1002", "파일 업로드에 실패했습니다"),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "F1003", "파일 크기가 허용 한도를 초과했습니다"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "F1004", "허용되지 않은 파일 형식입니다"),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F1005", "파일 삭제에 실패했습니다"),
    
    // ==================== 외부 연동 에러 (I000번대) ====================
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "I1001", "외부 시스템과의 연동에 실패했습니다"),
    EXTERNAL_API_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "I1002", "외부 시스템 응답 시간이 초과되었습니다"),
    EXTERNAL_API_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "I1003", "외부 시스템이 일시적으로 사용할 수 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    /**
     * HTTP 상태 코드 반환
     */
    public int getStatus() {
        return httpStatus.value();
    }

    /**
     * 에러 코드와 메시지를 포함한 문자열 반환
     */
    public String getFullMessage() {
        return String.format("[%s] %s", code, message);
    }

    /**
     * 에러 코드로 ErrorCode 찾기
     */
    public static ErrorCode findByCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }
}
