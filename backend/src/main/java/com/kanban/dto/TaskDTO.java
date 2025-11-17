package com.kanban.dto;

import com.kanban.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 태스크 DTO (Data Transfer Object)
 *
 * 태스크 관련 데이터 전송 객체입니다.
 * 엔티티와 클라이언트 간의 데이터 전송을 위해 사용합니다.
 *
 * API 엔드포인트:
 * - POST /api/boards/{boardId}/tasks (CreateRequest)
 * - PUT /api/tasks/{id} (UpdateRequest)
 * - PATCH /api/tasks/{id}/status (UpdateStatusRequest)
 * - PATCH /api/tasks/{id}/position (UpdatePositionRequest)
 * - 응답: TaskDTO
 *
 * @author 메가존 클라우드 인턴십
 */
public class TaskDTO {

    /**
     * 태스크 생성 요청 DTO
     *
     * 새로운 태스크를 생성할 때 사용합니다.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {

        @NotBlank(message = "태스크 제목은 필수입니다")
        @Size(max = 200, message = "태스크 제목은 200자를 초과할 수 없습니다")
        private String title;

        @Size(max = 2000, message = "태스크 설명은 2000자를 초과할 수 없습니다")
        private String description;

        @NotNull(message = "태스크 상태는 필수입니다")
        private TaskStatus status;

        @NotNull(message = "태스크 위치는 필수입니다")
        private Integer position;
    }

    /**
     * 태스크 수정 요청 DTO
     *
     * 기존 태스크의 제목과 설명을 수정할 때 사용합니다.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {

        @NotBlank(message = "태스크 제목은 필수입니다")
        @Size(max = 200, message = "태스크 제목은 200자를 초과할 수 없습니다")
        private String title;

        @Size(max = 2000, message = "태스크 설명은 2000자를 초과할 수 없습니다")
        private String description;
    }

    /**
     * 태스크 상태 변경 요청 DTO
     *
     * 태스크의 상태(할 일, 진행 중, 완료)를 변경할 때 사용합니다.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateStatusRequest {

        @NotNull(message = "태스크 상태는 필수입니다")
        private TaskStatus status;

        @NotNull(message = "태스크 위치는 필수입니다")
        private Integer position;
    }

    /**
     * 태스크 위치 변경 요청 DTO
     *
     * 태스크의 순서를 변경할 때 사용합니다.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdatePositionRequest {

        @NotNull(message = "태스크 위치는 필수입니다")
        private Integer position;
    }

    /**
     * 태스크 응답 DTO
     *
     * 클라이언트로 태스크 정보를 반환할 때 사용합니다.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaskResponse {

        private Long id;
        private String title;
        private String description;
        private TaskStatus status;
        private Integer position;
        private Long boardId;
        private String boardTitle;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
