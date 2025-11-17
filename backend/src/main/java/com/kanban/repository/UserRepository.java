package com.kanban.repository;

import com.kanban.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 리포지토리
 *
 * User 엔티티에 대한 데이터베이스 접근을 담당하는 리포지토리입니다.
 * Spring Data JPA를 사용하여 기본적인 CRUD 작업과 사용자 정의 쿼리를 제공합니다.
 *
 * 기본 제공 메서드:
 * - save(): 사용자 저장 (생성/수정)
 * - findById(): ID로 사용자 조회
 * - findAll(): 모든 사용자 조회
 * - delete(): 사용자 삭제
 * - existsById(): 사용자 존재 여부 확인
 *
 * 커스텀 메서드:
 * - findByUsername(): 사용자명으로 사용자 조회
 * - findByEmail(): 이메일로 사용자 조회
 * - existsByUsername(): 사용자명 중복 확인
 * - existsByEmail(): 이메일 중복 확인
 *
 * @author 메가존 클라우드 인턴십
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 사용자를 조회합니다.
     *
     * @param username 조회할 사용자명
     * @return 사용자 Optional 객체
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자를 조회합니다.
     *
     * @param email 조회할 이메일
     * @return 사용자 Optional 객체
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자명이 이미 존재하는지 확인합니다.
     * 회원가입 시 사용자명 중복 체크에 사용됩니다.
     *
     * @param username 확인할 사용자명
     * @return 존재 여부 (true: 존재함, false: 존재하지 않음)
     */
    Boolean existsByUsername(String username);

    /**
     * 이메일이 이미 존재하는지 확인합니다.
     * 회원가입 시 이메일 중복 체크에 사용됩니다.
     *
     * @param email 확인할 이메일
     * @return 존재 여부 (true: 존재함, false: 존재하지 않음)
     */
    Boolean existsByEmail(String email);
}
