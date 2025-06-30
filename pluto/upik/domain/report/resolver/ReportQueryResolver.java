package pluto.upik.domain.report.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.report.application.ReportApplicationInterface;
import pluto.upik.domain.report.data.DTO.ReportQuery;
import pluto.upik.domain.report.data.DTO.ReportResponse;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 신고 관련 GraphQL 쿼리 리졸버
 * 신고 목록 조회 등의 쿼리 요청을 처리합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ReportQueryResolver {

    private final ReportApplicationInterface reportApplication;
    
    // 임시 사용자 ID (나중에 JWT를 통해 받을 예정)
    private final UUID dummyUserId = UUID.fromString("e49207e8-471a-11f0-937c-42010a800003");

    /**
     * 현재 사용자가 신고한 목록을 조회합니다.
     *
     * @param parent GraphQL 부모 객체
     * @return 신고 응답 목록
     */
    @SchemaMapping(typeName = "ReportQuery", field = "getMyReports")
    public List<ReportResponse> getMyReports(ReportQuery parent) {
        log.info("GraphQL 쿼리 - 내 신고 조회 요청: userId={}", dummyUserId);
        
        try {
            List<ReportResponse> reports = reportApplication.getReportsByUser(dummyUserId);
            log.info("GraphQL 쿼리 - 내 신고 조회 완료: userId={}, 결과 개수={}", dummyUserId, reports.size());
            return reports;
        } catch (ResourceNotFoundException e) {
            log.info("GraphQL 쿼리 - 내 신고 조회 결과 없음: userId={}, 사유={}", dummyUserId, e.getMessage());
            return new ArrayList<>();
        } catch (BusinessException e) {
            log.warn("GraphQL 쿼리 - 내 신고 조회 중 비즈니스 오류: userId={}, 사유={}", dummyUserId, e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - 내 신고 조회 중 예상치 못한 오류: userId={}, 오류={}", dummyUserId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 특정 사용자가 신고한 목록을 조회합니다.
     *
     * @param parent GraphQL 부모 객체
     * @param userId 조회할 사용자 ID
     * @return 신고 응답 목록
     */
    @SchemaMapping(typeName = "ReportQuery", field = "getReportsByUser")
    public List<ReportResponse> getReportsByUser(ReportQuery parent, @Argument UUID userId) {
        log.info("GraphQL 쿼리 - 사용자 신고 조회 요청: userId={}", userId);
        
        try {
            List<ReportResponse> reports = reportApplication.getReportsByUser(userId);
            log.info("GraphQL 쿼리 - 사용자 신고 조회 완료: userId={}, 결과 개수={}", userId, reports.size());
            return reports;
        } catch (ResourceNotFoundException e) {
            log.info("GraphQL 쿼리 - 사용자 신고 조회 결과 없음: userId={}, 사유={}", userId, e.getMessage());
            return new ArrayList<>();
        } catch (BusinessException e) {
            log.warn("GraphQL 쿼리 - 사용자 신고 조회 중 비즈니스 오류: userId={}, 사유={}", userId, e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - 사용자 신고 조회 중 예상치 못한 오류: userId={}, 오류={}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 특정 대상에 대한 신고 목록을 조회합니다.
     *
     * @param parent GraphQL 부모 객체
     * @param targetId 조회할 대상 ID
     * @return 신고 응답 목록
     */
    @SchemaMapping(typeName = "ReportQuery", field = "getReportsByTarget")
    public List<ReportResponse> getReportsByTarget(ReportQuery parent, @Argument UUID targetId) {
        log.info("GraphQL 쿼리 - 신고 대상 조회 요청: targetId={}", targetId);
        
        try {
            List<ReportResponse> reports = reportApplication.getReportsByTarget(targetId);
            log.info("GraphQL 쿼리 - 신고 대상 조회 완료: targetId={}, 결과 개수={}", targetId, reports.size());
            return reports;
        } catch (ResourceNotFoundException e) {
            log.info("GraphQL 쿼리 - 신고 대상 조회 결과 없음: targetId={}, 사유={}", targetId, e.getMessage());
            return new ArrayList<>();
        } catch (BusinessException e) {
            log.warn("GraphQL 쿼리 - 신고 대상 조회 중 비즈니스 오류: targetId={}, 사유={}", targetId, e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - 신고 대상 조회 중 예상치 못한 오류: targetId={}, 오류={}", targetId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 모든 신고 목록을 조회합니다.
     *
     * @param parent GraphQL 부모 객체
     * @return 신고 응답 목록
     */
    @SchemaMapping(typeName = "ReportQuery", field = "getAllReports")
    public List<ReportResponse> getAllReports(ReportQuery parent) {
        log.info("GraphQL 쿼리 - 모든 신고 조회 요청");
        
        try {
            List<ReportResponse> reports = reportApplication.getAllReports();
            log.info("GraphQL 쿼리 - 모든 신고 조회 완료: 결과 개수={}", reports.size());
            return reports;
        } catch (ResourceNotFoundException e) {
            log.info("GraphQL 쿼리 - 모든 신고 조회 결과 없음: 사유={}", e.getMessage());
            return new ArrayList<>();
        } catch (BusinessException e) {
            log.warn("GraphQL 쿼리 - 모든 신고 조회 중 비즈니스 오류: 사유={}", e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - 모든 신고 조회 중 예상치 못한 오류: 오류={}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}