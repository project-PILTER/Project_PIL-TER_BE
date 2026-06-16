package com.ll.projectLimC.domain.dto;

import com.ll.projectLimC.domain.entity.Comment.Comment;
import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCommentRequest {
    private Long communityArticleId;
    private String content;

    public Comment toEntity(String author, CommunityArticle communityArticle){
        return Comment.builder()
                .communityArticle(communityArticle)
                .content(content)
                .author(author)
                .build();
    }
}
