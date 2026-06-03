package com.ll.projectLimC.repository;

import com.ll.projectLimC.entity.Article.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Article, Long> {


}
