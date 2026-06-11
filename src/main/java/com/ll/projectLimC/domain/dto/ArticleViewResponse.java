package com.ll.projectLimC.domain.dto;

import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ArticleViewResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public ArticleViewResponse(CommunityArticle article){
        this.id = article.getId();
        this.title = article.getTitle();;
        this.content = article.getContent();;
        this.createdAt = article.getCreatedAt();
    }
}
