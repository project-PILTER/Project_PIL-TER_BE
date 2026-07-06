package com.ll.projectLimC.domain.community.service;

import com.ll.projectLimC.domain.comment.repository.CommentRepository;
import com.ll.projectLimC.domain.community.ArticleStatus;
import com.ll.projectLimC.domain.community.dto.CommunityArticleCreateForm;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.community.repository.CommunityRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    // 커뮤니티 게시글 단건 조회용 메서드
    public CommunityArticle findById(Long id){
        return communityRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_ARTICLE));
    }

    // 커뮤니티 게시글 전체 조회용 메서드
    public List<CommunityArticle> findAll(Pageable pageable){
        return communityRepository.findByStatus(ArticleStatus.PUBLISHED, pageable);
    }

    // 커뮤니티 게시글 삭제용 메서드
    public void deleteCommunityArticle(long id){
        CommunityArticle communityArticle = communityRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_ARTICLE));

        authorizeArticleAuthor(communityArticle);
        communityRepository.delete(communityArticle);
    }

    // 커뮤니티 게시글 수정용 메서드
    @Transactional
    public CommunityArticle updateCommunityArticle(long id, UpdateCommunityArticleRequest request){
        CommunityArticle communityArticle = communityRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_ARTICLE));

        authorizeArticleAuthor(communityArticle);
        ArticleStatus nextStatus = request.isDraft() ? ArticleStatus.DRAFT : ArticleStatus.PUBLISHED;

        communityArticle.updateCommunityArticle(request.getTitle(),
                request.getContent(),
                request.getImageUrl(),
                nextStatus);


        return communityArticle;
    }

    // 게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(CommunityArticle communityArticle){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!communityArticle.getAuthor().equals(userName)){
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_THE_ARTICLE);
        }
    }

    // 마이페이지나 임시저장함 뷰에 뿌려줄 제욱님의 임시저장 목록 조회용 - 선택하기
    public List<CommunityArticle> findMyDrafts(String userName) {
        return communityRepository.findByAuthorAndStatus(userName, ArticleStatus.DRAFT);
    }
}
// 인기 게시글 상위 5개 조회용 메서드
//    public List<CommunityArticle> getPopularCommunityArticles(){
//        return communityRepository.findTop5ByOrderByLikeDesc();
//    }