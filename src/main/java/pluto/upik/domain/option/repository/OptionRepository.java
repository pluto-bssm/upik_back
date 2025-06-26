package pluto.upik.domain.option.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.vote.data.model.Vote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OptionRepository extends JpaRepository<Option, Long> {
    // vote로 가장 첫번째 Option 가져오기 (정렬 기준에 따라 변경 가능)
    Optional<Option> findTopByVoteOrderByIdAsc(Vote vote);

    // voteId로 옵션 리스트 가져오기
    List<Option> findByVoteId(UUID voteId);
}
