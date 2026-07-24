package com.ll.projectLimC.domain.comment.dto;

import com.ll.projectLimC.domain.comment.entity.Comment;
import com.ll.projectLimC.domain.community.dto.Community.Response.AuthorResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "대댓글 관련 정보 응답 폼")
public class CommentResponse {
    @Schema(description = "댓글 고유 ID", example = "23")
    private Long id;

    @Schema(description = "게시글 고유 ID (PK)", example = "1")
    private Long articleId;

    @Schema(description = "대댓글 고유 ID", example = "123")
    private Long parentId; // 대댓글 대응 (없으면 null)

    @Schema(description = "대댓글 작성자", example = "나는야김땡땡")
    private AuthorResponse author; // 신설한 작성자 DTO 삽입

    @Schema(description = "대댓글 내용", example = "댓글 감사합니다!")
    private String content;

    @Schema(description = "좋아요 개수")
    private long likeCount; // 좋아요 수

    @Schema(description = "대댓글 작성 작성 일시", example = "2026-06-24T11:00:00")
    private OffsetDateTime createdAt;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.articleId = comment.getCommunityArticle() != null ? comment.getCommunityArticle().getId() : null;

        // 대댓글 미구현 -> null
        this.parentId = null;

        // 작성자 DTO 변환
        this.author = AuthorResponse.from(comment.getUser());

        this.content = comment.getContent();

        // 댓글 좋아요 미구현 -> 0 (추후 CommentLike 기능 개발 시 연결)
        this.likeCount = 0;

        this.createdAt = comment.getCreatedAt();
    }
}
