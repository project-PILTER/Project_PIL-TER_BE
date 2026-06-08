package com.ll.projectLimC.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import lombok.Getter;

@Getter
@JsonPropertyOrder({ "title", "content" })
public class CommunityArticleResponse {
    private String title;
    private String content;

    @JsonPropertyOrder({ "title", "content" })
    public CommunityArticleResponse(CommunityArticle communityArticle){
        this.title = communityArticle.getTitle();
        this.content = communityArticle.getContent();
    }
}
