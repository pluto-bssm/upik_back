package pluto.upik.domain.report.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.report.application.ReportApplication;
import pluto.upik.domain.report.data.DTO.ReportResponse;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReportQueryResolver {

    private final ReportApplication reportApplication;

    @SchemaMapping(typeName = "ReportQuery", field = "getReportsByUser")
    public List<ReportResponse> getReportsByUser(Object parent, @Argument UUID userId) {
        log.info("GraphQL Query - 사용자 신고 조회 요청: userId={}", userId);
        return reportApplication.getReportsByUser(userId);
    }

    @SchemaMapping(typeName = "ReportQuery", field = "getReportsByTarget")
    public List<ReportResponse> getReportsByTarget(Object parent, @Argument UUID targetId) {
        log.info("GraphQL Query - 신고 대상 조회 요청: targetId={}", targetId);
        return reportApplication.getReportsByTarget(targetId);
    }

    @SchemaMapping(typeName = "ReportQuery", field = "getAllReports")
    public List<ReportResponse> getAllReports(Object parent) {
        log.info("GraphQL Query - 모든 신고 조회 요청");
        return reportApplication.getAllReports();
    }
}

