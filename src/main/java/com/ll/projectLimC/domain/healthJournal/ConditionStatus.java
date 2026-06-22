package com.ll.projectLimC.domain.healthJournal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConditionStatus {
    GOOD("좋음"),
    NORMAL("보통"),
    BAD("나쁨");

    private final String description;
}
