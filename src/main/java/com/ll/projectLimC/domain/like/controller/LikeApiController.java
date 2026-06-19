package com.ll.projectLimC.domain.like.controller;

import com.ll.projectLimC.domain.like.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LikeApiController {
    private final LikeService likeService;

    @Operation(summary = "게시글 좋아요 토글",
            description = "좋아요를 처음 눌렀다면 좋아요 표시가 되고, 2번째 누르는 것이라면 좋아요가 취소됩니다.")
    @PostMapping("/api/community/articles/{Id}/likes")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long id,
            Principal principal
    ){
        // 토글 실행 결과 받기 (true: 등록, false: 취소)
        boolean isLiked = likeService.toggleLike(id, principal.getName());

        return ResponseEntity.ok().body(Map.of(
                "articleId", id,
                "isLiked", isLiked,
                "message", isLiked ? "좋아요 등록 완료" : "좋아요 취소 완료"
        ));
    }
}
