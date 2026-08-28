package com.ll.projectLimC.domain.medicine.medicine.service;

import com.ll.projectLimC.domain.medicine.bookmark.repository.BookmarkRepository;
import com.ll.projectLimC.domain.medicine.medicine.dto.MedicineDetailResponseDto;
import com.ll.projectLimC.domain.medicine.medicine.dto.MedicineListResponseDTO;
import com.ll.projectLimC.domain.medicine.medicine.entity.Medicine;
import com.ll.projectLimC.domain.medicine.medicine.repository.MedicineRepository;
import com.ll.projectLimC.domain.medicine.review.entity.Review;
import com.ll.projectLimC.domain.medicine.review.repository.ReviewRepository;
import com.ll.projectLimC.domain.user.repository.UserRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineService {
    private final MedicineRepository medicineRepository;
    private final ReviewRepository reviewRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    // 약품 상세 조회용 메서드
    @Transactional
    public MedicineDetailResponseDto getMedicineDetailInfo(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NO_EXIST_THAT_MEDICINE));

        // 조회수 증가 (Dirty Checking으로 DB 반영)
        medicine.incrementViewCount();

        List<Review> reviews = reviewRepository.findByMedicineId(id);
        long bookmarkCount = bookmarkRepository.countByMedicineId(id);

        return new MedicineDetailResponseDto(medicine, reviews, bookmarkCount);
    }

    // 좋아요 표시하기
    @Transactional
    public boolean updateLikeStatus(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NO_EXIST_THAT_MEDICINE));

        boolean newLikeStatus = !medicine.isHot();
        // isHot 상태 반전과 함께 likeCount 증감 실행
        medicine.toggleLike(newLikeStatus);

        return newLikeStatus;
    }
}