package com.erp.common.controller;

import com.erp.common.constants.ErrorCode;
import com.erp.common.dto.ApiResponse;
import com.erp.common.exception.BusinessException;
import com.erp.common.exception.ValidationException;
import com.erp.common.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 예외 처리 사용 예시 컨트롤러
 * API 응답 형식과 예외 처리 사용법을 보여줍니다
 */
@Slf4j
@RestController
@RequestMapping("/api/example")
public class ExampleController {

    /**
     * 성공 응답 예시
     */
    @GetMapping("/success")
    public ResponseEntity<ApiResponse<Map<String, Object>>> successExample() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "성공적으로 처리되었습니다");
        data.put("timestamp", java.time.LocalDateTime.now());
        data.put("userId", 1L);
        
        return ResponseEntity.ok(ApiResponse.success("데이터 조회 완료", data));
    }

    /**
     * 페이지네이션 성공 응답 예시
     */
    @GetMapping("/success-pagination")
    public ResponseEntity<ApiResponse<ApiResponse.PageResponse<Map<String, Object>>>> successPaginationExample() {
        Map<String, Object> content = new HashMap<>();
        content.put("users", java.util.List.of("사용자1", "사용자2", "사용자3"));
        
        return ResponseEntity.ok(
            ApiResponse.successWithPagination(content, 0, 10, 50, 5)
        );
    }

    /**
     * 비즈니스 예외 발생 예시
     */
    @GetMapping("/business-error")
    public ResponseEntity<ApiResponse<Void>> businessErrorExample() {
        // ErrorCode를 사용한 예외 발생
        throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    /**
     * 커스텀 메시지 비즈니스 예외 예시
     */
    @GetMapping("/custom-business-error")
    public ResponseEntity<ApiResponse<Void>> customBusinessErrorExample() {
        // 커스텀 메시지와 함께 예외 발생
        throw new BusinessException(
            ErrorCode.COMPANY_INACTIVE, 
            "ABC 회사는 현재 비활성화 상태입니다"
        );
    }

    /**
     * 데이터와 함께 예외 발생 예시
     */
    @GetMapping("/error-with-data")
    public ResponseEntity<ApiResponse<Void>> errorWithDataExample() {
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("attemptedUserId", 999L);
        errorData.put("availableUsers", java.util.List.of(1L, 2L, 3L));
        
        throw new BusinessException(ErrorCode.USER_NOT_FOUND, errorData);
    }

    /**
     * 검증 예외 발생 예시
     */
    @GetMapping("/validation-error")
    public ResponseEntity<ApiResponse<Void>> validationErrorExample() {
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put("username", "사용자명은 3자 이상이어야 합니다");
        fieldErrors.put("email", "올바른 이메일 형식이어야 합니다");
        fieldErrors.put("password", "비밀번호는 8자 이상이어야 합니다");
        
        throw new ValidationException("입력값 검증에 실패했습니다", fieldErrors);
    }

    /**
     * 엔티티 조회 실패 예외 예시
     */
    @GetMapping("/entity-not-found")
    public ResponseEntity<ApiResponse<Void>> entityNotFoundExample() {
        // ExceptionUtils를 사용한 편리한 예외 생성
        throw ExceptionUtils.userNotFound(999L);
    }

    /**
     * 중복 데이터 예외 예시
     */
    @GetMapping("/duplicate-error")
    public ResponseEntity<ApiResponse<Void>> duplicateErrorExample() {
        // ExceptionUtils를 사용한 중복 예외 생성
        throw ExceptionUtils.duplicateUsername("admin");
    }

    /**
     * 인증 예외 예시
     */
    @GetMapping("/auth-error")
    public ResponseEntity<ApiResponse<Void>> authErrorExample() {
        // ExceptionUtils를 사용한 인증 예외 생성
        throw ExceptionUtils.loginFailed("invalid_user");
    }

    /**
     * 인가 예외 예시
     */
    @GetMapping("/authz-error")
    public ResponseEntity<ApiResponse<Void>> authzErrorExample() {
        // ExceptionUtils를 사용한 인가 예외 생성
        throw ExceptionUtils.adminRequired();
    }

    /**
     * 복잡한 비즈니스 로직 예외 예시
     */
    @GetMapping("/complex-business-error")
    public ResponseEntity<ApiResponse<Void>> complexBusinessErrorExample() {
        // 재고 부족 예외
        throw ExceptionUtils.insufficientStock("iPhone 15 Pro", 5, 10);
    }

    /**
     * 일반 런타임 예외 예시 (GlobalExceptionHandler에서 처리)
     */
    @GetMapping("/runtime-error")
    public ResponseEntity<ApiResponse<Void>> runtimeErrorExample() {
        // 일반 RuntimeException 발생
        throw new RuntimeException("예상치 못한 오류가 발생했습니다");
    }

    /**
     * IllegalArgumentException 예시
     */
    @GetMapping("/illegal-argument")
    public ResponseEntity<ApiResponse<Void>> illegalArgumentExample() {
        throw new IllegalArgumentException("잘못된 인수가 전달되었습니다");
    }

    /**
     * 조건부 에러 처리 예시
     */
    @GetMapping("/conditional-error/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> conditionalErrorExample(
            @PathVariable Long userId) {
        
        // 비즈니스 로직에 따른 조건부 예외 처리
        if (userId <= 0) {
            throw ExceptionUtils.requiredFieldMissing("userId");
        }
        
        if (userId > 1000) {
            throw ExceptionUtils.valueOutOfRange("userId", 1, 1000);
        }
        
        if (userId == 999) {
            throw ExceptionUtils.userNotFound(userId);
        }
        
        // 정상 처리
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("username", "user" + userId);
        data.put("status", "active");
        
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 완료", data));
    }

    /**
     * 파일 업로드 예외 예시
     */
    @PostMapping("/file-upload-error")
    public ResponseEntity<ApiResponse<Void>> fileUploadErrorExample() {
        // 파일 관련 예외들
        String fileName = "large-file.pdf";
        long fileSize = 50 * 1024 * 1024; // 50MB
        long maxSize = 10 * 1024 * 1024;  // 10MB
        
        throw ExceptionUtils.fileSizeExceeded(fileName, fileSize, maxSize);
    }

    /**
     * 여러 예외 타입을 처리하는 복합 예시
     */
    @PostMapping("/complex-validation")
    public ResponseEntity<ApiResponse<Map<String, Object>>> complexValidationExample(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String value) {
        
        // 다양한 검증 로직과 예외 처리
        if (type == null || type.trim().isEmpty()) {
            throw ExceptionUtils.requiredFieldMissing("type");
        }
        
        if (value == null || value.trim().isEmpty()) {
            throw ExceptionUtils.requiredFieldMissing("value");
        }
        
        switch (type.toLowerCase()) {
            case "email":
                if (!value.contains("@")) {
                    throw ExceptionUtils.invalidFormat("email", "user@example.com");
                }
                break;
            case "phone":
                if (!value.matches("^\\d{2,3}-\\d{3,4}-\\d{4}$")) {
                    throw ExceptionUtils.invalidFormat("phone", "010-1234-5678");
                }
                break;
            case "age":
                try {
                    int age = Integer.parseInt(value);
                    if (age < 0 || age > 150) {
                        throw ExceptionUtils.valueOutOfRange("age", 0, 150);
                    }
                } catch (NumberFormatException e) {
                    throw ExceptionUtils.invalidFormat("age", "숫자");
                }
                break;
            default:
                throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "지원하지 않는 검증 타입입니다: " + type
                );
        }
        
        // 검증 성공
        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("value", value);
        result.put("validation", "success");
        
        return ResponseEntity.ok(ApiResponse.success("검증이 완료되었습니다", result));
    }
}




