package com.ll.projectLimC.domain.community.dto.ArticleDrafts.Response;

import com.ll.projectLimC.domain.community.entity.ArticleDrafts.ArticleDrafts;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class ArticleDraftsListResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String category;
    private final OffsetDateTime updatedAt;

    // Entity를 DTO로 변환하는 생성자
    public ArticleDraftsListResponse(ArticleDrafts drafts) {
        this.id = drafts.getId();
        this.title = (drafts.getTitle() != null && !drafts.getTitle().isBlank())
                ? drafts.getTitle() : "(제목 없음)";
        this.content = drafts.getContent();
        this.category = drafts.getCategory();
        this.updatedAt = drafts.getUpdatedAt(); // 엔티티의 필드명에 맞게 연결
    }
}
