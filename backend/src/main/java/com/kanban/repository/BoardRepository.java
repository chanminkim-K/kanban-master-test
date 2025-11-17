package com.kanban.repository;

import com.kanban.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 보드 리포지토리
 *
 * Board 엔티티에 대한 데이터베이스 접근을 담당하는 리포지토리입니다.
 * Spring Data JPA를 사용하여 기본적인 CRUD 작업과 사용자 정의 쿼리를 제공합니다.
 *
 * 기본 제공 메서드:
 * - save(): 보드 저장 (생성/수정)
 * - findById(): ID로 보드 조회
 * - findAll(): 모든 보드 조회
 * - delete(): 보드 삭제
 * - existsById(): 보드 존재 여부 확인
 *
 * 커스텀 메서드:
 * - findByUserId(): 특정 사용자의 모든 보드 조회
 * - findByUserIdOrderByCreatedAtDesc(): 특정 사용자의 보드를 생성일 기준 내림차순으로 조회
 *
 * API 엔드포인트와의 매핑:
 * - GET /api/boards -> findByUserId()
 * - GET /api/boards/{id} -> findById()
 * - POST /api/boards -> save()
 * - PUT /api/boards/{id} -> save()
 * - DELETE /api/boards/{id} -> delete()
 *
 * @author 메가존 클라우드 인턴십
 */
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    /**
     * 특정 사용자의 모든 보드를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자의 보드 목록
     */
    List<Board> findByUserId(Long userId);

    /**
     * 특정 사용자의 모든 보드를 생성일 기준 내림차순으로 조회합니다.
     * 최신 보드가 먼저 표시됩니다.
     *
     * @param userId 사용자 ID
     * @return 생성일 기준 내림차순으로 정렬된 보드 목록
     */
    List<Board> findByUserIdOrderByCreatedAtDesc(Long userId);
}
