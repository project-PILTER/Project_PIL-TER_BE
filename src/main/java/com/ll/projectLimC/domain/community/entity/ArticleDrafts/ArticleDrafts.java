package com.ll.projectLimC.domain.community.entity.ArticleDrafts;

import com.ll.projectLimC.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Table(name = "article_drafts")
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ArticleDrafts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    private String category;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void updateDrafts(String title, String content, String category){
        this.title =title;
        this.content = content;
        this.category = category;
    }
}
