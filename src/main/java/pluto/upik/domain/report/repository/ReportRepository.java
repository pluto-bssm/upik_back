package pluto.upik.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pluto.upik.domain.report.data.model.Report;

import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    boolean existsByUserIdAndTargetId(UUID userId, UUID targetId);

    void deleteByUserIdAndTargetId(UUID userId, UUID targetId);
}
