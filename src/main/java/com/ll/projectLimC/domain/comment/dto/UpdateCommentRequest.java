package com.ll.projectLimC.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter // 필수 추가-스프링이 JSON 데이터를 바인딩하고 값을 꺼내 쓰기 위해 반드시 필요
@Schema(description = "댓글 수정 요청 폼")
public class UpdateCommentRequest {
    @Schema(description = "댓글 내용", example = "헉, 얼른 가까운 내과에 내원하세요.")
    private String content;

    @Schema(description = "댓글 내용 수정 시간", example = "26-08-03 00:00:00 + 9:00")
    private OffsetDateTime updatedAt;

    private Long parentId;
}
