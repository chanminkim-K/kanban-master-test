package com.kanban.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 인증 DTO (Data Transfer Object)
 *
 * 사용자 인증(회원가입, 로그인)과 관련된 데이터 전송 객체입니다.
 *
 * API 엔드포인트:
 * - POST /api/auth/signup (SignupRequest)
 * - POST /api/auth/login (LoginRequest)
 * - 응답: AuthResponse (JWT 토큰 포함)
 *
 * @author 메가존 클라우드 인턴십
 */
public class AuthDTO {

    /**
     * 회원가입 요청 DTO
     *
     * 새로운 사용자를 등록할 때 사용합니다.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignupRequest {

        @NotBlank(message = "사용자명은 필수입니다")
        @Size(min = 3, max = 50, message = "사용자명은 3자 이상 50자 이하여야 합니다")
        private String username;

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이어야 합니다")
        @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
        private String password;
    }

    /**
     * 로그인 요청 DTO
     *
     * 사용자 인증(로그인)을 수행할 때 사용합니다.
     * 사용자명 또는 이메일로 로그인할 수 있습니다.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {

        @NotBlank(message = "사용자명 또는 이메일은 필수입니다")
        private String usernameOrEmail;

        @NotBlank(message = "비밀번호는 필수입니다")
        private String password;
    }

    /**
     * 인증 응답 DTO
     *
     * 회원가입 또는 로그인 성공 시 반환되는 응답입니다.
     * JWT 토큰과 사용자 정보를 포함합니다.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthResponse {

        /**
         * JWT 액세스 토큰
         * 클라이언트는 이 토큰을 Authorization 헤더에 포함하여 API 요청을 보냅니다.
         * 형식: "Bearer {token}"
         */
        private String accessToken;

        /**
         * 토큰 타입 (기본값: "Bearer")
         */
        @Builder.Default
        private String tokenType = "Bearer";

        /**
         * 사용자 ID
         */
        private Long userId;

        /**
         * 사용자명
         */
        private String username;

        /**
         * 이메일
         */
        private String email;
    }

    /**
     * 현재 사용자 정보 응답 DTO
     *
     * GET /api/auth/me 엔드포인트에서 반환됩니다.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfoResponse {

        private Long id;
        private String username;
        private String email;
    }
}
