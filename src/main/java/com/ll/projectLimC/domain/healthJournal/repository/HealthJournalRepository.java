package com.ll.projectLimC.domain.healthJournal.repository;

import com.ll.projectLimC.domain.User.entity.User;
import com.ll.projectLimC.domain.healthJournal.entity.HealthJournal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthJournalRepository extends JpaRepository<HealthJournal, Long> {
    // 나중에 유저별로 일지 목록을 가져올 때 사용할 수 있기에 미리 선언
    List<HealthJournal> findByUserIdOrderByJournalDateDesc(User user);
}