package com.kanban.repository;

import com.kanban.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 태스크 리포지토리
 *
 * Task 엔티티에 대한 데이터베이스 접근을 담당하는 리포지토리입니다.
 * Spring Data JPA를 사용하여 기본적인 CRUD 작업과 사용자 정의 쿼리를 제공합니다.
 *
 * 기본 제공 메서드:
 * - save(): 태스크 저장 (생성/수정)
 * - findById(): ID로 태스크 조회
 * - findAll(): 모든 태스크 조회
 * - delete(): 태스크 삭제
 * - existsById(): 태스크 존재 여부 확인
 *
 * 커스텀 메서드:
 * - findByBoardId(): 특정 보드의 모든 태스크 조회
 * - findByBoardIdOrderByPositionAsc(): 특정 보드의 태스크를 position 순으로 조회
 * - findByBoardIdAndStatus(): 특정 보드에서 특정 상태의 태스크 조회
 *
 * API 엔드포인트와의 매핑:
 * - GET /api/boards/{boardId}/tasks -> findByBoardId()
 * - GET /api/tasks/{id} -> findById()
 * - POST /api/boards/{boardId}/tasks -> save()
 * - PUT /api/tasks/{id} -> save()
 * - PATCH /api/tasks/{id}/status -> save()
 * - DELETE /api/tasks/{id} -> delete()
 *
 * @author 메가존 클라우드 인턴십
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * 특정 보드의 모든 태스크를 조회합니다.
     *
     * @param boardId 보드 ID
     * @return 보드의 태스크 목록
     */
    List<Task> findByBoardId(Long boardId);

    /**
     * 특정 보드의 모든 태스크를 position 순으로 조회합니다.
     * 드래그 앤 드롭 기능에서 태스크 순서를 유지하는데 사용됩니다.
     *
     * @param boardId 보드 ID
     * @return position 순으로 정렬된 태스크 목록
     */
    List<Task> findByBoardIdOrderByPositionAsc(Long boardId);

    /**
     * 특정 보드에서 특정 상태의 태스크를 조회합니다.
     * 예: "할 일", "진행 중", "완료" 상태별로 태스크를 필터링합니다.
     *
     * @param boardId 보드 ID
     * @param status 태스크 상태
     * @return 특정 상태의 태스크 목록
     */
    List<Task> findByBoardIdAndStatus(Long boardId, Task.TaskStatus status);

    /**
     * 특정 보드의 태스크를 생성일 기준 내림차순으로 조회합니다.
     * 최신 태스크가 먼저 표시됩니다.
     *
     * @param boardId 보드 ID
     * @return 생성일 기준 내림차순으로 정렬된 태스크 목록
     */
    List<Task> findByBoardIdOrderByCreatedAtDesc(Long boardId);
}
