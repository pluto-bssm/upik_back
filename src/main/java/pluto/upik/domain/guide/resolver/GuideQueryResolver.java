package pluto.upik.domain.guide.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.guide.data.DTO.GuideDetailResponse;
import pluto.upik.domain.guide.data.DTO.GuideResponse;
import pluto.upik.domain.guide.service.GuideQueryServiceInterface;

import java.util.List;
import java.util.UUID;

/**
 * 가이드 관련 GraphQL 쿼리 리졸버
 * 카테고리별 가이드 조회 등의 쿼리 요청을 처리합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class GuideQueryResolver {

    private final GuideQueryServiceInterface guideQueryService;

    /**
     * 특정 카테고리에 속한 가이드 목록을 조회합니다.
     *
     * @param category 조회할 카테고리
     * @return 가이드 응답 목록
     */
    @SchemaMapping(typeName = "Query", field = "guidesByCategory")
    public List<GuideResponse> getGuidesByCategory(@Argument String category) {
        log.info("GraphQL query guidesByCategory called with category: {}", category);
        List<GuideResponse> guides = guideQueryService.findByCategory(category);
        log.info("Number of guides found: {}", guides.size());
        return guides;
    }
    
    /**
     * 특정 ID의 가이드를 상세 조회합니다.
     *
     * @param parent GraphQL 부모 객체
     * @param id 조회할 가이드 ID 문자열
     * @return 가이드 상세 응답
     */
    @SchemaMapping(typeName = "GuideQuery", field = "guideById")
    public GuideDetailResponse guideById(Object parent, @Argument String id) {
        log.info("GraphQL 쿼리 - 가이드 상세 조회 요청: id={}", id);
        
        try {
            UUID guideId = UUID.fromString(id);
            GuideDetailResponse guide = guideQueryService.findGuideById(guideId);
            log.info("GraphQL 쿼리 - 가이드 상세 조회 완료: id={}, title={}", id, guide.getTitle());
            return guide;
        } catch (IllegalArgumentException e) {
            log.error("GraphQL 쿼리 - 가이드 상세 조회 실패: 잘못된 UUID 형식 - id={}", id, e);
            throw new IllegalArgumentException("잘못된 UUID 형식입니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("GraphQL 쿼리 - 가이드 상세 조회 실패: id={}", id, e);
            throw e;
        }
    }
}