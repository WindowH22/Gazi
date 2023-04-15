package com.example.gazi.dto;

import com.example.gazi.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ResponseMember {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MemberInfo {
        private String email;
        private String nickName;

        public static ResponseMember.MemberInfo of(Member member) {
            return MemberInfo.builder()
                    .email(member.getEmail())
                    .nickName(member.getNickName())
                    .build();
        }
    }
}
