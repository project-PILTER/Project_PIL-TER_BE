package com.ll.projectLimC.domain.medicine.repository;

import com.ll.projectLimC.domain.medicine.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    // 중복 방지를 위해 이름으로 검색
    Optional<Medicine> findByName(String name);
}
