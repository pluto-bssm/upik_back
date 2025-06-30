package pluto.upik.domain.voteResponse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pluto.upik.domain.voteResponse.data.model.VoteResponse;

import java.util.List;
import java.util.UUID;

public interface VoteResponseRepository extends JpaRepository<VoteResponse, Long> {
    List<VoteResponse> findByVoteId(UUID voteId);

    /**
     * 특정 질문(Vote)에 대한 모든 응답을 삭제합니다.
     *
     * @param voteId 삭제할 응답들이 속한 질문 ID
     */
    @Modifying
    @Query("DELETE FROM VoteResponse vr WHERE vr.vote.id = :voteId")
    void deleteByVoteId(@Param("voteId") UUID voteId);
}
