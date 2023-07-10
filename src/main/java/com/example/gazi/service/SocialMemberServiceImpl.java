package com.example.gazi.service;

import com.example.gazi.config.JwtTokenProvider;
import com.example.gazi.domain.Member;
import com.example.gazi.dto.RequestMember;
import com.example.gazi.dto.Response;
import com.example.gazi.dto.ResponseToken;
import com.example.gazi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class SocialMemberServiceImpl implements SocialMemberService {

    private final RedisTemplate redisTemplate;
    private final MemberRepository memberRepository;
    private final Response response;
    private final JwtTokenProvider jwtTokenProvider;
    @Value("${social.secretKey}")
    private String secretKey;
    @Override
    public ResponseEntity<Response.Body> saveOrUpdate(RequestMember.SocialSignUp social) {

        Member member = memberRepository.findByEmail(social.getEmail())
                .map(entity -> entity.update(social.getNickName(), social.getEmail(), social.getProvider()))
                .orElse(social.toEntity());
        memberRepository.save(member);
        return response.success("회원가입 완료");
    }

    @Override
    public ResponseEntity<Response.Body> login(RequestMember.SocialLogin socialDto) {

        if (!socialDto.getSecretKey().equals(secretKey)) {
            return response.fail("당신은 해당 api를 쓸 수 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        Member member = memberRepository.findByEmail(socialDto.getEmail()).orElse(null);
        if (member == null) {
            return response.fail("등록되지 않은 이메일입니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            // 소셜 로그인이나 토큰 기반 인증과 같이 암호를 포함하지 않는 인증 메커니즘을 사용할 때 여전히 관련 사용자 식별자 또는 토큰을 주체로 사용하여 인증 개체를 만들 수 있다는 점은 주목할 가치가 있습니다.
            Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
            Authentication authentication = new UsernamePasswordAuthenticationToken(socialDto.getEmail(), null, authorities);
            ResponseToken responseToken = jwtTokenProvider.generateToken(authentication);
            responseToken.setMemberId(member.getId());
            responseToken.setNickName(member.getNickName());
            responseToken.setEmail(member.getEmail());
            responseToken.setNotificationByKeyword(member.getNotificationByKeyword());
            responseToken.setNotificationByRepost(member.getNotificationByRepost());
            responseToken.setNotificationByLike(member.getNotificationByLike());

            // firebaseToken이 등록되어있는지 여부 판단
            if (member.getFireBaseToken() == null) {
                responseToken.setFirebaseToken(false);
            } else {
                responseToken.setFirebaseToken(true);
            }

            // RefreshToken Redis 저장 (expirationTime 으로 자동 삭제 처리)
            redisTemplate.opsForValue()
                    .set("RT:" + member.getEmail(), responseToken.getRefreshToken(),
                            responseToken.getRefreshTokenExpirationTime(),
                            TimeUnit.MILLISECONDS
                    );

            return response.success(responseToken, "로그인에 성공했습니다.", HttpStatus.OK);
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            return response.fail("비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

    }

}
