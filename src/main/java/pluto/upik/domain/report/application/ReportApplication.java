package pluto.upik.domain.report.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pluto.upik.domain.report.data.DTO.ReportResponse;
import pluto.upik.domain.report.service.ReportService;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportApplication {

    private final ReportService reportService;

    public String rejectReport(UUID userId, UUID targetId) {
        log.info("신고 거부 처리 시작 - userId: {}, targetId: {}", userId, targetId);

        // Service 호출
        reportService.deleteReport(userId, targetId);

        log.info("신고 거부 처리 완료 - userId: {}, targetId: {}", userId, targetId);
        return "거부 성공";
    }


    // 특정 사용자가 신고한 목록 조회
    public List<ReportResponse> getReportsByUser(UUID userId) {
        return reportService.getReportsByUser(userId);
    }

    // 특정 신고 대상의 신고 목록 조회
    public List<ReportResponse> getReportsByTarget(UUID targetId) {
        return reportService.getReportsByTarget(targetId);
    }

    // 모든 신고 목록 조회
    public List<ReportResponse> getAllReports() {
        return reportService.getAllReports();
    }
}

