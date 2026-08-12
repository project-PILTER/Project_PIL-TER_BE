package com.ll.projectLimC.domain.medicine.repository;

import com.ll.projectLimC.domain.medicine.Entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}
