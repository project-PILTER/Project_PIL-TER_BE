package com.ll.projectLimC.domain.healthJournal.controller;

import com.ll.projectLimC.domain.User.entity.User;
import com.ll.projectLimC.domain.healthJournal.dto.HealthJournalRequest;
import com.ll.projectLimC.domain.healthJournal.entity.HealthJournal;
import com.ll.projectLimC.domain.healthJournal.service.HealthJournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthJournalApiController {
    private final HealthJournalService healthJournalService;

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
}
