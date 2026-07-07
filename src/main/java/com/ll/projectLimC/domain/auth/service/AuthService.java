package com.ll.projectLimC.domain.auth.service;

import com.ll.projectLimC.domain.user.dto.UserResponse;
import com.ll.projectLimC.domain.auth.dto.LoginRequest;
import com.ll.projectLimC.domain.auth.dto.LoginResponse;
import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.user.repository.UserRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import com.ll.projectLimC.global.jwt.JwtTokenProvider;
import com.ll.projectLimC.global.refreshToken.entity.RefreshToken;
import com.ll.projectLimC.global.refreshToken.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Principal principal) {
        // 1. 로그인이 안 되어 있는 상태라면 즉시 401 예외 발생
        if (principal == null) {
            throw new GlobalCustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        // 2. 단일 신뢰 원천(SSOT)인 DB에서 로그인한 유저 정보 조회
        User loginUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 3. 응답 규격(DTO)으로 이쁘게 빌드하여 반환
        return UserResponse.builder()
                .id(loginUser.getId())
                .email(loginUser.getEmail())
                .name(loginUser.getNickname())
                .role(loginUser.getRole().toString())
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletResponse response){

        // 1. 이메일로 유저 존재 여부 확인
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        // 2. 입력된 평문 패스워드와 DB의 암호화된 패스워드 비교
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new GlobalCustomException(ErrorCode.SIGN_IN_PASSWORD_NOT_MATCH);
        }

        // 3. 비밀번호가 일치하면 1일짜리 액세스 토큰 생성 후 반환
        String accessToken = tokenProvider.generateToken(user, Duration.ofDays(1));

        // Refresh Token 발행 및 DB 저장/갱신 로직 추가
        String refreshToken = tokenProvider.generateToken(user, Duration.ofDays(7));

        RefreshToken tokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .map(entity -> entity.update(refreshToken))
                .orElseGet(() -> new RefreshToken(user.getId(), refreshToken));

        refreshTokenRepository.save(tokenEntity);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)                    // 자바스크립트 접근 차단 (XSS 방어)
                .secure(true)                      // HTTPS 환경에서만 전송
                .path("/")                         // 모든 경로에서 쿠키 유효
                .maxAge(Duration.ofDays(7))        // 쿠키 만료 시간 (7일)
                .sameSite("None")                  // 크로스 도메인(프론트-백 주소 다를 때) 간 전송 허용
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new LoginResponse(accessToken);
    }

    // 로그아웃은 회원 삭제가 아니라 '리프레시 토큰'을 지워주는 것
    @Transactional
    public void logout(Long id, HttpServletResponse response){
        refreshTokenRepository.findByUserId(id)
                .ifPresent(refreshTokenRepository::delete);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // 즉시 만료
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}