package com.ll.projectLimC.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LikeCommentResponse {
    private boolean isLiked;
    private Long totalLikes;
}
