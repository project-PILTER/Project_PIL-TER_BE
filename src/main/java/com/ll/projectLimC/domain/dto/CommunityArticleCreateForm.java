package com.ll.projectLimC.domain.dto;

import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 빋는 생성자 추가
@Getter
public class CommunityArticleCreateForm {
    @NotBlank(message = "제목을 입력하세요.") // Spring validation을 의존성 추가하면 사용 가능.
    private String title;
    private String content;


    public CommunityArticle toEntity({
        return CommunityArticle.builder()
                .title(title)
                .content(content)
                .build();
    }
}
