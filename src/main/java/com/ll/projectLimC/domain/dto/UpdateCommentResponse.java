package com.ll.projectLimC.domain.dto;

import com.ll.projectLimC.domain.entity.Comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UpdateCommentResponse {
    private Long id;
    private CommentAuthorResponse author; // String author 대신 객체 구조로 변경!
    private String content;
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
