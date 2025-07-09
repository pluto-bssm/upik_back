package pluto.upik.domain.report.application;

import pluto.upik.domain.report.data.DTO.*;

import java.util.List;
import java.util.UUID;

/**
 * 신고 관련 애플리케이션 로직을 처리하는 인터페이스
 * 서비스 계층과 리졸버 계층 사이의 중간 계층으로 작동합니다.
 */
public interface ReportApplicationInterface {

    /**
     * 신고를 거부(삭제)하는 기능
     *
     * @param userId   신고한 사용자 ID
     * @param targetId 신고 대상 ID
     * @return 처리 결과 메시지
     */
    String rejectReport(UUID userId, UUID targetId);

    /**
     * 특정 사용자가 신고한 목록을 조회
     *
     * @param userId 조회할 사용자 ID
     * @return 신고 응답 목록
     */
    List<ReportResponse> getReportsByUser(UUID userId);

    /**
     * 특정 대상에 대한 신고 목록을 조회
     *
     * @param targetId 조회할 대상 ID
     * @return 신고 응답 목록
     */
    List<ReportResponse> getReportsByTarget(UUID targetId);

    /**
     * 모든 신고 목록을 조회
     *
     * @return 신고 응답 목록
     */
    List<ReportResponse> getAllReports();

    /**
     * 가이드 신고를 수락하고 새로운 질문을 생성
     *
     * @param request 가이드 신고 수락 요청 정보
     * @return 가이드 신고 수락 결과
     */
    AcceptGuideReportResponse acceptGuideReport(AcceptGuideReportRequest request);

    /**
     * 질문에 대한 신고를 등록
     *
     * @param request 질문 신고 요청 정보
     * @return 질문 신고 결과
     */
    QuestionReportResponse reportQuestion(QuestionReportRequest request);

    /**
     * 질문 신고를 거부
     *
     * @param request 질문 신고 거부 요청 정보
     * @return 질문 신고 거부 결과
     */
    QuestionReportResponse rejectQuestionReport(RejectQuestionReportRequest request);

    /**
     * 질문 신고를 수락하고 해당 질문을 삭제
     *
     * @param request 질문 신고 수락 요청 정보
     * @return 질문 신고 수락 결과
     */
    QuestionReportResponse acceptQuestionReport(AcceptQuestionReportRequest request);
}