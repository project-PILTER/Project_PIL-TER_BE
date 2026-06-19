package com.ll.projectLimC.domain.dto;

import com.ll.projectLimC.domain.entity.User.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentAuthorResponse {
    private String id; // 댓글 작성자 고유 ID (프론트엔드 주석대로 식별용으로 노출)
    private String nickName;
    private String profileImage;
    private boolean isMedicalExpert;
    private String expertTitle;

    public CommentAuthorResponse(User user){
        this.id = String.valueOf(user.getId()); // Long 타입을 프론트 스펙에 맞춰 String으로 변환
        this.nickName = user.getNickname();;
        this.profileImage = user.getProfileImage() != null ? user.getProfileImage() : "default_profile_url";
        this.isMedicalExpert = user.isMedicalExpert();
        this.expertTitle = user.getExpertTitle();
    }
}