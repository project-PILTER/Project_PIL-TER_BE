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
    public ResponseEntity<CommonResponse<?>> createNewAccessToken( //  <LoginResponse> 대신 <?> 지정
                                                                   @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        // 1. 쿠키가 없거나 비어있는 경우
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(CommonResponse.fail(ErrorCode.AUTHENTICATION_FAILED)); // 👈 깔끔하게 통과!
        }

        // 2. 새로운 Access Token 발급
        String newAccessToken = tokenService.createNewAccessToken(refreshToken);

        return ResponseEntity.ok(CommonResponse.success(new LoginResponse(newAccessToken)));
    }
}
