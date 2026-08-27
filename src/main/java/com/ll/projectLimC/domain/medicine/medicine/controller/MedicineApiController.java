package com.ll.projectLimC.domain.medicine.medicine.controller;

import com.ll.projectLimC.domain.medicine.bookmark.service.BookmarkService;
import com.ll.projectLimC.domain.medicine.medicine.dto.MedicineDetailResponseDto;
import com.ll.projectLimC.domain.medicine.medicine.dto.MedicineListResponseDTO;
import com.ll.projectLimC.domain.medicine.medicine.entity.Medicine;
import com.ll.projectLimC.domain.medicine.medicine.repository.MedicineRepository;
import com.ll.projectLimC.domain.medicine.medicine.service.MedicineService;
import com.ll.projectLimC.domain.medicine.medicine.service.PublicDataSyncService;
import com.ll.projectLimC.domain.medicine.review.dto.request.ReviewRequestDto;
import com.ll.projectLimC.domain.medicine.review.service.ReviewService;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MedicineApiController {
    private final PublicDataSyncService publicDataSyncService;
    private final MedicineRepository medicineRepository;
    private final MedicineService medicineService;
    private final ReviewService reviewService;
    private final BookmarkService bookmarkService;

    // 관리자: 공공데이터 강제 동기화 (Admin용)
    @PostMapping("/medicines/sync")
    public String syncData() throws Exception{
        publicDataSyncService.fetchAndSaveMedicinesFromPortal();
        return "동기화 완료";
    }


    // 약품 목록 조회 (페이지네이션)
    @GetMapping("/medicines")
    public ResponseEntity<Page<MedicineListResponseDTO>> getMedicines(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Medicine> medicinePage = medicineRepository.findAll(pageable);
        return ResponseEntity.ok(medicinePage.map(MedicineListResponseDTO::new));
    }

    // 약품 정보 상세 조회 (리뷰, 통계, 북마크 수 포함)
    @Operation(summary = "약품 정보 상세 조회",
            description = "게시글 고유 ID(id)를 통해 해당 약품 정보의 상세 내용을 조회합니다.")
    @GetMapping("/medicines/{id}")
    public ResponseEntity<MedicineDetailResponseDto> getMedicineDetailInfo(@PathVariable Long id) {
        // ✨ 기존의 findByMedicineDetailInfo 대신 확장된 상세 조회 서비스 메서드 호출
        MedicineDetailResponseDto response = medicineService.getMedicineDetailInfo(id);
        return ResponseEntity.ok(response);
    }

    // 약품 후기 삭제
    @Operation(summary = "약품 후기 글 삭제",
            description = "후기 고유 ID를 받아 해당 후기를 삭제합니다.")
    @DeleteMapping("/medicines/{id}/reviews")
    public ResponseEntity<Void> deleteMedicineReview(
            @PathVariable Long id,
            Principal principal
    ) {
        if (principal == null) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        reviewService.deleteMedicineReview(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    // 북마크(찜하기/취소) 토글 API 추가
    @Operation(summary = "약품 북마크 토글",
            description = "특정 약품의 북마크를 설정하거나 해제합니다.")
    @PostMapping("/medicines/{id}/bookmark")
    public ResponseEntity<String> toggleBookmark(
            @PathVariable Long id,
            @RequestParam Long userId) {
        boolean isBookmarked = bookmarkService.toggleMedicineBookmark(id, userId);
        String message = isBookmarked ? "북마크가 설정되었습니다." : "북마크가 해제되었습니다.";
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "약품 좋아요 상태 토글",
            description = "특정 약품의 인기 상품 지정 상태를 변경합니다.")
    @PostMapping("/medicines/{id}/hot")
    public ResponseEntity<String> toggleHotStatus(@PathVariable Long id) {
        boolean isHot = medicineService.updateLikeStatus(id);
        String message = isHot ? "좋아요(Hot) 약품으로 지정되었습니다." : "좋아요(Hot) 지정이 해제되었습니다.";
        return ResponseEntity.ok(message);
    }

    // 후기 작성 API 추가
    @Operation(summary = "약품 후기 작성",
            description = "특정 약품에 대한 후기와 별점을 등록합니다.")
    @PostMapping("/medicines/{id}/reviews")
    public ResponseEntity<String> createMedicineReview(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestBody ReviewRequestDto request) {
        reviewService.createReview(id, userId, request);
        return ResponseEntity.ok("후기가 등록되었습니다.");
    }

    // 약품 후기 수정
    @Operation(summary = "약품 후기 글 수정",
            description = "후기 고유 ID(reviewId)를 받아 해당 후기를 수정합니다.")
    @PutMapping("/medicines/{id}/reviews")
    public ResponseEntity<String> updateMedicineReview(
            @PathVariable Long id,
            @RequestBody ReviewRequestDto request,
            Principal principal
    ) {
        if (principal == null) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        reviewService.updateMedicineReview(id, principal.getName(), request);
        return ResponseEntity.ok("후기가 수정되었습니다.");
    }


}
