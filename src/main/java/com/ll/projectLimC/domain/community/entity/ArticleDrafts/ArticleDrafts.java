package com.ll.projectLimC.domain.community.entity.ArticleDrafts;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    private String category;

    @CreatedDate
    private OffsetDateTime createdAt;

    @CreatedDate
    private OffsetDateTime updateAt;

    public void updateDrafts(String title, String content, String category){
        this.title =title;
        this.content = content;
        this.category = category;
    }
}
