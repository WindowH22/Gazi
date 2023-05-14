package com.example.gazi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestReportDto {

    private Long postId;
    private Long repostId;
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
