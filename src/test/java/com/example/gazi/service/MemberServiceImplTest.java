package com.example.gazi.service;

import com.example.gazi.domain.Member;
import com.example.gazi.dto.RequestMember;
import com.example.gazi.dto.Response;
import com.example.gazi.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class MemberServiceImplTest {

    @Autowired
    MemberServiceImpl memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    Response response;

    RequestMember.SignUp setUp(){
        RequestMember.SignUp dto = new RequestMember.SignUp();
        dto.setEmail("dlckdgml35@gmail.com");
        dto.setPassword("kkkllll");
        dto.setNickName("testnick");

        return dto;
    }

    @Test
    @DisplayName("회원가입")
    void signUp() {
        // given
        RequestMember.SignUp dto = setUp();

        // when
        Member member = memberService.signUp(dto);

        // then
        assertEquals(member,memberRepository.findByEmail(dto.getEmail()).get());

    }

    @Test
    @DisplayName("이메일 중복 테스트")
    void checkEmail() {
        // given
        RequestMember.SignUp dto1 = setUp();
        RequestMember.SignUp dto2 = new RequestMember.SignUp();
        dto2.setEmail("dlckdgml35@gmail.com");
        dto2.setPassword("ddd");
        dto2.setNickName("fadf");

        // when
        try{
            Member member1 =  memberService.signUp(dto1);
            Member member2 =  memberService.signUp(dto2);
        }
        // then
        catch (IllegalStateException e){
            return;
        }


    }

    @Test
    void checkNickName() {
    }

    @Test
    void login() {
    }

    @Test
    void reissue() {
    }

    @Test
    void logout() {
    }

    @Test
    void getInfo() {
    }

    @Test
    void deleteMember() {
    }

    @Test
    void changeNickName() {
    }

    @Test
    void validateHandling() {
    }

    @Test
    void getFirebaseAccessToken() {
    }

    @Test
    void changeNotificationByKeyword() {
    }

    @Test
    void changeNotificationByRepost() {
    }

    @Test
    void changeNotificationByLike() {
    }
}