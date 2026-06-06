package com.ll.projectLimC.controller;

import com.ll.projectLimC.domain.community.dto.CommunityArticleCreateForm;
import com.ll.projectLimC.domain.community.dto.UpdateCommunityArticleRequest;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.community.repository.CommunityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        final String url = "/api/community/articles";
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

    @DisplayName("findAllCommunityArticles: 커뮤니티 게시글 목록 조회에 성공한다.")
    @Test
    public void findAllCommunityArticles() throws Exception{
        // given
        final String url = "/api/community/articles";
        final String title = "title";
        final String content = "content";

        communityRepository.save(CommunityArticle.builder()
                .title(title)
                .content(content)
                .build());

        // when
        final ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(content))
                .andExpect(jsonPath("$[0].title").value(title));
    }

    @DisplayName("deleteCommunityArticle: 커뮤니티 게시글 삭제에 성공한다.")
    @Test
    public void deleteCommunityArticle() throws Exception{
        // given
        final String url = "/api/community/articles/{id}";
        final String title = "title";
        final String content = "content";

        CommunityArticle savedCommunityArticle = communityRepository.save(CommunityArticle.builder()
                .title(title)
                .content(content)
                .build());

        // when
        mockMvc.perform(delete(url,savedCommunityArticle.getId()))
                .andExpect(status().isOk());

        // then
        List<CommunityArticle> communityArticles = communityRepository.findAll();

        assertThat(communityArticles).isEmpty();
    }

    @DisplayName("updateCommunityArticle: 커뮤니티 게시글 수정에 성공한다.")
    @Test
    public void updateCommunityArticle() throws Exception{
        // given
        final String url = "/api/community/articles/{id}";
        final String title = "title";
        final String content = "content";

        CommunityArticle savedCommunityArticle = communityRepository.save(CommunityArticle.builder()
                .title(title)
                .content(content)
                .build());

        final String newTitle = "title";
        final String newContent = "content";

        UpdateCommunityArticleRequest request = new UpdateCommunityArticleRequest(newTitle, newContent);

        // when
        ResultActions resultActions = mockMvc.perform(put(url, savedCommunityArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions.andExpect(status().isOk());

        CommunityArticle communityArticle = communityRepository.findById(savedCommunityArticle
                .getId())
                .get();

        assertThat(communityArticle.getTitle()).isEqualTo(newTitle);
        assertThat(communityArticle.getContent()).isEqualTo(newContent);
    }
}