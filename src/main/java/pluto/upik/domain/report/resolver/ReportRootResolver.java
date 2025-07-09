package pluto.upik.domain.report.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.report.data.DTO.ReportMutation;

/**
 * 신고 관련 GraphQL 루트 리졸버
 * 신고 쿼리와 뮤테이션에 대한 루트 진입점을 제공합니다.
 * 참고: 쿼리 진입점은 ReportRootQueryResolver에서 제공하므로 이 클래스에서는 제거했습니다.
 */
@Controller
@Slf4j
public class ReportRootResolver {

    // report() 메서드 제거 - ReportRootQueryResolver에서 이미 제공하고 있음

    /**
     * 신고 뮤테이션 루트 진입점
     * 
     * @return 신고 뮤테이션 객체
     */
    @MutationMapping
    public ReportMutation reportMutation() {
        log.debug("GraphQL 신고 뮤테이션 루트 진입점 호출");
        return new ReportMutation(); // 명확한 타입 반환
    }
}