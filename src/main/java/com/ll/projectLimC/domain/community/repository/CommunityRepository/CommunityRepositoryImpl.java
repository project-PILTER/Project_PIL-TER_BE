package com.ll.projectLimC.domain.community.repository.CommunityRepository;

import com.ll.projectLimC.domain.comment.entity.Comment;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ll.projectLimC.domain.community.entity.CommunityArticle.QCommunityArticle.communityArticle;


@RequiredArgsConstructor
public class CommunityRepositoryImpl implements CommunityRepositoryCustom {
    public final JPAQueryFactory queryFactory;

    @Override
    public List<CommunityArticle> searchCommunityArticle(String keyword) {
        return queryFactory
                .selectFrom(communityArticle)
                .where(
                        keyword != null ? communityArticle.content.contains(keyword) : null
                )
                .fetch();
    }
}
