package com.example.gazi.dto;

import com.example.gazi.domain.Member;
import com.example.gazi.domain.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RequestMember {

    @Getter
    @Setter
    public static class SignUp {
        private String email;
        private String password;
        private String nickName;

        public Member toMember(PasswordEncoder passwordEncoder) {
            return Member.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .nickName(nickName)
                    .role(Role.ROLE_USER)
                    .build();
        }

    }

    @Getter
    @Setter
    public static class Login {
        private String email;
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
        private String nickName;
    }

    @Getter
    public static class Email{
        private String email;
    }
}
