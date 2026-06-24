//package com.ll.projectLimC.domain.community.controller;
//
//import com.ll.projectLimC.domain.community.dto.ArticleViewResponse;
//import com.ll.projectLimC.domain.community.dto.CommunityArticleListViewResponse;
//import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
//import com.ll.projectLimC.domain.community.service.CommunityService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@Tag(name = "커뮤니티 API", description = "커뮤니티 게시판 게시글 조회 컨트롤러")
//@RestController
//@RequiredArgsConstructor
//public class CommunityViewController {
//    private final CommunityService communityService;
//
//    @Operation(summary = "커뮤니티 게시글 전체 조회",
//            description = "커뮤니티 페이지에 접속하면 커뮤니티에 게시된 전체 게시글을 조회합니다.")
//    @GetMapping("/api/community/articles")
//    public String getArticles(Model model){
//        List<CommunityArticleListViewResponse> articles = communityService.findAll()
//                .stream()
//                .map(CommunityArticleListViewResponse::new)
//                .toList();
//        model.addAttribute("articles", articles);
//
//        return "articleList";
//    }
//
//    @GetMapping("/api/community/articles/{id}")
//    public String getArticle(
//            @PathVariable Long id,
//            Model model
//    ){
//        CommunityArticle article = communityService.findById(id);
//        model.addAttribute("article", new ArticleViewResponse(article));
//
//        return "article";
//    }
//
//    @GetMapping("/api/community/new-article")
//    // id 키를 가진 쿼리 파라미터의 값을 id 변수에 매핑(id는 없을 수도 있음)
//    public String newArticle(
//            @RequestParam(required = false) Long id,
//            Model model
//    ){
//        if (id == null){
//            model.addAttribute("article", new ArticleViewResponse());
//        }else { // id가 없으면 수정
//            CommunityArticle article = communityService.findById(id);
//            model.addAttribute("article", new ArticleViewResponse(article));
//        }
//
//        return "newArticle";
//    }
//}
