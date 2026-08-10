package com.ll.projectLimC.domain.mypage.controller;

import com.ll.projectLimC.domain.mypage.dto.MyPageResponse;
import com.ll.projectLimC.domain.mypage.dto.UpdateProfileRequest;
import com.ll.projectLimC.domain.mypage.service.MyPageService;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Tag(name = "마이페이지 대시보드 API", description = "마이페이지 조회 및 회원 프로필 설정 컴포넌트")
@RestController
@RequiredArgsConstructor
public class MyPageApiController {

    private final MyPageService mypageService;

    @Operation(summary = "마이페이지 대시보드 통합 데이터 조회",
            description = "현재 인증된 사용자의 프로필 상태 및 활동 스펙, 최근 컨디션 로그(최대 3건)를 원스톱으로 집계해 내려줍니다.")
    @GetMapping("/mypage")
    public ResponseEntity<MyPageResponse> getMypageDashboard(Principal principal) {
        // principal null 검증 (로그인 안 한 사용자 방어)
        if (principal == null) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        MyPageResponse response = mypageService.getMypageData(principal.getName());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "마이페이지 회원 정보(프로필) 수정",
            description = "닉네임 변경 및 프로필 이미지 리소스를 수정하고 더티체킹 상태 갱신을 수행합니다.")
    @PutMapping("/mypage/profile")
    public ResponseEntity<Void> updateProfile(
            Principal principal,
            @RequestPart(value = "request") UpdateProfileRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        // principal null 검증 (로그인 안 한 사용자 방어)
        if (principal == null) {
            throw new GlobalCustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        mypageService.updateUserProfile(principal.getName(), request, file);
        return ResponseEntity.ok().build();
    }
}
