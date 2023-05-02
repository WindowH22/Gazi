package com.example.gazi.controller;

import com.example.gazi.dto.RequestReportDto;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Body> reportPost(@RequestBody RequestReportDto dto){
        return reportService.ReportPost(dto);
    }

}
