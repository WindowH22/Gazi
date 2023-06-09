package com.example.gazi.service;

import com.example.gazi.domain.Member;
import com.example.gazi.domain.enums.NotificationEnum;
import com.example.gazi.dto.RequestMember;
import com.example.gazi.dto.Response.Body;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

import java.util.List;


public interface MemberService {
    Member signUp(RequestMember.SignUp signUpDto);

    ResponseEntity<Body> checkEmail(String email);

    ResponseEntity<Body> checkNickName(String nickName);

    ResponseEntity<Body> login(RequestMember.Login loginDto);

    ResponseEntity<Body> reissue(RequestMember.Reissue reissue);

    ResponseEntity<Body> logout(RequestMember.Logout logoutDto);

    ResponseEntity<Body> getInfo();

    ResponseEntity<Body> deleteMember();

    ResponseEntity<Body> changeNickName(String nickName);

    ResponseEntity<Body> validateHandling(Errors errors);

    ResponseEntity<Body> getFirebaseAccessToken(RequestMember.FirebaseToken firebaseToken);

    ResponseEntity<Body> changeNotificationByKeyword();

    ResponseEntity<Body> changeNotificationByRepost();

    ResponseEntity<Body> changeNotificationByLike();

    ResponseEntity<Body> getNotificationList(List<NotificationEnum> notificationEnums, Pageable pageable);
}
