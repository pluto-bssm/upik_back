package pluto.upik.domain.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.report.data.DTO.*;
import pluto.upik.shared.service.AsyncNotificationService;
import pluto.upik.shared.util.LoggingUtils;
import pluto.upik.shared.util.ValidationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final AsyncNotificationService notificationService;

    @Override
    @Transactional
    public void deleteReport(UUID userId, UUID targetId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("targetId", targetId);

        LoggingUtils.logOperation(log, "deleteReport", params, () -> {
            ValidationUtils.validateCondition(userId != null, () ->
                new IllegalArgumentException("사용자 ID는 필수입니다."));
            ValidationUtils.validateCondition(targetId != null, () ->
                new IllegalArgumentException("대상 ID는 필수입니다."));
        reportCommandService.deleteReport(userId, targetId);
            return null;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByUser(UUID userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        return LoggingUtils.logOperation(log, "getReportsByUser", params, () -> {
            ValidationUtils.validateCondition(userId != null, () ->
                new IllegalArgumentException("사용자 ID는 필수입니다."));
        return reportQueryService.getReportsByUser(userId);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByTarget(UUID targetId) {
        Map<String, Object> params = new HashMap<>();
        params.put("targetId", targetId);

        return LoggingUtils.logOperation(log, "getReportsByTarget", params, () -> {
            ValidationUtils.validateCondition(targetId != null, () ->
                new IllegalArgumentException("대상 ID는 필수입니다."));
        return reportQueryService.getReportsByTarget(targetId);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports() {
        return LoggingUtils.logOperation(log, "getAllReports", new HashMap<>(),
            reportQueryService::getAllReports);
    }

    @Override
    @Transactional
    public AcceptGuideReportResponse acceptGuideReport(AcceptGuideReportRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", request.getUserId());
        params.put("guideId", request.getGuideId());

        return LoggingUtils.logOperation(log, "acceptGuideReport", params, () -> {
            ValidationUtils.validateCondition(request.getUserId() != null, () ->
                new IllegalArgumentException("사용자 ID는 필수입니다."));
            ValidationUtils.validateCondition(request.getGuideId() != null, () ->
                new IllegalArgumentException("가이드 ID는 필수입니다."));
            AcceptGuideReportResponse response = reportCommandService.acceptGuideReport(request);

            // 비동기 알림 전송
            notificationService.sendInAppNotification(
                request.getUserId(),
                "GUIDE_REPORT_ACCEPTED",
                "가이드 신고가 수락되었습니다.",
                request.getGuideId()
            );

            return response;
        });
    }

    @Override
    @Transactional
    public QuestionReportResponse reportQuestion(QuestionReportRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", request.getUserId());
        params.put("questionId", request.getQuestionId());

        return LoggingUtils.logOperation(log, "reportQuestion", params, () -> {
            ValidationUtils.validateCondition(request.getUserId() != null, () ->
                new IllegalArgumentException("사용자 ID는 필수입니다."));
            ValidationUtils.validateCondition(request.getQuestionId() != null, () ->
                new IllegalArgumentException("질문 ID는 필수입니다."));
            ValidationUtils.validateCondition(request.getReason() != null && !request.getReason().isEmpty(), () ->
                new IllegalArgumentException("신고 사유는 필수입니다."));
            QuestionReportResponse response = reportCommandService.reportQuestion(request);

            // 비동기 알림 전송
            notificationService.saveInAppNotification(
                request.getUserId(),
                "QUESTION_REPORTED",
                "질문이 신고되었습니다.",
                request.getQuestionId()
            );

            return response;
        });
    }

    @Override
    @Transactional
    public QuestionReportResponse rejectQuestionReport(RejectQuestionReportRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", request.getUserId());
        params.put("questionId", request.getQuestionId());

        return LoggingUtils.logOperation(log, "rejectQuestionReport", params, () -> {
            ValidationUtils.validateCondition(request.getUserId() != null, () ->
                new IllegalArgumentException("사용자 ID는 필수입니다."));
            ValidationUtils.validateCondition(request.getQuestionId() != null, () ->
                new IllegalArgumentException("질문 ID는 필수입니다."));
            QuestionReportResponse response = reportCommandService.rejectQuestionReport(request);

            // 비동기 알림 전송
            notificationService.saveInAppNotification(
                request.getUserId(),
                "QUESTION_REPORT_REJECTED",
                "질문 신고가 거부되었습니다.",
                request.getQuestionId()
            );

            return response;
        });
    }

    @Override
    @Transactional
    public QuestionReportResponse acceptQuestionReport(AcceptQuestionReportRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", request.getUserId());
        params.put("questionId", request.getQuestionId());

        return LoggingUtils.logOperation(log, "acceptQuestionReport", params, () -> {
            ValidationUtils.validateCondition(request.getUserId() != null, () ->
                new IllegalArgumentException("사용자 ID는 필수입니다."));
            ValidationUtils.validateCondition(request.getQuestionId() != null, () ->
                new IllegalArgumentException("질문 ID는 필수입니다."));
            QuestionReportResponse response = reportCommandService.acceptQuestionReport(request);

            // 비동기 알림 전송
            notificationService.saveInAppNotification(
                request.getUserId(),
                "QUESTION_REPORT_ACCEPTED",
                "질문 신고가 수락되었습니다.",
                request.getQuestionId()
            );

            return response;
        });
    }
}