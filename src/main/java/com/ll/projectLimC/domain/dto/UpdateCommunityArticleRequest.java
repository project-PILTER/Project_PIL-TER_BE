package com.ll.projectLimC.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "커뮤니티 게시글 수정 요청 양식")
public class UpdateCommunityArticleRequest {
    @Schema(description = "수정할 게시글 제목",
            example = "머리가 아파요.")
    private String title;

    @Schema(description = "수정할 게시글 본문 내용", example = "어떤 약을 먹어야 할까요?")
    private String content;

    @Schema(description = "수정할 첨부 이미지 URL (선택)",
            example = "https://example.com/new-image.png")
    private String imageUrl;
}
