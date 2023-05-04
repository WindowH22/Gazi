package com.example.gazi.controller;

import com.example.gazi.dto.RequestReportDto;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Body> reportPost(
            @RequestPart(required = false) RequestReportDto.reportPostDto reportPostDto,
            @RequestPart(required = false) RequestReportDto.reportRepostDto reportRepostDto
    ){
        if(reportPostDto != null) return reportService.ReportPost(reportPostDto);
        else return reportService.ReportRepost(reportRepostDto);
    }

}
