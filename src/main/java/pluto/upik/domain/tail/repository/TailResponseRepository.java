package pluto.upik.domain.tail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.tail.data.model.Tail;
import pluto.upik.domain.tail.data.model.TailResponse;

import java.util.List;
import java.util.UUID;

/**
 * 테일 응답 레포지토리
 * 테일 응답 엔티티에 대한 데이터베이스 접근을 제공합니다.
 */
@Repository
public interface TailResponseRepository extends JpaRepository<TailResponse, UUID> {

    /**
     * 특정 테일에 대한 응답 목록을 조회합니다.
     *
     * @param tailId 테일 ID
     * @return 테일에 대한 응답 목록
     */
    List<TailResponse> findByTailId(UUID tailId);

    /**
     * 특정 사용자가 작성한 응답 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자가 작성한 응답 목록
     */
    List<TailResponse> findByUserId(UUID userId);

    /**
     * 특정 투표에 속한 테일들에 대한 응답 목록을 조회합니다.
     *
     * @param voteId 투표 ID
     * @return 투표에 속한 테일들에 대한 응답 목록
     */
    @Query("SELECT tr FROM TailResponse tr JOIN tr.tail t WHERE t.vote.id = :voteId")
    List<TailResponse> findByVoteId(UUID voteId);

    /**
     * 특정 테일에 대한 응답 수를 조회합니다.
     *
     * @param tailId 테일 ID
     * @return 응답 수
     */
    long countByTailId(UUID tailId);


    // Tail 별 TailResponse 리스트 조회
    List<TailResponse> findByTail(Tail tail);
}
