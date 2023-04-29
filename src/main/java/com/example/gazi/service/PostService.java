package com.example.gazi.service;

import com.example.gazi.dto.RequestPostDto;
import com.example.gazi.dto.RequestPostDto.addPostDto;
import com.example.gazi.dto.Response.Body;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    ResponseEntity<Body> addPost(addPostDto dto, List<MultipartFile> fileList);

    ResponseEntity<Body> updatePost(Long postId, RequestPostDto.updatePostDto dto, List<MultipartFile> multipartFiles);

    ResponseEntity<Body> deletePost(Long postId);

    ResponseEntity<Body> getPost(Long postId);
}
