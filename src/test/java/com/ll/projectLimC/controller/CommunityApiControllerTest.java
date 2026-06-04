package com.ll.projectLimC.controller;

import com.ll.projectLimC.dto.CommunityArticleCreateForm;
import com.ll.projectLimC.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.repository.CommunityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest // 테스트용 애플리케이션 컨텍스트
@AutoConfigureMockMvc // MockMvc 생성 및 자동 구성
class CommunityApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    CommunityRepository communityRepository;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        communityRepository.deleteAll();
        }

    @DisplayName("addCommunityArticle: 커뮤니티에 글을 추가하는데 성공한다.")
    @Test
    public void addCommunityArticle() throws Exception{
        //given
        final String url = "/api/commnuity/articles";
        final String title = "title";
        final String content = "content";
        final CommunityArticleCreateForm userRequest = new CommunityArticleCreateForm(title, content);

        //객체 JSON으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        //when
        //설정한 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));
        //then
        result.andExpect(status().isCreated());

        List<CommunityArticle> communityArticles = communityRepository.findAll();

        assertThat(communityArticles.size()).isEqualTo(1);
        assertThat(communityArticles.get(0).getTitle()).isEqualTo(title);
        assertThat(communityArticles.get(0).getContent()).isEqualTo(content);
    }

}