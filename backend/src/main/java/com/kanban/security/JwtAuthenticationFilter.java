package com.kanban.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT 인증 필터
 *
 * 모든 HTTP 요청을 가로채서 JWT 토큰을 검증하고 인증을 처리하는 필터입니다.
 * Spring Security의 필터 체인에 등록되어 UsernamePasswordAuthenticationFilter 이전에 실행됩니다.
 *
 * 동작 과정:
 * 1. HTTP 요청 헤더에서 Authorization 헤더를 추출
 * 2. Bearer 토큰 형식인지 확인
 * 3. JWT 토큰 유효성 검증
 * 4. 토큰에서 사용자 ID 추출
 * 5. Spring Security Context에 인증 정보 저장
 *
 * 이후 컨트롤러에서는 @AuthenticationPrincipal 또는 SecurityContextHolder를 통해
 * 인증된 사용자 정보를 조회할 수 있습니다.
 *
 * @author 메가존 클라우드 인턴십
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    /**
     * JWT 토큰 검증 및 인증 처리를 수행합니다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException 입출력 예외
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. 요청 헤더에서 JWT 토큰 추출
            String jwt = getJwtFromRequest(request);

            // 2. 토큰이 존재하고 유효한지 검증
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

                // 3. 토큰에서 사용자 ID 추출
                Long userId = tokenProvider.getUserIdFromToken(jwt);

                // 4. 인증 객체 생성
                // principal에 userId를 저장하여 나중에 컨트롤러에서 사용
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,              // principal (인증된 사용자 정보)
                                null,                // credentials (비밀번호, JWT에서는 불필요)
                                new ArrayList<>()    // authorities (권한 목록, 현재는 빈 리스트)
                        );

                // 요청 상세 정보 설정
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. Security Context에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT 인증 성공 - 사용자 ID: {}, URI: {}", userId, request.getRequestURI());
            }
        } catch (Exception ex) {
            log.error("Security Context에 사용자 인증을 설정할 수 없습니다: {}", ex.getMessage());
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 헤더에서 JWT 토큰을 추출합니다.
     *
     * Authorization 헤더 형식: "Bearer {JWT 토큰}"
     *
     * @param request HTTP 요청
     * @return JWT 토큰 문자열 (없으면 null)
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Authorization 헤더가 "Bearer "로 시작하는지 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // "Bearer " 이후의 토큰 부분만 반환
            return bearerToken.substring(7);
        }

        return null;
    }
}
