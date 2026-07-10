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

import java.util.Collections;
import java.util.UUID;
// import java.util.Map;

@RequiredArgsConstructor
@Service
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
                    // [기존 유저 로그인]: 이미 가입된 유저는 기존 닉네임을 그대로 유지하거나 동기화
                    return entity.updateSocialProfile(entity.getNickname());
                })
                .orElseGet(() -> {
                    // [신규 유저 회원가입]: 처음 가입하는 유저인 경우 닉네임 중복 체크를 합니다.
                    String initialNickname = attributes.getNickname();

                    // 만약 소셜에서 가져온 닉네임이 빈 값이거나, DB에 이미 존재하는 닉네임이라면 뒤에 랜덤 값을 붙입니다.
                    if (initialNickname == null || initialNickname.trim().isEmpty() || userRepository.existsByNickname(initialNickname)) {
                        // 이름 뒤에 랜덤 4자리 글자 추가 (예: 박땡땡_a1b2)
                        String suffix = UUID.randomUUID().toString().substring(0, 4);
                        initialNickname = (initialNickname != null ? initialNickname : "User") + "_" + suffix;
                    }

                    // 변경된 안전한 닉네임으로 엔티티를 생성
                    User newUser = attributes.toEntity();

                    // 엔티티 내부에 닉네임을 변경할 수 있는 Setter나 메서드가 있다면 바인딩해준다.
                    // 예: newUser.setNickname(initialNickname); 또는 newUser.changeNickname(initialNickname);
                    newUser.changeNickname(initialNickname);

                    return userRepository.save(newUser);
                });
    }
}
