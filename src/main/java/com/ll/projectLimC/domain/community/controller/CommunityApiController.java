package com.ll.projectLimC.domain.community.controller;

import com.ll.projectLimC.domain.community.dto.Community.Response.ArticleViewResponse;
import com.ll.projectLimC.domain.community.dto.Community.Request.CommunityArticleCreateForm;
import com.ll.projectLimC.domain.community.dto.Community.Response.CommunityArticleResponse;
import com.ll.projectLimC.domain.community.dto.Community.Request.UpdateCommunityArticleRequest;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "커뮤니티 게시판 API", description = "커뮤니티 게시판 게시글 CRUD 및 조회 컨트롤러")
@RestController
@RequiredArgsConstructor
// @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CommunityApiController {

    private final CommunityService communityService;

    // 1. 게시글 작성
    @Operation(summary = "커뮤니티 게시글 작성",
            description = "로그인한 사용자가 본문에 내용을 입력하여 새 게시글을 작성합니다.")
    @PostMapping("/community/articles")
    public ResponseEntity<CommunityArticle> addCommunityArticle(
            @RequestBody CommunityArticleCreateForm request,
            Principal principal
    ) {

        CommunityArticle savedCommunityArticle = communityService.save(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCommunityArticle);
    }

    // 2. 전체 게시글 조회 (기존 ViewController의 중복된 전체 조회를 데이터 전용으로 단일화)
    @Operation(summary = "전체 게시글 조회",
            description = "시스템에 등록된 모든 커뮤니티 게시글 목록을 JSON 데이터로 가져옵니다.")
    @GetMapping("/community/articles")
    public ResponseEntity<List<CommunityArticleResponse>> findAllCommunityArticles(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        List<CommunityArticleResponse> communityArticles = communityService.findAll(pageable)
                .stream()
                .map(CommunityArticleResponse::new)
                .toList();
        return ResponseEntity.ok().body(communityArticles);
    }

    // 3. 게시글 상세 조회 (기존 ViewController의 단건 조회를 데이터 반환용 API로 변환)
    @Operation(summary = "게시글 상세 조회",
            description = "게시글 고유 ID(id)를 통해 해당 글의 상세 내용을 조회합니다.")
    @GetMapping("/community/articles/{id}")
    public ResponseEntity<ArticleViewResponse> getCommunityArticle(@PathVariable Long id) {
        CommunityArticle article = communityService.findById(id);
        return ResponseEntity.ok().body(new ArticleViewResponse(article));
    }

    // 4. 게시글 수정 데이터 조회 (기존 신규/수정 폼 조회를 프론트엔드가 데이터만 바인딩하도록 API화)
//    @Operation(summary = "수정용 게시글 데이터 조회",
//            description = "글 수정 페이지 진입 시 기존에 작성된 제목과 본문 데이터를 불러옵니다.")
//    @GetMapping("/community/articles/edit/{id}")
//    public ResponseEntity<ArticleViewResponse> getArticleForEdit(@PathVariable Long id) {
//        CommunityArticle article = communityService.findById(id);
//        return ResponseEntity.ok().body(new ArticleViewResponse(article));
//    }

    // 5. 게시글 수정 완료 처리
    @Operation(summary = "게시글 수정",
            description = "게시글 고유 ID(id)와 수정할 본문 데이터를 받아 글을 갱신합니다.")
    @PutMapping("/community/articles/{id}")
    public ResponseEntity<CommunityArticle> updateCommunityArticle(
            @PathVariable long id,
            @RequestBody UpdateCommunityArticleRequest request
    ) {
        CommunityArticle updatedCommunityArticle = communityService.updateCommunityArticle(id, request);
        return ResponseEntity.ok().body(updatedCommunityArticle);
    }

    // 6. 게시글 삭제
    @Operation(summary = "게시글 삭제",
            description = "게시글 고유 ID(id)를 경로에 받아 해당 글을 삭제합니다.")
    @DeleteMapping("/community/articles/{id}")
    public ResponseEntity<Void> deleteCommunityArticle(@PathVariable long id) {
        communityService.deleteCommunityArticle(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내가 작성한 임시 저장 글 목록 조회",
            description = "현재 로그인한 유저가 임시 저장(DRAFT)해 둔 글 목록만 모아서 가져옵니다.")
    @GetMapping("/community/articles/drafts")
    public ResponseEntity<List<CommunityArticleResponse>> findMyDraftArticles(Principal principal){
        List<CommunityArticleResponse> drafts = communityService.findMyDrafts(principal.getName())
                .stream()
                .map(CommunityArticleResponse::new)
                .toList();

        return ResponseEntity.ok().body(drafts);
    }
}
    // 7. 인기 게시글 조회
//    @Operation(summary = "인기 게시글 조회",
//            description = "좋아요 수가 많은 상위 5개의 커뮤니티 게시글 목록을 가져옵니다.")
//    @GetMapping("/community/articles/popular")
//    public ResponseEntity<List<CommunityArticleResponse>> getPopularCommunityArticle() {
//        List<CommunityArticleResponse> getpopularCommunityArticles =
//                communityService.getPopularCommunityArticles()
//                        .stream()
//                        .map(CommunityArticleResponse::new)
//                        .toList();
//        return ResponseEntity.ok().body(getpopularCommunityArticles);
//    }

