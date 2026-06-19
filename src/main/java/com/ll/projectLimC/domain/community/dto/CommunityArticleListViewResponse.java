package com.ll.projectLimC.domain.community.dto;

import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import lombok.Getter;

@Getter
public class CommunityArticleListViewResponse {
    private final Long id;
    private final String title;
    private final String content;

    public CommunityArticleListViewResponse(CommunityArticle communityArticle){
        this.id = communityArticle.getId();
        this.title = communityArticle.getTitle();
        this.content = communityArticle.getContent();
    }
}
