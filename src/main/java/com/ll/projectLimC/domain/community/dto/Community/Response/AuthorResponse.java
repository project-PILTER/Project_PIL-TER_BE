package com.ll.projectLimC.domain.community.dto.Community.Response;

import com.ll.projectLimC.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorResponse {
    private Long id;
    private String nickname;
    private String profileImage;
    private Boolean isMedicalExpert; // 의료 전문가 여부
    private String expertTitle;      // 전문가 직함 (예: 혈액종양내과 전문의)

    // User 엔티티로부터 DTO 생성
    public static AuthorResponse from(User user) {
        if (user == null) {
            return new AuthorResponse(null, "익명", "/logo/logo.png", false, null);
        }
        return new AuthorResponse(
                user.getId(),
                user.getNickname(),
                // 값이 있더라도 비어있거나 공백이면 기본 로고 반환
                user.getProfileImage() != null && !user.getProfileImage().trim().isEmpty()
                        ? user.getProfileImage()
                        : "/logo/logo.png",
                user.getIsMedicalExpert() != null ? user.getIsMedicalExpert() : false,
                user.getExpertTitle()
        );
    }
}
