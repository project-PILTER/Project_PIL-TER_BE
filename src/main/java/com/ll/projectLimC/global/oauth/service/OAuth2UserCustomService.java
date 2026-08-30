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

import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
// import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("=================================================");
        System.out.println("🔥 [확인] OAuth2UserCustomService가 작동 중입니다!");
        System.out.println("=================================================");

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2Attributes attributes = OAuth2Attributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        if (user.getId() == null) {
            user = userRepository.saveAndFlush(user);
            if (user.getId() == null) {
                throw new OAuth2AuthenticationException("소셜 로그인 유저의 고유 식별자(ID)를 확보하는 데 실패했습니다.");
            }
        }

        System.out.println("🌱 [CustomService] DB 저장 완료 후 유저 ID: " + user.getId());

        Map<String, Object> customAttributes = new HashMap<>(attributes.getAttributes());

        // DB PK 및 사용자 정보 주입 🟢
        customAttributes.put("id", user.getId());
        customAttributes.put("email", user.getEmail());
        customAttributes.put("nickname", user.getNickname());
        customAttributes.put("provider", user.getProvider() != null ? user.getProvider() : registrationId);
        customAttributes.put("provider_id", user.getProviderId() != null ? user.getProviderId() : attributes.getProviderId());
        customAttributes.put("profile_image", user.getProfileImage());

        String nameAttributeKey = attributes.getNameAttributeKey();
        if ("sub".equals(nameAttributeKey) || !customAttributes.containsKey(nameAttributeKey)) {
            customAttributes.put(nameAttributeKey, user.getProviderId());
        }

        if (user.getCreatedAt() != null) {
            customAttributes.put("created_at", user.getCreatedAt()
                    .atZoneSameInstant(ZoneId.of("Asia/Seoul"))
                    .toString());
        }

        // ROLE_USER 규격에 맞추어 getKey() 적용 🟢
        String roleKey = user.getRole().getKey(); // e.g. "ROLE_USER"

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(roleKey)),
                customAttributes,
                nameAttributeKey
        );
    }


    // 유저가 있으면 업데이트, 없으면 유저 생성
    private User saveOrUpdate(OAuth2Attributes attributes) {
        return userRepository.findByEmail(attributes.getEmail())
                .map(existingUser -> {
                    // 이미 소셜 로그인 정보가 있는 유저라면 닉네임은 유지하고 프로필만 변경
                    if (existingUser.getProvider() != null && !existingUser.getProvider().isEmpty()) {
                        existingUser.updateProfileImage(attributes.getProfileImage());
                    } else {
                        // 일반 회원인데 소셜 정보가 최초로 연동되는 경우라면 유니크한 닉네임을 조합해서 넣어줍니다.
                        String uniqueNickname = attributes.getNickname() + "_" + attributes.getProvider() + "_" + java.util.UUID.randomUUID().toString().substring(0, 4);
                        existingUser.update(uniqueNickname, attributes.getProfileImage());
                    }
                    return userRepository.saveAndFlush(existingUser);
                })
                .orElseGet(() -> {
                    System.out.println("====== [디버깅] 신규 소셜 유저를 DB에 저장합니다: " + attributes.getEmail() + " ======");
                    User newUser = attributes.toEntity();
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