package pluto.upik.domain.voteResponse.data.model;

import jakarta.persistence.*;
import lombok.*;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.user.data.model.User;
import pluto.upik.domain.vote.data.model.Vote;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vote_response")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponse {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private Option option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @Column(name = "created_at")
    private LocalDate createdAt;
}
