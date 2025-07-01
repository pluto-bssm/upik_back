package pluto.upik.domain.voteResponse.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.user.data.model.User;
import pluto.upik.domain.vote.data.model.Vote;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 투표 응답 엔티티
 * 사용자가 제출한 투표 응답 정보를 저장합니다.
 */
@Entity
@Table(name = "vote_response")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "vote", "selectedOption"}) // 순환 참조 방지
public class VoteResponse {

    /**
     * 투표 응답 ID (기본 키)
     */
    @Id
    @Column(columnDefinition = "uuid")
    @GeneratedValue
    private UUID id;

    /**
     * 응답한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 응답한 투표
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    /**
     * 선택한 옵션
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private Option selectedOption;

    /**
     * 생성 일시
     */
    @Column
    private LocalDate createdAt;

    /**
     * 엔티티 생성 전 호출되는 메서드
     * 생성 일시를 현재 날짜로 설정합니다.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }

    /**
     * 선택한 옵션 변경
     */
    public void updateSelectedOption(Option option) {
        this.selectedOption = option;
    }
}
