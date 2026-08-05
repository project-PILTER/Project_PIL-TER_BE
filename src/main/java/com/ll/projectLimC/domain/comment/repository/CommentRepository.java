package com.ll.projectLimC.domain.comment.repository;

import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    long countByUser(User user);
}
