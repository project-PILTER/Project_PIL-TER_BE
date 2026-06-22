package com.ll.projectLimC.domain.like.repository;

import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // 유저가 해당 게시글에 좋아요를 이미 눌렀는지 조회
    Optional<Like> findByCommunityArticleAndAuthor(CommunityArticle communityArticle, String author);

    // 해당 게시글의 총 좋아요 개수 카운트
    long countByCommunityArticle(CommunityArticle communityArticle);
}
