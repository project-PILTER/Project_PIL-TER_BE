package com.ll.projectLimC.domain.controller;

import com.ll.projectLimC.domain.dto.AddCommentRequest;
import com.ll.projectLimC.domain.dto.AddCommentResponse;
import com.ll.projectLimC.domain.dto.UpdateCommentRequest;
import com.ll.projectLimC.domain.entity.Comment.Comment;
import com.ll.projectLimC.domain.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class CommentApiController {
    private final CommentService commentService;

    // 커뮤니티 게시글의 댓글 생성용 컨트롤러
    @Operation(summary = "댓글 작성",
            description = "특정 게시글에 로그인한 사용자가 댓글을 작성합니다.")
    @PostMapping("/api/community/comments")
    public ResponseEntity<AddCommentResponse> addComment(
            @RequestBody AddCommentRequest request,
            Principal principal
    ){
        Comment savedComment = commentService.addComment(request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AddCommentResponse(savedComment));
    }

    // 커뮤니티 게시글의 댓글 수정용 컨트롤러
    @Operation(summary = "댓글 수정",
            description = "로그인한 사용자가 특정 게시글에 작성한 댓글을 수정합니다.")
    @PutMapping("/api/community/comment/{id}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long id,
            @RequestBody UpdateCommentRequest request
            ){
        Comment updatedComment = commentService.updateComment(id, request);
        return ResponseEntity.ok().body(updatedComment);
    }

    @Operation(summary = "댓글 삭제",
            description = "로그인한 사용자가 특정 게시글에 작성한 댓글을 삭제합니다.")
    @DeleteMapping("/api/community/comment/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id
    ){
        commentService.deleteComment(id);

        return ResponseEntity.ok()
                .build();
    }
}