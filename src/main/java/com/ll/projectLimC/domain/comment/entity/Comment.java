package com.ll.projectLimC.domain.comment.entity;

import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Table(name = "comments")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

//    @Column(name = "author", nullable = false)
//    private String author;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성자 정보는 이 'user' 안에 다 들어있습니다.

    @CreatedDate
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)// 실무 최적화: 성능을 위해 지연 로딩 설정 권장.
    // @JoinColumn(name = "community_article_id")
    private CommunityArticle communityArticle;

    @Builder
    public Comment(CommunityArticle communityArticle, User user, String content, OffsetDateTime createdAt){
        this.communityArticle = communityArticle;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
    }

    // 댓글 수정 시에는 오직 내용(content)만 변경
    public void updateComment(String content, OffsetDateTime updatedAt){
        this.content = content;
        this.updatedAt = updatedAt;
    }
}