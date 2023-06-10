package com.example.gazi.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {
    // Amazon-s3-sdk
    private AmazonS3 s3Client;
    private final AmazonS3Client amazonS3Client;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretkey;

    @Value("${cloud.aws.region.static}")
    private String clientRegion;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket; // 버킷 명


    // upload 메서드 | MultipartFile을 사용할 경우
    public void upload(File file, String key, String contentType, long contentLength) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
    }

    public static String makeFileName(String folder) {
        LocalDateTime date = LocalDateTime.now();
        int randomNum = (int) (Math.random() * 100);
        String fileName = folder + "/" + randomNum + UUID.randomUUID() + date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        return fileName;
    }

    @Override
    public String uploadFile(MultipartFile file, String fileName) {

        try {
            String fileUrl = "https://" + bucket + ".s3." + clientRegion + ".amazonaws.com/" + fileName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
            log.info("s3업로드 완료");
            return fileUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return "IOExeption 발생";
        }
    }

    @Override
    public String uploadFile(byte[] file, String fileName) {

        try {
            String fileUrl = "https://" + bucket + ".s3." + clientRegion + ".amazonaws.com/" + fileName;

            InputStream inputStream = new ByteArrayInputStream(file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.length);

            PutObjectRequest request = new PutObjectRequest(bucket, fileName, inputStream, metadata);
            amazonS3Client.putObject(request);
            log.info("s3업로드 완료");
            return fileUrl;
        } catch (Exception e){
            e.printStackTrace();
            return "에외 발생";
        }
    }

    // 삭제 메서드
    @Override
    public void deleteFile(String key) {
        try {

            amazonS3Client.deleteObject(bucket, key);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
