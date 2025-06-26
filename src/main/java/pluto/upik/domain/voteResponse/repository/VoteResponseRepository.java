package pluto.upik.domain.voteResponse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pluto.upik.domain.voteResponse.data.model.VoteResponse;

import java.util.List;
import java.util.UUID;

public interface VoteResponseRepository extends JpaRepository<VoteResponse, Long> {
    List<VoteResponse> findByVoteId(UUID voteId);
}
