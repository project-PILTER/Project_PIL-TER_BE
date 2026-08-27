package com.ll.projectLimC.domain.medicine.review.service;

import com.ll.projectLimC.domain.medicine.medicine.entity.Medicine;
import com.ll.projectLimC.domain.medicine.medicine.repository.MedicineRepository;
import com.ll.projectLimC.domain.medicine.review.dto.request.ReviewRequestDto;
import com.ll.projectLimC.domain.medicine.review.entity.Review;
import com.ll.projectLimC.domain.medicine.review.repository.ReviewRepository;
import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.user.repository.UserRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;

    // ⭐️ [공통] 약품 평점 및 후기 수 갱신 메서드
    private void updateMedicineStats(Medicine medicine) {
        long totalCount = reviewRepository.countByMedicine(medicine);
        Double avgRating = reviewRepository.findAverageRatingByMedicine(medicine);

        double finalAvg = (avgRating != null) ? Math.round(avgRating * 10.0) / 10.0 : 0.0;
        medicine.updateRatingStats(finalAvg, totalCount);
    }

    @Transactional
    public void createReview(Long medicineId, Long userId, ReviewRequestDto request){
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NO_EXIST_THAT_MEDICINE));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        Review review = Review.builder()
                .medicine(medicine)
                .user(user)
                .rating(request.getRating())
                .effectType(request.getEffectType())
                .symptomTag(request.getSymptomTag())
                .content(request.getContent())
                .createdAt(OffsetDateTime.now())
                .build();

        reviewRepository.save(review);

        updateMedicineStats(medicine);
    }

    @Transactional
    public void updateMedicineReview(long Id, String email, ReviewRequestDto request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        Review review = reviewRepository.findById(Id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_REVIEW));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        review.updateReview(request.getRating(), request.getContent(), request.getEffectType());

        updateMedicineStats(review.getMedicine());
    }

    @Transactional
    public void deleteMedicineReview(long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_REVIEW));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        Medicine medicine = review.getMedicine(); // 삭제 전 객체 참조 저장

        reviewRepository.delete(review);
        reviewRepository.flush(); // DB 삭제 연산 반영 확정

        updateMedicineStats(medicine);
    }
}
