package com.ll.projectLimC.domain.comment.dto;

import com.ll.projectLimC.domain.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "댓글 생성 응답 폼")
public class AddCommentResponse {
    @Schema(description = "댓글 고유 ID (PK)", example = "1")
    private Long id;

    @Schema(description = "댓글 내용", example = "버틸만 하시면 타이레놀 드시고, 힘드시면 가까운 신경과에 내원해보세요.")
    private String content;

    @Schema(description = "댓글 작성자 닉네임", example = "달려라하니")
    private CommentAuthorResponse author;


    public AddCommentResponse(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.author = new CommentAuthorResponse(comment.getUser());
    }
}
