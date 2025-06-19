package pluto.upik.domain.vote.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pluto.upik.domain.user.data.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 투표 엔티티
 * 사용자의 투표 정보를 저장하는 엔티티입니다.
 */
@Entity
@Table(name = "vote")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    /**
     * 투표 ID (기본 키)
     */
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * 투표 생성자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User user;

    /**
     * 투표 질문
     */
    @Column(columnDefinition = "TEXT")
    private String question;

    /**
     * 투표 카테고리
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum ('A', 'B', 'C')")
    private Category category;

    /**
     * 투표 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum ('OPEN', 'CLOSED')")
    private Status status;

    /**
     * 투표 상태를 설정합니다.
     * 
     * @param status 설정할 상태
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * 투표 종료 일시
     * 이 시간이 지나면 가이드 생성 대상이 됩니다.
     */
    @Column
    private LocalDate finishedAt;

    /**
     * 가이드 생성 여부
     * 투표 종료 후 가이드가 생성되었는지 여부를 나타냅니다.
     */
    @Transient
    private boolean guideGenerated;

    /**
     * 투표 카테고리 열거형
     */
    public enum Category {
        A, B, C
    }

    /**
     * 투표 상태 열거형
     */
    public enum Status {
        OPEN, CLOSED
    }




    /**
     * 가이드 생성 완료 표시
     * 가이드 생성이 완료되었을 때 호출합니다.
     */
    public void markGuideAsGenerated() {
        this.guideGenerated = true;
    }

    /**
     * 투표가 종료되었는지 확인
     * 
     * @param currentDate 현재 날짜
     * @return 투표 종료 여부
     */
    public boolean isFinished(LocalDate currentDate) {
        return currentDate.isAfter(finishedAt);
    }
}
