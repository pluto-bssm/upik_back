package pluto.upik.domain.report.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pluto.upik.domain.report.data.DTO.*;
import pluto.upik.domain.report.service.ReportServiceInterface;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.util.Collections;
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

        try {
            // Service 호출
            reportService.deleteReport(userId, targetId);

            log.info("신고 거부 처리 완료 - userId: {}, targetId: {}", userId, targetId);
            return "거부 성공";
        } catch (ResourceNotFoundException | BusinessException e) {
            // 이미 로깅된 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            log.error("신고 거부 처리 중 예상치 못한 오류 - userId: {}, targetId: {}, error: {}", 
                    userId, targetId, e.getMessage(), e);
            throw new BusinessException("신고 거부 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportResponse> getReportsByUser(UUID userId) {
        log.info("사용자별 신고 목록 조회 요청 - userId: {}", userId);
        
        try {
            List<ReportResponse> reports = reportService.getReportsByUser(userId);
            log.info("사용자별 신고 목록 조회 완료 - userId: {}, 결과 개수: {}", userId, reports.size());
            return reports;
        } catch (ResourceNotFoundException | BusinessException e) {
            // 이미 로깅된 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            log.error("사용자별 신고 목록 조회 중 예상치 못한 오류 - userId: {}, error: {}", 
                    userId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportResponse> getReportsByTarget(UUID targetId) {
        log.info("대상별 신고 목록 조회 요청 - targetId: {}", targetId);
        
        try {
            List<ReportResponse> reports = reportService.getReportsByTarget(targetId);
            log.info("대상별 신고 목록 조회 완료 - targetId: {}, 결과 개수: {}", targetId, reports.size());
            return reports;
        } catch (ResourceNotFoundException | BusinessException e) {
            // 이미 로깅된 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            log.error("대상별 신고 목록 조회 중 예상치 못한 오류 - targetId: {}, error: {}", 
                    targetId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReportResponse> getAllReports() {
        log.info("모든 신고 목록 조회 요청");
        
        try {
            List<ReportResponse> reports = reportService.getAllReports();
            log.info("모든 신고 목록 조회 완료 - 결과 개수: {}", reports.size());
            return reports;
        } catch (ResourceNotFoundException | BusinessException e) {
            // 이미 로깅된 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            log.error("모든 신고 목록 조회 중 예상치 못한 오류 - error: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AcceptGuideReportResponse acceptGuideReport(AcceptGuideReportRequest request) {
        log.info("가이드 신고 수락 요청 - userId: {}, guideId: {}", request.getUserId(), request.getGuideId());
        
        try {
            AcceptGuideReportResponse response = reportService.acceptGuideReport(request);
            log.info("가이드 신고 수락 완료 - userId: {}, guideId: {}, 성공 여부: {}",
                    request.getUserId(), request.getGuideId(), response.isSuccess());
            return response;
        } catch (ResourceNotFoundException | BusinessException e) {
            // 이미 로깅된 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            log.error("가이드 신고 수락 중 예상치 못한 오류 - userId: {}, guideId: {}, error: {}", 
                    request.getUserId(), request.getGuideId(), e.getMessage(), e);
            return AcceptGuideReportResponse.builder()
                    .success(false)
                    .message("가이드 신고 수락 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionReportResponse reportQuestion(QuestionReportRequest request) {
        log.info("질문 신고 요청 - userId: {}, questionId: {}", request.getUserId(), request.getQuestionId());
        
        try {
            QuestionReportResponse response = reportService.reportQuestion(request);
            log.info("질문 신고 완료 - userId: {}, questionId: {}, 성공 여부: {}",
                    request.getUserId(), request.getQuestionId(), response.isSuccess());
            return response;
        } catch (ResourceNotFoundException | BusinessException e) {
            // 이미 로깅된 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            log.error("질문 신고 중 예상치 못한 오류 - userId: {}, questionId: {}, error: {}", 
                    request.getUserId(), request.getQuestionId(), e.getMessage(), e);
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("질문 신고 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionReportResponse rejectQuestionReport(RejectQuestionReportRequest request) {
        log.info("질문 신고 거부 요청 - userId: {}, questionId: {}", request.getUserId(), request.getQuestionId());
        
        try {
            QuestionReportResponse response = reportService.rejectQuestionReport(request);
            log.info("질문 신고 거부 완료 - userId: {}, questionId: {}, 성공 여부: {}",
                    request.getUserId(), request.getQuestionId(), response.isSuccess());
            return response;
        } catch (ResourceNotFoundException | BusinessException e) {
            // 이미 로깅된 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            log.error("질문 신고 거부 중 예상치 못한 오류 - userId: {}, questionId: {}, error: {}", 
                    request.getUserId(), request.getQuestionId(), e.getMessage(), e);
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("질문 신고 거부 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionReportResponse acceptQuestionReport(AcceptQuestionReportRequest request) {
        log.info("질문 신고 수락 요청 - userId: {}, questionId: {}", request.getUserId(), request.getQuestionId());
        
        try {
            QuestionReportResponse response = reportService.acceptQuestionReport(request);
            log.info("질문 신고 수락 완료 - userId: {}, questionId: {}, 성공 여부: {}",
                    request.getUserId(), request.getQuestionId(), response.isSuccess());
            return response;
        } catch (ResourceNotFoundException | BusinessException e) {
            // 이미 로깅된 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            log.error("질문 신고 수락 중 예상치 못한 오류 - userId: {}, questionId: {}, error: {}", 
                    request.getUserId(), request.getQuestionId(), e.getMessage(), e);
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("질문 신고 수락 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }
}