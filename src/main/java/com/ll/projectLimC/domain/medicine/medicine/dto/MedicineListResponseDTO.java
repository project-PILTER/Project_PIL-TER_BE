package com.ll.projectLimC.domain.medicine.medicine.dto;

import com.ll.projectLimC.domain.medicine.medicine.entity.Medicine;
import com.ll.projectLimC.domain.medicine.review.EffectType;
import com.ll.projectLimC.domain.medicine.review.dto.response.ReviewResponseDto;
import com.ll.projectLimC.domain.medicine.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Schema(description = "약품 목록 응답 정보")
public class MedicineListResponseDTO {
    private Long id;

    @Schema(description = "제품명", example = "타이레놀정500밀리그램")
    private String medicineName;

    @Schema(description = "제조사명", example = "한국얀센")
    private String manufacturer;

    @Schema(description = "효능")
    private String efficiency;

    @Schema(description = "복용 용법")
    private String useMethodQesitm;

    @Schema(description = "주의사항")
    private String atpnQesitm;

    @Schema(description = "부작용/경고")
    private String atpnWarnQesitm;

    @Schema(description = "약 이미지 URL")
    private String itemImage;

    @Schema(description = "보관 방법")
    private String depositMethodQesitm;

    @Schema(description = "해당 약품 후기 목록")
    private List<ReviewResponseDto> reviews;

    @Schema(description = "해당 약품 평균 후기")
    private double averageRating;

    @Schema(description = "해당 약품 전체 후기 횟수")
    private double totalReviewCount;

    @Schema(description = "약품 북마크 횟수")
    private long bookmarkCount;

    @Schema(description = "사용자 만족도 통계(효과 있음)")
    private int effectivePercent;

    @Schema(description = "사용자 만족도 통계(효과 없음)")
    private int ineffectivePercent;

    @Schema(description = "사용자 만족도 통계(부작용 겪음)")
    private int sideEffectPercent;

    // 만약 약품에도 조회수나 인기 여부(isHot) 같은 필드를 추가하고 싶다면 여기에 선언 가능
    @Schema(description = "인기 약품 여부")
    private boolean isHot;

    private Long likeCount;

    // 목록 조회용 생성자 (Medicine만 받음)
    public MedicineListResponseDTO(Medicine medicine) {
        this.id = medicine.getId();
        this.medicineName = medicine.getMedicineName();
        this.manufacturer = medicine.getManufacturer();
        this.efficiency = medicine.getEfficacy();
        this.useMethodQesitm = medicine.getUseMethodQesitm();
        this.atpnQesitm = medicine.getAtpnQesitm();
        this.atpnWarnQesitm = medicine.getAtpnWarnQesitm();
        this.itemImage = medicine.getItemImage();
        this.depositMethodQesitm = medicine.getDepositMethodQesitm();
        this.isHot = medicine.getViewCount() != null && medicine.getViewCount() >= 1000L;
        this.likeCount = medicine.getLikeCount();

        // Medicine 필드값 바인딩
        this.averageRating = medicine.getAverageRating();
        this.totalReviewCount = medicine.getTotalReviewCount();

        this.bookmarkCount = 0;
        this.effectivePercent = 0;
        this.ineffectivePercent = 0;
        this.sideEffectPercent = 0;
        this.reviews = List.of();
    }

    // CommunityArticleResponse처럼 생성자 기반 매핑 적용
    public MedicineListResponseDTO(Medicine medicine, List<Review> reviews, long bookmarkCount) {
        this.id = medicine.getId();
        this.medicineName = medicine.getMedicineName();
        this.manufacturer = medicine.getManufacturer();
        this.efficiency = medicine.getEfficacy();
        this.useMethodQesitm = medicine.getUseMethodQesitm();
        this.atpnQesitm = medicine.getAtpnQesitm();
        this.atpnWarnQesitm = medicine.getAtpnWarnQesitm();
        this.itemImage = medicine.getItemImage();
        this.depositMethodQesitm = medicine.getDepositMethodQesitm();
        this.isHot = medicine.getViewCount() != null && medicine.getViewCount() >= 1000L;
        this.bookmarkCount = bookmarkCount;
        this.totalReviewCount = reviews != null ? reviews.size() : 0;
        this.likeCount = medicine.getLikeCount();

        if (this.totalReviewCount > 0) {
            double sumRating = reviews.stream().mapToInt(Review::getRating).sum();
            this.averageRating = Math.round((sumRating / this.totalReviewCount) * 10.0) / 10.0;

            long effectiveCount = reviews.stream().filter(r -> r.getEffectType() == EffectType.EFFECTIVE).count();
            long ineffectiveCount = reviews.stream().filter(r -> r.getEffectType() == EffectType.INEFFECTIVE).count();

            this.effectivePercent = (int) ((double) effectiveCount / totalReviewCount * 100);
            this.ineffectivePercent = (int) ((double) ineffectiveCount / totalReviewCount * 100);
            this.sideEffectPercent = 100 - (this.effectivePercent + this.ineffectivePercent);
        } else {
            this.averageRating = 0.0;
            this.effectivePercent = 0;
            this.ineffectivePercent = 0;
            this.sideEffectPercent = 0;
        }

        this.reviews = reviews != null ? reviews.stream().
                map(ReviewResponseDto::new)
                .collect(Collectors.toList()) : List.of();
    }
}
