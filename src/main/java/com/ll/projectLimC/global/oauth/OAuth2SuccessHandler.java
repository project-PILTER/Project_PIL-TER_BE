package com.ll.projectLimC.global.oauth;

import com.ll.projectLimC.domain.user.entity.Role;
import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.user.repository.UserRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import com.ll.projectLimC.global.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.ll.projectLimC.global.refreshToken.entity.RefreshToken;
import com.ll.projectLimC.global.refreshToken.repository.RefreshTokenRepository;
import com.ll.projectLimC.domain.user.service.UserService;
import com.ll.projectLimC.global.jwt.JwtTokenProvider;
import com.ll.projectLimC.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
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
    private final UserService userService;
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
        Object idAttribute = attributes.get("id");

        if (idAttribute == null && attributes.get("sub") != null) {
            idAttribute = attributes.get("sub"); // 구글 공급자일 경우 sub를 식별값으로 대체 적용
        }

        Long userId = null;

        // 세션 컨텍스트에 따라 숫자가 Integer나 String으로 역직렬화될 수 있으므로 안전하게 변환
        if (idAttribute != null) {
            try {
                userId = Long.valueOf(String.valueOf(idAttribute));
            } catch (NumberFormatException e) {
                // 만약 소셜 고유 ID가 숫자가 아닌 문자열 형태로 되어 있다면
                // 하단 이메일 기반 DB 조회 방어선으로 흘러가도록 유도
                userId = null;
            }
        }

        String email = (String) attributes.get("email");
        String nickname = (String) attributes.get("nickname");

        if (nickname == null) {
            nickname = (String) attributes.get("name");
        }

        String provider = (String) attributes.get("provider");
        String providerId = (String) attributes.get("provider_id");
        String profileImage = (String) attributes.get("profile_image"); // 네이버는 이거 씀.

        // 🚨 [질문자님 가설 반영] 만약 CustomService 단계에서 영속화 타이밍 이슈로 id가 null로 넘어왔다면?
        if (userId == null) {
            System.out.println("⚠️ [경고] 핸들러 맵에 id가 없습니다. 이메일(" + email + ") 기반으로 DB 전수조사를 시작합니다.");

            if (email != null) {
                // 주입받은 userRepository를 사용하여 실제 DB에 저장된 유저의 고유 PK(id)를 직접 강제로 땡겨옵니다.
                userId = userRepository.findByEmail(email)
                        .map(User::getId)
                        .orElse(null);
            }
        }

        // 🔥 [최종 방어선] DB 조회까지 했는데도 없다면 이것은 심각한 오류이므로 흐름을 중단시킵니다.
        if (userId == null) {
            System.out.println("⚠️ [안내] 회원 데이터가 DB에 없는 신규 유저입니다. 자동 회원가입 처리를 개시합니다.");

            // DB에 새롭게 영속화하여 유저 식별자(PK)를 즉시 채굴해냅니다.
            User newUser = User.builder()
                    .email(email)
                    .nickname(nickname != null ? nickname : "User_" + System.currentTimeMillis() % 10000)
                    .provider(provider != null ? provider : "google")
                    .providerId(providerId != null ? providerId : (String) attributes.get("sub"))
                    .profileImage(profileImage)
                    .role(Role.USER)
                    .build();

            User savedUser = userRepository.save(newUser); // JpaRepository를 통한 실시간 DB 영속화
            userId = savedUser.getId(); // ⭐️ 드디어 확실하고 영속적인 DB의 auto_increment PK 확보!
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