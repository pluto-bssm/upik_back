package pluto.upik.domain.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.voteResponse.data.model.VoteResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * 투표 레포지토리
 * 투표 엔티티에 대한 데이터베이스 접근을 제공합니다.
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {

    /**
     * 종료 날짜가 지났고 상태가 OPEN인 투표 목록을 조회합니다.
     *
     * @param currentDate 현재 날짜
     * @return 가이드 생성이 필요한 투표 목록
     */
    @Query("SELECT v FROM Vote v WHERE v.finishedAt <= :currentDate AND v.status = pluto.upik.domain.vote.data.model.Vote.Status.OPEN")
    List<Vote> findFinishedVotesWithoutGuide(LocalDate currentDate);

    /**
     * 특정 사용자가 생성한 투표 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자가 생성한 투표 목록
     */
    List<Vote> findByUserId(UUID userId);

    /**
     * 종료 날짜가 지나지 않은 투표 목록을 조회합니다.
     *
     * @param currentDate 현재 날짜
     * @return 진행 중인 투표 목록
     */
    @Query("SELECT v FROM Vote v WHERE v.finishedAt > :currentDate")
    List<Vote> findActiveVotes(LocalDate currentDate);


    List<Vote> findByFinishedAtBefore(LocalDate date);
}
