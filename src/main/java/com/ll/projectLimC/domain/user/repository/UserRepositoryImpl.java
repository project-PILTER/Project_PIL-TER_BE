package com.ll.projectLimC.domain.user.repository;

import com.ll.projectLimC.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ll.projectLimC.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> searchUsers(String keyword){
        return queryFactory
                .select(user)
                .where(
                        keyword != null ? user.email.contains(keyword) : null
                )
                .fetch();
    }
}
