package com.example.gazi.service;

import com.example.gazi.domain.Member;
import com.example.gazi.dto.RequestFCMNotificationDto;
import com.example.gazi.dto.Response.Body;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FCMNotificationService {
    ResponseEntity<Body> sendNotificationByToken(RequestFCMNotificationDto requestDto);
    ResponseEntity<Body> sendGroupNotification(RequestFCMNotificationDto requestDto) throws FirebaseMessagingException;

    void sendMessageByKeyword(Member member,List<Long> keywordIdList, String postTitle);
    // 관심키워드 추가된 게시글 알림
    //관심키워드의 단어가 제목/본문에 포함된 게시글 알림
    // case2) 최초스레드에 답글 달리는 경우 답글 알림
    // case3) 스레드에 ‘도움돼요’ 버튼 클릭되는 경우 ㅇ알림
}
