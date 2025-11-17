package com.kanban.controller;

import com.kanban.dto.AuthDTO;
import com.kanban.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 컨트롤러
 *
 * 사용자 인증(회원가입, 로그인)과 관련된 REST API 엔드포인트를 제공합니다.
 *
 * Base URL: /api/auth
 *
 * API 엔드포인트:
 * - POST   /api/auth/signup     회원가입
 * - POST   /api/auth/login      로그인
 * - POST   /api/auth/logout     로그아웃 (클라이언트에서 토큰 삭제)
 * - GET    /api/auth/me         현재 사용자 정보 조회
 *
 * 인증 방식:
 * - JWT (JSON Web Token) 기반 무상태(stateless) 인증
 * - Authorization 헤더에 "Bearer {token}" 형식으로 토큰 전송
 *
 * @author 메가존 클라우드 인턴십
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입을 처리합니다.
     *
     * 요청 본문 예시:
     * {
     *   "username": "testuser",
     *   "email": "test@example.com",
     *   "password": "password123"
     * }
     *
     * 응답 예시:
     * {
     *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "tokenType": "Bearer",
     *   "userId": 1,
     *   "username": "testuser",
     *   "email": "test@example.com"
     * }
     *
     * @param request 회원가입 요청 정보
     * @return JWT 토큰과 사용자 정보, HTTP 201 상태 코드
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthDTO.AuthResponse> signup(
            @Valid @RequestBody AuthDTO.SignupRequest request) {
        log.info("POST /api/auth/signup - 회원가입 요청: {}", request.getUsername());

        AuthDTO.AuthResponse response = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 로그인을 처리합니다.
     *
     * 요청 본문 예시:
     * {
     *   "usernameOrEmail": "testuser",
     *   "password": "password123"
     * }
     *
     * 응답 예시:
     * {
     *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "tokenType": "Bearer",
     *   "userId": 1,
     *   "username": "testuser",
     *   "email": "test@example.com"
     * }
     *
     * @param request 로그인 요청 정보
     * @return JWT 토큰과 사용자 정보, HTTP 200 상태 코드
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDTO.AuthResponse> login(
            @Valid @RequestBody AuthDTO.LoginRequest request) {
        log.info("POST /api/auth/login - 로그인 요청: {}", request.getUsernameOrEmail());

        AuthDTO.AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃을 처리합니다.
     *
     * JWT는 무상태(stateless)이므로 서버에서 별도의 로그아웃 처리가 필요하지 않습니다.
     * 클라이언트에서 토큰을 삭제하면 됩니다.
     *
     * 이 엔드포인트는 클라이언트 편의를 위해 제공되며,
     * 향후 토큰 블랙리스트 기능을 추가할 수 있습니다.
     *
     * @return HTTP 200 상태 코드와 성공 메시지
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        log.info("POST /api/auth/logout - 로그아웃 요청");

        // JWT는 무상태이므로 서버에서 별도 처리 불필요
        // 클라이언트에서 토큰을 삭제하면 됨

        return ResponseEntity.ok(
                new MessageResponse("로그아웃되었습니다. 클라이언트에서 토큰을 삭제해주세요.")
        );
    }

    /**
     * 현재 인증된 사용자의 정보를 조회합니다.
     *
     * Authorization 헤더에 유효한 JWT 토큰이 필요합니다.
     * 헤더 형식: "Authorization: Bearer {token}"
     *
     * 응답 예시:
     * {
     *   "id": 1,
     *   "username": "testuser",
     *   "email": "test@example.com"
     * }
     *
     * @return 사용자 정보, HTTP 200 상태 코드
     */
    @GetMapping("/me")
    public ResponseEntity<AuthDTO.UserInfoResponse> getCurrentUser() {
        log.info("GET /api/auth/me - 현재 사용자 정보 조회");

        // SecurityContext에서 인증된 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        AuthDTO.UserInfoResponse response = authService.getCurrentUser(userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 전역 예외 핸들러
     *
     * 컨트롤러에서 발생하는 예외를 처리합니다.
     * 추후 별도의 GlobalExceptionHandler로 분리 예정
     *
     * @param e 발생한 예외
     * @return 에러 메시지와 HTTP 400 상태 코드
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("예외 발생: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * 에러 응답 DTO
     */
    record ErrorResponse(int status, String message) {}

    /**
     * 메시지 응답 DTO
     */
    record MessageResponse(String message) {}
}
