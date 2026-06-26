package com.ll.projectLimC.domain.community.repository;


import com.ll.projectLimC.domain.community.ArticleStatus;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityRepository extends JpaRepository<CommunityArticle, Long> {
    // 메인 전체 피드용: 최종 등록(PUBLISHED)된 글만 최신순(혹은 기본순)으로 조회
    List<CommunityArticle> findByStatus(ArticleStatus status, Pageable pageable);

    // 내가 작성한 임시 저장 글 목록만 따로 불러오기 기능용
    List<CommunityArticle> findByAuthorAndStatus(String author, ArticleStatus status);

    // 좋아요(likes) 개수가 많은 순서대로 상위 5개 게시글을 가져오는 쿼리
    // 기존 SIZE(a.likes) 쿼리 대신, Like 엔티티와 조인하여 개수를 세는 조인 쿼리로 변경.
    @Query("SELECT a FROM CommunityArticle a " +
            "LEFT JOIN Like l ON l.communityArticle = a " +
            "GROUP BY a " +
            "ORDER BY COUNT(l) DESC")
    List<CommunityArticle> findTop5ByOrderByLikeDesc();
}