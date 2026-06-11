package com.ll.projectLimC.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {
    // 요청값(이름, 값, 만료기간)을 바탕으로 쿠키 추가
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    // 쿠키의 이름을 입력받아 쿠키 삭제
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name){
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return;
        }

        for (Cookie cookie : cookies){
            if (name.equals(cookie.getName())){
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    // 객체를 직렬화해 쿠키의 값으로 변환
    public static String serialize(Object obj){
        if (obj == null) return "";
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    // ⭐ [수정됨] 쿠키를 역직렬화해 객체로 변환 (Deprecated 해결)
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(Cookie cookie, Class<T> cls){
        byte[] decodedBytes = Base64.getUrlDecoder().decode(cookie.getValue());

        // 최신 스프링 버전에 맞게 자바 기본 역직렬화를 수행하는 안전한 표준 방식 사용
        try (java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(decodedBytes);
             java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bis)) {

            return cls.cast(ois.readObject());

        } catch (Exception e) {
            throw new IllegalArgumentException("쿠키 역직렬화에 실패했습니다.", e);
        }
    }
}