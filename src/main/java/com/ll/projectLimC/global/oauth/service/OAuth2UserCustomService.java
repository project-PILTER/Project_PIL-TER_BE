package com.ll.projectLimC.global.oauth.service;

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
import java.util.UUID;
// import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)throws OAuth2AuthenticationException{
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

        // 5. 시큐리티 세션/필터 시스템에 등록될 인증 유저 반환 (Principal 권한 바인딩)
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }


    // 유저가 있으면 업데이트, 없으면 유저 생성
    private User saveOrUpdate(OAuth2Attributes attributes) {
        return userRepository.findByProviderAndProviderId(attributes.getProvider(), attributes.getProviderId())
                .map(entity -> {
                    // 기존 유저 로그인 프로필 갱신
                    return entity.updateSocialProfile(entity.getNickname());
                })
                .orElseGet(() -> {
                    // 신규 유저 회원가입
                    String initialNickname = attributes.getNickname();

                    if (initialNickname == null || initialNickname.trim().isEmpty() || userRepository.existsByNickname(initialNickname)) {
                        String suffix = UUID.randomUUID().toString().substring(0, 4);
                        initialNickname = (initialNickname != null ? initialNickname : "User") + "_" + suffix;
                    }

                    User newUser = attributes.toEntity();
                    newUser.changeNickname(initialNickname);

                    // save 대신 saveAndFlush를 사용하여 Security Filter가 끝나기 전 DB 적재를 강제
                    return userRepository.saveAndFlush(newUser);
                });
    }
}
//        // 디버깅 로그 추가: 실제로 값들이 정상적으로 추출되어 넘어오는지 검사합니다.
//        System.out.println("========== [OAuth2 로드 데이터 검증] ==========");
//        System.out.println("Provider (공급자): " + attributes.getProvider());
//        System.out.println("ProviderId (고유식별자): " + attributes.getProviderId());
//        System.out.println("Nickname (소셜 닉네임): " + attributes.getNickname());
//        System.out.println("=============================================");
//
//        return userRepository.findByProviderAndProviderId(attributes.getProvider(), attributes.getProviderId())
//                .map(entity -> {
//                    System.out.println("-> [기존 유저 발견] 로그인 프로세스 진행: " + entity.getEmail());
//                    return entity.updateSocialProfile(entity.getNickname());
//                })
//                .orElseGet(() -> {
//                    System.out.println("-> [신규 유저] 회원가입 프로세스 진입");
//                    String initialNickname = attributes.getNickname();
//
//                    if (initialNickname == null || initialNickname.trim().isEmpty() || userRepository.existsByNickname(initialNickname)) {
//                        String suffix = UUID.randomUUID().toString().substring(0, 4);
//                        initialNickname = (initialNickname != null ? initialNickname : "User") + "_" + suffix;
//                    }
//
//                    User newUser = attributes.toEntity();
//                    newUser.changeNickname(initialNickname);
//
//                    System.out.println("-> [신규 유저 저장 직전] 최종 닉네임: " + initialNickname);
//
//                    // 여기서 정상 저장되는지 확인
//                    User savedUser = userRepository.save(newUser);
//                    System.out.println("-> [DB 저장 성공] 생성된 유저 고유 ID: " + savedUser.getId());
//
//                    return savedUser;
//                });
//    }