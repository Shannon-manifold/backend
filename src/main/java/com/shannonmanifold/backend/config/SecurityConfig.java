package com.shannonmanifold.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // REST API의 경우 CSRF 비활성화 (선택 사항)
            .csrf(csrf -> csrf.disable())
            
            // 모든 요청에 대해 인증 없이 접근 허용 (개발 초기 단계)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            
            // 기본 제공되는 로그인 폼과 HTTP Basic 인증 비활성화
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
