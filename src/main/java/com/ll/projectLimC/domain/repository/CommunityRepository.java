package com.ll.projectLimC.domain.repository;

import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CommunityRepository extends JpaRepository<CommunityArticle, Long> {
    // 좋아요(likes) 개수가 많은 순서대로 상위 5개 게시글을 가져오는 쿼리
    @Query("SELECT a FROM CommunityArticle a ORDER BY SIZE(a.likes) DESC")
    List<CommunityArticle> findTop5ByOrderByLikeDesc();
}