package com.example.gazi.service;

import com.example.gazi.dto.RequestKeywordDto;
import com.example.gazi.dto.Response.Body;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface KeywordService {
    ResponseEntity<Body> interestKeyword(List<Long> keywordList);
    ResponseEntity<Body> addKeyword(RequestKeywordDto keywordDto);
    ResponseEntity<Body> myKeywordList();

    ResponseEntity<Body> keywordList();
}
