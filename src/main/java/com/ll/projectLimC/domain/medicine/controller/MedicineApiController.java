package com.ll.projectLimC.domain.medicine.controller;

import com.ll.projectLimC.domain.medicine.dto.MedicineResponseDTO;
import com.ll.projectLimC.domain.medicine.entity.Medicine;
import com.ll.projectLimC.domain.medicine.repository.MedicineRepository;
import com.ll.projectLimC.domain.medicine.service.PublicDataSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MedicineApiController {
    private final PublicDataSyncService publicDataSyncService;
    private final MedicineRepository medicineRepository;

    // 관리자: 공공데이터 강제 동기화 (Admin용)
    @PostMapping("/medicines/sync")
    public String syncData() throws Exception{
        publicDataSyncService.fetchAndSaveMedicinesFromPortal();
        return "동기화 완료";
    }


    @GetMapping("/medicines")
    public Page<MedicineResponseDTO> getMedicines(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        // 1. pageable을 repository에 전달하여 Page<Medicine>을 가져옴
        Page<Medicine> medicinePage = medicineRepository.findAll(pageable);

        // 2. Page 객체 자체의 .map()을 사용하면 페이징 메타데이터를 유지한 채 DTO로 변환 가능
        return medicinePage.map(MedicineResponseDTO::fromEntity);
    }
}
