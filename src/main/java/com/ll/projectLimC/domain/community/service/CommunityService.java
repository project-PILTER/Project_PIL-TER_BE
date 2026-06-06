package com.ll.projectLimC.domain.community.service;

import com.ll.projectLimC.domain.community.dto.CommunityArticleCreateForm;
import com.ll.projectLimC.domain.community.dto.UpdateCommunityArticleRequest;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // final이 붙거나 @NonNull이 붙은 필드의 생성자 추가
public class CommunityService {
    private final CommunityRepository communityRepository;

    // 커뮤니티 게시글 저장용 메서드
    public CommunityArticle save(CommunityArticleCreateForm request){
        return communityRepository.save(request.toEntity());
    }

    // 커뮤니티 게시글 조회용 메서드
    public List<CommunityArticle> findAllCommunityArticle(){
        return communityRepository.findAll();
    }

    // 커뮤니티 게시글 삭제용 메서드
    public void deleteCommunityArticle(long id){
        communityRepository.deleteById(id);
    }

    // 커뮤니티 게시글 수정용 메서드
    @Transactional
    public CommunityArticle updateCommunityArticle(long id, UpdateCommunityArticleRequest request){
        CommunityArticle communityArticle = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found:" + id));

        communityArticle.updateCommunityArticle(request.getTitle(), request.getContent());

        return communityArticle;
    }
}
