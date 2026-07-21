package com.ll.projectLimC.global.token;

import com.ll.projectLimC.global.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthentiocationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";
    private final static String COOKIE_ACCESS_TOKEN = "refreshToken";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 헤더 또는 쿠키에서 토큰 추출
        String token = resolveToken(request);

        // 가져온 토큰이 유효한지 확인하고, 유효할 때만 인증 정보 설정
        if (token != null && tokenProvider.validToken(token)) {
            Authentication authentication = tokenProvider.getAuthenication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        // 1. 요청 헤더(Authorization: Bearer <token>)에서 추출
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }

        // 2. 헤더에 없으면 쿠키(Cookie)에서 추출 ⭐ (이 부분이 핵심!)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (COOKIE_ACCESS_TOKEN.equals(cookie.getName())) { // 쿠키 키 이름 체크
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
