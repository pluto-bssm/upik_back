package pluto.upik.domain.report.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pluto.upik.domain.report.data.DTO.*;
import pluto.upik.domain.report.service.ReportServiceInterface;

import java.util.List;
import java.util.UUID;

/**
 * 신고 관련 애플리케이션 로직을 처리하는 구현체
 * 서비스 계층과 리졸버 계층 사이의 중간 계층으로 작동합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReportApplication implements ReportApplicationInterface {

    private final ReportServiceInterface reportService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String rejectReport(UUID userId, UUID targetId) {
        log.info("신고 거부 처리 시작 - userId: {}, targetId: {}", userId, targetId);

        // Service 호출
        reportService.deleteReport(userId, targetId);

        log.info("신고 거부 처리 완료 - userId: {}, targetId: {}", userId, targetId);
        return "거부 성공";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportResponse> getReportsByUser(UUID userId) {
        log.info("사용자별 신고 목록 조회 요청 - userId: {}", userId);
        List<ReportResponse> reports = reportService.getReportsByUser(userId);
        log.info("사용자별 신고 목록 조회 완료 - userId: {}, 결과 개수: {}", userId, reports.size());
        return reports;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportResponse> getReportsByTarget(UUID targetId) {
        log.info("대상별 신고 목록 조회 요청 - targetId: {}", targetId);
        List<ReportResponse> reports = reportService.getReportsByTarget(targetId);
        log.info("대상별 신고 목록 조회 완료 - targetId: {}, 결과 개수: {}", targetId, reports.size());
        return reports;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportResponse> getAllReports() {
        log.info("모든 신고 목록 조회 요청");
        List<ReportResponse> reports = reportService.getAllReports();
        log.info("모든 신고 목록 조회 완료 - 결과 개수: {}", reports.size());
        return reports;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AcceptGuideReportResponse acceptGuideReport(AcceptGuideReportRequest request) {
        log.info("가이드 신고 수락 요청 - userId: {}, guideId: {}", request.getUserId(), request.getGuideId());
        AcceptGuideReportResponse response = reportService.acceptGuideReport(request);
        log.info("가이드 신고 수락 완료 - userId: {}, guideId: {}, 성공 여부: {}",
                request.getUserId(), request.getGuideId(), response.isSuccess());
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionReportResponse reportQuestion(QuestionReportRequest request) {
        log.info("질문 신고 요청 - userId: {}, questionId: {}", request.getUserId(), request.getQuestionId());
        QuestionReportResponse response = reportService.reportQuestion(request);
        log.info("질문 신고 완료 - userId: {}, questionId: {}, 성공 여부: {}",
                request.getUserId(), request.getQuestionId(), response.isSuccess());
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionReportResponse rejectQuestionReport(RejectQuestionReportRequest request) {
        log.info("질문 신고 거부 요청 - userId: {}, questionId: {}", request.getUserId(), request.getQuestionId());
        QuestionReportResponse response = reportService.rejectQuestionReport(request);
        log.info("질문 신고 거부 완료 - userId: {}, questionId: {}, 성공 여부: {}",
                request.getUserId(), request.getQuestionId(), response.isSuccess());
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionReportResponse acceptQuestionReport(AcceptQuestionReportRequest request) {
        log.info("질문 신고 수락 요청 - userId: {}, questionId: {}", request.getUserId(), request.getQuestionId());
        QuestionReportResponse response = reportService.acceptQuestionReport(request);
        log.info("질문 신고 수락 완료 - userId: {}, questionId: {}, 성공 여부: {}",
                request.getUserId(), request.getQuestionId(), response.isSuccess());
        return response;
    }
}