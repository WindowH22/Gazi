package com.example.gazi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestFCMNotificationDto {
    private Long targetUserId;
    private String title;
    private String body;

    @Builder
    public RequestFCMNotificationDto(Long targetUserId, String title, String body) {
        this.targetUserId = targetUserId;
        this.title = title;
        this.body = body;
    }
}
