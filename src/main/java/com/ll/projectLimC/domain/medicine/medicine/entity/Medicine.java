package com.ll.projectLimC.domain.medicine.medicine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 약품 id

    @Column(unique = true)
    private String itemSeq; // 공공데이터 고유 품목기준코드

    @Column(columnDefinition = "TEXT")
    private String medicineName;// 제품명

    @Column(columnDefinition = "TEXT")
    private String manufacturer;// 제조사명

    @Column(columnDefinition = "TEXT")
    private String efficacy;// 효능

    @Column(columnDefinition = "TEXT")
    private String useMethodQesitm;// 복용 용법

    @Column(columnDefinition = "TEXT")
    private String atpnQesitm;// 주의사항

    @Column(columnDefinition = "TEXT")
    private String atpnWarnQesitm; // 부작용

    @Column(columnDefinition = "TEXT")
    private String itemImage; // 약 이미지

    @Column(columnDefinition = "TEXT")
    private String depositMethodQesitm; // 보관방법

    @Builder.Default
    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long viewCount = 0L;

    @Builder.Default
    @Column(nullable = false)
    private boolean isHot = false;

    @Builder.Default
    @Column(nullable = false)
    private Long likeCount = 0L; // 좋아요 수

    @Builder.Default
    @Column(nullable = false)
    private double averageRating = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private double totalReviewCount = 0.0;

    // 기존 객체의 필드 값을 덮어씌우는 용도
    public void updateInfo(String manufacturer, String efficacy, String useMethodQesitm,
                           String atpnQesitm, String atpnWarnQesitm, String itemImage,
                           String depositMethodQesitm) {
        this.manufacturer = manufacturer;
        this.efficacy = efficacy;
        this.useMethodQesitm = useMethodQesitm;
        this.atpnQesitm = atpnQesitm;
        this.atpnWarnQesitm = atpnWarnQesitm;
        this.itemImage = itemImage;
        this.depositMethodQesitm = depositMethodQesitm;
    }

    public void incrementViewCount() {
        if (this.viewCount == null) {
            this.viewCount = 0L;
        }
        this.viewCount++;
    }

    // 상태 변경 메서드
    public void updateHotStatus(boolean isHot) {
        this.isHot = isHot;
    }

    public void updateRatingStats(double averageRating, long totalReviewCount) {
        this.averageRating = averageRating;
        this.totalReviewCount = totalReviewCount;
    }

    public Long getLikeCount() {
        return this.likeCount == null ? 0L : this.likeCount;
    }

    // 좋아요 토글 시 카운트 증감 처리
    public void toggleLike(boolean isHot) {
        this.isHot = isHot;
        long currentCount = (this.likeCount == null) ? 0L : this.likeCount;
        this.likeCount = isHot ? currentCount + 1 : Math.max(0L, currentCount - 1);
    }
}
