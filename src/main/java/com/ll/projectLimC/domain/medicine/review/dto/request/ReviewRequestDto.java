package com.ll.projectLimC.domain.medicine.review.dto.request;

import com.ll.projectLimC.domain.medicine.review.EffectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDto {
    private int rating;
    private EffectType effectType;
    private String symptomTag;
    private String content;
}