package com.ll.projectLimC.domain.entity.Comment;

import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "content", nullable = false)
    private String content;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    private CommunityArticle communityArticle;

    @Builder
    public Comment(CommunityArticle communityArticle, String author, String content){
        this.communityArticle = communityArticle;
        this.author = author;
        this.content = content;
    }

    public void updateComment(CommunityArticle communityArticle, String content){
        this.communityArticle = communityArticle;
        this.content = content;
    }
}