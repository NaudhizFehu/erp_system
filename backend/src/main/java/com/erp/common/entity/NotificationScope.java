package com.erp.common.entity;

/**
 * 알림 범위 열거형
 * 알림의 적용 범위를 정의합니다
 */
public enum NotificationScope {
    SYSTEM("시스템 전체"),
    COMPANY("회사별"),
    DEPARTMENT("부서별"),
    USER("개인");

    private final String description;

    NotificationScope(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}


