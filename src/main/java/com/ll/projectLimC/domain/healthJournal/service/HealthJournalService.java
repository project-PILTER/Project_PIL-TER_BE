package com.ll.projectLimC.domain.healthJournal.service;

import com.ll.projectLimC.domain.User.entity.User;
import com.ll.projectLimC.domain.User.repository.UserRepository;
import com.ll.projectLimC.domain.healthJournal.dto.HealthJournalRequest;
import com.ll.projectLimC.domain.healthJournal.entity.HealthJournal;
import com.ll.projectLimC.domain.healthJournal.repository.HealthJournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HealthJournalService {
    private final HealthJournalRepository healthJournalRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long saveHealthJournal(HealthJournalRequest request,
                                  String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("not found user : " + userEmail));

        HealthJournal journal = HealthJournal.builder()
                .user(user)
                .journalDate(request.getJournalDate())
                .condiotionStatus(request.getConditionStatus())
                .painScore(request.getPainScore())
                .content(request.getContent())
                .symptoms(request.getSymptoms())
                .supplements(request.getSupplements())
                .build();

        return healthJournalRepository.save(journal).getId();
    }
}
