package pluto.upik.domain.guide.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.guide.data.DTO.GuideResponse;
import pluto.upik.domain.guide.service.GuideQueryService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GuideQueryResolver {

    private final GuideQueryService guideQueryService;

    @SchemaMapping(typeName = "GuideQuery", field = "guidesByCategory")
    public List<GuideResponse> guidesByCategory(Object parent, @Argument String category) {
        log.info("GraphQL query guidesByCategory called with category: {}", category);
        return guideQueryService.findByCategory(category);
    }
}
