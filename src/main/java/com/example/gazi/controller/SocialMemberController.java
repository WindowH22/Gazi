package com.example.gazi.controller;

import com.example.gazi.dto.RequestMember;
import com.example.gazi.dto.Response;
import com.example.gazi.service.SocialMemberService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/member/social")
@RestController
public class SocialMemberController {

    private final SocialMemberService socialMemberService;
    private Logger log = LoggerFactory.getLogger(getClass());

    @PostMapping("/signup")
    public ResponseEntity<Response.Body> signUp(@RequestBody RequestMember.SocialSignUp socialDto) {
        socialMemberService.saveOrUpdate(socialDto);
        log.info("회원가입 완료");
        // 회원가입 완료시 로그인
        RequestMember.SocialLogin loginDto = new RequestMember.SocialLogin();
        loginDto.setEmail(socialDto.getEmail());
        loginDto.setSecretKey(socialDto.getSecretKey());
        return socialMemberService.login(loginDto);
    }

    @PostMapping("/login")
    public ResponseEntity<Response.Body> login(@RequestBody RequestMember.SocialLogin socialDto) {
        return socialMemberService.login(socialDto);
    }
}
