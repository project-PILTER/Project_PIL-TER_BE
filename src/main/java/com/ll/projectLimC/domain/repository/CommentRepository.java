package com.ll.projectLimC.domain.repository;

import com.ll.projectLimC.domain.entity.Comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
