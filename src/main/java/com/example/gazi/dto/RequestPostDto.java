package com.example.gazi.dto;

import com.example.gazi.domain.Keyword;
import com.example.gazi.domain.Member;
import com.example.gazi.domain.Post;
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
        private String thumbNail;
        private Double latitude;
        private Double longitude;
        private List<Long> keywordIdList;
        private Long headKeywordId;
        private Long accId;


        public Post toEntity(String placeName,String title, String content, Double latitude, Double longitude, Keyword headKeyword,String thumbNail, Member member){
            return Post.builder()
                    .placeName(placeName)
                    .title(title)
                    .content(content)
                    .latitude(latitude)
                    .longitude(longitude)
                    .headKeyword(headKeyword)
                    .thumbNail(thumbNail)
                    .member(member)
                    .build();
        }

        public Post autoToEntity(String placeName,String title, String content, Double latitude, Double longitude, Keyword headKeyword,String thumbNail, Member member,Long accId){

            return Post.builder()
                    .placeName(placeName)
                    .title(title)
                    .content(content)
                    .latitude(latitude)
                    .longitude(longitude)
                    .headKeyword(headKeyword)
                    .thumbNail(thumbNail)
                    .member(member)
                    .accId(accId)
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

        public Post toEntity(String placeName,String title, String content,String thumbNail, Double latitude, Double longitude, Keyword headKeyword, Member member){
            return Post.builder()
                    .placeName(placeName)
                    .title(title)
                    .content(content)
                    .thumbNail(thumbNail)
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
    public static class getPostDto {
        Double minLat;
        Double minLon;
        Double maxLat;
        Double maxLon;
        Double curX;
        Double curY;
    }

}
