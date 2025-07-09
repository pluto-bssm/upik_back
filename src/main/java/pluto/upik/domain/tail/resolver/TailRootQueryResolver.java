package pluto.upik.domain.tail.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.tail.data.DTO.TailQuery;

/**
 * 테일 루트 쿼리 리졸버
 * GraphQL 테일 쿼리의 루트 진입점을 제공합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class TailRootQueryResolver {

    /**
     * 테일 쿼리 루트 진입점
     * 
     * @return 테일 쿼리 객체
     */
    @QueryMapping
    public TailQuery tail() {
        log.debug("GraphQL 테일 쿼리 루트 진입점 호출");
        return new TailQuery();
    }
}