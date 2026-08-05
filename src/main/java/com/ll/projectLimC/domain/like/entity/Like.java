package com.ll.projectLimC.domain.like.entity;

import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Table(
        name = "community_article_like", // 🎯 예약어 충돌을 피하기 위한 안전한 테이블명
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_article_author", // 제약조건 이름 명시 (관리 편의성)
                        columnNames = {"community_article_id", "author"} // 💥 중복 좋아요 철저히 방지!
                )
        }
)
@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "author", nullable = false)
    private String author; // 좋아요를 누른 사용자 이름

    @ManyToOne(fetch = FetchType.LAZY)// 어떤 회원이 어떤 글에 좋아요를 눌렀는지의 매핑
    @JoinColumn(name = "community_article_id", nullable = false)
    private CommunityArticle communityArticle; // 좋아요 누른 커뮤니티 게시글

//    @Builder
//    public Like(String author, CommunityArticle communityArticle){
//        this.author = author;
//        this.communityArticle = communityArticle;
//    }
}
