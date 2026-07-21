package com.ll.projectLimC.domain.community.dto.Community.Response;

import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "")
public class CommunityArticleListViewResponse {
    @Schema(description = "게시글 고유 ID (PK)", example = "1")
    private final Long id;

    @Schema(description = "게시글 제목", example = "머리가 아파요..")
    private final String title;

    @Schema(description = "게시글 본문 내용", example = "오후부터 갑자기 편두통이 심한데 타이레놀 먹어도 될까요?")
    private final String content;

    public CommunityArticleListViewResponse(CommunityArticle communityArticle){
        this.id = communityArticle.getId();
        this.title = communityArticle.getTitle();
        this.content = communityArticle.getContent();
    }
}
