package pluto.upik.domain.voteResponse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pluto.upik.domain.voteResponse.data.model.VoteResponse;

import java.util.List;
import java.util.Optional;
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
    // 특정 사용자가 특정 투표에 이미 응답했는지 확인
    @Query("SELECT vr FROM VoteResponse vr WHERE vr.user.id = :userId AND vr.vote.id = :voteId")
    Optional<VoteResponse> findByUserIdAndVoteId(@Param("userId") UUID userId, @Param("voteId") UUID voteId);

    // 특정 투표의 총 응답 수
    @Query("SELECT COUNT(vr) FROM VoteResponse vr WHERE vr.vote.id = :voteId")
    Long countByVoteId(@Param("voteId") UUID voteId);

    // 특정 옵션의 응답 수
    @Query("SELECT COUNT(vr) FROM VoteResponse vr WHERE vr.selectedOption.id = :optionId")
    Long countByOptionId(@Param("optionId") UUID optionId);
}
