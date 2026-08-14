package com.ll.projectLimC.domain.medicine.dto;

import com.ll.projectLimC.domain.medicine.entity.Medicine;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "약품 상세 응답 정보")
public class MedicineResponseDTO {
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

    // 만약 약품에도 조회수나 인기 여부(isHot) 같은 필드를 추가하고 싶다면 여기에 선언할 수 있습니다.
    @Schema(description = "인기 약품 여부")
    private boolean isHot;

    // ✨ CommunityArticleResponse처럼 생성자 기반 매핑 적용
    public MedicineResponseDTO(Medicine medicine) {
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
    }
}
