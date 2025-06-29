package pluto.upik.domain.report.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.report.application.ReportApplication;
import pluto.upik.domain.report.data.DTO.RejectReportPayload;
import pluto.upik.domain.report.data.DTO.ReportResponse;

import java.util.Collections; // 빈 배열 반환을 위한 유틸리티
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReportMutationResolver {

    private final ReportApplication reportApplication;

    @SchemaMapping(typeName = "Mutation", field = "rejectReport")
    public RejectReportPayload rejectReport(
            @Argument String targetId) {
        UUID dummyUserId = UUID.fromString("e49207e8-471a-11f0-937c-42010a800003");
        log.info("GraphQL Mutation - 신고 거부 요청: userId={}, targetId={}", dummyUserId, targetId);

        try {
            // Application 계층 호출
            String message = reportApplication.rejectReport(dummyUserId, UUID.fromString(targetId));

            log.info("GraphQL Mutation - 신고 거부 성공: userId={}, targetId={}", dummyUserId, targetId);
            return new RejectReportPayload(message);
        } catch (IllegalArgumentException e) {
            log.warn("GraphQL Mutation - 신고 거부 실패: {}", e.getMessage());
            throw new RuntimeException(e.getMessage()); // GraphQL에서 클라이언트로 에러 전달
        } catch (Exception e) {
            log.error("GraphQL Mutation - 처리 중 알 수 없는 오류 발생", e);
            throw new RuntimeException("서버 오류가 발생했습니다.");
        }
    }

    @SchemaMapping(typeName = "Query", field = "getReportsByUser")
    public List<ReportResponse> getReportsByUser(@Argument UUID userId) {
        log.info("GraphQL Query - 사용자 신고 조회 요청: userId={}", userId);

        // Application 계층에서 데이터 가져오기
        List<ReportResponse> reports = reportApplication.getReportsByUser(userId);

        // null 체크 후 빈 배열 반환
        return reports != null ? reports : Collections.emptyList();
    }

    @SchemaMapping(typeName = "Query", field = "getReportsByTarget")
    public List<ReportResponse> getReportsByTarget(@Argument UUID targetId) {
        log.info("GraphQL Query - 신고 대상 조회 요청: targetId={}", targetId);

        // Application 계층에서 데이터 가져오기
        List<ReportResponse> reports = reportApplication.getReportsByTarget(targetId);

        // null 체크 후 빈 배열 반환
        return reports != null ? reports : Collections.emptyList();
    }

    @SchemaMapping(typeName = "Query", field = "getAllReports")
    public List<ReportResponse> getAllReports() {
        log.info("GraphQL Query - 모든 신고 조회 요청");

        // Application 계층에서 데이터 가져오기
        List<ReportResponse> reports = reportApplication.getAllReports();

        // null 체크 후 빈 배열 반환
        return reports != null ? reports : Collections.emptyList();
    }

}
