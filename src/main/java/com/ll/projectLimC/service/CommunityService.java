package com.ll.projectLimC.service;

import com.ll.projectLimC.dto.AddArticleRequest;
import com.ll.projectLimC.entity.Article.Article;
import com.ll.projectLimC.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // final이 붙거나 @NonNull이 붙은 필드의 생성자 추가
public class CommunityService {
    private final CommunityRepository communityRepository;

    public Article save(AddArticleRequest request){
        return communityRepository.save(request.toEntity());
    }
}
