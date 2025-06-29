package pluto.upik.domain.guide.api;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class KeywordGuideRootQueryResolver {

    @QueryMapping
    public Object keywordGuide() {
        return new Object(); // 또는 KeywordGuideQuery 인스턴스
    }
}

