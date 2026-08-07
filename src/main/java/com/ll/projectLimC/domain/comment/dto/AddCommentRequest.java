package com.ll.projectLimC.domain.comment.dto;

import com.ll.projectLimC.domain.comment.entity.Comment;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "댓글 생성 요청 폼")
public class AddCommentRequest {
    @Schema(description = "게시글 고유 ID (PK)", example = "1")
    private Long communityArticleId;

    @Schema(description = "댓글 내용", example = "버틸만 하시면 타이레놀 드시고, 힘드시면 가까운 신경과에 내원해보세요.")
    private String content;

    private Long parentId;

    public Comment toEntity(User user, CommunityArticle communityArticle, Comment parent){
        return Comment.builder()
                .communityArticle(communityArticle)
                .content(content)
                .parent(parent)
                .user(user)
                .build();
    }
}
