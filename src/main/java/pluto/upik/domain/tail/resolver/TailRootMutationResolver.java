package pluto.upik.domain.tail.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.tail.data.DTO.TailMutation;

/**
 * 테일 루트 뮤테이션 리졸버
 * GraphQL 테일 뮤테이션의 루트 진입점을 제공합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class TailRootMutationResolver {

    /**
     * 테일 뮤테이션 루트 진입점
     * 
     * @return 테일 뮤테이션 객체
     */
    @MutationMapping
    public TailMutation tail() {
        log.debug("GraphQL 테일 뮤테이션 루트 진입점 호출");
        return new TailMutation();
    }
}