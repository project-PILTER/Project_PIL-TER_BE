package com.ll.projectLimC.domain.community;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 저장 상태")
public enum ArticleStatus {
    @Schema(description = "임시 저장 상태")
    DRAFT,

    @Schema(description = "최종 등록 상태")
    PUBLISHED
}
