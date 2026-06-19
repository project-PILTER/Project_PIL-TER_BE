package com.ll.projectLimC.domain.comment.dto;

import com.ll.projectLimC.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCommentResponse {
    private Long id;
    private String content;
    private CommentAuthorResponse author;


    public AddCommentResponse(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.author = new CommentAuthorResponse(comment.getUser());
    }
}
