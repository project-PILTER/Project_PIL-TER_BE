package com.ll.projectLimC.domain.like.repository;

import com.ll.projectLimC.domain.comment.entity.Comment;
import com.ll.projectLimC.domain.like.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndAuthor(Comment comment, String author);
    long countByComment(Comment comment);
}
