package com.ll.projectLimC.domain.healthJournal.dto;

import com.ll.projectLimC.domain.healthJournal.ConditionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Schema(description = "")
public class UpdateHealthJournalRequest {
    private ConditionStatus conditionStatus;
    private int painScore;
    private String content;
    private List<String> symptoms;
    private List<String> supplements;
}
