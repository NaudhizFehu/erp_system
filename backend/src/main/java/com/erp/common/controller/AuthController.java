package com.erp.common.controller;

import com.erp.common.constants.ErrorCode;
import com.erp.common.dto.ApiResponse;
import com.erp.common.dto.auth.*;
import com.erp.common.dto.UserProfileUpdateDto;
import com.erp.common.entity.User;
import com.erp.common.exception.BusinessException;
import com.erp.common.repository.UserRepository;
import com.erp.hr.repository.DepartmentRepository;
import com.erp.common.security.CustomUserDetailsService;
import com.erp.common.security.JwtAuthenticationFilter;
import com.erp.common.security.JwtUtils;
import com.erp.common.security.UserPrincipal;
import com.erp.common.utils.ExceptionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.responses.ApiResponse; // 별칭 대신 완전한 클래스명 사용
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 인증 관련 REST API 컨트롤러
 * 로그인, 로그아웃, 토큰 갱신, 비밀번호 변경 등의 기능을 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 관리", description = "사용자 인증 및 권한 관리 API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 로그인
     * 
     * @param loginRequest 로그인 요청 정보
     * @return 로그인 응답 (JWT 토큰 포함)
     */
    @Operation(
        summary = "사용자 로그인",
        description = """
            사용자명 또는 이메일과 비밀번호를 사용하여 로그인합니다.
            
            **주요 기능:**
            - 사용자명 또는 이메일로 로그인 가능
            - JWT Access Token과 Refresh Token 발급
            - 마지막 로그인 시간 업데이트
            - 계정 잠금 및 비활성화 상태 확인
            
            **보안 정책:**
            - 연속 로그인 실패시 계정 잠금
            - 비활성화된 계정은 로그인 불가
            - 토큰 만료 시간: 24시간 (Access Token), 30일 (Refresh Token)
            """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "로그인 성공 응답",
                    value = """
                        {
                          "success": true,
                          "message": "로그인이 완료되었습니다",
                          "data": {
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
                          },
                          "timestamp": "2023-12-01T10:30:00Z"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "로그인 실패 - 잘못된 인증 정보",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "로그인 실패 응답",
                    value = """
                        {
                          "success": false,
                          "message": "로그인에 실패했습니다",
                          "error": "사용자명 또는 비밀번호가 올바르지 않습니다",
                          "timestamp": "2023-12-01T10:30:00Z"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "423",
            description = "계정 잠금",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "계정 잠금 응답",
                    value = """
                        {
                          "success": false,
                          "message": "계정이 잠겨있습니다",
                          "error": "연속 로그인 실패로 인해 계정이 잠겼습니다. 관리자에게 문의하세요.",
                          "timestamp": "2023-12-01T10:30:00Z"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "유효성 검증 실패 응답",
                    value = """
                        {
                          "success": false,
                          "message": "입력 데이터가 올바르지 않습니다",
                          "error": "usernameOrEmail은 필수 입력 항목입니다",
                          "timestamp": "2023-12-01T10:30:00Z"
                        }
                        """
                )
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
        @Parameter(
            description = "로그인 요청 정보",
            required = true,
            schema = @Schema(implementation = LoginRequest.class),
            example = """
                {
                  "usernameOrEmail": "admin",
                  "password": "password123",
                  "rememberMe": true
                }
                """
        )
        @Valid @RequestBody LoginRequest loginRequest) {
        log.info("로그인 시도: {}", loginRequest.usernameOrEmail());
        
        try {
            // 사용자 인증
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.usernameOrEmail(),
                    loginRequest.password()
                )
            );
            
            // SecurityContext에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // UserPrincipal 추출
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userPrincipal.getUser();
            
            // JWT 토큰 생성
            String accessToken = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(user);
            
            // 토큰 만료 시간 계산
            long expiresIn = jwtUtils.getTokenRemainingTime(accessToken);
            
            // 마지막 로그인 시간 업데이트
            userDetailsService.updateLastLoginTime(user.getId());
            
            // 응답 생성
            LoginResponse loginResponse = LoginResponse.of(accessToken, refreshToken, expiresIn, user);
            
            log.info("로그인 성공: userId={}, username={}", user.getId(), user.getUsername());
            
            return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다", loginResponse));
            
        } catch (org.springframework.security.core.AuthenticationException e) {
            log.warn("로그인 실패: {} - {}", loginRequest.usernameOrEmail(), e.getMessage());
            throw ExceptionUtils.loginFailed(loginRequest.usernameOrEmail());
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생: {}", loginRequest.usernameOrEmail(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "로그인 처리 중 오류가 발생했습니다");
        }
    }

    /**
     * 토큰 갱신
     * 
     * @param refreshRequest 리프레시 토큰 요청
     * @return 새로운 액세스 토큰
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        log.debug("토큰 갱신 요청");
        
        try {
            String refreshToken = refreshRequest.refreshToken();
            
            // 리프레시 토큰 검증
            if (!jwtUtils.validateJwtToken(refreshToken)) {
                log.warn("유효하지 않은 리프레시 토큰");
                throw ExceptionUtils.invalidToken();
            }
            
            // 토큰에서 사용자 ID 추출
            Long userId = jwtUtils.getUserIdFromJwtToken(refreshToken);
            
            // 사용자 정보 로드
            UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserById(userId);
            User user = userPrincipal.getUser();
            
            // 새로운 토큰 생성
            String newAccessToken = jwtUtils.generateTokenFromUser(user);
            String newRefreshToken = jwtUtils.generateRefreshToken(user);
            
            // 토큰 만료 시간 계산
            long expiresIn = jwtUtils.getTokenRemainingTime(newAccessToken);
            
            // 응답 생성
            LoginResponse loginResponse = LoginResponse.of(newAccessToken, newRefreshToken, expiresIn, user);
            
            log.debug("토큰 갱신 완료: userId={}", userId);
            
            return ResponseEntity.ok(ApiResponse.success("토큰이 갱신되었습니다", loginResponse));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("토큰 갱신 중 오류 발생", e);
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "토큰 갱신에 실패했습니다");
        }
    }

    /**
     * 로그아웃
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return 로그아웃 응답
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        log.debug("로그아웃 요청");
        
        try {
            // 현재 사용자 정보 가져오기
            UserPrincipal currentUser = JwtAuthenticationFilter.getCurrentUser();
            if (currentUser != null) {
                log.info("로그아웃: userId={}, username={}", currentUser.getId(), currentUser.getUsername());
            }
            
            // Spring Security 로그아웃 처리
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);
            }
            
            // SecurityContext 정리
            SecurityContextHolder.clearContext();
            
            // TODO: 토큰 블랙리스트 처리 (Redis 등을 사용하여 토큰 무효화)
            
            return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다"));
            
        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "로그아웃 처리 중 오류가 발생했습니다");
        }
    }

    /**
     * 비밀번호 변경
     * 
     * @param changePasswordRequest 비밀번호 변경 요청
     * @return 변경 결과
     */
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        log.debug("비밀번호 변경 요청");
        
        try {
            // 현재 사용자 정보 가져오기
            UserPrincipal currentUser = JwtAuthenticationFilter.getCurrentUser();
            if (currentUser == null) {
                throw ExceptionUtils.unauthorized();
            }
            
            User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> ExceptionUtils.userNotFound(currentUser.getId()));
            
            // 현재 비밀번호 검증
            if (!passwordEncoder.matches(changePasswordRequest.currentPassword(), user.getPassword())) {
                log.warn("비밀번호 변경 실패 - 현재 비밀번호 불일치: userId={}", user.getId());
                throw new BusinessException(ErrorCode.INVALID_PASSWORD, "현재 비밀번호가 올바르지 않습니다");
            }
            
            // 새 비밀번호 암호화
            String encodedNewPassword = passwordEncoder.encode(changePasswordRequest.newPassword());
            
            // 비밀번호 업데이트
            user.setPassword(encodedNewPassword);
            user.setPasswordChangedAt(LocalDateTime.now());
            userRepository.save(user);
            
            log.info("비밀번호 변경 완료: userId={}", user.getId());
            
            return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다"));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비밀번호 변경 중 오류가 발생했습니다");
        }
    }

    /**
     * 사용자 프로필 업데이트
     * 
     * @param profileUpdate 프로필 업데이트 정보
     * @return 업데이트 결과
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<LoginResponse.UserInfo>> updateProfile(@Valid @RequestBody UserProfileUpdateDto profileUpdate) {
        log.info("사용자 프로필 업데이트 요청");
        
        try {
            // 현재 사용자 정보 가져오기
            UserPrincipal currentUser = JwtAuthenticationFilter.getCurrentUser();
            if (currentUser == null) {
                throw ExceptionUtils.unauthorized();
            }
            
            // 최신 사용자 정보 조회
            User user = userRepository.findByIdWithCompanyAndDepartment(currentUser.getId())
                .orElseThrow(() -> ExceptionUtils.userNotFound(currentUser.getId()));
            
            // 프로필 정보 업데이트
            if (profileUpdate.fullName() != null && !profileUpdate.fullName().trim().isEmpty()) {
                user.setFullName(profileUpdate.fullName().trim());
            }
            
            if (profileUpdate.email() != null && !profileUpdate.email().trim().isEmpty()) {
                // 이메일 중복 확인 (본인 제외)
                if (userRepository.existsByEmailAndIdNot(profileUpdate.email(), user.getId())) {
                    throw new BusinessException(ErrorCode.DUPLICATE_EMAIL, "이미 사용 중인 이메일입니다");
                }
                user.setEmail(profileUpdate.email().trim());
            }
            
            if (profileUpdate.phone() != null && !profileUpdate.phone().trim().isEmpty()) {
                String normalizedPhone = normalizePhoneNumber(profileUpdate.phone().trim());
                log.info("유선전화번호 업데이트: {} -> {} (정규화: {})", user.getPhone(), profileUpdate.phone().trim(), normalizedPhone);
                user.setPhone(normalizedPhone);
            }
            
            if (profileUpdate.phoneNumber() != null && !profileUpdate.phoneNumber().trim().isEmpty()) {
                String normalizedPhoneNumber = normalizePhoneNumber(profileUpdate.phoneNumber().trim());
                log.info("전화번호 업데이트: {} -> {} (정규화: {})", user.getPhoneNumber(), profileUpdate.phoneNumber().trim(), normalizedPhoneNumber);
                user.setPhoneNumber(normalizedPhoneNumber);
            }
            
            // 부서 정보 업데이트 (부서명으로 검색)
            if (profileUpdate.department() != null && !profileUpdate.department().trim().isEmpty()) {
                departmentRepository.findByName(profileUpdate.department().trim())
                    .ifPresent(user::setDepartment);
            }
            
            // 직책 정보 업데이트
            if (profileUpdate.position() != null && !profileUpdate.position().trim().isEmpty()) {
                user.setPosition(profileUpdate.position().trim());
            }
            
            // 사용자 정보 저장
            User savedUser = userRepository.save(user);
            
            log.info("사용자 프로필 업데이트 완료: userId={}", savedUser.getId());
            
            // 업데이트된 사용자 정보 반환
            LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.from(savedUser);
            return ResponseEntity.ok(ApiResponse.success("프로필이 성공적으로 업데이트되었습니다", userInfo));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("프로필 업데이트 중 오류 발생", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "프로필 업데이트 중 오류가 발생했습니다");
        }
    }

    /**
     * 전화번호 정규화 (하이픈 추가)
     * 지원 형식:
     * - 휴대폰: 010-1234-5678, 011-123-4567, 016-123-4567, 017-123-4567, 018-123-4567, 019-123-4567
     * - 지역번호: 02-123-4567, 02-1234-5678, 031-123-4567, 031-1234-5678, 032-123-4567, 033-123-4567, 041-123-4567, 042-123-4567, 043-123-4567, 044-123-4567, 051-123-4567, 052-123-4567, 053-123-4567, 054-123-4567, 055-123-4567, 061-123-4567, 062-123-4567, 063-123-4567, 064-123-4567
     * - 특수번호: 1588-1234, 1577-1234, 1566-1234, 1544-1234, 1599-1234, 1600-1234, 1800-1234
     */
    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return phoneNumber;
        }
        
        // 하이픈 제거
        String digitsOnly = phoneNumber.replaceAll("-", "");
        
        // 이미 하이픈이 있는 경우 그대로 반환
        if (phoneNumber.contains("-")) {
            return phoneNumber;
        }
        
        // 11자리인 경우 (휴대폰: 01012345678)
        if (digitsOnly.length() == 11) {
            String prefix = digitsOnly.substring(0, 3);
            // 휴대폰 번호 (010, 011, 016, 017, 018, 019)
            if (prefix.matches("01[016789]")) {
                return digitsOnly.substring(0, 3) + "-" + digitsOnly.substring(3, 7) + "-" + digitsOnly.substring(7);
            }
        }
        
        // 10자리인 경우 (지역번호)
        if (digitsOnly.length() == 10) {
            String prefix = digitsOnly.substring(0, 2);
            String prefix3 = digitsOnly.substring(0, 3);
            
            // 02 (서울) - 8자리 국번 (02-2345-6789)
            if ("02".equals(prefix)) {
                return digitsOnly.substring(0, 2) + "-" + digitsOnly.substring(2, 6) + "-" + digitsOnly.substring(6);
            }
            // 3자리 지역번호 (031, 032, 033, 041, 042, 043, 044, 051, 052, 053, 054, 055, 061, 062, 063, 064)
            else if (prefix3.matches("03[123]|04[1234]|05[12345]|06[1234]")) {
                return digitsOnly.substring(0, 3) + "-" + digitsOnly.substring(3, 6) + "-" + digitsOnly.substring(6);
            }
            // 2자리 지역번호 (기타)
            else {
                return digitsOnly.substring(0, 2) + "-" + digitsOnly.substring(2, 5) + "-" + digitsOnly.substring(5);
            }
        }
        
        // 8자리인 경우 (특수번호: 15881234)
        if (digitsOnly.length() == 8) {
            String prefix = digitsOnly.substring(0, 4);
            // 특수번호 (1588, 1577, 1566, 1544, 1599, 1600, 1800)
            if (prefix.matches("1588|1577|1566|1544|1599|1600|1800")) {
                return digitsOnly.substring(0, 4) + "-" + digitsOnly.substring(4);
            }
        }
        
        // 그 외의 경우 원본 반환
        return phoneNumber;
    }

    /**
     * 현재 사용자 정보 조회
     * 
     * @return 현재 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<LoginResponse.UserInfo>> getCurrentUser() {
        log.info("현재 사용자 정보 조회 요청");
        
        try {
            // 현재 사용자 정보 가져오기
            UserPrincipal currentUser = JwtAuthenticationFilter.getCurrentUser();
            if (currentUser == null) {
                log.warn("인증되지 않은 사용자가 /me 엔드포인트에 접근 시도");
                throw ExceptionUtils.unauthorized();
            }
            
            // 최신 사용자 정보 조회 (회사 및 부서 정보 포함)
            User user = userRepository.findByIdWithCompanyAndDepartment(currentUser.getId())
                .orElseThrow(() -> ExceptionUtils.userNotFound(currentUser.getId()));
            
            // DepartmentInfo 생성 과정 상세 로그
            if (user.getDepartment() != null) {
                log.info("Department 엔티티 정보: id={}, name={}, departmentCode={}", 
                        user.getDepartment().getId(), 
                        user.getDepartment().getName(), 
                        user.getDepartment().getDepartmentCode());
                
                // DepartmentInfo.from() 호출 전후 로그
                try {
                    LoginResponse.DepartmentInfo deptInfo = LoginResponse.DepartmentInfo.from(user.getDepartment());
                    log.info("DepartmentInfo 생성 성공: id={}, name={}, departmentCode={}", 
                            deptInfo.id(), deptInfo.name(), deptInfo.departmentCode());
                } catch (Exception e) {
                    log.error("DepartmentInfo 생성 실패: {}", e.getMessage(), e);
                }
            } else {
                log.info("사용자의 department가 null입니다");
            }
            
            // 사용자 정보 응답 생성
            LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.from(user);
            
            log.info("사용자 정보 조회 성공: userId={}, username={}, department={}, position={}", 
                    user.getId(), user.getUsername(), 
                    user.getDepartment() != null ? user.getDepartment().getName() : "null",
                    user.getPosition());
            
            // 반환할 UserInfo 상세 로그
            log.info("반환할 UserInfo: id={}, username={}, email={}, fullName={}, role={}, position={}, department={}", 
                    userInfo.id(), userInfo.username(), userInfo.email(), userInfo.fullName(), 
                    userInfo.role(), userInfo.position(),
                    userInfo.department() != null ? userInfo.department().name() : "null");
            
            return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 완료", userInfo));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 정보 조회 중 오류가 발생했습니다");
        }
    }

    /**
     * 토큰 유효성 검증
     * 
     * @param request HTTP 요청
     * @return 토큰 유효성 결과
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validateToken(HttpServletRequest request) {
        log.debug("토큰 유효성 검증 요청");
        
        try {
            // Authorization 헤더에서 토큰 추출
            String authHeader = request.getHeader("Authorization");
            String token = jwtUtils.parseJwtFromAuthHeader(authHeader);
            
            if (token == null) {
                return ResponseEntity.ok(ApiResponse.success("토큰 유효성 검증 완료", 
                    new TokenValidationResponse(false, "토큰이 없습니다", null, null)));
            }
            
            // 토큰 유효성 검증
            boolean isValid = jwtUtils.validateJwtToken(token);
            
            if (isValid) {
                Long userId = jwtUtils.getUserIdFromJwtToken(token);
                String username = jwtUtils.getUsernameFromJwtToken(token);
                Long remainingTime = jwtUtils.getTokenRemainingTime(token);
                
                return ResponseEntity.ok(ApiResponse.success("토큰 유효성 검증 완료", 
                    new TokenValidationResponse(true, "유효한 토큰입니다", remainingTime, userId)));
            } else {
                return ResponseEntity.ok(ApiResponse.success("토큰 유효성 검증 완료", 
                    new TokenValidationResponse(false, "유효하지 않은 토큰입니다", null, null)));
            }
            
        } catch (Exception e) {
            log.error("토큰 유효성 검증 중 오류 발생", e);
            return ResponseEntity.ok(ApiResponse.success("토큰 유효성 검증 완료", 
                new TokenValidationResponse(false, "토큰 검증 중 오류가 발생했습니다", null, null)));
        }
    }

    /**
     * 토큰 유효성 검증 응답 DTO
     */
    public record TokenValidationResponse(
            boolean valid,
            String message,
            Long remainingTime,
            Long userId
    ) {}

    /**
     * 계정 잠금 해제 (관리자 전용)
     * 
     * @param userId 사용자 ID
     * @return 잠금 해제 결과
     */
    @PutMapping("/unlock/{userId}")
    public ResponseEntity<ApiResponse<Void>> unlockAccount(@PathVariable Long userId) {
        log.info("계정 잠금 해제 요청: userId={}", userId);
        
        try {
            // 현재 사용자가 관리자인지 확인
            UserPrincipal currentUser = JwtAuthenticationFilter.getCurrentUser();
            if (currentUser == null || !currentUser.isAdmin()) {
                throw ExceptionUtils.adminRequired();
            }
            
            // 대상 사용자 조회
            User user = userRepository.findById(userId)
                .orElseThrow(() -> ExceptionUtils.userNotFound(userId));
            
            // 계정 잠금 해제
            user.setIsLocked(false);
            userRepository.save(user);
            
            log.info("계정 잠금 해제 완료: userId={}, adminId={}", userId, currentUser.getId());
            
            return ResponseEntity.ok(ApiResponse.success("계정 잠금이 해제되었습니다"));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("계정 잠금 해제 중 오류 발생: userId={}", userId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "계정 잠금 해제 중 오류가 발생했습니다");
        }
    }

    /**
     * 사용자 활성화/비활성화 (관리자 또는 HR팀 매니저 전용)
     * 
     * @param userId 사용자 ID
     * @param isActive 활성화 여부
     * @return 변경 결과
     */
    @PutMapping("/activate/{userId}")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long userId, 
                                                         @RequestParam boolean isActive) {
        log.info("사용자 활성화 상태 변경 요청: userId={}, isActive={}", userId, isActive);
        
        try {
            // 현재 사용자가 관리자 또는 HR팀 매니저인지 확인
            UserPrincipal currentUser = JwtAuthenticationFilter.getCurrentUser();
            if (currentUser == null || !currentUser.hasUserManagementPermission()) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_PERMISSION, "사용자 계정을 관리할 권한이 없습니다");
            }
            
            // 대상 사용자 조회
            User user = userRepository.findById(userId)
                .orElseThrow(() -> ExceptionUtils.userNotFound(userId));
            
            // 자기 자신은 비활성화할 수 없음
            if (user.getId().equals(currentUser.getId()) && !isActive) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "자기 자신을 비활성화할 수 없습니다");
            }
            
            // 활성화 상태 변경
            user.setIsActive(isActive);
            userRepository.save(user);
            
            String action = isActive ? "활성화" : "비활성화";
            log.info("사용자 {} 완료: userId={}, adminId={}", action, userId, currentUser.getId());
            
            return ResponseEntity.ok(ApiResponse.success("사용자가 " + action + "되었습니다"));
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("사용자 활성화 상태 변경 중 오류 발생: userId={}", userId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 활성화 상태 변경 중 오류가 발생했습니다");
        }
    }

    /**
     * 디버깅용: admin 계정 정보 확인
     * 개발 환경에서만 사용
     */
    @GetMapping("/debug/admin-info")
    public ResponseEntity<ApiResponse<Object>> getAdminInfo() {
        log.info("디버깅: admin 계정 정보 확인 요청");
        
        try {
            // admin 계정 조회
            var adminUser = userRepository.findByUsernameWithCompanyAndDepartment("admin");
            
            if (adminUser.isPresent()) {
                User user = adminUser.get();
                var adminInfo = new java.util.HashMap<String, Object>();
                adminInfo.put("id", user.getId());
                adminInfo.put("username", user.getUsername());
                adminInfo.put("email", user.getEmail());
                adminInfo.put("fullName", user.getFullName());
                adminInfo.put("role", user.getRole());
                adminInfo.put("isActive", user.getIsActive());
                adminInfo.put("isLocked", user.getIsLocked());
                adminInfo.put("isDeleted", user.getIsDeleted());
                adminInfo.put("isPasswordExpired", user.getIsPasswordExpired());
                adminInfo.put("companyId", user.getCompany() != null ? user.getCompany().getId() : null);
                adminInfo.put("companyName", user.getCompany() != null ? user.getCompany().getName() : null);
                adminInfo.put("departmentId", user.getDepartment() != null ? user.getDepartment().getId() : null);
                adminInfo.put("departmentName", user.getDepartment() != null ? user.getDepartment().getName() : null);
                adminInfo.put("createdAt", user.getCreatedAt());
                adminInfo.put("lastLoginAt", user.getLastLoginAt());
                
                // 비밀번호 검증 테스트
                boolean adminPasswordMatches = passwordEncoder.matches("admin123", user.getPassword());
                boolean userPasswordMatches = passwordEncoder.matches("user123", user.getPassword());
                adminInfo.put("adminPasswordMatches", adminPasswordMatches);
                adminInfo.put("userPasswordMatches", userPasswordMatches);
                
                log.info("✅ admin 계정 정보 확인 완료: {}", adminInfo);
                return ResponseEntity.ok(ApiResponse.success("admin 계정 정보 조회 완료", adminInfo));
            } else {
                log.warn("❌ admin 계정을 찾을 수 없습니다");
                return ResponseEntity.ok(ApiResponse.success("admin 계정을 찾을 수 없습니다", null));
            }
            
        } catch (Exception e) {
            log.error("admin 계정 정보 확인 중 오류 발생", e);
            return ResponseEntity.ok(ApiResponse.success("admin 계정 정보 확인 중 오류 발생: " + e.getMessage(), null));
        }
    }

    /**
     * 디버깅용: 로그인 테스트
     * 개발 환경에서만 사용
     */
    @PostMapping("/debug/test-login")
    public ResponseEntity<ApiResponse<Object>> testLogin(@RequestBody LoginRequest loginRequest) {
        log.info("디버깅: 로그인 테스트 요청 - username: {}", loginRequest.usernameOrEmail());
        
        try {
            // 1. 사용자 조회 테스트
            var user = userRepository.findByUsernameWithCompanyAndDepartment(loginRequest.usernameOrEmail());
            if (user.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("사용자를 찾을 수 없습니다", null));
            }
            
            User foundUser = user.get();
            log.info("✅ 사용자 조회 성공: {}", foundUser.getUsername());
            
            // 2. 비밀번호 검증 테스트
            boolean passwordMatches = passwordEncoder.matches(loginRequest.password(), foundUser.getPassword());
            log.info("🔐 비밀번호 검증 결과: {}", passwordMatches);
            
            // 3. 계정 상태 확인
            var statusInfo = new java.util.HashMap<String, Object>();
            statusInfo.put("isActive", foundUser.getIsActive());
            statusInfo.put("isLocked", foundUser.getIsLocked());
            statusInfo.put("isDeleted", foundUser.getIsDeleted());
            statusInfo.put("isPasswordExpired", foundUser.getIsPasswordExpired());
            statusInfo.put("passwordMatches", passwordMatches);
            
            // 4. 인증 테스트
            try {
                Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        loginRequest.usernameOrEmail(),
                        loginRequest.password()
                    )
                );
                statusInfo.put("authenticationSuccess", true);
                statusInfo.put("authorities", authentication.getAuthorities());
                log.info("✅ 인증 성공");
            } catch (Exception e) {
                statusInfo.put("authenticationSuccess", false);
                statusInfo.put("authenticationError", e.getMessage());
                log.warn("❌ 인증 실패: {}", e.getMessage());
            }
            
            return ResponseEntity.ok(ApiResponse.success("로그인 테스트 완료", statusInfo));
            
        } catch (Exception e) {
            log.error("로그인 테스트 중 오류 발생", e);
            return ResponseEntity.ok(ApiResponse.success("로그인 테스트 중 오류 발생: " + e.getMessage(), null));
        }
    }
}
