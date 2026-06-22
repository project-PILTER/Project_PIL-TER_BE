package com.ll.projectLimC.domain.community.dto;

import com.ll.projectLimC.domain.comment.dto.CommentResponse;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class ArticleViewResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String author;
    private String imageUrl;
    private List<CommentResponse> comments;

    public ArticleViewResponse(CommunityArticle article){
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.author = article.getAuthor();
        this.imageUrl = article.getImageUrl();
        this.createdAt = article.getCreatedAt();
        this.comments = comments; // 매핑 완료

    }
}
