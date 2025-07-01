package pluto.upik.domain.option.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pluto.upik.domain.vote.data.model.Vote;

import java.util.UUID;

/**
 * 투표 옵션 엔티티
 * 투표에 포함된 선택지 정보를 저장합니다.
 */
@Entity
@Table(name = "`option`") // 'option'은 SQL 예약어이므로 백틱 사용
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "vote") // 순환 참조 방지
public class Option {

    /**
     * 옵션 ID (기본 키)
     */
    @Id
    @Column(columnDefinition = "uuid")
    @GeneratedValue
    private UUID id;

    /**
     * 옵션이 속한 투표
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    /**
     * 옵션 내용
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 옵션 순서
     */
    @Column
    private Integer sequence;

    /**
     * 옵션 내용 변경 메서드
     */
    public void updateContent(String content) {
        this.content = content;
    }

    /**
     * 옵션 순서 변경 메서드
     */
    public void updateSequence(Integer sequence) {
        this.sequence = sequence;
    }
}