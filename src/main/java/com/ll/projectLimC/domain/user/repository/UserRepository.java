package com.ll.projectLimC.domain.user.repository;

import com.ll.projectLimC.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByEmail(String email); // email로 사용자 정보를 가져옴.

    // 소셜 계정 중복 가입 체크 및 대조를 위한 핵심 쿼리
    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    boolean existsByNickname(String initialNickname);
}
