package com.ll.projectLimC.domain.repository;

import com.ll.projectLimC.domain.entity.CommunityArticle.CommunityArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<CommunityArticle, Long> {


}
