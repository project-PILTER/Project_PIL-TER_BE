package com.ll.projectLimC.domain.comment.dto;

import com.ll.projectLimC.domain.comment.entity.Comment;
import com.ll.projectLimC.domain.community.dto.Community.Response.AuthorResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "댓글 수정 요청 응답 폼")
public class UpdateCommentResponse {
    @Schema(description = "댓글 고유 ID (PK)", example = "1")
    private Long id;

    @Schema(description = "게시글 고유 ID", example = "5")
    private Long communityArticleId;

    @Schema(description = "댓글 작성자 닉네임", example = "달려라하니")
    private AuthorResponse author; // String author 대신 객체 구조로 변경!

    @Schema(description = "댓글 내용", example = "헉, 얼른 가까운 내과에 내원하세요.")
    private String content;

    @Schema(description = "댓글 수정 시간", example = "26-08-07 00:00:00 + 9:00")
    private OffsetDateTime updatedAt;

    @Schema(description = "좋아요 수", example = "1")
    private Long likeCount;

    @Schema(description = "부모 댓글 ID (대댓글인 경우 존재)", example = "3")
    private Long parentId;

    @Schema(description = "대댓글 목록 (재귀 구조)")
    private List<AddCommentResponse> children;

    // 엔티티를 DTO로 편하게 변환하기 위한 생성자
    public UpdateCommentResponse(Comment comment) {
        this.id = comment.getId();
        this.communityArticleId = comment.getCommunityArticle() != null ? comment.getCommunityArticle().getId() : null;
        this.content = comment.getContent();
        this.author = AuthorResponse.from(comment.getUser());
        this.likeCount = comment.getLikeCount();
        this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
        this.updatedAt = comment.getUpdatedAt();

        // 자식 댓글(children)을 DTO로 재귀적 매핑
        if (comment.getChildren() != null && !comment.getChildren().isEmpty()) {
            this.children = comment.getChildren().stream()
                    .map(AddCommentResponse::new) // 자기 자신의 생성자를 호출하여 대댓글들도 DTO로 변환
                    .toList();
        } else {
            this.children = new ArrayList<>(); // 자식 없으면 빈 리스트
        }
    }
}
