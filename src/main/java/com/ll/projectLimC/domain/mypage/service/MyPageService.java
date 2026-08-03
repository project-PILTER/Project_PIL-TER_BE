package com.ll.projectLimC.domain.mypage.service;

import com.ll.projectLimC.domain.user.entity.User;
import com.ll.projectLimC.domain.user.repository.UserRepository;
import com.ll.projectLimC.domain.comment.repository.CommentRepository;
import com.ll.projectLimC.domain.community.repository.CommunityRepository;
import com.ll.projectLimC.domain.healthJournal.repository.HealthJournalRepository;
import com.ll.projectLimC.domain.mypage.dto.MyPageResponse;
import com.ll.projectLimC.domain.mypage.dto.UpdateProfileRequest;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;
    private final HealthJournalRepository healthJournalRepository;

    public MyPageResponse getMypageData(String email) {
        // 1. 유저 검증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        // 2. 내가 작성한 게시글 수 집계
        long articleCount = communityRepository.countByUser(user);

        // 3. 내가 작성한 댓글 수 집계
        long commentCount = commentRepository.countByUser(user);

        try {
            commentCount = commentRepository.count();
        } catch(Exception e) {
            log.error("댓글 카운트 중 에러 발생", e);
        }

        // 4. 내가 작성한 글들이 받은 총 '좋아요' 개수 합산
        long totalLikesReceived = 0;

        // 5. 최근 건강 기록 최신순 3건만 잘라와서 DTO로 완벽하게 맵핑 변환
        Pageable topThree = PageRequest.of(0, 3, Sort.by("journalDate").descending());

        // 레포지토리에서 나온 엔티티 스트림을 DTO의 내부 생성자(new)를 통해 규격에 맞게 변환
        List<MyPageResponse.HealthJournalSummaryResponse> recentJournals =
                healthJournalRepository.findByUser(user, topThree)
                        .getContent()
                        .stream()
                        .map(MyPageResponse.HealthJournalSummaryResponse::new)
                        .toList();

        // 6. 연속 달성일수
        int continuousHealthDays = 7;

        // 7. 빌더 패턴을 사용하여 홍수처럼 밀려오는 생성자 에러 원천 차단
        return MyPageResponse.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .articleCount(articleCount)
                .commentCount(commentCount)
                .totalLikesReceived(totalLikesReceived)
                .continuousHealthDays(continuousHealthDays)
                .recentJournals(recentJournals)
                .build();
    }

    @Transactional
    public void updateUserProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.NOT_FOUND_THE_USER));

        user.update(request.getNickname(), request.getProfileImageUrl());
    }
}
