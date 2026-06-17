package com.ll.projectLimC.domain.service;

import com.ll.projectLimC.domain.dto.AddCommentRequest;
import com.ll.projectLimC.domain.dto.UpdateCommentRequest;
import com.ll.projectLimC.domain.entity.Comment.Comment;
import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.repository.CommentRepository;
import com.ll.projectLimC.domain.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;

    public Comment addComment(AddCommentRequest request,
                              String userName){
        CommunityArticle communityArticle = communityRepository.findById(request.getCommunityArticleId())
                .orElseThrow(()-> new IllegalArgumentException("not found : " + request.getCommunityArticleId()));

        return commentRepository.save(request.toEntity(userName, communityArticle));
    }

    // 댓글 수정용 메서드
    @Transactional
    public Comment updateComment(Long id, UpdateCommentRequest request, String userName){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found comment : " + id));

        // 작성자 검증
        authorizeCommentAuthor(comment, userName);

        comment.updateComment(request.getContent());
        return comment;
    }

    // 댓글 삭제용 메서드
    @Transactional
    public void deleteComment(Long id, String userName){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found comment : " + id));

        // 작성자 검증
        authorizeCommentAuthor(comment, userName);

        commentRepository.delete(comment);
    }

    // 댓글 작성자 검증 공통 메서드
    private void authorizeCommentAuthor(Comment comment, String userName) {
        if (!comment.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized to this comment");
        }
    }
}
