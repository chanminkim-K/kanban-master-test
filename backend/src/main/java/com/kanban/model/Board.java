package com.kanban.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 보드 엔티티
 *
 * 애플리케이션의 칸반 보드를 나타냅니다.
 * 각 보드는 한 명의 사용자에게 속하며 여러 개의 태스크를 포함합니다.
 *
 * 기능:
 * - 보드 CRUD 작업 (생성, 조회, 수정, 삭제)
 * - User와 다대일 관계
 * - Task와 일대다 관계
 * - 자동 타임스탬프 관리
 *
 * API 엔드포인트:
 * - GET    /api/boards             # 모든 보드 조회
 * - GET    /api/boards/{id}        # 특정 보드 조회
 * - POST   /api/boards             # 보드 생성
 * - PUT    /api/boards/{id}        # 보드 수정
 * - DELETE /api/boards/{id}        # 보드 삭제
 *
 * @author 메가존 클라우드 인턴십
 */
@Entity
@Table(name = "boards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "보드 제목은 필수입니다")
    @Size(min = 1, max = 100, message = "보드 제목은 1자 이상 100자 이하여야 합니다")
    @Column(nullable = false, length = 100)
    private String title;

    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 보드의 태스크 목록에 태스크를 추가하는 헬퍼 메서드입니다.
     * 양방향 관계를 유지합니다.
     *
     * @param task 추가할 태스크
     */
    public void addTask(Task task) {
        tasks.add(task);
        task.setBoard(this);
    }

    /**
     * 보드의 태스크 목록에서 태스크를 제거하는 헬퍼 메서드입니다.
     * 양방향 관계를 유지합니다.
     *
     * @param task 제거할 태스크
     */
    public void removeTask(Task task) {
        tasks.remove(task);
        task.setBoard(null);
    }
}
