package com.ll.projectLimC.global.oauth;

import com.ll.projectLimC.domain.user.entity.Role;
import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.user.repository.UserRepository;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "https://pilter.co.kr/auth/callback";

    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    // private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException { // 💡 ServletException 추가

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 1. 기본 어트리뷰트 맵 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println("🔍 [Handler] oAuth2User Attributes: " + attributes);

        // 2. 일차적으로 맵에서 데이터 추출 시도
        String email = null;
        String nickname = null;
        String provider = null;
        String providerId = null;
        String profileImage = null;

        // A. 카카오 로그인인 경우 (kakao_account 키의 존재 여부로 판단)
        if (attributes.containsKey("kakao_account")) {
            provider = "kakao";
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");

            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null) {
                nickname = (String) profile.get("nickname");
                String kakaoImg = (String) profile.get("profile_image_url");
                profileImage = (kakaoImg != null && !kakaoImg.isEmpty()) ? kakaoImg : "/api/images/default-profile.png";
            } else {
                profileImage = "/api/images/default-profile.png";
            }
            providerId = String.valueOf(attributes.get("id"));

            // B. 네이버 로그인인 경우 (response 키의 존재 여부로 판단)
        } else if (attributes.containsKey("response")) {
            provider = "naver";
            Map<String, Object> responseMap = (Map<String, Object>) attributes.get("response");
            email = (String) responseMap.get("email");
            nickname = (String) responseMap.get("name");

            String naverImg = (String) responseMap.get("profile_image");
            profileImage = (naverImg != null && !naverImg.isEmpty()) ? naverImg : "/api/images/default-profile.png";
            providerId = (String) responseMap.get("id");

            // C. 구글 로그인인 경우 (구글 원본 Attributes 구조 대응)
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

//        String email = (String) attributes.get("email");
//        String nickname = (String) attributes.get("nickname");

        // 2. 식별자(userId) 파싱 및 DB 전수조사 방어선
        Object idAttribute = attributes.get("id");
        if (idAttribute == null && providerId != null) {
            idAttribute = providerId; // 구글/네이버 등에서 추출한 고유 식별값을 ID 대용으로 매핑
        }

        Long userId = null;
        if (idAttribute != null) {
            try {
                userId = Long.valueOf(String.valueOf(idAttribute));
            } catch (NumberFormatException e) {
                userId = null; // 소셜 고유 ID가 숫자가 아닌 문자열(구글 sub 등)인 경우 아래 DB 조회로 해결
            }
        }

        // DB 전수조사를 통해 유저 식별자(PK)를 명확하게 조회
        if (userId == null && email != null) {
            System.out.println("⚠️ [안내] 식별자 재추출 시작. 이메일(" + email + ") 기반으로 DB 조회를 수행합니다.");
            userId = userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElse(null);
        }

        // [최종 방어선] DB 조회까지 했는데도 없다면 자동 회원가입 처리 진행
        if (userId == null) {
            System.out.println("⚠️ [안내] 회원 데이터가 DB에 없는 신규 유저입니다. 자동 회원가입 처리를 개시합니다.");

            User newUser = User.builder()
                    .email(email)
                    .nickname(nickname != null ? nickname : "User_" + System.currentTimeMillis() % 10000)
                    .provider(provider)
                    .providerId(providerId)
                    .profileImage(profileImage)
                    .role(Role.USER)
                    .password("")
                    .createdAt(LocalDateTime.now()) // 혹시 몰라 생성일자도 추가
                    .build();

            User savedUser = userRepository.save(newUser);
            userId = savedUser.getId();
        }

        System.out.println("🎯 [디버깅] OAuth2 로그인 성공 - 이메일: " + email + ", 공급자: " + provider);
        System.out.println("🎯 [디버깅] 최종 확정된 DB User ID: " + userId);

        // 3. 확보된 확실한 userId를 가지고 가상/영속 객체 바인딩
        User targetUser = User.builder()
                .id(userId) // ⭐️ 이제 절대 null이 아닙니다.
                .email(email)
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .profileImage(profileImage)
                .role(Role.USER)
                .build();

        // 4. 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(targetUser, REFRESH_TOKEN_DURATION);
        saveRefreshToken(userId, refreshToken); // 🚀 안전하게 저장소로 Insert 쿼리가 수행됩니다.
        addRefreshTokenToCookie(request, response, refreshToken);

        // 5. 액세스 토큰 생성 -> 패스에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(targetUser, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        // 인증 관련 설정값, 쿠키 제거
        clearAuthenticationAttributes(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 생성된 리프레시 토큰을 전달받아 DB에 저장
    private void saveRefreshToken(Long userId, String newRefreshToken){
        // 기존 엔티티를 찾아서 업데이트하거나 없으면 생성 (생성자 인자 매핑 확인 필요)
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

    // 인증관련 설정값, 쿠키 제거 (부모 클래스의 시그니처 오류 방지를 위해 시큐리티 규격으로 보완)
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
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