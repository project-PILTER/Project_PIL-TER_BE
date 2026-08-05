package com.ll.projectLimC.domain.user.repository;

import com.ll.projectLimC.domain.user.entity.User;

import java.util.List;

public interface UserRepositoryCustom {
    // 필요한 동적 쿼리 메서드 선언
    List<User> searchUsers(String keyword);
}
