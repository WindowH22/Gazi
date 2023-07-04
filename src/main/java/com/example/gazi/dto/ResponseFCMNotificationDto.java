package com.example.gazi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFCMNotificationDto {
    private String title;
    private String body;
    private Long postId;
    private Long repostId;
    private LocalDateTime createdAt;

}
