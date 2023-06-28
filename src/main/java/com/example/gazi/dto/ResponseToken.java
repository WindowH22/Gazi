package com.example.gazi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ResponseToken {
    private String grantType;
    private Long memberId;
    private String nickName;
    private String email;
    private boolean isFirebaseToken;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpirationTime;
    private Long refreshTokenExpirationTime;
    private boolean notificationByKeyword;
    private boolean notificationByRepost;
    private boolean notificationByLike;

}
