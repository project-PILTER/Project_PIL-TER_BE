package com.ll.projectLimC.domain.auth.service;

import com.ll.projectLimC.domain.user.dto.UserResponse;
import com.ll.projectLimC.domain.auth.dto.LoginRequest;
import com.ll.projectLimC.domain.auth.dto.LoginResponse;
import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.user.repository.UserRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import com.ll.projectLimC.global.jwt.JwtProperties;
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
    private final JwtProperties jwtProperties;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Principal principal) {
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

        // 이메일로 유저 존재 여부 확인
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        // 입력된 평문 패스워드와 DB의 암호화된 패스워드 비교
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new GlobalCustomException(ErrorCode.SIGN_IN_PASSWORD_NOT_MATCH);
        }

        // 토큰 생성
        String accessToken = tokenProvider.generateToken(
                user,
                Duration.ofMillis(jwtProperties.getAccessTokenExpiration())
        );

        String refreshToken = tokenProvider.generateToken(
                user,
                Duration.ofMillis(jwtProperties.getRefreshTokenExpiration())
        );

        // Refresh Token DB 저장/갱신
        RefreshToken tokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .map(entity -> entity.update(refreshToken))
                .orElseGet(() -> new RefreshToken(user.getId(), refreshToken));

        refreshTokenRepository.save(tokenEntity);

        // Access Token 쿠키 생성 (프론트 serverApiGet의 cookies() 대응)
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMillis(jwtProperties.getAccessTokenExpiration()))
                .sameSite("None")
                .build();

        // Refresh Token 쿠키 생성
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMillis(jwtProperties.getRefreshTokenExpiration()))
                .sameSite("None")
                .build();

        // 응답 헤더에 쿠키 2개 각각 추가 (addHeader 사용)
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return new LoginResponse(accessToken);
    }

    // 로그아웃은 회원 삭제가 아니라 '리프레시 토큰'을 지워주는 것
    @Transactional
    public void logout(Long id, HttpServletResponse response){
        refreshTokenRepository.findByUserId(id)
                .ifPresent(refreshTokenRepository::delete);

        // 두 쿠키 모두 만료 시키기
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true).secure(true).path("/").maxAge(0).sameSite("None").build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true).secure(true).path("/").maxAge(0).sameSite("None").build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}