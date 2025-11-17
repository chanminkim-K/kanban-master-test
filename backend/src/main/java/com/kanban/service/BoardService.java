package com.kanban.service;

import com.kanban.dto.BoardDTO;
import com.kanban.model.Board;
import com.kanban.model.User;
import com.kanban.repository.BoardRepository;
import com.kanban.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 보드 서비스
 *
 * 보드 관련 비즈니스 로직을 처리하는 서비스 계층입니다.
 * Controller와 Repository 사이에서 중재자 역할을 수행합니다.
 *
 * 주요 기능:
 * - 보드 생성
 * - 보드 조회 (전체, 단건)
 * - 보드 수정
 * - 보드 삭제
 * - Entity <-> DTO 변환
 *
 * API 엔드포인트와의 매핑:
 * - POST /api/boards -> createBoard()
 * - GET /api/boards -> getAllBoards()
 * - GET /api/boards/{id} -> getBoardById()
 * - PUT /api/boards/{id} -> updateBoard()
 * - DELETE /api/boards/{id} -> deleteBoard()
 *
 * @author 메가존 클라우드 인턴십
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * 새로운 보드를 생성합니다.
     *
     * @param request 보드 생성 요청 DTO
     * @param userId 보드를 생성하는 사용자 ID
     * @return 생성된 보드 DTO
     * @throws RuntimeException 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public BoardDTO createBoard(BoardDTO.CreateRequest request, Long userId) {
        log.info("보드 생성 시작 - 제목: {}, 사용자 ID: {}", request.getTitle(), userId);

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        // 보드 엔티티 생성
        Board board = Board.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .build();

        // 보드 저장
        Board savedBoard = boardRepository.save(board);
        log.info("보드 생성 완료 - ID: {}", savedBoard.getId());

        return convertToDTO(savedBoard);
    }

    /**
     * 특정 사용자의 모든 보드를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 보드 DTO 목록
     */
    public List<BoardDTO> getAllBoards(Long userId) {
        log.info("사용자 보드 목록 조회 - 사용자 ID: {}", userId);

        List<Board> boards = boardRepository.findByUserIdOrderByCreatedAtDesc(userId);
        log.info("보드 목록 조회 완료 - 개수: {}", boards.size());

        return boards.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 보드를 ID로 조회합니다.
     *
     * @param id 보드 ID
     * @return 보드 DTO
     * @throws RuntimeException 보드를 찾을 수 없는 경우
     */
    public BoardDTO getBoardById(Long id) {
        log.info("보드 조회 - ID: {}", id);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("보드를 찾을 수 없습니다. ID: " + id));

        return convertToDTO(board);
    }

    /**
     * 보드 정보를 수정합니다.
     *
     * @param id 보드 ID
     * @param request 보드 수정 요청 DTO
     * @param userId 요청한 사용자 ID
     * @return 수정된 보드 DTO
     * @throws RuntimeException 보드를 찾을 수 없거나 권한이 없는 경우
     */
    @Transactional
    public BoardDTO updateBoard(Long id, BoardDTO.UpdateRequest request, Long userId) {
        log.info("보드 수정 시작 - ID: {}, 사용자 ID: {}", id, userId);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("보드를 찾을 수 없습니다. ID: " + id));

        // 권한 확인 (보드 소유자만 수정 가능)
        if (!board.getUser().getId().equals(userId)) {
            throw new RuntimeException("보드를 수정할 권한이 없습니다.");
        }

        // 보드 정보 수정
        board.setTitle(request.getTitle());
        board.setDescription(request.getDescription());

        Board updatedBoard = boardRepository.save(board);
        log.info("보드 수정 완료 - ID: {}", updatedBoard.getId());

        return convertToDTO(updatedBoard);
    }

    /**
     * 보드를 삭제합니다.
     *
     * @param id 보드 ID
     * @param userId 요청한 사용자 ID
     * @throws RuntimeException 보드를 찾을 수 없거나 권한이 없는 경우
     */
    @Transactional
    public void deleteBoard(Long id, Long userId) {
        log.info("보드 삭제 시작 - ID: {}, 사용자 ID: {}", id, userId);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("보드를 찾을 수 없습니다. ID: " + id));

        // 권한 확인 (보드 소유자만 삭제 가능)
        if (!board.getUser().getId().equals(userId)) {
            throw new RuntimeException("보드를 삭제할 권한이 없습니다.");
        }

        boardRepository.delete(board);
        log.info("보드 삭제 완료 - ID: {}", id);
    }

    /**
     * Board 엔티티를 BoardDTO로 변환합니다.
     *
     * @param board 보드 엔티티
     * @return 보드 DTO
     */
    private BoardDTO convertToDTO(Board board) {
        return BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .description(board.getDescription())
                .userId(board.getUser().getId())
                .taskCount(board.getTasks() != null ? board.getTasks().size() : 0)
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}
