package com.ll.projectLimC.domain.dto;

import com.ll.projectLimC.domain.entity.Comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UpdateCommentResponse {
    private Long id;
    private String author;
    private String content;
    private LocalDateTime createdAt;

    // 엔티티를 DTO로 편하게 변환하기 위한 생성자
    public UpdateCommentResponse(Comment comment) {
        this.id = comment.getId();
        this.author = comment.getAuthor();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
    }
}
