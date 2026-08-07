package com.ll.projectLimC.domain.comment.dto;

import com.ll.projectLimC.domain.comment.entity.Comment;
import com.ll.projectLimC.domain.community.dto.Community.Response.AuthorResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "댓글 생성 응답 폼")
public class AddCommentResponse {
    @Schema(description = "댓글 고유 ID (PK)", example = "1")
    private Long id;

    @Schema(description = "게시글 고유 ID", example = "5")
    private Long communityArticleId;

    @Schema(description = "댓글 내용", example = "버틸만 하시면 타이레놀 드시고, 힘드시면 가까운 신경과에 내원해보세요.")
    private String content;

    @Schema(description = "댓글 작성자 닉네임", example = "달려라하니")
    private AuthorResponse author;

    @Schema(description = "댓글 작성 시간", example = "26-08-07 00:00:00 + 9:00")
    private OffsetDateTime createdAt;

    @Schema(description = "댓글 수정 시간", example = "26-08-07 00:00:00 + 9:00")
    private OffsetDateTime updatedAt;

    @Schema(description = "좋아요 수", example = "1")
    private Long likeCount;

    @Schema(description = "부모 댓글 ID (대댓글인 경우 존재)", example = "3")
    private Long parentId;

    public AddCommentResponse(Comment comment){
        this.id = comment.getId();
        this.communityArticleId = comment.getCommunityArticle() != null ? comment.getCommunityArticle().getId() : null;
        this.content = comment.getContent();
        this.author = AuthorResponse.from(comment.getUser());
        this.likeCount = comment.getLikeCount();
        this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}
