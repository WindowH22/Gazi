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
    @DisplayName("닉네임 중복체크")
    void checkNickName() {
        // given
        RequestMember.SignUp dto1 = setUp();
        RequestMember.SignUp dto2 = new RequestMember.SignUp();
        dto2.setEmail("dlckdgml1235@gmail.com");
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

    RequestMember.Login loginSetUp(){
        RequestMember.Login login = new RequestMember.Login();
        login.setEmail("dlckdgml35@gmail.com");
        login.setPassword("kkkllll");
        return login;
    }

    @Test
    @DisplayName("로그인 성공")
    void login() {
        RequestMember.SignUp signUp = setUp();
        memberService.signUp(signUp);
        RequestMember.Login login = loginSetUp();
        assertEquals(HttpStatus.OK,memberService.login(login).getStatusCode());
    }

    @Test
    @DisplayName("등록되지 않은 이메일로 로그인")
    void noAuthorityEmailLogin() {
        // given
        RequestMember.SignUp signUp = setUp();
        memberService.signUp(signUp);
        // when
        RequestMember.Login login = loginSetUp();
        login.setEmail("dlckdgmlckdgml35@gmail.com");
        ResponseEntity<Response.Body> responseEntity = memberService.login(login);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED,responseEntity.getStatusCode());
        assertEquals("등록되지 않은 이메일입니다.",responseEntity.getBody().getMessage());
    }

    @Test
    @DisplayName("잘못된 비밀번호 입력")
    void noAuthorityPasswordLogin() {
        // given
        RequestMember.SignUp signUp = setUp();
        memberService.signUp(signUp);
        // when
        RequestMember.Login login = loginSetUp();
        login.setPassword("kkkll");

        ResponseEntity<Response.Body> responseEntity = memberService.login(login);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED,responseEntity.getStatusCode());
        assertEquals("비밀번호가 올바르지 않습니다.",responseEntity.getBody().getMessage());
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