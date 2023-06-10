package com.example.gazi.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFile(MultipartFile file, String fileName);
    String uploadFile(byte[] file, String fileName);

    void deleteFile(String key);
}
