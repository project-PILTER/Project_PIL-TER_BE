package com.ll.projectLimC.domain.healthJournal.service;

import com.ll.projectLimC.domain.User.entity.User;
import com.ll.projectLimC.domain.User.repository.UserRepository;
import com.ll.projectLimC.domain.healthJournal.dto.HealthJournalRequest;
import com.ll.projectLimC.domain.healthJournal.dto.UpdateHealthJournalRequest;
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

    // 건강일지 생성용 메서드.
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

    // 건강일지 수정용 메서드
    @Transactional
    public void updateHealthJournal(Long id, UpdateHealthJournalRequest request,
                                    String userEmail){
        HealthJournal journal = healthJournalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 건강일지입니다."));

        // [보안 검증] 로그인한 유저가 본인이 쓴 일지가 맞는지 확인
        if (!journal.getUser().getEmail().equals(userEmail)){
            throw new IllegalArgumentException("해당 일지를 수정할 권한이 없습니다.");
        }

        // 엔티티의 내부 수정 메서드 호출
        journal.updateHealthJournal(
                request.getConditionStatus(),
                request.getPainScore(),
                request.getContent(),
                request.getSymptoms(),
                request.getSupplements()
        );
    }

    // 건강일지 삭제용 메서드
    public void deleteHealthJournal(Long id, String userEmail){
        HealthJournal journal = healthJournalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일지입니다."));

        // [보안 검증] 작성자 본인만 삭제 가능하도록 체크
        if (!journal.getUser().getEmail().equals(userEmail)){
            throw new IllegalArgumentException("해당 일지를 삭제할 권한이 없습니다.");
        }

        healthJournalRepository.delete(journal);
    }
}
