package com.ll.projectLimC.domain.community.entity.CommunityArticle;

import com.ll.projectLimC.domain.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "author", nullable = false)
    private String author;

    @CreatedDate // 엔티티가 생성될 때 생성 시간 저장
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티가 수정될 때 수정 시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "imageUrl", nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "communityArticle", cascade = CascadeType.REMOVE)
    private List<Comment> comments;


    @Builder
    public CommunityArticle(String author,
                            String title,
                            String content,
                            String imageUrl
                            // String nickname
                            ){
        this.author = author;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        // this.nickname = nickname;
    }

    public void updateCommunityArticle(String title, String content, String imageUrl){
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
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