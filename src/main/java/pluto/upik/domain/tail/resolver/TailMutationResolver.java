package pluto.upik.domain.tail.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.tail.data.DTO.TailMutation;
import pluto.upik.domain.tail.data.DTO.TailPayload;
import pluto.upik.domain.tail.data.DTO.TailResponsePayload;
import pluto.upik.domain.tail.service.TailService;

import java.util.UUID;
/**
 * 테일 뮤테이션 리졸버
 * GraphQL 테일 뮤테이션 필드에 대한 리졸버를 제공합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class TailMutationResolver {

    private final TailService tailService;

    /**
     * 테일 생성
     * 
     * @param tailMutation 테일 뮤테이션 객체
     * @param voteId 투표 ID
     * @param question 테일 질문
     * @return 생성된 테일 정보
     */
    @SchemaMapping(typeName = "TailMutation", field = "createTail")
    public TailPayload createTail(
            TailMutation tailMutation,
            @Argument String voteId,
            @Argument String question) {
        log.debug("GraphQL 테일 생성 시작: voteId={}, question={}", voteId, question);

        if (voteId == null) {
            log.error("voteId가 null입니다.");
            throw new IllegalArgumentException("voteId는 null일 수 없습니다.");
        }

        TailPayload result = tailService.createTail(voteId, question);
        log.debug("GraphQL 테일 생성 완료: result={}", result);
        return result;
    }

    /**
     * 테일 응답 생성
     * 
     * @param tailMutation 테일 뮤테이션 객체
     * @param tailId 테일 ID
     * @param userId 사용자 ID
     * @param answer 응답 내용
     * @return 생성된 테일 응답 정보
     */
    @SchemaMapping(typeName = "TailMutation", field = "createTailResponse")
    public TailResponsePayload createTailResponse(
            TailMutation tailMutation,
            @Argument String tailId,
            @Argument String userId,
            @Argument String answer) {

        log.debug("GraphQL 테일 응답 생성: tailId={}, userId={}, answer={}", tailId, userId, answer);
        return tailService.createTailResponse(tailId, userId, answer);
    }
}