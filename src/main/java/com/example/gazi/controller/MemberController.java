package com.example.gazi.controller;

import com.example.gazi.service.EmailService;
import com.example.gazi.service.KeywordService;
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
    private final KeywordService keywordService;

    @PostMapping("/signup")
    public ResponseEntity<Body> signUp(@RequestBody SignUp memberDto) {
        memberService.signUp(memberDto);

        Login loginDto = new Login();
        loginDto.setEmail(memberDto.getEmail());
        loginDto.setPassword(memberDto.getPassword());
        // 회원가입 완료시 로그인
        return memberService.login(loginDto);
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

    @PostMapping("/email-confirm")
    public ResponseEntity<Body> emailConfirm(@RequestBody Email email) throws Exception {
        return emailService.sendSimpleMessage(email.getEmail());
    }


    @GetMapping("/check-nickname")
    public ResponseEntity<Body> checkNickName(@RequestParam String nickName){
        return memberService.checkNickName(nickName);
    }

    @GetMapping("/myKeyword")
    public ResponseEntity<Body> myKeyword(){
        return keywordService.myKeywordList();
    }

    @PostMapping("/delete-member")
    public ResponseEntity<Body> deleteMember(){
        return memberService.DeleteMember();
    }
}
