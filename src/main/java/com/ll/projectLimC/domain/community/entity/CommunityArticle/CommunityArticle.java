package com.ll.projectLimC.domain.community.entity.CommunityArticle;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Builder
    public CommunityArticle(String title, String content){
        this.title = title;
        this.content = content;
    }

    public void updateCommunityArticle(String title, String content){
        this.title = title;
        this.content = content;
    }
}
