package com.ll.projectLimC.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Access Token 신규 발급 요청")
public class CreateAccessTokenRequest {
    @Schema(description = "기존에 저장된 유효한 Refresh Token값",
            example = "asldfkjhqwhroqUIHJOADH...")
    private String refreshToken;
}
