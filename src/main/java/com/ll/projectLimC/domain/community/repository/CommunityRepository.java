package com.ll.projectLimC.domain.community.repository;


import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityRepository extends JpaRepository<CommunityArticle, Long> {
    // 좋아요(likes) 개수가 많은 순서대로 상위 5개 게시글을 가져오는 쿼리
    // 기존 SIZE(a.likes) 쿼리 대신, Like 엔티티와 조인하여 개수를 세는 조인 쿼리로 변경.
    @Query("SELECT a FROM CommunityArticle a " +
            "LEFT JOIN Like l ON l.communityArticle = a " +
            "GROUP BY a " +
            "ORDER BY COUNT(l) DESC")
    List<CommunityArticle> findTop5ByOrderByLikeDesc();
}