package com.example.gazi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestLikeDto {
    @Getter
    @Setter
    public static class likePostDto{
        Long postId;
    }


    @Getter
    @Setter
    public static class likeRepostDto{
        Long repostId;
    }
}
