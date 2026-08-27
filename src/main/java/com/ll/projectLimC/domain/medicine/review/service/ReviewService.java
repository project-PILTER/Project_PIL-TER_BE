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
    }

    @Transactional
    public void updateMedicineReview(long Id, String email, ReviewRequestDto request) {
        // 유저 검증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        // 후기 존재 여부 검증
        Review review = reviewRepository.findById(Id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_REVIEW));

        // 3. 작성자 본인 확인
        if (!review.getUser().getId().equals(user.getId())) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        // 4. 더티 체킹(Dirty Checking)을 통한 수정
        review.updateReview(request.getRating(), request.getContent(), request.getEffectType());
    }

    @Transactional
    public void deleteMedicineReview(long id, String email) {
        // 유저 검증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        // 후기 존재 여부 검증
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_REVIEW));

        // 작성자 본인 확인
        if (!review.getUser().getId().equals(user.getId())) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        // 삭제 수행
        reviewRepository.delete(review);
    }
}
