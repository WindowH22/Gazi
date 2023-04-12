package com.example.gazi.dto;

import com.example.gazi.domain.Member;
import com.example.gazi.domain.Role;
import lombok.Getter;
import lombok.Setter;
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

}
