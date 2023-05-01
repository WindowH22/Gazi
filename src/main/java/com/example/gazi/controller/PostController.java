package com.example.gazi.controller;

import com.example.gazi.dto.RequestPostDto;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping("/topPost")
    public ResponseEntity<Body> addPost(@RequestPart RequestPostDto.addPostDto dto, @RequestPart(required = false) List<MultipartFile> files) {
        return postService.addPost(dto, files);
    }

    @GetMapping("/topPost")
    public ResponseEntity<Body> getPost(@RequestParam Long postId) {
        return postService.getPost(postId);
    }

    @PutMapping("/topPost")
    public ResponseEntity<Body> updatePost(@RequestParam Long postId, @RequestPart RequestPostDto.updatePostDto dto, @RequestPart(required = false) List<MultipartFile> files) {
        return postService.updatePost(postId, dto, files);
    }

    @DeleteMapping("/topPost")
    public ResponseEntity<Body> deletePost(@RequestParam Long postId) {
        return postService.deletePost(postId);
    }
}
