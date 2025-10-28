package com.erp.common.entity;

/**
 * 알림 우선순위 열거형
 * 알림의 중요도를 정의합니다
 */
public enum NotificationPriority {
    LOW("낮음"),
    NORMAL("보통"),
    HIGH("높음"),
    URGENT("긴급");

    private final String description;

    NotificationPriority(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}


