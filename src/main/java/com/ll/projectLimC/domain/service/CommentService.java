package com.ll.projectLimC.domain.service;

import com.ll.projectLimC.domain.dto.AddCommentRequest;
import com.ll.projectLimC.domain.dto.UpdateCommentRequest;
import com.ll.projectLimC.domain.dto.UpdateCommentResponse;
import com.ll.projectLimC.domain.entity.Comment.Comment;
import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import com.ll.projectLimC.domain.entity.User.User;
import com.ll.projectLimC.domain.repository.CommentRepository;
import com.ll.projectLimC.domain.repository.CommunityRepository;
import com.ll.projectLimC.domain.repository.UserRepository;
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
        CommunityArticle communityArticle = communityRepository.findById(request.getCommunityArticleId())
                .orElseThrow(()-> new IllegalArgumentException("not found : " + request.getCommunityArticleId()));

        // 로그인한 유저의 이메일(또는 닉네임)로 실제 DB에 있는 User 엔티티를 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("not found user"));

        // 빌더에 조인한 user 객체를 넘겨서 저장
        Comment comment = Comment.builder()
                .communityArticle(communityArticle)
                .user(user) // 👈 기존 userName 문자열 대신 유저 객체 매핑!
                .content(request.getContent())
                .build();

        return commentRepository.save(comment);
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
        if (!comment.getUser().equals(userName)) {
            throw new IllegalArgumentException("not authorized to this comment");
        }
    }
}
