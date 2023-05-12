package com.example.gazi.dto;

import com.example.gazi.domain.Member;
import com.example.gazi.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RequestMember {

    @Getter
    @Setter
    public static class SignUp {
        @NotBlank
        private String email;
        @NotBlank
        private String password;
        @NotBlank
        private String nickName;

        public Member toMember(PasswordEncoder passwordEncoder) {
            return Member.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .nickName(nickName)
                    .isAgree(true)
                    .role(Role.ROLE_USER)
                    .build();
        }

    }

    @Getter
    @Setter
    public static class Login {
        @NotBlank(message = "Email는 필수 입력 값입니다.")
        private String email;

        @NotBlank(message = "Password는 필수 입력 값입니다.")
        private String password;

        public UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken() {
            return new UsernamePasswordAuthenticationToken(email, password);
        }
    }

    @Getter
    @Setter
    public static class Reissue {
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Setter
    public static class Logout {
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    public static class NickName{
        @Pattern(regexp = "^[A-Za-z0-9가-힣]{1,7}$", message = "7글자 까지만 허용됩니다. (ㄱ,ㄴ,ㄷ 같은형식 입력 불가능)") // 7글자 수정 영어 소문자, 대문자,번호, 한글(ㄱ,ㄴ,ㄷ 같은형식 입력불가능)
        private String nickName;
    }

    @Getter
    public static class Email{
        @NotBlank(message = "아이디는 필수 입력 값입니다.")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$",message = "올바른 이메일 형식이 아닙니다.")
        private String email;
    }
}
