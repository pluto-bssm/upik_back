package pluto.upik.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pluto.upik.domain.report.data.model.Report;
import pluto.upik.domain.report.data.model.ReportId;

import java.util.List;
import java.util.UUID;

/**
 * 신고 관련 데이터 액세스를 위한 리포지토리
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, ReportId> {
    
    /**
     * 특정 사용자가 신고한 목록을 조회
     *
     * @param userId 조회할 사용자 ID
     * @return 신고 목록
     */
    List<Report> findByUserId(UUID userId);

    /**
     * 특정 대상에 대한 신고 목록을 조회
     *
     * @param targetId 조회할 대상 ID
     * @return 신고 목록
     */
    List<Report> findByTargetId(UUID targetId);

    /**
     * 특정 사용자의 특정 대상에 대한 신고 존재 여부 확인
     *
     * @param userId   확인할 사용자 ID
     * @param targetId 확인할 대상 ID
     * @return 존재 여부
     */
    boolean existsByUserIdAndTargetId(UUID userId, UUID targetId);

    /**
     * 특정 사용자의 특정 대상에 대한 신고를 삭제
     *
     * @param userId   삭제할 신고의 사용자 ID
     * @param targetId 삭제할 신고의 대상 ID
     */
    @Modifying
    @Query("DELETE FROM Report r WHERE r.userId = :userId AND r.targetId = :targetId")
    void deleteByUserIdAndTargetId(@Param("userId") UUID userId, @Param("targetId") UUID targetId);

    /**
     * 특정 대상에 대한 모든 신고를 삭제
     *
     * @param targetId 삭제할 신고의 대상 ID
     */
    @Modifying
    @Query("DELETE FROM Report r WHERE r.targetId = :targetId")
    void deleteByTargetId(@Param("targetId") UUID targetId);
}