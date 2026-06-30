package com.ll.projectLimC.domain.User.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER", "일반 사용자"),
    EXPERT("ROLE_EXPERT", "의학 전문가"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}