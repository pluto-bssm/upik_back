package pluto.upik.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pluto.upik.domain.report.data.model.Report;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    boolean existsByUserIdAndTargetId(UUID userId, UUID targetId);
    // 특정 사용자가 신고한 목록 조회
    List<Report> findByUserId(UUID userId);

    // 특정 신고 대상의 신고 목록 조회
    List<Report> findByTargetId(UUID targetId);

    void deleteByUserIdAndTargetId(UUID userId, UUID targetId);
}
