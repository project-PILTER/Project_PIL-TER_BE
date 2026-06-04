package com.ll.projectLimC.controller;

import com.ll.projectLimC.dto.AddCommunityArticleRequest;
import com.ll.projectLimC.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController // HTTP Response Body에 객체 데이터로 JSON 형식으로 반환하는 컨트롤러
@RequiredArgsConstructor
public class CommunityApiController {
    private final CommunityService communityService;

    // HTTP 메서드가 POST일 때 전달받은 Uri와 동일하면 메서드로 매핑
    @PostMapping("/api/commnuity/articles")
    public ResponseEntity<CommunityArticle> addCommunityArticle(@RequestBody AddCommunityArticleRequest request){
        // @RequestBody로 요청 본문 값 매핑
        CommunityArticle savedCommunityArticle = communityService.save(request);

        // 요청한 자원이 성공적으로 생성되었고 저장된 블로그 글 정보를 응답 객체에 담아 전송
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCommunityArticle);
    }
}
