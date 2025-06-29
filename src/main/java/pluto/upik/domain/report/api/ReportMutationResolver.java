package pluto.upik.domain.report.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.report.application.ReportApplication;
import pluto.upik.domain.report.data.DTO.RejectReportPayload;
import pluto.upik.domain.report.data.DTO.ReportResponse;
import pluto.upik.domain.report.data.DTO.SubmitReportInput;

import java.util.Collections; // 빈 배열 반환을 위한 유틸리티
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReportMutationResolver {

    private final ReportApplication reportApplication;

    @SchemaMapping(typeName = "ReportMutation", field = "rejectReport")
    public RejectReportPayload rejectReport(Object parent, @Argument String userId, @Argument String targetId) {
        log.info("GraphQL Mutation - 신고 거부 요청: userId={}, targetId={}", userId, targetId);
        String message = reportApplication.rejectReport(
                UUID.fromString(userId), UUID.fromString(targetId));
        return new RejectReportPayload(message);
    }

//    @SchemaMapping(typeName = "ReportMutation", field = "submitReport")
//    public SubmitReportPayload submitReport(Object parent, @Argument SubmitReportInput input) {
//        log.info("GraphQL Mutation - 신고 접수 요청: {}", input);
//        return reportApplication.submitReport(input);
//    }
}