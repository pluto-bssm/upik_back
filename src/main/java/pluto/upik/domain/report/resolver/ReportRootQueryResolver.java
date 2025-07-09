package pluto.upik.domain.report.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.report.data.DTO.ReportQuery;

/**
 * 신고 관련 GraphQL 루트 쿼리 리졸버
 * 신고 쿼리에 대한 루트 진입점을 제공합니다.
 */
@Controller
@Slf4j
public class ReportRootQueryResolver {

    /**
     * 신고 쿼리 루트 진입점
     * 
     * @return 신고 쿼리 객체
     */
    @QueryMapping
    public ReportQuery report() {
        log.debug("GraphQL 신고 쿼리 루트 진입점 호출");
        return new ReportQuery(); // 빈 객체로 리턴, 내부 필드에서 처리
    }
}