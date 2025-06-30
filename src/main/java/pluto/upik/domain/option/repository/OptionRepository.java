package pluto.upik.domain.option.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.vote.data.model.Vote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OptionRepository extends JpaRepository<Option, UUID> {
    List<Option> findByVoteId(UUID voteId);

    // 추가된 메서드
    Optional<Option> findTopByVoteOrderByIdAsc(Vote vote);
}
