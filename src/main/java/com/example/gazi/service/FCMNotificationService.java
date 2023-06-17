package com.example.gazi.service;

import com.example.gazi.dto.RequestFCMNotificationDto;
import com.example.gazi.dto.Response.Body;
import org.springframework.http.ResponseEntity;

public interface FCMNotificationService {
    ResponseEntity<Body> sendNotificationByToken(RequestFCMNotificationDto requestDto);
}
