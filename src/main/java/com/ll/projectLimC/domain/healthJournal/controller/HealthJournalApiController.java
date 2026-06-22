package com.ll.projectLimC.domain.healthJournal.controller;

import com.ll.projectLimC.domain.User.entity.User;
import com.ll.projectLimC.domain.healthJournal.dto.HealthJournalRequest;
import com.ll.projectLimC.domain.healthJournal.dto.HealthJournalResponse;
import com.ll.projectLimC.domain.healthJournal.dto.UpdateHealthJournalRequest;
import com.ll.projectLimC.domain.healthJournal.entity.HealthJournal;
import com.ll.projectLimC.domain.healthJournal.repository.HealthJournalRepository;
import com.ll.projectLimC.domain.healthJournal.service.HealthJournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthJournalApiController {
    private final HealthJournalService healthJournalService;
    private final HealthJournalRepository healthJournalRepository;

    // 내 건강 일지 목록 전체 조회 API
    @GetMapping("/api/journals")
    public ResponseEntity<List<HealthJournalResponse>> getAllJournals(
            Principal principal
    ){
        List<HealthJournalResponse> journals = healthJournalService.findAllByUser(principal.getName());

        return ResponseEntity.ok().body(journals);
    }

    // 특정 건강 일지 상세 단건 조회 API
    @GetMapping("/api/journals/{id}")
    public ResponseEntity<HealthJournalResponse> getHealthJournalById(
            @PathVariable Long id,
            Principal principal
    ){
        HealthJournalResponse journal = healthJournalService.findById(id, principal.getName());

        return ResponseEntity.ok().body(journal);
    }

    // 건강일지 생성 API
    @PostMapping("/api/journals")
    public ResponseEntity<Map<String, Object>> addHealthJournal(
            @RequestBody HealthJournalRequest request,
            Principal principal
            ){
        // 인증객체에서 이메일을 추출하여 서비스단으로 넘김
        Long journalId = healthJournalService.saveHealthJournal(request, principal.getName());

        return ResponseEntity.ok().body(
                Map.of(
                        "success", true,
                        "journalId", journalId,
                        "message", "건강 일지가 성공적으로 기록되었습니다."
                ));
    }

    // 건강일지 수정 API
    @PutMapping("/api/journals/{id}")
    public ResponseEntity<Map<String, Object>> updateHealthJournal(
            @PathVariable Long id,
            @RequestBody UpdateHealthJournalRequest request,
            Principal principal
            ){
        healthJournalService.updateHealthJournal(id, request, principal.getName());

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "건강일지가 성공적으로 수정되었습니다.")
        );
    }

    // 건강일지 삭제용 API
    @DeleteMapping("/api/journals/{id}")
    public ResponseEntity<Map<String, Object>> deleteHealthJournal(
            @PathVariable Long id,
            Principal principal
    ){
        healthJournalService.deleteHealthJournal(id, principal.getName());

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "건강 일지가 성공적으로 삭제되었습니다."
        ));
    }
}
