package com.ll.projectLimC.domain.entity.Like;

import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
        name = "community_article_like",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"community_article_id", "author"}) // 중복 좋아요 방지
        }
)
@Entity
@Getter
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

    @Builder
    public Like(String author, CommunityArticle communityArticle){
        this.author = author;
        this.communityArticle = communityArticle;
    }
}
