package com.ll.projectLimC.domain.medicine.review.entity;

import com.ll.projectLimC.domain.medicine.medicine.entity.Medicine;
import com.ll.projectLimC.domain.medicine.review.EffectType;
import com.ll.projectLimC.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int rating;

    @Enumerated(EnumType.STRING)
    private EffectType effectType; // 효과 있음 / 효과 없음 / 부작용 겪음

    private String symptomTag; // 증상 태그 (예: 두통, 근육통)

    @Column(columnDefinition = "TEXT")
    private String content; // 후기 내용

    @Builder.Default
    private Long likeCount = 0L; // 좋아요 수

    private OffsetDateTime createdAt;// 약 후기 생성 시간
}
