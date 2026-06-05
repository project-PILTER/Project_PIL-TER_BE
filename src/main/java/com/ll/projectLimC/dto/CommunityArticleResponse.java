package com.ll.projectLimC.dto;

import com.ll.projectLimC.entity.CommunityArticle.CommunityArticle;
import lombok.Getter;

@Getter
public class CommunityArticleResponse {
    private String title;
    private String content;

    public CommunityArticleResponse(CommunityArticle communityArticle){
        this.title = communityArticle.getTitle();
        this.content = communityArticle.getContent();
    }
}
