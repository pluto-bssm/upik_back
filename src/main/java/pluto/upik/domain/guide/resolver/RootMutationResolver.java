package pluto.upik.domain.guide.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.guide.data.DTO.GuideMutation;
import pluto.upik.domain.report.data.DTO.ReportMutation;

/**
 * GraphQL 루트 뮤테이션 리졸버
 * 가이드 및 신고 뮤테이션에 대한 루트 진입점을 제공합니다.
 */
@Controller
@Slf4j
public class RootMutationResolver {

    /**
     * 가이드 뮤테이션 루트 진입점
     * 
     * @return 가이드 뮤테이션 객체
     */
    @SchemaMapping(typeName = "Mutation", field = "guide")
    public GuideMutation guideMutation() {
        log.debug("GraphQL 가이드 뮤테이션 루트 진입점 호출");
        return new GuideMutation(); // 빈 객체로 리턴, 내부 필드에서 처리
    }

    /**
     * 신고 뮤테이션 루트 진입점
     * 
     * @return 신고 뮤테이션 객체
     */
    @SchemaMapping(typeName = "Mutation", field = "report")
    public ReportMutation report() {
        log.debug("GraphQL 신고 뮤테이션 루트 진입점 호출");
        return new ReportMutation();  // ReportMutation은 DTO 혹은 빈 POJO
    }
}