package com.example.gazi.dto;

import com.example.gazi.domain.Keyword;
import com.example.gazi.domain.Post;
import com.example.gazi.domain.Repost;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

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
        private Long headKeyword;
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
                    .headKeyword(post.getHeadKeyword().getId())
                    .thumbNail(post.getThumbNail())
                    .postId(post.getId())
                    .build();

        }
    }
    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class getMyPostDto{
        Long postCount;
        Page<getPostDto> postDtoPage;
    }
    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class getMyRepostDto{
        Long repostCount;
        Page<myRepost> repostListPage;
    }
    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class myRepost {
        String title;
        String content; // 회원님이 "" 사건에 댓글을 남겼습니다.
        LocalDateTime createTime;

        public static myRepost toDto(Repost repost){
            return  myRepost.builder()
                    .title(repost.getContent())
                    .content(repost.getPost().getTitle())
                    .createTime(repost.getCreatedAt())
                    .build();
        }
    }

}
