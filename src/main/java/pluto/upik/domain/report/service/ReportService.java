package pluto.upik.domain.report.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.domain.report.data.DTO.ReportResponse;
import pluto.upik.domain.report.data.model.Report;
import pluto.upik.domain.report.repository.ReportRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final GuideRepository guideRepository;

    @Transactional
    public void deleteReport(UUID userId, UUID targetId) {
        log.info("신고 삭제 요청 시작 - userId: {}, targetId: {}", userId, targetId);

        // 존재 여부 확인
        if (!reportRepository.existsByUserIdAndTargetId(userId, targetId)) {
            log.warn("신고 삭제 실패 - 신고가 존재하지 않음 (userId: {}, targetId: {})", userId, targetId);
            throw new IllegalArgumentException("해당 신고가 존재하지 않습니다.");
        }

        // 삭제
        reportRepository.deleteByUserIdAndTargetId(userId, targetId);
        log.info("신고 삭제 성공 - 신고 데이터 삭제 완료 (userId: {}, targetId: {})", userId, targetId);

        // 가이드의 revote 카운트 감소
        guideRepository.decrementRevoteCount(targetId);
        log.info("가이드 revote 카운트 감소 완료 - targetId: {}", targetId);
    }

    // 특정 사용자가 신고한 목록 조회
    public List<ReportResponse> getReportsByUser(UUID userId) {
        log.info("사용자 신고 목록 조회 요청 시작 - userId: {}", userId);

        List<ReportResponse> reports = reportRepository.findByUserId(userId).stream()
                .map(this::mapToReportResponse)
                .collect(Collectors.toList());

        log.info("사용자 신고 목록 조회 완료 - userId: {}, 결과 개수: {}", userId, reports.size());
        return reports;
    }

    // 특정 신고 대상의 신고 목록 조회
    public List<ReportResponse> getReportsByTarget(UUID targetId) {
        log.info("신고 대상 목록 조회 요청 시작 - targetId: {}", targetId);

        List<ReportResponse> reports = reportRepository.findByTargetId(targetId).stream()
                .map(this::mapToReportResponse)
                .collect(Collectors.toList());

        log.info("신고 대상 목록 조회 완료 - targetId: {}, 결과 개수: {}", targetId, reports.size());
        return reports;
    }

    // 모든 신고 목록 조회
    public List<ReportResponse> getAllReports() {
        log.info("모든 신고 목록 조회 요청 시작");

        List<ReportResponse> reports = reportRepository.findAll().stream()
                .map(this::mapToReportResponse)
                .collect(Collectors.toList());

        log.info("모든 신고 목록 조회 완료 - 결과 개수: {}", reports.size());
        return reports;
    }

    // Report 엔티티를 ReportResponse로 변환
    private ReportResponse mapToReportResponse(Report report) {
        log.debug("Report 엔티티를 ReportResponse로 변환 중 - reportId: {}", report.getUserId());

        ReportResponse response = new ReportResponse(
                report.getUserId(),
                report.getTargetId(),
                report.getReason(),
                report.getCreatedAt()
        );

        log.debug("Report 엔티티 변환 완료 - reportId: {}, response: {}", report.getUserId(), response);
        return response;
    }
}
