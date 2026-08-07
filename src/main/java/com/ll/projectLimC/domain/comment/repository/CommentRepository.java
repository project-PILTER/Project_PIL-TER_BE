package com.ll.projectLimC.domain.comment.repository;

import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    long countByUser(User user);

    // 특정 게시글의 최상위 댓글만 조회 (대댓글은 parent가 있으므로 제외됨)
    List<Comment> findByCommunityArticleAndParentIsNull(CommunityArticle communityArticle);
}
