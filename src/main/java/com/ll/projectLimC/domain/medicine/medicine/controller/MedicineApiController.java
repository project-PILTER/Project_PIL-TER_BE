package com.ll.projectLimC.domain.medicine.medicine.controller;

import com.ll.projectLimC.domain.medicine.medicine.dto.MedicineResponseDTO;
import com.ll.projectLimC.domain.medicine.medicine.entity.Medicine;
import com.ll.projectLimC.domain.medicine.medicine.repository.MedicineRepository;
import com.ll.projectLimC.domain.medicine.medicine.service.MedicineService;
import com.ll.projectLimC.domain.medicine.medicine.service.PublicDataSyncService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MedicineApiController {
    private final PublicDataSyncService publicDataSyncService;
    private final MedicineRepository medicineRepository;
    private final MedicineService medicineService;

    // 관리자: 공공데이터 강제 동기화 (Admin용)
    @PostMapping("/medicines/sync")
    public String syncData() throws Exception{
        publicDataSyncService.fetchAndSaveMedicinesFromPortal();
        return "동기화 완료";
    }


    @GetMapping("/medicines")
    public ResponseEntity<Page<MedicineResponseDTO>> getMedicines(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Medicine> medicinePage = medicineRepository.findAll(pageable);
        return ResponseEntity.ok(medicinePage.map(MedicineResponseDTO::new));
    }

    @Operation(summary = "약품 정보 상세 조회",
            description = "게시글 고유 ID(id)를 통해 해당 약품 정보의 상세 내용을 조회합니다.")
    @GetMapping("/medicines/{id}")
    public ResponseEntity<MedicineResponseDTO> getMedicineDetailInfo(@PathVariable Long id) {
        Medicine medicine = medicineService.findByMedicineDetailInfo(id);
        return ResponseEntity.ok(new MedicineResponseDTO(medicine));
    }
}
