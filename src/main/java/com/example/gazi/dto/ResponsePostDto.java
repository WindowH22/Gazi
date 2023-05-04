package com.example.gazi.dto;

import com.example.gazi.domain.Keyword;
import com.example.gazi.domain.Post;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class ResponsePostDto {

    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class getTopPostDto {
        private Long userId;
        private String title;
        private String placeName;
        private String content;
        private List<Long> keywordIdList;
        private Long headKeywordId;
        private List<ResponseFilePostDto> fileUrlList;
        private Page<ResponseRepostDto> rePostList;
        private LocalDateTime createdAt;
        private String nickName;
        private Long hit;
        private Long memberId;
        private Boolean isLike;
        private Boolean isReport;
        private String thumbnail;
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

        public static getPostDto toDto(Post post, String time, String distance) {
            return getPostDto.builder()
                    .title(post.getTitle())
                    .distance(distance)
                    .time(time)
                    .rePostCount(post.getRePosts().stream().count())
                    .content(post.getContent())
                    .latitude(post.getLatitude())
                    .longitude(post.getLongitude())
                    .headKeyword(post.getHeadKeyword())
                    .thumbNail(post.getThumbNail())
                    .postId(post.getId())
                    .build();

        }
    }

}
