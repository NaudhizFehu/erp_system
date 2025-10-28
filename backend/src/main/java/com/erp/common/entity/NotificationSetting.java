package com.erp.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 알림 설정 엔티티
 * 역할별 알림 수신 설정을 관리합니다
 */
@Entity
@Table(name = "notification_settings", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_notification_settings", 
            columnNames = {"company_id", "role", "scope"})
    },
    indexes = {
        @Index(name = "idx_notification_settings_company", columnList = "company_id"),
        @Index(name = "idx_notification_settings_role", columnList = "role")
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSetting extends BaseEntity {

    /**
     * 회사 (null이면 시스템 전체 기본 설정)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    /**
     * 대상 역할
     */
    @NotNull(message = "대상 역할은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private User.UserRole role;

    /**
     * 알림 범위
     */
    @NotNull(message = "알림 범위는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false, length = 20)
    private NotificationScope scope;

    /**
     * 수신 가능 여부
     */
    @NotNull(message = "수신 가능 여부는 필수입니다")
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    /**
     * 최소 우선순위 (이 우선순위 이상만 수신)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "min_priority", length = 20)
    private NotificationPriority minPriority = NotificationPriority.LOW;

    /**
     * 카테고리별 설정 (JSON 형태, 예: {"order": true, "system": false})
     */
    @Column(name = "category_settings", columnDefinition = "TEXT")
    private String categorySettings;
}


