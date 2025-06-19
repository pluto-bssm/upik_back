package pluto.upik.domain.tail.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pluto.upik.domain.user.data.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 테일 응답 엔티티
 * 사용자가 테일에 대해 응답한 내용을 저장하는 엔티티입니다.
 */
@Entity
@Table(name = "tail_response")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TailResponse {

    /**
     * 테일 응답 ID (기본 키)
     */
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * 응답한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 응답한 테일
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tail_id")
    private Tail tail;

    /**
     * 응답 내용
     */
    @Column(columnDefinition = "TEXT")
    private String answer;

    /**
     * 생성 일시
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 엔티티 생성 전 호출되는 메서드
     * 생성 일시와 수정 일시를 현재 시간으로 설정합니다.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 엔티티 수정 전 호출되는 메서드
     * 수정 일시를 현재 시간으로 설정합니다.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
