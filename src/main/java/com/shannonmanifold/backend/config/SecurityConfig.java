package com.shannonmanifold.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    public SecurityConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // 세션을 사용하지 않으므로 STATELESS로 설정
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/api/v1/auth/**").permitAll() // 메인 및 로그인/회원가입 허용
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/blogs/**").permitAll() // 블로그 조회 허용
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/questions/**").permitAll() // 질문/답변 조회 허용
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/challenges/**").permitAll() // 챌린지 조회 허용
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/tutorials/**").permitAll() // 튜토리얼 조회 허용
                .anyRequest().authenticated() // 그 외 요청은 인증 필요
            )
            
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            
            // JWT 필터 추가
            .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
