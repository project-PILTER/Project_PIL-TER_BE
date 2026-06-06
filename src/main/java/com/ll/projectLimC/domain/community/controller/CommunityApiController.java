package com.ll.projectLimC.domain.community.controller;

import com.ll.projectLimC.domain.community.dto.CommunityArticleCreateForm;
import com.ll.projectLimC.domain.community.dto.CommunityArticleResponse;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // HTTP Response Body에 객체 데이터로 JSON 형식으로 반환하는 컨트롤러
@RequiredArgsConstructor
public class CommunityApiController {
    private final CommunityService communityService;

    // 커뮤니티 게시글 작성
    // HTTP 메서드가 POST일 때 전달받은 Uri와 동일하면 메서드로 매핑
    @PostMapping("/api/community/articles")
    public ResponseEntity<CommunityArticle> addCommunityArticle(@RequestBody CommunityArticleCreateForm request){
        // @RequestBody로 요청 본문 값 매핑
        CommunityArticle savedCommunityArticle = communityService.save(request);

        // 요청한 자원이 성공적으로 생성되었고 저장된 블로그 글 정보를 응답 객체에 담아 전송
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCommunityArticle);
    }

    // 커뮤니티 게시글 조회
    @GetMapping("/api/community/articles")
    public ResponseEntity<List<CommunityArticleResponse>> findAllCommunityArticles(){
        List<CommunityArticleResponse> communityArticles = communityService.findAllCommunityArticle()
                .stream()
                .map(CommunityArticleResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(communityArticles);
    }

    // 커뮤니티 게시글 삭제
    @DeleteMapping("/api/community/articles/{id}")
    public ResponseEntity<Void> deleteCommunityArticle(@PathVariable long id){
        communityService.deleteCommunityArticle(id);

        return ResponseEntity.ok()
                .build();
    }
}
