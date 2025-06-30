package pluto.upik.domain.guide.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.guide.data.DTO.KeywordGuideResponse;
import pluto.upik.domain.guide.service.KeywordGuideServiceInterface;

import java.util.List;

/**
 * 키워드 가이드 관련 GraphQL 쿼리 리졸버
 * 키워드 기반 가이드 검색 등의 쿼리 요청을 처리합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class KeywordGuideResolver {

    private final KeywordGuideServiceInterface keywordGuideService;

    /**
     * 특정 키워드가 포함된 가이드 목록을 검색합니다.
     *
     * @param parent GraphQL 부모 객체
     * @param keyword 검색할 키워드
     * @return 키워드 가이드 응답 목록
     */
    @SchemaMapping(typeName = "KeywordGuideQuery", field = "searchByKeyword")
    public List<KeywordGuideResponse> searchByKeyword(Object parent, @Argument String keyword) {
        log.info("GraphQL 쿼리 - 키워드 기반 가이드 검색 요청: keyword={}", keyword);
        
        try {
            List<KeywordGuideResponse> guides = keywordGuideService.searchGuidesByKeyword(keyword);
            log.info("GraphQL 쿼리 - 키워드 기반 가이드 검색 완료: keyword={}, 결과 개수={}", keyword, guides.size());
            return guides;
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - 키워드 기반 가이드 검색 실패: keyword={}", keyword, e);
            throw e;
        }
    }
}