package com.kanban.service;

import com.kanban.dto.TaskDTO;
import com.kanban.model.Board;
import com.kanban.model.Task;
import com.kanban.repository.BoardRepository;
import com.kanban.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 태스크 서비스
 *
 * 태스크 관련 비즈니스 로직을 처리하는 서비스입니다.
 *
 * 주요 기능:
 * - 태스크 생성: 보드에 새로운 태스크 추가
 * - 태스크 조회: 보드별 태스크 목록 조회, 개별 태스크 조회
 * - 태스크 수정: 제목, 설명 수정
 * - 태스크 상태 변경: 할 일 → 진행 중 → 완료
 * - 태스크 위치 변경: 드래그 앤 드롭으로 순서 변경
 * - 태스크 삭제: 태스크 제거
 *
 * 보안:
 * - 보드 소유자만 해당 보드의 태스크를 수정/삭제할 수 있음
 *
 * @author 메가존 클라우드 인턴십
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;

    /**
     * 새로운 태스크를 생성합니다.
     *
     * 처리 과정:
     * 1. 보드 존재 여부 확인
     * 2. 보드 소유자 권한 확인
     * 3. 태스크 엔티티 생성 및 저장
     * 4. DTO로 변환하여 반환
     *
     * @param boardId 보드 ID
     * @param request 태스크 생성 요청 DTO
     * @param userId 사용자 ID (JWT에서 추출)
     * @return 생성된 태스크 정보
     * @throws RuntimeException 보드를 찾을 수 없거나 권한이 없는 경우
     */
    @Transactional
    public TaskDTO.TaskResponse createTask(Long boardId, TaskDTO.CreateRequest request, Long userId) {
        log.info("태스크 생성 시작 - 보드 ID: {}, 제목: {}", boardId, request.getTitle());

        // 1. 보드 존재 여부 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("보드를 찾을 수 없습니다. ID: " + boardId));

        // 2. 보드 소유자 권한 확인
        if (!board.getUser().getId().equals(userId)) {
            throw new RuntimeException("보드에 대한 권한이 없습니다. 보드 ID: " + boardId);
        }

        // 3. 태스크 엔티티 생성 및 저장
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .position(request.getPosition())
                .board(board)
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("태스크 생성 완료 - 태스크 ID: {}, 제목: {}", savedTask.getId(), savedTask.getTitle());

        // 4. DTO로 변환하여 반환
        return convertToDTO(savedTask);
    }

    /**
     * 특정 보드의 모든 태스크를 조회합니다.
     *
     * @param boardId 보드 ID
     * @param userId 사용자 ID (JWT에서 추출)
     * @return 태스크 목록 (위치 순으로 정렬)
     * @throws RuntimeException 보드를 찾을 수 없거나 권한이 없는 경우
     */
    public List<TaskDTO.TaskResponse> getTasksByBoardId(Long boardId, Long userId) {
        log.info("태스크 목록 조회 - 보드 ID: {}", boardId);

        // 보드 존재 여부 및 권한 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("보드를 찾을 수 없습니다. ID: " + boardId));

        if (!board.getUser().getId().equals(userId)) {
            throw new RuntimeException("보드에 대한 권한이 없습니다. 보드 ID: " + boardId);
        }

        // 태스크 조회 및 DTO 변환
        List<Task> tasks = taskRepository.findByBoardIdOrderByPositionAsc(boardId);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 태스크를 ID로 조회합니다.
     *
     * @param taskId 태스크 ID
     * @param userId 사용자 ID (JWT에서 추출)
     * @return 태스크 정보
     * @throws RuntimeException 태스크를 찾을 수 없거나 권한이 없는 경우
     */
    public TaskDTO.TaskResponse getTaskById(Long taskId, Long userId) {
        log.info("태스크 조회 - 태스크 ID: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("태스크를 찾을 수 없습니다. ID: " + taskId));

        // 권한 확인
        if (!task.getBoard().getUser().getId().equals(userId)) {
            throw new RuntimeException("태스크에 대한 권한이 없습니다. 태스크 ID: " + taskId);
        }

        return convertToDTO(task);
    }

    /**
     * 태스크 정보를 수정합니다 (제목, 설명).
     *
     * @param taskId 태스크 ID
     * @param request 태스크 수정 요청 DTO
     * @param userId 사용자 ID (JWT에서 추출)
     * @return 수정된 태스크 정보
     * @throws RuntimeException 태스크를 찾을 수 없거나 권한이 없는 경우
     */
    @Transactional
    public TaskDTO.TaskResponse updateTask(Long taskId, TaskDTO.UpdateRequest request, Long userId) {
        log.info("태스크 수정 - 태스크 ID: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("태스크를 찾을 수 없습니다. ID: " + taskId));

        // 권한 확인
        if (!task.getBoard().getUser().getId().equals(userId)) {
            throw new RuntimeException("태스크에 대한 권한이 없습니다. 태스크 ID: " + taskId);
        }

        // 태스크 정보 업데이트
        task.updateInfo(request.getTitle(), request.getDescription());

        log.info("태스크 수정 완료 - 태스크 ID: {}", taskId);
        return convertToDTO(task);
    }

    /**
     * 태스크의 상태를 변경합니다 (할 일, 진행 중, 완료).
     *
     * @param taskId 태스크 ID
     * @param request 상태 변경 요청 DTO
     * @param userId 사용자 ID (JWT에서 추출)
     * @return 수정된 태스크 정보
     * @throws RuntimeException 태스크를 찾을 수 없거나 권한이 없는 경우
     */
    @Transactional
    public TaskDTO.TaskResponse updateTaskStatus(Long taskId, TaskDTO.UpdateStatusRequest request, Long userId) {
        log.info("태스크 상태 변경 - 태스크 ID: {}, 새 상태: {}", taskId, request.getStatus());

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("태스크를 찾을 수 없습니다. ID: " + taskId));

        // 권한 확인
        if (!task.getBoard().getUser().getId().equals(userId)) {
            throw new RuntimeException("태스크에 대한 권한이 없습니다. 태스크 ID: " + taskId);
        }

        // 상태 및 위치 업데이트
        task.updateStatus(request.getStatus());
        task.updatePosition(request.getPosition());

        log.info("태스크 상태 변경 완료 - 태스크 ID: {}, 새 상태: {}", taskId, request.getStatus());
        return convertToDTO(task);
    }

    /**
     * 태스크의 위치를 변경합니다 (드래그 앤 드롭).
     *
     * @param taskId 태스크 ID
     * @param request 위치 변경 요청 DTO
     * @param userId 사용자 ID (JWT에서 추출)
     * @return 수정된 태스크 정보
     * @throws RuntimeException 태스크를 찾을 수 없거나 권한이 없는 경우
     */
    @Transactional
    public TaskDTO.TaskResponse updateTaskPosition(Long taskId, TaskDTO.UpdatePositionRequest request, Long userId) {
        log.info("태스크 위치 변경 - 태스크 ID: {}, 새 위치: {}", taskId, request.getPosition());

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("태스크를 찾을 수 없습니다. ID: " + taskId));

        // 권한 확인
        if (!task.getBoard().getUser().getId().equals(userId)) {
            throw new RuntimeException("태스크에 대한 권한이 없습니다. 태스크 ID: " + taskId);
        }

        // 위치 업데이트
        task.updatePosition(request.getPosition());

        log.info("태스크 위치 변경 완료 - 태스크 ID: {}, 새 위치: {}", taskId, request.getPosition());
        return convertToDTO(task);
    }

    /**
     * 태스크를 삭제합니다.
     *
     * @param taskId 태스크 ID
     * @param userId 사용자 ID (JWT에서 추출)
     * @throws RuntimeException 태스크를 찾을 수 없거나 권한이 없는 경우
     */
    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        log.info("태스크 삭제 - 태스크 ID: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("태스크를 찾을 수 없습니다. ID: " + taskId));

        // 권한 확인
        if (!task.getBoard().getUser().getId().equals(userId)) {
            throw new RuntimeException("태스크에 대한 권한이 없습니다. 태스크 ID: " + taskId);
        }

        taskRepository.delete(task);
        log.info("태스크 삭제 완료 - 태스크 ID: {}", taskId);
    }

    /**
     * Task 엔티티를 TaskDTO.TaskResponse로 변환합니다.
     *
     * @param task 태스크 엔티티
     * @return 태스크 응답 DTO
     */
    private TaskDTO.TaskResponse convertToDTO(Task task) {
        return TaskDTO.TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .position(task.getPosition())
                .boardId(task.getBoard().getId())
                .boardTitle(task.getBoard().getTitle())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
