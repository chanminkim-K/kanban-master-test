package com.kanban.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
 * 사용자 엔티티
 *
 * 칸반 보드 애플리케이션의 사용자를 나타냅니다.
 * 사용자는 여러 개의 보드와 태스크를 생성하고 관리할 수 있습니다.
 *
 * 기능:
 * - 사용자 인증 (사용자명, 이메일, 비밀번호)
 * - Board와 일대다 관계
 * - 자동 타임스탬프 관리 (생성일시, 수정일시)
 *
 * @author 메가존 클라우드 인턴십
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 3, max = 50, message = "사용자명은 3자 이상 50자 이하여야 합니다")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이어야 합니다")
    @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Board> boards = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 사용자의 보드 목록에 보드를 추가하는 헬퍼 메서드입니다.
     * 양방향 관계를 유지합니다.
     *
     * @param board 추가할 보드
     */
    public void addBoard(Board board) {
        boards.add(board);
        board.setUser(this);
    }

    /**
     * 사용자의 보드 목록에서 보드를 제거하는 헬퍼 메서드입니다.
     * 양방향 관계를 유지합니다.
     *
     * @param board 제거할 보드
     */
    public void removeBoard(Board board) {
        boards.remove(board);
        board.setUser(null);
    }
}
