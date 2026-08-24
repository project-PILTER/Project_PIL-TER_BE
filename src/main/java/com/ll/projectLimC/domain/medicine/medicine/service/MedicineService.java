package com.ll.projectLimC.domain.medicine.medicine.service;

import com.ll.projectLimC.domain.comment.entity.Comment;
import com.ll.projectLimC.domain.medicine.bookmark.repository.BookmarkRepository;
import com.ll.projectLimC.domain.medicine.medicine.dto.MedicineResponseDTO;
import com.ll.projectLimC.domain.medicine.medicine.entity.Medicine;
import com.ll.projectLimC.domain.medicine.medicine.repository.MedicineRepository;
import com.ll.projectLimC.domain.medicine.review.entity.Review;
import com.ll.projectLimC.domain.medicine.review.repository.ReviewRepository;
import com.ll.projectLimC.domain.user.entity.User;
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
    public MedicineResponseDTO getMedicineDetailInfo(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NO_EXIST_THAT_MEDICINE));

        // 조회수 증가
        medicine.incrementViewCount();

        // 리뷰 목록 및 북마크 개수 조회
        List<Review> reviews = reviewRepository.findByMedicineId(id);
        long bookmarkCount = bookmarkRepository.countByMedicineId(id);

        return new MedicineResponseDTO(medicine, reviews, bookmarkCount);
    }

    @Transactional
    public boolean updateHotStatus(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NO_EXIST_THAT_MEDICINE));

        // 현재 상태의 반대값으로 토글
        boolean newHotStatus = !medicine.isHot();
        medicine.updateHotStatus(newHotStatus);

        return newHotStatus;
    }

    public void deleteMedicineReview(long id, String email) {
        // 1. 유저 검증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        // 2. 후기 존재 여부 검증
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_REVIEW));

        // 3. 작성자 본인 확인
        if (!review.getUser().getId().equals(user.getId())) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        // 4. 삭제 수행
        reviewRepository.delete(review);
    }
}