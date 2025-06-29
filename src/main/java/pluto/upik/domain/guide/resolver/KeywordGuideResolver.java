package pluto.upik.domain.guide.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.guide.data.DTO.KeywordGuideResponse;
import pluto.upik.domain.guide.service.KeywordGuideService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class KeywordGuideResolver {

    private final KeywordGuideService keywordGuideService;

    @SchemaMapping(typeName = "KeywordGuideQuery", field = "searchByKeyword")
    public List<KeywordGuideResponse> searchByKeyword(Object parent, @Argument String keyword) {
        log.info("GraphQL query searchByKeyword called with keyword: {}", keyword);
        return keywordGuideService.searchGuidesByKeyword(keyword);
    }
}
