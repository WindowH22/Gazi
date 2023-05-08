package com.example.gazi.dto;

import com.example.gazi.domain.Member;
import com.example.gazi.domain.Post;
import com.example.gazi.domain.Repost;
import lombok.Getter;

import java.util.List;

public class RequestRepostDto {

    @Getter
    public static class addDto {
        private Long postId;
        private String content;
        private Double latitude;
        private Double longitude;
        private List<Long> keywordIdList;

        public Repost toEntity(Post post, String content, Member member,Double latitude,Double longitude, List<Long> keywordIdList) {
            return Repost.builder()
                    .content(content)
                    .post(post)
                    .member(member)
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
        }

    }

    @Getter
    public static class updateDto {
        private List<String> deleteFileNameList;
        private String content;
    }
}
