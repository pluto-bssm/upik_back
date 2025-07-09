package pluto.upik.domain.tail.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.tail.data.DTO.TailPayload;
import pluto.upik.domain.tail.data.DTO.TailQuery;
import pluto.upik.domain.tail.data.DTO.TailResponsePayload;
import pluto.upik.domain.tail.service.TailService;

import java.util.List;
import java.util.UUID;

/**
 * 테일 쿼리 리졸버
 * GraphQL 테일 쿼리 필드에 대한 리졸버를 제공합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class TailQueryResolver {

    private final TailService tailService;

    /**
     * 특정 투표에 속한 테일 목록 조회
     *
     * @param tailQuery 테일 쿼리 객체
     * @param voteId 투표 ID
     * @return 테일 목록
     */
    @SchemaMapping(typeName = "TailQuery", field = "getTailsByVoteId")
    public List<TailPayload> getTailsByVoteId(TailQuery tailQuery, @Argument String voteId) {
        log.debug("GraphQL 투표에 속한 테일 목록 조회: voteId={}", voteId);
        return tailService.getTailsByVoteId(UUID.fromString(voteId));
    }

    /**
     * 특정 테일에 대한 응답 목록 조회
     *
     * @param tailQuery 테일 쿼리 객체
     * @param tailId 테일 ID
     * @return 테일 응답 목록
     */
    @SchemaMapping(typeName = "TailQuery", field = "getTailResponsesByTailId")
    public List<TailResponsePayload> getTailResponsesByTailId(TailQuery tailQuery, @Argument String tailId) {
        log.debug("GraphQL 테일에 대한 응답 목록 조회: tailId={}", tailId);
        return tailService.getTailResponsesByTailId(UUID.fromString(tailId));
    }
}