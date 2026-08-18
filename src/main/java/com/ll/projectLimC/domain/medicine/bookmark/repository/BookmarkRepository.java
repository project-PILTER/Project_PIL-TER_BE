package com.ll.projectLimC.domain.medicine.bookmark.repository;

import com.ll.projectLimC.domain.medicine.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    long countByMedicineId(Long medicineId);
    boolean existsByUserIdAndMedicineId(Long userId, Long medicineId);
    Optional<Bookmark> findByUserIdAndMedicineId(Long userId, Long medicineId);
}
