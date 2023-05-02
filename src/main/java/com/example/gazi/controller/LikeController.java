package com.example.gazi.controller;

import com.example.gazi.dto.RequestLikeDto;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<Body> likePost(@RequestBody RequestLikeDto dto) {
        return likeService.likePost(dto);
    }

    @DeleteMapping
    public ResponseEntity<Body> deleteLikePost(@RequestBody RequestLikeDto dto) {
        return likeService.deleteLikePost(dto);
    }
}
