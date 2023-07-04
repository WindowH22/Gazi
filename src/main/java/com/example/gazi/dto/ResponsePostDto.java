package com.example.gazi.dto;

import com.example.gazi.domain.Post;
import com.example.gazi.domain.Repost;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.format.DateTimeFormatter;

public class ResponsePostDto {

    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class getTopPostDto {
        private Long userId;
        private String title;
        private String distance;
        private String time;
        private int rePostCount;
        private String placeName;
        private Long headKeywordId;
        private Page<ResponsePostListDto> postList;
        private Long hit;
        private String backgroundMapUrl;
        public static getTopPostDto toDto(Post post, String distance, String time, Page<ResponsePostListDto> postList) {
            return getTopPostDto.builder()
                    .userId(post.getMember().getId())
                    .title(post.getTitle())
                    .distance(distance)
                    .time(time)
                    .rePostCount(post.getRePosts().size() + 1) // 포스트 개수 =  답글 + 최초 게시글(1)
                    .placeName(post.getPlaceName())
                    .headKeywordId(post.getHeadKeyword().getId())
                    .hit(post.getHit())
                    .backgroundMapUrl(post.getBackgroundMap())
                    .postList(postList)
                    .build();
        }

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
        private String backgroundMap;
        private String placeName;

        public static getPostDto toDto(Post post, String time, String distance, String content) {
            return getPostDto.builder()
                    .title(post.getTitle())
                    .distance(distance)
                    .time(time)
                    .rePostCount(post.getRePosts().stream().count() + 1)
                    .content(content)
                    .latitude(post.getLatitude())
                    .longitude(post.getLongitude())
                    .headKeyword(post.getHeadKeyword().getId())
                    .thumbNail(post.getThumbNail())
                    .postId(post.getId())
                    .backgroundMap(post.getBackgroundMap())
                    .placeName(post.getPlaceName())
                    .build();

        }
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class getMyPostDto {
        Long postCount;
        Page<getPostDto> postDtoPage;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class getMyRepostDto {
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
        String createTime;

        public static myRepost toDto(Repost repost) {

            return myRepost.builder()
                    .title(repost.getContent())
                    .content(repost.getPost().getTitle())
                    .createTime(repost.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                    .build();
        }
    }

}
