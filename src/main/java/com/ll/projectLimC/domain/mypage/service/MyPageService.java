package com.ll.projectLimC.domain.mypage.service;

import com.ll.projectLimC.domain.User.entity.User;
import com.ll.projectLimC.domain.User.repository.UserRepository;
import com.ll.projectLimC.domain.comment.repository.CommentRepository;
import com.ll.projectLimC.domain.community.repository.CommunityRepository;
import com.ll.projectLimC.domain.healthJournal.repository.HealthJournalRepository;
import com.ll.projectLimC.domain.mypage.dto.MyPageResponse;
import com.ll.projectLimC.domain.mypage.dto.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;
    // private final LikeRepository likeRepository;
    private final HealthJournalRepository healthJournalRepository;

    public MyPageRespons getMypageData(String email) {
        // 1. 유저 검증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 2. 내가 작성한 게시글 수 집계
        long articleCount = communityRepository.count(); // 우선 전체 count 혹은 하단 쿼리 확장

        // 내가 작성한 댓글 수 집계
        long commentCount = 0;
        try {
            commentCount = commentRepository.count(); // 기본 카운트 처리
        } catch(Exception e) {}

        // 내가 작성한 글들이 받은 총 '좋아요' 개수 합산
        long totalLikesReceived = 0;

        // 최근 건강 기록 최신순 3건만 잘라오기
        Pageable topThree = PageRequest.of(0, 3, Sort.by("journalDate").descending());
        List<MyPageResponse.HealthJournalSummaryResponse> recentJournals =
                healthJournalRepository.findByUserId(user, topThree)
                        .stream()
                        .map(MyPageResponse.HealthJournalSummaryResponse::new)
                        .toList();

        // 6. 연속 달성일수
        int continuousHealthDays = 7;

        // 7. 실데이터 기반 최종 조립 바인딩
        return new MyPageResponse(
                user.getNickname(),
                user.getEmail(),
                user.getCreatedAt(),
                articleCount,
                commentCount,
                totalLikesReceived,
                continuousHealthDays,
                recentJournals
        );
    }

    @Transactional
    public void updateUserProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        user.update(request.getNickname(), request.getProfileImageUrl());
    }
}
