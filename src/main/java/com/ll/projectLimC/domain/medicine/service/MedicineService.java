package com.ll.projectLimC.domain.medicine.service;

import com.ll.projectLimC.domain.medicine.entity.Medicine;
import com.ll.projectLimC.domain.medicine.repository.MedicineRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicineService {
    private final MedicineRepository medicineRepository; // 1. 리포지토리 주입

    // 약품 상세 조회용 메서드
    public Medicine findByMedicineDetailInfo(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NO_EXIST_THAT_MEDICINE));

        medicine.incrementViewCount();

        return medicine;
    }
}