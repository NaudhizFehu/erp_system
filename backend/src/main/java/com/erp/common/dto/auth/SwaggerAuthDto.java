package com.erp.common.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 인증 관련 Swagger DTO 정의
 * API 문서화를 위한 스키마 정의입니다
 */
public class SwaggerAuthDto {

    /**
     * 로그인 요청 DTO
     */
    @Schema(
        name = "LoginRequest",
        description = "로그인 요청 정보",
        example = """
            {
              "usernameOrEmail": "admin",
              "password": "password123",
              "rememberMe": true
            }
            """
    )
    public record LoginRequestSchema(
        @Schema(
            description = "사용자명 또는 이메일 주소",
            example = "admin",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 3,
            maxLength = 100
        )
        @NotBlank(message = "사용자명 또는 이메일은 필수 입력 항목입니다")
        @Size(min = 3, max = 100, message = "사용자명 또는 이메일은 3-100자 사이여야 합니다")
        String usernameOrEmail,

        @Schema(
            description = "비밀번호",
            example = "password123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            maxLength = 50,
            format = "password"
        )
        @NotBlank(message = "비밀번호는 필수 입력 항목입니다")
        @Size(min = 8, max = 50, message = "비밀번호는 8-50자 사이여야 합니다")
        String password,

        @Schema(
            description = "로그인 상태 유지 여부",
            example = "true",
            defaultValue = "false"
        )
        Boolean rememberMe
    ) {}

    /**
     * 로그인 응답 DTO
     */
    @Schema(
        name = "LoginResponse",
        description = "로그인 응답 정보",
        example = """
            {
              "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
              "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
              "tokenType": "Bearer",
              "expiresIn": 86400,
              "userInfo": {
                "id": 1,
                "username": "admin",
                "email": "admin@company.com",
                "name": "관리자",
                "role": "ADMIN",
                "companyId": 1,
                "companyName": "테스트 회사",
                "departmentId": 1,
                "departmentName": "관리부서",
                "lastLoginAt": "2023-12-01T10:30:00Z"
              }
            }
            """
    )
    public record LoginResponseSchema(
        @Schema(
            description = "JWT 액세스 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        String accessToken,

        @Schema(
            description = "JWT 리프레시 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        String refreshToken,

        @Schema(
            description = "토큰 타입",
            example = "Bearer",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        String tokenType,

        @Schema(
            description = "토큰 만료 시간 (초)",
            example = "86400",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        Long expiresIn,

        @Schema(
            description = "사용자 정보",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        UserInfoSchema userInfo
    ) {}

    /**
     * 사용자 정보 DTO
     */
    @Schema(
        name = "UserInfo",
        description = "사용자 기본 정보",
        example = """
            {
              "id": 1,
              "username": "admin",
              "email": "admin@company.com",
              "name": "관리자",
              "role": "ADMIN",
              "companyId": 1,
              "companyName": "테스트 회사",
              "departmentId": 1,
              "departmentName": "관리부서",
              "lastLoginAt": "2023-12-01T10:30:00Z"
            }
            """
    )
    public record UserInfoSchema(
        @Schema(description = "사용자 ID", example = "1")
        Long id,

        @Schema(description = "사용자명", example = "admin")
        String username,

        @Schema(description = "이메일 주소", example = "admin@company.com")
        String email,

        @Schema(description = "사용자 실명", example = "관리자")
        String name,

        @Schema(description = "사용자 역할", example = "ADMIN", allowableValues = {"USER", "MANAGER", "ADMIN"})
        String role,

        @Schema(description = "소속 회사 ID", example = "1")
        Long companyId,

        @Schema(description = "소속 회사명", example = "테스트 회사")
        String companyName,

        @Schema(description = "소속 부서 ID", example = "1")
        Long departmentId,

        @Schema(description = "소속 부서명", example = "관리부서")
        String departmentName,

        @Schema(description = "마지막 로그인 시간", example = "2023-12-01T10:30:00Z")
        String lastLoginAt
    ) {}

    /**
     * 토큰 갱신 요청 DTO
     */
    @Schema(
        name = "RefreshTokenRequest",
        description = "토큰 갱신 요청 정보",
        example = """
            {
              "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            }
            """
    )
    public record RefreshTokenRequestSchema(
        @Schema(
            description = "리프레시 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "리프레시 토큰은 필수 입력 항목입니다")
        String refreshToken
    ) {}

    /**
     * 비밀번호 변경 요청 DTO
     */
    @Schema(
        name = "ChangePasswordRequest",
        description = "비밀번호 변경 요청 정보",
        example = """
            {
              "currentPassword": "oldPassword123",
              "newPassword": "newPassword123",
              "confirmPassword": "newPassword123"
            }
            """
    )
    public record ChangePasswordRequestSchema(
        @Schema(
            description = "현재 비밀번호",
            example = "oldPassword123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "password"
        )
        @NotBlank(message = "현재 비밀번호는 필수 입력 항목입니다")
        String currentPassword,

        @Schema(
            description = "새 비밀번호",
            example = "newPassword123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            maxLength = 50,
            format = "password"
        )
        @NotBlank(message = "새 비밀번호는 필수 입력 항목입니다")
        @Size(min = 8, max = 50, message = "새 비밀번호는 8-50자 사이여야 합니다")
        String newPassword,

        @Schema(
            description = "새 비밀번호 확인",
            example = "newPassword123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "password"
        )
        @NotBlank(message = "새 비밀번호 확인은 필수 입력 항목입니다")
        String confirmPassword
    ) {}

    /**
     * 토큰 유효성 검증 응답 DTO
     */
    @Schema(
        name = "TokenValidationResponse",
        description = "토큰 유효성 검증 결과",
        example = """
            {
              "valid": true,
              "message": "유효한 토큰입니다",
              "remainingTime": 3600,
              "userId": 1
            }
            """
    )
    public record TokenValidationResponseSchema(
        @Schema(
            description = "토큰 유효성 여부",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        Boolean valid,

        @Schema(
            description = "검증 결과 메시지",
            example = "유효한 토큰입니다",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        String message,

        @Schema(
            description = "토큰 남은 유효 시간 (초)",
            example = "3600"
        )
        Long remainingTime,

        @Schema(
            description = "토큰에 포함된 사용자 ID",
            example = "1"
        )
        Long userId
    ) {}

    /**
     * 공통 API 응답 DTO
     */
    @Schema(
        name = "ApiResponse",
        description = "표준 API 응답 형식",
        example = """
            {
              "success": true,
              "message": "요청이 성공적으로 처리되었습니다",
              "data": {},
              "timestamp": "2023-12-01T10:30:00Z"
            }
            """
    )
    public record ApiResponseSchema<T>(
        @Schema(
            description = "요청 성공 여부",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        Boolean success,

        @Schema(
            description = "응답 메시지",
            example = "요청이 성공적으로 처리되었습니다",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        String message,

        @Schema(
            description = "응답 데이터"
        )
        T data,

        @Schema(
            description = "응답 시간",
            example = "2023-12-01T10:30:00Z",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        String timestamp,

        @Schema(
            description = "에러 정보 (실패시에만 포함)",
            example = "상세 에러 메시지"
        )
        String error
    ) {}

    /**
     * 페이징 응답 DTO
     */
    @Schema(
        name = "PageResponse",
        description = "페이징 응답 형식",
        example = """
            {
              "content": [],
              "totalElements": 100,
              "totalPages": 5,
              "size": 20,
              "number": 0,
              "first": true,
              "last": false,
              "empty": false
            }
            """
    )
    public record PageResponseSchema<T>(
        @Schema(description = "페이지 내용 목록")
        java.util.List<T> content,

        @Schema(description = "전체 요소 수", example = "100")
        Long totalElements,

        @Schema(description = "전체 페이지 수", example = "5")
        Integer totalPages,

        @Schema(description = "페이지 크기", example = "20")
        Integer size,

        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        Integer number,

        @Schema(description = "첫 번째 페이지 여부", example = "true")
        Boolean first,

        @Schema(description = "마지막 페이지 여부", example = "false")
        Boolean last,

        @Schema(description = "빈 페이지 여부", example = "false")
        Boolean empty
    ) {}
}




