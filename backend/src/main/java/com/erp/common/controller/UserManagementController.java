package com.erp.common.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.common.dto.PageResponse;
import com.erp.common.dto.UserDto;
import com.erp.common.dto.CompanyDto;
import com.erp.common.dto.DepartmentDto;
import com.erp.common.entity.User;
import com.erp.common.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 관리 컨트롤러
 * ADMIN 이상 권한 사용자가 시스템 사용자를 관리합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public class UserManagementController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 전체 사용자 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserDto>>> getAllUsers(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        try {
            Page<User> users = userRepository.findAllWithCompanyAndDepartment(pageable);

            List<UserDto> userDTOs = users.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            PageResponse<UserDto> pageResponse = PageResponse.<UserDto>builder()
                    .content(userDTOs)
                    .page(users.getNumber())
                    .size(users.getSize())
                    .totalElements(users.getTotalElements())
                    .totalPages(users.getTotalPages())
                    .first(users.isFirst())
                    .last(users.isLast())
                    .build();

            return ResponseEntity.ok(ApiResponse.success(pageResponse));
        } catch (Exception e) {
            log.error("사용자 목록 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("사용자 목록을 조회할 수 없습니다", null));
        }
    }

    /**
     * 사용자 검색 (페이징)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<UserDto>>> searchUsers(
            @RequestParam String query,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        try {
            Page<User> users = userRepository.searchByUsernameOrFullName(query, pageable);

            List<UserDto> userDTOs = users.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            PageResponse<UserDto> pageResponse = PageResponse.<UserDto>builder()
                    .content(userDTOs)
                    .page(users.getNumber())
                    .size(users.getSize())
                    .totalElements(users.getTotalElements())
                    .totalPages(users.getTotalPages())
                    .first(users.isFirst())
                    .last(users.isLast())
                    .build();

            return ResponseEntity.ok(ApiResponse.success(pageResponse));
        } catch (Exception e) {
            log.error("사용자 검색 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("사용자를 검색할 수 없습니다", null));
        }
    }

    /**
     * 특정 사용자 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long userId) {
        try {
            User user = userRepository.findByIdWithCompanyAndDepartment(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            return ResponseEntity.ok(ApiResponse.success(convertToDTO(user)));
        } catch (IllegalArgumentException e) {
            log.warn("사용자 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("사용자 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("사용자를 조회할 수 없습니다", null));
        }
    }

    /**
     * 역할별 사용자 조회
     */
    @GetMapping("/by-role/{role}")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsersByRole(@PathVariable User.UserRole role) {
        try {
            List<User> users = userRepository.findByRole(role);

            List<UserDto> userDTOs = users.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(userDTOs));
        } catch (Exception e) {
            log.error("역할별 사용자 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("역할별 사용자를 조회할 수 없습니다", null));
        }
    }

    /**
     * 사용자 계정 활성화/비활성화
     */
    @PatchMapping("/{userId}/active")
    public ResponseEntity<ApiResponse<UserDto>> toggleUserActiveStatus(
            @PathVariable Long userId,
            @RequestParam Boolean isActive) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            user.setIsActive(isActive);
            User updatedUser = userRepository.save(user);

            log.info("사용자 계정 상태 변경: userId={}, isActive={}", userId, isActive);
            return ResponseEntity.ok(ApiResponse.success(convertToDTO(updatedUser)));
        } catch (IllegalArgumentException e) {
            log.warn("사용자 상태 변경 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("사용자 상태 변경 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("사용자 상태를 변경할 수 없습니다", null));
        }
    }

    /**
     * 사용자 계정 잠금/잠금해제
     */
    @PatchMapping("/{userId}/lock")
    public ResponseEntity<ApiResponse<UserDto>> toggleUserLockStatus(
            @PathVariable Long userId,
            @RequestParam Boolean isLocked) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            user.setIsLocked(isLocked);
            User updatedUser = userRepository.save(user);

            log.info("사용자 계정 잠금 상태 변경: userId={}, isLocked={}", userId, isLocked);
            return ResponseEntity.ok(ApiResponse.success(convertToDTO(updatedUser)));
        } catch (IllegalArgumentException e) {
            log.warn("사용자 잠금 상태 변경 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("사용자 잠금 상태 변경 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("사용자 잠금 상태를 변경할 수 없습니다", null));
        }
    }

    /**
     * 사용자 역할 변경
     */
    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateUserRole(
            @PathVariable Long userId,
            @RequestParam User.UserRole role) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            user.setRole(role);
            User updatedUser = userRepository.save(user);

            log.info("사용자 역할 변경: userId={}, newRole={}", userId, role);
            return ResponseEntity.ok(ApiResponse.success(convertToDTO(updatedUser)));
        } catch (IllegalArgumentException e) {
            log.warn("사용자 역할 변경 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("사용자 역할 변경 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("사용자 역할을 변경할 수 없습니다", null));
        }
    }

    /**
     * 사용자 비밀번호 재설정
     */
    @PatchMapping("/{userId}/reset-password")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> resetUserPassword(
            @PathVariable Long userId,
            @RequestParam String newPassword) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setPasswordChangedAt(LocalDateTime.now());
            user.setIsPasswordExpired(true); // 다음 로그인 시 비밀번호 변경 강제
            userRepository.save(user);

            log.info("사용자 비밀번호 재설정: userId={}", userId);
            return ResponseEntity.ok(ApiResponse.success("비밀번호가 재설정되었습니다"));
        } catch (IllegalArgumentException e) {
            log.warn("비밀번호 재설정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("비밀번호 재설정 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("비밀번호를 재설정할 수 없습니다", null));
        }
    }

    /**
     * 사용자 정보 수정
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserDto.UserUpdateDto updateDto) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            // 수정 가능한 필드만 업데이트
            if (updateDto.fullName() != null) {
                user.setFullName(updateDto.fullName());
            }
            if (updateDto.email() != null) {
                // 이메일 중복 확인
                if (userRepository.existsByEmailAndIdNot(updateDto.email(), userId)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.error("이미 사용 중인 이메일입니다", null));
                }
                user.setEmail(updateDto.email());
            }
            if (updateDto.phone() != null) {
                user.setPhone(updateDto.phone());
            }
            if (updateDto.phoneNumber() != null) {
                user.setPhoneNumber(updateDto.phoneNumber());
            }
            if (updateDto.position() != null) {
                user.setPosition(updateDto.position());
            }

            User updatedUser = userRepository.save(user);
            log.info("사용자 정보 수정: userId={}", userId);

            return ResponseEntity.ok(ApiResponse.success(convertToDTO(updatedUser)));
        } catch (IllegalArgumentException e) {
            log.warn("사용자 정보 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("사용자 정보 수정 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("사용자 정보를 수정할 수 없습니다", null));
        }
    }

    /**
     * 사용자 삭제 (논리 삭제)
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            user.setIsDeleted(true);
            user.setIsActive(false);
            userRepository.save(user);

            log.info("사용자 삭제: userId={}", userId);
            return ResponseEntity.ok(ApiResponse.success("사용자가 삭제되었습니다"));
        } catch (IllegalArgumentException e) {
            log.warn("사용자 삭제 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage(), null));
        } catch (Exception e) {
            log.error("사용자 삭제 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("사용자를 삭제할 수 없습니다", null));
        }
    }

    /**
     * User 엔티티를 DTO로 변환
     */
    private UserDto convertToDTO(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(),
                user.getIsActive(),
                user.getIsLocked(),
                user.getIsPasswordExpired(),
                user.getCompany() != null ? CompanyDto.from(user.getCompany()) : null,
                user.getDepartment() != null ? DepartmentDto.from(user.getDepartment()) : null,
                user.getLastLoginAt(),
                user.getPasswordChangedAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
