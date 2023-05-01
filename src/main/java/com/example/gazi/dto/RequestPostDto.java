package com.example.gazi.dto;

import com.example.gazi.domain.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

public class RequestPostDto {

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class addPostDto{
        private String title;
        private String placeName;
        private String content;
        private Double latitude;
        private Double longitude;
        private List<Long> keywordIdList;
        private Long headKeywordId;

        public Post toEntity(String placeName,String title, String content, Double latitude, Double longitude, Keyword headKeyword, Member member){
            return Post.builder()
                    .placeName(placeName)
                    .title(title)
                    .content(content)
                    .latitude(latitude)
                    .longitude(longitude)
                    .headKeyword(headKeyword)
                    .member(member)
                    .build();
        }
    }
    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class updatePostDto{
        private String title;
        private String placeName;
        private String content;
        private List<Long> keywordIdList;
        private Long headKeywordId;
        private List<String> deleteFileNameList;

        public Post toEntity(String placeName,String title, String content, Double latitude, Double longitude, Keyword headKeyword, Member member){
            return Post.builder()
                    .placeName(placeName)
                    .title(title)
                    .content(content)
                    .latitude(latitude)
                    .longitude(longitude)
                    .headKeyword(headKeyword)
                    .member(member)
                    .build();
        }
    }

}
