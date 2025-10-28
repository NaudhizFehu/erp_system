package com.erp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 설정
 * ERP 시스템의 API 문서화를 위한 설정입니다
 */
@Configuration
public class SwaggerConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:9961" + contextPath)
                                .description("개발 서버"),
                        new Server()
                                .url("https://api.erp-system.com" + contextPath)
                                .description("운영 서버")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", createAPIKeyScheme()))
                .tags(List.of(
                        createTag("인증 관리", "사용자 인증 및 권한 관리 API"),
                        createTag("공통 관리", "공통코드, 회사, 부서 관리 API"),
                        createTag("인사관리", "직원, 급여, 근태 관리 API"),
                        createTag("영업관리", "고객, 주문, 견적, 계약 관리 API"),
                        createTag("재고관리", "상품, 재고, 입출고 관리 API"),
                        createTag("회계관리", "계정과목, 거래, 예산 관리 API"),
                        createTag("대시보드", "통계 및 현황 조회 API")
                ));
    }

    /**
     * API 정보 설정
     */
    private Info apiInfo() {
        return new Info()
                .title("ERP 시스템 API")
                .description("""
                        ## ERP 시스템 REST API 문서
                        
                        이 문서는 ERP 시스템의 모든 API 엔드포인트에 대한 상세한 정보를 제공합니다.
                        
                        ### 주요 기능
                        - **인사관리**: 직원, 급여, 근태 관리
                        - **영업관리**: 고객, 주문, 견적, 계약 관리  
                        - **재고관리**: 상품, 재고, 입출고 관리
                        - **회계관리**: 계정과목, 거래, 예산 관리
                        - **대시보드**: 통계 및 현황 조회
                        
                        ### 인증 방법
                        모든 API는 JWT Bearer 토큰을 사용한 인증이 필요합니다.
                        
                        1. `/api/auth/login` 엔드포인트로 로그인
                        2. 응답에서 받은 `accessToken`을 Authorization 헤더에 포함
                        3. 헤더 형식: `Authorization: Bearer {accessToken}`
                        
                        ### 권한 레벨
                        - **USER**: 일반 사용자 권한
                        - **MANAGER**: 관리자 권한  
                        - **ADMIN**: 시스템 관리자 권한
                        
                        ### 응답 형식
                        모든 API는 표준화된 응답 형식을 사용합니다:
                        ```json
                        {
                          "success": true,
                          "message": "성공 메시지",
                          "data": { ... },
                          "timestamp": "2023-12-01T10:30:00Z"
                        }
                        ```
                        
                        ### 에러 처리
                        에러 발생시 HTTP 상태 코드와 함께 에러 정보를 반환합니다:
                        ```json
                        {
                          "success": false,
                          "message": "에러 메시지",
                          "error": "상세 에러 정보",
                          "timestamp": "2023-12-01T10:30:00Z"
                        }
                        ```
                        
                        ### 페이징
                        목록 조회 API는 페이징을 지원합니다:
                        - `page`: 페이지 번호 (0부터 시작)
                        - `size`: 페이지 크기 (기본값: 20)
                        - `sort`: 정렬 조건 (예: name,asc)
                        """)
                .version(appVersion)
                .contact(new Contact()
                        .name("ERP 시스템 개발팀")
                        .email("dev@erp-system.com")
                        .url("https://erp-system.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * JWT 보안 스키마 생성
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("""
                        JWT Bearer 토큰을 사용한 인증입니다.
                        
                        **사용 방법:**
                        1. 로그인 API를 통해 토큰을 획득합니다
                        2. Authorization 헤더에 'Bearer {token}' 형식으로 포함합니다
                        
                        **예시:**
                        ```
                        Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                        ```
                        """);
    }

    /**
     * API 태그 생성
     */
    private Tag createTag(String name, String description) {
        return new Tag()
                .name(name)
                .description(description);
    }
}

/**
 * 공통 API 응답 스키마
 * 모든 API에서 사용하는 표준 응답 형식을 정의합니다
 */
class ApiResponseSchemas {
    
    /**
     * 성공 응답 예시
     */
    public static final String SUCCESS_RESPONSE_EXAMPLE = """
            {
              "success": true,
              "message": "요청이 성공적으로 처리되었습니다",
              "data": {
                "id": 1,
                "name": "샘플 데이터"
              },
              "timestamp": "2023-12-01T10:30:00Z"
            }
            """;

    /**
     * 에러 응답 예시
     */
    public static final String ERROR_RESPONSE_EXAMPLE = """
            {
              "success": false,
              "message": "요청 처리 중 오류가 발생했습니다",
              "error": "상세 에러 정보",
              "timestamp": "2023-12-01T10:30:00Z"
            }
            """;

    /**
     * 페이징 응답 예시
     */
    public static final String PAGING_RESPONSE_EXAMPLE = """
            {
              "success": true,
              "message": "목록 조회가 완료되었습니다",
              "data": {
                "content": [
                  { "id": 1, "name": "항목1" },
                  { "id": 2, "name": "항목2" }
                ],
                "totalElements": 100,
                "totalPages": 5,
                "size": 20,
                "number": 0,
                "first": true,
                "last": false,
                "empty": false
              },
              "timestamp": "2023-12-01T10:30:00Z"
            }
            """;

    /**
     * 인증 에러 응답 예시
     */
    public static final String AUTH_ERROR_EXAMPLE = """
            {
              "success": false,
              "message": "인증이 필요합니다",
              "error": "JWT token is missing or invalid",
              "timestamp": "2023-12-01T10:30:00Z"
            }
            """;

    /**
     * 권한 에러 응답 예시
     */
    public static final String FORBIDDEN_ERROR_EXAMPLE = """
            {
              "success": false,
              "message": "접근 권한이 없습니다",
              "error": "Access denied for this resource",
              "timestamp": "2023-12-01T10:30:00Z"
            }
            """;

    /**
     * 유효성 검증 에러 응답 예시
     */
    public static final String VALIDATION_ERROR_EXAMPLE = """
            {
              "success": false,
              "message": "입력 데이터가 올바르지 않습니다",
              "error": "Validation failed: name은 필수 입력 항목입니다",
              "timestamp": "2023-12-01T10:30:00Z"
            }
            """;
}