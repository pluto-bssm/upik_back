package pluto.upik.domain.tail.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pluto.upik.domain.vote.data.model.Vote;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 테일 엔티티
 * 투표에 포함된 테일(선택지) 정보를 저장하는 엔티티입니다.
 */
@Entity
@Table(name = "tail")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tail {

    /**
     * 테일 ID (기본 키)
     */
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * 테일이 속한 투표
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    /**
     * 테일 질문
     */
    @Column(columnDefinition = "TEXT")
    private String question;

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
