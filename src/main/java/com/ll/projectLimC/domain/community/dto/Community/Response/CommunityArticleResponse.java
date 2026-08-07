package com.ll.projectLimC.domain.community.dto.Community.Response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ll.projectLimC.domain.comment.dto.AddCommentResponse;
import com.ll.projectLimC.domain.comment.dto.CommentResponse;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@JsonPropertyOrder({ "title", "content" })
@Schema(description = "커뮤니티 게시글 상세 응답 정보")
public class CommunityArticleResponse {
    private Long id;

    @Schema(description = "게시글 제목", example = "배가 너무 아파요.")
    private String title;

    @Schema(description = "게시글 본문 내용", example = "배가 아파요. 어떤 약을 먹어야 할까요?")
    private String content;
    private CategoryResponse category;
    private String imageUrl;

    private AuthorResponse author;

    private Long viewCount;
    private int likeCount;
    private int commentCount;
    private boolean isHot;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private List<AddCommentResponse> comments;

    @JsonPropertyOrder({ "title", "content" })
    public CommunityArticleResponse(CommunityArticle communityArticle){
        this.id = communityArticle.getId();
        this.title = communityArticle.getTitle();
        this.content = communityArticle.getContent();

        this.category = new CategoryResponse(1L, communityArticle.getCategory());

        this.author = AuthorResponse.from(communityArticle.getUser());

        this.imageUrl = communityArticle.getImageUrl();

        // 1. likeCount 주석 해제 및 null 체크
        this.likeCount = communityArticle.getLikes() != null ? communityArticle.getLikes().size() : 0;
        this.commentCount = communityArticle.getComments() != null ? communityArticle.getComments().size() : 0;

        // 2. viewCount null 안전하게 초기화
        Long articleViewCount = communityArticle.getViewCount();
        this.viewCount = articleViewCount != null ? articleViewCount : 0L;

        // 3. HOT 조건 판별 (viewCount가 0L 이상으로 확실히 채워졌으므로 안전함)
        // HOT 조건 (좋아요 100개 이상 OR 조회수 1000회 이상)
        this.isHot = this.likeCount >= 100 || this.viewCount >= 1000L;


        this.createdAt = communityArticle.getCreatedAt();
        this.updatedAt = communityArticle.getUpdatedAt();

        // 5. 댓글 목록 DTO 변환
        this.comments = communityArticle.getComments() != null ?
                communityArticle.getComments().stream()
                        .filter(comment -> comment.getParent() == null) // 부모가 없는 최상위 댓글만 추출!
                        .map(AddCommentResponse::new) // 대댓글은 AddCommentResponse 내부에서 children으로 자동 재귀 매핑됨
                        .collect(Collectors.toList())
                : new ArrayList<>();
    }
}
