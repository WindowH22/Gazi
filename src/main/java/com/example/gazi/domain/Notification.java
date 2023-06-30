package com.example.gazi.domain;

import com.example.gazi.domain.enums.NotificationEnum;
import com.example.gazi.dto.RequestFCMNotificationDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "NOTIFICATION")
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String body;
    @Enumerated(EnumType.STRING)
    private NotificationEnum notificationEnum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public static Notification toEntity(RequestFCMNotificationDto dto, Member member, NotificationEnum notificationEnum){
        Notification notification = Notification.builder()
                .title(dto.getTitle())
                .body(dto.getBody())
                .notificationEnum(notificationEnum)
                .member(member)
                .build();

        return notification;
    }
}
