package com.ll.projectLimC.domain.community.dto;

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

    @Schema(description = "수정 후 최종 등록 여부 (false 입력 시 PUBLISHED로 상태 전환)", example = "false")
    private boolean isDraft; // 임시 저장 연장할지, 출간할지 결정
}
