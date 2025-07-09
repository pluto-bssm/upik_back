package pluto.upik.domain.guide.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.guide.data.model.GuideAndUser;
import pluto.upik.domain.guide.data.model.GuideAndUserId;

import java.util.List;
import java.util.UUID;

/**
 * 가이드와 사용자 간의 관계 레포지토리
 * 사용자가 가이드에 좋아요를 누른 관계 등을 관리합니다.
 */
@Repository
public interface GuideAndUserRepository extends JpaRepository<GuideAndUser, GuideAndUserId> {
    
    /**
     * 특정 사용자가 좋아요한 모든 가이드 관계를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자가 좋아요한 가이드 관계 목록
     */
    @Query("SELECT g FROM GuideAndUser g WHERE g.id.userId = :userId")
    List<GuideAndUser> findAllByUserId(@Param("userId") UUID userId);
    
    /**
     * 특정 가이드에 좋아요한 모든 사용자 관계를 조회합니다.
     *
     * @param guideId 가이드 ID
     * @return 가이드에 좋아요한 사용자 관계 목록
     */
    @Query("SELECT g FROM GuideAndUser g WHERE g.id.guideId = :guideId")
    List<GuideAndUser> findAllByGuideId(@Param("guideId") UUID guideId);
    
    /**
     * 특정 가이드에 좋아요한 사용자 수를 조회합니다.
     *
     * @param guideId 가이드 ID
     * @return 좋아요한 사용자 수
     */
    @Query("SELECT COUNT(g) FROM GuideAndUser g WHERE g.id.guideId = :guideId")
    long countByGuideId(@Param("guideId") UUID guideId);
}