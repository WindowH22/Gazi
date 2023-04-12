package com.example.gazi.service;

import com.example.gazi.domain.Member;
import com.example.gazi.dto.RequestMember;
import com.example.gazi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Member signUp(RequestMember.SignUp signUpDto) {
        Member member = signUpDto.toMember(passwordEncoder);
        memberRepository.save(member);
        return member;
    }

    @Override
    public boolean checkEmail(String email) {
        return !memberRepository.existsByEmail(email);
    }

    @Override
    public boolean checkNickName(String nickName) {
        return !memberRepository.existsByNickName(nickName);
    }
}
