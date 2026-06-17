package com.ll.projectLimC.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter // 필수 추가-스프링이 JSON 데이터를 바인딩하고 값을 꺼내 쓰기 위해 반드시 필요
public class UpdateCommentRequest {
    private String content;
}
