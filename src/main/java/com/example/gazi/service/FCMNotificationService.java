package com.example.gazi.service;

import com.example.gazi.domain.Member;
import com.example.gazi.domain.Post;
import com.example.gazi.dto.RequestFCMNotificationDto;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;

public interface FCMNotificationService {
    void sendNotificationByToken(RequestFCMNotificationDto requestDto);
    void sendGroupNotification(RequestFCMNotificationDto requestDto) throws FirebaseMessagingException;
    void sendMessageByKeyword(Member member, Post post, List<Long> keywordIdList);
}
