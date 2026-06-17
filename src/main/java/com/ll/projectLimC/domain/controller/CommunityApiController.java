package com.ll.projectLimC.domain.controller;

import com.ll.projectLimC.domain.dto.*;
import com.ll.projectLimC.domain.entity.Comment.Comment;
import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.service.CommentService;
import com.ll.projectLimC.domain.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController // HTTP Response Body에 객체 데이터로 JSON 형식으로 반환하는 컨트롤러
@RequiredArgsConstructor
public class CommunityApiController {
    private final CommunityService communityService;
    private final CommentService commentService;

    // 커뮤니티 게시글 작성 컨트롤러
    // HTTP 메서드가 POST일 때 전달받은 Uri와 동일하면 메서드로 매핑
    @Operation(summary = "커뮤니티 게시글 작성",
            description = "로그인한 사용자가 본문에 내용을 입력하여 새 게시글을 작성합니다.")
    @PostMapping("/api/community/articles")
    public ResponseEntity<CommunityArticle> addCommunityArticle(
            @RequestBody CommunityArticleCreateForm request,
            Principal principal
    ){
        // @RequestBody로 요청 본문 값 매핑
        CommunityArticle savedCommunityArticle = communityService.save(request, principal.getName());

        // 요청한 자원이 성공적으로 생성되었고 저장된 블로그 글 정보를 응답 객체에 담아 전송
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCommunityArticle);
    }

    // 커뮤니티 게시글 조회 컨트롤러
    @Operation(summary = "전체 게시글 조회",
            description = "시스템에 등록된 모든 커뮤니티 게시글 목록을 가져옵니다.")
    @GetMapping("/api/community/articles")
    public ResponseEntity<List<CommunityArticleResponse>> findAllCommunityArticles(){
        List<CommunityArticleResponse> communityArticles = communityService.findAll()
                .stream()
                .map(CommunityArticleResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(communityArticles);
    }

    @Operation(summary = "게시글 삭제",
            description = "게시글 고유 ID(id)를 경로에 받아 해당 글을 삭제합니다.")
    // 커뮤니티 게시글 삭제 컨트롤러
    @DeleteMapping("/api/community/articles/{id}")
    public ResponseEntity<Void> deleteCommunityArticle(@PathVariable long id){
        communityService.deleteCommunityArticle(id);

        return ResponseEntity.ok()
                .build();
    }

    // 커뮤니티 게시글 수정 컨트롤러
    @Operation(summary = "게시글 수정",
            description = "게시글 고유 ID(id)와 수정할 본문 데이터를 받아 글을 갱신합니다.")
    @PutMapping("/api/community/articles/{id}")
    public ResponseEntity<CommunityArticle> updateCommunityArticle(@PathVariable long id,
                                                                   @RequestBody UpdateCommunityArticleRequest request){
        CommunityArticle updatedCommunityArticle = communityService.updateCommunityArticle(id, request);

        return ResponseEntity.ok()
                .body(updatedCommunityArticle);
    }
}
