package pluto.upik.domain.guide.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pluto.upik.domain.vote.data.model.Vote;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 가이드 엔티티
 * AI가 생성한 가이드 정보를 저장하는 엔티티입니다.
 */
@Entity
@Table(name = "guide")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guide {

    /**
     * 가이드 ID (기본 키)
     */
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * 가이드가 생성된 투표
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    /**
     * 가이드 제목
     */
    @Column(columnDefinition = "TEXT")
    private String title;

    /**
     * 가이드 내용
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 생성 일시
     */
    @Column
    private LocalDate createdAt;

    /**
     * 카테고리
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum ('A', 'B', 'C')")
    private Vote.Category category;

    /**
     * 가이드 타입
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum ('TEXT', 'VIDEO')")
    private GuideType guideType;

    /**
     * 재투표 수
     */
    @Column
    private Long revoteCount;

    /**
     * 좋아요 수
     */
    @Column(name = "`like`")
    private Long like;

    /**
     * 가이드 타입 열거형
     */
    public enum GuideType {
        TEXT, VIDEO
    }

    /**
     * 수정 일시
     */
    @Transient
    private LocalDateTime updatedAt;

    /**
     * 엔티티 생성 전 호출되는 메서드
     * 생성 일시를 현재 날짜로 설정합니다.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }
}
