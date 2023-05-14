package com.example.gazi.service;

import com.example.gazi.dto.RequestReportDto;
import com.example.gazi.dto.Response;
import com.example.gazi.dto.Response.Body;
import org.springframework.http.ResponseEntity;

public interface ReportService {

//    ResponseEntity<Body> ReportPost(RequestReportDto.reportPostDto dto);

//    ResponseEntity<Body> ReportRepost(RequestReportDto.reportRepostDto dto);

    ResponseEntity<Body> ReportPost(RequestReportDto dto);

    ResponseEntity<Body> ReportRepost(RequestReportDto dto);
}
