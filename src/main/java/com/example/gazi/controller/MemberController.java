package com.example.gazi.controller;

import com.example.gazi.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.gazi.dto.RequestMember.SignUp;

;


@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/member")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<SignUp> signUp(@RequestBody SignUp memberDto) {

        if (memberService.checkEmail(memberDto.getEmail()) && memberService.checkNickName(memberDto.getNickName())) {
            memberService.signUp(memberDto);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
