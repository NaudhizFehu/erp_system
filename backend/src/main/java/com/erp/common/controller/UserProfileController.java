package com.erp.common.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.common.dto.UserProfileDto;
import com.erp.common.entity.User;
import com.erp.common.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자 프로필", description = "사용자 프로필 관리 API")
public class UserProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
            
            UserProfileDto userProfile = UserProfileDto.from(user);
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
    @Operation(summary = "사용자 프로필 업데이트", description = "현재 사용자의 프로필 정보를 업데이트합니다")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            @Valid @RequestBody Map<String, String> updateData,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("사용자 프로필 업데이트: username={}", username);
            
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
            
            // 업데이트할 필드들 처리
            if (updateData.containsKey("fullName")) {
                user.setFullName(updateData.get("fullName"));
            }
            if (updateData.containsKey("email")) {
                user.setEmail(updateData.get("email"));
            }
            if (updateData.containsKey("phoneNumber")) {
                user.setPhoneNumber(updateData.get("phoneNumber"));
            }
            // department는 Department 엔티티이므로 문자열로는 설정할 수 없음
            // TODO: department ID로 Department 엔티티를 찾아서 설정하는 로직 필요
            if (updateData.containsKey("position")) {
                user.setPosition(updateData.get("position"));
            }
            
            User savedUser = userRepository.save(user);
            UserProfileDto userProfile = UserProfileDto.from(savedUser);
            
            log.info("사용자 프로필 업데이트 완료: userId={}", savedUser.getId());
            return ResponseEntity.ok(ApiResponse.success("프로필 업데이트 완료", userProfile));
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
            @Valid @RequestBody Map<String, String> passwordData,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");
            
            log.info("비밀번호 변경 요청: username={}", username);
            
            User user = userRepository.findByUsername(username)
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
            
            User user = userRepository.findByUsername(username)
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
}
