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
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
    @SuppressWarnings("unchecked")
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        if (oAuth2User == null) return;

        // 1. 기본 어트리뷰트 맵 추출 및 로그 출력
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
                    // profile_image_url 과 thumbnail_image_url 2중 방어선 구축
                    String kakaoImg = (String) profile.get("profile_image_url");
                    if (kakaoImg == null || kakaoImg.isEmpty()) {
                        kakaoImg = (String) profile.get("thumbnail_image_url");
                    }
                    profileImage = (kakaoImg != null && !kakaoImg.isEmpty()) ? kakaoImg : "/api/images/default-profile.png";
                }
            }
            if (profileImage == null) profileImage = "/api/images/default-profile.png";
            providerId = String.valueOf(attributes.get("id")); // 최상단 고유 ID 추출

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

        // 2. ⭐️ [핵심 수정] 무조건 이메일을 기반으로 실제 우리 DB의 오토 인크리먼트 PK(id)를 선행 조회합니다.
        Long userId = null;
        if (email != null) {
            System.out.println("🔍 [안내] 가입 여부 확인을 위해 이메일(" + email + ") 기반 DB 전수조사를 개시합니다.");
            userId = userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElse(null);
        }

        // [최종 방어선] DB 조회 결과가 null 이라면 진짜 최초 가입자이므로 자동 회원가입 진행
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
                    .createdAt(OffsetDateTime.now(ZoneId.of("Asia/Seoul")))
                    .build();

            User savedUser = userRepository.save(newUser);
            userId = savedUser.getId();
        } else {
            // ⭐️ 기존 회원인 경우, 소셜 미디어에서 최신 이미지나 닉네임이 바뀌었을 수 있으므로 더티 체킹용 업데이트 처리 (선택 사항)
            User existingUser = userRepository.findById(userId).orElse(null);
            if (existingUser != null && profileImage != null && !profileImage.equals(existingUser.getProfileImage())) {
                // 기존 유저의 프로필 사진이 카카오 실제 이미지로 동기화될 수 있도록 필요한 경우 업데이트 로직 배치 가능
                System.out.println("🔄 [안내] 기존 유저의 최신 프로필 이미지 동기화를 진행합니다.");
            }
        }

        System.out.println("🎯 [디버깅] OAuth2 로그인 성공 - 이메일: " + email + ", 공급자: " + provider);
        System.out.println("🎯 [디버깅] 최종 확정된 DB User ID: " + userId);

        // 3. 확보된 확실한 userId를 가지고 가상/영속 객체 바인딩 (이제 토큰에 무조건 DB PK 숫자가 들어감)
        User targetUser = User.builder()
                .id(userId)
                .email(email)
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .profileImage(profileImage)
                .role(Role.USER)
                .build();

        // 4. 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(targetUser, REFRESH_TOKEN_DURATION);
        saveRefreshToken(userId, refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        // 5. 액세스 토큰 생성 -> 패스에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(targetUser, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

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