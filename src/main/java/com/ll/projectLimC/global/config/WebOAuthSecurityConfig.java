package com.ll.projectLimC.global.config;

import com.ll.projectLimC.global.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.ll.projectLimC.global.oauth.OAuth2SuccessHandler;
import com.ll.projectLimC.global.oauth.service.OAuth2UserCustomService;
import com.ll.projectLimC.global.refreshToken.repository.RefreshTokenRepository;
import com.ll.projectLimC.domain.user.service.UserService;
import com.ll.projectLimC.global.token.TokenAuthentiocationFilter;
import com.ll.projectLimC.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final Environment env;
    @Bean
    public WebSecurityCustomizer configure(){
        return (web) -> {
            // 1. 기본적으로 무시할 정적 리소스 설정
            var ignoring = web.ignoring().requestMatchers(
                    "/img/**",
                    "/css/**",
                    "/js/**"
            );

            // 2. 현재 활성화된 프로필 중 'prod'가 없을 때만 H2 콘솔 무시 설정 추가
            boolean isProd = Arrays.asList(env.getActiveProfiles()).contains("prod");
            if (!isProd) {
                ignoring.requestMatchers(PathRequest.toH2Console());
            }
        };
    }

    @Value("${cors.allowed-origin:https://pilter.co.kr}")
    private String allowedOrigin;

    // 토큰 방식으로 인증을 하기에 기존에 사용하던 폼 로그인, 세션 비활성화
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 연결
                .cors(cors -> cors.disable())
                // CSRF 비활성화 (API 서버이므로 필수)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(management -> management.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                // 헤더를 확인할 커스텀 필터 추가
                .addFilterBefore(tokenAuthentiocationFilter(), UsernamePasswordAuthenticationFilter.class)
                // 토큰 재발급 URL은 인증 없이 접근 가능하도록 설정. 나머지 API URL은 인증 필요
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/token").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/v3/api-docs/**",
                         "/api/v3/api-docs/**",
                         "/swagger-ui/**",
                         "/api/swagger-ui/**",
                         "/swagger-ui.html",
                         "/api/swagger-ui.html",
                         "/swagger-resources/**",
                         "/webjars/**").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        // Authorization 요청과 관련된 상태 저장
                        .authorizationEndpoint(authorizationEndpoint ->
                                authorizationEndpoint.authorizationRequestRepository(
                                oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oAuth2UserCustomService))
                        // 인증 성공 시 실행할 핸들러
                        .successHandler(oAuth2SuccessHandler())
                )
                // /api로 시작하는 url인 경우 401 상태 코드를 반환하도록 예외 처리
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                        request -> request.getRequestURI().startsWith("/api/"))
                        )
                .build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler(){
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService);
    }

    @Bean
    public TokenAuthentiocationFilter tokenAuthentiocationFilter(){
        return new TokenAuthentiocationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository(){
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    // CORS 세부 설정 빈 추가
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000"); // 프론트엔드 주소
        configuration.addAllowedOrigin("https://pilter.co.kr");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}