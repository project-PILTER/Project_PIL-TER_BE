package com.ll.projectLimC.domain.comment.service;

import com.ll.projectLimC.domain.comment.dto.UpdateCommentRequest;
import com.ll.projectLimC.domain.comment.dto.UpdateCommentResponse;
import com.ll.projectLimC.domain.comment.dto.AddCommentRequest;
import com.ll.projectLimC.domain.comment.entity.Comment;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.comment.repository.CommentRepository;
import com.ll.projectLimC.domain.community.repository.CommunityRepository.CommunityRepository;
import com.ll.projectLimC.domain.user.repository.UserRepository;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public Comment addComment(AddCommentRequest request, String userEmail){
        // request.getArticleId() 등이 null인지 먼저 체크
        if (request.getCommunityArticleId() == null) {
            throw new GlobalCustomException(ErrorCode.NOT_FOUND_THE_ARTICLE);
        }

        CommunityArticle communityArticle = communityRepository.findById(request.getCommunityArticleId())
                .orElseThrow(()-> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_ARTICLE_ID));

        // 로그인한 유저의 이메일(또는 닉네임)로 실제 DB에 있는 User 엔티티를 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        // 부모 댓글이 있는 경우(대댓글) 조회 및 검증
        Comment parentComment = null;
        if (request.getParentId() != null) {
            parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_COMMENT));

            // 대댓글의 대댓글을 방지하고 싶다면 부모가 이미 parent를 가지고 있는지 체크할 수도 있습니다.
            if (parentComment.getParent() != null) {
                // 🎯 부모 댓글이 이미 최상위 댓글의 자식(즉, 대댓글)이라면, 그 아래에는 더 달 수 없도록 예외 발생
                throw new GlobalCustomException(ErrorCode.CANNOT_NEST_DEEP_COMMENT);
            }
        }

        // 🎯 2. 댓글 저장 시 parent 전달
        Comment comment = Comment.builder()
                .communityArticle(communityArticle)
                .user(user)
                .content(request.getContent())
                .parent(parentComment)
                .likeCount(0L)
                .build();

        // 빌더에 조인한 user 객체를 넘겨서 저장
        return commentRepository.save(comment);
    }

    // 댓글 수정용 메서드
    @Transactional
    public UpdateCommentResponse updateComment(Long id, UpdateCommentRequest request, String userName){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_COMMENT));

        // 작성자 검증 (보안 구멍 원천 차단)
        authorizeCommentAuthor(comment, userName);

        // 엔티티 상태 변경 (더티 체킹으로 자동 update 쿼리 유발)
        comment.updateComment(request.getContent(), request.getUpdatedAt());

        // DTO로 감싸서 반환
        return new UpdateCommentResponse(comment);
    }

    // 댓글 삭제용 메서드
    @Transactional
    public void deleteComment(Long id, String userName){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_COMMENT));

        authorizeCommentAuthor(comment, userName);

        commentRepository.delete(comment);
    }

    // 댓글 작성자 검증 공통 메서드
    private void authorizeCommentAuthor(Comment comment, String userName) {
        if (!comment.getUser().getEmail().equals(userName)) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIED_THE_COMMENT);
        }
    }
}