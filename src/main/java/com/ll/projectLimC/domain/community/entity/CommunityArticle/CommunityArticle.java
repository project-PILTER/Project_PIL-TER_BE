package com.ll.projectLimC.domain.community.entity.CommunityArticle;

import com.ll.projectLimC.domain.comment.entity.Comment;
import com.ll.projectLimC.domain.community.ArticleStatus;
import com.ll.projectLimC.domain.like.entity.Like;
import com.ll.projectLimC.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CommunityArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩(LAZY) 권장
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate // 엔티티가 생성될 때 생성 시간 저장
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @LastModifiedDate // 엔티티가 수정될 때 수정 시간 저장
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "imageUrl", nullable = true)
    private String imageUrl;

    @Column(name = "category")
    private String category;

    @OneToMany(mappedBy = "communityArticle", cascade = CascadeType.REMOVE)
    private List<Comment> comments= new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ArticleStatus status = ArticleStatus.PUBLISHED; // 기본값 지정

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L; // 👈 조회수 (기본값 0)

    @OneToMany(mappedBy = "communityArticle", cascade = CascadeType.REMOVE)
    private List<Like> likes = new ArrayList<>();

    // 조회수 증가 메서드
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0L : this.viewCount) + 1;
    }

    @Builder
    public CommunityArticle(User user,
                            String title,
                            String content,
                            String imageUrl,
                            String category,
                            // String nickname
                            ArticleStatus status
                            ){
        this.user = user;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.category = category;
        this.status = status != null ? status : ArticleStatus.PUBLISHED; // 상태값 추가 받아오기
    }

    public void updateCommunityArticle(String title, String content, String imageUrl, String category, ArticleStatus status){
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.category = category;
        if (status != null) {
            this.status = status; // 수정할 때 DRAFT -> PUBLISHED로 전환 가능하도록 추가
        }
    }
}


//    @Column(name = "nickname", nullable = false)
//    private String nickname;
//
//    @Column(name = "viewCount", nullable = false)
//    private Long viewCount;
//
//    @Column(name = "likeCount", nullable = false)
//    private Long likeCount;
//
//    @Column(name = "commentCount", nullable = false)
//    private Long commentCount;