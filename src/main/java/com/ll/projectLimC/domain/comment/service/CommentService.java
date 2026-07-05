package com.ll.projectLimC.domain.comment.service;

import com.ll.projectLimC.domain.comment.dto.UpdateCommentRequest;
import com.ll.projectLimC.domain.comment.dto.UpdateCommentResponse;
import com.ll.projectLimC.domain.comment.dto.AddCommentRequest;
import com.ll.projectLimC.domain.comment.entity.Comment;
import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.comment.repository.CommentRepository;
import com.ll.projectLimC.domain.community.repository.CommunityRepository;
import com.ll.projectLimC.domain.user.repository.UserRepository;
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
            throw new IllegalArgumentException("게시글 ID는 필수입니다.");
        }

        CommunityArticle communityArticle = communityRepository.findById(request.getCommunityArticleId())
                .orElseThrow(()-> new IllegalArgumentException("not found : " + request.getCommunityArticleId()));

        // 로그인한 유저의 이메일(또는 닉네임)로 실제 DB에 있는 User 엔티티를 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("not found user"));

        // 빌더에 조인한 user 객체를 넘겨서 저장
        return commentRepository.save(request.toEntity(user, communityArticle));
    }

    // 댓글 수정용 메서드
    @Transactional
    public UpdateCommentResponse updateComment(Long id, UpdateCommentRequest request, String userName){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found comment : " + id));

        // 작성자 검증 (보안 구멍 원천 차단)
        authorizeCommentAuthor(comment, userName);

        // 엔티티 상태 변경 (더티 체킹으로 자동 update 쿼리 유발)
        comment.updateComment(request.getContent());

        // DTO로 감싸서 반환
        return new UpdateCommentResponse(comment);
    }

    // 댓글 삭제용 메서드
    @Transactional
    public void deleteComment(Long id, String userName){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found comment : " + id));

        authorizeCommentAuthor(comment, userName);

        commentRepository.delete(comment);
    }

    // 댓글 작성자 검증 공통 메서드
    private void authorizeCommentAuthor(Comment comment, String userName) {
        if (!comment.getUser().getEmail().equals(userName)) {
            throw new IllegalArgumentException("댓글 권한이 없습니다.");
        }
    }
}
