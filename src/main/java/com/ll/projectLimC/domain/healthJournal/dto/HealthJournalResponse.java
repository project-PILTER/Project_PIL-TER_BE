package com.ll.projectLimC.domain.healthJournal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ll.projectLimC.domain.healthJournal.ConditionStatus;
import com.ll.projectLimC.domain.healthJournal.entity.HealthJournal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Schema(description = "건강일지 조회 응답 정보 폼")
public class HealthJournalResponse {
    @Schema(description = "건강일지 고유 ID (PK)", example = "1")
    private final Long id;

    @Schema(description = "일지 기록 날짜 (ISO 표준 날짜 포맷)", example = "2026-06-24")
    private final LocalDate journalDate;

    @Schema(description = "오늘의 전반적인 컨디션 상태 (GOOD, NORMAL, BAD)", example = "GOOD")
    private final ConditionStatus conditionStatus;

    @Schema(description = "컨디션 상태에 대한 설명", example = "좋음")
    private final String conditionDescription;

    @Schema(description = "통증 점수 (0: 통증 없음 ~ 10: 극심한 통증)", example = "3")
    private final int painScore;

    @Schema(description = "오늘의 건강 상태 및 증상 상세 기록",
            example = "오전부터 뒷목이 뻐근하고 가벼운 두통이 지속됨. 휴식을 취하니 조금 나아짐.")
    private final String content;

    @Schema(description = "선택한 증상 태그 목록", example = "두통")
    private final List<String> symptoms;

    @Schema(description = "오늘 복용한 영양제 및 의약품 목록", example = "타이레놀")
    private final List<String> supplements;

    @Schema(description ="건강일지 생성 시간", example = "26-08-03 00:00:00 + 9:00")
    // ⭐️ 원하는 형태대로 JSON 변환 포맷 지정
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss.SSS Z",
            timezone = "Asia/Seoul"
    )
    private final OffsetDateTime createdAt;

    public HealthJournalResponse(HealthJournal journal){
        this.id = journal.getId();;
        this.journalDate = journal.getJournalDate();
        this.conditionStatus = journal.getConditionStatus();
        this.conditionDescription = journal.getConditionStatus().getDescription();
        this.painScore = journal.getPainScore();;
        this.content = journal.getContent();;
        this.createdAt = journal.getCreatedAt();
        this.symptoms = journal.getSymptoms();
        this.supplements = journal.getSupplements();
    }
}
