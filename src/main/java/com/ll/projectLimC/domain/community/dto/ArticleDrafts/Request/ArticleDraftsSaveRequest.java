package com.ll.projectLimC.domain.community.dto.ArticleDrafts.Request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArticleDraftsSaveRequest {
    private String title;
    private String content;
    private String category;

    public ArticleDraftsSaveRequest(String title, String content, String category){
        this.title = title;
        this.content = content;
        this.category = category;
    }
}
