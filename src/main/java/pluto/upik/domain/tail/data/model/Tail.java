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

}
