package com.kanban.controller;

import com.kanban.dto.BoardDTO;
import com.kanban.service.BoardService;
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
 * 보드 컨트롤러
 *
 * 보드 관련 REST API 엔드포인트를 제공하는 컨트롤러입니다.
 * 클라이언트의 HTTP 요청을 받아 BoardService로 전달하고 응답을 반환합니다.
 *
 * Base URL: /api/boards
 *
 * API 엔드포인트:
 * - POST   /api/boards              보드 생성 (JWT 인증 필요)
 * - GET    /api/boards              모든 보드 조회 (JWT 인증 필요)
 * - GET    /api/boards/{id}         특정 보드 조회 (JWT 인증 필요)
 * - PUT    /api/boards/{id}         보드 수정 (JWT 인증 필요)
 * - DELETE /api/boards/{id}         보드 삭제 (JWT 인증 필요)
 *
 * 인증:
 * - JWT 토큰을 Authorization 헤더에 포함하여 요청해야 합니다.
 * - 형식: "Authorization: Bearer {token}"
 * - SecurityContext에서 사용자 ID를 자동으로 추출합니다.
 *
 * @author 메가존 클라우드 인턴십
 */
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;

    /**
     * 새로운 보드를 생성합니다.
     *
     * JWT 토큰에서 사용자 ID를 추출하여 보드를 생성합니다.
     *
     * @param request 보드 생성 요청 DTO
     * @return 생성된 보드 정보와 HTTP 201 상태 코드
     */
    @PostMapping
    public ResponseEntity<BoardDTO> createBoard(
            @Valid @RequestBody BoardDTO.CreateRequest request) {
        log.info("POST /api/boards - 보드 생성 요청: {}", request.getTitle());

        // SecurityContext에서 인증된 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        BoardDTO createdBoard = boardService.createBoard(request, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdBoard);
    }

    /**
     * 특정 사용자의 모든 보드를 조회합니다.
     *
     * JWT 토큰에서 사용자 ID를 추출하여 해당 사용자의 보드 목록을 조회합니다.
     *
     * @return 보드 목록과 HTTP 200 상태 코드
     */
    @GetMapping
    public ResponseEntity<List<BoardDTO>> getAllBoards() {
        // SecurityContext에서 인증된 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        log.info("GET /api/boards - 보드 목록 조회 요청, 사용자 ID: {}", userId);

        List<BoardDTO> boards = boardService.getAllBoards(userId);

        return ResponseEntity.ok(boards);
    }

    /**
     * 특정 보드를 ID로 조회합니다.
     *
     * @param id 보드 ID
     * @return 보드 정보와 HTTP 200 상태 코드
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoardById(@PathVariable Long id) {
        log.info("GET /api/boards/{} - 보드 조회 요청", id);

        BoardDTO board = boardService.getBoardById(id);

        return ResponseEntity.ok(board);
    }

    /**
     * 보드 정보를 수정합니다.
     *
     * JWT 토큰에서 사용자 ID를 추출하여 권한을 확인하고 보드를 수정합니다.
     *
     * @param id 보드 ID
     * @param request 보드 수정 요청 DTO
     * @return 수정된 보드 정보와 HTTP 200 상태 코드
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoardDTO> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody BoardDTO.UpdateRequest request) {
        log.info("PUT /api/boards/{} - 보드 수정 요청", id);

        // SecurityContext에서 인증된 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        BoardDTO updatedBoard = boardService.updateBoard(id, request, userId);

        return ResponseEntity.ok(updatedBoard);
    }

    /**
     * 보드를 삭제합니다.
     *
     * JWT 토큰에서 사용자 ID를 추출하여 권한을 확인하고 보드를 삭제합니다.
     *
     * @param id 보드 ID
     * @return HTTP 204 상태 코드 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        log.info("DELETE /api/boards/{} - 보드 삭제 요청", id);

        // SecurityContext에서 인증된 사용자 ID 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        boardService.deleteBoard(id, userId);

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
