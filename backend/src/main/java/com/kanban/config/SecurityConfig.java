package com.kanban.config;

import com.kanban.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 설정
 *
 * 칸반 보드 애플리케이션의 보안 설정을 구성하는 클래스입니다.
 * JWT 기반 인증, 인가, CORS, 세션 관리를 설정합니다.
 *
 * 현재 설정:
 * - /api/auth/** : 인증 없이 허용 (회원가입, 로그인)
 * - /api/** : JWT 인증 필요
 * - H2 콘솔 접근 활성화 (개발용)
 * - 프론트엔드 요청을 허용하도록 CORS 설정
 * - 무상태 세션 관리 (JWT 기반)
 * - BCrypt 비밀번호 암호화
 * - JwtAuthenticationFilter를 필터 체인에 등록
 *
 * @author 메가존 클라우드 인턴십
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 보안 필터 체인을 설정합니다.
     *
     * 보안 규칙:
     * - /api/auth/** : 인증 없이 허용 (회원가입, 로그인)
     * - /api/** : JWT 인증 필요
     * - /h2-console/** : H2 데이터베이스 콘솔 접근 (개발 전용)
     * - CSRF 보호 비활성화 (무상태 인증을 위해 JWT 사용)
     * - 세션 관리는 무상태 (JWT 기반)
     * - JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
     *
     * @param http 설정할 HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 설정 실패 시
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 설정 - 무상태 API를 위해 비활성화
                .csrf(csrf -> csrf.disable())

                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        // 인증 API는 모두 허용 (회원가입, 로그인)
                        .requestMatchers("/api/auth/**").permitAll()

                        // H2 콘솔 접근 허용 (개발 전용)
                        .requestMatchers("/h2-console/**").permitAll()

                        // 그 외 모든 API는 인증 필요
                        .requestMatchers("/api/**").authenticated()

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 세션 관리 - JWT를 위한 무상태
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // H2 콘솔 설정 (H2 콘솔을 위해 프레임 허용)
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )

                // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt 해싱 알고리즘을 사용하는 비밀번호 인코더 빈입니다.
     * BCrypt는 비밀번호 저장을 위해 설계된 강력하고 적응형 해싱 함수입니다.
     *
     * @return PasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 프론트엔드 애플리케이션의 요청을 허용하도록 CORS를 설정합니다.
     *
     * 허용된 Origin: http://localhost:3000 (React 개발 서버)
     * 허용된 메서드: GET, POST, PUT, PATCH, DELETE, OPTIONS
     * 허용된 헤더: 모든 헤더
     * 자격 증명 허용: true (쿠키/인증 헤더)
     *
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드 origin 허용
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // 모든 HTTP 메서드 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 모든 헤더 허용
        configuration.setAllowedHeaders(List.of("*"));

        // 자격 증명 허용 (쿠키, 인증 헤더)
        configuration.setAllowCredentials(true);

        // 모든 경로에 CORS 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
