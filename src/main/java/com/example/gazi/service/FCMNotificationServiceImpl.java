package com.example.gazi.service;

import com.example.gazi.domain.Member;
import com.example.gazi.dto.RequestFCMNotificationDto;
import com.example.gazi.dto.Response;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.repository.MemberRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FCMNotificationServiceImpl implements FCMNotificationService{

    private final FirebaseMessaging firebaseMessaging;
    private final MemberRepository memberRepository;
    private final Response response;
    @Override
    public ResponseEntity<Body> sendNotificationByToken(RequestFCMNotificationDto requestDto) {

        Optional<Member> member = memberRepository.findById(requestDto.getTargetUserId());

        if(member.isPresent()){
            if(member.get().getFireBaseToken() != null){
                Notification notification = Notification.builder()
                        .setTitle(requestDto.getTitle())
                        .setBody(requestDto.getBody())
                        .build();

                Message message = Message.builder()
                        .setToken(member.get().getFireBaseToken())
                        .setNotification(notification)
                        .build();

                try {
                    firebaseMessaging.send(message);
                    return response.success("알림을 성공적으로 전송했습니다.");
                }catch (FirebaseMessagingException e){
                    e.printStackTrace();
                    return response.fail("알림 보내기를  실패했습니다.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return response.fail("서버에 저장된 해당 유저의 FirebaseToken이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return response.fail("해당 유저가 존재하지 않습니다.",HttpStatus.NOT_FOUND);
        }

    }
}
