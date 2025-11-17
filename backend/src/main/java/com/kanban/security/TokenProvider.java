package com.kanban.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 제공자
 *
 * JWT(JSON Web Token) 토큰의 생성, 검증, 파싱을 담당하는 유틸리티 클래스입니다.
 *
 * 주요 기능:
 * - JWT 토큰 생성
 * - JWT 토큰 검증
 * - 토큰에서 사용자 ID 추출
 * - 토큰 만료 시간 관리
 *
 * 사용되는 JWT 라이브러리: io.jsonwebtoken (jjwt)
 *
 * @author 메가존 클라우드 인턴십
 */
@Component
@Slf4j
public class TokenProvider {

    private final SecretKey key;
    private final long tokenValidityInMilliseconds;

    /**
     * TokenProvider 생성자
     *
     * application.properties에서 JWT 설정을 읽어와 초기화합니다.
     *
     * @param secret JWT 서명에 사용할 시크릿 키
     * @param tokenValidityInMilliseconds 토큰 유효 시간 (밀리초)
     */
    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long tokenValidityInMilliseconds) {

        // 시크릿 키를 바이트 배열로 변환하여 SecretKey 생성
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;

        log.info("TokenProvider 초기화 완료 - 토큰 유효 시간: {}ms", tokenValidityInMilliseconds);
    }

    /**
     * JWT 토큰을 생성합니다.
     *
     * @param userId 사용자 ID
     * @return 생성된 JWT 토큰
     */
    public String createToken(Long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.tokenValidityInMilliseconds);

        String token = Jwts.builder()
                .setSubject(userId.toString())  // 토큰의 주체(subject)에 사용자 ID 저장
                .setIssuedAt(now)                // 토큰 발급 시간
                .setExpiration(validity)         // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256)  // HS256 알고리즘으로 서명
                .compact();

        log.debug("JWT 토큰 생성 완료 - 사용자 ID: {}", userId);
        return token;
    }

    /**
     * JWT 토큰을 검증합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 유효한 토큰이면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * JWT 토큰에서 사용자 ID를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String subject = claims.getSubject();
        log.debug("토큰에서 사용자 ID 추출: {}", subject);

        return Long.parseLong(subject);
    }
}
