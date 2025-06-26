package pluto.upik.domain.tail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.tail.data.model.Tail;
import pluto.upik.domain.vote.data.model.Vote;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 테일 레포지토리
 * 테일 엔티티에 대한 데이터베이스 접근을 제공합니다.
 */
@Repository
public interface TailRepository extends JpaRepository<Tail, UUID> {

    /**
     * 특정 투표에 속한 테일 목록을 조회합니다.
     *
     * @param voteId 투표 ID
     * @return 투표에 속한 테일 목록
     */
    List<Tail> findByVoteId(UUID voteId);

    // Vote 별로 첫 번째 Tail 조회
    Optional<Tail> findFirstByVote(Vote vote);
}
