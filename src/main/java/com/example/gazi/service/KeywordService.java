package com.example.gazi.service;

import com.example.gazi.dto.RequestKeywordCartDto;
import com.example.gazi.dto.RequestKeywordDto;
import com.example.gazi.dto.Response.Body;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface KeywordService {
    ResponseEntity<Body> interestKeyword(List<RequestKeywordCartDto> keywordCartDtoList);
    ResponseEntity<Body> addKeyword(RequestKeywordDto keywordDto);
    ResponseEntity<Body> myKeywordList();
}
