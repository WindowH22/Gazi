package com.example.gazi.service;

import com.example.gazi.config.JwtTokenProvider;
import com.example.gazi.config.SecurityUtil;
import com.example.gazi.domain.Cart;
import com.example.gazi.domain.Like;
import com.example.gazi.domain.Member;
import com.example.gazi.domain.Report;
import com.example.gazi.dto.RequestMember;
import com.example.gazi.dto.Response;
import com.example.gazi.dto.Response.Body;
import com.example.gazi.dto.ResponseMember.MemberInfo;
import com.example.gazi.dto.ResponseToken;
import com.example.gazi.repository.CartRepository;
import com.example.gazi.repository.LikeRepository;
import com.example.gazi.repository.MemberRepository;
import com.example.gazi.repository.ReportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final LikeRepository likeRepository;
    private final ReportRepository reportRepository;

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

        // 회원가입과 동시에 키워드카트 생성
        cartRepository.save(Cart.addCart(member));

        // 회원가입과 동시에 좋아요 리스트 생성
        likeRepository.save(Like.addLike(member));

        // 회원가입과 동시에 신고 리스트 생성
        reportRepository.save(Report.addReport(member));


        return member;
    }

    @Override
    public ResponseEntity<Body> checkEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            return response.fail("이미 가입된 이메일입니다.", HttpStatus.CONFLICT);
        } else {
            return response.success("회원가입이 가능한 이메일입니다.");
        }

    }

    @Override
    public ResponseEntity<Body> checkNickName(String nickName) {
        if (memberRepository.existsByNickName(nickName)) {
            return response.fail("중복된 닉네임입니다.", HttpStatus.CONFLICT);
        } else {
            return response.success("사용가능한 닉네임입니다.");
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Body> login(RequestMember.Login loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEmail()).orElse(null);
        if (member == null) {
            return response.fail("등록되지 않은 이메일입니다.", HttpStatus.UNAUTHORIZED);
        }
        // LoginDto email, password 를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.usernamePasswordAuthenticationToken();


        try {
            // 실제 검증 (사용자 비밀번호 체크)
            // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
            Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken); // 인증 정보를 기반으로 JWT 토큰 생성
            ResponseToken responseToken = jwtTokenProvider.generateToken(authentication);
            responseToken.setMemberId(member.getId());
            responseToken.setNickName(member.getNickName());
            responseToken.setEmail(member.getEmail());
            responseToken.setFirebaseToken(!member.getFireBaseToken().isEmpty()); // firebaseToken이 등록되어있는지 여부 판단

            log.info("리프레쉬 토큰 만료시간: " + jwtTokenProvider.getExpiration(responseToken.getRefreshToken()));

            // RefreshToken Redis 저장 (expirationTime 으로 자동 삭제 처리)
            redisTemplate.opsForValue()
                    .set("RT:" + authentication.getName(), responseToken.getRefreshToken(),
                            responseToken.getRefreshTokenExpirationTime(),
                            TimeUnit.MILLISECONDS
                    );


            return response.success(responseToken, "로그인에 성공했습니다.", HttpStatus.OK);
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            return response.fail("비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

    }

    @Override
    public ResponseEntity<Body> reissue(RequestMember.Reissue reissue) {
        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
            return response.fail("Refresh Token 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
        }


        // Access Token 에서 Member email 가져옴.
        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());

        log.info("유저 email: " + authentication.getName());

        log.info("엑세스 토큰 만료까지 남은시간(ms) : " + jwtTokenProvider.getExpiration(reissue.getAccessToken()));
        log.info("리프레쉬 토큰 만료까지 남은시간(ms): " + jwtTokenProvider.getExpiration(reissue.getRefreshToken()));

        // Redis 에서 Member email 을 기반으로 저장된 Refresh Token 을 가져옴.
        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
        log.info("Redis에서 찾은 refreshToken :" + refreshToken);
        log.info("리프레쉬 토큰이 존재하는지:" + redisTemplate.hasKey("RT:" + authentication.getName()));

        // 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if (ObjectUtils.isEmpty(refreshToken)) {
            log.info("Redis 에 RefreshToken 이 존재하지 않는 경우 처리");
            log.info("accessToken: " + reissue.getAccessToken());
            log.info("refreshToken: " + reissue.getRefreshToken());
            jwtTokenProvider.validateToken(refreshToken);
            return response.fail("잘못된 요청입니다. 엑세스토큰으로 찾은 유저 이메일 : " + authentication.getName(), HttpStatus.BAD_REQUEST);
        }
        if (!refreshToken.equals(reissue.getRefreshToken())) {
            return response.fail("Refresh Token 정보가 일치하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        Member member = memberRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("회원을 찾을 수 없습니다.")
        );

        // 새로운 토큰 생성
        ResponseToken tokenInfo = jwtTokenProvider.generateToken(authentication);
        tokenInfo.setMemberId(member.getId());
        tokenInfo.setEmail(member.getEmail());
        tokenInfo.setNickName(member.getNickName());
        tokenInfo.setFirebaseToken(!member.getFireBaseToken().isEmpty()); // firebaseToken이 등록되어있는지 여부 판단

        Date date = new Date(tokenInfo.getRefreshTokenExpirationTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("재발급으로 발급된 토큰에 만료날짜:" + formatter.format(date));

        // RefreshToken Redis 업데이트
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return response.success(tokenInfo, "Token 정보가 갱신되었습니다.", HttpStatus.OK);
    }

    @Transactional(readOnly = true)
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
            return response.success(MemberInfo.of(memberRes.get()), "유저 정보를 불러왔습니다.", HttpStatus.OK);
        } else {
            return response.fail("토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Body> deleteMember() {
        try {
            Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                    () -> new EntityNotFoundException("해당 회원이 존재하지 않습니다.")
            );
            memberRepository.delete(member);
            return response.success("탈퇴 되었습니다.");
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Body> changeNickName(String nickName) {
        Optional<Member> memberRes = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail());
        if (memberRes.isPresent()) {
            memberRes.get().setNickName(nickName);
            memberRepository.save(memberRes.get());
            return response.success("닉네임이" + memberRes.get().getNickName() + "으로 변경 되었습니다.");
        } else {
            return response.fail("토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }
    }

    /* 회원가입 시, 유효성 체크 */
    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Body> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();

        // 유효성 검사에 실패한 필드 목록을 받음
        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return response.fail(validatorResult, "유효성 검증 실패", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Body> getFirebaseAccessToken(RequestMember.FirebaseToken firebaseAccessToken) {
        Optional<Member> memberRes = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail());
        if (memberRes.isPresent()) {
            Member member = memberRes.get();
            member.setFireBaseToken(firebaseAccessToken.getFireBaseToken());
            memberRepository.save(member);
            return response.success();
        } else {
            return response.fail("이메일을 통해 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }
}
