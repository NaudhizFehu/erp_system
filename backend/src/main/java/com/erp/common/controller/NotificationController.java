package com.erp.common.controller;

import com.erp.common.dto.ApiResponse;
import com.erp.common.dto.NotificationDto;
import com.erp.common.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 알림 관리 REST Controller
 * 알림 조회, 읽음 처리, 삭제 기능을 제공합니다
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림 관리", description = "알림 정보 관리 API")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 사용자의 알림 목록 조회 (페이징)
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 조회합니다")
    public ResponseEntity<ApiResponse<Page<NotificationDto>>> getNotifications(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            Long userId = getCurrentUserId(authentication);
            log.info("알림 목록 조회 요청: userId={}, page={}, size={}", 
                    userId, pageable.getPageNumber(), pageable.getPageSize());
            
            Page<NotificationDto> notifications = notificationService.getUserNotifications(userId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success("알림 목록 조회 완료", notifications));
        } catch (Exception e) {
            log.error("알림 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("알림 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 읽지 않은 알림 개수 조회
     */
    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "읽지 않은 알림 개수", description = "읽지 않은 알림의 개수를 조회합니다")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            log.info("읽지 않은 알림 개수 조회: userId={}", userId);
            
            long unreadCount = notificationService.getUnreadCount(userId);
            
            return ResponseEntity.ok(ApiResponse.success("읽지 않은 알림 개수 조회 완료", unreadCount));
        } catch (Exception e) {
            log.error("읽지 않은 알림 개수 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("읽지 않은 알림 개수 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 최근 3일 이내 알림 목록 조회
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "최근 3일 이내 알림 목록", description = "최근 3일 이내의 모든 알림(읽은/읽지 않은) 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getRecentNotifications(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            log.info("최근 3일 이내 알림 목록 조회: userId={}", userId);
            
            List<NotificationDto> notifications = notificationService.getRecentNotifications(userId);
            
            return ResponseEntity.ok(ApiResponse.success("최근 3일 이내 알림 목록 조회 완료", notifications));
        } catch (Exception e) {
            log.error("최근 3일 이내 알림 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("최근 3일 이내 알림 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 최근 2주 이내 모든 알림 목록 조회
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "최근 2주 이내 모든 알림 목록", description = "최근 2주 이내의 모든 알림(읽은/읽지 않은) 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getAllNotifications(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            log.info("최근 2주 이내 모든 알림 목록 조회: userId={}", userId);
            
            List<NotificationDto> notifications = notificationService.getAllNotifications(userId);
            
            return ResponseEntity.ok(ApiResponse.success("최근 2주 이내 모든 알림 목록 조회 완료", notifications));
        } catch (Exception e) {
            log.error("최근 2주 이내 모든 알림 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("최근 2주 이내 모든 알림 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 읽지 않은 알림 목록 조회
     */
    @GetMapping("/unread")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "읽지 않은 알림 목록", description = "읽지 않은 알림 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUnreadNotifications(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            log.info("읽지 않은 알림 목록 조회: userId={}", userId);
            
            List<NotificationDto> notifications = notificationService.getUnreadNotifications(userId);
            
            return ResponseEntity.ok(ApiResponse.success("읽지 않은 알림 목록 조회 완료", notifications));
        } catch (Exception e) {
            log.error("읽지 않은 알림 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("읽지 않은 알림 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 특정 알림을 읽음 처리
     */
    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @Parameter(description = "알림 ID") @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            log.info("알림 읽음 처리: notificationId={}, userId={}", id, userId);
            
            notificationService.markAsRead(id, userId);
            
            return ResponseEntity.ok(ApiResponse.success("알림 읽음 처리 완료", null));
        } catch (Exception e) {
            log.error("알림 읽음 처리 실패: notificationId={}, {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("알림 읽음 처리에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 모든 알림을 읽음 처리
     */
    @PutMapping("/read-all")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림을 읽음 처리합니다")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            log.info("모든 알림 읽음 처리: userId={}", userId);
            
            notificationService.markAllAsRead(userId);
            
            return ResponseEntity.ok(ApiResponse.success("모든 알림 읽음 처리 완료", null));
        } catch (Exception e) {
            log.error("모든 알림 읽음 처리 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("모든 알림 읽음 처리에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 알림 삭제
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @Parameter(description = "알림 ID") @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            log.info("알림 삭제: notificationId={}, userId={}", id, userId);
            
            notificationService.deleteNotification(id, userId);
            
            return ResponseEntity.ok(ApiResponse.success("알림 삭제 완료", null));
        } catch (Exception e) {
            log.error("알림 삭제 실패: notificationId={}, {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("알림 삭제에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 테스트 알림 생성 (개발용)
     */
    @PostMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "테스트 알림 생성", description = "개발/테스트용 알림을 생성합니다")
    public ResponseEntity<ApiResponse<NotificationDto>> createTestNotification(
            @RequestBody TestNotificationRequest request,
            Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            log.info("테스트 알림 생성: userId={}, type={}", userId, request.type());
            
            // 임시로 admin 사용자 객체 생성
            com.erp.common.entity.User user = new com.erp.common.entity.User();
            user.setId(userId);
            
            NotificationDto notification = notificationService.createNotification(
                user,
                request.title(),
                request.message(),
                request.type(),
                request.actionUrl()
            );
            
            return ResponseEntity.ok(ApiResponse.success("테스트 알림 생성 완료", notification));
        } catch (Exception e) {
            log.error("테스트 알림 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("테스트 알림 생성에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 테스트 알림 요청 DTO
     */
    public record TestNotificationRequest(
            String title,
            String message,
            com.erp.common.entity.Notification.NotificationType type,
            String actionUrl
    ) {}

    /**
     * 현재 사용자 ID 추출
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("인증 정보가 없습니다");
        }
        
        // 실제 인증된 사용자 ID 반환
        if (authentication.getPrincipal() instanceof com.erp.common.security.UserPrincipal) {
            com.erp.common.security.UserPrincipal userPrincipal = 
                (com.erp.common.security.UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getId();
        }
        
        throw new IllegalArgumentException("사용자 ID를 추출할 수 없습니다");
    }
}
