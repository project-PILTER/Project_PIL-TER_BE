package com.ll.projectLimC.domain.like.service;

import com.ll.projectLimC.domain.comment.entity.Comment;
import com.ll.projectLimC.domain.comment.repository.CommentRepository;
import com.ll.projectLimC.domain.community.repository.CommunityRepository.CommunityRepository;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.like.dto.LikeCommentResponse;
import com.ll.projectLimC.domain.like.dto.LikeResponse;
import com.ll.projectLimC.domain.like.entity.CommentLike;
import com.ll.projectLimC.domain.like.entity.Like;
import com.ll.projectLimC.domain.like.repository.CommentLikeRepository;
import com.ll.projectLimC.domain.like.repository.LikeRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public LikeResponse toggleCommunityArticleLike(Long id, String userName) {
        CommunityArticle communityArticle = communityRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_ARTICLE));

        // 이미 좋아요를 누른 상태인지 확인
        Optional<Like> alreadyLike = likeRepository.findByCommunityArticleAndAuthor(communityArticle, userName);

        boolean isLiked;

        if (alreadyLike.isPresent()){
            // 이미 존재하면 좋아요 취소 (삭제)
            likeRepository.delete(alreadyLike.get());
            isLiked = false;
        }else {
            // 존재하지 않으면 좋아요 등록 (저장)
            Like like = Like.builder()
                    .author(userName)
                    .communityArticle(communityArticle)
                    .build();
            likeRepository.save(like);
            isLiked = true; // 등록됨을 의미.
        }

        long totalLikes = likeRepository.countByCommunityArticle(communityArticle);

        return new LikeResponse(isLiked, totalLikes);
    }

    public LikeCommentResponse toggleCommentLikeLike(Long id, String userName) {
        // 1. 댓글 존재 여부 확인 (예외 처리는 프로젝트에 맞는 ErrorCode 사용)
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_ARTICLE)); // 또는 적절한 댓글 에러코드로 변경

        // 2. 이미 좋아요를 눌렀는지 확인
        Optional<CommentLike> alreadyLike = commentLikeRepository.findByCommentAndAuthor(comment, userName);

        boolean isLiked;

        if (alreadyLike.isPresent()) {
            // 이미 존재하면 좋아요 취소 (삭제)
            commentLikeRepository.delete(alreadyLike.get());
            isLiked = false;
        } else {
            // 존재하지 않으면 좋아요 등록 (저장)
            CommentLike commentLike = CommentLike.builder()
                    .author(userName)
                    .comment(comment)
                    .build();
            commentLikeRepository.save(commentLike);
            isLiked = true;
        }

        // 3. 해당 댓글의 총 좋아요 개수 카운트
        long totalLikes = commentLikeRepository.countByComment(comment);

        return new LikeCommentResponse(isLiked, totalLikes);


    }
}
