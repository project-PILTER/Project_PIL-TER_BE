package com.ll.projectLimC.domain.medicine.dto;

import com.ll.projectLimC.domain.medicine.entity.Medicine;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
public class MedicineResponseDTO {
    private String medicineName;
    private String manufacturer;
    private String efficiency;

    public static MedicineResponseDTO fromEntity(Medicine medicine) {
        return MedicineResponseDTO.builder()
                .medicineName(medicine.getMedicineName())
                .manufacturer(medicine.getManufacturer())
                .efficiency(medicine.getEfficacy())
                .build();
    }
}
