package com.ll.projectLimC.domain.healthJournal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConditionStatus {
    EXCELLENT("아주 좋음"),
    GOOD("좋음"),
    NORMAL("보통"),
    BAD("나쁨"),
    AWFUL("아주 나쁨");

    private final String description;
}
