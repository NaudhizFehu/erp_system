package com.erp.common.dto;

import com.erp.common.constants.ErrorCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * API 응답 표준 포맷
 * 모든 REST API 응답에서 사용되는 공통 응답 구조입니다
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    /**
     * 성공 여부
     */
    private boolean success;
    
    /**
     * 응답 메시지
     */
    private String message;
    
    /**
     * 응답 데이터
     */
    private T data;
    
    /**
     * 에러 코드
     */
    private String errorCode;
    
    /**
     * HTTP 상태 코드
     */
    private int status;
    
    /**
     * 응답 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * 요청 경로 (에러 시에만 포함)
     */
    private String path;
    
    /**
     * 상세 에러 정보 (개발/디버그용)
     */
    private Object details;

    /**
     * 성공 응답 생성 (기본 메시지)
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(ErrorCode.SUCCESS.getMessage(), data);
    }
    
    /**
     * 성공 응답 생성 (커스텀 메시지)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.message = message;
        response.data = data;
        response.status = ErrorCode.SUCCESS.getStatus();
        response.timestamp = LocalDateTime.now();
        return response;
    }
    
    /**
     * 성공 응답 생성 (데이터 없음)
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }
    
    /**
     * 성공 응답 생성 (메시지만)
     */
    public static <T> ApiResponse<T> success(String message) {
        return success(message, null);
    }
    
    /**
     * 실패 응답 생성 (ErrorCode 사용)
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return error(errorCode, null, null);
    }
    
    /**
     * 실패 응답 생성 (ErrorCode + 경로)
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String path) {
        return error(errorCode, path, null);
    }
    
    /**
     * 실패 응답 생성 (ErrorCode + 경로 + 상세정보)
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String path, Object details) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = errorCode.getMessage();
        response.errorCode = errorCode.getCode();
        response.status = errorCode.getStatus();
        response.timestamp = LocalDateTime.now();
        response.path = path;
        response.details = details;
        return response;
    }
    
    /**
     * 실패 응답 생성 (커스텀 메시지)
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(message, null);
    }
    
    /**
     * 실패 응답 생성 (커스텀 메시지 + 에러코드)
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.errorCode = errorCode;
        response.status = 500;
        response.timestamp = LocalDateTime.now();
        return response;
    }
    
    /**
     * 검증 실패 응답 생성
     */
    public static <T> ApiResponse<T> validationError(String message, Object details) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.message = message;
        response.errorCode = ErrorCode.INVALID_INPUT_VALUE.getCode();
        response.status = ErrorCode.INVALID_INPUT_VALUE.getStatus();
        response.timestamp = LocalDateTime.now();
        response.details = details;
        return response;
    }
    
    /**
     * 페이지네이션 정보를 포함한 성공 응답 생성
     */
    public static <T> ApiResponse<PageResponse<T>> successWithPagination(
            T content, 
            int pageNumber, 
            int pageSize, 
            long totalElements, 
            int totalPages) {
        
        PageResponse<T> pageResponse = new PageResponse<>(
            content, pageNumber, pageSize, totalElements, totalPages
        );
        
        return success(pageResponse);
    }
    
    /**
     * 응답이 성공인지 확인
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * 응답이 실패인지 확인
     */
    public boolean isError() {
        return !success;
    }
    
    /**
     * 페이지네이션 응답을 위한 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageResponse<T> {
        private T content;
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
        private boolean empty;
        
        public PageResponse(T content, int pageNumber, int pageSize, long totalElements, int totalPages) {
            this.content = content;
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.first = pageNumber == 0;
            this.last = pageNumber >= totalPages - 1;
            this.empty = totalElements == 0;
        }
    }
}

