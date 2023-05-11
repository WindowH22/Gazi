package com.example.gazi.service;

import com.example.gazi.dto.RequestRepostDto;
import com.example.gazi.dto.Response;
import com.example.gazi.dto.Response.Body;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RepostService {
    ResponseEntity<Body> addRepost(RequestRepostDto.addDto dto, List<MultipartFile> files);

    ResponseEntity<Body> addRepost(RequestRepostDto.addDto dto);

    ResponseEntity<Body> fileUpload(List<MultipartFile> fileList, Long repostId);

    ResponseEntity<Body> updateRepost(Long RepostId, RequestRepostDto.updateDto dto, List<MultipartFile> fileList);

    ResponseEntity<Body> deleteRepost(Long repostId);
}
