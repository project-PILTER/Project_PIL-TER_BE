package com.ll.projectLimC.domain.repository;

import com.ll.projectLimC.domain.entity.Like.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
