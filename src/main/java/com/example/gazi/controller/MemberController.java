package com.example.gazi.controller;

import com.example.gazi.domain.NotificationEnum;
import com.example.gazi.dto.RequestMember;
import com.example.gazi.service.EmailService;
import com.example.gazi.service.KeywordService;
import com.example.gazi.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
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
    public ResponseEntity<Body> signUp(@RequestBody @Valid SignUp memberDto, Errors errors) {

        memberService.validateHandling(errors);
        memberService.signUp(memberDto);

        Login loginDto = new Login();
        loginDto.setEmail(memberDto.getEmail());
        loginDto.setPassword(memberDto.getPassword());
        // 회원가입 완료시 로그인
        return memberService.login(loginDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid Login loginDto, Errors errors) {
        memberService.validateHandling(errors);
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
    public ResponseEntity<Body> changeNickName(@RequestBody @Valid NickName nickName, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            return memberService.validateHandling(errors);
        }
        return memberService.changeNickName(nickName.getNickName());
    }

    @PostMapping("/email-confirm")
    public ResponseEntity<Body> emailConfirm(@RequestBody @Valid Email email, Errors errors) throws Exception {

        if (errors.hasErrors()) {
            return memberService.validateHandling(errors);
        }
        return emailService.sendSimpleMessage(email.getEmail());
    }

    @PostMapping("/check-nickname")
    public ResponseEntity<Body> checkNickName(@RequestBody @Valid NickName nickName, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            return memberService.validateHandling(errors);
        }
        return memberService.checkNickName(nickName.getNickName());
    }

    @GetMapping("/my-keyword")
    public ResponseEntity<Body> myKeyword() {
        return keywordService.myKeywordList();
    }

    @PostMapping("/delete-member")
    public ResponseEntity<Body> deleteMember() {
        return memberService.deleteMember();
    }

    @PostMapping("/get-firebase-access-key")
    public ResponseEntity<Body> getFirebaseAccessToken(@RequestBody RequestMember.FirebaseToken firebaseToken) {
        return memberService.getFirebaseAccessToken(firebaseToken);
    }

    @PostMapping("/change_notification_keyword")
    public ResponseEntity<Body> changeNotificationByKeyword(){
        return memberService.changeNotificationByKeyword();
    }

    @PostMapping("/change_notification_repost")
    public ResponseEntity<Body> changeNotificationByRepost(){
        return memberService.changeNotificationByRepost();
    }

    @PostMapping("/change_notification_like")
    public ResponseEntity<Body> changeNotificationByLike(){
        return memberService.changeNotificationByLike();
    }

    @GetMapping("/get_notification_list")
    public ResponseEntity<Body> getNotificationList(@RequestParam NotificationEnum notificationEnum, Pageable pageable){

        return memberService.getNotificationList(notificationEnum,pageable);
    }
}
