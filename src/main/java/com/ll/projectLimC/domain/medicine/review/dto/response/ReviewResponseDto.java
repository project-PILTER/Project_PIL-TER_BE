package com.ll.projectLimC.domain.medicine.review.dto.response;

import com.ll.projectLimC.domain.medicine.review.entity.Review;
import lombok.Getter;

@Getter
public class ReviewResponseDto {
    private Long id;
    private String writerNickname;
    private int rating;
    private String effectType;
    private String symptomTag;
    private String content;
    private Long likeCount;
    private String createdAt;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.writerNickname = review.getUser() != null ? review.getUser().getNickname() : "알 수 없음";
        this.rating = review.getRating();
        this.effectType = review.getEffectType() != null ? review.getEffectType().name() : null;
        this.symptomTag = review.getSymptomTag();
        this.content = review.getContent();
        this.likeCount = review.getLikeCount();
        this.createdAt = review.getCreatedAt() != null ? review.getCreatedAt().toLocalDate().toString() : "";
    }
}
