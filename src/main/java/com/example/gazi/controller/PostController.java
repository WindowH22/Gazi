package com.example.gazi.controller;

import com.example.gazi.dto.RequestPostDto;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<Body> addPost(@RequestPart RequestPostDto.addPostDto dto, @RequestPart(required = false) List<MultipartFile> files, @RequestPart(required = false) MultipartFile thumbnail) {
        return postService.addPost(dto, files, thumbnail);
    }

    @GetMapping("/topPost")
    public ResponseEntity<Body> getTopPost(
            @RequestParam Double curX,
            @RequestParam Double curY,
            @RequestParam Long postId, @PageableDefault(page = 0, size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return postService.getTopPost(curX, curY, postId, pageable);
    }

    @PutMapping("/topPost")
    public ResponseEntity<Body> updatePost(@RequestParam Long postId, @RequestPart RequestPostDto.updatePostDto dto, @RequestPart(required = false) List<MultipartFile> files) {
        return postService.updatePost(postId, dto, files);
    }

    @DeleteMapping("/topPost")
    public ResponseEntity<Body> deletePost(@RequestParam Long postId) {
        return postService.deletePost(postId);
    }

    @GetMapping("/topPostList") // 커뮤 전체 게시글 조회
    public ResponseEntity<Body> getPost(
            @RequestParam("curLat") Double curLat,
            @RequestParam("curLon") Double curLon,
            @PageableDefault(page = 0, size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return postService.getPost(curLat, curLon, pageable);
    }

    @GetMapping("/locationPost")
    public ResponseEntity<Body> getPostByLocation(
            @RequestParam("minLat") Double minLat,
            @RequestParam("minLon") Double minLon,
            @RequestParam("maxLat") Double maxLat,
            @RequestParam("maxLon") Double maxLon,
            @RequestParam("curLat") Double curLat,
            @RequestParam("curLon") Double curLon,
            @RequestParam("isNearSearch") Boolean isNearSearch,
            @PageableDefault(page = 0, size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return postService.getPostByLocation(minLat, minLon, maxLat, maxLon, curLat, curLon, pageable, isNearSearch);
    }

    @GetMapping("/myPost")
    public ResponseEntity<Body> getMyPost(
            @RequestParam("curLat") Double curLat,
            @RequestParam("curLon") Double curLon,
            @RequestParam("isPost") Boolean isPost,
            @PageableDefault(page = 0, size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable)
    {
        return postService.getMyPost(curLat,curLon,pageable,isPost);
    }

}
