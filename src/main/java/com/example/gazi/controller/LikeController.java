package com.example.gazi.controller;

import com.example.gazi.dto.RequestLikeDto;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<Body> likePost(
            @RequestPart(required = false) RequestLikeDto.likePostDto likePostDto,
            @RequestPart(required = false) RequestLikeDto.likeRepostDto likeRepostDto
    ) {
        if (likePostDto != null) return likeService.likePost(likePostDto);
        else return likeService.likeRepost(likeRepostDto);
    }

    @DeleteMapping
    public ResponseEntity<Body> deleteLikePost(
            @RequestPart(required = false) RequestLikeDto.likePostDto likePostDto,
            @RequestPart(required = false) RequestLikeDto.likeRepostDto likeRepostDto
    ) {
        if (likePostDto != null) return likeService.deleteLikePost(likePostDto);
        else return likeService.deleteLikRePost(likeRepostDto);
    }

}
