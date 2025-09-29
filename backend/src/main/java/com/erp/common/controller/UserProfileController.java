package com.erp.common.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.common.dto.UserProfileDto;
import com.erp.common.dto.UserProfileUpdateDto;
import com.erp.common.dto.PasswordChangeDto;
import com.erp.common.entity.User;
import com.erp.common.repository.UserRepository;
import com.erp.hr.entity.Department;
import com.erp.hr.repository.DepartmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자 프로필", description = "사용자 프로필 관리 API")
public class UserProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;

    /**
     * 현재 사용자 정보 조회
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "현재 사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentUser(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("현재 사용자 정보 조회: username={}", username);
            
            User user = userRepository.findByUsernameWithCompanyAndDepartment(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
            
            log.info("조회된 사용자 정보: userId={}, username={}, department={}, position={}", 
                    user.getId(), user.getUsername(), 
                    user.getDepartment() != null ? user.getDepartment().getName() : "null",
                    user.getPosition());
            
            UserProfileDto userProfile = UserProfileDto.from(user);
            log.info("변환된 프로필 정보: department={}, position={}", 
                    userProfile.department(), userProfile.position());
            
            return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 완료", userProfile));
        } catch (Exception e) {
            log.error("사용자 정보 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("사용자 정보 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 사용자 프로필 업데이트
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Transactional
    @Operation(summary = "사용자 프로필 업데이트", description = "현재 사용자의 프로필 정보를 업데이트합니다")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            @Valid @RequestBody UserProfileUpdateDto updateDto,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("사용자 프로필 업데이트 요청 시작: username={}", username);
                   log.info("업데이트 데이터: fullName={}, email={}, phone={}, phoneNumber={}, department={}, position={}", 
                           updateDto.fullName(), updateDto.email(), updateDto.phone(), updateDto.phoneNumber(), 
                           updateDto.department(), updateDto.position());
            log.info("UserProfileController.updateProfile 호출됨 - PUT /api/users/profile");
            
            User user = userRepository.findByUsernameWithCompanyAndDepartment(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
            
            // 업데이트할 필드들 처리
            if (updateDto.fullName() != null && !updateDto.fullName().trim().isEmpty()) {
                user.setFullName(updateDto.fullName().trim());
            }
                   if (updateDto.email() != null && !updateDto.email().trim().isEmpty()) {
                       user.setEmail(updateDto.email().trim());
                   }
                   if (updateDto.phone() != null && !updateDto.phone().trim().isEmpty()) {
                       String normalizedPhone = normalizePhoneNumber(updateDto.phone().trim());
                       log.info("유선전화번호 업데이트: {} -> {} (정규화: {})", user.getPhone(), updateDto.phone().trim(), normalizedPhone);
                       user.setPhone(normalizedPhone);
                       log.info("유선전화번호 업데이트 완료: {}", user.getPhone());
                   } else {
                       log.info("유선전화번호 업데이트 건너뜀: phone={}", updateDto.phone());
                   }
                   if (updateDto.phoneNumber() != null && !updateDto.phoneNumber().trim().isEmpty()) {
                String normalizedPhoneNumber = normalizePhoneNumber(updateDto.phoneNumber().trim());
                log.info("전화번호 업데이트: {} -> {} (정규화: {})", user.getPhoneNumber(), updateDto.phoneNumber().trim(), normalizedPhoneNumber);
                user.setPhoneNumber(normalizedPhoneNumber);
                log.info("전화번호 업데이트 완료: {}", user.getPhoneNumber());
            } else {
                log.info("전화번호 업데이트 건너뜀: phoneNumber={}", updateDto.phoneNumber());
            }
            if (updateDto.department() != null && !updateDto.department().trim().isEmpty()) {
                // 부서명으로 Department 엔티티 조회
                Optional<Department> departmentOpt = departmentRepository.findByName(updateDto.department().trim());
                if (departmentOpt.isPresent()) {
                    user.setDepartment(departmentOpt.get());
                    log.info("Department 업데이트 완료: {}", updateDto.department());
                } else {
                    log.warn("Department를 찾을 수 없습니다: {}", updateDto.department());
                }
            }
            if (updateDto.position() != null && !updateDto.position().trim().isEmpty()) {
                user.setPosition(updateDto.position().trim());
            }
            
            User savedUser = userRepository.save(user);
            UserProfileDto userProfile = UserProfileDto.from(savedUser);
            
            log.info("사용자 프로필 업데이트 완료: userId={}", savedUser.getId());
            log.info("반환할 프로필 데이터: id={}, fullName={}, email={}, department={}, position={}", 
                    userProfile.id(), userProfile.fullName(), userProfile.email(), 
                    userProfile.department(), userProfile.position());
            
            ApiResponse<UserProfileDto> apiResponse = ApiResponse.success("프로필 업데이트 완료", userProfile);
            log.info("ApiResponse 생성 완료: success={}, message={}, data={}", 
                    apiResponse.isSuccess(), apiResponse.getMessage(), 
                    apiResponse.getData() != null ? "데이터 있음" : "데이터 없음");
            if (apiResponse.getData() != null) {
                log.info("ApiResponse data 상세: id={}, department={}, position={}", 
                        apiResponse.getData().id(), 
                        apiResponse.getData().department(), 
                        apiResponse.getData().position());
            }
            
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            log.error("사용자 프로필 업데이트 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("프로필 업데이트에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 비밀번호 변경
     */
    @PutMapping("/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "비밀번호 변경", description = "현재 사용자의 비밀번호를 변경합니다")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody PasswordChangeDto passwordData,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            String currentPassword = passwordData.currentPassword();
            String newPassword = passwordData.newPassword();
            
            log.info("비밀번호 변경 요청: username={}", username);
            
            User user = userRepository.findByUsernameWithCompanyAndDepartment(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
            
            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("현재 비밀번호가 올바르지 않습니다"));
            }
            
            // 새 비밀번호로 변경
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            log.info("비밀번호 변경 완료: userId={}", user.getId());
            return ResponseEntity.ok(ApiResponse.success("비밀번호 변경 완료"));
        } catch (Exception e) {
            log.error("비밀번호 변경 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("비밀번호 변경에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 계정 비활성화
     */
    @PutMapping("/deactivate")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "계정 비활성화", description = "현재 사용자의 계정을 비활성화합니다")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("계정 비활성화 요청: username={}", username);
            
            User user = userRepository.findByUsernameWithCompanyAndDepartment(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
            
            user.setIsActive(false);
            userRepository.save(user);
            
            log.info("계정 비활성화 완료: userId={}", user.getId());
            return ResponseEntity.ok(ApiResponse.success("계정이 비활성화되었습니다"));
        } catch (Exception e) {
            log.error("계정 비활성화 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("계정 비활성화에 실패했습니다: " + e.getMessage()));
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
}
