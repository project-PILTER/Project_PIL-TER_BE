package com.ll.projectLimC.domain.community.dto.ArticleDrafts.Request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
public class ArticleDraftsSaveRequest {
    private String title;
    private String content;
    private String category;
    private OffsetDateTime updatedAt;

    public ArticleDraftsSaveRequest(String title, String content, String category, OffsetDateTime updatedAt){
        this.title = title;
        this.content = content;
        this.category = category;
        this.updatedAt = updatedAt;
    }
}
