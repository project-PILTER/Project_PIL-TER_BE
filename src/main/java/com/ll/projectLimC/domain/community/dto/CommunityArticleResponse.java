package com.ll.projectLimC.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@JsonPropertyOrder({ "title", "content" })
@Schema(description = "커뮤니티 게시글 상세 응답 정보")
public class CommunityArticleResponse {
    @Schema(description = "게시글 제목", example = "배가 너무 아파요.")
    private String title;

    @Schema(description = "게시글 본문 내용", example = "배가 아파요. 어떤 약을 먹어야 할까요?")
    private String content;

    @JsonPropertyOrder({ "title", "content" })
    public CommunityArticleResponse(CommunityArticle communityArticle){
        this.title = communityArticle.getTitle();
        this.content = communityArticle.getContent();
    }
}
