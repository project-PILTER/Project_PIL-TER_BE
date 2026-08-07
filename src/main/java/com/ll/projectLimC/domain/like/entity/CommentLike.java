package com.ll.projectLimC.domain.like.entity;

import com.ll.projectLimC.domain.comment.entity.Comment;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
        name = "comment_like",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_comment_author",
                        columnNames = {"comment_id", "author"} // 중복 좋아요 방지
                )
        }
)
@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "author", nullable = false)
    private String author; // 좋아요를 누른 사용자 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;


}
