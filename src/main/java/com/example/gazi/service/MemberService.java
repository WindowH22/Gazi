package com.example.gazi.service;

import com.example.gazi.domain.Member;
import com.example.gazi.dto.RequestMember;
import com.example.gazi.dto.Response.Body;
import org.springframework.http.ResponseEntity;


public interface MemberService {
    Member signUp(RequestMember.SignUp signUpDto);

    ResponseEntity<Body> checkEmail(String email);

    ResponseEntity<Body> checkNickName(String nickName);

    ResponseEntity<Body> login(RequestMember.Login loginDto);

    ResponseEntity<Body> reissue(RequestMember.Reissue reissue);

    ResponseEntity<Body> logout(RequestMember.Logout logoutDto);

    ResponseEntity<Body> getInfo();

    // 아이디 찾기

    // 비밀번호 찾기

    // 닉네임 수정
    ResponseEntity<Body> changeNickName(String nickName);
}
