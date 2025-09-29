package com.erp.common.service;

import com.erp.common.dto.NotificationDto;
import com.erp.common.entity.Notification;
import com.erp.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 알림 서비스 인터페이스
 * 알림 관련 비즈니스 로직을 정의합니다
 */
public interface NotificationService {

    /**
     * 사용자의 알림 목록 조회 (페이징)
     */
    Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable);

    /**
     * 사용자의 읽지 않은 알림 개수 조회
     */
    long getUnreadCount(Long userId);

    /**
     * 사용자의 읽지 않은 알림 목록 조회
     */
    List<NotificationDto> getUnreadNotifications(Long userId);

    /**
     * 사용자의 최근 3일 이내 알림 목록 조회 (읽은/읽지 않은 모든 알림)
     */
    List<NotificationDto> getRecentNotifications(Long userId);

    /**
     * 사용자의 최근 2주 이내 알림 목록 조회 (읽은/읽지 않은 모든 알림)
     */
    List<NotificationDto> getAllNotifications(Long userId);

    /**
     * 특정 알림을 읽음 처리
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 사용자의 모든 알림을 읽음 처리
     */
    void markAllAsRead(Long userId);

    /**
     * 알림 삭제
     */
    void deleteNotification(Long notificationId, Long userId);

    /**
     * 사용자의 모든 알림 삭제 (개발/테스트용)
     */
    void deleteAllNotificationsByUser(Long userId);

    /**
     * 새로운 알림 생성
     */
    NotificationDto createNotification(User user, String title, String message, 
                                     Notification.NotificationType type, String actionUrl);

    /**
     * 시스템 알림 생성 (모든 사용자에게)
     */
    void createSystemNotification(String title, String message, 
                                Notification.NotificationType type, String actionUrl);

    /**
     * 주문 관련 알림 생성
     */
    void createOrderNotification(User user, String title, String message, String actionUrl);

    /**
     * 재고 관련 알림 생성
     */
    void createInventoryNotification(User user, String title, String message, String actionUrl);
}
