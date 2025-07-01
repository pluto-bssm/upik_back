package pluto.upik.domain.option.data.model;

import jakarta.persistence.*;
import lombok.*;
import pluto.upik.domain.vote.data.model.Vote;

import java.util.UUID;

/**
 * 투표 옵션 엔티티
 */
@Entity
@Table(name = "option")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Option {

    /**
     * 옵션 ID
     */
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * 옵션이 속한 투표
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    /**
     * 옵션 내용
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;


    /**
     * 옵션 내용 변경 메서드
     */
    public void updateContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Option{" +
                "id=" + id +
                ", vote=" + vote +
                ", content='" + content + '\'' +
                '}';
    }
}