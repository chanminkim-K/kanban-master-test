package com.kanban.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 태스크 엔티티
 *
 * 칸반 보드 내의 태스크를 나타냅니다.
 * 각 태스크는 하나의 보드에 속하며 상태(할 일, 진행 중, 완료)를 가집니다.
 *
 * 기능:
 * - 태스크 CRUD 작업 (생성, 조회, 수정, 삭제)
 * - 상태 관리 (TO_DO, IN_PROGRESS, DONE)
 * - Board와 다대일 관계
 * - 자동 타임스탬프 관리
 *
 * API 엔드포인트:
 * - GET    /api/boards/{boardId}/tasks       # 보드의 모든 태스크 조회
 * - GET    /api/tasks/{id}                   # 특정 태스크 조회
 * - POST   /api/boards/{boardId}/tasks       # 태스크 생성
 * - PUT    /api/tasks/{id}                   # 태스크 수정
 * - PATCH  /api/tasks/{id}/status            # 태스크 상태 변경
 * - DELETE /api/tasks/{id}                   # 태스크 삭제
 *
 * @author 메가존 클라우드 인턴십
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "태스크 제목은 필수입니다")
    @Size(min = 1, max = 200, message = "태스크 제목은 1자 이상 200자 이하여야 합니다")
    @Column(nullable = false, length = 200)
    private String title;

    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "태스크 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TaskStatus status = TaskStatus.TO_DO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    @JsonIgnore
    private Board board;

    @Column(name = "position")
    private Integer position;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 태스크 정보를 업데이트합니다 (제목, 설명).
     *
     * @param title 새 제목
     * @param description 새 설명
     */
    public void updateInfo(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /**
     * 태스크 상태를 변경합니다.
     *
     * @param status 새 상태
     */
    public void updateStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * 태스크 위치를 변경합니다.
     *
     * @param position 새 위치
     */
    public void updatePosition(Integer position) {
        this.position = position;
    }

    /**
     * 태스크 상태 Enum
     *
     * 칸반 워크플로우에서 태스크의 현재 상태를 나타냅니다.
     *
     * - TO_DO: 아직 시작하지 않은 태스크
     * - IN_PROGRESS: 현재 진행 중인 태스크
     * - DONE: 완료된 태스크
     */
    public enum TaskStatus {
        TO_DO("할 일"),
        IN_PROGRESS("진행 중"),
        DONE("완료");

        private final String displayName;

        TaskStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
