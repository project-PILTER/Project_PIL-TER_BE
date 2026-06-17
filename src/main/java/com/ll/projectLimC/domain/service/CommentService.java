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

    public Comment addComment(AddCommentRequest request, String userName){
        CommunityArticle communityArticle = communityRepository.findById(request.getCommunityArticleId())
                .orElseThrow(()-> new IllegalArgumentException("not found : " + request.getCommunityArticleId()));

        return commentRepository.save(request.toEntity(userName, communityArticle));
    }

    // 댓글 수정용 메서드
    @Transactional
    public Comment updateComment(Long id, UpdateCommentRequest request){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found comment : " + id));

        comment.updateComment(request.getContent());
        return comment;
    }

//    public void deleteComment(){
//
//  }
}
