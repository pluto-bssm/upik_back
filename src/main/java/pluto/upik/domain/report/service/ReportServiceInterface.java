package pluto.upik.domain.report.service;

import pluto.upik.domain.report.data.DTO.*;

import java.util.List;
import java.util.UUID;

/**
 * 신고 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface ReportServiceInterface {

    /**
     * 특정 사용자의 특정 대상에 대한 신고를 삭제합니다.
     *
     * @param userId   신고를 한 사용자 ID
     * @param targetId 신고 대상 ID
     * @throws pluto.upik.shared.exception.ResourceNotFoundException 해당 신고가 존재하지 않을 경우 발생
     * @throws pluto.upik.shared.exception.BusinessException 신고 삭제 중 오류 발생 시
     */
    void deleteReport(UUID userId, UUID targetId);

    /**
     * 특정 사용자가 신고한 목록을 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 신고 응답 목록
     * @throws pluto.upik.shared.exception.ResourceNotFoundException 해당 사용자의 신고가 없을 경우 발생
     * @throws pluto.upik.shared.exception.BusinessException 조회 중 오류 발생 시
     */
    List<ReportResponse> getReportsByUser(UUID userId);

    /**
     * 특정 대상에 대한 신고 목록을 조회합니다.
     *
     * @param targetId 조회할 대상 ID
     * @return 신고 응답 목록
     * @throws pluto.upik.shared.exception.ResourceNotFoundException 해당 대상에 대한 신고가 없을 경우 발생
     * @throws pluto.upik.shared.exception.BusinessException 조회 중 오류 발생 시
     */
    List<ReportResponse> getReportsByTarget(UUID targetId);

    /**
     * 모든 신고 목록을 조회합니다.
     *
     * @return 신고 응답 목록
     * @throws pluto.upik.shared.exception.ResourceNotFoundException 신고 내역이 존재하지 않을 경우 발생
     * @throws pluto.upik.shared.exception.BusinessException 조회 중 오류 발생 시
     */
    List<ReportResponse> getAllReports();
    
    /**
     * 가이드 신고를 수락하고 새로운 질문을 생성합니다.
     *
     * @param request 가이드 신고 수락 요청 정보
     * @return 가이드 신고 수락 결과
     * @throws pluto.upik.shared.exception.ResourceNotFoundException 해당 신고가 존재하지 않을 경우 발생
     * @throws pluto.upik.shared.exception.BusinessException 처리 중 오류 발생 시
     */
    AcceptGuideReportResponse acceptGuideReport(AcceptGuideReportRequest request);
    
    /**
     * 질문에 대한 신고를 등록합니다.
     *
     * @param request 질문 신고 요청 정보
     * @return 질문 신고 결과
     * @throws pluto.upik.shared.exception.BusinessException 처리 중 오류 발생 시
     */
    QuestionReportResponse reportQuestion(QuestionReportRequest request);
    
    /**
     * 질문 신고를 거부합니다.
     *
     * @param request 질문 신고 거부 요청 정보
     * @return 질문 신고 거부 결과
     * @throws pluto.upik.shared.exception.ResourceNotFoundException 해당 신고가 존재하지 않을 경우 발생
     * @throws pluto.upik.shared.exception.BusinessException 처리 중 오류 발생 시
     */
    QuestionReportResponse rejectQuestionReport(RejectQuestionReportRequest request);
    
    /**
     * 질문 신고를 수락하고 해당 질문을 삭제합니다.
     *
     * @param request 질문 신고 수락 요청 정보
     * @return 질문 신고 수락 결과
     * @throws pluto.upik.shared.exception.ResourceNotFoundException 해당 신고가 존재하지 않을 경우 발생
     * @throws pluto.upik.shared.exception.BusinessException 처리 중 오류 발생 시
     */
    QuestionReportResponse acceptQuestionReport(AcceptQuestionReportRequest request);
}