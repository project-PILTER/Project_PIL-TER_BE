package com.ll.projectLimC.domain.comment.repository;

import com.ll.projectLimC.domain.comment.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ll.projectLimC.domain.comment.entity.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> searchComments(String keyword) {
        return queryFactory
                .selectFrom(comment)
                .where(
                        keyword != null ? comment.content.contains(keyword) : null
                )
                .fetch();
    }
}
