package com.ll.projectLimC.domain.healthJournal.dto;

import com.ll.projectLimC.domain.healthJournal.ConditionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "")
public class HealthJournalRequest {
    @Schema(description = "", example = "26년 3월 1일")
    private LocalDate journalDate;
    @Schema(description = "")
    // 프론트가 "GOOD"이라고 보내면 자동으로 Enum 매핑됨
    private ConditionStatus conditionStatus;
    private int painScore;
    private String content;
    private List<String> symptoms;
    private List<String> supplements;
}
