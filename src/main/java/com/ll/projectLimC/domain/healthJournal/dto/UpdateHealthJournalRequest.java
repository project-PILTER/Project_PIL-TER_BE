package com.ll.projectLimC.domain.healthJournal.dto;

import com.ll.projectLimC.domain.healthJournal.ConditionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Schema(description = "건강일지 수정 요청 정보")
public class UpdateHealthJournalRequest {
    @Schema(description = "오늘의 전반적인 컨디션 상태 (GOOD, NORMAL, BAD)", example = "BAD")
    private ConditionStatus conditionStatus;

    @Schema(description = "통증 점수 (0: 통증 없음 ~ 10: 극심한 통증)", example = "7")
    private int painScore;

    @Schema(description = "오늘의 건강 상태 및 증상 상세 기록",
            example = "치킨, 떡볶이, 부리또, 등등.. 너무 많이 먹어 배탈이남.")
    private String content;

    @Schema(description = "선택한 증상 태그 목록", example = "복통")
    private List<String> symptoms;

    @Schema(description = "오늘 복용한 영양제 및 의약품 목록", example = "메부라틴")
    private List<String> supplements;
}
