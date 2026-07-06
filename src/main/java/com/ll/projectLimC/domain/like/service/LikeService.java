package com.ll.projectLimC.domain.like.service;

import com.ll.projectLimC.domain.community.repository.CommunityRepository;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.like.dto.LikeResponse;
import com.ll.projectLimC.domain.like.entity.Like;
import com.ll.projectLimC.domain.like.repository.LikeRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final CommunityRepository communityRepository;

    public LikeResponse toggleLike(Long id, String userName) {
        CommunityArticle communityArticle = communityRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_ARTICLE));

        // 이미 좋아요를 누른 상태인지 확인
        Optional<Like> alreadyLike = likeRepository.findByCommunityArticleAndAuthor(communityArticle, userName);

        boolean isLiked;

        if (alreadyLike.isPresent()){
            // 이미 존재하면 좋아요 취소 (삭제)
            likeRepository.delete(alreadyLike.get());
            isLiked = false;
        }else {
            // 존재하지 않으면 좋아요 등록 (저장)
            Like like = Like.builder()
                    .author(userName)
                    .communityArticle(communityArticle)
                    .build();
            likeRepository.save(like);
            isLiked = true; // 등록됨을 의미.
        }

        long totalLikes = likeRepository.countByCommunityArticle(communityArticle);

        return new LikeResponse(isLiked, totalLikes);
    }
}
