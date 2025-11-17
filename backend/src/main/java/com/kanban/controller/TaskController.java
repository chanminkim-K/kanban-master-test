package com.kanban.controller;

import com.kanban.dto.TaskDTO;
import com.kanban.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 태스크 컨트롤러
 *
 * 태스크 관련 REST API 엔드포인트를 제공하는 컨트롤러입니다.
 * 클라이언트의 HTTP 요청을 받아 TaskService로 전달하고 응답을 반환합니다.
 *
 * Base URL: /api
 *
 * API 엔드포인트:
 * - POST   /api/boards/{boardId}/tasks         태스크 생성 (JWT 인증 필요)
 * - GET    /api/boards/{boardId}/tasks         보드의 모든 태스크 조회 (JWT 인증 필요)
 * - GET    /api/tasks/{id}                     특정 태스크 조회 (JWT 인증 필요)
 * - PUT    /api/tasks/{id}                     태스크 수정 (JWT 인증 필요)
 * - PATCH  /api/tasks/{id}/status              태스크 상태 변경 (JWT 인증 필요)
 * - PATCH  /api/tasks/{id}/position            태스크 위치 변경 (JWT 인증 필요)
 * - DELETE /api/tasks/{id}                     태스크 삭제 (JWT 인증 필요)
 *
 * 인증:
 * - JWT 토큰을 Authorization 헤더에 포함하여 요청해야 합니다.
 * - 형식: "Authorization: Bearer {token}"
 * - SecurityContext에서 사용자 ID를 자동으로 추출합니다.
 *
 * @author 메가존 클라우드 인턴십
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    /**
     * 새로운 태스크를 생성합니다.
     *
     * JWT 토큰에서 사용자 ID를 추출하여 보드 소유자 권한을 확인하고 태스크를 생성합니다.
     *
     * @param boardId 보드 ID
     * @param request 태스크 생성 요청 DTO
     * @return 생성된 태스크 정보와 HTTP 201 상태 코드
     */
    @PostMapping("/boards/{boardId}/tasks")
    public ResponseEntity<TaskDTO.TaskResponse> createTask(
            @PathVariable Long boardId,
            @Valid @RequestBody TaskDTO.CreateRequest request) {
        log.info("POST /api/boards/{}/tasks - 태스크 생성 요청: {}", boardId, request.getTitle());

        // SecurityContext에서 인증된 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        TaskDTO.TaskResponse createdTask = taskService.createTask(boardId, request, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);
    }

    /**
     * 특정 보드의 모든 태스크를 조회합니다.
     *
     * JWT 토큰에서 사용자 ID를 추출하여 보드 소유자 권한을 확인하고 태스크 목록을 조회합니다.
     *
     * @param boardId 보드 ID
     * @return 태스크 목록과 HTTP 200 상태 코드
     */
    @GetMapping("/boards/{boardId}/tasks")
    public ResponseEntity<List<TaskDTO.TaskResponse>> getTasksByBoardId(@PathVariable Long boardId) {
        log.info("GET /api/boards/{}/tasks - 태스크 목록 조회 요청", boardId);

        // SecurityContext에서 인증된 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        List<TaskDTO.TaskResponse> tasks = taskService.getTasksByBoardId(boardId, userId);

        return ResponseEntity.ok(tasks);
    }

    /**
     * 특정 태스크를 ID로 조회합니다.
     *
     * JWT 토큰에서 사용자 ID를 추출하여 권한을 확인하고 태스크를 조회합니다.
     *
     * @param id 태스크 ID
     * @return 태스크 정보와 HTTP 200 상태 코드
     */
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO.TaskResponse> getTaskById(@PathVariable Long id) {
        log.info("GET /api/tasks/{} - 태스크 조회 요청", id);

        // SecurityContext에서 인증된 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        TaskDTO.TaskResponse task = taskService.getTaskById(id, userId);

        return ResponseEntity.ok(task);
    }

    /**
     * 태스크 정보를 수정합니다 (제목, 설명).
     *
     * JWT 토큰에서 사용자 ID를 추출하여 권한을 확인하고 태스크를 수정합니다.
     *
     * @param id 태스크 ID
     * @param request 태스크 수정 요청 DTO
     * @return 수정된 태스크 정보와 HTTP 200 상태 코드
     */
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO.TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO.UpdateRequest request) {
        log.info("PUT /api/tasks/{} - 태스크 수정 요청", id);

        // SecurityContext에서 인증된 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        TaskDTO.TaskResponse updatedTask = taskService.updateTask(id, request, userId);

        return ResponseEntity.ok(updatedTask);
    }

    /**
     * 태스크의 상태를 변경합니다 (할 일, 진행 중, 완료).
     *
     * JWT 토큰에서 사용자 ID를 추출하여 권한을 확인하고 태스크 상태를 변경합니다.
     * 상태가 변경되면 해당 상태 컬럼 내에서 새로운 위치로 이동합니다.
     *
     * @param id 태스크 ID
     * @param request 상태 변경 요청 DTO
     * @return 수정된 태스크 정보와 HTTP 200 상태 코드
     */
    @PatchMapping("/tasks/{id}/status")
    public ResponseEntity<TaskDTO.TaskResponse> updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO.UpdateStatusRequest request) {
        log.info("PATCH /api/tasks/{}/status - 태스크 상태 변경 요청: {}", id, request.getStatus());

        // SecurityContext에서 인증된 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        TaskDTO.TaskResponse updatedTask = taskService.updateTaskStatus(id, request, userId);

        return ResponseEntity.ok(updatedTask);
    }

    /**
     * 태스크의 위치를 변경합니다 (드래그 앤 드롭).
     *
     * JWT 토큰에서 사용자 ID를 추출하여 권한을 확인하고 태스크 위치를 변경합니다.
     * 동일한 상태 컬럼 내에서 순서만 변경할 때 사용합니다.
     *
     * @param id 태스크 ID
     * @param request 위치 변경 요청 DTO
     * @return 수정된 태스크 정보와 HTTP 200 상태 코드
     */
    @PatchMapping("/tasks/{id}/position")
    public ResponseEntity<TaskDTO.TaskResponse> updateTaskPosition(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO.UpdatePositionRequest request) {
        log.info("PATCH /api/tasks/{}/position - 태스크 위치 변경 요청: {}", id, request.getPosition());

        // SecurityContext에서 인증된 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        TaskDTO.TaskResponse updatedTask = taskService.updateTaskPosition(id, request, userId);

        return ResponseEntity.ok(updatedTask);
    }

    /**
     * 태스크를 삭제합니다.
     *
     * JWT 토큰에서 사용자 ID를 추출하여 권한을 확인하고 태스크를 삭제합니다.
     *
     * @param id 태스크 ID
     * @return HTTP 204 상태 코드 (No Content)
     */
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("DELETE /api/tasks/{} - 태스크 삭제 요청", id);

        // SecurityContext에서 인증된 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        taskService.deleteTask(id, userId);

        return ResponseEntity.noContent().build();
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
}
