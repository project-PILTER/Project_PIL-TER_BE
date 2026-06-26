package com.ll.projectLimC.domain.mypage.dto;

import com.ll.projectLimC.domain.healthJournal.entity.HealthJournal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "마이페이지 대시보드 전체 정보 응답")
public class MyPageResponse {

    // 프로필 정보
    private String nickname;
    private String email;
    private LocalDateTime createdAt; // 가입일

    // 상단 카운트 배지
    private long articleCount;       // 작성 게시글 수
    private long commentCount;       // 작성 댓글 수
    private long totalLikesReceived; // 받은 좋아요 총합
    private int continuousHealthDays; // 건강 기록 연속 일수

    // 최근 건강 기록 요약 (화면 우측 하단 위젯용)
    private List<HealthJournalSummaryResponse> recentJournals;

    // 🎯 제욱님의 실데이터(ConditionStatus)와 완벽 연동되는 내부 DTO
    @Getter
    public static class HealthJournalSummaryResponse {
        private String dateLabel; // "오늘", "어제", "N일 전" 또는 날짜 문자열
        private String condition; // 컨디션 한글 설명 ("아주 좋음", "좋음", "보통" 등)

        public HealthJournalSummaryResponse(HealthJournal journal) {
            // 1) 날짜 자동 라벨링 연산 (오늘, 어제, N일 전)
            LocalDate journalDate = journal.getJournalDate();
            LocalDate today = LocalDate.now();

            if (journalDate.isEqual(today)) {
                this.dateLabel = "오늘";
            } else if (journalDate.isEqual(today.minusDays(1))) {
                this.dateLabel = "어제";
            } else {
                long daysBetween = ChronoUnit.DAYS.between(journalDate, today);
                this.dateLabel = daysBetween + "일 전";
            }

            if (journal.getConditionStatus() != null) {
                this.condition = journal.getConditionStatus().getDescription();
            } else {
                this.condition = "기록 없음";
            }
        }
    }
}
