package com.ll.projectLimC.domain.healthJournal.service;

import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.user.repository.UserRepository;
import com.ll.projectLimC.domain.healthJournal.dto.HealthJournalRequest;
import com.ll.projectLimC.domain.healthJournal.dto.HealthJournalResponse;
import com.ll.projectLimC.domain.healthJournal.dto.UpdateHealthJournalRequest;
import com.ll.projectLimC.domain.healthJournal.entity.HealthJournal;
import com.ll.projectLimC.domain.healthJournal.repository.HealthJournalRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthJournalService {
    private final HealthJournalRepository healthJournalRepository;
    private final UserRepository userRepository;

    // 내 일지 목록 전체 조회
    @Transactional
    public List<HealthJournalResponse> findAllByUser(String userEmail, Pageable pageable){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        return healthJournalRepository.findByUser(user, pageable)
                .stream()
                // 엔티티 리스트를 DTO 리스트로 변환
                .map(HealthJournalResponse::new)
                .toList();
    }

    // 일지 상세 단건 조회 (수정 화면 등에서 사용)
    @Transactional
    public HealthJournalResponse findById(Long id, String userEmail){
        HealthJournal journal = healthJournalRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_HEALTHJOURNAL));

        if (!journal.getUser().getEmail().equals(userEmail)){
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_THE_HEALTHJOURNAL);
        }

        return new HealthJournalResponse(journal);
    }

    // 건강일지 생성용 메서드.
    @Transactional
    public Long saveHealthJournal(HealthJournalRequest request,
                                  String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        HealthJournal journal = HealthJournal.builder()
                .user(user)
                .journalDate(request.getJournalDate())
                .condiotionStatus(request.getConditionStatus())
                .painScore(request.getPainScore())
                .content(request.getContent())
                .symptoms(request.getSymptoms())
                .createdAt(request.getCreatedAt())
                .supplements(request.getSupplements())
                .build();

        return healthJournalRepository.save(journal).getId();
    }

    // 건강일지 수정용 메서드
    @Transactional
    public void updateHealthJournal(Long id, UpdateHealthJournalRequest request,
                                    String userEmail){
        HealthJournal journal = healthJournalRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_HEALTHJOURNAL));

        // [보안 검증] 로그인한 유저가 본인이 쓴 일지가 맞는지 확인
        if (!journal.getUser().getEmail().equals(userEmail)){
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_THE_HEALTHJOURNAL);
        }

        // 엔티티의 내부 수정 메서드 호출
        journal.updateHealthJournal(
                request.getConditionStatus(),
                request.getPainScore(),
                request.getContent(),
                request.getSymptoms(),
                request.getSupplements(),
                request.getUpdatedAt()
        );
    }

    // 건강일지 삭제용 메서드
    public void deleteHealthJournal(Long id, String userEmail){
        HealthJournal journal = healthJournalRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_HEALTHJOURNAL));

        // [보안 검증] 작성자 본인만 삭제 가능하도록 체크
        if (!journal.getUser().getEmail().equals(userEmail)){
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_THE_HEALTHJOURNAL);
        }

        healthJournalRepository.delete(journal);
    }
}
