package com.ll.projectLimC.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

// 테이블 이름을 데이터베이스 예약어와 격리하기 위해 큰따옴표안에 이스케이프 처리하여 매핑
@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// UserDetails를 상속받아 인증 객체로 사용
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    // 사용자 이름
    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(name = "is_medical_expert", nullable = false)
    private boolean isMedicalExpert = false; // 기본값은 일반 유저(false)

    @Column(name = "expert_title")
    private String expertTitle; // 전문의가 아닐 경우 null 가능

    // 소셜 로그인 식별용 연동 필드
    private String provider;   // google, naver, kakao
    private String providerId; // 소셜 측에서 던져준 고유 서브/ID 키 값

    @Builder
    public User(String email, String password, String nickname,
                String profileImage,
                LocalDateTime createdAt,
                boolean isMedicalExpert,
                String expertTitle){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
        this.isMedicalExpert = isMedicalExpert;
        this.expertTitle = expertTitle;
    }

    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    // 사용자의 id를 반환 - 고유한 값
    @Override
    public String getUsername() {
        return this.email; // "email" 문자열 대신 실제 필드 반환
    }

    // 사용자의 패스워드 반환
    @Override
    public String getPassword() {
        return this.password; // "password" 문자열 대신 실제 암호화된 필드 반환
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        // 계정이 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않았음.
    }

    // 게정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        // 계정이 잠겼는지 확인하는 로직
        return true; // true -> 만료되지 않았음.
    }

    // 패스워드의 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        // 패스워드가 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않았음.
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        // 계정 사용 가능한지 확인하는 로직
        return true; // true -> 사용 가능.
    }

    // 사용자 이름 변경
    // 유저 정보 수정 메서드 확장 (프로필 이미지 등도 변경 가능하도록)
    public User update(String nickname, String profileImage){
        this.nickname = nickname;
        this.profileImage = profileImage;

        return this;
    }

    // 소셜 로그인 시 프로필 업데이트용 메서드
    public User updateSocialProfile(String nickname) {
        this.nickname = nickname;
        return this;
    }
}
