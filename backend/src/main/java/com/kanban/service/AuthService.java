package com.kanban.service;

import com.kanban.dto.AuthDTO;
import com.kanban.model.User;
import com.kanban.repository.UserRepository;
import com.kanban.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스
 *
 * 사용자 인증(회원가입, 로그인)과 관련된 비즈니스 로직을 처리하는 서비스입니다.
 *
 * 주요 기능:
 * - 회원가입: 사용자 정보 검증, 비밀번호 암호화, 사용자 저장
 * - 로그인: 사용자 인증, JWT 토큰 생성
 * - 현재 사용자 정보 조회
 *
 * 보안:
 * - 비밀번호는 BCrypt로 암호화하여 저장
 * - JWT 토큰으로 무상태(stateless) 인증 제공
 *
 * @author 메가존 클라우드 인턴십
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    /**
     * 회원가입을 처리합니다.
     *
     * 처리 과정:
     * 1. 사용자명 중복 확인
     * 2. 이메일 중복 확인
     * 3. 비밀번호 암호화 (BCrypt)
     * 4. 사용자 정보 저장
     * 5. JWT 토큰 생성 및 반환
     *
     * @param request 회원가입 요청 정보
     * @return JWT 토큰과 사용자 정보
     * @throws RuntimeException 사용자명 또는 이메일이 이미 존재하는 경우
     */
    @Transactional
    public AuthDTO.AuthResponse signup(AuthDTO.SignupRequest request) {
        log.info("회원가입 시작 - 사용자명: {}, 이메일: {}", request.getUsername(), request.getEmail());

        // 1. 사용자명 중복 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("이미 사용 중인 사용자명입니다: " + request.getUsername());
        }

        // 2. 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다: " + request.getEmail());
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4. 사용자 엔티티 생성 및 저장
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encodedPassword)
                .build();

        User savedUser = userRepository.save(user);
        log.info("회원가입 완료 - 사용자 ID: {}, 사용자명: {}", savedUser.getId(), savedUser.getUsername());

        // 5. JWT 토큰 생성
        String accessToken = tokenProvider.createToken(savedUser.getId());

        // 6. 응답 DTO 생성 및 반환
        return AuthDTO.AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();
    }

    /**
     * 로그인을 처리합니다.
     *
     * 처리 과정:
     * 1. 사용자명 또는 이메일로 사용자 조회
     * 2. 비밀번호 검증
     * 3. JWT 토큰 생성 및 반환
     *
     * @param request 로그인 요청 정보
     * @return JWT 토큰과 사용자 정보
     * @throws RuntimeException 사용자를 찾을 수 없거나 비밀번호가 일치하지 않는 경우
     */
    @Transactional
    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        log.info("로그인 시도 - 사용자명/이메일: {}", request.getUsernameOrEmail());

        // 1. 사용자명 또는 이메일로 사용자 조회
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + request.getUsernameOrEmail()));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("로그인 실패 - 비밀번호 불일치: {}", request.getUsernameOrEmail());
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        log.info("로그인 성공 - 사용자 ID: {}, 사용자명: {}", user.getId(), user.getUsername());

        // 3. JWT 토큰 생성
        String accessToken = tokenProvider.createToken(user.getId());

        // 4. 응답 DTO 생성 및 반환
        return AuthDTO.AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    /**
     * 현재 인증된 사용자의 정보를 조회합니다.
     *
     * @param userId 사용자 ID (SecurityContext에서 추출)
     * @return 사용자 정보
     * @throws RuntimeException 사용자를 찾을 수 없는 경우
     */
    public AuthDTO.UserInfoResponse getCurrentUser(Long userId) {
        log.info("현재 사용자 정보 조회 - 사용자 ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        return AuthDTO.UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
