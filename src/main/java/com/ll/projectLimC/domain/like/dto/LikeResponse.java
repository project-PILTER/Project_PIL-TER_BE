package com.ll.projectLimC.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LikeResponse {
    private boolean isLiked;
    private long totalLikes;

    public LikeResponse(boolean isLiked, long totalLikes) {
        this.isLiked = isLiked;
        this.totalLikes = totalLikes;
    }
}
