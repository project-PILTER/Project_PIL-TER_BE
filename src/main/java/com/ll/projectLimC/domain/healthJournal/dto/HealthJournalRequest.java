package com.ll.projectLimC.domain.healthJournal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ll.projectLimC.domain.healthJournal.ConditionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "건강일지 조회 요청 정보 폼")
public class HealthJournalRequest {

    // 1. @CreatedDate 제거
    // 2. pattern에 선택적 시간/타임존 구문([ '00:00:00'Z]) 추가
    @Schema(description = "일지 기록 날짜 (ISO 표준 날짜 포맷)", example = "2026-06-24")
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd[ '00:00:00'Z]", timezone = "Asia/Seoul")
    private LocalDate journalDate;

    @Schema(description = "오늘의 전반적인 컨디션 상태 (GOOD, NORMAL, BAD)", example = "GOOD")
    private ConditionStatus conditionStatus;

    @Schema(description = "통증 점수 (0: 통증 없음 ~ 10: 극심한 통증)", example = "3")
    private int painScore;

    @Schema(description = "오늘의 건강 상태 및 증상 상세 기록",
            example = "오전부터 뒷목이 뻐근하고 가벼운 두통이 지속됨. 휴식을 취하니 조금 나아짐.")
    private String content;

    @Schema(description = "선택한 증상 태그 목록", example = "두통")
    private List<String> symptoms;

    @Schema(description = "오늘 복용한 영양제 및 의약품 목록", example = "타이레놀")
    private List<String> supplements;
}