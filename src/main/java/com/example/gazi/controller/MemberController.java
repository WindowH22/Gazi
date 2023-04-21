package com.example.gazi.controller;

import com.example.gazi.dto.RequestMember;
import com.example.gazi.service.EmailService;
import com.example.gazi.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.gazi.dto.RequestMember.*;
import static com.example.gazi.dto.Response.Body;

@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/member")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;
    @PostMapping("/signup")
    public ResponseEntity<SignUp> signUp(@RequestBody SignUp memberDto) {
        memberService.signUp(memberDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login loginDto) {
        return memberService.login(loginDto);
    }

    @PostMapping("/reissue")
    public ResponseEntity<Body> reissue(@RequestBody Reissue reissueDto) {
        return memberService.reissue(reissueDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Body> logout(@RequestBody Logout logoutDto) {
        return memberService.logout(logoutDto);
    }

    @GetMapping
    public ResponseEntity<Body> getInfo() {
        return memberService.getInfo();
    }

    @PostMapping("/change-nickname")
    public ResponseEntity<Body> changeNickName(@RequestBody NickName nickName){
        return memberService.changeNickName(nickName.getNickName());
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Body> checkNickName(@RequestBody NickName nickName){
        return memberService.checkNickName(nickName.getNickName());
    }

    @PostMapping("/emailConfirm")
    public String emailConfirm(@RequestBody Email email) throws Exception {
        String confirm = emailService.sendSimpleMessage(email.getEmail());
        return confirm;
    }
}
