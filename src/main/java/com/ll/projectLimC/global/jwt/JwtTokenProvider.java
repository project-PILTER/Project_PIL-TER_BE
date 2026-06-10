package com.ll.projectLimC.global.jwt;

import com.ll.projectLimC.domain.entity.User.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt){
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    private String makeToken(Date expiry, User user){
        Date now = new Date();

        return Jwts.builder()
                .header()
                .type("JWT") // 최신 체인형 헤더 세팅
                .and()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiry)
                .subject(user.getEmail())
                .claim("id", user.getId())
                // SignatureAlgorithm.HS256 대신 내장 암호화 키 알고리즘 매핑 적용
                .signWith(jwtProperties.getSecretKeyObject(), Jwts.SIG.HS256)
                .compact();
    }

    // JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(jwtProperties.getSecretKeyObject()) // setSigningKey 대용 최신 인터페이스
                    .build()
                    .parseSignedClaims(token); // parseClaimsJws 대신 객체 형식 파싱

            return true;
        } catch (Exception e){
            return false;
        }
    }

    // 토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthenication(String token){
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_USER"));

        // org.springframework.security.core.userdetails.User 복사해서 명시적으로 지정
        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities),
                token,
                authorities
        );
    }

    // 토큰 기반으로 유저 ID를 가져오는 메서드
    public Long getUserID(String token){
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    public Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(jwtProperties.getSecretKeyObject())
                .build()
                .parseSignedClaims(token)
                .getPayload(); // getBody() 대신 최신 스펙 명칭인 getPayload() 사용
    }
}