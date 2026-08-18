package com.ll.projectLimC.domain.medicine.review.repository;

import com.ll.projectLimC.domain.medicine.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReviewRepository extends JpaRepository<Review, Long> {

}
