package com.example.gazi.domain.enums;

import lombok.Getter;

@Getter
public enum NotificationEnum {
    KEYWORD("KEYWORD"),
    REPOST("REPOST"),
    LIKE("LIKE");

    NotificationEnum(String notificationEnum) {
    }
}
