package com.example.gazi.controller;

import com.example.gazi.dto.RequestFCMNotificationDto;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.service.FCMNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
@Controller
public class FCMNotificationController {

    private final FCMNotificationService fcmNotificationService;

    @PostMapping
    public ResponseEntity<Body> sendNotificationByToken(@RequestBody RequestFCMNotificationDto requestDto) {
        return fcmNotificationService.sendNotificationByToken(requestDto);
    }
}
