package com.example.gazi.service;

import com.example.gazi.domain.Member;
import com.example.gazi.dto.RequestMember;


public interface MemberService {
    Member signUp(RequestMember.SignUp signUpDto);

    boolean checkEmail(String email);

    boolean checkNickName(String nickName);

}
