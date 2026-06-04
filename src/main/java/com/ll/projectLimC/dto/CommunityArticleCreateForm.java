package com.ll.projectLimC.dto;

import com.ll.projectLimC.entity.CommunityArticle.CommunityArticle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 빋는 생성자 추가
@Getter
public class CommunityArticleCreateForm {
    private String title;
    private String content;

    public CommunityArticle toEntity(){
        return CommunityArticle.builder()
                .title(title)
                .content(content)
                .build();
    }
}
