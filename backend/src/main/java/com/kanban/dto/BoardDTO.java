package com.kanban.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 보드 DTO (Data Transfer Object)
 *
 * 클라이언트와 서버 간 보드 데이터를 전송하기 위한 객체입니다.
 * 엔티티와 클라이언트 사이의 데이터 변환 계층 역할을 합니다.
 *
 * 용도:
 * - 보드 생성 요청 (Request)
 * - 보드 수정 요청 (Request)
 * - 보드 조회 응답 (Response)
 *
 * API 엔드포인트:
 * - POST /api/boards (요청)
 * - PUT /api/boards/{id} (요청)
 * - GET /api/boards (응답)
 * - GET /api/boards/{id} (응답)
 *
 * @author 메가존 클라우드 인턴십
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {

    /**
     * 보드 ID
     * 응답 시에만 사용 (생성 요청 시에는 null)
     */
    private Long id;

    /**
     * 보드 제목
     * 필수 입력 필드
     */
    @NotBlank(message = "보드 제목은 필수입니다")
    @Size(min = 1, max = 100, message = "보드 제목은 1자 이상 100자 이하여야 합니다")
    private String title;

    /**
     * 보드 설명
     * 선택 입력 필드
     */
    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    private String description;

    /**
     * 보드 소유자 ID
     * 요청 시 현재 로그인한 사용자 ID로 자동 설정
     */
    private Long userId;

    /**
     * 보드 생성 일시
     * 응답 시에만 사용
     */
    private LocalDateTime createdAt;

    /**
     * 보드 수정 일시
     * 응답 시에만 사용
     */
    private LocalDateTime updatedAt;

    /**
     * 보드에 포함된 태스크 개수
     * 응답 시 추가 정보로 제공
     */
    private Integer taskCount;

    /**
     * 생성 요청용 DTO
     *
     * 보드 생성 시 필요한 최소 정보만 포함합니다.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "보드 제목은 필수입니다")
        @Size(min = 1, max = 100, message = "보드 제목은 1자 이상 100자 이하여야 합니다")
        private String title;

        @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
        private String description;
    }

    /**
     * 수정 요청용 DTO
     *
     * 보드 수정 시 변경 가능한 필드만 포함합니다.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        @NotBlank(message = "보드 제목은 필수입니다")
        @Size(min = 1, max = 100, message = "보드 제목은 1자 이상 100자 이하여야 합니다")
        private String title;

        @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
        private String description;
    }
}
