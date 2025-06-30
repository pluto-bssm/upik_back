package pluto.upik.domain.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.domain.report.data.DTO.ReportResponse;
import pluto.upik.domain.report.data.model.Report;
import pluto.upik.domain.report.repository.ReportRepository;
import pluto.upik.shared.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 신고 조회 관련 비즈니스 로직을 처리하는 서비스 구현체
 * ReportService에서 조회 기능을 분리하여 단일 책임 원칙을 준수합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportQueryService {

    private final ReportRepository reportRepository;
    private final GuideRepository guideRepository;

    /**
     * 사용자가 작성한 신고 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 신고 응답 목록
     */
    public List<ReportResponse> getReportsByUser(UUID userId) {
        log.debug("사용자 신고 목록 조회 시작 - userId: {}", userId);

        List<Report> reportList = reportRepository.findByUserId(userId);
        
        if (reportList.isEmpty()) {
            log.info("사용자 신고 내역 없음 - userId: {}", userId);
            return new ArrayList<>();
        }

        List<ReportResponse> reports = mapToReportResponses(reportList);
        log.debug("사용자 신고 목록 조회 완료 - userId: {}, 결과 개수: {}", userId, reports.size());
        
        return reports;
    }

    /**
     * 특정 대상에 대한 신고 목록을 조회합니다.
     *
     * @param targetId 대상 ID
     * @return 신고 응답 목록
     */
    public List<ReportResponse> getReportsByTarget(UUID targetId) {
        log.debug("대상별 신고 목록 조회 시작 - targetId: {}", targetId);

        List<Report> reportList = reportRepository.findByTargetId(targetId);
        
        if (reportList.isEmpty()) {
            log.info("대상별 신고 내역 없음 - targetId: {}", targetId);
            return new ArrayList<>();
        }

        List<ReportResponse> reports = mapToReportResponses(reportList);
        
        // 대상 유형 설정 (가이드 또는 투표)
        setTargetTypes(reports);
        
        log.debug("대상별 신고 목록 조회 완료 - targetId: {}, 결과 개수: {}", targetId, reports.size());
        return reports;
    }

    /**
     * 모든 신고 목록을 조회합니다.
     *
     * @return 신고 응답 목록
     */
    public List<ReportResponse> getAllReports() {
        log.debug("모든 신고 목록 조회 시작");

        List<Report> reportList = reportRepository.findAll();
        
        if (reportList.isEmpty()) {
            log.info("신고 내역 없음");
            return new ArrayList<>();
        }

        List<ReportResponse> reports = mapToReportResponses(reportList);
        
        // 대상 유형 설정 (가이드 또는 투표)
        setTargetTypes(reports);
        
        log.debug("모든 신고 목록 조회 완료 - 결과 개수: {}", reports.size());
        return reports;
    }

    /**
     * Report 엔티티 목록을 ReportResponse DTO 목록으로 변환합니다.
     *
     * @param reportList 변환할 Report 엔티티 목록
     * @return 변환된 ReportResponse 객체 목록
     */
    private List<ReportResponse> mapToReportResponses(List<Report> reportList) {
        return reportList.stream()
                .map(report -> new ReportResponse(
                        report.getUserId(),
                        report.getTargetId(),
                        report.getReason(),
                        "",  // targetType은 별도로 설정
                        report.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * 신고 응답 목록의 대상 유형을 설정합니다.
     * 
     * @param reports 대상 유형을 설정할 신고 응답 목록
     */
    private void setTargetTypes(List<ReportResponse> reports) {
        for (ReportResponse report : reports) {
            boolean isGuide = guideRepository.existsById(report.getTargetId());
            report.setTargetType(isGuide ? "guide" : "vote");
        }
    }
}