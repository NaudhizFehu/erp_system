package com.erp.common.utils;

import com.erp.common.constants.ErrorCode;
import com.erp.common.exception.*;

/**
 * 예외 처리 유틸리티 클래스
 * 자주 사용되는 예외 생성 로직을 편리하게 제공합니다
 */
public class ExceptionUtils {

    private ExceptionUtils() {
        // 유틸리티 클래스는 인스턴스화 방지
    }

    // ==================== Entity 관련 예외 ====================

    /**
     * 사용자 조회 실패 예외
     */
    public static EntityNotFoundException userNotFound(Long userId) {
        return EntityNotFoundException.userNotFound(userId);
    }

    /**
     * 회사 조회 실패 예외
     */
    public static EntityNotFoundException companyNotFound(Long companyId) {
        return EntityNotFoundException.companyNotFound(companyId);
    }

    /**
     * 부서 조회 실패 예외
     */
    public static EntityNotFoundException departmentNotFound(Long departmentId) {
        return EntityNotFoundException.departmentNotFound(departmentId);
    }

    /**
     * 공통코드 조회 실패 예외
     */
    public static EntityNotFoundException codeNotFound(String groupCode, String codeValue) {
        return EntityNotFoundException.codeNotFound(groupCode, codeValue);
    }

    // ==================== 중복 데이터 예외 ====================

    /**
     * 사용자명 중복 예외
     */
    public static DuplicateException duplicateUsername(String username) {
        return DuplicateException.username(username);
    }

    /**
     * 이메일 중복 예외
     */
    public static DuplicateException duplicateEmail(String email) {
        return DuplicateException.email(email);
    }

    /**
     * 회사 코드 중복 예외
     */
    public static DuplicateException duplicateCompanyCode(String companyCode) {
        return DuplicateException.companyCode(companyCode);
    }

    /**
     * 사업자등록번호 중복 예외
     */
    public static DuplicateException duplicateBusinessNumber(String businessNumber) {
        return DuplicateException.businessNumber(businessNumber);
    }

    /**
     * 부서 코드 중복 예외
     */
    public static DuplicateException duplicateDepartmentCode(String departmentCode) {
        return DuplicateException.departmentCode(departmentCode);
    }

    // ==================== 인증/인가 예외 ====================

    /**
     * 로그인 실패 예외
     */
    public static AuthenticationException loginFailed(String username) {
        return AuthenticationException.loginFailed(username);
    }

    /**
     * 계정 잠금 예외
     */
    public static AuthenticationException accountLocked(String username) {
        return AuthenticationException.accountLocked(username);
    }

    /**
     * 접근 거부 예외
     */
    public static AuthorizationException accessDenied() {
        return AuthorizationException.accessDenied();
    }

    /**
     * 관리자 권한 필요 예외
     */
    public static AuthorizationException adminRequired() {
        return AuthorizationException.adminRequired();
    }

    /**
     * 유효하지 않은 토큰 예외
     */
    public static AuthenticationException invalidToken() {
        return AuthenticationException.invalidToken();
    }

    /**
     * 인증되지 않은 접근 예외
     */
    public static AuthenticationException unauthorized() {
        return AuthenticationException.unauthorized();
    }

    // ==================== 비즈니스 로직 예외 ====================

    /**
     * 회사 비활성화 예외
     */
    public static BusinessException companyInactive(String companyName) {
        return new BusinessException(
            ErrorCode.COMPANY_INACTIVE,
            String.format("비활성화된 회사입니다: %s", companyName)
        );
    }

    /**
     * 부서 비활성화 예외
     */
    public static BusinessException departmentInactive(String departmentName) {
        return new BusinessException(
            ErrorCode.DEPARTMENT_INACTIVE,
            String.format("비활성화된 부서입니다: %s", departmentName)
        );
    }

    /**
     * 하위 부서 존재로 인한 삭제 불가 예외
     */
    public static BusinessException departmentHasSubdepartments(String departmentName) {
        return new BusinessException(
            ErrorCode.DEPARTMENT_HAS_SUBDEPARTMENTS,
            String.format("하위 부서가 있어 삭제할 수 없습니다: %s", departmentName)
        );
    }

    /**
     * 소속 직원 존재로 인한 부서 삭제 불가 예외
     */
    public static BusinessException departmentHasUsers(String departmentName, int userCount) {
        return new BusinessException(
            ErrorCode.DEPARTMENT_HAS_USERS,
            String.format("소속 직원이 %d명 있어 부서를 삭제할 수 없습니다: %s", userCount, departmentName)
        );
    }

    /**
     * 순환 참조 예외
     */
    public static BusinessException circularReference(String entityType, String entityName) {
        if ("department".equals(entityType)) {
            return new BusinessException(
                ErrorCode.CIRCULAR_DEPARTMENT_REFERENCE,
                String.format("부서 계층 구조에서 순환 참조가 발생했습니다: %s", entityName)
            );
        } else if ("code".equals(entityType)) {
            return new BusinessException(
                ErrorCode.CIRCULAR_CODE_REFERENCE,
                String.format("코드 계층 구조에서 순환 참조가 발생했습니다: %s", entityName)
            );
        } else {
            return new BusinessException(
                ErrorCode.INVALID_INPUT_VALUE,
                String.format("순환 참조가 발생했습니다: %s", entityName)
            );
        }
    }

    /**
     * 시스템 코드 수정 불가 예외
     */
    public static BusinessException systemCodeModifyDenied(String codeValue) {
        return new BusinessException(
            ErrorCode.SYSTEM_CODE_MODIFY_DENIED,
            String.format("시스템 코드는 수정할 수 없습니다: %s", codeValue)
        );
    }

    /**
     * 시스템 코드 삭제 불가 예외
     */
    public static BusinessException systemCodeDeleteDenied(String codeValue) {
        return new BusinessException(
            ErrorCode.SYSTEM_CODE_DELETE_DENIED,
            String.format("시스템 코드는 삭제할 수 없습니다: %s", codeValue)
        );
    }

    /**
     * 재고 부족 예외
     */
    public static BusinessException insufficientStock(String productName, int availableStock, int requestedQuantity) {
        return new BusinessException(
            ErrorCode.INSUFFICIENT_STOCK,
            String.format("재고가 부족합니다. 제품: %s, 재고: %d개, 요청: %d개", 
                productName, availableStock, requestedQuantity)
        );
    }

    /**
     * 제품 단종 예외
     */
    public static BusinessException productDiscontinued(String productName) {
        return new BusinessException(
            ErrorCode.PRODUCT_DISCONTINUED,
            String.format("단종된 제품입니다: %s", productName)
        );
    }

    /**
     * 주문 취소 불가 예외
     */
    public static BusinessException orderCannotBeCancelled(String orderNumber, String currentStatus) {
        return new BusinessException(
            ErrorCode.ORDER_CANNOT_BE_MODIFIED,
            String.format("현재 상태에서는 주문을 취소할 수 없습니다. 주문번호: %s, 상태: %s", 
                orderNumber, currentStatus)
        );
    }

    /**
     * 전표 전기 완료로 인한 수정 불가 예외
     */
    public static BusinessException voucherAlreadyPosted(String voucherNumber) {
        return new BusinessException(
            ErrorCode.VOUCHER_ALREADY_POSTED,
            String.format("이미 전기된 전표는 수정할 수 없습니다: %s", voucherNumber)
        );
    }

    /**
     * 차대변 불일치 예외
     */
    public static BusinessException unbalancedVoucher(String voucherNumber, 
                                                     java.math.BigDecimal debitTotal, 
                                                     java.math.BigDecimal creditTotal) {
        return new BusinessException(
            ErrorCode.UNBALANCED_VOUCHER,
            String.format("차변과 대변의 합계가 일치하지 않습니다. 전표번호: %s, 차변: %s, 대변: %s", 
                voucherNumber, debitTotal, creditTotal)
        );
    }

    // ==================== 검증 예외 ====================

    /**
     * 필수값 누락 예외
     */
    public static ValidationException requiredFieldMissing(String fieldName) {
        java.util.Map<String, String> fieldErrors = java.util.Map.of(
            fieldName, fieldName + "은(는) 필수 입력 항목입니다"
        );
        return new ValidationException("필수 입력값이 누락되었습니다", fieldErrors);
    }

    /**
     * 잘못된 형식 예외
     */
    public static ValidationException invalidFormat(String fieldName, String expectedFormat) {
        java.util.Map<String, String> fieldErrors = java.util.Map.of(
            fieldName, String.format("올바른 형식이 아닙니다. 예상 형식: %s", expectedFormat)
        );
        return new ValidationException("입력 형식이 올바르지 않습니다", fieldErrors);
    }

    /**
     * 값 범위 초과 예외
     */
    public static ValidationException valueOutOfRange(String fieldName, Object minValue, Object maxValue) {
        java.util.Map<String, String> fieldErrors = java.util.Map.of(
            fieldName, String.format("값이 허용 범위를 벗어났습니다. 범위: %s ~ %s", minValue, maxValue)
        );
        return new ValidationException("값이 허용 범위를 벗어났습니다", fieldErrors);
    }

    // ==================== 파일 관련 예외 ====================

    /**
     * 파일 업로드 실패 예외
     */
    public static BusinessException fileUploadFailed(String fileName, String reason) {
        return new BusinessException(
            ErrorCode.FILE_UPLOAD_FAILED,
            String.format("파일 업로드에 실패했습니다. 파일: %s, 사유: %s", fileName, reason)
        );
    }

    /**
     * 파일 크기 초과 예외
     */
    public static BusinessException fileSizeExceeded(String fileName, long fileSize, long maxSize) {
        return new BusinessException(
            ErrorCode.FILE_SIZE_EXCEEDED,
            String.format("파일 크기가 허용 한도를 초과했습니다. 파일: %s, 크기: %dMB, 한도: %dMB", 
                fileName, fileSize / 1024 / 1024, maxSize / 1024 / 1024)
        );
    }

    /**
     * 허용되지 않은 파일 형식 예외
     */
    public static BusinessException invalidFileType(String fileName, String fileType, String allowedTypes) {
        return new BusinessException(
            ErrorCode.INVALID_FILE_TYPE,
            String.format("허용되지 않은 파일 형식입니다. 파일: %s, 형식: %s, 허용 형식: %s", 
                fileName, fileType, allowedTypes)
        );
    }

    // ==================== 편의 메서드 ====================

    /**
     * 중복 데이터 예외 (일반적인 메시지)
     */
    public static void throwDuplicate(String message) {
        throw new DuplicateException(ErrorCode.DUPLICATE_RESOURCE, message);
    }

    /**
     * 엔티티 조회 실패 예외 (일반적인 메시지)
     */
    public static RuntimeException entityNotFound(String message) {
        return new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND, message);
    }

    /**
     * 비즈니스 로직 예외 (일반적인 메시지)
     */
    public static void throwBusinessException(String message) {
        throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, message);
    }

    /**
     * 비즈니스 로직 예외 객체 반환 (일반적인 메시지)
     */
    public static BusinessException businessException(String message) {
        return new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, message);
    }

    /**
     * 엔티티 조회 실패 예외 객체 반환 (일반적인 메시지)
     */
    public static EntityNotFoundException entityNotFoundException(String message) {
        return new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND, message);
    }

    /**
     * 중복 데이터 예외 객체 반환 (일반적인 메시지)
     */
    public static DuplicateException duplicateException(String message) {
        return new DuplicateException(ErrorCode.DUPLICATE_RESOURCE, message);
    }

    /**
     * 유효성 검증 예외 (일반적인 메시지)
     */
    public static void throwValidation(String message) {
        throw new ValidationException(message, new java.util.HashMap<>());
    }

    /**
     * 엔티티 조회 실패 예외 생성 (Supplier용)
     */
    public static RuntimeException throwEntityNotFoundException(String message) {
        return new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND, message);
    }
}
