package com.example.gazi.service;

import com.example.gazi.domain.Member;
import com.example.gazi.dto.RequestMember;
import com.example.gazi.dto.Response;
import org.springframework.http.ResponseEntity;

public interface SocialMemberService {

    ResponseEntity<Response.Body> saveOrUpdate(RequestMember.SocialSignUp social );

    ResponseEntity<Response.Body> login(RequestMember.SocialLogin socialDto);
}
