package com.ll.projectLimC.domain.community.service;

import com.ll.projectLimC.domain.comment.repository.CommentRepository;
import com.ll.projectLimC.domain.community.dto.CommunityArticleCreateForm;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ll.projectLimC.domain.community.dto.UpdateCommunityArticleRequest;

import java.util.List;

@Service
@RequiredArgsConstructor // final이 붙거나 @NonNull이 붙은 필드의 생성자 추가
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;

    // 커뮤니티 게시글 저장용 메서드
    public CommunityArticle save(CommunityArticleCreateForm request, String userName){
        return communityRepository.save(request.toEntity(userName));
    }

    public CommunityArticle findById(Long id){
        return communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found " + id));
    }

    // 커뮤니티 게시글 조회용 메서드
    public List<CommunityArticle> findAll(){
        return communityRepository.findAll();
    }

    // 커뮤니티 게시글 삭제용 메서드
    public void deleteCommunityArticle(long id){
        CommunityArticle communityArticle = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(communityArticle);
        communityRepository.delete(communityArticle);
    }

    // 커뮤니티 게시글 수정용 메서드
    @Transactional
    public CommunityArticle updateCommunityArticle(long id, UpdateCommunityArticleRequest request){
        CommunityArticle communityArticle = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found:" + id));

        authorizeArticleAuthor(communityArticle);
        communityArticle.updateCommunityArticle(request.getTitle(), request.getContent(), request.getImageUrl());

        return communityArticle;
    }

    // 인기 게시글 상위 5개 조회용 메서드
    public List<CommunityArticle> getPopularCommunityArticles(){
        return communityRepository.findTop5ByOrderByLikeDesc();
    }

    // 게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(CommunityArticle communityArticle){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!communityArticle.getAuthor().equals(userName)){
            throw new IllegalArgumentException("not authorized");
        }
    }
}
