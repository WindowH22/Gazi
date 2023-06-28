package com.example.gazi.service;

import com.example.gazi.domain.KeywordCart;
import com.example.gazi.domain.Member;
import com.example.gazi.domain.NotificationEnum;
import com.example.gazi.dto.RequestFCMNotificationDto;
import com.example.gazi.dto.Response;
import com.example.gazi.repository.KeywordCartRepository;
import com.example.gazi.repository.MemberRepository;
import com.example.gazi.repository.NotificationRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class FCMNotificationServiceImpl implements FCMNotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final MemberRepository memberRepository;
    private final KeywordCartRepository keywordCartRepository;
    private final NotificationRepository notificationRepository;
    private final Response response;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override // 타겟 지정
    public void sendNotificationByToken(RequestFCMNotificationDto requestDto) {

        Optional<Member> member = memberRepository.findById(requestDto.getTargetUserId());

        if (member.isPresent()) {
            if (member.get().getFireBaseToken() != null) {
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
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                    return response.fail("알림 보내기를  실패했습니다.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return response.fail("서버에 저장된 해당 유저의 FirebaseToken이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return response.fail("해당 유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

    }

    // 단체 메시지 보낼시
    public void sendGroupNotification(RequestFCMNotificationDto requestDto) throws FirebaseMessagingException {

        List<String> fireBaseTokenList = new ArrayList<>();

        for (Member member : requestDto.getMemberList()) {
            if (member.getFireBaseToken() != null) {
                fireBaseTokenList.add(member.getFireBaseToken());
            } else {
                return response.fail("서버에 저장된 " + member.getNickName() + "FirebaseToken이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
            }

        }
        Notification notification = Notification.builder()
                .setTitle(requestDto.getTitle())
                .setBody(requestDto.getBody())
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(fireBaseTokenList)
                .setNotification(notification)
                .build();
        BatchResponse batchResponse = FirebaseMessaging.getInstance().sendMulticast(message);

        if (batchResponse.getFailureCount() > 0) {
            List<SendResponse> responses = batchResponse.getResponses();
            List<String> failedTokens = new ArrayList<>();
            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    // The order of responses corresponds to the order of the registration tokens.
                    failedTokens.add(fireBaseTokenList.get(i));
                }
            }
            return response.fail("List of tokens that caused failures: " + failedTokens, HttpStatus.BAD_REQUEST);
        }

        return response.success(batchResponse.getSuccessCount() + "messages were sent successfully");
    }

    public void sendMessageByKeyword(Member member, Post post, List<Long> keywordIdList) {
        // 관심 키워드 설정한 유저들에게 알림 보내기
        List<KeywordCart> keywordCarts = keywordCartRepository.findAllByKeywordIdIn(keywordIdList);

        // 교통수단 키워드

        // 교통이슈 키워드

        Set<Member> memberIdByKeyword = new HashSet<>();
        for (KeywordCart keywordCart : keywordCarts) {
            Member memberByKeyword = keywordCart.getCart().getMember();
            if (!memberByKeyword.getId().equals(member.getId()) && memberByKeyword.getNotificationByKeyword()) {
                memberIdByKeyword.add(memberByKeyword);
            }
        }

        RequestFCMNotificationDto requestDto = RequestFCMNotificationDto.builder().
                title("{교통수단}에서 {교통이슈}가 발생했어요.").
                body(postTitle).
                memberList(memberIdByKeyword)
                .build();
        try {
            sendGroupNotification(requestDto);

            for(Member memberKeyword : memberIdByKeyword){
                notificationRepository.save(com.example.gazi.domain.Notification.toEntity(requestDto,memberKeyword, NotificationEnum.KEYWORD));
            }

            log.info("알림 보내기 성공");

        } catch (FirebaseMessagingException e) {
            log.debug("알림 보내기 에러 발생");
            log.debug(e.getMessage());
        }
    }


}
