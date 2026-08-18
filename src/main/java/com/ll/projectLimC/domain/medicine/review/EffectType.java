package com.ll.projectLimC.domain.medicine.review;

public enum EffectType {
    EFFECTIVE("효과 있음"),
    INEFFECTIVE("효과 없음"),
    SIDE_EFFECT("부작용 겪음");

    private final String description;
    EffectType(String description) { this.description = description; }
}