package com.ll.projectLimC.domain.healthJournal.dto;

import com.ll.projectLimC.domain.healthJournal.ConditionStatus;
import com.ll.projectLimC.domain.healthJournal.entity.HealthJournal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Schema(description = "")
public class HealthJournalResponse {
    private final Long id;
    private final LocalDate journalDate;
    private final ConditionStatus conditionStatus;
    private final String conditionDescription;
    private final int painScore;
    private final String content;
    private final List<String> symptoms;
    private final List<String> supplements;

    public HealthJournalResponse(HealthJournal journal){
        this.id = journal.getId();;
        this.journalDate = journal.getJournalDate();
        this.conditionStatus = journal.getConditionStatus();
        this.conditionDescription = journal.getConditionStatus().getDescription();
        this.painScore = journal.getPainScore();;
        this.content = journal.getContent();;
        this.symptoms = journal.getSymptoms();
        this.supplements = journal.getSupplements();
    }
}
