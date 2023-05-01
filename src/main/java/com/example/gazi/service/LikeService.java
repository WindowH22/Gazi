package com.example.gazi.service;

import com.example.gazi.dto.RequestLikeDto;
import com.example.gazi.dto.Response.Body;
import org.springframework.http.ResponseEntity;

public interface LikeService {

    ResponseEntity<Body> likePost(RequestLikeDto dto) ;

    ResponseEntity<Body> deleteLikePost(RequestLikeDto dto);
}
