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

@Component // ⭐️ Spring Bean 등록을 위해 @Component 명시
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String REDIRECT_PATH = "https://pilter.co.kr/auth/callback";

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    @Override
    @SuppressWarnings("unchecked")
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        if (oAuth2User == null) return;

        // 1. 기본 어트리뷰트 맵 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println("🔍 [Handler] oAuth2User Attributes: " + attributes);

        String email = null;
        String nickname = null;
        String provider = null;
        String providerId = null;
        String profileImage = null;

        // A. 카카오 로그인인 경우
        if (attributes.containsKey("kakao_account")) {
            provider = "kakao";
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if (profile != null) {
                    nickname = (String) profile.get("nickname");
                    String kakaoImg = (String) profile.get("profile_image_url");
                    if (kakaoImg == null || kakaoImg.isEmpty()) {
                        kakaoImg = (String) profile.get("thumbnail_image_url");
                    }
                    profileImage = (kakaoImg != null && !kakaoImg.isEmpty()) ? kakaoImg : "/api/images/default-profile.png";
                }
            }
            if (profileImage == null) profileImage = "/api/images/default-profile.png";
            providerId = String.valueOf(attributes.get("id"));

            // B. 네이버 로그인인 경우
        } else if (attributes.containsKey("response")) {
            provider = "naver";
            Map<String, Object> responseMap = (Map<String, Object>) attributes.get("response");
            if (responseMap != null) {
                email = (String) responseMap.get("email");
                nickname = (String) responseMap.get("name");
                String naverImg = (String) responseMap.get("profile_image");
                profileImage = (naverImg != null && !naverImg.isEmpty()) ? naverImg : "/api/images/default-profile.png";
                providerId = (String) responseMap.get("id");
            }

            // C. 구글 로그인인 경우
        } else {
            provider = "google";
            email = (String) attributes.get("email");
            nickname = (String) attributes.get("name");
            if (nickname == null || nickname.trim().isEmpty()) {
                nickname = (String) attributes.get("given_name");
            }
            String googleImg = (String) attributes.get("picture");
            profileImage = (googleImg != null && !googleImg.isEmpty()) ? googleImg : "/api/images/default-profile.png";
            providerId = (String) attributes.get("sub");
        }

        // 2. 이메일 기반 DB 조회
        Long userId = null;
        if (email != null) {
            userId = userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElse(null);
        }

        if (userId == null) {
            User newUser = User.builder()
                    .email(email)
                    .nickname(nickname != null ? nickname : "User_" + System.currentTimeMillis() % 10000)
                    .provider(provider)
                    .providerId(providerId)
                    .profileImage(profileImage)
                    .role(Role.USER)
                    .password("")
                    .createdAt(OffsetDateTime.now(ZoneId.of("Asia/Seoul")))
                    .build();

            User savedUser = userRepository.save(newUser);
            userId = savedUser.getId();
        }

        User targetUser = User.builder()
                .id(userId)
                .email(email)
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .profileImage(profileImage)
                .role(Role.USER)
                .build();

        // ⭐️ 2. YML 설정(application-secret.yml)의 만료 시간(밀리초)을 Duration 객체로 생성
        Duration accessTokenDuration = Duration.ofMillis(jwtProperties.getAccessTokenExpiration());
        Duration refreshTokenDuration = Duration.ofMillis(jwtProperties.getRefreshTokenExpiration());

        // 4. 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(targetUser, refreshTokenDuration);
        saveRefreshToken(userId, refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken, refreshTokenDuration); // 👈 duration 인자 추가

        // 5. 액세스 토큰 생성 -> 쿠키에 저장 -> 리다이렉트
        String accessToken = tokenProvider.generateToken(targetUser, accessTokenDuration);
        addAccessTokenToCookie(request, response, accessToken, accessTokenDuration); // 👈 duration 인자 추가

        String targetUrl = getTargetUrl(accessToken);

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // ⭐️ 3. 만료 시간을 인자로 전달받도록 메서드 시그니처 수정
    private void addAccessTokenToCookie(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String accessToken,
                                        Duration duration) {
        int cookieMaxAge = (int) duration.toSeconds();
        CookieUtil.deleteCookie(request, response, "accessToken");
        CookieUtil.addCookie(response, "accessToken", accessToken, cookieMaxAge);
    }

    private void saveRefreshToken(Long userId, String newRefreshToken){
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    // ⭐️ 4. 만료 시간을 인자로 전달받도록 메서드 시그니처 수정
    private void addRefreshTokenToCookie(HttpServletRequest request,
                                         HttpServletResponse response,
                                         String refreshToken,
                                         Duration duration){
        int cookieMaxAge = (int) duration.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
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