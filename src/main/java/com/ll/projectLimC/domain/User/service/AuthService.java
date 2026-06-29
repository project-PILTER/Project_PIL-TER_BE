package com.ll.projectLimC.domain.User.service;

import com.ll.projectLimC.domain.User.dto.login.LoginRequest;
import com.ll.projectLimC.domain.User.dto.login.LoginResponse;
import com.ll.projectLimC.domain.User.entity.User;
import com.ll.projectLimC.domain.User.repository.UserRepository;
import com.ll.projectLimC.global.jwt.JwtTokenProvider;
import com.ll.projectLimC.global.refreshToken.entity.RefreshToken;
import com.ll.projectLimC.global.refreshToken.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest request){

        // 1. 이메일로 유저 존재 여부 확인
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 2. 입력된 평문 패스워드와 DB의 암호화된 패스워드 비교
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 비밀번호가 일치하면 1일짜리 액세스 토큰 생성 후 반환
        String accessToken = tokenProvider.generateToken(user, Duration.ofDays(1));

        // Refresh Token 발행 및 DB 저장/갱신 로직 추가
        String refreshToken = tokenProvider.generateToken(user, Duration.ofDays(14));
        RefreshToken tokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .map(entity -> entity.update(refreshToken))
                .orElse(new RefreshToken(user.getId(), refreshToken));
        refreshTokenRepository.save(tokenEntity);

        return new LoginResponse(accessToken);
    }
}