package com.ll.projectLimC.domain.community.repository;

import com.ll.projectLimC.domain.community.entity.ArticleDrafts.ArticleDrafts;
import com.ll.projectLimC.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleDraftsRepository extends JpaRepository<ArticleDrafts, Long> {
    // 특정 유저의 '모든' 임시저장 목록 조회 (최신순 정렬)
    List<ArticleDrafts> findByUserOrderByUpdatedAtDesc(User user);

    // 여러 임시저장 글 중 '특정 임시저장 글 1개'를 선택해서 불러오거나 수정할 때 사용
    Optional<ArticleDrafts> findByIdAndUser(Long id, User user);

    // 특정 유저의 '모든' 임시저장 글을 일괄 삭제 (회원 탈퇴, 또는 전체 초기화 시 활용)
    void deleteByUser(User user);

    // 특정 임시저장 글 '하나만' 삭제할 때 사용
    void deleteByIdAndUser(Long id, User user);
}
