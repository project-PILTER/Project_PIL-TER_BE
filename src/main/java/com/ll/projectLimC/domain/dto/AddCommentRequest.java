package com.ll.projectLimC.domain.dto;

import com.ll.projectLimC.domain.entity.Comment.Comment;
import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.entity.User.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCommentRequest {
    private Long communityArticleId;
    private String content;

    public Comment toEntity(User user, CommunityArticle communityArticle){
        return Comment.builder()
                .communityArticle(communityArticle)
                .content(content)
                .user(user)
                .build();
    }
}
