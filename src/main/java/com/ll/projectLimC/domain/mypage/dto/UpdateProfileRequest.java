package com.ll.projectLimC.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "마이페이지 프로필 수정 요청 양식")
public class UpdateProfileRequest {

    @Schema(description = "변경할 새 닉네임", example = "개발왕제욱")
    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    @Column(unique = true)
    private String nickname;

    @Schema(description = "변경할 새 프로필 이미지 URL (선택)", example = "https://example.com/new-avatar.png")
    private String profileImageUrl;
}
