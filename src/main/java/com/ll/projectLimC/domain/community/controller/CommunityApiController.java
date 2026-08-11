package com.ll.projectLimC.domain.community.controller;

import com.ll.projectLimC.domain.community.dto.ArticleDrafts.Request.ArticleDraftsSaveRequest;
import com.ll.projectLimC.domain.community.dto.ArticleDrafts.Response.ArticleDraftsListResponse;
import com.ll.projectLimC.domain.community.dto.Community.Request.CommunityArticleCreateForm;
import com.ll.projectLimC.domain.community.dto.Community.Response.CommunityArticleResponse;
import com.ll.projectLimC.domain.community.dto.Community.Request.UpdateCommunityArticleRequest;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.community.service.ArticleDraftsService.ArticleDraftsService;
import com.ll.projectLimC.domain.community.service.CommunityService.CommunityService;
import com.ll.projectLimC.domain.s3.service.S3Service;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import com.ll.projectLimC.util.S3FolderName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Tag(name = "커뮤니티 게시판 API", description = "커뮤니티 게시판 게시글 CRUD 및 조회 컨트롤러")
@RestController
@RequiredArgsConstructor
// @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CommunityApiController {

    private final CommunityService communityService;
    private final ArticleDraftsService articleDraftsService;
    private final S3Service s3Service;

    // 1. 게시글 작성
    @Operation(summary = "커뮤니티 게시글 작성",
            description = "로그인한 사용자가 본문에 내용을 입력하여 새 게시글을 작성합니다.")
    @PostMapping(value = "/community/articles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommunityArticle> addCommunityArticle(
            @RequestPart(value = "request") CommunityArticleCreateForm request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal
    ) {

        if (principal == null) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        // 1. 이미지가 존재하는 경우 처리
        if (file != null && !file.isEmpty()) {
            // S3에 파일 업로드 후 실제 URL(CloudFront URL) 반환 받기
            String fileUrl = s3Service.uploadFile(file, S3FolderName.COMMUNITY);

            // (선택) 게시글 대표 이미지(Thumbnail 등)가 있다면 설정
            request.setImageUrl(fileUrl);

            // 2. 본문(content) HTML 안의 blob: URL을 실제 S3 URL로 교체
            String content = request.getContent();
            if (content != null && !content.isBlank()) {
                content = communityService.replaceBlobImageUrl(content, fileUrl);
                request.setContent(content); // 치환된 본문으로 덮어쓰기
            }
        }

        // 3. DB에 저장
        CommunityArticle savedCommunityArticle = communityService.save(request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCommunityArticle);
    }

    // 2. 전체 게시글 조회
    @Operation(summary = "전체 게시글 조회",
            description = "시스템에 등록된 모든 커뮤니티 게시글 목록을 JSON 데이터로 가져옵니다.")
    @GetMapping("/community/articles")
    public ResponseEntity<Page<CommunityArticleResponse>> findAllCommunityArticles(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
            // Principal principal
    ) {

        Page<CommunityArticleResponse> articlePage = communityService.findAll(pageable);

        return ResponseEntity.ok().body(articlePage);
    }

    // 3. 게시글 상세 조회 (기존 ViewController의 단건 조회를 데이터 반환용 API로 변환)
    @Operation(summary = "게시글 상세 조회",
            description = "게시글 고유 ID(id)를 통해 해당 글의 상세 내용을 조회합니다.")
    @GetMapping("/community/articles/{id}")
    public ResponseEntity<CommunityArticleResponse> getCommunityArticle(@PathVariable Long id) {
        CommunityArticle article = communityService.findById(id);
        return ResponseEntity.ok().body(new CommunityArticleResponse(article));
    }

    // 5. 게시글 수정 완료 처리
    @Operation(summary = "게시글 수정",
            description = "게시글 고유 ID(id)와 수정할 본문 데이터를 받아 글을 갱신합니다.")
    @PutMapping("/community/articles/{id}")
    public ResponseEntity<CommunityArticle> updateCommunityArticle(
            @PathVariable long id,
            @RequestPart(value = "request") UpdateCommunityArticleRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal // [추가] Principal 주입
    ) {
        // principal null 검증 (로그인 안 한 사용자 방어)
        if (principal == null) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        CommunityArticle updatedCommunityArticle = communityService.updateCommunityArticle(id, request, file, principal.getName());

        return ResponseEntity.ok().body(updatedCommunityArticle);
    }

    // 6. 게시글 삭제
    @Operation(summary = "게시글 삭제",
            description = "게시글 고유 ID(id)를 경로에 받아 해당 글을 삭제합니다.")
    @DeleteMapping("/community/articles/{id}")
    public ResponseEntity<Void> deleteCommunityArticle(
            @PathVariable long id,
            Principal principal // [추가] Principal 주입
    ) {
        // principal null 검증 (로그인 안 한 사용자 방어)
        if (principal == null) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        communityService.deleteCommunityArticle(id, principal.getName());

        return ResponseEntity.ok().build();
    }

    /** ---------------임시저장용 컨트롤러-------------**/
    @Operation(summary = "게시글 임시저장 생성 및 수정", description = "draftId 쿼리 파라미터 존재 여부에 따라 생성/수정 처리합니다.")
    @PostMapping("/community/articles/drafts")
    public ResponseEntity<Long> saveOrUpdateDraft(
            @RequestParam(required = false) Long draftId,
            @RequestBody ArticleDraftsSaveRequest request,
            Principal principal
    ) {
        // principal null 검증 (로그인 안 한 사용자 방어)
        if (principal == null) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        Long savedDraftId = articleDraftsService.saveOrUpdateDraft(draftId, request, principal.getName());
        return ResponseEntity.ok(savedDraftId);
    }

    @Operation(summary = "내 임시저장 글 목록 조회", description = "로그인한 사용자의 임시저장 목록 전체를 가져옵니다.")
    @GetMapping("/community/articles/drafts")
    public ResponseEntity<List<ArticleDraftsListResponse>> getMyDrafts(Principal principal) {
        // principal null 검증 (로그인 안 한 사용자 방어)
        if (principal == null) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        List<ArticleDraftsListResponse> drafts = articleDraftsService.findMyDrafts(principal.getName());
        return ResponseEntity.ok(drafts);
    }

    @Operation(summary = "임시저장 글 단건 조회 (이어쓰기용)", description = "특정 임시저장 글의 내용을 불러옵니다.")
    @GetMapping("/community/articles/drafts/{id}")
    public ResponseEntity<ArticleDraftsListResponse> getDraftDetail(
            @PathVariable Long id,
            Principal principal
    ) {
        // principal null 검증 (로그인 안 한 사용자 방어)
        if (principal == null) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        ArticleDraftsListResponse draft = articleDraftsService.findDraftById(id, principal.getName());
        return ResponseEntity.ok(draft);
    }

    @Operation(summary = "임시저장 글 단건 삭제", description = "특정 임시저장 글을 삭제합니다.")
    @DeleteMapping("/community/articles/drafts/{id}")
    public ResponseEntity<Void> deleteDraft(
            @PathVariable Long id,
            Principal principal
    ) {
        // principal null 검증 (로그인 안 한 사용자 방어)
        if (principal == null) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        articleDraftsService.deleteDraft(id, principal.getName());
        return ResponseEntity.ok().build();
    }
}
//    @Operation(summary = "내가 작성한 임시 저장 글 목록 조회",
//            description = "현재 로그인한 유저가 임시 저장(DRAFT)해 둔 글 목록만 모아서 가져옵니다.")
//    @GetMapping("/community/articles/drafts")
//    public ResponseEntity<List<CommunityArticleResponse>> findMyDraftArticles(Principal principal){
//        List<CommunityArticleResponse> drafts = articleDraftsService.findMyDrafts
//                        (principal.getName())
//                .stream()
//                .map(CommunityArticleResponse::new)
//                .toList();
//
//        return ResponseEntity.ok().body(drafts);
//    }

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
// 4. 게시글 수정 데이터 조회 (기존 신규/수정 폼 조회를 프론트엔드가 데이터만 바인딩하도록 API화)
//    @Operation(summary = "수정용 게시글 데이터 조회",
//            description = "글 수정 페이지 진입 시 기존에 작성된 제목과 본문 데이터를 불러옵니다.")
//    @GetMapping("/community/articles/edit/{id}")
//    public ResponseEntity<ArticleViewResponse> getArticleForEdit(@PathVariable Long id) {
//        CommunityArticle article = communityService.findById(id);
//        return ResponseEntity.ok().body(new ArticleViewResponse(article));
//    }
// private String getAuthenticatedEmail(Principal principal) {
//        if (principal == null) {
//            // 토큰이 없거나 로그인하지 않은 요청일 때 500 에러 대신 Custom 예외를 던짐
//            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_THE_ARTICLE);
//        }
//        return principal.getName(); // 안전하게 email 추출
//    }