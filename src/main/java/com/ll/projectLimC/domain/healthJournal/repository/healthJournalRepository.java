package com.ll.projectLimC.domain.healthJournal.repository;

import com.ll.projectLimC.domain.healthJournal.entity.HealthJournal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface healthJournalRepository extends JpaRepository<HealthJournal, Long> {

}