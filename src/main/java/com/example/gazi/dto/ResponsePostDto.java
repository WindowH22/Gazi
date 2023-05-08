package com.example.gazi.dto;

import com.example.gazi.domain.Keyword;
import com.example.gazi.domain.Post;
import lombok.*;
import org.springframework.data.domain.Page;

public class ResponsePostDto {

    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class getTopPostDto {
        private Long userId;
        private String title;
        private int rePostCount;
        private String placeName;
        private Long headKeywordId;
        private Page<ResponsePostListDto> postList;
        private Long hit;
    }


    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class getPostDto {
        private String title;
        private String distance;
        private String time;
        private Long rePostCount;
        private String content;
        private Double latitude;
        private Double longitude;
        private Keyword headKeyword;
        private String thumbNail;
        private Long postId;

        public static getPostDto toDto(Post post, String time, String distance,String content) {
            return getPostDto.builder()
                    .title(post.getTitle())
                    .distance(distance)
                    .time(time)
                    .rePostCount(post.getRePosts().stream().count())
                    .content(content)
                    .latitude(post.getLatitude())
                    .longitude(post.getLongitude())
                    .headKeyword(post.getHeadKeyword())
                    .thumbNail(post.getThumbNail())
                    .postId(post.getId())
                    .build();

        }
    }

}
