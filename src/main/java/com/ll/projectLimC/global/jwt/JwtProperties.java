package com.ll.projectLimC.global.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Setter
@Getter
@Component
@ConfigurationProperties("jwt")
public class JwtProperties {
    private String issuer;
    private String secretKey;

    private Long accessTokenExpiration; // jwt.access-token-expiration 과 자동 매핑
    private Long refreshTokenExpiration; // jwt.refresh-token-expiration 과 자동 매핑

    // String으로 된 비밀키를 최신 jjwt 가 규격화한 SecretKey 객체로 변환
    public SecretKey getSecretKeyObject() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
