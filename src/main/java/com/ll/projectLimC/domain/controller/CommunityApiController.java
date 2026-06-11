package com.ll.projectLimC.domain.controller;

import com.ll.projectLimC.domain.dto.CommunityArticleCreateForm;
import com.ll.projectLimC.domain.dto.CommunityArticleResponse;
import com.ll.projectLimC.domain.dto.UpdateCommunityArticleRequest;
import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.service.CommunityService;
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

    // 커뮤니티 게시글 작성 컨트롤러
    // HTTP 메서드가 POST일 때 전달받은 Uri와 동일하면 메서드로 매핑
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
    @GetMapping("/api/community/articles")
    public ResponseEntity<List<CommunityArticleResponse>> findAllCommunityArticles(){
        List<CommunityArticleResponse> communityArticles = communityService.findAll()
                .stream()
                .map(CommunityArticleResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(communityArticles);
    }

    // 커뮤니티 게시글 삭제 컨트롤러
    @DeleteMapping("/api/community/articles/{id}")
    public ResponseEntity<Void> deleteCommunityArticle(@PathVariable long id){
        communityService.deleteCommunityArticle(id);

        return ResponseEntity.ok()
                .build();
    }

    // 커뮤니티 게시글 수정 컨트롤러
    @PutMapping("/api/community/articles/{id}")
    public ResponseEntity<CommunityArticle> updateCommunityArticle(@PathVariable long id,
                                                                   @RequestBody UpdateCommunityArticleRequest request){
        CommunityArticle updatedCommunityArticle = communityService.update(id, request);

        return ResponseEntity.ok()
                .body(updatedCommunityArticle);
    }
}
