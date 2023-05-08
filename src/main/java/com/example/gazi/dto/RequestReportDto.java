package com.example.gazi.dto;

import lombok.Getter;
import lombok.Setter;


public class RequestReportDto {
    @Getter
    @Setter
    public static class reportPostDto{
        private Long postId;
    }
    @Getter
    @Setter
    public static class reportRepostDto{
        private Long repostId;
    }
}
