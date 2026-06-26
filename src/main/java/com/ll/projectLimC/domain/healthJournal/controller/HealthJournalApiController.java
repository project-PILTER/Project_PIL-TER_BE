package com.ll.projectLimC.domain.healthJournal.controller;

import com.ll.projectLimC.domain.User.entity.User;
import com.ll.projectLimC.domain.healthJournal.dto.HealthJournalRequest;
import com.ll.projectLimC.domain.healthJournal.dto.HealthJournalResponse;
import com.ll.projectLimC.domain.healthJournal.dto.UpdateHealthJournalRequest;
import com.ll.projectLimC.domain.healthJournal.entity.HealthJournal;
import com.ll.projectLimC.domain.healthJournal.repository.HealthJournalRepository;
import com.ll.projectLimC.domain.healthJournal.service.HealthJournalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Tag(name = "건강일지 API", description = "건강일지 CRUD 및 조회 컨트롤러")
@RestController
@RequiredArgsConstructor
public class HealthJournalApiController {
    private final HealthJournalService healthJournalService;
    private final HealthJournalRepository healthJournalRepository;

    // 내 건강 일지 목록 전체 조회 API
    @Operation(summary = "건강일지 목록 전체 조회",
            description = "로그인한 사용자가 건강일지 페이지를 클릭하여 작성한 모든 건강일지 목록을 조회합니다.")
    @GetMapping("/journals")
    public ResponseEntity<List<HealthJournalResponse>> getAllJournals(
            Principal principal,
            @PageableDefault(size = 10, sort = "journalDate", direction = Sort.Direction.DESC) Pageable pageable
    ){
        List<HealthJournalResponse> journals = healthJournalService.findAllByUser(principal.getName(), pageable);

        return ResponseEntity.ok().body(journals);
    }

    // 특정 건강 일지 상세 단건 조회 API
    @Operation(summary = "건강일지 단건 조회",
            description = "로그인한 사용자가 특정 건강일지를 선택하여 해당 건강일지를 조회합니다.")
    @GetMapping("/journals/{id}")
    public ResponseEntity<HealthJournalResponse> getHealthJournalById(
            @PathVariable Long id,
            Principal principal
    ){
        HealthJournalResponse journal = healthJournalService.findById(id, principal.getName());

        return ResponseEntity.ok().body(journal);
    }

    // 건강일지 생성 API
    @Operation(summary = "건강일지 생성",
            description = "로그인한 사용자가 새로운 건강일지를 생성합니다.")
    @PostMapping("/journals")
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
    @Operation(summary = "건강일지 수정",
            description = "로그인한 사용자가 기존에 작성한 건강일지를 수정합니다.")
    @PutMapping("/journals/{id}")
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
    @Operation(summary = "건강일지 삭제",
            description = "로그인한 사용자가 작성한 건강일지를 삭제합니다.")
    @DeleteMapping("/journals/{id}")
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
