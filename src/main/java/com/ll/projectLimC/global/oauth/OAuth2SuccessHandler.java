package com.ll.projectLimC.global.oauth;

import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import com.ll.projectLimC.global.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.ll.projectLimC.global.refreshToken.entity.RefreshToken;
import com.ll.projectLimC.global.refreshToken.repository.RefreshTokenRepository;
import com.ll.projectLimC.domain.user.service.UserService;
import com.ll.projectLimC.global.jwt.JwtTokenProvider;
import com.ll.projectLimC.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "https://pilter.co.kr/auth/callback";

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

    // 💡 더 이상 Handler 단에서 가입 여부를 조회할 필요가 없으므로 UserService 주입 불필요!
    // private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 1. CustomService가 안전하게 서빙해준 주 키(ID)와 이메일 가공 없이 바로 추출
        Long userId = (Long) attributes.get("id");
        String email = (String) attributes.get("email");

        System.out.println("🎯 [디버깅] 핸들러 안전 진입 완료!");
        System.out.println("🎯 추출된 User ID: " + userId);
        System.out.println("🎯 추출된 User Email: " + email);

        // 예외 방어선
        if (userId == null || email == null) {
            throw new IllegalArgumentException("소셜 세션 컨텍스트에 유저 식별 정보가 누락되었습니다.");
        }

        // 2. 가상의 가짜 User 엔티티 객체를 빌드하여 토큰 프로바이더에 전달
        // (토큰 생성 시 내부적으로 user.getId(), user.getEmail() 등만 쓰기 때문에 굳이 영속 객체일 필요가 없음)
        User mockUser = User.builder()
                .id(userId)
                .email(email)
                .build();

        // 3. 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(mockUser, REFRESH_TOKEN_DURATION);
        saveRefreshToken(userId, refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        // 4. 액세스 토큰 생성 -> 패스에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(mockUser, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        // 인증 관련 설정값, 쿠키 제거
        clearAuthenticationAttributes(request, response);

        // 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 생성된 리프레시 토큰을 전달받아 DB에 저장
    private void saveRefreshToken(Long userId, String newRefreshToken){
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    // 생성된 리프레시 토큰을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest request,
                                         HttpServletResponse response,
                                         String refreshToken){
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    // 인증관련 설정값, 쿠키 제거
    private void clearAuthenticationAttributes(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    // 액세스 토큰을 패스에 추가
    private String getTargetUrl(String token){
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .encode()
                .toUriString();
    }
}
