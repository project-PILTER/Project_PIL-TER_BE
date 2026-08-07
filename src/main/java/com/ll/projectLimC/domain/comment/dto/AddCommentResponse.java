package com.ll.projectLimC.domain.comment.dto;

import com.ll.projectLimC.domain.comment.entity.Comment;
import com.ll.projectLimC.domain.community.dto.Community.Response.AuthorResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Schema(description = "좋아요 수", example = "1")
    private Long likeCount;

    @Schema(description = "부모 댓글 ID (대댓글인 경우 존재)", example = "3")
    private Long parentId;

    @Schema(description = "대댓글 목록 (재귀 구조)")
    private List<AddCommentResponse> children;

    public AddCommentResponse(Comment comment){
        this.id = comment.getId();
        this.communityArticleId = comment.getCommunityArticle() != null ? comment.getCommunityArticle().getId() : null;
        this.content = comment.getContent();
        this.author = AuthorResponse.from(comment.getUser());
        this.likeCount = comment.getLikeCount();
        this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
        this.createdAt = comment.getCreatedAt();

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
