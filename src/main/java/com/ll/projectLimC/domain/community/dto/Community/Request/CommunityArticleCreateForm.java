package com.ll.projectLimC.domain.community.dto.Community.Request;

import com.ll.projectLimC.domain.community.ArticleStatus;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 빋는 생성자 추가
@Getter
@Schema(description = "커뮤니티 게시글 생성 요청 양식")
public class CommunityArticleCreateForm {
    @Schema(description = "게시글 제목", example = "배가 너무 아파요.")
    @NotBlank(message = "제목을 입력하세요.") // Spring validation을 의존성 추가하면 사용 가능.
    private String title;

    @Schema(description = "게시글 본문 내용", example = "배가 아파요. 어떤 약을 먹어야 할까요?")
    @NotBlank(message = "내용을 입력하세요.")
    private String content;

    @Schema(description = "첨부 이미지 URL (선택)", example = "https://example.com/image.png")
    private String imageUrl;

    @Schema(description = "임시 저장 여부 (true: 임시저장, false: 즉시등록)", example = "false")
    private Boolean isDraft;

    @Schema(description ="게시글 분류를 위한 카테고리", example = "공통")
    private String category;

    public CommunityArticle toEntity(String author){
        return CommunityArticle.builder()
                .title(title)
                .content(content)
                .author(author)
                .imageUrl(imageUrl)
                // isDraft가 null이어도 안전하게 false로 처리되어 PUBLISHED가 됨.
                // isDraft가 true ➔ ArticleStatus.DRAFT
                .status(Boolean.TRUE.equals(isDraft) ? ArticleStatus.DRAFT : ArticleStatus.PUBLISHED)
                .build();
    }
}
