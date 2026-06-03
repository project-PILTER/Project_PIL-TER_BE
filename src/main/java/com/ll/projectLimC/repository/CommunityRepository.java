package com.ll.projectLimC.repository;

import com.ll.projectLimC.entity.CommunityArticle.CommunityArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<CommunityArticle, Long> {


}
