package pluto.upik.domain.guide.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pluto.upik.domain.vote.data.model.Vote;

import java.time.LocalDate;
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
@ToString(exclude = "vote") // 순환 참조 방지를 위해 vote 필드 제외
public class Guide {

    /**
     * 가이드 ID (기본 키)
     */
    @Id
    @Column(columnDefinition = "uuid")
    @GeneratedValue
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
    @Column(columnDefinition = "TEXT", nullable = false)
    private String title;

    /**
     * 가이드 내용
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 생성 일시
     */
    @Column(nullable = false)
    private LocalDate createdAt;

    /**
     * 카테고리
     */
    @Column(length = 50)
    private String category;

    /**
     * 가이드 타입
     */
    @Column(name = "guide_type", length = 50)
    private String guideType;

    /**
     * 재투표 수
     */
    @Column(nullable = false)
    private Long revoteCount;

    /**
     * 좋아요 수
     */
    @Column(name = "`like`", nullable = false)
    private Long like;

    /**
     * 엔티티 생성 전 호출되는 메서드
     * 생성 일시를 현재 날짜로 설정합니다.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
        
        // null 값 방지
        if (this.revoteCount == null) {
            this.revoteCount = 0L;
        }
        
        if (this.like == null) {
            this.like = 0L;
        }
    }

    /**
     * 재투표 수 증가
     */
    public void incrementRevoteCount() {
        this.revoteCount++;
}

    /**
     * 재투표 수 감소
     */
    public void decrementRevoteCount() {
        if (this.revoteCount > 0) {
            this.revoteCount--;
        }
    }

    /**
     * 좋아요 수 증가
     */
    public void incrementLikeCount() {
        this.like++;
    }

    /**
     * 좋아요 수 감소
     */
    public void decrementLikeCount() {
        if (this.like > 0) {
            this.like--;
        }
    }

    /**
     * 카테고리 업데이트
     */
    public void updateCategory(String category) {
        this.category = category;
    }

    /**
     * 가이드 타입 업데이트
     */
    public void updateGuideType(String guideType) {
        this.guideType = guideType;
    }

    /**
     * 가이드 제목 및 내용 업데이트
     */
    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
