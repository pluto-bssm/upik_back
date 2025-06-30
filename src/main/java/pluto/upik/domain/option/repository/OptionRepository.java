package pluto.upik.domain.option.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.vote.data.model.Vote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OptionRepository extends JpaRepository<Option, UUID> {
    // vote로 가장 첫번째 Option 가져오기 (정렬 기준에 따라 변경 가능)
    Optional<Option> findTopByVoteOrderByIdAsc(Vote vote);

    // voteId로 옵션 리스트 가져오기
    List<Option> findByVoteId(UUID voteId);

    // voteId로 옵션 삭제하기
    @Modifying
    @Query("DELETE FROM Option o WHERE o.vote.id = :voteId")
    void deleteByVoteId(@Param("voteId") UUID voteId);
}
