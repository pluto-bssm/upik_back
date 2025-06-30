package pluto.upik.domain.report.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.domain.report.data.DTO.*;
import pluto.upik.domain.report.data.model.Report;
import pluto.upik.domain.report.repository.ReportRepository;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.vote.repository.VoteRepository;
import pluto.upik.domain.voteResponse.repository.VoteResponseRepository;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 신고 생성, 수정, 삭제 관련 비즈니스 로직을 처리하는 서비스 구현체
 * ReportService에서 명령 기능을 분리하여 단일 책임 원칙을 준수합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportCommandService {

    private final ReportRepository reportRepository;
    private final GuideRepository guideRepository;
    private final VoteRepository voteRepository;
    private final VoteResponseRepository voteResponseRepository;

    /**
     * 신고를 삭제합니다.
     *
     * @param userId 사용자 ID
     * @param targetId 대상 ID
     */
    @Transactional
    public void deleteReport(UUID userId, UUID targetId) {
        log.debug("신고 삭제 시작 - userId: {}, targetId: {}", userId, targetId);

        // 존재 여부 확인
        if (!reportRepository.existsByUserIdAndTargetId(userId, targetId)) {
            log.warn("신고 삭제 실패 - 신고가 존재하지 않음 (userId: {}, targetId: {})", userId, targetId);
            throw new ResourceNotFoundException("해당 신고가 존재하지 않습니다.");
        }

        // 삭제
        reportRepository.deleteByUserIdAndTargetId(userId, targetId);
        log.info("신고 데이터 삭제 완료 - userId: {}, targetId: {}", userId, targetId);

        // 가이드의 revote 카운트 감소 시도
        try {
            if (guideRepository.existsById(targetId)) {
                guideRepository.decrementRevoteCount(targetId);
                log.debug("가이드 revote 카운트 감소 완료 - targetId: {}", targetId);
            }
        } catch (Exception e) {
            log.error("가이드 revote 카운트 감소 중 오류 - targetId: {}, error: {}", targetId, e.getMessage());
            throw new BusinessException("가이드 revote 카운트 감소 중 오류가 발생했습니다.");
        }
    }

    /**
     * 가이드 신고를 수락합니다.
     *
     * @param request 가이드 신고 수락 요청
     * @return 가이드 신고 수락 응답
     */
    @Transactional
    public AcceptGuideReportResponse acceptGuideReport(AcceptGuideReportRequest request) {
        UUID userId = request.getUserId();
        UUID guideId = request.getGuideId();
        
        log.debug("가이드 신고 수락 시작 - userId: {}, guideId: {}", userId, guideId);

        // 신고 존재 여부 확인
        if (!reportRepository.existsByUserIdAndTargetId(userId, guideId)) {
            log.warn("가이드 신고 수락 실패 - 신고가 존재하지 않음 (userId: {}, guideId: {})", userId, guideId);
            throw new ResourceNotFoundException("해당 신고가 존재하지 않습니다.");
        }

        // 가이드 조회
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> {
                    log.warn("가이드 신고 수락 실패 - 가이드가 존재하지 않음 (guideId: {})", guideId);
                    return new ResourceNotFoundException("해당 가이드가 존재하지 않습니다.");
                });

        // 연결된 질문 확인
        Vote vote = guide.getVote();
        if (vote == null) {
            log.warn("가이드 신고 수락 실패 - 가이드에 연결된 질문이 없음 (guideId: {})", guideId);
            throw new BusinessException("가이드에 연결된 질문이 없습니다.");
        }

        // 질문 상태를 OPEN으로 설정하고 종료일을 2일 후로 설정
        vote.setStatus(Vote.Status.OPEN);
        LocalDate twoLaterDate = LocalDate.now().plusDays(2);
        vote.setFinishedAt(twoLaterDate);
        voteRepository.save(vote);

        // 신고 삭제
        reportRepository.deleteByUserIdAndTargetId(userId, guideId);

        log.info("가이드 신고 수락 완료 - userId: {}, guideId: {}, 새 질문 ID: {}", userId, guideId, vote.getId());

        return AcceptGuideReportResponse.builder()
                .message("가이드 신고가 수락되었습니다. 질문이 다시 열렸습니다.")
                .newQuestionId(vote.getId())
                .success(true)
                .build();
    }

    /**
     * 질문을 신고합니다.
     *
     * @param request 질문 신고 요청
     * @return 질문 신고 응답
     */
    @Transactional
    public QuestionReportResponse reportQuestion(QuestionReportRequest request) {
        UUID userId = request.getUserId();
        UUID questionId = request.getQuestionId();
        
        log.debug("질문 신고 시작 - userId: {}, questionId: {}", userId, questionId);

        // 질문 존재 여부 확인
        if (!voteRepository.existsById(questionId)) {
            log.warn("질문 신고 실패 - 질문이 존재하지 않음 (questionId: {})", questionId);
            throw new ResourceNotFoundException("해당 질문이 존재하지 않습니다.");
        }

        // 이미 신고한 경우 체크
        if (reportRepository.existsByUserIdAndTargetId(userId, questionId)) {
            log.warn("질문 신고 실패 - 이미 신고한 질문 (userId: {}, questionId: {})", userId, questionId);
            throw new BusinessException("이미 신고한 질문입니다.");
        }

        // 신고 생성
        Report report = Report.builder()
                .userId(userId)
                .targetId(questionId)
                .reason(request.getReason())
                .createdAt(LocalDate.now())
                .build();

        reportRepository.save(report);

        log.info("질문 신고 완료 - userId: {}, questionId: {}", userId, questionId);

        return QuestionReportResponse.builder()
                .message("질문 신고가 접수되었습니다.")
                .success(true)
                .build();
    }

    /**
     * 질문 신고를 거부합니다.
     *
     * @param request 질문 신고 거부 요청
     * @return 질문 신고 거부 응답
     */
    @Transactional
    public QuestionReportResponse rejectQuestionReport(RejectQuestionReportRequest request) {
        UUID userId = request.getUserId();
        UUID questionId = request.getQuestionId();
        
        log.debug("질문 신고 거부 시작 - userId: {}, questionId: {}", userId, questionId);

        // 신고 존재 여부 확인
        if (!reportRepository.existsByUserIdAndTargetId(userId, questionId)) {
            log.warn("질문 신고 거부 실패 - 신고가 존재하지 않음 (userId: {}, questionId: {})", userId, questionId);
            throw new ResourceNotFoundException("해당 신고가 존재하지 않습니다.");
        }

        // 신고 삭제
        reportRepository.deleteByUserIdAndTargetId(userId, questionId);

        log.info("질문 신고 거부 완료 - userId: {}, questionId: {}", userId, questionId);

        return QuestionReportResponse.builder()
                .message("질문 신고가 거부되었습니다.")
                .success(true)
                .build();
    }

    /**
     * 질문 신고를 수락합니다.
     *
     * @param request 질문 신고 수락 요청
     * @return 질문 신고 수락 응답
     */
    @Transactional
    public QuestionReportResponse acceptQuestionReport(AcceptQuestionReportRequest request) {
        UUID userId = request.getUserId();
        UUID questionId = request.getQuestionId();
        
        log.debug("질문 신고 수락 시작 - userId: {}, questionId: {}", userId, questionId);

        // 신고 존재 여부 확인
        if (!reportRepository.existsByUserIdAndTargetId(userId, questionId)) {
            log.warn("질문 신고 수락 실패 - 신고가 존재하지 않음 (userId: {}, questionId: {})", userId, questionId);
            throw new ResourceNotFoundException("해당 신고가 존재하지 않습니다.");
        }

        // 질문 존재 여부 확인
        if (!voteRepository.existsById(questionId)) {
            log.warn("질문 신고 수락 실패 - 질문이 존재하지 않음 (questionId: {})", questionId);
            throw new ResourceNotFoundException("해당 질문이 존재하지 않습니다.");
        }

        // 트랜잭션 내에서 순차적으로 삭제 처리
        deleteVoteResponses(questionId);
        deleteVote(questionId);
        deleteReportsForTarget(questionId);

        log.info("질문 신고 수락 완료 - userId: {}, questionId: {}", userId, questionId);

        return QuestionReportResponse.builder()
                .message("질문 신고가 수락되었습니다. 질문이 삭제되었습니다.")
                .success(true)
                .build();
    }

    /**
     * 질문에 대한 응답을 삭제합니다.
     *
     * @param questionId 질문 ID
     */
    private void deleteVoteResponses(UUID questionId) {
        try {
            log.debug("질문 관련 응답 삭제 시작 - questionId: {}", questionId);
            voteResponseRepository.deleteByVoteId(questionId);
            log.debug("질문 관련 응답 삭제 완료 - questionId: {}", questionId);
        } catch (Exception e) {
            log.error("질문 관련 응답 삭제 중 오류 - questionId: {}, error: {}", questionId, e.getMessage());
            throw new BusinessException("질문 관련 응답 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 질문을 삭제합니다.
     *
     * @param questionId 질문 ID
     */
    private void deleteVote(UUID questionId) {
        try {
            log.debug("질문 삭제 시작 - questionId: {}", questionId);
            voteRepository.deleteById(questionId);
            log.debug("질문 삭제 완료 - questionId: {}", questionId);
        } catch (Exception e) {
            log.error("질문 삭제 중 오류 - questionId: {}, error: {}", questionId, e.getMessage());
            throw new BusinessException("질문 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 대상에 대한 모든 신고를 삭제합니다.
     *
     * @param targetId 대상 ID
     */
    private void deleteReportsForTarget(UUID targetId) {
        try {
            log.debug("대상 관련 신고 삭제 시작 - targetId: {}", targetId);
            reportRepository.deleteByTargetId(targetId);
            log.debug("대상 관련 신고 삭제 완료 - targetId: {}", targetId);
        } catch (Exception e) {
            log.error("대상 관련 신고 삭제 중 오류 - targetId: {}, error: {}", targetId, e.getMessage());
            throw new BusinessException("대상 관련 신고 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}