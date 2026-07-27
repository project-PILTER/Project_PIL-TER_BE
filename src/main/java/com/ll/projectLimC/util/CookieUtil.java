package com.ll.projectLimC.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {

    // HTTPS 및 SameSite=None, HttpOnly, Domain 옵션이 완벽 적용된 쿠키 생성
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)            // JavaScript 접근 방지 (보안 강화)
                .secure(true)              // HTTPS 환경 필수
                .sameSite("None")          // 크로스 도메인 요청 시 쿠키 전송 허용
                // .domain(".pilter.co.kr") // 필요 시 메인 도메인 설정
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // 쿠키 삭제 로직도 ResponseCookie 규격으로 안전하게 변경
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                ResponseCookie deleteCookie = ResponseCookie.from(name, "")
                        .path("/")
                        .maxAge(0)
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("None")
                        .build();

                response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
            }
        }
    }

    // 객체를 직렬화해 쿠키의 값으로 변환
    public static String serialize(Object obj) {
        if (obj == null) return "";
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    // 쿠키를 역직렬화해 객체로 변환
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(cookie.getValue());

        try (java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(decodedBytes);
             java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bis)) {

            return cls.cast(ois.readObject());

        } catch (Exception e) {
            throw new IllegalArgumentException("쿠키 역직렬화에 실패했습니다.", e);
        }
    }
}