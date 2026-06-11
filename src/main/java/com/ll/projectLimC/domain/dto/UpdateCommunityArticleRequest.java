package com.ll.projectLimC.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateCommunityArticleRequest {
    private String title;
    private String content;
    private String imageUrl;
}
