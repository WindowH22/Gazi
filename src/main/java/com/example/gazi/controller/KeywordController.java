package com.example.gazi.controller;

import com.example.gazi.dto.RequestKeywordCartDto;
import com.example.gazi.dto.RequestKeywordDto;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/keyword")
@RestController
public class KeywordController {

    private final KeywordService keywordService;
    //관심 키워드 등록
    @PostMapping("interest-keyword")
    public ResponseEntity<Body> interestKeyword(@RequestBody RequestKeywordCartDto dto){
       return keywordService.interestKeyword(dto.getMyKeywordList());
    }

    //키워드 추가
    @PostMapping("/add-keyword")
    public ResponseEntity<Body> addKeyword(@RequestBody RequestKeywordDto dto){
        return keywordService.addKeyword(dto);
    }

    //키워드 조회
    @GetMapping
    public ResponseEntity<Body> keywordList(){
        return keywordService.keywordList();
    }
}
