package com.ll.projectLimC.domain.auth.controller;

import com.ll.projectLimC.domain.user.dto.UserResponse;
import com.ll.projectLimC.domain.user.service.UserService;
import com.ll.projectLimC.domain.auth.dto.LoginRequest;
import com.ll.projectLimC.domain.auth.dto.LoginResponse;
import com.ll.projectLimC.domain.auth.service.AuthService;
import com.ll.projectLimC.global.Execption.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        UserResponse response = authService.getCurrentUser(principal);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 로그인",
        description = "회원가입한 사용자가 해당 내용으로 로그인을 합니다.")
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        LoginResponse loginResponse = authService.login(request, response);

        // 💡 Generic(제네릭) 타입 일치를 위해 정적 팩토리 메서드 구조에 맞춰 반환합니다.
        return ResponseEntity.ok(CommonResponse.success(loginResponse));
    }

    @Operation(summary = "사용자 로그아웃",
        description = "서비스를 사용하던 사용자가 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam Long id,
                                         HttpServletResponse response){
        // 본인인증이 된 상태에서 토큰 무효화를 위해 리프레시 토큰 레코드 삭제 처리
        authService.logout(id, response);
        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }
}
