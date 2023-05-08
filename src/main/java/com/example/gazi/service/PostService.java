package com.example.gazi.service;

import com.example.gazi.dto.RequestPostDto;
import com.example.gazi.dto.RequestPostDto.addPostDto;
import com.example.gazi.dto.Response.Body;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    ResponseEntity<Body> addPost(addPostDto dto, List<MultipartFile> fileList, MultipartFile thumbNail);

    ResponseEntity<Body> updatePost(Long postId, RequestPostDto.updatePostDto dto, List<MultipartFile> multipartFiles);

    ResponseEntity<Body> deletePost(Long postId);

    ResponseEntity<Body> getTopPost(Double curX, Double curY, Long postId, Pageable pageable);

    ResponseEntity<Body> getPost(Double curX, Double curY, Pageable pageable);

    @Transactional(readOnly = true)
    ResponseEntity<Body> getPostByLocation(Double minLat, Double minLon, Double maxLat, Double maxLon, Double curX, Double curY, Pageable pageable, Boolean isNearSearch);
}
