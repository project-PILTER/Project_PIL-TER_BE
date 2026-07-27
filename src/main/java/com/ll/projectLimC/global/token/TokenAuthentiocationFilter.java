package com.ll.projectLimC.global.token;

import com.ll.projectLimC.global.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthentiocationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";
    private final static String COOKIE_ACCESS_TOKEN = "accessToken";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // /api 하위 요청에 대해서만 로그 출력
        if (requestURI.startsWith("/api")) {
            String token = resolveToken(request);

            if (token == null) {
                log.warn("[JwtFilter] 요청에서 토큰을 찾을 수 없습니다. URI: {}", requestURI);
            } else {
                boolean isValid = tokenProvider.validToken(token);
                log.info("[JwtFilter] 토큰 추출 성공! URI: {}, 유효 여부: {}", requestURI, isValid);

                if (isValid) {
                    Authentication authentication = tokenProvider.getAuthenication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("[JwtFilter] SecurityContext에 인증 객체 저장 완료: {}", authentication.getName());
                } else {
                    log.warn("[JwtFilter] 토큰이 유효하지 않습니다(만료/위변조 등). URI: {}", requestURI);
                }
            }
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
