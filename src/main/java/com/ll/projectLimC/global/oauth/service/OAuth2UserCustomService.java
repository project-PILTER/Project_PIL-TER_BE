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
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("=================================================");
        System.out.println("🔥 [확인] OAuth2UserCustomService가 작동 중입니다!");
        System.out.println("=================================================");

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

        // 만약 영속화 직후인데도 user.getId()가 null이라면, DB 연동이나 트랜잭션 시점이 꼬인 것.
        if (user.getId() == null) {
            System.out.println("⚠️ [경고] user.getId()가 null입니다. 강제 saveAndFlush를 실행합니다.");
            user = userRepository.saveAndFlush(user);

            // 강제 저장 후에도 null이라면 데이터베이스 저장 메커니즘 자체에 문제가 있는 것이므로 예외 발생
            if (user.getId() == null) {
                throw new OAuth2AuthenticationException("소셜 로그인 유저의 고유 식별자(ID)를 확보하는 데 실패했습니다.");
            }
        }

        System.out.println("🌱 [CustomService] DB 저장 완료 후 유저 ID: " + user.getId());

        // 가공할 맵 생성
        Map<String, Object> customAttributes = new HashMap<>(attributes.getAttributes());

        customAttributes.put("id", user.getId());
        customAttributes.put("email", user.getEmail());
        customAttributes.put("nickname", user.getNickname());
        customAttributes.put("provider", user.getProvider()!= null ? user.getProvider() : registrationId); // 공급자 정보 보장);
        customAttributes.put("provider_id", user.getProviderId() != null ? user.getProviderId() : attributes.getProviderId());
        customAttributes.put("profile_image", user.getProfileImage());

        // 구글 등 'sub'을 식별자로 쓰는 소셜 매체를 위해 customAttributes 맵에 해당 키를 강제로 매핑해 줌.
        String nameAttributeKey = attributes.getNameAttributeKey(); // google은 "sub", kakao는 "id" 등
        if ("sub".equals(nameAttributeKey) || !customAttributes.containsKey(nameAttributeKey)) {
            // 구글의 경우 sub 식별 키값에 식별자를 완벽하게 주입하여 핸들러로 넘겨줌.
            customAttributes.put(nameAttributeKey, user.getProviderId());
        }

        if (user.getCreatedAt() != null) {
            customAttributes.put("created_at", user.getCreatedAt()
                    .atZoneSameInstant(ZoneId.of("Asia/Seoul"))
                    .toString());
        }

        // 5. 시큐리티 세션/필터 시스템에 등록될 인증 유저 반환 (Principal 권한 바인딩)
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString())),
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