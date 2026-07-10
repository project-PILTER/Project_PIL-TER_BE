package com.ll.projectLimC.global.oauth.service;

import com.ll.projectLimC.domain.user.entity.Role;
import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.user.repository.UserRepository;
import com.ll.projectLimC.global.oauth.OAuth2Attributes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
// import java.util.Map;

@RequiredArgsConstructor
@Service
// @Transactional
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 1. 어떤 소셜 매체인지 식별 (google, naver, kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 2. 소셜 서비스별 고유 식별 주 키 값 파싱명 추출
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 3. 공급자별로 파편화된 정보를 하나의 공통 규격 객체로 추출
        OAuth2Attributes attributes = OAuth2Attributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 4. 가입/갱신 후 엔티티 영속화
        User user = saveOrUpdate(attributes);
        userRepository.saveAndFlush(user); // DB에 즉시 반영하여 ID(PK)를 객체에 확보

        System.out.println("🌱 [CustomService] DB 저장 완료 후 유저 ID: " + user.getId());

        Map<String, Object> customAttributes = new java.util.HashMap<>(attributes.getAttributes());
        customAttributes.put("id", user.getId());         // 고유 ID 바인딩
        customAttributes.put("email", user.getEmail());

        // 5. 시큐리티 세션/필터 시스템에 등록될 인증 유저 반환 (Principal 권한 바인딩)
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString())),
                customAttributes, // 새로 커스텀한 맵을 주입
                attributes.getNameAttributeKey()
        );
    }


    // 유저가 있으면 업데이트, 없으면 유저 생성
    private User saveOrUpdate(OAuth2Attributes attributes) {
        return userRepository.findByEmail(attributes.getEmail())
                .map(existingUser -> {
                    existingUser.update(attributes.getNickname(), attributes.getProfileImage());
                    return existingUser;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(attributes.getEmail())
                            .nickname(attributes.getNickname())
                            .profileImage(attributes.getProfileImage()) // 신규 가입 시에도 함께 빌드
                            .role(Role.USER)
                            .build();

                    System.out.println("====== [디버깅] 신규 소셜 유저를 DB에 저장합니다: " + newUser.getEmail() + " ======");
                    return userRepository.saveAndFlush(newUser);
                });
    }
}
//        try {
//            System.out.println("====== [디버깅] 소셜 로그인 데이터 확인 ======");
//            System.out.println("Provider: " + attributes.getProvider());
//            System.out.println("ProviderId: " + attributes.getProviderId());
//            System.out.println("Email: " + attributes.getEmail());
//            System.out.println("Nickname: " + attributes.getNickname());
//            System.out.println("==========================================");
//
//            return userRepository.findByProviderAndProviderId(attributes.getProvider(), attributes.getProviderId())
//                    .map(entity -> {
//                        System.out.println("-> [디버깅] 기존 유저 발견: " + entity.getEmail());
//                        return entity.updateSocialProfile(entity.getNickname());
//                    })
//                    .orElseGet(() -> {
//                        System.out.println("-> [디버깅] 신규 유저 생성 프로세스 진입");
//                        String initialNickname = attributes.getNickname();
//
//                        if (initialNickname == null || initialNickname.trim().isEmpty() || userRepository.existsByNickname(initialNickname)) {
//                            String suffix = UUID.randomUUID().toString().substring(0, 4);
//                            initialNickname = (initialNickname != null ? initialNickname : "User") + "_" + suffix;
//                        }
//
//                        User newUser = attributes.toEntity();
//                        newUser.changeNickname(initialNickname);
//
//                        System.out.println("-> [디버깅] DB 저장 시도 직전 유저 엔티티 상태 체크");
//                        // 만약 엔티티에 password나 다른 필수값이 null이면 여기서 디비가 거부합니다.
//
//                        User savedUser = userRepository.saveAndFlush(newUser);
//                        System.out.println("-> [디버깅] DB 저장 성공! 생성된 ID: " + savedUser.getId());
//                        return savedUser;
//                    });
//
//        } catch (Exception e) {
//            System.err.println("❌❌❌ [디버깅] 소셜 유저 DB 저장 중 진짜 에러 터짐!! ❌❌❌");
//            e.printStackTrace(); // 스프링이 숨기던 원본 JPA/데이터베이스 에러 스택트레이스를 강제로 출력합니다.
//            throw e;
//        }