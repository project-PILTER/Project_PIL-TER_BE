package com.ll.projectLimC.domain.comment.dto;

import com.ll.projectLimC.domain.comment.entity.Comment;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.User.entity.User;
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
