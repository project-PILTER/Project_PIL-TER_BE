package com.ll.projectLimC.domain.community.service;

import com.ll.projectLimC.domain.community.dto.CommunityArticleCreateForm;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // final이 붙거나 @NonNull이 붙은 필드의 생성자 추가
public class CommunityService {
    private final CommunityRepository communityRepository;

    public CommunityArticle save(CommunityArticleCreateForm request){
        return communityRepository.save(request.toEntity());
    }

    public List<CommunityArticle> findAll(){
        return communityRepository.findAll();
    }
}
