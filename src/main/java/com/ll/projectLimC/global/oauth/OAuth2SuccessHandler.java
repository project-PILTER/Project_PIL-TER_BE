package com.ll.projectLimC.global.oauth;

import com.ll.projectLimC.domain.user.entity.Role;
import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.user.repository.UserRepository;
import com.ll.projectLimC.global.jwt.JwtProperties;
import com.ll.projectLimC.global.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.ll.projectLimC.global.refreshToken.entity.RefreshToken;
import com.ll.projectLimC.global.refreshToken.repository.RefreshTokenRepository;
import com.ll.projectLimC.global.jwt.JwtTokenProvider;
import com.ll.projectLimC.util.CookieUtil;
import jakarta.servlet.ServletException;
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
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String REDIRECT_PATH = "https://pilter.co.kr/auth/callback";

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final JwtProperties jwtProperties; // 🟢 UserRepository 제거됨

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        if (oAuth2User == null) return;

        // CustomService에서 넘어온 id 및 email 추출 (DB 재조회 불필요)
        Long userId = oAuth2User.getAttribute("id");
        String email = oAuth2User.getAttribute("email");
        String role = oAuth2User.getAuthorities().iterator().next().getAuthority();

        User targetUser = User.builder()
                .id(userId)
                .email(email)
                .role(Role.valueOf(role.replace("ROLE_", "")))
                .build();

        Duration accessTokenDuration = Duration.ofMillis(jwtProperties.getAccessTokenExpiration());
        Duration refreshTokenDuration = Duration.ofMillis(jwtProperties.getRefreshTokenExpiration());

        // 토큰 생성 및 쿠키 저장
        String refreshToken = tokenProvider.generateToken(targetUser, refreshTokenDuration);
        saveRefreshToken(userId, refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken, refreshTokenDuration);

        String accessToken = tokenProvider.generateToken(targetUser, accessTokenDuration);
        addAccessTokenToCookie(request, response, accessToken, accessTokenDuration);

        String targetUrl = getTargetUrl(accessToken);

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void saveRefreshToken(Long userId, String newRefreshToken){
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    private void addAccessTokenToCookie(HttpServletRequest request, HttpServletResponse response, String accessToken, Duration duration) {
        CookieUtil.deleteCookie(request, response, "accessToken");
        CookieUtil.addCookie(response, "accessToken", accessToken, (int) duration.toSeconds());
    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken, Duration duration){
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) duration.toSeconds());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private String getTargetUrl(String token){
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .encode()
                .toUriString();
    }
}