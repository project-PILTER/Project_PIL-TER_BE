package com.ll.projectLimC.domain.comment.repository;

import com.ll.projectLimC.domain.comment.entity.Comment;

import java.util.List;

public interface CommentRepositoryCustom {
    // 필요한 동적 쿼리 메서드 선언
    List<Comment> searchComments(String keyword);
}
