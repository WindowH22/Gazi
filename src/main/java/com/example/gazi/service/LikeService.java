package com.example.gazi.service;

import com.example.gazi.dto.RequestLikeDto;
import com.example.gazi.dto.Response.Body;
import org.springframework.http.ResponseEntity;

public interface LikeService {


    ResponseEntity<Body> likePost(RequestLikeDto.likePostDto dto);

    ResponseEntity<Body> likeRepost(RequestLikeDto.likeRepostDto dto);


    ResponseEntity<Body> deleteLikePost(RequestLikeDto.likePostDto dto);

    ResponseEntity<Body> deleteLikRePost(RequestLikeDto.likeRepostDto dto);
}
