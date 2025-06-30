package pluto.upik.domain.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pluto.upik.domain.report.data.DTO.*;

import java.util.List;
import java.util.UUID;

/**
 * 신고 관련 비즈니스 로직을 처리하는 서비스 구현체
 * 기존 ReportService를 ReportQueryService와 ReportCommandService로 분리하고,
 * 이 클래스에서 두 서비스를 조합하여 ReportServiceInterface를 구현합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportServiceInterface {

    private final ReportQueryService reportQueryService;
    private final ReportCommandService reportCommandService;

    @Override
    public void deleteReport(UUID userId, UUID targetId) {
        log.info("신고 삭제 요청 - userId: {}, targetId: {}", userId, targetId);
        reportCommandService.deleteReport(userId, targetId);
    }

    @Override
    public List<ReportResponse> getReportsByUser(UUID userId) {
        log.info("사용자별 신고 목록 조회 요청 - userId: {}", userId);
        return reportQueryService.getReportsByUser(userId);
    }

    @Override
    public List<ReportResponse> getReportsByTarget(UUID targetId) {
        log.info("대상별 신고 목록 조회 요청 - targetId: {}", targetId);
        return reportQueryService.getReportsByTarget(targetId);
    }

    @Override
    public List<ReportResponse> getAllReports() {
        log.info("모든 신고 목록 조회 요청");
        return reportQueryService.getAllReports();
    }

    @Override
    public AcceptGuideReportResponse acceptGuideReport(AcceptGuideReportRequest request) {
        log.info("가이드 신고 수락 요청 - userId: {}, guideId: {}", request.getUserId(), request.getGuideId());
        return reportCommandService.acceptGuideReport(request);
    }

    @Override
    public QuestionReportResponse reportQuestion(QuestionReportRequest request) {
        log.info("질문 신고 요청 - userId: {}, questionId: {}", request.getUserId(), request.getQuestionId());
        return reportCommandService.reportQuestion(request);
    }

    @Override
    public QuestionReportResponse rejectQuestionReport(RejectQuestionReportRequest request) {
        log.info("질문 신고 거부 요청 - userId: {}, questionId: {}", request.getUserId(), request.getQuestionId());
        return reportCommandService.rejectQuestionReport(request);
    }

    @Override
    public QuestionReportResponse acceptQuestionReport(AcceptQuestionReportRequest request) {
        log.info("질문 신고 수락 요청 - userId: {}, questionId: {}", request.getUserId(), request.getQuestionId());
        return reportCommandService.acceptQuestionReport(request);
    }
}