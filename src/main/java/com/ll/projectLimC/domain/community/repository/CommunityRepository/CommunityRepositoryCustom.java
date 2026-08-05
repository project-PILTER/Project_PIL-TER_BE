package com.ll.projectLimC.domain.community.repository.CommunityRepository;

import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;

import java.util.List;

public interface CommunityRepositoryCustom {
    List<CommunityArticle> searchCommunityArticle(String keyword);
}
