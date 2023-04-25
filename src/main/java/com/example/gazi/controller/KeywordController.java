package com.example.gazi.controller;

import com.example.gazi.dto.RequestKeywordDto;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/keyword")
@RestController
public class KeywordController {

    private final KeywordService keywordService;
    //관심 키워드 등록
    @PostMapping("interest-keyword")
    public ResponseEntity<Body> interestKeyword(@RequestBody List<Long> keywordList){
       return keywordService.interestKeyword(keywordList);
    }

    //키워드 추가
    @PostMapping("/add-keyword")
    public ResponseEntity<Body> addKeyword(@RequestBody RequestKeywordDto dto){
        return keywordService.addKeyword(dto);
    }
}
