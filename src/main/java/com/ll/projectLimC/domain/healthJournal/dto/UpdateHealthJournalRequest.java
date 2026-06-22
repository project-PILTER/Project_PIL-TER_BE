package com.ll.projectLimC.domain.healthJournal.dto;

import com.ll.projectLimC.domain.healthJournal.ConditionStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class UpdateHealthJournalRequest {
    private ConditionStatus conditionStatus;
    private int painScore;
    private String content;
    private List<String> symptoms;
    private List<String> supplements;
}
