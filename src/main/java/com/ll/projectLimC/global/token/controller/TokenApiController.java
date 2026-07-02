package com.ll.projectLimC.global.token.controller;

import com.ll.projectLimC.global.token.dto.CreateAccessTokenRequest;
import com.ll.projectLimC.global.token.dto.CreateAccessTokenResponse;
import com.ll.projectLimC.global.token.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(
            @RequestBody CreateAccessTokenRequest request
            ){
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}
