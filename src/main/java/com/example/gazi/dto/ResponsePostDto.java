package com.example.gazi.dto;

import com.example.gazi.domain.RePost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class ResponsePostDto {

    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class getPostDto {
        private String title;
        private String placeName;
        private String content;
        private List<Long> keywordIdList;
        private Long headKeywordId;
        private List<String> fileUrlList;
        private List<RePost> rePostList;
        private LocalDateTime createdAt;
        private String nickName;
        private Long hit;
    }
}
