package com.ll.projectLimC.domain.medicine.review.repository;

import com.ll.projectLimC.domain.medicine.medicine.entity.Medicine;
import com.ll.projectLimC.domain.medicine.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMedicineId(Long medicineId);

    // 통계 계산용 메서드 추가
    long countByMedicine(Medicine medicine);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.medicine = :medicine")
    Double findAverageRatingByMedicine(@Param("medicine") Medicine medicine);
}
