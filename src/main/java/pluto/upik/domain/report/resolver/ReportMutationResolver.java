package pluto.upik.domain.report.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.report.application.ReportApplicationInterface;
import pluto.upik.domain.report.data.DTO.*;

import java.util.UUID;

/**
 * 신고 관련 GraphQL Mutation 리졸버
 * 신고 생성, 수정, 삭제 등의 작업을 처리합니다.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ReportMutationResolver {

    private final ReportApplicationInterface reportApplication;
    
    // 임시 사용자 ID (나중에 JWT를 통해 받을 예정)
    private final UUID dummyUserId = UUID.fromString("e49207e8-471a-11f0-937c-42010a800003");

    /**
     * 신고를 거부(삭제)하는 GraphQL 리졸버 메서드
     *
     * @param targetId 신고 대상 ID
     * @return 거부 결과 페이로드
     */
    @SchemaMapping(typeName = "ReportMutation", field = "rejectReport")
    public RejectReportPayload rejectReport(@Argument String targetId) {
        log.info("GraphQL 신고 거부 요청 - targetId: {}", targetId);

        try {
            UUID targetUUID = UUID.fromString(targetId);
            
            String result = reportApplication.rejectReport(dummyUserId, targetUUID);
            log.info("GraphQL 신고 거부 완료 - userId: {}, targetId: {}, result: {}", dummyUserId, targetId, result);
            return new RejectReportPayload(result);
        } catch (IllegalArgumentException e) {
            log.error("GraphQL 신고 거부 실패 - 잘못된 UUID 형식: userId: {}, targetId: {}, error: {}", dummyUserId, targetId, e.getMessage());
            // null을 반환하지 않고 오류 메시지를 포함한 객체 반환
            return new RejectReportPayload("잘못된 UUID 형식입니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("GraphQL 신고 거부 실패 - userId: {}, targetId: {}, error: {}", dummyUserId, targetId, e.getMessage(), e);
            // null을 반환하지 않고 오류 메시지를 포함한 객체 반환
            return new RejectReportPayload("신고 거부 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 가이드 신고를 수락하는 GraphQL 리졸버 메서드
     *
     * @param guideId 신고 대상 가이드 ID
     * @param userId 처리하는 사용자 ID (선택적)
     * @return 가이드 신고 수락 결과
     */
    @SchemaMapping(typeName = "ReportMutation", field = "acceptGuideReport")
    public AcceptGuideReportResponse acceptGuideReport(@Argument String guideId, @Argument(name = "userId") String userId) {
        UUID userUUID = (userId != null) ? UUID.fromString(userId) : dummyUserId;
        log.info("GraphQL 가이드 신고 수락 요청 - userId: {}, guideId: {}", userUUID, guideId);

        try {
            UUID guideUUID = UUID.fromString(guideId);

            AcceptGuideReportRequest request = AcceptGuideReportRequest.builder()
                    .userId(userUUID)
                    .guideId(guideUUID)
                    .build();

            AcceptGuideReportResponse response = reportApplication.acceptGuideReport(request);

            log.info("GraphQL 가이드 신고 수락 완료 - userId: {}, guideId: {}, success: {}",
                    userUUID, guideId, response.isSuccess());
            return response;
        } catch (IllegalArgumentException e) {
            log.error("GraphQL 가이드 신고 수락 실패 - 잘못된 UUID 형식: userId: {}, guideId: {}, error: {}",
                    userUUID, guideId, e.getMessage());
            // null을 반환하지 않고 오류 메시지를 포함한 객체 반환
            return AcceptGuideReportResponse.builder()
                    .success(false)
                    .message("잘못된 UUID 형식입니다: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("GraphQL 가이드 신고 수락 실패 - userId: {}, guideId: {}, error: {}",
                    userUUID, guideId, e.getMessage(), e);
            // null을 반환하지 않고 오류 메시지를 포함한 객체 반환
            return AcceptGuideReportResponse.builder()
                    .success(false)
                    .message("가이드 신고 수락 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 질문을 신고하는 GraphQL 리졸버 메서드
     *
     * @param questionId 신고 대상 질문 ID
     * @param reason     신고 사유
     * @param userId     신고하는 사용자 ID (선택적)
     * @return 질문 신고 결과
     */
    @SchemaMapping(typeName = "ReportMutation", field = "reportQuestion")
    public QuestionReportResponse reportQuestion(
            @Argument String questionId, 
            @Argument String reason,
            @Argument(name = "userId") String userId) {

        UUID userUUID = (userId != null) ? UUID.fromString(userId) : dummyUserId;
        log.info("GraphQL 질문 신고 요청 - userId: {}, questionId: {}", userUUID, questionId);

        try {
            UUID questionUUID = UUID.fromString(questionId);

            QuestionReportRequest request = QuestionReportRequest.builder()
                    .userId(userUUID)
                    .questionId(questionUUID)
                    .reason(reason)
                    .build();

            QuestionReportResponse response = reportApplication.reportQuestion(request);

            log.info("GraphQL 질문 신고 완료 - userId: {}, questionId: {}, success: {}",
                    userUUID, questionId, response.isSuccess());
            return response;
        } catch (IllegalArgumentException e) {
            log.error("GraphQL 질문 신고 실패 - 잘못된 UUID 형식: userId: {}, questionId: {}, error: {}",
                    userUUID, questionId, e.getMessage());
            // null을 반환하지 않고 오류 메시지를 포함한 객체 반환
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("잘못된 UUID 형식입니다: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("GraphQL 질문 신고 실패 - userId: {}, questionId: {}, error: {}",
                    userUUID, questionId, e.getMessage(), e);
            // null을 반환하지 않고 오류 메시지를 포함한 객체 반환
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("질문 신고 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 질문 신고를 거부하는 GraphQL 리졸버 메서드
     *
     * @param questionId 신고 대상 질문 ID
     * @param userId 처리하는 사용자 ID (선택적)
     * @return 질문 신고 거부 결과
     */
    @SchemaMapping(typeName = "ReportMutation", field = "rejectQuestionReport")
    public QuestionReportResponse rejectQuestionReport(
            @Argument String questionId,
            @Argument(name = "userId") String userId) {

        UUID userUUID = (userId != null) ? UUID.fromString(userId) : dummyUserId;
        log.info("GraphQL 질문 신고 거부 요청 - userId: {}, questionId: {}", userUUID, questionId);

        try {
            UUID questionUUID = UUID.fromString(questionId);

            RejectQuestionReportRequest request = RejectQuestionReportRequest.builder()
                    .userId(userUUID)
                    .questionId(questionUUID)
                    .build();

            QuestionReportResponse response = reportApplication.rejectQuestionReport(request);

            log.info("GraphQL 질문 신고 거부 완료 - userId: {}, questionId: {}, success: {}",
                    userUUID, questionId, response.isSuccess());
            return response;
        } catch (IllegalArgumentException e) {
            log.error("GraphQL 질문 신고 거부 실패 - 잘못된 UUID 형식: userId: {}, questionId: {}, error: {}",
                    userUUID, questionId, e.getMessage());
            // null을 반환하지 않고 오류 메시지를 포함한 객체 반환
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("잘못된 UUID 형식입니다: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("GraphQL 질문 신고 거부 실패 - userId: {}, questionId: {}, error: {}",
                    userUUID, questionId, e.getMessage(), e);
            // null을 반환하지 않고 오류 메시지를 포함한 객체 반환
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("질문 신고 거부 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 질문 신고를 수락하는 GraphQL 리졸버 메서드
     *
     * @param questionId 신고 대상 질문 ID
     * @param userId 처리하는 사용자 ID (선택적)
     * @return 질문 신고 수락 결과
     */
    @SchemaMapping(typeName = "ReportMutation", field = "acceptQuestionReport")
    public QuestionReportResponse acceptQuestionReport(
            @Argument String questionId,
            @Argument(name = "userId") String userId) {

        UUID userUUID = (userId != null) ? UUID.fromString(userId) : dummyUserId;
        log.info("GraphQL 질문 신고 수락 요청 - userId: {}, questionId: {}", userUUID, questionId);

        try {
            UUID questionUUID = UUID.fromString(questionId);

            AcceptQuestionReportRequest request = AcceptQuestionReportRequest.builder()
                    .userId(userUUID)
                    .questionId(questionUUID)
                    .build();

            QuestionReportResponse response = reportApplication.acceptQuestionReport(request);

            log.info("GraphQL 질문 신고 수락 완료 - userId: {}, questionId: {}, success: {}",
                    userUUID, questionId, response.isSuccess());
            return response;
        } catch (IllegalArgumentException e) {
            log.error("GraphQL 질문 신고 수락 실패 - 잘못된 UUID 형식: userId: {}, questionId: {}, error: {}",
                    userUUID, questionId, e.getMessage());
            // null을 반환하지 않고 오류 메시지를 포함한 객체 반환
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("잘못된 UUID 형식입니다: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("GraphQL 질문 신고 수락 실패 - userId: {}, questionId: {}, error: {}",
                    userUUID, questionId, e.getMessage(), e);
            // null을 반환하지 않고 오류 메시지를 포함한 객체 반환
            return QuestionReportResponse.builder()
                    .success(false)
                    .message("질문 신고 수락 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }
}