package com.erp.common.entity;

import com.erp.hr.entity.Department;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 알림 엔티티
 * 시스템에서 발생하는 알림 정보를 저장합니다
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notifications_user_id", columnList = "user_id"),
    @Index(name = "idx_notifications_is_read", columnList = "is_read"),
    @Index(name = "idx_notifications_created_at", columnList = "created_at"),
    @Index(name = "idx_notifications_scope", columnList = "scope"),
    @Index(name = "idx_notifications_company_id", columnList = "company_id"),
    @Index(name = "idx_notifications_department_id", columnList = "department_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    /**
     * 알림을 받을 사용자
     */
    @NotNull(message = "사용자는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 알림 제목
     */
    @NotBlank(message = "알림 제목은 필수입니다")
    @Size(max = 200, message = "알림 제목은 200자 이하여야 합니다")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 알림 내용
     */
    @NotBlank(message = "알림 내용은 필수입니다")
    @Size(max = 1000, message = "알림 내용은 1000자 이하여야 합니다")
    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    /**
     * 알림 타입
     */
    @NotNull(message = "알림 타입은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private NotificationType type = NotificationType.INFO;

    /**
     * 읽음 여부
     */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    /**
     * 액션 URL (클릭 시 이동할 페이지)
     */
    @Size(max = 500, message = "액션 URL은 500자 이하여야 합니다")
    @Column(name = "action_url", length = 500)
    private String actionUrl;

    /**
     * 읽음 처리 시간
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * 알림 범위
     */
    @NotNull(message = "알림 범위는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false, length = 20)
    private NotificationScope scope = NotificationScope.USER;

    /**
     * 알림 우선순위
     */
    @NotNull(message = "알림 우선순위는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private NotificationPriority priority = NotificationPriority.NORMAL;

    /**
     * 대상 회사 (scope가 COMPANY, DEPARTMENT일 때 사용)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    /**
     * 대상 부서 (scope가 DEPARTMENT일 때 사용)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * 알림 카테고리 (선택적)
     */
    @Size(max = 50, message = "알림 카테고리는 50자 이하여야 합니다")
    @Column(name = "category", length = 50)
    private String category;

    /**
     * 알림 타입 열거형
     */
    public enum NotificationType {
        INFO("정보"),
        WARNING("경고"),
        ERROR("오류"),
        SUCCESS("성공");

        private final String description;

        NotificationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 알림을 읽음 처리합니다
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * 알림이 읽지 않은 상태인지 확인합니다
     */
    public boolean isUnread() {
        return !this.isRead;
    }
}
