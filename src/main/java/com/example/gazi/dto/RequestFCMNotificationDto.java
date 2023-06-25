package com.example.gazi.dto;

import com.example.gazi.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class RequestFCMNotificationDto {
    private Long targetUserId;
    private String title;
    private String body;
    private Set<Member> memberList;

    @Builder
    public RequestFCMNotificationDto(Long targetUserId, String title, String body, Set<Member> memberList) {
        this.targetUserId = targetUserId;
        this.title = title;
        this.body = body;
        this.memberList = memberList;
    }
}
