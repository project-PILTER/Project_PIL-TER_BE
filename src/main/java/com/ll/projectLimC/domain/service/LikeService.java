package com.ll.projectLimC.domain.service;

import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.entity.Like.Like;
import com.ll.projectLimC.domain.repository.CommunityRepository;
import com.ll.projectLimC.domain.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final CommunityRepository communityRepository;

    public boolean toggleLike(Long id, String userName) {
        CommunityArticle communityArticle = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found article : " + id));

        // 이미 좋아요를 누른 상태인지 확인
        Optional<Like> alreadyLike = likeRepository.findByCommunityArticleAndAuthor(communityArticle, userName);

        if (alreadyLike.isPresent()){
            // 이미 존재하면 좋아요 취소 (삭제)
            likeRepository.delete(alreadyLike.get());
            return false;
        }else {
            // 존재하지 않으면 좋아요 등록 (저장)
            Like like = Like.builder()
                    .author(userName)
                    .communityArticle(communityArticle)
                    .build();
            likeRepository.save(like);
            return true; // 등록됨을 의미.
        }
    }
}
