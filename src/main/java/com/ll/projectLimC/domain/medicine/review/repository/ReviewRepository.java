package com.ll.projectLimC.domain.medicine.review.repository;

import com.ll.projectLimC.domain.medicine.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMedicineId(Long medicineId);
}
