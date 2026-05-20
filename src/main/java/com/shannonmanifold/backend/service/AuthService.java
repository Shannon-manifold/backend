package com.shannonmanifold.backend.service;

import com.shannonmanifold.backend.config.JwtProvider;
import com.shannonmanifold.backend.dto.LoginRequest;
import com.shannonmanifold.backend.dto.LoginResponse;
import com.shannonmanifold.backend.dto.RegisterRequest;
import com.shannonmanifold.backend.entity.RefreshToken;
import com.shannonmanifold.backend.entity.User;
import com.shannonmanifold.backend.entity.UserRole;
import com.shannonmanifold.backend.repository.RefreshTokenRepository;
import com.shannonmanifold.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .joinDate(LocalDate.now())
                .role(UserRole.member)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.createAccessToken(user.getEmail());
        String refreshTokenString = jwtProvider.createRefreshToken(user.getEmail());

        // RefreshToken 저장/업데이트
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .map(token -> {
                    token.updateToken(refreshTokenString, LocalDateTime.now().plusDays(7));
                    return token;
                })
                .orElseGet(() -> RefreshToken.builder()
                        .user(user)
                        .token(refreshTokenString)
                        .expiryDate(LocalDateTime.now().plusDays(7))
                        .build());

        refreshTokenRepository.save(refreshToken);

        return new LoginResponse(accessToken, refreshTokenString);
    }

    @Transactional
    public LoginResponse refresh(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다. 다시 로그인해주세요.");
        }

        String newAccessToken = jwtProvider.createAccessToken(refreshToken.getUser().getEmail());
        // 리프레시 토큰도 새로 갱신 (선택 사항, 여기서는 보안을 위해 갱신)
        String newRefreshTokenString = jwtProvider.createRefreshToken(refreshToken.getUser().getEmail());
        refreshToken.updateToken(newRefreshTokenString, LocalDateTime.now().plusDays(7));

        return new LoginResponse(newAccessToken, newRefreshTokenString);
    }
}
