package com.ll.projectLimC.domain.service;

import com.ll.projectLimC.domain.repository.CommunityRepository;
import com.ll.projectLimC.domain.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final CommunityRepository communityRepository;


}
