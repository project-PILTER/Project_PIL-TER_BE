package com.ll.projectLimC.domain.medicine.dto;

import com.ll.projectLimC.domain.medicine.entity.Medicine;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
public class MedicineResponseDTO {
    private Long id;
    private String medicineName;
    private String manufacturer;
    private String efficiency;
    private String useMethodQesitm;
    private String atpnQesitm;
    private String atpnWarnQesitm;
    private String itemImage;
    private String depositMethodQesitm;

    public static MedicineResponseDTO fromEntity(Medicine medicine) {
        return MedicineResponseDTO.builder()
                .id(medicine.getId())
                .medicineName(medicine.getMedicineName())
                .manufacturer(medicine.getManufacturer())
                .efficiency(medicine.getEfficacy())
                .useMethodQesitm(medicine.getUseMethodQesitm())
                .atpnQesitm(medicine.getAtpnQesitm())
                .atpnWarnQesitm(medicine.getAtpnWarnQesitm())
                .itemImage(medicine.getItemImage())
                .depositMethodQesitm(medicine.getDepositMethodQesitm())
                .build();
    }
}
