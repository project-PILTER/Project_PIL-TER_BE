package com.ll.projectLimC.global.oauth;

import com.ll.projectLimC.domain.user.entity.Role;
import com.ll.projectLimC.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Getter
@Builder
public class OAuth2Attributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String nickname;
    private String email;
    private String provider;
    private String providerId;
    private String profileImage;

    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver(userNameAttributeName, attributes);
        } else if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        String googleName = (String) attributes.get("name");
        if (googleName == null || googleName.trim().isEmpty()) {
            googleName = (String) attributes.get("given_name"); // alternative
        }

        String pictureUrl = (String) attributes.get("picture");
        String defaultImage = "/images/default-profile.png";

        return OAuth2Attributes.builder()
                .nickname(googleName)
                .email((String) attributes.get("email"))
                .profileImage(pictureUrl != null && !pictureUrl.isEmpty() ? pictureUrl : defaultImage)
                .provider("google")
                .providerId((String) attributes.get("sub"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuth2Attributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuth2Attributes.builder()
                .nickname((String) response.get("name"))
                .email((String) response.get("email"))
                .profileImage((String) response.get("profile_image"))
                .provider("naver")
                .providerId((String) response.get("id"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // 스크린샷에 찍힌 카카오 매핑 메서드 (구조 정돈)
    private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String profileImage = (profile != null) ? (String) profile.get("profile_image_url") : null;
        String defaultImage = "/images/default-profile.png";

        return OAuth2Attributes.builder()
                .nickname(profile != null ? (String) profile.get("nickname") : null)
                .email((String) kakaoAccount.get("email"))
                .profileImage(profileImage != null && !profileImage.isEmpty() ? profileImage : defaultImage)
                .provider("kakao")
                .providerId(String.valueOf(attributes.get("id")))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // 괄호 쌍을 완벽히 맞추고 빌더 구조를 정돈한 변환 메서드
    public User toEntity() {
        // 회원가입 시점에 중복 방지 접미사를 결합 (예: 박제욱_kakao_a2f1)
        String uniqueSuffix = "_" + this.provider + "_" + java.util.UUID.randomUUID().toString().substring(0, 4);

        return User.builder()
                .email(this.email)
                .nickname(this.nickname + uniqueSuffix) // 유니크 닉네임 자동 저장
                .role(Role.USER)
                .profileImage(this.profileImage)
                .provider(this.provider)
                .providerId(this.providerId)
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .password("")
                .build();
    }
}