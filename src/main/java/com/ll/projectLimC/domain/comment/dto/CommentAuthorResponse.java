//package com.ll.projectLimC.domain.comment.dto;
//
//import com.ll.projectLimC.domain.user.entity.User;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Getter;
//
//@Getter
//@Schema(description = "댓글 작성자 정보 응답 폼")
//public class CommentAuthorResponse {
//    @Schema(description = "", example = "123")
//    private String id; // 댓글 작성자 고유 ID (프론트엔드 주석대로 식별용으로 노출)
//
//    @Schema(description = "댓글 작성자 닉네임", example = "달려라하니")
//    private String nickName;
//
//    @Schema(description = "댓글 작성자 프로필 이미지", example = "https://example.com/image.png")
//    private String profileImage;
//
//    @Schema(description = "의사인지 아닌지 확인")
//    private Boolean isMedicalExpert;
//
//    @Schema(description = "의사인 댓글 작성자", example = "내과 전문의")
//    private String expertTitle;
//
//    public CommentAuthorResponse(User user){
//        this.id = String.valueOf(user.getId()); // Long 타입을 프론트 스펙에 맞춰 String으로 변환
//        this.nickName = user.getNickname();;
//        this.profileImage = user.getProfileImage() != null ? user.getProfileImage() : "default_profile_url";
//        this.isMedicalExpert = user.getIsMedicalExpert();
//        this.expertTitle = user.getExpertTitle();
//    }
//}