package com.example.gazi.controller;

import com.example.gazi.dto.RequestRepostDto;
import com.example.gazi.dto.Response;
import com.example.gazi.service.RepostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/post")
@RestController
public class RepostController {
    private final RepostService repostService;

//    @PostMapping("/repost")
//    public ResponseEntity<Response.Body> addRepost(@RequestPart RequestRepostDto.addDto dto, @RequestPart(required = false) List<MultipartFile> files) {
//        return repostService.addRepost(dto, files);
//    }

    @PostMapping("/repost")
    public ResponseEntity<Response.Body> addRepost(@RequestBody RequestRepostDto.addDto dto) {
        return repostService.addRepost(dto);
    }

    @PostMapping("/repost-file")
    public ResponseEntity<Response.Body> addRepost(@RequestPart(required = false) List<MultipartFile> files, @RequestParam Long repostId) {
        return repostService.fileUpload(files,repostId);
    }

    @PutMapping("/repost")
    public ResponseEntity<Response.Body> updatePost(@RequestParam Long repostId, @RequestPart RequestRepostDto.updateDto dto, @RequestPart(required = false) List<MultipartFile> files) {
        return repostService.updateRepost(repostId, dto, files);
    }

    @DeleteMapping("/repost")
    public ResponseEntity<Response.Body> deletePost(@RequestParam Long repostId) {
        return repostService.deleteRepost(repostId);
    }


}
