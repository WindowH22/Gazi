package com.example.gazi.config;


import com.example.gazi.oauth.CustomOauth2UserService;
import com.example.gazi.oauth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;
    private final CustomOauth2UserService userService;
    private final OAuth2SuccessHandler successHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return
                http.csrf().disable()
                        .sessionManagement(session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )


                        .headers()
                        .frameOptions().disable().and()
                        .logout().disable() // 로그아웃 사용 X
                        .formLogin().disable() // 폼 로그인 사용 X

                        .authorizeRequests()
                        .requestMatchers("/api/v1/member/signup", "/api/v1/member/login","/api/v1/member/email-confirm","/api/v1/member/reissue").permitAll()
                        .anyRequest().authenticated() // 나머지 요청들은 모두 인증 절차 수행해야함

                        .and()
                        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class)
                        .oauth2Login()
//                        .defaultSuccessUrl()
                        .successHandler(successHandler)
                        .userInfoEndpoint()
                        .userService(userService)
                        .and()
                        .and()
                        .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
