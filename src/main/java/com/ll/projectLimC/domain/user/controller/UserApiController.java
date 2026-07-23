package com.ll.projectLimC.domain.user.controller;

import com.ll.projectLimC.domain.user.dto.AddUserRequest;
import com.ll.projectLimC.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 API", description = "사용자 회원가입 및 인증 관련 컨트롤러")
@RestController // @Controller를 @RestController로 변경하여 JSON 응답 기반으로 전환
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @Operation(summary = "일반 회원가입",
            description = "프론트엔드에서 전달된 사용자 정보를 바탕으로 데이터베이스에 회원을 등록합니다.")
    @PostMapping("/user/signup")
    public ResponseEntity<Void> signup(@RequestBody AddUserRequest request) { // @RequestBody 추가 및 ResponseEntity 반환

        // 1. 회원가입 비즈니스 로직 수행
        userService.createUser(request);

        // 2. 화면 리다이렉트 대신, 성공했다는 의미의 201 Created 상태 코드만 깔끔하게 반환
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
