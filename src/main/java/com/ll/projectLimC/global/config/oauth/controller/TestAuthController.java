package com.ll.projectLimC.global.config.oauth.controller;

import com.ll.projectLimC.domain.User.entity.User;
import com.ll.projectLimC.domain.User.repository.UserRepository;
import com.ll.projectLimC.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@Tag(name = "[개발용] 인증 테스트 API",
        description = "프론트 연동 전 스웨거/포스트맨 테스트를 위한 만능 토큰 발급기")
@RestController
@RequiredArgsConstructor
public class TestAuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    @Operation(summary = "테스트용 Bearer 토큰 즉시 발급",
            description = "입력한 이메일로 유저를 조회하거나 없을 경우 즉시 가짜 유저를 생성하여 1일간 유효한 만능 JWT 토큰을 발급합니다.")
    @GetMapping("/api/test/token")
    public ResponseEntity<Map<String, String>> getTestToken(
            @RequestParam(defaultValue = "testuser@gmail.com") String email
    ) {
        // 1. DB에 해당 이메일의 유저가 있는지 확인하고, 없으면 가짜 데이터로 즉시 저장
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .nickname("하니보이")
                        .profileImage("https://example.com/default.png")
                        .isMedicalExpert(false)
                        .build()));

        // 2. JwtTokenProvider를 활용해 24시간짜리 든든한 액세스 토큰 생성
        String accessToken = tokenProvider.generateToken(user, Duration.ofDays(1));

        // 3. 스웨거에서 바로 복사하기 좋게 JSON 형식으로 리턴
        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "nickname", user.getNickname(),
                "accessToken", accessToken
        ));
    }
}
