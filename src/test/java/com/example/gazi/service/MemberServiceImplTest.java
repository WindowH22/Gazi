package com.example.gazi.service;

import com.example.gazi.domain.Member;
import com.example.gazi.dto.RequestMember;
import com.example.gazi.dto.Response;
import com.example.gazi.dto.ResponseToken;
import com.example.gazi.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

    String email = "dlckdgml35@gmail.com";
    String password = "kkkllll";
    String nickName = "testnick";

    RequestMember.SignUp signUpSetUp() {
        RequestMember.SignUp dto = new RequestMember.SignUp();
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setNickName(nickName);

        return dto;
    }

    RequestMember.Login loginSetUp() {
        RequestMember.Login login = new RequestMember.Login();
        login.setEmail(email);
        login.setPassword(password);
        return login;
    }

    RequestMember.Reissue reissueSetUp() {
        RequestMember.SignUp signUp = signUpSetUp();
        memberService.signUp(signUp);
        RequestMember.Login login = loginSetUp();
        ResponseToken token = (ResponseToken) memberService.login(login).getBody().getData();
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();
        return new RequestMember.Reissue(accessToken, refreshToken);
    }

    @Test
    @DisplayName("회원가입")
    void signUp() {
        // given
        RequestMember.SignUp dto = signUpSetUp();

        // when
        Member member = memberService.signUp(dto);

        // then
        assertEquals(member, memberRepository.findByEmail(dto.getEmail()).get());

    }

    @Test
    @DisplayName("이메일 중복 테스트")
    void checkEmail() {
        // given
        RequestMember.SignUp dto1 = signUpSetUp();
        RequestMember.SignUp dto2 = new RequestMember.SignUp();
        dto2.setEmail("dlckdgml35@gmail.com");
        dto2.setPassword("ddd");
        dto2.setNickName("fadf");

        // when
        try {
            Member member1 = memberService.signUp(dto1);
            Member member2 = memberService.signUp(dto2);
        }
        // then
        catch (IllegalStateException e) {
            return;
        }

    }

    @Test
    @DisplayName("닉네임 중복체크")
    void checkNickName() {
        // given
        RequestMember.SignUp dto1 = signUpSetUp();
        RequestMember.SignUp dto2 = new RequestMember.SignUp();
        dto2.setEmail("dlckdgml1235@gmail.com");
        dto2.setPassword("ddd");
        dto2.setNickName("fadf");
        // when
        try {
            Member member1 = memberService.signUp(dto1);
            Member member2 = memberService.signUp(dto2);
        }
        // then
        catch (IllegalStateException e) {
            return;
        }

    }

    @Test
    @DisplayName("로그인 성공")
    void login() {
        RequestMember.SignUp signUp = signUpSetUp();
        memberService.signUp(signUp);
        RequestMember.Login login = loginSetUp();
        assertEquals(HttpStatus.OK, memberService.login(login).getStatusCode());
    }

    @Test
    @DisplayName("등록되지 않은 이메일로 로그인")
    void noAuthorityEmailLogin() {
        // given
        RequestMember.SignUp signUp = signUpSetUp();
        memberService.signUp(signUp);
        // when
        RequestMember.Login login = loginSetUp();
        login.setEmail("dlckdgmlckdgml35@gmail.com");
        ResponseEntity<Response.Body> responseEntity = memberService.login(login);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("등록되지 않은 이메일입니다.", responseEntity.getBody().getMessage());
    }

    @Test
    @DisplayName("잘못된 비밀번호 입력")
    void noAuthorityPasswordLogin() {
        // given
        RequestMember.SignUp signUp = signUpSetUp();
        memberService.signUp(signUp);
        // when
        RequestMember.Login login = loginSetUp();
        login.setPassword("kkkll");

        ResponseEntity<Response.Body> responseEntity = memberService.login(login);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("비밀번호가 올바르지 않습니다.", responseEntity.getBody().getMessage());
    }

    @Test
    @DisplayName("로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우")
    void reissueFail() {
        // given
        RequestMember.Reissue reissueDto = reissueSetUp();
        RequestMember.Logout logoutDto = new RequestMember.Logout(reissueDto.getAccessToken(), reissueDto.getRefreshToken());
        // when
        memberService.logout(logoutDto);
        ResponseEntity<Response.Body> responseEntity = memberService.reissue(reissueDto);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("잘못된 요청입니다. 엑세스토큰으로 찾은 유저 이메일 : " + email, responseEntity.getBody().getMessage());

    }

    @Test
    @DisplayName("토큰 재발급 sucess")
    void reissue() {
        // given
        RequestMember.Reissue reissueDto = reissueSetUp();

        // when
        ResponseEntity<Response.Body> responseEntity = memberService.reissue(reissueDto);
        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("성공적인 로그아웃")
    void logout() {
        //given
        RequestMember.SignUp signUp = signUpSetUp();
        memberService.signUp(signUp);
        RequestMember.Login login = loginSetUp();
        ResponseEntity<Response.Body> loginResponse = memberService.login(login);
        ResponseToken token = (ResponseToken) loginResponse.getBody().getData();
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();
        RequestMember.Logout logoutDto = new RequestMember.Logout(accessToken,refreshToken);

        //when
        ResponseEntity<Response.Body> responseEntity= memberService.logout(logoutDto);

        //then
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("내 정보확인하기")
    void getInfo() {
        //given
        RequestMember.SignUp signUp = signUpSetUp();
        memberService.signUp(signUp);
        RequestMember.Login login = loginSetUp();
        memberService.login(login);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));

        //when
        ResponseEntity<Response.Body> responseEntity = memberService.getInfo();

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("유저 정보를 불러왔습니다.", responseEntity.getBody().getMessage());
    }

    @Test
    @DisplayName("멤버 탈퇴")
    void deleteMember() {
        //given
        RequestMember.SignUp signUp = signUpSetUp();
        memberService.signUp(signUp);
        RequestMember.Login login = loginSetUp();
        memberService.login(login);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));

        //when
        ResponseEntity<Response.Body> responseEntity = memberService.deleteMember();

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("탈퇴 되었습니다.", responseEntity.getBody().getMessage());

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