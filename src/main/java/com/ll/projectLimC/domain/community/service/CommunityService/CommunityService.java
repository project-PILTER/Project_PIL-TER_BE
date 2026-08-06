package com.ll.projectLimC.domain.community.service.CommunityService;

import com.ll.projectLimC.domain.community.ArticleStatus;
import com.ll.projectLimC.domain.community.dto.Community.Request.CommunityArticleCreateForm;
import com.ll.projectLimC.domain.community.dto.Community.Response.CommunityArticleResponse;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.community.repository.CommunityRepository.CommunityRepository;
import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.user.repository.UserRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ll.projectLimC.domain.community.dto.Community.Request.UpdateCommunityArticleRequest;

@Service
@RequiredArgsConstructor // final이 붙거나 @NonNull이 붙은 필드의 생성자 추가
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    // 커뮤니티 게시글 저장용 메서드
    public CommunityArticle save(CommunityArticleCreateForm request, String email){

        // DB에서 유저 존재 여부 검증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER));

        return communityRepository.save(request.toEntity(user));
    }

    // 커뮤니티 게시글 단건 조회용 메서드
    public CommunityArticle findById(Long id){
        CommunityArticle article = communityRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_ARTICLE));

        article.incrementViewCount(); // 조회수 누적

        return article;
    }

    // 커뮤니티 게시글 전체 조회용 메서드
    public Page<CommunityArticleResponse> findAll(Pageable pageable){
        Page<CommunityArticle> articlePage = communityRepository.findByStatus(ArticleStatus.PUBLISHED, pageable);

        return articlePage.map(CommunityArticleResponse::new);
    }

    // 커뮤니티 게시글 삭제용 메서드
    @Transactional
    public void deleteCommunityArticle(long id, String email) {
        // 유저 존재 여부 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        CommunityArticle communityArticle = communityRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_ARTICLE));

        // 인자로 받은 email 기준으로 작성자 검증
        authorizeArticleAuthor(communityArticle, user.getEmail());

        communityRepository.delete(communityArticle);
    }

    // 커뮤니티 게시글 수정용 메서드
    @Transactional
    public CommunityArticle updateCommunityArticle(long id, UpdateCommunityArticleRequest request, String email) {
        // DB에서 유저 존재 여부 검증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        CommunityArticle communityArticle = communityRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_ARTICLE));

        // 인자로 받은 email 기준으로 작성자 검증
        authorizeArticleAuthor(communityArticle, user.getEmail());

        ArticleStatus nextStatus = request.isDraft() ? ArticleStatus.DRAFT : ArticleStatus.PUBLISHED;

        communityArticle.updateCommunityArticle(
                request.getTitle(),
                request.getUpdatedAt(),
                request.getContent(),
                request.getImageUrl(),
                request.getCategory(),
                nextStatus
        );

        return communityArticle;
    }

    private void authorizeArticleAuthor(CommunityArticle communityArticle, String userEmail) {
        if (communityArticle.getUser() == null || !communityArticle.getUser().getEmail().equals(userEmail)) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_THE_ARTICLE);
        }
    }
}
    // 게시글을 작성한 유저인지 확인
//    private static void authorizeArticleAuthor(CommunityArticle communityArticle){
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        if (!communityArticle.getAuthor().equals(userName)){
//            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_THE_ARTICLE);
//        }
//    }

//    // 마이페이지나 임시저장함 뷰에 뿌려줄 제욱님의 임시저장 목록 조회용
//    public List<CommunityArticle> findMyDrafts(String userName) {
//        return communityRepository.findByAuthorAndStatus(userName, ArticleStatus.DRAFT);
//    }

// 인기 게시글 상위 5개 조회용 메서드
//    public List<CommunityArticle> getPopularCommunityArticles(){
//        return communityRepository.findTop5ByOrderByLikeDesc();
//    }