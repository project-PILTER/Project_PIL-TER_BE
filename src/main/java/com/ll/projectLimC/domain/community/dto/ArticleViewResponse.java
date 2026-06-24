package com.ll.projectLimC.domain.community.dto;

import com.ll.projectLimC.domain.comment.dto.CommentResponse;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Schema(description = "커뮤니티 게시글 상세 조회 응답 정보")
public class ArticleViewResponse {
    @Schema(description = "게시글 고유 ID (PK)", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "머리가 아파요..")
    private String title;

    @Schema(description = "게시글 본문 내용", example = "이럴 때는 어떤 약을 섭취해야 할까요?")
    private String content;

    @Schema(description = "게시글 작성 일시", example = "2026-06-24T11:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "작성자 닉네임", example = "홍길동")
    private String author;

    @Schema(description = "첨부 이미지 URL (없는 경우 null)",
            example = "https://images/article1.png")
    private String imageUrl;

    @Schema(description = "게시글에 작성된 댓글 목록", example = "타이레놀 드세요.")
    private List<CommentResponse> comments;

    public ArticleViewResponse(CommunityArticle article){
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.author = article.getAuthor();
        this.imageUrl = article.getImageUrl();
        this.createdAt = article.getCreatedAt();
        this.comments = comments;

    }
}
