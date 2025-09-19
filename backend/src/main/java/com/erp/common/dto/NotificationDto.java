package com.erp.common.dto;

import com.erp.common.entity.Notification;
import java.time.LocalDateTime;

/**
 * 알림 정보 DTO
 */
public record NotificationDto(
    Long id,
    String title,
    String message,
    String type,
    Boolean isRead,
    String actionUrl,
    LocalDateTime createdAt,
    LocalDateTime readAt
) {
    /**
     * Notification 엔티티를 NotificationDto로 변환
     */
    public static NotificationDto from(Notification notification) {
        return new NotificationDto(
            notification.getId(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getType().name(),
            notification.getIsRead(),
            notification.getActionUrl(),
            notification.getCreatedAt(),
            notification.getReadAt()
        );
    }
}
