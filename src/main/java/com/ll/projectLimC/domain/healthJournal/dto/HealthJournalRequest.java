package com.ll.projectLimC.domain.healthJournal.dto;

import com.ll.projectLimC.domain.healthJournal.ConditionStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class HealthJournalRequest {
    private LocalDate journalDate;
    // 프론트가 "GOOD"이라고 보내면 자동으로 Enum 매핑됨
    private ConditionStatus conditionStatus;
    private int painScore;
    private String content;
    private List<String> symptoms;
    private List<String> supplements;
}
