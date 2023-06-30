package com.example.gazi.dto;

import com.example.gazi.domain.enums.ReportEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestReportDto {

    private Long postId;
    private Long repostId;
    private ReportEnum reportEnum;
    private String reason;
}
