package com.ll.projectLimC.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청 정보 폼")
public class AddUserRequest {
    @Schema(description = "사용자 로그인용 이메일 주소", example = "user1234@gmail.com")
    @Column(unique = true)
    private String email;

    @Schema(description = "사용자명", example = "홀리몰리")
    @Column(unique = true)
    private String nickname;

    @Schema(description = "비밀번호 (암호화되어 저장됨)", example = "password1234!")
    private String password;
}
