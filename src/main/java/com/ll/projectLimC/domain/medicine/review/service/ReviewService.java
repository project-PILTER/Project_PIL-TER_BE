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
}
