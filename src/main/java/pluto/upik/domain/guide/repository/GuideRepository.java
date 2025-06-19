package pluto.upik.domain.guide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.guide.data.model.Guide;

import java.util.Optional;
import java.util.UUID;

/**
 * 가이드 레포지토리
 * 가이드 엔티티에 대한 데이터베이스 접근을 제공합니다.
 */
@Repository
public interface GuideRepository extends JpaRepository<Guide, UUID> {

    /**
     * 특정 투표에 대한 가이드를 조회합니다.
     *
     * @param voteId 투표 ID
     * @return 가이드 (존재하지 않을 경우 빈 Optional)
     */
    Optional<Guide> findByVoteId(UUID voteId);

    /**
     * 특정 투표에 대한 가이드가 존재하는지 확인합니다.
     *
     * @param voteId 투표 ID
     * @return 존재 여부
     */
    boolean existsByVoteId(UUID voteId);
}
