package com.ll.projectLimC.domain.dto;

import com.ll.projectLimC.domain.entity.Comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long articleId;
    private Long parentId; // 대댓글 대응 (없으면 null)
    private CommentAuthorResponse author; // 🎯 신설한 작성자 DTO 삽입
    private String content;
    private long likeCount; // 좋아요 수
    private LocalDateTime createdAt;

    public CommentResponse(Comment comment, CommentAuthorResponse authorResponse) {
        this.id = comment.getId();
        this.articleId = comment.getCommunityArticle().getId();
        this.parentId = null; // 💡 아직 대댓글 엔티티 구조가 없다면 기본 null 처리
        this.author = authorResponse;
        this.content = comment.getContent();
        this.likeCount = 0; // 💡 좋아요 기능 연결 전이라면 기본 0 처리
        this.createdAt = comment.getCreatedAt();
    }
}
