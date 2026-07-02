package com.ll.projectLimC.domain.auth.login.controller;

import com.ll.projectLimC.domain.User.dto.UserResponse;
import com.ll.projectLimC.domain.User.entity.User;
import com.ll.projectLimC.domain.User.service.UserService;
import com.ll.projectLimC.domain.auth.login.dto.LoginRequest;
import com.ll.projectLimC.domain.auth.login.dto.LoginResponse;
import com.ll.projectLimC.domain.auth.login.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    // ✨ 프론트엔드 요청 1번: 로그인 세션/쿠키 유지용 유저 정보 조회 API
    @Operation(summary = "현재 로그인한 유저 정보 조회",
            description = "프론트엔드 AuthProvider에서 로그인 상태 유지를 위해 쿠키/세션을 기반으로 유저 정보를 조회합니다.")
    @GetMapping("/user") // 프론트가 요청한 엔드포인트 매핑
    public ResponseEntity<UserResponse> getCurrentUser(Principal principal) {


        // ➔ 로그인이 안 되어 있으면 프론트엔드가 요구한 401 Unauthorized 에러를 리턴합니다.
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 유효하지 않거나 필요합니다.");
        }

        User loginUser = userService.findByEmail(principal.getName());

        // 로그인된 경우: 서비스 레이어 등에서 principal.getName()(이메일 또는 ID)으로 유저를 조회한 뒤 반환합니다.
        //
        UserResponse userResponse = UserResponse.builder()
                .id(loginUser.getId())
                .email(loginUser.getEmail())
                .name(loginUser.getNickname())
                .role(loginUser.getRole().toString())
                .build();

        return ResponseEntity.ok().body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
