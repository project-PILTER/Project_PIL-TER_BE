package com.ll.projectLimC.domain.community.service.ArticleDraftsService;


import com.ll.projectLimC.domain.community.ArticleStatus;
import com.ll.projectLimC.domain.community.dto.ArticleDrafts.Request.ArticleDraftsSaveRequest;
import com.ll.projectLimC.domain.community.dto.ArticleDrafts.Response.ArticleDraftsListResponse;
import com.ll.projectLimC.domain.community.entity.ArticleDrafts.ArticleDrafts;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.community.repository.ArticleDraftsRepository;
import com.ll.projectLimC.domain.community.repository.CommunityRepository;
import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.user.repository.UserRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleDraftsService {
    private final ArticleDraftsRepository articleDraftsRepository;
    private final UserRepository userRepository; // User 조회를 위해 추가

    /**
     * 임시저장 생성 또는 수정 (draftId 유무에 따른 처리)
     */
    @Transactional
    public Long saveOrUpdateDraft(Long draftId, ArticleDraftsSaveRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        if (draftId != null) {
            ArticleDrafts draft = articleDraftsRepository.findByIdAndUser(draftId, user)
                    .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_AUTHORIZED_THE_DRAFT_ARTICLE));

            draft.updateDrafts(request.getTitle(), request.getContent(), request.getCategory(), request.getUpdatedAt());
            return draft.getId();
        }

        ArticleDrafts newDraft = ArticleDrafts.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .user(user)
                .build();

        return articleDraftsRepository.save(newDraft).getId();
    }

    /**
     * 내 임시저장 목록 전체 조회
     */
    public List<ArticleDraftsListResponse> findMyDrafts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        return articleDraftsRepository.findByUserOrderByUpdatedAtDesc(user)
                .stream()
                .map(ArticleDraftsListResponse::new)
                .toList();
    }

    /**
     * 특정 임시저장 글 단건 상세 조회 (이어쓰기용)
     */
    public ArticleDraftsListResponse findDraftById(Long draftId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        ArticleDrafts draft = articleDraftsRepository.findByIdAndUser(draftId, user)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_DRAFT_ARTICLE));

        return new ArticleDraftsListResponse(draft);
    }

    /**
     * 특정 임시저장 글 단건 삭제
     */
    @Transactional
    public void deleteDraft(Long draftId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        articleDraftsRepository.deleteByIdAndUser(draftId, user);
    }
}
