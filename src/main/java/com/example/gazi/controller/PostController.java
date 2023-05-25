package com.example.gazi.controller;

import com.example.gazi.dto.RequestPostDto;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/post")
@RestController
public class PostController {

    private final PostService postService;

    private Logger log = LoggerFactory.getLogger(getClass());

//    @PostMapping("/top-post")
//    public ResponseEntity<Body> addPost(@RequestPart RequestPostDto.addPostDto dto, @RequestPart(required = false) List<MultipartFile> files, @RequestPart(required = false) MultipartFile thumbnail) {
//        return postService.addPost(dto);
//    }

    @PostMapping("/top-post")
    public ResponseEntity<Body> addPost(@RequestBody RequestPostDto.addPostDto dto) {
        return postService.addPost(dto);
    }

    @PostMapping("/top-post-file")
    public ResponseEntity<Body> addPostFile(@RequestPart(required = false) List<MultipartFile> files, @RequestPart(required = false) MultipartFile thumbnail, @RequestPart(required = false) MultipartFile backgroundMap, @RequestParam Long postId) {
        return postService.fileUpload(files, thumbnail,backgroundMap,postId);
    }

    @GetMapping("/top-post")
    public ResponseEntity<Body> getTopPost(
            @RequestParam Double curX,
            @RequestParam Double curY,
            @RequestParam Long postId, @PageableDefault(page = 0, size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return postService.getTopPost(curX, curY, postId, pageable);
    }

    @PutMapping("/top-post")
    public ResponseEntity<Body> updatePost(@RequestParam Long postId, @RequestPart RequestPostDto.updatePostDto dto, @RequestPart(required = false) List<MultipartFile> files) {
        return postService.updatePost(postId, dto, files);
    }

    @DeleteMapping("/top-post")
    public ResponseEntity<Body> deletePost(@RequestParam Long postId) {
        return postService.deletePost(postId);
    }

    @GetMapping("/top-post-list") // 커뮤 전체 게시글 조회
    public ResponseEntity<Body> getPost(
            @RequestParam("curLat") Double curLat,
            @RequestParam("curLon") Double curLon,
            @RequestParam(value = "keywordId",required = false) List<Long> keywordId,
            @PageableDefault(page = 0, size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) throws IOException, ParseException {

        postService.autoAddPost();
        return postService.getPost(curLat, curLon, pageable,keywordId);
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
            @PageableDefault(page = 0, size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) throws IOException, ParseException {
        postService.autoAddPost();
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
