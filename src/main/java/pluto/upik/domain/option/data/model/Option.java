package pluto.upik.domain.option.data.model;

import jakarta.persistence.*;
import lombok.*;
import pluto.upik.domain.vote.data.model.Vote;

import java.util.UUID;

@Entity
@Table(name = "`option`") // 'option'은 SQL 예약어이므로 백틱 사용
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @Column(columnDefinition = "text")
    private String content;
}