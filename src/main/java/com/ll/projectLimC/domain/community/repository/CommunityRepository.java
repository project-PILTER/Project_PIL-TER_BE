package com.ll.projectLimC.domain.community.repository;

import com.ll.projectLimC.domain.community.entity.CommunityArticle.CommunityArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<CommunityArticle, Long> {


}
