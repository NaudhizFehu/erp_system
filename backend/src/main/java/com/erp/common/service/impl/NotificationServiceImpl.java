package com.erp.common.service.impl;

import com.erp.common.dto.NotificationDto;
import com.erp.common.entity.Notification;
import com.erp.common.entity.User;
import com.erp.common.repository.NotificationRepository;
import com.erp.common.repository.UserRepository;
import com.erp.common.service.NotificationService;
import com.erp.common.utils.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 알림 서비스 구현체
 * 알림 관련 비즈니스 로직을 처리합니다
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable) {
        log.info("사용자 알림 목록 조회: userId={}, page={}, size={}", 
                userId, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        return notifications.map(NotificationDto::from);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        log.info("읽지 않은 알림 개수 조회: userId={}", userId);
        
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(Long userId) {
        log.info("읽지 않은 알림 목록 조회: userId={}", userId);
        
        List<Notification> notifications = notificationRepository.findUnreadByUserId(userId);
        
        return notifications.stream()
                .map(NotificationDto::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getRecentNotifications(Long userId) {
        log.info("최근 3일 이내 알림 목록 조회: userId={}", userId);
        
        // 3일 전 날짜 계산
        java.time.LocalDateTime threeDaysAgo = java.time.LocalDateTime.now().minusDays(3);
        
        List<Notification> notifications = notificationRepository.findRecentNotificationsByUserId(userId, threeDaysAgo);
        
        return notifications.stream()
                .map(NotificationDto::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getAllNotifications(Long userId) {
        log.info("최근 2주 이내 알림 목록 조회: userId={}", userId);
        
        // 2주 전 날짜 계산
        java.time.LocalDateTime twoWeeksAgo = java.time.LocalDateTime.now().minusWeeks(2);
        
        List<Notification> notifications = notificationRepository.findNotificationsByUserIdAndDateRange(userId, twoWeeksAgo);
        
        return notifications.stream()
                .map(NotificationDto::from)
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        log.info("알림 읽음 처리: notificationId={}, userId={}", notificationId, userId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ExceptionUtils.entityNotFound("알림을 찾을 수 없습니다"));
        
        // 본인의 알림인지 확인
        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 알림만 읽음 처리할 수 있습니다");
        }
        
        notification.markAsRead();
        notificationRepository.save(notification);
        
        log.info("알림 읽음 처리 완료: notificationId={}", notificationId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("모든 알림 읽음 처리: userId={}", userId);
        
        List<Notification> unreadNotifications = notificationRepository.findUnreadByUserId(userId);
        
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
        }
        
        notificationRepository.saveAll(unreadNotifications);
        
        log.info("모든 알림 읽음 처리 완료: userId={}, count={}", userId, unreadNotifications.size());
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        log.info("알림 삭제: notificationId={}, userId={}", notificationId, userId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ExceptionUtils.entityNotFound("알림을 찾을 수 없습니다"));
        
        // 본인의 알림인지 확인
        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 알림만 삭제할 수 있습니다");
        }
        
        notificationRepository.delete(notification);
        
        log.info("알림 삭제 완료: notificationId={}", notificationId);
    }

    @Override
    @Transactional
    public NotificationDto createNotification(User user, String title, String message, 
                                            Notification.NotificationType type, String actionUrl) {
        log.info("새 알림 생성: userId={}, title={}, type={}", user.getId(), title, type);
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setActionUrl(actionUrl);
        notification.setIsRead(false);
        
        Notification savedNotification = notificationRepository.save(notification);
        
        log.info("새 알림 생성 완료: notificationId={}", savedNotification.getId());
        
        return NotificationDto.from(savedNotification);
    }

    @Override
    @Transactional
    public void createSystemNotification(String title, String message, 
                                       Notification.NotificationType type, String actionUrl) {
        log.info("시스템 알림 생성: title={}, type={}", title, type);
        
        // 모든 활성 사용자에게 알림 생성
        List<User> activeUsers = userRepository.findByIsActive(true);
        
        for (User user : activeUsers) {
            createNotification(user, title, message, type, actionUrl);
        }
        
        log.info("시스템 알림 생성 완료: userCount={}", activeUsers.size());
    }

    @Override
    @Transactional
    public void createOrderNotification(User user, String title, String message, String actionUrl) {
        log.info("주문 알림 생성: userId={}, title={}", user.getId(), title);
        
        createNotification(user, title, message, Notification.NotificationType.INFO, actionUrl);
    }

    @Override
    @Transactional
    public void createInventoryNotification(User user, String title, String message, String actionUrl) {
        log.info("재고 알림 생성: userId={}, title={}", user.getId(), title);
        
        createNotification(user, title, message, Notification.NotificationType.WARNING, actionUrl);
    }
}
