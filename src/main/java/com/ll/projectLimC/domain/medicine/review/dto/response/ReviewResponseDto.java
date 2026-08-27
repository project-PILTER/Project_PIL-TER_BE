package com.ll.projectLimC.domain.medicine.review.dto.response;

import com.ll.projectLimC.domain.medicine.review.entity.Review;
import com.ll.projectLimC.domain.user.entity.User;
import lombok.Getter;

@Getter
public class ReviewResponseDto {
    private Long id;
    private String nickname;
    private String profileImage;
    private int rating;
    private String effectType;
    private String symptomTag;
    private String content;
    private Long likeCount;
    private String createdAt;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        User user = review.getUser();
        if(user != null){
            this.nickname = user.getNickname() != null ? user.getNickname() : "알 수 없음";
            this.profileImage = user.getProfileImage() != null ? user.getProfileImage() : "";
        }else{
            this.nickname = "알 수 없음";
            this.profileImage = null;
        }

        this.rating = review.getRating();
        this.effectType = review.getEffectType() != null ? review.getEffectType().name() : null;
        this.symptomTag = review.getSymptomTag();
        this.content = review.getContent();
        this.likeCount = review.getLikeCount();
        this.createdAt = review.getCreatedAt() != null ? review.getCreatedAt().toLocalDate().toString() : "";
    }
}
