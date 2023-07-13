package com.example.gazi.service;

import com.example.gazi.domain.Member;
import com.example.gazi.dto.RequestMember;
import com.example.gazi.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceImplTest {

    @Autowired
    MemberServiceImpl memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입")
    void signUp() {
        // given
        RequestMember.SignUp dto = new RequestMember.SignUp();
        dto.setEmail("dlckdgml35@gmail.com");
        dto.setPassword("kkkllll");
        dto.setNickName("testnick");

        // when
        Member member = memberService.signUp(dto);

        // then
        assertEquals(member,memberRepository.findByEmail(dto.getEmail()).get());

    }

    @Test
    @DisplayName("이메일 중복 테스트")
    void checkEmail() {
        // given
        RequestMember.SignUp dto1 = new RequestMember.SignUp();
        dto1.setEmail("dlckdgml35@gmail.com");
        dto1.setPassword("kkkllll");
        dto1.setNickName("testnick");

        RequestMember.SignUp dto2 = new RequestMember.SignUp();
        dto2.setEmail("dlckdgml35@gmail.com");
        dto2.setPassword("ddd");
        dto2.setNickName("fadf");

        // when
        Member member1 =  memberService.signUp(dto1);
        try{
            Member member2 =  memberService.signUp(dto2);
        }catch (IllegalStateException e){
            return;
        }
        // then

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