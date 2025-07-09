package pluto.upik.domain.guide.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * 가이드 관련 GraphQL 루트 쿼리 리졸버
 * 가이드 쿼리에 대한 루트 진입점을 제공합니다.
 */
@Controller
@Slf4j
public class GuideRootQueryResolver {

    /**
     * 가이드 쿼리 루트 진입점
     * 
     * @return 가이드 쿼리 객체
     */
    @QueryMapping
    public Object guide() {
        log.debug("GraphQL 가이드 쿼리 루트 진입점 호출");
        return new Object(); // 또는 GuideQuery 인스턴스. POJO면 아무거나 가능
    }
}