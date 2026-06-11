package com.ll.projectLimC.domain.controller;

import com.ll.projectLimC.domain.dto.ArticleViewResponse;
import com.ll.projectLimC.domain.dto.CommunityArticleListViewResponse;
import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommunityViewController {
    private final CommunityService communityService;

    @GetMapping("/articles")
    public String getArticles(Model model){
        List<CommunityArticleListViewResponse> articles = communityService.findAll()
                .stream()
                .map(CommunityArticleListViewResponse::new)
                .toList();
        model.addAttribute("articles", articles);

        return "articleList";
    }

    @GetMapping("/articles/{id}")
    public String getArticle(
            @PathVariable Long id,
            Model model
    ){
        CommunityArticle article = communityService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }

    @GetMapping("/new-article")
    // id 키를 가진 쿼리 파라미터의 값을 id 변수에 매핑(id는 없을 수도 있음)
    public String newArticle(
            @RequestParam(required = false) Long id,
            Model model
    ){
        if (id == null){
            model.addAttribute("article", new ArticleViewResponse());
        }else { // id가 없으면 수정
            CommunityArticle article = communityService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }

        return "newArticle";
    }
}
