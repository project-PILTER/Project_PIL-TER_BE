package com.ll.projectLimC.domain.healthJournal.entity;

import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.healthJournal.ConditionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "health_journal")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HealthJournal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_journal_id", updatable = false)
    private Long id;

    // User 엔티티와 다대일(Many-to-One) 연관 관계를 맺음.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 기록 날짜 UI 우측 달력 및 리스트 표현용
    @Column(name = "journal_date", nullable = false)
    private LocalDate journalDate;

    // 평균 기분 / 컨디션 (UI의 '좋음', '보통', '나쁨' 대시보드 연동)
    @Column(name = "condition_status", nullable = false)
    private ConditionStatus conditionStatus;

    @Column(name = "pain_score", nullable = false)
    private int painScore;

    @Column(name = "content", nullable = false)
    private String content;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    // 증상 태그 리스트 (UI의 '가벼운 두통', '피로감' 노란색 배지 매핑)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "journal_symptoms", joinColumns = @JoinColumn(name = "health_journal_id"))
    @Column(name = "symptom_name")
    private List<String> symptoms = new ArrayList<>();

    // 복용한 영양제 / 의약품 태그 리스트 (UI의 '비타민 C', '오메가3' 클립 배지 매핑)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name ="journal_supplements", joinColumns = @JoinColumn(name = "health_journal_id"))
    @Column(name = "supplement_name")
    private List<String> supplements = new ArrayList<>();

    @Builder
    public HealthJournal(User user, LocalDate journalDate, OffsetDateTime createdAt, ConditionStatus condiotionStatus,
                         int painScore, String content, List<String> symptoms, List<String> supplements){
        this.user = user;
        this.journalDate = journalDate;
        this.conditionStatus = condiotionStatus;
        this.painScore = painScore;
        this.content = content;
        this.createdAt = createdAt;
        this.symptoms = symptoms != null ? symptoms : new ArrayList<>();;
        this.supplements = supplements != null ? supplements : new ArrayList<>();
    }

    // 일지 수정 메서드
    public void updateHealthJournal(
            ConditionStatus condiotionStatus,
            int painScore, String content,
            List<String> symptoms, List<String> supplements
    ){
        this.conditionStatus = condiotionStatus;
        this.painScore = painScore;
        this.content = content;
        this.symptoms = symptoms;
        this.supplements = supplements;
    }
}
