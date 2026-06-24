package com.ll.projectLimC.domain.comment.dto;

import com.ll.projectLimC.domain.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "댓글 수정 요청 응답 폼")
public class UpdateCommentResponse {
    @Schema(description = "댓글 고유 ID (PK)", example = "1")
    private Long id;

    @Schema(description = "댓글 작성자 닉네임", example = "달려라하니")
    private CommentAuthorResponse author; // String author 대신 객체 구조로 변경!

    @Schema(description = "댓글 내용", example = "헉, 얼른 가까운 내과에 내원하세요.")
    private String content;

    @Schema(description = "댓글 작성 작성 일시", example = "2026-06-24T11:00:00")
    private LocalDateTime createdAt;

    // 엔티티를 DTO로 편하게 변환하기 위한 생성자
    public UpdateCommentResponse(Comment comment) {
        this.id = comment.getId();
        // 변경된 comment.getUser() 구조를 DTO 생성자에 매핑
        this.author = new CommentAuthorResponse(comment.getUser());
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
    }
}
