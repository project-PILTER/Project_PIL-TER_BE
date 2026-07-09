package com.ll.projectLimC.global.token.controller;

import com.ll.projectLimC.domain.auth.dto.LoginResponse;
import com.ll.projectLimC.global.Execption.CommonResponse;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import com.ll.projectLimC.global.token.dto.CreateAccessTokenRequest;
import com.ll.projectLimC.global.token.dto.CreateAccessTokenResponse;
import com.ll.projectLimC.global.token.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {
    private final TokenService tokenService;

    @Operation(summary = "Access Token 신규 발급",
            description = "만료되지 않은 Refresh Token을 사용하여 새로운 Access Token을 획득합니다.")
    @PostMapping("/token")
    public ResponseEntity<CommonResponse<LoginResponse>> createNewAccessToken(
            // 브라우저가 헤더에 실어 보낸 "refreshToken" 쿠키를 스프링이 자동으로 매핑
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        // 쿠키가 아예 없거나 비어있다면 로그인 안 된 상태로 예외 처리
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new GlobalCustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        // 쿠키에서 꺼낸 refreshToken으로 새로운 Access Token 발급 진행
        String newAccessToken = tokenService.createNewAccessToken(refreshToken);

        return ResponseEntity.ok(CommonResponse.success(new LoginResponse(newAccessToken)));
    }
}
