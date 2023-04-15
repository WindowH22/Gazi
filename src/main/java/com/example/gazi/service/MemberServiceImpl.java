package com.example.gazi.service;

import com.example.gazi.config.JwtTokenProvider;
import com.example.gazi.config.SecurityUtil;
import com.example.gazi.domain.Member;
import com.example.gazi.dto.RequestMember;
import com.example.gazi.dto.Response;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.dto.ResponseMember.MemberInfo;
import com.example.gazi.dto.ResponseToken;
import com.example.gazi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder managerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate redisTemplate;
    private final Response response;


    @Transactional
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

    @Transactional
    @Override
    public ResponseEntity<Body> login(RequestMember.Login loginDto) {
        if (memberRepository.findByEmail(loginDto.getEmail()).orElse(null) == null) {
            return response.fail("해당 유저가 존재하지않습니다.", HttpStatus.BAD_REQUEST);
        }
        // LoginDto email, password 를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.usernamePasswordAuthenticationToken();

        // 실제 검증 (사용자 비밀번호 체크)
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);

        // 인증 정보를 기반으로 JWT 토큰 생성
        ResponseToken responseToken = jwtTokenProvider.generateToken(authentication);

        // RefreshToken Redis 저장 (expirationTime 으로 자동 삭제 처리)
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(),
                        responseToken.getRefreshToken(),
                        responseToken.getRefreshTokenExpirationTime(),
                        TimeUnit.MILLISECONDS);

        return response.success(responseToken,"로그인에 성공했습니다.",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Body> reissue(RequestMember.Reissue reissue) {
        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
            return response.fail("Refresh Token 정보가 유효하지 않습니다.",HttpStatus.BAD_REQUEST);
        }

        // Access Token 에서 Member email 가져옴.
        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());

        // Redis 에서 Member email 을 기반으로 저장된 Refresh Token 을 가져옴.
        String refreshToken = (String)redisTemplate.opsForValue().get("RT:" + authentication.getName());

        // 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if(ObjectUtils.isEmpty(refreshToken)) {
            return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        }
        if(!refreshToken.equals(reissue.getRefreshToken())) {
            return response.fail("Refresh Token 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 새로운 토큰 생성
        ResponseToken tokenInfo = jwtTokenProvider.generateToken(authentication);

        // RefreshToken Redis 업데이트
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return response.success(tokenInfo, "Token 정보가 갱신되었습니다.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Body> logout(RequestMember.Logout logoutDto) {
        // Access Token 검증
        if (!jwtTokenProvider.validateToken(logoutDto.getAccessToken())) {
            return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        }

        // Access Token 에서 Member email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(logoutDto.getAccessToken());


        // Redis 에서 해당 Member email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisTemplate.opsForValue().get("AT:" + authentication.getName()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("AT:" + authentication.getName());
        }
        // Redis 에서 해당 Member email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("RT:" + authentication.getName());
        }

        // Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(logoutDto.getAccessToken());
        redisTemplate.opsForValue()
                .set(logoutDto.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

        return response.success("로그아웃 되었습니다.");
    }

    @Override
    public ResponseEntity<Body> getInfo() {
        Optional<Member> memberRes = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail());
        if (memberRes.isPresent()) {
            return response.success(MemberInfo.of(memberRes.get()),"유저 정보를 불러왔습니다.",HttpStatus.OK);
        } else {
            return response.fail("토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }
    }


}
