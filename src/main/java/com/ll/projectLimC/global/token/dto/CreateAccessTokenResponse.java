package com.ll.projectLimC.global.token.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "Access Token 신규 발급 응답 결과")
public class CreateAccessTokenResponse {
    @Schema(description = "새로 발급된 Access Token (모든 API 요청 헤더 Authorization에 사용)",
            example = "asldfkjhqwhroqUIHJOADH...")
    private String accessToken;
}
