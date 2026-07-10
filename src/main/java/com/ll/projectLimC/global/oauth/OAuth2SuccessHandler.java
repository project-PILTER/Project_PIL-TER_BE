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
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 1. 공급자별 파편화에 대응하여 안전하게 이메일 추출
        String email = null;
        Map<String, Object> attributes = oAuth2User.getAttributes();

        if (attributes.containsKey("email")) {
            // 구글 등 일반적인 형태
            email = (String) attributes.get("email");
        } else if (attributes.containsKey("kakao_account")) {
            // 카카오 구조 분해
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");
            }
        } else if (attributes.containsKey("response")) {
            // ★ 네이버 구조 분해 (추가)
            Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
            if (naverResponse != null) {
                email = (String) naverResponse.get("email");
            }
        }

        // 2. 이메일 동의가 거부되었을 때를 대비한 공급자별 2차 방어선
        if (email == null) {
            if (attributes.containsKey("kakao_account") || attributes.containsKey("properties")) {
                // 카카오 가짜 이메일 생성
                Object id = attributes.get("id");
                email = id != null ? id.toString() + "@kakao.com" : null;
            } else if (attributes.containsKey("response")) {
                // ★ 네이버 가짜 이메일 생성 (추가)
                Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
                Object id = naverResponse != null ? naverResponse.get("id") : null;
                email = id != null ? id.toString() + "@naver.com" : null;
            }
        }

        // 이메일을 최종적으로도 추출하지 못했을 경우의 안전장치
        if (email == null) {
            throw new GlobalCustomException(ErrorCode.NOT_FOUND_THE_EMAIL_TO_SOCIAL);
        }

        // 3. 유저 조회
        User user = userService.findByEmail(email);

        // 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        // 액세스 토큰 생성 -> 패스에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
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
